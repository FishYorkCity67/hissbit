/*
 * Hissbit – Gastgeber-Seite in der unsichtbaren WebView. Für jeden Lauf entsteht ein frisches
 * iframe (eigener Realm: keine Variablen aus früheren Läufen), dorthin kommen zuerst die
 * Laufzeit (prelude.js) und dann das Programm als main.js. HB ist die Brücke zur App.
 */
(function () {
  'use strict';
  const PRELUDE = window.__HB_PRELUDE + '\n//# sourceURL=hissbit-runtime.js';
  delete window.__HB_PRELUDE;

  let frame = null;
  let queue = [];
  const channel = new MessageChannel();
  channel.port1.onmessage = function () {
    const fn = queue.shift();
    if (fn) {
      try { fn(); } catch (e) { console.error('hissbit: ' + (e && e.stack || e)); }
    }
  };
  function defer(fn) {
    queue.push(fn);
    channel.port2.postMessage(0);
  }

  function fail(id, text) {
    HB.done(id, JSON.stringify({
      stdout: '', stderr: '', aborted: '', ms: 0,
      error: { kind: 'value', text: text, phase: 'internal' },
    }));
  }

  window.__hbRun = function (id, payload) {
    queue = [];
    if (frame) { frame.remove(); frame = null; }
    frame = document.createElement('iframe');
    frame.style.display = 'none';
    document.body.appendChild(frame);
    const win = frame.contentWindow;
    const doc = frame.contentDocument;
    let api = null;
    let doneSent = false;
    let mainRunning = false;
    win.__hbHost = {
      out: (kind, text) => HB.out(id, kind, text),
      readLine: (prompt) => {
        const v = HB.readLine(id, prompt);
        return v === undefined ? null : v;
      },
      sleep: (ms) => HB.sleep(id, ms),
      poll: () => HB.poll(id),
      now: () => performance.now(),
      defer: defer,
      attach: (a) => { api = a; },
      done: (json) => {
        if (doneSent) return;
        doneSent = true;
        HB.done(id, json);
      },
    };
    win.__hbConfig = {
      stdin: payload.stdin,
      interactive: payload.interactive,
      realtime: payload.realtime,
      timeoutMs: payload.timeoutMs,
      lang: payload.lang,
      stream: payload.stream,
    };
    win.onerror = function (message, source, line, col, error) {
      if (!api) return true;
      const e = error === undefined || error === null ? new win.Error(String(message)) : error;
      if (mainRunning) api.mainError(e, line, col);
      else api.asyncError(e);
      return true;
    };
    win.addEventListener('unhandledrejection', function (ev) {
      ev.preventDefault();
      if (api) api.unhandled(ev.reason);
    });

    const prelude = doc.createElement('script');
    prelude.textContent = PRELUDE;
    (doc.head || doc.documentElement).appendChild(prelude);
    if (!api) {
      fail(id, 'Hissbit-Laufzeit konnte nicht starten');
      return;
    }

    mainRunning = true;
    const main = doc.createElement('script');
    main.textContent = payload.code + '\n//# sourceURL=main.js';
    (doc.body || doc.documentElement).appendChild(main);
    mainRunning = false;
    api.afterMain();
  };

  HB.ready();
})();
