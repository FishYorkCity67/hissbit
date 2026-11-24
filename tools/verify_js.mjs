// Prüft alle JavaScript-Kursinhalte mit der Hissbit-Laufzeit – genau so, wie die App sie ausführt
// (gleiche prelude.js, gleiche Schleifen-Wächter aus dem Export).
//
// Aufruf (nach ./gradlew :app:testDebugUnitTest --tests *ContentExportTest):
//   node tools/verify_js.mjs <build>/content-export/content-javascript-de.json
import fs from 'node:fs';
import vm from 'node:vm';
import { runJs } from './js_runtime_node.mjs';

const TIMEOUT = 6000;
const SLOW_MS = 1500;
const MAX_COMBOS = 400;
const RUNS = 2;

const normalize = (s) => String(s).replace(/\r\n/g, '\n').split('\n').map((l) => l.trimEnd()).join('\n').replace(/^\n+|\n+$/g, '');

async function run(code, stdin = []) {
  const started = Date.now();
  const r = await runJs(code, { stdin, timeoutMs: TIMEOUT });
  r.wall = Date.now() - started;
  r.ok = !r.hung && !r.error && (!r.aborted || (r.aborted === 'exit' && !r.exitCode));
  r.timedOut = r.hung || r.aborted === 'timeout';
  r.errorType = r.error ? (r.error.kind === 'value' ? 'Uncaught' : r.error.name)
    : r.aborted === 'output' ? 'OutputLimit' : r.aborted === 'exit' ? 'Exit' : null;
  r.shown = (r.chunks || []).filter((c) => c[0] === 0 || c[0] === 2).map((c) => c[1]).join('');
  r.stdout = r.stdout || '';
  return r;
}

function describe(r) {
  if (r.timedOut) return 'Zeitlimit überschritten';
  if (r.error) return r.error.kind === 'value' ? 'Uncaught ' + r.error.text : `${r.error.name}: ${r.error.message}`;
  if (r.aborted) return 'abgebrochen: ' + r.aborted;
  return '?';
}

const fp = (r, key) => normalize(r[key]) + (r.ok ? '' : `\n<${r.errorType || 'Zeitlimit'}>`);

function compiles(code) {
  try {
    new vm.Script(code, { filename: 'main.js' });
    return null;
  } catch (e) {
    const m = /main\.js:(\d+)/.exec(String(e.stack));
    return `${e.name}: ${e.message}${m ? ` (Zeile ${m[1]})` : ''}`;
  }
}

function fillTemplate(template, tiles) {
  let out = template;
  for (const t of tiles) out = out.replace('??', () => t);
  return out;
}

function* alternatives(item) {
  const answers = item.answers;
  const bank = answers.concat(item.distractors);
  const k = answers.length;
  const seen = new Set([JSON.stringify(answers)]);
  let combos = 1;
  for (let i = 0; i < k; i++) combos *= Math.max(1, bank.length - i);
  function* perms(prefix, used) {
    if (prefix.length === k) { yield prefix; return; }
    for (let i = 0; i < bank.length; i++) {
      if (used.has(i)) continue;
      used.add(i);
      yield* perms(prefix.concat([bank[i]]), used);
      used.delete(i);
    }
  }
  function* single() {
    for (let pos = 0; pos < k; pos++) {
      for (const t of bank) {
        const alt = answers.slice();
        alt[pos] = t;
        yield alt;
      }
    }
  }
  for (const alt of combos <= MAX_COMBOS ? perms([], new Set()) : single()) {
    const key = JSON.stringify(alt);
    if (seen.has(key)) continue;
    seen.add(key);
    yield alt;
  }
}

function diff(expected, actual) {
  const e = normalize(expected).split('\n');
  const a = normalize(actual).split('\n');
  const lines = [];
  for (let i = 0; i < Math.max(e.length, a.length); i++) {
    const x = i < e.length ? e[i] : '<fehlt>';
    const y = i < a.length ? a[i] : '<fehlt>';
    lines.push(`      ${x === y ? '  ' : '≠ '}erwartet: ${JSON.stringify(x)}`);
    if (x !== y) lines.push(`        echt:     ${JSON.stringify(y)}`);
  }
  return lines.slice(0, 40).join('\n');
}

