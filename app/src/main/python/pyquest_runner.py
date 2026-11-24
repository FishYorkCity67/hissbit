"""Führt Code von Lernenden aus: Ausgabe abfangen, input() bedienen,
Zeitlimit/Abbruch erzwingen und Fehler verständlich erklären."""

import builtins
import importlib
import io
import json
import linecache
import os
import sys
import threading
import time
import traceback

FILENAME = "<main.py>"
MAX_OUTPUT = 300_000

_cancel = threading.Event()
_real_sleep = time.sleep
_workdir = None
_current_token = 0


class _Abort(BaseException):
    pass


class _Timeout(_Abort):
    pass


class _Cancelled(_Abort):
    pass


class _TooMuchOutput(_Abort):
    pass


def init(workdir):
    global _workdir
    try:
        os.makedirs(workdir, exist_ok=True)
        os.chdir(workdir)
    except Exception:
        pass
    if _workdir in sys.path:
        sys.path.remove(_workdir)
    _workdir = os.path.abspath(workdir)
    # Hinten anhängen: eigene Module (z.B. helfer.py) sind importierbar,
    # verdecken aber keine Standardmodule.
    sys.path.append(_workdir)
    sys.argv = ["main.py"]
    return sys.version.split()[0]


def _fresh_start():
    """Jeder Lauf soll sich wie ein frisch gestartetes Python-Programm anfühlen,
    obwohl der Interpreter in der App die ganze Zeit weiterläuft."""
    sys.argv = ["main.py"]
    if _workdir:
        try:
            os.chdir(_workdir)
        except Exception:
            pass
        # Eigene Module neu laden lassen, falls die Datei geändert wurde.
        for name, mod in list(sys.modules.items()):
            path = getattr(mod, "__file__", None)
            if path and os.path.dirname(os.path.abspath(path)) == _workdir:
                sys.modules.pop(name, None)
    # "import this" gibt das Zen of Python nur beim ersten Import aus.
    sys.modules.pop("this", None)
    importlib.invalidate_caches()
    rnd = sys.modules.get("random")
    if rnd is not None:
        try:
            rnd.seed()  # sonst wirkt ein random.seed(42) aus einem früheren Lauf nach
        except Exception:
            pass


def cancel(token=None):
    """Bricht den laufenden Lauf ab – aber nur, wenn [token] noch der aktuelle ist.
    So trifft ein verspäteter Abbruch nicht versehentlich den nächsten Lauf."""
    if token is None or token == _current_token:
        _cancel.set()


class _Out(io.TextIOBase):
    """Sammelt Ausgaben und reicht sie gebündelt an die App weiter."""

    def __init__(self, bridge, kind, state):
        super().__init__()
        self._bridge = bridge
        self._kind = kind
        self._state = state
        self._pending = []
        self._pending_len = 0
        self._last = time.monotonic()

    def writable(self):
        return True

    def isatty(self):
        return False

    @property
    def encoding(self):
        return "utf-8"

    def write(self, s):
        if not isinstance(s, str):
            raise TypeError(f"write() argument must be str, not {type(s).__name__}")
        if not s:
            return 0
        state = self._state
        state["size"] += len(s)
        if state["size"] > MAX_OUTPUT:
            raise _TooMuchOutput()
        state["out" if self._kind == 0 else "err"].append(s)
        if self._bridge is not None:
            self._pending.append(s)
            self._pending_len += len(s)
            now = time.monotonic()
            if self._pending_len > 2048 or now - self._last > 0.05:
                self.flush()
        return len(s)

    def flush(self):
        if self._pending and self._bridge is not None:
            text = "".join(self._pending)
            self._pending.clear()
            self._pending_len = 0
            self._last = time.monotonic()
            self._bridge.write(text, self._kind)


def _make_input(bridge, interactive, lines, out, state):
    def _input(prompt=""):
        prompt = str(prompt)
        if interactive and bridge is not None:
            if prompt:
                out.write(prompt)
            out.flush()
            waited = time.monotonic()
            value = bridge.readLine(prompt)
            state["wait"] += time.monotonic() - waited
            if value is None:
                raise _Cancelled()
            return str(value)
        # Aufgaben-Modus: vorgegebene Eingaben. Der Prompt wird angezeigt,
        # zählt aber nicht zur geprüften Ausgabe.
        if not lines:
            raise EOFError("EOF when reading a line")
        line = lines.pop(0)
        if bridge is not None:
            out.flush()
            bridge.write(prompt + line + "\n", 2)
        return line

    return _input


def _make_sleep(deadline):
    def _sleep(secs):
        secs = float(secs)
        if secs < 0:
            raise ValueError("sleep length must be non-negative")
        if deadline is not None:
            remaining = deadline - time.monotonic()
            if secs > remaining:
                if _cancel.wait(max(0.0, remaining)):
                    raise _Cancelled()
                raise _Timeout()
        if _cancel.wait(secs):
            raise _Cancelled()

    return _sleep


