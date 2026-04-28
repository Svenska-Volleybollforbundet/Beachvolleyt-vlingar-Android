# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug       # Build debug APK
./gradlew assembleRelease     # Build release APK
./gradlew installDebug        # Build and install to connected device
./gradlew clean build         # Full clean rebuild
```

There are no automated tests in this project.

## Architecture

Android app (Java, minSdk 24, targetSdk 34) for browsing Swedish beach volleyball tournaments and player rankings.

**Data flow:**
```
Activities → Services → I/O Layer → Parsers → External APIs
```

**Three external data sources:**
- **Profixio** (HTML scraping via JSoup): tournament list, team registrations, match results
- **BeachLive** (JSON API): player rankings for men/women/mixed
- **Firebase Realtime DB**: court locations with real-time updates

**Package structure:**
- `activity/` — 6 Activities + `MyApplication` (service locator singleton)
- `service/` — `TournamentService`, `PlayerService`, `CourtService`
- `io/` — HTTP (`SourceCodeRequester`), caching (`TournamentListCache`, `Cache`)
- `parser/` — JSoup parsers for Profixio HTML, `PlayerListParser` for BeachLive JSON
- `domain/` — Data models (`Tournament`, `Player`, `Team`, `Match`, `Clazz`, `Region`)
- `adapters/` — RecyclerView/ListView adapters

**Service access:** `MyApplication` holds lazily initialized service singletons. Activities call `((MyApplication) getApplication()).getTournamentService()` etc.

**Caching:** Tournament list and player list are Java-serialized to disk. Cache reads fall back to a previously serialized file on network failure.

**Async pattern:** Long-running operations use `AsyncTask` (legacy). Network is called on the main thread in some places — `StrictMode.permitAll()` is set in `MyApplication`.

## Key Domain Details

`Clazz.java` is an enum of 47+ competition classes (men, women, youth, veteran, mixed) used for filtering throughout the app.

`CompetitionPeriod.java` defines Swedish season date ranges — update this each new year when the tournament list URL also needs updating (see recent commits).

The `Tournament` domain object has nested `TournamentClazz` (list of registered teams) and `Match` objects populated lazily when the user opens a tournament detail view.

`Region.java` maps Swedish regions to sets of clubs for filtering.

## Notable Constraints

- The app targets Swedish beach volleyball specifically; all UI strings are in Swedish (`res/values/strings.xml`).
- The tournament list URL in `TournamentListCache` is season-specific and must be updated annually.
- `ExpandableCardView` dependency is pinned to `0.6_beta` from jitpack (the `0.8` release could not be resolved).
- Cleartext HTTP traffic is enabled in the manifest (`android:usesCleartextTraffic="true"`).
- The Google Maps API key is stored in `gradle.properties`.