async function check(item) {
  const findings = [];
  const add = (level, msg) => findings.push([level, msg]);
  let fingerprint = '';
  const kind = item.kind;

  if (kind === 'theory') {
    if (!item.runnable) {
      const err = compiles(item.code);
      if (err) add('INFO', `nicht ausführbarer Theorie-Code kompiliert nicht: ${err}`);
    } else {
      const r = await run(item.codeI ?? item.code, item.stdin);
      fingerprint = fp(r, 'shown');
      if (item.fails) {
        if (r.ok) add('THEORY', 'Theorie-Code soll einen Fehler zeigen, läuft aber fehlerfrei');
      } else if (!r.ok) {
        add('THEORY', `Theorie-Code endet mit Fehler: ${describe(r)}`);
      }
      if ('output' in item && normalize(r.shown) !== normalize(item.output)) {
        add('THEORY', 'Angezeigte Ausgabe stimmt nicht:\n' + diff(item.output, r.shown));
      }
      if (r.ms > SLOW_MS) add('SLOW', `${r.ms} ms`);
    }
  } else if (kind === 'output' || kind === 'typeOutput') {
    const r = await run(item.codeI ?? item.code);
    fingerprint = fp(r, 'stdout');
    const expected = kind === 'output' ? item.options[0] : item.expected;
    if (!r.ok) add('ERROR', `Code endet mit Fehler: ${describe(r)}`);
    else if (normalize(r.stdout) !== normalize(expected)) {
      add('ERROR', 'Richtige Antwort stimmt nicht mit der echten Ausgabe überein:\n' + diff(expected, r.stdout));
    }
    if (kind === 'output') {
      for (const other of item.options.slice(1)) {
        if (r.ok && normalize(other) === normalize(r.stdout)) add('ERROR', `Falsche Option entspricht der echten Ausgabe: ${JSON.stringify(other)}`);
      }
    }
  } else if (kind === 'fill') {
    const expected = item.expected;
    if (expected === undefined) {
      const err = compiles(item.solved);
      if (err) add('INFO', `Lückentext (ohne Ausgabe) kompiliert nicht: ${err}`);
    } else {
      const r = await run(item.solvedI ?? item.solved);
      fingerprint = fp(r, 'stdout');
      if (!r.ok) add('ERROR', `Gelöster Lückentext endet mit Fehler: ${describe(r)}`);
      else if (normalize(r.stdout) !== normalize(expected)) add('ERROR', 'Erwartete Ausgabe stimmt nicht:\n' + diff(expected, r.stdout));
      else {
        for (const alt of alternatives(item)) {
          const filled = fillTemplate(item.template, alt);
          if (compiles(filled)) continue;
          const r2 = await run(filled);
          if (r2.ok && normalize(r2.stdout) === normalize(expected)) {
            add('AMBIG', `Auch diese Lösung ergibt die erwartete Ausgabe: ${JSON.stringify(alt)}`);
            break;
          }
        }
      }
    }
  } else if (kind === 'arrange') {
    const code = item.lines.join('\n');
    const expected = item.expected;
    if (expected === undefined) {
      const err = compiles(code);
      if (err) add('ERROR', `Puzzle-Lösung kompiliert nicht: ${err}`);
      else add('NOOUT', 'Puzzle ohne erwartete Ausgabe (andere gültige Reihenfolgen werden als falsch gewertet)');
    } else {
      const r = await run(item.codeI ?? code);
      fingerprint = fp(r, 'stdout');
      if (!r.ok) add('ERROR', `Puzzle-Lösung endet mit Fehler: ${describe(r)}`);
      else if (normalize(r.stdout) !== normalize(expected)) add('ERROR', 'Erwartete Ausgabe stimmt nicht:\n' + diff(expected, r.stdout));
    }
  } else if (kind === 'code') {
    const program = item.solution + ('test' in item ? '\n\n' + item.test : '');
    const r = await run(item.programI ?? program, item.stdin);
    fingerprint = fp(r, 'stdout');
    const expected = item.expected;
    if (!r.ok) add('ERROR', `Musterlösung besteht nicht: ${describe(r)}`);
    else if (expected !== undefined && normalize(r.stdout) !== normalize(expected)) {
      add('ERROR', 'Ausgabe der Musterlösung stimmt nicht:\n' + diff(expected, r.stdout));
    }
    const missing = item.mustContain.filter((m) => !item.solution.includes(m));
    if (missing.length) add('ERROR', `Musterlösung enthält nicht: ${JSON.stringify(missing)}`);
    if (r.ms > SLOW_MS) add('SLOW', `${r.ms} ms`);
    const starterProg = item.starter + ('test' in item ? '\n\n' + item.test : '');
    const s = await run(starterProg, item.stdin);
    const starterPasses = s.ok && (expected === undefined || normalize(s.stdout) === normalize(expected)) &&
      !item.mustContain.some((m) => !item.starter.includes(m));
    if (starterPasses) add('ERROR', 'Die Vorlage besteht die Aufgabe schon ohne Änderung');
    if (item.starter.trim()) {
      const err = compiles(item.starter);
      if (err) add('INFO', `Vorlage kompiliert nicht: ${err}`);
    }
  } else if (kind === 'example') {
    const r = await run(item.codeI ?? item.code, item.stdin);
    fingerprint = fp(r, 'shown');
    if (!r.ok) add('ERROR', `Beispielprogramm endet mit Fehler: ${describe(r)}`);
    if (r.ms > SLOW_MS) add('SLOW', `${r.ms} ms`);
  } else if (kind === 'reference') {
    // Auch im Fehlerlexikon zeigt der Code die korrigierte Fassung – er muss fehlerfrei laufen.
    const r = await run(item.codeI ?? item.code);
    fingerprint = fp(r, 'shown');
    if (!r.ok) add('REF', `Codebeispiel endet mit Fehler: ${describe(r)}`);
  } else if (kind === 'compile') {
    const err = compiles(item.code);
    if (err) add('INFO', `Code kompiliert nicht: ${err}`);
  }
  const expects = ['output', 'typeOutput', 'code'].includes(kind) || 'expected' in item || (kind === 'theory' && 'output' in item);
  return { where: item.where, kind, findings, fingerprint, expects };
}