def _make_tracer(deadline):
    is_set = _cancel.is_set
    mono = time.monotonic
    counter = [0]

    def local(frame, event, arg):
        counter[0] += 1
        if counter[0] & 31 == 0:
            if is_set():
                raise _Cancelled()
            if deadline is not None and mono() > deadline:
                raise _Timeout()
        return local

    def tracer(frame, event, arg):
        if frame.f_code.co_filename != FILENAME:
            return None
        return local(frame, event, arg)

    return tracer


def _format(e):
    try:
        if isinstance(e, SyntaxError):
            return "".join(traceback.format_exception_only(type(e), e)).rstrip()
        tb = e.__traceback__
        while tb is not None and tb.tb_frame.f_code.co_filename != FILENAME:
            tb = tb.tb_next
        return "".join(traceback.format_exception(type(e), e, tb)).rstrip()
    except Exception:
        return f"{type(e).__name__}: {e}"


def _lineno(e):
    if isinstance(e, SyntaxError):
        return e.lineno
    tb = e.__traceback__
    line = None
    while tb is not None:
        if tb.tb_frame.f_code.co_filename == FILENAME:
            line = tb.tb_lineno
        tb = tb.tb_next
    return line


_HINTS = [
    (IndentationError,
     "Die Einrückung stimmt nicht. Nach einem Doppelpunkt (:) muss der Block eingerückt sein – "
     "nutze überall gleich viele Leerzeichen (empfohlen: 4).",
     "The indentation is off. After a colon (:) the block must be indented – "
     "use the same number of spaces everywhere (4 is recommended)."),
    (SyntaxError,
     "Python versteht die Schreibweise nicht. Prüfe Klammern, Anführungszeichen, Doppelpunkte "
     "nach if/for/def und ob du = (Zuweisung) und == (Vergleich) verwechselt hast.",
     "Python can't understand how this is written. Check brackets, quotes, colons after "
     "if/for/def and whether you mixed up = (assignment) and == (comparison)."),
    (UnboundLocalError,
     "Du benutzt eine Variable in einer Funktion, bevor sie dort einen Wert bekommen hat. "
     "Willst du eine globale Variable ändern, brauchst du das Schlüsselwort global.",
     "You're using a variable inside a function before it got a value there. "
     "To change a global variable you need the keyword global."),
    (NameError,
     "Python kennt diesen Namen nicht. Hast du dich vertippt (auch Groß-/Kleinschreibung!), "
     "die Variable noch nicht erstellt oder Anführungszeichen bei einem Text vergessen?",
     "Python doesn't know this name. Is there a typo (case matters!), did you forget to create "
     "the variable, or are quotes missing around a text?"),
    (ZeroDivisionError,
     "Durch 0 teilen geht nicht – auch nicht in Python. Prüfe vorher mit if, ob der Teiler 0 ist.",
     "You can't divide by 0 – not even in Python. Check with if whether the divisor is 0."),
    (ModuleNotFoundError,
     "Dieses Modul gibt es hier nicht. Die komplette Standardbibliothek ist dabei, "
     "aber keine zusätzlich installierten Pakete (z.B. numpy) und keine Grafik (tkinter/turtle).",
     "This module isn't available here. The whole standard library is included, "
     "but no extra packages (e.g. numpy) and no graphics (tkinter/turtle)."),
    (ImportError,
     "Der Import hat nicht geklappt. Stimmt der Name des Moduls bzw. der Funktion?",
     "The import didn't work. Is the name of the module or function correct?"),
    (IndexError,
     "Diesen Index gibt es nicht. Denk daran: Die Zählung beginnt bei 0, der letzte Index ist len(x) - 1.",
     "This index doesn't exist. Remember: counting starts at 0, the last index is len(x) - 1."),
    (KeyError,
     "Diesen Schlüssel gibt es im Dictionary nicht. Tipp: d.get(schluessel) liefert None statt eines Fehlers, "
     "und mit 'in' kannst du vorher prüfen.",
     "This key isn't in the dictionary. Tip: d.get(key) returns None instead of an error, "
     "and you can check first with 'in'."),
    (AttributeError,
     "Dieses Objekt hat keine solche Methode oder Eigenschaft. Tippfehler? Mit dir(objekt) "
     "siehst du, was es gibt.",
     "This object has no such method or attribute. A typo? dir(obj) shows what's available."),
    (TypeError,
     "Ein Wert hat den falschen Typ für diese Operation – z.B. Text + Zahl. Wandle mit str(), int() "
     "oder float() um. Passt auch die Anzahl der Argumente beim Funktionsaufruf?",
     "A value has the wrong type for this operation – e.g. text + number. Convert with str(), int() "
     "or float(). Does the number of arguments in the function call match?"),
    (ValueError,
     "Der Typ stimmt, aber der Wert passt nicht – z.B. int(\"abc\"). Prüfe die Eingabe oder fange "
     "den Fehler mit try/except ab.",
     "The type is right but the value isn't – e.g. int(\"abc\"). Check the input or catch "
     "the error with try/except."),
    (RecursionError,
     "Die Funktion ruft sich zu oft selbst auf. Fehlt eine Abbruchbedingung (Basisfall)?",
     "The function calls itself too often. Is a stopping condition (base case) missing?"),
    (FileNotFoundError,
     "Diese Datei gibt es nicht. Stimmt der Name? Zum Erstellen öffnest du sie mit dem Modus \"w\".",
     "This file doesn't exist. Is the name right? To create it, open it with mode \"w\"."),
    (PermissionError,
     "Auf diese Datei oder diesen Ordner darfst du nicht zugreifen.",
     "You're not allowed to access this file or folder."),
    (OverflowError,
     "Die Zahl ist zu groß für diese Berechnung.",
     "The number is too large for this calculation."),
    (MemoryError,
     "Dein Programm braucht zu viel Speicher.",
     "Your program needs too much memory."),
    (EOFError,
     "Das Programm wollte mit input() etwas einlesen, aber es gab keine Eingabe (mehr).",
     "The program wanted to read with input(), but there was no (more) input."),
    (AssertionError,
     "Eine assert-Prüfung ist fehlgeschlagen – ein Ergebnis entspricht nicht der Erwartung.",
     "An assert check failed – a result doesn't match what was expected."),
    (StopIteration,
     "Der Iterator hat keine Werte mehr. next() wurde öfter aufgerufen, als es Elemente gibt.",
     "The iterator has no more values. next() was called more often than there are elements."),
    (KeyboardInterrupt,
     "Das Programm wurde unterbrochen.",
     "The program was interrupted."),
    (RuntimeError,
     "Zur Laufzeit ist ein Fehler aufgetreten.",
     "An error occurred while running."),
]


