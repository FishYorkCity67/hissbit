# Hissbit

Lern-App für **Python** und **JavaScript** auf Android – offline, auf Deutsch und Englisch.

## Was drin ist

- **Python-Kurs** mit 16 Einheiten und **JavaScript-Kurs** mit 14 Einheiten, beide auf Deutsch und Englisch
- Python-Code läuft direkt auf dem Gerät (Chaquopy), JavaScript in einer unsichtbaren WebView mit Ausgabe wie in Node.js und Schutz vor Endlosschleifen
- Lernpfad, Tages-Challenge, Shop, Notizen und Code-Editor
- Ruhige animierte Hintergründe (Kirschblüte mit Fuji, Schnee, Herbstlaub, Glühwürmchen), hell und dunkel
- Keine Internet-Berechtigung – alles bleibt auf dem Handy

Gebaut mit Kotlin und Jetpack Compose.

## Bauen

Voraussetzungen: JDK 21 und das Android SDK (`ANDROID_HOME` setzen oder `local.properties` mit `sdk.dir` anlegen).

```powershell
.\gradlew.bat ':app:assembleDebug'
```

Release für Handys mit arm64:

```powershell
.\gradlew.bat ':app:assembleRelease' '-Ppyquest.abi=arm64-v8a'
```

Unter Windows landet die Build-Ausgabe in `%LOCALAPPDATA%\PyQuestBuild`.
Liegt kein eigener Schlüssel unter `keystore/hissbit-signing.keystore`, wird mit dem normalen Debug-Schlüssel signiert.

## Tests

```powershell
.\gradlew.bat ':app:testDebugUnitTest'
```

Die Unit-Tests prüfen unter anderem, dass die Lektions-IDs gleich bleiben, damit der Lernfortschritt bei Updates erhalten bleibt.

## Lizenz

MIT – siehe [LICENSE](LICENSE).
