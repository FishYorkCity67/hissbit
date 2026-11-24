"""Prüft alle Kursinhalte mit echtem Python – genau so, wie die App sie ausführt.

Aufruf (nach ./gradlew :app:testDebugUnitTest --tests *ContentExportTest):
    py -3.14 -I -X utf8 tools/verify_content.py <build>/content-export/content.json

Das Skript startet sich selbst mehrmals mit verschiedenen PYTHONHASHSEED-Werten,
um Ausgaben zu finden, die von der (zufälligen) Reihenfolge in Sets abhängen.
"""

import itertools
import json
import os
import shutil
import subprocess
import sys
import tempfile
import time

HERE = os.path.dirname(os.path.abspath(__file__))
RUNNER_DIR = os.path.join(HERE, "..", "app", "src", "main", "python")
SEEDS = ["1", "2", "3"]
TIMEOUT = 6.0
SLOW_MS = 1500
MAX_COMBOS = 400


def normalize(s):
    return "\n".join(line.rstrip() for line in s.replace("\r\n", "\n").split("\n")).strip("\n")


class Bridge:
    def __init__(self):
        self.chunks = []

    def write(self, text, kind):
        self.chunks.append((int(kind), text))

    def readLine(self, prompt):  # noqa: N802 – Name wie in Kotlin
        return None

    def shown(self):
        """Was die Theorie-Ansicht anzeigt: stdout + Echo der Eingaben."""
        return "".join(t for k, t in self.chunks if k in (0, 2))


