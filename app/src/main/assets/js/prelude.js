/*
 * Hissbit – Laufzeit für JavaScript-Programme von Lernenden.
 *
 * Läuft in einem frischen Realm (in der App: ein iframe in einer unsichtbaren WebView,
 * im Prüfskript tools/verify_js.mjs: ein vm-Kontext von Node) VOR dem eigentlichen
 * Programm. Stellt bereit: console (Ausgabe wie in Node.js), prompt(), Timer mit eigener
 * Ereignisschleife, Schutz vor Endlosschleifen (__hb_g/__hb_gi, eingefügt vom
 * JsInstrumenter) sowie Zeitlimit und Abbruch.
 *
 * Erwartet die Globals __hbHost (Brücke zur App) und __hbConfig; beide werden sofort entfernt.
 */
(function () {
  'use strict';
  const G = globalThis;
  const host = G.__hbHost;
  const cfg = G.__hbConfig || {};
  try { delete G.__hbHost; } catch (e) { /* egal */ }
  try { delete G.__hbConfig; } catch (e) { /* egal */ }

  const de = cfg.lang !== 'en';
  const realtime = !!cfg.realtime;
  const interactive = !!cfg.interactive;
  // Ohne Live-Konsole (Lektionsprüfung) kommt die Ausgabe erst am Ende – spart Brückenaufrufe.
  const stream = cfg.stream !== false;
  const timeoutMs = +cfg.timeoutMs || 0;
  const stdinLines = Array.isArray(cfg.stdin) ? cfg.stdin.slice() : [];
  const MAX_OUTPUT = 300000;
  const VIRTUAL_LIMIT_MS = 10 * 60 * 1000;

  // ------------------------------------------------------------------ Grundbausteine
  // Lernende dürfen Prototypen verändern – die Laufzeit hält sich an die Originale.
  const uncurry = (fn) => Function.prototype.call.bind(fn);
  const _Map = Map;
  const _String = String;
  const _Number = Number;
  const _TypeError = TypeError;
  const _Symbol = Symbol;
  const _JSON = JSON;
  const _Math = Math;
  const ObjectKeys = Object.keys;
  const getOwnPropertySymbols = Object.getOwnPropertySymbols;
  const getPrototypeOf = Object.getPrototypeOf;
  const getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;
  const defineProperty = Object.defineProperty;
  const objectIs = Object.is;
  const isArray = Array.isArray;
  const reflectApply = Reflect.apply;
  const hasOwn = uncurry(Object.prototype.hasOwnProperty);
  const isEnumerable = uncurry(Object.prototype.propertyIsEnumerable);
  const objToString = uncurry(Object.prototype.toString);
  const fnToString = uncurry(Function.prototype.toString);
  const symToString = uncurry(Symbol.prototype.toString);
  const dateGetTime = uncurry(Date.prototype.getTime);
  const dateToISOString = uncurry(Date.prototype.toISOString);
  const dateToString = uncurry(Date.prototype.toString);
  const regexpToString = uncurry(RegExp.prototype.toString);
  const regexpSource = uncurry(getOwnPropertyDescriptor(RegExp.prototype, 'source').get);
  const regexpTest = uncurry(RegExp.prototype.test);
  const mapSize = uncurry(getOwnPropertyDescriptor(Map.prototype, 'size').get);
  const setSize = uncurry(getOwnPropertyDescriptor(Set.prototype, 'size').get);
  const mapEntries = uncurry(Map.prototype.entries);
  const setValues = uncurry(Set.prototype.values);
  const mapIterNext = uncurry(getPrototypeOf(new Map().entries()).next);
  const setIterNext = uncurry(getPrototypeOf(new Set().values()).next);
  const typedArrayProto = getPrototypeOf(Uint8Array.prototype);
  const typedArrayTag = uncurry(getOwnPropertyDescriptor(typedArrayProto, Symbol.toStringTag).get);
  const typedArrayLength = uncurry(getOwnPropertyDescriptor(typedArrayProto, 'length').get);
  const numberValueOf = uncurry(Number.prototype.valueOf);
  const stringValueOf = uncurry(String.prototype.valueOf);
  const booleanValueOf = uncurry(Boolean.prototype.valueOf);
  const symbolValueOf = uncurry(Symbol.prototype.valueOf);
  const bigintValueOf = typeof BigInt === 'function' ? uncurry(BigInt.prototype.valueOf) : null;
  const strSlice = uncurry(String.prototype.slice);
  const strIncludes = uncurry(String.prototype.includes);
  const strRepeat = uncurry(String.prototype.repeat);
  const strPadStart = uncurry(String.prototype.padStart);
  const strPadEnd = uncurry(String.prototype.padEnd);
  const strCharCodeAt = uncurry(String.prototype.charCodeAt);
  const strStartsWith = uncurry(String.prototype.startsWith);
  const strEndsWith = uncurry(String.prototype.endsWith);
  const strIndexOf = uncurry(String.prototype.indexOf);
  const strSplit = uncurry(String.prototype.split);
  const strReplace = uncurry(String.prototype.replace);
  const numToString = uncurry(Number.prototype.toString);
  const numToFixed = uncurry(Number.prototype.toFixed);
  const parseIntFn = parseInt;
  const parseFloatFn = parseFloat;
  const now = () => host.now();

  function push(arr, v) { arr[arr.length] = v; }
  function contains(arr, v) {
    for (let i = 0; i < arr.length; i++) if (arr[i] === v) return true;
    return false;
  }
  function joinList(arr, sep) {
    let s = '';
    for (let i = 0; i < arr.length; i++) s += (i === 0 ? '' : sep) + arr[i];
    return s;
  }
  function tryGet(fn, fallback) {
    try { return fn(); } catch (e) { return fallback; }
  }
  function test(fn, v) { try { fn(v); return true; } catch (e) { return false; } }
  const isMap = (v) => test(mapSize, v);
  const isSet = (v) => test(setSize, v);
  const isDate = (v) => test(dateGetTime, v);
  const isRegExp = (v) => test(regexpSource, v);
  const isTypedArray = (v) => tryGet(() => typedArrayTag(v), undefined) !== undefined;
  const tagOf = (v) => tryGet(() => objToString(v), '[object Object]');
  const isError = (v) => tagOf(v) === '[object Error]';
  const isPromise = (v) => tagOf(v) === '[object Promise]';

  // ------------------------------------------------------------------ Zustand
  const ABORT = Object.freeze({ toString() { return de ? 'Programm abgebrochen' : 'Program stopped'; } });
  const S = {
    aborted: '',          // '', 'timeout', 'cancel', 'output', 'exit'
    exitCode: 0,
    size: 0,
    stdout: '',
    stderr: '',
    pend: '',
    pendKind: 0,
    lastFlush: 0,
    lastPoll: 0,
    waitMs: 0,
    start: now(),
    finished: false,
    error: null,
    indent: '',
  };
  S.lastFlush = S.start;
  S.lastPoll = S.start;

  function stop(kind) {
    if (!S.aborted) S.aborted = kind;
    throw ABORT;
  }

  /** Programmzeit in ms seit dem Start (bei virtueller Zeit inklusive übersprungener Wartezeiten). */
  let vSkip = 0;
  function clock() { return now() - S.start + vSkip; }
  function busyMs() { return now() - S.start - S.waitMs; }

  function checkLimits() {
    const t = now();
    if (S.pend && t - S.lastFlush > 50) flush();
    if (timeoutMs > 0 && t - S.start - S.waitMs > timeoutMs) stop('timeout');
    if (t - S.lastPoll > 100) {
      S.lastPoll = t;
      if (host.poll()) stop('cancel');
    }
  }

  // ------------------------------------------------------------------ Ausgabe
  function flush() {
    if (S.pend) {
      const text = S.pend;
      S.pend = '';
      host.out(S.pendKind, text);
    }
    S.lastFlush = now();
  }

  function emit(kind, text) {
    if (S.aborted) throw ABORT;
    S.size += text.length;
    if (S.size > MAX_OUTPUT) stop('output');
    if (kind === 1) S.stderr += text; else S.stdout += text;
    if (!stream) return;
    if (S.pend && S.pendKind !== kind) flush();
    S.pendKind = kind;
    S.pend += text;
    if (S.pend.length > 8192 || now() - S.lastFlush > 60) flush();
  }

  function echo(text) {
    if (!stream) return;
    flush();
    host.out(2, text);
  }

  // ------------------------------------------------------------------ inspect (wie Node.js util.inspect)
  const kObjectType = 0;
  const kArrayType = 1;
  const kArrayExtrasType = 2;
  const keyStrRegExp = /^[a-zA-Z_][a-zA-Z_0-9]*$/;
  const numberRegExp = /^(0|[1-9][0-9]*)$/;
  const strEscapeSequencesRegExp = /[\x00-\x1f\x27\x5c\x7f-\x9f]/;
  const strEscapeSequencesRegExpSingle = /[\x00-\x1f\x5c\x7f-\x9f]/;
  const meta = [];
  for (let i = 0; i < 160; i++) meta[i] = '';
  for (let i = 0; i < 32; i++) meta[i] = '\\x' + (i < 16 ? '0' : '') + numToString(i, 16).toUpperCase();
  meta[8] = '\\b'; meta[9] = '\\t'; meta[10] = '\\n'; meta[12] = '\\f'; meta[13] = '\\r';
  meta[39] = "\\'"; meta[92] = '\\\\';
  for (let i = 127; i < 160; i++) meta[i] = '\\x' + numToString(i, 16).toUpperCase();

  function addQuotes(str, quotes) {
    if (quotes === -1) return '"' + str + '"';
    if (quotes === -2) return '`' + str + '`';
    return "'" + str + "'";
  }

  function strEscape(str) {
    let escapeTest = strEscapeSequencesRegExp;
    let singleQuote = 39;
    if (strIncludes(str, "'")) {
      if (!strIncludes(str, '"')) singleQuote = -1;
      else if (!strIncludes(str, '`') && !strIncludes(str, '${')) singleQuote = -2;
      if (singleQuote !== 39) escapeTest = strEscapeSequencesRegExpSingle;
    }
    if (str.length < 5000 && !regexpTest(escapeTest, str)) return addQuotes(str, singleQuote);
    let result = '';
    let last = 0;
    for (let i = 0; i < str.length; i++) {
      const point = strCharCodeAt(str, i);
      if (point === singleQuote || point === 92 || point < 32 || (point > 126 && point < 160)) {
        result += (last === i ? '' : strSlice(str, last, i)) + meta[point];
        last = i + 1;
      } else if (point >= 0xd800 && point <= 0xdfff) {
        if (point <= 0xdbff && i + 1 < str.length) {
          const next = strCharCodeAt(str, i + 1);
          if (next >= 0xdc00 && next <= 0xdfff) { i++; continue; }
        }
        result += strSlice(str, last, i) + '\\u' + numToString(point, 16).toUpperCase();
        last = i + 1;
      }
    }
    if (last !== str.length) result += strSlice(str, last);
    return addQuotes(result, singleQuote);
  }

  function formatNumber(n) {
    return objectIs(n, -0) ? '-0' : _String(n);
  }

  function formatPrimitive(value, ctx) {
    switch (typeof value) {
      case 'string': {
        if (value.length > 16 && value.length > ctx.breakLength - ctx.indentationLvl - 4) {
          const parts = strSplit(value, /(?<=\n)/);
          if (parts.length > 1) {
            const escaped = [];
            for (let i = 0; i < parts.length; i++) push(escaped, strEscape(parts[i]));
            return joinList(escaped, ' +\n' + strRepeat(' ', ctx.indentationLvl + 2));
          }
        }
        return strEscape(value);
      }
      case 'number': return formatNumber(value);
      case 'bigint': return _String(value) + 'n';
      case 'boolean': return value ? 'true' : 'false';
      case 'undefined': return 'undefined';
      case 'symbol': return symToString(value);
      default: return _String(value);
    }
  }

  function inspect(value, opts) {
    const ctx = {
      seen: [],
      circular: undefined,
      indentationLvl: 0,
      currentDepth: 0,
      depth: opts && opts.depth !== undefined ? opts.depth : 2,
      breakLength: 80,
      compact: 3,
      maxArrayLength: 100,
    };
    return formatValue(ctx, value, 0);
  }

  function formatValue(ctx, value, recurseTimes, typedArray) {
    if (value === null) return 'null';
    if (typeof value !== 'object' && typeof value !== 'function') return formatPrimitive(value, ctx);
    if (contains(ctx.seen, value)) {
      let index = 1;
      if (ctx.circular === undefined) {
        ctx.circular = new _Map();
        ctx.circular.set(value, index);
      } else {
        index = ctx.circular.get(value);
        if (index === undefined) {
          index = ctx.circular.size + 1;
          ctx.circular.set(value, index);
        }
      }
      return '[Circular *' + index + ']';
    }
    return formatRaw(ctx, value, recurseTimes, typedArray);
  }

  function isInstanceof(obj, ctor) {
    try { return obj instanceof ctor; } catch (e) { return false; }
  }

  function getConstructorName(obj) {
    let o = obj;
    let firstProto;
    let first = true;
    while (o !== null && o !== undefined) {
      const desc = tryGet(() => getOwnPropertyDescriptor(o, 'constructor'), undefined);
      if (desc !== undefined && typeof desc.value === 'function' && desc.value.name !== '' && isInstanceof(obj, desc.value)) {
        return _String(desc.value.name);
      }
      o = tryGet(() => getPrototypeOf(o), null);
      if (first) { firstProto = o; first = false; }
    }
    if (firstProto === null) return null;
    return 'Object';
  }

  function getPrefix(constructor, tag, fallback, size) {
    size = size || '';
    if (constructor === null) {
      if (tag !== '' && fallback !== tag) return '[' + fallback + size + ': null prototype] [' + tag + '] ';
      return '[' + fallback + size + ': null prototype] ';
    }
    if (tag !== '' && constructor !== tag) return constructor + size + ' [' + tag + '] ';
    return constructor + size + ' ';
  }

  function getKeys(value) {
    const keys = ObjectKeys(value);
    const symbols = getOwnPropertySymbols(value);
    for (let i = 0; i < symbols.length; i++) {
      if (isEnumerable(value, symbols[i])) push(keys, symbols[i]);
    }
    return keys;
  }

  function getNonIndexKeys(value) {
    const keys = [];
    const all = ObjectKeys(value);
    for (let i = 0; i < all.length; i++) {
      if (!regexpTest(numberRegExp, all[i]) || +all[i] >= 4294967295) push(keys, all[i]);
    }
    const symbols = getOwnPropertySymbols(value);
    for (let i = 0; i < symbols.length; i++) {
      if (isEnumerable(value, symbols[i])) push(keys, symbols[i]);
    }
    return keys;
  }

  function getFunctionBase(value, constructor, tag) {
    const stringified = tryGet(() => fnToString(value), '');
    if (strStartsWith(stringified, 'class') && strEndsWith(stringified, '}')) {
      const slice = strSlice(stringified, 5, -1);
      const bracketIndex = strIndexOf(slice, '{');
      if (bracketIndex !== -1 && (!strIncludes(strSlice(slice, 0, bracketIndex), '(') || regexpTest(/^(\s+[^(]*?)\s*{/, slice))) {
        return getClassBase(value, constructor, tag);
      }
    }
    let type = 'Function';
    const t = tagOf(value);
    if (t === '[object GeneratorFunction]') type = 'GeneratorFunction';
    else if (t === '[object AsyncFunction]') type = 'AsyncFunction';
    else if (t === '[object AsyncGeneratorFunction]') type = 'AsyncGeneratorFunction';
    let base = '[' + type;
    if (constructor === null) base += ' (null prototype)';
    const name = tryGet(() => value.name, '');
    if (name === '' || typeof name !== 'string') base += ' (anonymous)';
    else base += ': ' + name;
    base += ']';
    if (constructor !== type && constructor !== null) base += ' ' + constructor;
    if (tag !== '' && constructor !== tag) base += ' [' + tag + ']';
    return base;
  }

  function getClassBase(value, constructor, tag) {
    const name = (hasOwn(value, 'name') && tryGet(() => value.name, '')) || '(anonymous)';
    let base = 'class ' + name;
    if (constructor !== 'Function' && constructor !== null) base += ' [' + constructor + ']';
    if (tag !== '' && constructor !== tag) base += ' [' + tag + ']';
    if (constructor !== null) {
      const superName = tryGet(() => getPrototypeOf(value).name, '');
      if (superName) base += ' extends ' + superName;
    } else {
      base += ' extends [null prototype]';
    }
    return '[' + base + ']';
  }

  function formatErrorBase(err, keys, nested) {
    let name = tryGet(() => err.name, 'Error');
    let message = tryGet(() => err.message, '');
    name = name === undefined || name === null ? 'Error' : _String(name);
    message = message === undefined || message === null ? '' : _String(message);
    let base = message === '' ? name : (name === '' ? message : name + ': ' + message);
    // name/message nicht doppelt anzeigen
    for (let i = keys.length - 1; i >= 0; i--) {
      const k = keys[i];
      if ((k === 'name' || k === 'message' || k === 'stack') && typeof tryGet(() => err[k], undefined) === 'string' &&
          strIncludes(base, err[k])) {
        keys.splice(i, 1);
      }
    }
    return nested ? '[' + base + ']' : base;
  }

  function formatRaw(ctx, value, recurseTimes, typedArray) {
    let keys;
    const constructor = getConstructorName(value);
    let tag = tryGet(() => value[Symbol.toStringTag], '');
    if (typeof tag !== 'string' || (tag !== '' && tag === constructor)) tag = '';
    let base = '';
    let formatter = () => [];
    let braces;
    let noIterator = true;
    let extrasType = kObjectType;

    if (tryGet(() => value[Symbol.iterator], undefined) || constructor === null) {
      noIterator = false;
      if (isArray(value)) {
        const prefix = (constructor !== 'Array' || tag !== '') ? getPrefix(constructor, tag, 'Array', '(' + value.length + ')') : '';
        keys = getNonIndexKeys(value);
        braces = [prefix + '[', ']'];
        if (value.length === 0 && keys.length === 0) return braces[0] + ']';
        extrasType = kArrayExtrasType;
        formatter = formatArray;
      } else if (isSet(value)) {
        const size = setSize(value);
        const prefix = getPrefix(constructor, tag, 'Set', '(' + size + ')');
        keys = getKeys(value);
        formatter = formatSet;
        if (size === 0 && keys.length === 0) return prefix + '{}';
        braces = [prefix + '{', '}'];
      } else if (isMap(value)) {
        const size = mapSize(value);
        const prefix = getPrefix(constructor, tag, 'Map', '(' + size + ')');
        keys = getKeys(value);
        formatter = formatMap;
        if (size === 0 && keys.length === 0) return prefix + '{}';
        braces = [prefix + '{', '}'];
      } else if (isTypedArray(value)) {
        keys = getNonIndexKeys(value);
        const fallback = typedArrayTag(value);
        const size = typedArrayLength(value);
        const prefix = getPrefix(constructor, tag, fallback, '(' + size + ')');
        braces = [prefix + '[', ']'];
        if (size === 0 && keys.length === 0) return braces[0] + ']';
        formatter = formatTypedArray;
        extrasType = kArrayExtrasType;
      } else {
        noIterator = true;
      }
    }
    if (noIterator) {
      keys = getKeys(value);
      braces = ['{', '}'];
      if (constructor === 'Object') {
        if (tagOf(value) === '[object Arguments]') braces[0] = '[Arguments] {';
        else if (tag !== '') braces[0] = getPrefix(constructor, tag, 'Object') + '{';
        if (keys.length === 0) return braces[0] + '}';
      } else if (typeof value === 'function') {
        base = getFunctionBase(value, constructor, tag);
        if (keys.length === 0) return base;
      } else if (isRegExp(value)) {
        base = regexpToString(value);
        const prefix = getPrefix(constructor, tag, 'RegExp');
        if (prefix !== 'RegExp ') base = prefix + base;
        if (keys.length === 0 || recurseTimes > ctx.depth) return base;
      } else if (isDate(value)) {
        const time = dateGetTime(value);
        base = time !== time ? dateToString(value) : dateToISOString(value);
        const prefix = getPrefix(constructor, tag, 'Date');
        if (prefix !== 'Date ') base = prefix + base;
        if (keys.length === 0) return base;
      } else if (isError(value)) {
        base = formatErrorBase(value, keys, recurseTimes > 0);
        if (keys.length === 0) return base;
      } else if (isPromise(value)) {
        braces[0] = getPrefix(constructor, tag, 'Promise') + '{';
        formatter = () => ['<pending>'];
      } else if (tagOf(value) === '[object WeakMap]' || tagOf(value) === '[object WeakSet]') {
        const kind = tagOf(value) === '[object WeakMap]' ? 'WeakMap' : 'WeakSet';
        braces[0] = getPrefix(constructor, tag, kind) + '{';
        formatter = () => ['<items unknown>'];
      } else {
        const boxed = boxedBase(value);
        if (boxed !== null) {
          base = boxed;
          if (test(stringValueOf, value)) keys = getNonIndexKeys(value);
          if (keys.length === 0) return base;
        } else {
          if (keys.length === 0) return getPrefix(constructor, tag, 'Object') + '{}';
          braces[0] = getPrefix(constructor, tag, 'Object') + '{';
        }
      }
    }

    if (recurseTimes > ctx.depth && ctx.depth !== null) {
      let name = strSlice(getPrefix(constructor, tag, 'Object'), 0, -1);
      if (constructor !== null) name = '[' + name + ']';
      return name;
    }
    recurseTimes += 1;
    push(ctx.seen, value);
    ctx.currentDepth = recurseTimes;
    let output;
    try {
      output = formatter(ctx, value, recurseTimes);
      for (let i = 0; i < keys.length; i++) {
        push(output, formatProperty(ctx, value, recurseTimes, keys[i], extrasType));
      }
    } catch (err) {
      if (err === ABORT) throw err;
      ctx.seen.pop();
      return '[' + (constructor || 'Object') + ': Inspection interrupted]';
    }
    if (ctx.circular !== undefined) {
      const index = ctx.circular.get(value);
      if (index !== undefined) {
        const reference = '<ref *' + index + '>';
        base = base === '' ? reference : reference + ' ' + base;
      }
    }
    ctx.seen.pop();
    return reduceToSingleString(ctx, output, base, braces, extrasType, recurseTimes, value);
  }

  function boxedBase(value) {
    if (test(numberValueOf, value)) return '[Number: ' + formatNumber(numberValueOf(value)) + ']';
    if (test(stringValueOf, value)) return '[String: ' + strEscape(stringValueOf(value)) + ']';
    if (test(booleanValueOf, value)) return '[Boolean: ' + booleanValueOf(value) + ']';
    if (test(symbolValueOf, value)) return '[Symbol: ' + symToString(symbolValueOf(value)) + ']';
    if (bigintValueOf && test(bigintValueOf, value)) return '[BigInt: ' + bigintValueOf(value) + 'n]';
    return null;
  }

  function formatProperty(ctx, value, recurseTimes, key, type) {
    let name;
    let str;
    const desc = tryGet(() => getOwnPropertyDescriptor(value, key), undefined) || { value: tryGet(() => value[key], undefined), enumerable: true };
    if (desc.value !== undefined) {
      ctx.indentationLvl += 2;
      str = formatValue(ctx, desc.value, recurseTimes);
      ctx.indentationLvl -= 2;
    } else if (desc.get !== undefined) {
      str = desc.set !== undefined ? '[Getter/Setter]' : '[Getter]';
    } else if (desc.set !== undefined) {
      str = '[Setter]';
    } else {
      str = 'undefined';
    }
    if (type === kArrayType) return str;
    if (typeof key === 'symbol') {
      name = desc.enumerable === false ? '[' + symToString(key) + ']' : symToString(key);
    } else if (key === '__proto__') {
      name = "['__proto__']";
    } else if (desc.enumerable === false) {
      name = '[' + key + ']';
    } else if (regexpTest(keyStrRegExp, key)) {
      name = key;
    } else {
      name = strEscape(key);
    }
    return name + ': ' + str;
  }

  function formatArray(ctx, value, recurseTimes) {
    const valLen = value.length;
    const len = _Math.min(_Math.max(0, ctx.maxArrayLength), valLen);
    const remaining = valLen - len;
    const output = [];
    for (let i = 0; i < len; i++) {
      if (!hasOwn(value, i)) return formatSpecialArray(ctx, value, recurseTimes, len, output, i);
      push(output, formatProperty(ctx, value, recurseTimes, i, kArrayType));
    }
    if (remaining > 0) push(output, '... ' + remaining + ' more item' + (remaining > 1 ? 's' : ''));
    return output;
  }

  function formatSpecialArray(ctx, value, recurseTimes, maxLength, output, i) {
    const keys = ObjectKeys(value);
    let index = i;
    for (; i < keys.length && output.length < maxLength; i++) {
      const key = keys[i];
      const tmp = +key;
      if (tmp > 2 ** 32 - 2) break;
      if (_String(index) !== key) {
        if (!regexpTest(numberRegExp, key)) break;
        const emptyItems = tmp - index;
        push(output, '<' + emptyItems + ' empty item' + (emptyItems > 1 ? 's' : '') + '>');
        index = tmp;
        if (output.length === maxLength) break;
      }
      push(output, formatProperty(ctx, value, recurseTimes, key, kArrayType));
      index++;
    }
    const remaining = value.length - index;
    if (output.length !== maxLength) {
      if (remaining > 0) push(output, '<' + remaining + ' empty item' + (remaining > 1 ? 's' : '') + '>');
    } else if (remaining > 0) {
      push(output, '... ' + remaining + ' more item' + (remaining > 1 ? 's' : ''));
    }
    return output;
  }

  function formatTypedArray(ctx, value, recurseTimes) {
    const length = typedArrayLength(value);
    const max = _Math.min(_Math.max(0, ctx.maxArrayLength), length);
    const output = [];
    for (let i = 0; i < max; i++) {
      const v = value[i];
      push(output, typeof v === 'bigint' ? _String(v) + 'n' : formatNumber(v));
    }
    const remaining = length - max;
    if (remaining > 0) push(output, '... ' + remaining + ' more item' + (remaining > 1 ? 's' : ''));
    return output;
  }

  function formatSet(ctx, value, recurseTimes) {
    const output = [];
    ctx.indentationLvl += 2;
    const it = setValues(value);
    for (let r = setIterNext(it); !r.done; r = setIterNext(it)) {
      push(output, formatValue(ctx, r.value, recurseTimes));
    }
    ctx.indentationLvl -= 2;
    return output;
  }

  function formatMap(ctx, value, recurseTimes) {
    const output = [];
    ctx.indentationLvl += 2;
    const it = mapEntries(value);
    for (let r = mapIterNext(it); !r.done; r = mapIterNext(it)) {
      push(output, formatValue(ctx, r.value[0], recurseTimes) + ' => ' + formatValue(ctx, r.value[1], recurseTimes));
    }
    ctx.indentationLvl -= 2;
    return output;
  }

  function groupArrayElements(ctx, output, value) {
    let totalLength = 0;
    let maxLength = 0;
    let i = 0;
    let outputLength = output.length;
    if (ctx.maxArrayLength < output.length) outputLength--;
    const separatorSpace = 2;
    const dataLen = new Array(outputLength);
    for (; i < outputLength; i++) {
      const len = output[i].length;
      dataLen[i] = len;
      totalLength += len + separatorSpace;
      if (maxLength < len) maxLength = len;
    }
    const actualMax = maxLength + separatorSpace;
    if (actualMax * 3 + ctx.indentationLvl < ctx.breakLength &&
        (totalLength / actualMax > 5 || maxLength <= 6)) {
      const approxCharHeights = 2.5;
      const averageBias = _Math.sqrt(actualMax - totalLength / output.length);
      const biasedMax = _Math.max(actualMax - 3 - averageBias, 1);
      const columns = _Math.min(
        _Math.round(_Math.sqrt(approxCharHeights * biasedMax * outputLength) / biasedMax),
        _Math.floor((ctx.breakLength - ctx.indentationLvl) / actualMax),
        ctx.compact * 4,
        15,
      );
      if (columns <= 1) return output;
      const tmp = [];
      const maxLineLength = [];
      for (let c = 0; c < columns; c++) {
        let lineLength = 0;
        for (let j = c; j < output.length; j += columns) {
          if (dataLen[j] > lineLength) lineLength = dataLen[j];
        }
        push(maxLineLength, lineLength + separatorSpace);
      }
      let padStart = true;
      if (value !== undefined) {
        for (let k = 0; k < output.length; k++) {
          if (typeof value[k] !== 'number' && typeof value[k] !== 'bigint') {
            padStart = false;
            break;
          }
        }
      }
      for (let k = 0; k < outputLength; k += columns) {
        const max = _Math.min(k + columns, outputLength);
        let str = '';
        let j = k;
        for (; j < max - 1; j++) {
          const padding = maxLineLength[j - k];
          str += padStart ? strPadStart(output[j] + ', ', padding, ' ') : strPadEnd(output[j] + ', ', padding, ' ');
        }
        if (padStart) {
          const padding = maxLineLength[j - k] - separatorSpace;
          str += strPadStart(output[j], padding, ' ');
        } else {
          str += output[j];
        }
        push(tmp, str);
      }
      if (ctx.maxArrayLength < output.length) push(tmp, output[outputLength]);
      output = tmp;
    }
    return output;
  }

  function isBelowBreakLength(ctx, output, start, base) {
    let totalLength = output.length + start;
    if (totalLength + output.length > ctx.breakLength) return false;
    for (let i = 0; i < output.length; i++) {
      totalLength += output[i].length;
      if (totalLength > ctx.breakLength) return false;
    }
    return base === '' || !strIncludes(base, '\n');
  }

  function reduceToSingleString(ctx, output, base, braces, extrasType, recurseTimes, value) {
    const entries = output.length;
    if (extrasType === kArrayExtrasType && entries > 6) output = groupArrayElements(ctx, output, value);
    if (ctx.currentDepth - recurseTimes < ctx.compact && entries === output.length) {
      const start = output.length + ctx.indentationLvl + braces[0].length + base.length + 10;
      if (isBelowBreakLength(ctx, output, start, base)) {
        const joined = joinList(output, ', ');
        if (!strIncludes(joined, '\n')) {
          return (base ? base + ' ' : '') + braces[0] + ' ' + joined + ' ' + braces[1];
        }
      }
    }
    const indentation = '\n' + strRepeat(' ', ctx.indentationLvl);
    return (base ? base + ' ' : '') + braces[0] + indentation + '  ' +
      joinList(output, ',' + indentation + '  ') + indentation + braces[1];
  }

  // ------------------------------------------------------------------ console.log-Formatierung
  function hasBuiltInToString(value) {
    // Eigenes toString (z. B. in einer Klasse) → %s nutzt es; sonst inspect.
    let o = value;
    while (o !== null && o !== undefined) {
      const d = tryGet(() => getOwnPropertyDescriptor(o, 'toString'), undefined);
      if (d !== undefined) {
        const proto = getPrototypeOf(o);
        return o === Object.prototype || o === Array.prototype || o === Error.prototype ||
          o === Date.prototype || o === RegExp.prototype || o === Function.prototype || proto === null;
      }
      o = getPrototypeOf(o);
    }
    return true;
  }

  function formatArgs(args) {
    const first = args[0];
    let a = 0;
    let str = '';
    let join = '';
    if (typeof first === 'string') {
      if (args.length === 1) return first;
      let tempStr;
      let lastPos = 0;
      for (let i = 0; i < first.length - 1; i++) {
        if (strCharCodeAt(first, i) === 37) {
          const nextChar = strCharCodeAt(first, ++i);
          if (a + 1 !== args.length) {
            switch (nextChar) {
              case 115: { // %s
                const arg = args[++a];
                if (typeof arg === 'number') tempStr = formatNumber(arg);
                else if (typeof arg === 'bigint') tempStr = _String(arg) + 'n';
                else if (typeof arg !== 'object' || arg === null || !hasBuiltInToString(arg)) tempStr = _String(arg);
                else tempStr = inspect(arg, { depth: 0 });
                break;
              }
              case 106: // %j
                tempStr = tryGet(() => _JSON.stringify(args[++a]), '[Circular]');
                break;
              case 100: { // %d
                const n = args[++a];
                if (typeof n === 'bigint') tempStr = _String(n) + 'n';
                else if (typeof n === 'symbol') tempStr = 'NaN';
                else tempStr = formatNumber(_Number(n));
                break;
              }
              case 79: // %O
                tempStr = inspect(args[++a]);
                break;
              case 111: // %o
                tempStr = inspect(args[++a], { depth: 4 });
                break;
              case 105: { // %i
                const n = args[++a];
                if (typeof n === 'bigint') tempStr = _String(n) + 'n';
                else if (typeof n === 'symbol') tempStr = 'NaN';
                else tempStr = formatNumber(parseIntFn(n));
                break;
              }
              case 102: { // %f
                const n = args[++a];
                tempStr = typeof n === 'symbol' ? 'NaN' : formatNumber(parseFloatFn(n));
                break;
              }
              case 99: // %c (CSS) – wird ignoriert
                a += 1;
                tempStr = '';
                break;
              case 37: // %%
                str += strSlice(first, lastPos, i);
                lastPos = i + 1;
                continue;
              default:
                continue;
            }
            if (lastPos !== i - 1) str += strSlice(first, lastPos, i - 1);
            str += tempStr;
            lastPos = i + 1;
          } else if (nextChar === 37) {
            str += strSlice(first, lastPos, i);
            lastPos = i + 1;
          }
        }
      }
      if (lastPos !== 0) {
        a++;
        join = ' ';
        if (lastPos < first.length) str += strSlice(first, lastPos);
      }
    }
    while (a < args.length) {
      const value = args[a];
      str += join;
      str += typeof value !== 'string' ? inspect(value) : value;
      join = ' ';
      a++;
    }
    return str;
  }

  function print(kind, args) {
    if (S.aborted) throw ABORT;
    let text = formatArgs(args);
    if (S.indent) text = S.indent + strReplace(text, /\n/g, '\n' + S.indent);
    emit(kind, text + '\n');
  }

  function formatMs(ms) {
    if (ms >= 60000) {
      const minutes = _Math.floor(ms / 60000);
      const seconds = numToFixed((ms % 60000) / 1000, 3);
      return minutes + ':' + strPadStart(seconds, 6, '0') + ' (m:ss.mmm)';
    }
    if (ms >= 1000) return numToFixed(ms / 1000, 3) + 's';
    return _Number(numToFixed(ms, 3)) + 'ms';
  }

  // console.table – Tabelle mit Rahmen wie in Node.js
  function renderTable(head, rows) {
    const widths = [];
    for (let c = 0; c < head.length; c++) {
      let w = head[c].length;
      for (let r = 0; r < rows.length; r++) if ((rows[r][c] || '').length > w) w = (rows[r][c] || '').length;
      push(widths, w + 2);
    }
    const line = (l, m, r) => {
      let s = l;
      for (let c = 0; c < widths.length; c++) s += strRepeat('─', widths[c]) + (c === widths.length - 1 ? r : m);
      return s;
    };
    const row = (cells) => {
      let s = '│';
      for (let c = 0; c < widths.length; c++) s += ' ' + strPadEnd(cells[c] || '', widths[c] - 1, ' ') + '│';
      return s;
    };
    let out = line('┌', '┬', '┐') + '\n' + row(head) + '\n' + line('├', '┼', '┤') + '\n';
    for (let r = 0; r < rows.length; r++) out += row(rows[r]) + '\n';
    return out + line('└', '┴', '┘');
  }

  function table(data, properties) {
    if (data === null || typeof data !== 'object') return print(0, [data]);
    const cell = (v) => inspect(v, { depth: 1 });
    const rowKeys = [];
    const rowValues = [];
    if (isMap(data)) {
      const it = mapEntries(data);
      for (let r = mapIterNext(it); !r.done; r = mapIterNext(it)) { push(rowKeys, cell(r.value[0])); push(rowValues, r.value[1]); }
    } else if (isSet(data)) {
      const it = setValues(data);
      let i = 0;
      for (let r = setIterNext(it); !r.done; r = setIterNext(it)) { push(rowKeys, _String(i++)); push(rowValues, r.value); }
    } else {
      const keys = ObjectKeys(data);
      for (let i = 0; i < keys.length; i++) { push(rowKeys, keys[i]); push(rowValues, data[keys[i]]); }
    }
    const columns = [];
    let hasValues = false;
    for (let i = 0; i < rowValues.length; i++) {
      const v = rowValues[i];
      if (v !== null && typeof v === 'object') {
        const ks = ObjectKeys(v);
        for (let k = 0; k < ks.length; k++) if (!contains(columns, ks[k])) push(columns, ks[k]);
      } else {
        hasValues = true;
      }
    }
    let cols = columns;
    if (isArray(properties)) cols = properties.map(_String);
    const head = ['(index)'];
    for (let c = 0; c < cols.length; c++) push(head, cols[c]);
    if (hasValues) push(head, 'Values');
    const rows = [];
    for (let i = 0; i < rowValues.length; i++) {
      const v = rowValues[i];
      const r = [rowKeys[i]];
      for (let c = 0; c < cols.length; c++) {
        r[r.length] = v !== null && typeof v === 'object' && hasOwn(v, cols[c]) ? cell(v[cols[c]]) : '';
      }
      if (hasValues) r[r.length] = v !== null && typeof v === 'object' ? '' : cell(v);
      push(rows, r);
    }
    print(0, [renderTable(head, rows)]);
  }

  const counts = new _Map();
  const timeLabels = new _Map();
  const consoleObj = {
    log: (...args) => print(0, args),
    info: (...args) => print(0, args),
    debug: (...args) => print(0, args),
    error: (...args) => print(1, args),
    warn: (...args) => print(1, args),
    trace: (...args) => print(1, ['Trace' + (args.length ? ':' : '')].concat(args)),
    dir: (value) => print(0, [inspect(value)]),
    table,
    assert: (condition, ...args) => {
      if (!condition) {
        if (args.length && typeof args[0] === 'string') args[0] = 'Assertion failed: ' + args[0];
        else args.unshift('Assertion failed');
        print(1, args);
      }
    },
    count: (label = 'default') => {
      label = _String(label);
      const n = (counts.get(label) || 0) + 1;
      counts.set(label, n);
      print(0, [label + ': ' + n]);
    },
    countReset: (label = 'default') => { counts.set(_String(label), 0); },
    group: (...args) => { if (args.length) print(0, args); S.indent += '  '; },
    groupCollapsed: (...args) => { if (args.length) print(0, args); S.indent += '  '; },
    groupEnd: () => { S.indent = strSlice(S.indent, 2); },
    time: (label = 'default') => { timeLabels.set(_String(label), clock()); },
    timeLog: (label = 'default', ...args) => {
      const t = timeLabels.get(_String(label));
      if (t === undefined) print(1, ["Warning: No such label '" + label + "' for console.timeLog()"]);
      else print(0, [label + ': ' + formatMs(clock() - t)].concat(args));
    },
    timeEnd: (label = 'default') => {
      const t = timeLabels.get(_String(label));
      if (t === undefined) print(1, ["Warning: No such label '" + label + "' for console.timeEnd()"]);
      else { print(0, [label + ': ' + formatMs(clock() - t)]); timeLabels.delete(_String(label)); }
    },
    clear: () => {},
  };

  function define(name, value) {
    defineProperty(G, name, { value, writable: true, configurable: true, enumerable: false });
  }

  define('console', consoleObj);

  // ------------------------------------------------------------------ Eingabe
  function promptImpl(message) {
    if (S.aborted) throw ABORT;
    let msg = message === undefined ? '' : _String(message);
    if (msg !== '' && !/\s$/.test(msg)) msg += ' ';
    flush();
    if (interactive) {
      if (msg) emit(0, msg);
      flush();
      const t0 = now();
      const value = host.readLine(msg);
      S.waitMs += now() - t0;
      if (value === null || value === undefined) stop('cancel');
      return _String(value);
    }
    if (stdinLines.length === 0) {
      if (msg) echo(msg + '\n');
      return null;
    }
    const line = _String(stdinLines.shift());
    echo(msg + line + '\n');
    return line;
  }
  define('prompt', function prompt(message) { return promptImpl(message); });
  define('alert', function alert(message) {
    print(0, [message === undefined ? '' : _String(message)]);
  });
  define('confirm', function confirm(message) {
    const answer = promptImpl(message === undefined ? '' : _String(message) + (de ? ' (j/n)' : ' (y/n)'));
    if (answer === null) return false;
    const a = answer.trim().toLowerCase();
    return a === 'j' || a === 'ja' || a === 'y' || a === 'yes' || a === 'true' || a === 'ok';
  });

  // ------------------------------------------------------------------ Timer & Zeit
  const timerList = [];
  let timerSeq = 0;
  let orderSeq = 0;

  function addTimer(fn, delay, args, repeat) {
    if (S.aborted) throw ABORT;
    if (typeof fn !== 'function') {
      throw new _TypeError('The "callback" argument must be of type function. Received ' + describeValue(fn));
    }
    delay = _Number(delay);
    if (!(delay >= 1 && delay <= 2147483647)) delay = 1;
    const t = { id: ++timerSeq, fn, args, delay, repeat, due: clock() + delay, order: ++orderSeq };
    push(timerList, t);
    return t.id;
  }
  function clearTimer(id) {
    for (let i = 0; i < timerList.length; i++) {
      if (timerList[i].id === id) { timerList.splice(i, 1); return; }
    }
  }
  define('setTimeout', function setTimeout(fn, delay, ...args) { return addTimer(fn, delay, args, false); });
  define('setInterval', function setInterval(fn, delay, ...args) { return addTimer(fn, delay, args, true); });
  define('clearTimeout', function clearTimeout(id) { clearTimer(id); });
  define('clearInterval', function clearInterval(id) { clearTimer(id); });
  define('requestAnimationFrame', function requestAnimationFrame(fn) {
    return addTimer(() => fn(clock()), 16, [], false);
  });
  define('cancelAnimationFrame', function cancelAnimationFrame(id) { clearTimer(id); });
  if (typeof G.queueMicrotask !== 'function') {
    define('queueMicrotask', function queueMicrotask(fn) { Promise.resolve().then(fn); });
  }

  // Bei virtueller Zeit (Lektionsprüfungen) läuft die Uhr mit den übersprungenen Wartezeiten mit.
  if (!realtime) {
    const RealDate = G.Date;
    const realNow = RealDate.now;
    const startWall = realNow();
    const startClock = clock();
    const wallNow = () => startWall + (clock() - startClock);
    const VDate = function Date(...args) {
      if (!new.target) return dateToString(new RealDate(wallNow()));
      return args.length === 0 ? reflectApply(Reflect.construct, null, [RealDate, [wallNow()], new.target])
        : reflectApply(Reflect.construct, null, [RealDate, args, new.target]);
    };
    VDate.prototype = RealDate.prototype;
    defineProperty(RealDate.prototype, 'constructor', { value: VDate, writable: true, configurable: true, enumerable: false });
    VDate.now = function now() { return _Math.floor(wallNow()); };
    VDate.parse = RealDate.parse;
    VDate.UTC = RealDate.UTC;
    define('Date', VDate);
    if (G.performance && typeof G.performance.now === 'function') {
      const perf = G.performance;
      const perfStart = perf.now();
      const startC = clock();
      try {
        defineProperty(perf, 'now', { value: function now() { return perfStart + (clock() - startC); }, configurable: true, writable: true });
      } catch (e) { /* egal */ }
    }
  }

  // ------------------------------------------------------------------ Node-Kompatibilität
  define('process', {
    argv: ['node', 'main.js'],
    env: {},
    platform: 'hissbit',
    exitCode: undefined,
    stdout: { write: (s) => { emit(0, _String(s)); return true; } },
    stderr: { write: (s) => { emit(1, _String(s)); return true; } },
    exit: (code) => { S.exitCode = code === undefined ? 0 : _Number(code); stop('exit'); },
    nextTick: (fn, ...args) => { Promise.resolve().then(() => fn(...args)); },
  });
  define('require', function require(name) {
    throw new Error(de
      ? "require('" + name + "') gibt es hier nicht – Hissbit führt reines JavaScript aus, ohne Node-Module."
      : "require('" + name + "') isn't available here – Hissbit runs plain JavaScript without Node modules.");
  });
  define('fetch', function fetch() {
    return Promise.reject(new _TypeError(de ? 'fetch() ist hier nicht verfügbar – es gibt kein Internet im Spielplatz.'
      : "fetch() isn't available here – the playground has no internet access."));
  });

  // ------------------------------------------------------------------ Schleifen-Wächter
  let guardCount = 0;
  define('__hb_g', function () {
    if (S.aborted) throw ABORT;
    if ((++guardCount & 1023) === 0) checkLimits();
    return true;
  });

  function describeValue(v, text) {
    if (typeof text === 'string' && text !== '') return text;
    if (v === null) return 'null';
    if (v === undefined) return 'undefined';
    if (typeof v === 'object') return 'object';
    if (typeof v === 'function') return 'function';
    return inspect(v);
  }

  define('__hb_gi', function* (iterable, text) {
    if (iterable === null || iterable === undefined || typeof iterable[Symbol.iterator] !== 'function') {
      throw new _TypeError(describeValue(iterable, text) + ' is not iterable');
    }
    for (const v of iterable) {
      if (S.aborted) throw ABORT;
      if ((++guardCount & 1023) === 0) checkLimits();
      yield v;
    }
  });

  // ------------------------------------------------------------------ Fehler & Ereignisschleife
  function recordError(e, phase, line, col) {
    if (e === ABORT || S.error || S.aborted) return;
    let info;
    if (e !== null && (typeof e === 'object' || typeof e === 'function') && isError(e)) {
      info = {
        kind: 'error',
        name: _String(tryGet(() => e.name, 'Error')),
        message: _String(tryGet(() => e.message, '')),
        stack: _String(tryGet(() => e.stack, '') || ''),
      };
    } else {
      info = { kind: 'value', text: tryGet(() => inspect(e), _String(e)) };
    }
    info.phase = phase;
    if (line !== undefined) info.line = line;
    if (col !== undefined) info.col = col;
    S.error = info;
  }

  function nextTimer() {
    let best = null;
    let bi = -1;
    for (let i = 0; i < timerList.length; i++) {
      const t = timerList[i];
      if (best === null || t.due < best.due || (t.due === best.due && t.order < best.order)) {
        best = t;
        bi = i;
      }
    }
    return best === null ? null : { t: best, i: bi };
  }

  function step() {
    if (S.finished) return;
    if (S.aborted || S.error) return finish();
    const next = nextTimer();
    if (next === null) return finish();
    const t = next.t;
    try {
      const wait = t.due - clock();
      if (wait > 0) {
        if (realtime) {
          flush();
          let sleepFor = wait;
          let hitsLimit = false;
          if (timeoutMs > 0) {
            const remain = timeoutMs - busyMs();
            if (remain < sleepFor) { sleepFor = _Math.max(0, remain); hitsLimit = true; }
          }
          if (sleepFor > 0 && host.sleep(_Math.ceil(sleepFor))) stop('cancel');
          if (hitsLimit) stop('timeout');
        } else {
          vSkip += wait;
          if (clock() > VIRTUAL_LIMIT_MS) stop('timeout');
        }
      }
      checkLimits();
    } catch (e) {
      return finish();
    }
    if (t.repeat) {
      t.due = clock() + t.delay;
      t.order = ++orderSeq;
    } else {
      timerList.splice(next.i, 1);
    }
    try {
      reflectApply(t.fn, undefined, t.args);
    } catch (e) {
      if (e !== ABORT) recordError(e, 'timer');
      return finish();
    }
    host.defer(step);
  }

  let finishHops = 0;
  function finish() {
    if (S.finished) return;
    // Meldungen über unbehandelte Promise-Fehler kommen als eigene Aufgabe – kurz abwarten.
    if (finishHops < 2 && !S.aborted && !S.error) {
      finishHops++;
      host.defer(finish);
      return;
    }
    S.finished = true;
    try { flush(); } catch (e) { /* egal */ }
    const result = {
      stdout: S.stdout,
      stderr: S.stderr,
      aborted: S.aborted,
      exitCode: S.exitCode,
      error: S.error,
      ms: _Math.max(0, _Math.round(busyMs())),
    };
    host.done(_JSON.stringify(result));
  }

  host.attach({
    /** Fehler im Hauptprogramm (auch Syntaxfehler) – line/col nur, wenn kein Stack existiert. */
    mainError(e, line, col) {
      if (e === ABORT) return;
      recordError(e, 'main', line, col);
    },
    /** Unbehandelte Ablehnung eines Promise. */
    unhandled(reason) {
      if (reason === ABORT) return;
      recordError(reason, 'promise');
    },
    /** Fehler in sonstigen Rückrufen. */
    asyncError(e) {
      if (e === ABORT) return;
      recordError(e, 'async');
    },
    isAbort(e) { return e === ABORT; },
    afterMain() {
      try { flush(); } catch (e) { /* egal */ }
      host.defer(step);
    },
  });
})();