def _hint(e, lang="de"):
    for exc_type, de, en in _HINTS:
        if isinstance(e, exc_type):
            return en if lang == "en" else de
    return None

def run(code, stdin_json, interactive, bridge, timeout, token=0, lang="de"):
    global _current_token
    _current_token = token
    _cancel.clear()
    _fresh_start()
    state = {"size": 0, "out": [], "err": [], "wait": 0.0}
    out = _Out(bridge, 0, state)
    err = _Out(bridge, 1, state)
    try:
        lines = [str(x) for x in json.loads(stdin_json)] if stdin_json else []
    except Exception:
        lines = []

    started = time.monotonic()
    deadline = started + float(timeout) if timeout and float(timeout) > 0 else None

    saved = (sys.stdout, sys.stderr, builtins.input, time.sleep)
    sys.stdout = out
    sys.stderr = err
    builtins.input = _make_input(bridge, bool(interactive), lines, out, state)
    time.sleep = _make_sleep(deadline)
    linecache.cache[FILENAME] = (len(code), None, code.splitlines(True), FILENAME)

    result = {"error": None, "errorType": None, "hint": None, "line": None,
              "timedOut": False, "cancelled": False}
    namespace = {"__name__": "__main__", "__builtins__": builtins, "__file__": "main.py"}
    try:
        compiled = compile(code, FILENAME, "exec")
        sys.settrace(_make_tracer(deadline))
        try:
            exec(compiled, namespace)
        finally:
            sys.settrace(None)
    except _Timeout:
        result["timedOut"] = True
    except _Cancelled:
        result["cancelled"] = True
    except _TooMuchOutput:
        result["errorType"] = "OutputLimit"
        result["error"] = ("Stopped: your program printed a huge amount of text (infinite loop?)." if lang == "en"
                           else "Abgebrochen: Dein Programm hat sehr viel Text ausgegeben (Endlosschleife?).")
    except SystemExit as e:
        if e.code not in (None, 0):
            result["errorType"] = "SystemExit"
            result["error"] = f"SystemExit: {e.code}"
    except BaseException as e:  # noqa: BLE001 – wir wollen wirklich alles abfangen
        result["errorType"] = type(e).__name__
        result["error"] = _format(e)
        result["hint"] = _hint(e, lang)
        result["line"] = _lineno(e)
    finally:
        sys.settrace(None)
        try:
            out.flush()
            err.flush()
        except BaseException:
            pass
        sys.stdout, sys.stderr, builtins.input, time.sleep = saved

    result["stdout"] = "".join(state["out"])
    result["stderr"] = "".join(state["err"])
    result["ms"] = int(max(0.0, time.monotonic() - started - state["wait"]) * 1000)
    return json.dumps(result)