def worker(content_path, out_path):
    import warnings
    warnings.simplefilter("ignore")
    sys.path.insert(0, os.path.abspath(RUNNER_DIR))
    import pyquest_runner as runner

    runner.init(tempfile.gettempdir())
    data = json.load(open(content_path, encoding="utf-8"))
    results = []
    base = os.getcwd()

    shared = tempfile.mkdtemp(prefix="pyq_shared_")
    main_programs = []  # (index, code, stdin) für den Durchlauf im gemeinsamen Ordner

    def run(code, stdin=(), workdir=None):
        work = workdir or tempfile.mkdtemp(prefix="pyq_")
        runner.init(work)  # wie in der App: Arbeitsordner setzen
        bridge = Bridge()
        try:
            started = time.monotonic()
            raw = runner.run(code, json.dumps(list(stdin)), False, bridge, TIMEOUT)
            r = json.loads(raw)
            r["wall"] = int((time.monotonic() - started) * 1000)
        finally:
            os.chdir(base)
            if workdir is None:
                shutil.rmtree(work, ignore_errors=True)
        r["shown"] = bridge.shown()
        r["ok"] = r["error"] is None and not r["timedOut"] and not r["cancelled"]
        return r

    def main_run(code, stdin, key):
        """Hauptprogramm eines Prüfpunkts – wird später auch im gemeinsamen Ordner wiederholt."""
        main_programs.append((len(results), code, list(stdin), key))
        return run(code, stdin)

    def fp(r, key):
        """Fingerabdruck eines Laufs: Ausgabe plus ggf. Fehlertyp."""
        return normalize(r[key]) + ("" if r["ok"] else f"\n<{r['errorType'] or 'Zeitlimit'}>")

    def compiles(code):
        try:
            compile(code, "<main.py>", "exec")
            return None
        except SyntaxError as e:
            return f"{type(e).__name__}: {e.msg} (Zeile {e.lineno})"

    def describe(r):
        if r["timedOut"]:
            return "Zeitlimit überschritten"
        return (r["error"] or "").strip().splitlines()[-1] if r["error"] else "?"

    for item in data["items"]:
        kind = item["kind"]
        where = item["where"]
        findings = []
        fingerprint = ""

        def add(level, msg):
            findings.append([level, msg])

        if kind == "theory":
            if not item["runnable"]:
                err = compiles(item["code"])
                if err:
                    add("INFO", f"nicht ausführbarer Theorie-Code kompiliert nicht: {err}")
            else:
                r = main_run(item["code"], item["stdin"], "shown")
                fingerprint = fp(r, "shown")
                if item.get("fails"):
                    if r["ok"]:
                        add("THEORY", "Theorie-Code soll einen Fehler zeigen, läuft aber fehlerfrei")
                elif not r["ok"]:
                    add("THEORY", f"Theorie-Code endet mit Fehler: {describe(r)}")
                if "output" in item and normalize(r["shown"]) != normalize(item["output"]):
                    add("THEORY", "Angezeigte Ausgabe stimmt nicht:\n" + diff(item["output"], r["shown"]))
                if r["ms"] > SLOW_MS:
                    add("SLOW", f"{r['ms']} ms")
        elif kind in ("output", "typeOutput"):
            r = main_run(item["code"], (), "stdout")
            fingerprint = fp(r, "stdout")
            expected = item["options"][0] if kind == "output" else item["expected"]
            if not r["ok"]:
                add("ERROR", f"Code endet mit Fehler: {describe(r)}")
            elif normalize(r["stdout"]) != normalize(expected):
                add("ERROR", "Richtige Antwort stimmt nicht mit der echten Ausgabe überein:\n" + diff(expected, r["stdout"]))
            if kind == "output":
                for other in item["options"][1:]:
                    if r["ok"] and normalize(other) == normalize(r["stdout"]):
                        add("ERROR", f"Falsche Option entspricht der echten Ausgabe: {other!r}")
        elif kind == "fill":
            expected = item.get("expected")
            solved = item["solved"]
            if expected is None:
                err = compiles(solved)
                if err:
                    add("INFO", f"Lückentext (ohne Ausgabe) kompiliert nicht: {err}")
            else:
                r = main_run(solved, (), "stdout")
                fingerprint = fp(r, "stdout")
                if not r["ok"]:
                    add("ERROR", f"Gelöster Lückentext endet mit Fehler: {describe(r)}")
                elif normalize(r["stdout"]) != normalize(expected):
                    add("ERROR", "Erwartete Ausgabe stimmt nicht:\n" + diff(expected, r["stdout"]))
                else:
                    for alt in alternatives(item):
                        filled = fill_template(item["template"], alt)
                        if compiles(filled):
                            continue
                        r2 = run(filled)
                        if r2["ok"] and normalize(r2["stdout"]) == normalize(expected):
                            add("AMBIG", f"Auch diese Lösung ergibt die erwartete Ausgabe: {alt}")
                            break
        elif kind == "arrange":
            code = "\n".join(item["lines"])
            expected = item.get("expected")
            if expected is None:
                err = compiles(code)
                if err:
                    add("ERROR", f"Puzzle-Lösung kompiliert nicht: {err}")
                else:
                    add("NOOUT", "Puzzle ohne erwartete Ausgabe (andere gültige Reihenfolgen werden als falsch gewertet)")
            else:
                r = main_run(code, (), "stdout")
                fingerprint = fp(r, "stdout")
                if not r["ok"]:
                    add("ERROR", f"Puzzle-Lösung endet mit Fehler: {describe(r)}")
                elif normalize(r["stdout"]) != normalize(expected):
                    add("ERROR", "Erwartete Ausgabe stimmt nicht:\n" + diff(expected, r["stdout"]))
        elif kind == "code":
            program = item["solution"] + ("\n\n" + item["test"] if "test" in item else "")
            r = main_run(program, item["stdin"], "stdout")
            fingerprint = fp(r, "stdout")
            expected = item.get("expected")
            if not r["ok"]:
                add("ERROR", f"Musterlösung besteht nicht: {describe(r)}")
            elif expected is not None and normalize(r["stdout"]) != normalize(expected):
                add("ERROR", "Ausgabe der Musterlösung stimmt nicht:\n" + diff(expected, r["stdout"]))
            missing = [m for m in item["mustContain"] if m not in item["solution"]]
            if missing:
                add("ERROR", f"Musterlösung enthält nicht: {missing}")
            if r["ms"] > SLOW_MS:
                add("SLOW", f"{r['ms']} ms")
            starter_prog = item["starter"] + ("\n\n" + item["test"] if "test" in item else "")
            s = run(starter_prog, item["stdin"])
            starter_passes = s["ok"] and (expected is None or normalize(s["stdout"]) == normalize(expected)) and not [
                m for m in item["mustContain"] if m not in item["starter"]]
            if starter_passes:
                add("ERROR", "Die Vorlage besteht die Aufgabe schon ohne Änderung")
            if item["starter"].strip():
                err = compiles(item["starter"])
                if err:
                    add("INFO", f"Vorlage kompiliert nicht: {err}")
        elif kind == "example":
            r = main_run(item["code"], item["stdin"], "shown")
            fingerprint = fp(r, "shown")
            if not r["ok"]:
                add("ERROR", f"Beispielprogramm endet mit Fehler: {describe(r)}")
            if r["ms"] > SLOW_MS:
                add("SLOW", f"{r['ms']} ms")
        elif kind == "reference":
            r = main_run(item["code"], (), "shown")
            fingerprint = fp(r, "shown")
            if not r["ok"]:
                add("REF", f"Codebeispiel endet mit Fehler: {describe(r)}")
        elif kind == "compile":
            err = compiles(item["code"])
            if err:
                add("INFO", f"Code kompiliert nicht: {err}")
        expects = (kind in ("output", "typeOutput", "code") or "expected" in item
                   or (kind == "theory" and "output" in item))
        results.append({"where": where, "kind": kind, "findings": findings, "fingerprint": fingerprint,
                        "expects": expects})

    # Wie in der App: alle Programme nacheinander im selben Ordner, jedes zweimal hintereinander.
    for idx, code, stdin, key in main_programs:
        results[idx]["shared1"] = fp(run(code, stdin, shared), key)
        results[idx]["shared2"] = fp(run(code, stdin, shared), key)
    shutil.rmtree(shared, ignore_errors=True)

    json.dump(results, open(out_path, "w", encoding="utf-8"), ensure_ascii=False)


