// Führt JavaScript-Code mit der Hissbit-Laufzeit (app/src/main/assets/js/prelude.js) in Node aus –
// genau wie die App, nur dass statt eines iframes ein vm-Kontext den frischen Realm liefert.
import vm from 'node:vm';
import fs from 'node:fs';
import { performance } from 'node:perf_hooks';

const preludeUrl = new URL('../app/src/main/assets/js/prelude.js', import.meta.url);
const preludeScript = new vm.Script(fs.readFileSync(preludeUrl, 'utf8'), { filename: 'hissbit-runtime.js' });

let currentApi = null;
process.on('unhandledRejection', (reason) => {
  if (currentApi) currentApi.unhandled(reason);
});

/**
 * @returns Promise<{stdout, stderr, aborted, exitCode, error, ms, chunks, hung?}>
 */
export function runJs(code, { stdin = [], timeoutMs = 6000, lang = 'de', realtime = false } = {}) {
  return new Promise((resolve) => {
    const chunks = [];
    let api = null;
    let finished = false;
    let watchdog = null;
    const host = {
      out: (kind, text) => { chunks.push([kind, text]); },
      readLine: () => null,
      sleep: () => false,
      poll: () => false,
      now: () => performance.now(),
      defer: (fn) => { setImmediate(fn); },
      attach: (a) => { api = a; },
      done: (json) => {
        if (finished) return;
        finished = true;
        currentApi = null;
        clearTimeout(watchdog);
        resolve({ ...JSON.parse(json), chunks });
      },
    };
    const context = vm.createContext({
      __hbHost: host,
      __hbConfig: { stdin, interactive: false, realtime, timeoutMs, lang },
    });
    preludeScript.runInContext(context);
    currentApi = api;
    watchdog = setTimeout(() => {
      if (finished) return;
      finished = true;
      currentApi = null;
      resolve({ hung: true, stdout: chunks.filter((c) => c[0] === 0).map((c) => c[1]).join(''), stderr: '', chunks });
    }, timeoutMs + 4000);

    let script;
    try {
      script = new vm.Script(code, { filename: 'main.js' });
    } catch (e) {
      const m = /main\.js:(\d+)/.exec(String(e && e.stack));
      let col;
      if (m) {
        const lines = String(e.stack).split('\n');
        if (lines.length > 2 && /^\s*\^/.test(lines[2])) col = lines[2].indexOf('^') + 1;
      }
      api.mainError(e, m ? +m[1] : undefined, col);
      api.afterMain();
      return;
    }
    try {
      script.runInContext(context, { timeout: timeoutMs + 1000 });
    } catch (e) {
      api.mainError(e);
    }
    api.afterMain();
  });
}