async function main() {
  const file = process.argv[2];
  if (!file) {
    console.error('Aufruf: node tools/verify_js.mjs <content-javascript-de.json>');
    process.exit(2);
  }
  const data = JSON.parse(fs.readFileSync(file, 'utf8'));
  const runs = [];
  for (let i = 0; i < RUNS; i++) {
    const results = [];
    for (const item of data.items) results.push(await check(item));
    runs.push(results);
  }
  const order = ['ERROR', 'AMBIG', 'NONDET', 'THEORY', 'REF', 'NOOUT', 'SLOW', 'INFO'];
  const byLevel = Object.fromEntries(order.map((l) => [l, []]));
  runs[0].forEach((res, idx) => {
    const prints = new Set(runs.map((r) => r[idx].fingerprint));
    const findings = res.findings.slice();
    if (prints.size > 1) {
      findings.push(res.expects
        ? ['NONDET', 'Ausgabe ist bei jedem Lauf anders (Zufall oder Uhrzeit?)']
        : ['INFO', 'Ausgabe ändert sich von Lauf zu Lauf (ohne feste Erwartung – ok)']);
    }
    for (const [level, msg] of findings) byLevel[level].push([res.where, msg]);
  });
  console.log(`Geprüft: ${runs[0].length} Prüfpunkte aus ${data.units} Einheiten / ${data.lessons} Lektionen / ${data.exercises} Aufgaben, je ${RUNS} Läufe`);
  for (const p of data.problems) console.log('STRUKTUR', p);
  for (const level of order) {
    const entries = byLevel[level];
    if (!entries.length) continue;
    console.log(`\n===== ${level} (${entries.length}) =====`);
    for (const [where, msg] of entries) console.log(`- ${where}\n    ${msg}`);
  }
  const serious = ['ERROR', 'AMBIG', 'NONDET', 'THEORY', 'REF'].reduce((n, l) => n + byLevel[l].length, 0);
  console.log(`\nErgebnis: ${serious} ernsthafte Befunde`);
  process.exitCode = serious ? 1 : 0;
}

main();
