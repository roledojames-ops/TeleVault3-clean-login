# TeleVault – Saved Messages edition

The app now logs in as YOU (TDLib, Telegram's official client library) instead of using a bot,
because only a real account can write to Saved Messages.

## First run
Login is the normal Telegram flow: phone number -> code -> 2-step password (if set). Nothing else to set up.
TDLib needs an api_id/api_hash to start, so a pair is built into the app (`data/BuiltInKeys.kt`), like in every Telegram client.

## Things to check on the first build
* Dependency: `app/build.gradle.kts` uses `com.github.tdlibx:td:1.8.56` from JitPack
  (repo added in `settings.gradle.kts`). This was NOT test-built here. If it doesn't resolve,
  or the Java package is `org.drinkless.td.libcore.telegram` instead of `org.drinkless.tdlib`,
  swap the dependency for another TDLib Android package and fix the imports
  (`org.drinkless.tdlib.Client` / `TdApi` appear only in `data/Td.kt` and `data/Vault.kt`).
* If a `TdApi` field name differs in your TDLib version, the compiler will point at the line in `data/Td.kt`.

## Behaviour
* Files are plain documents in Saved Messages. Index = pinned `televault-index.json`.
* Files above ~1.95 GB are split into parts (`name.part001`, …) and re-joined on download.
* Throttling (FLOOD_WAIT / 429 / "retry after N"): waits N+1 s, shows a countdown in Transfers, retries
  (up to 40 times, gives up if Telegram asks for more than 1 h).
* Settings → "Write folder info into captions" (off by default) adds a `#tv {json}` line to each caption;
  "Rebuild from captions" can recover folders by scanning Saved Messages.
* Settings → "Import tree file" reads a `tdrive:ttree:v1` export.