def fill_template(template, tiles):
    out = template
    for t in tiles:
        out = out.replace("??", t, 1)
    return out


def alternatives(item):
    answers = item["answers"]
    bank = answers + item["distractors"]
    k = len(answers)
    seen = {tuple(answers)}
    combos = 1
    for i in range(k):
        combos *= max(1, len(bank) - i)
    if combos <= MAX_COMBOS:
        candidates = (tuple(bank[i] for i in idx) for idx in itertools.permutations(range(len(bank)), k))
    else:
        def single():
            for pos in range(k):
                for t in bank:
                    alt = list(answers)
                    alt[pos] = t
                    yield tuple(alt)
        candidates = single()
    for alt in candidates:
        if alt in seen:
            continue
        seen.add(alt)
        yield list(alt)


def diff(expected, actual):
    e = normalize(expected).split("\n")
    a = normalize(actual).split("\n")
    lines = []
    for i in range(max(len(e), len(a))):
        x = e[i] if i < len(e) else "<fehlt>"
        y = a[i] if i < len(a) else "<fehlt>"
        mark = "  " if x == y else "≠ "
        lines.append(f"      {mark}erwartet: {x!r}")
        if x != y:
            lines.append(f"        echt:     {y!r}")
    return "\n".join(lines[:40])


def main():
    content = os.path.abspath(sys.argv[1])
    outdir = tempfile.mkdtemp(prefix="pyq_verify_")
    runs = []
    for seed in SEEDS:
        out = os.path.join(outdir, f"seed{seed}.json")
        env = dict(os.environ, PYTHONHASHSEED=seed)
        subprocess.run([sys.executable, "-I", "-X", "utf8", os.path.abspath(__file__), "--worker", content, out],
                       env=env, check=True)
        runs.append(json.load(open(out, encoding="utf-8")))
    shutil.rmtree(outdir, ignore_errors=True)

    first = runs[0]
    order = ["ERROR", "AMBIG", "NONDET", "STATE", "THEORY", "REF", "NOOUT", "SLOW", "INFO"]
    by_level = {lvl: [] for lvl in order}
    for idx, res in enumerate(first):
        prints = {r[idx]["fingerprint"] for r in runs}
        findings = list(res["findings"])
        if len(prints) == 1 and "shared1" in res:
            # Ohne feste Erwartung (z.B. Zeitmessungen) nur als Hinweis melden.
            level = "STATE" if res["expects"] else "INFO"
            if res["shared1"] != res["fingerprint"]:
                findings.append([level, "Anderes Ergebnis, wenn vorher andere Lektionen im selben Ordner liefen "
                                        "(Dateien?):\n" + diff(res["fingerprint"], res["shared1"])])
            elif res["shared2"] != res["shared1"]:
                findings.append([level, "Ergebnis ändert sich beim zweiten Ausführen (Dateien aus dem ersten Lauf?):\n"
                                 + diff(res["shared1"], res["shared2"])])
        if len(prints) > 1:
            if res["expects"]:
                findings.append(["NONDET", "Ausgabe ist bei jedem Lauf anders (Set-Reihenfolge oder Zufall?)"])
            else:
                findings.append(["INFO", "Ausgabe ändert sich von Lauf zu Lauf (ohne feste Erwartung – ok)"])
        for level, msg in findings:
            by_level[level].append((res["where"], msg))

    data = json.load(open(content, encoding="utf-8"))
    print(f"Geprüft: {len(first)} Prüfpunkte aus {data['units']} Einheiten / {data['lessons']} Lektionen / "
          f"{data['exercises']} Aufgaben, je {len(SEEDS)} Läufe")
    for p in data["problems"]:
        print("STRUKTUR", p)
    for level in order:
        entries = by_level[level]
        if not entries:
            continue
        print(f"\n===== {level} ({len(entries)}) =====")
        for where, msg in entries:
            print(f"- {where}\n    {msg}")
    serious = sum(len(by_level[lvl]) for lvl in ("ERROR", "AMBIG", "NONDET", "STATE", "THEORY"))
    print(f"\nErgebnis: {serious} ernsthafte Befunde")
    return 1 if serious else 0


if __name__ == "__main__":
    if len(sys.argv) >= 4 and sys.argv[1] == "--worker":
        worker(sys.argv[2], sys.argv[3])
    else:
        sys.exit(main())
