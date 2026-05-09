# Section 1 — Stale Dependencies

## 1. Goal

Bring the version catalog at `gradle/libs.versions.toml` in line with current stable releases of AndroidX, Compose-adjacent, Kotlin coroutines, JUnit/test, detekt, and SDK libraries. Out-of-date pins predate the project's Kotlin 2.2.20 / AGP 8.13 / JDK 21 baseline and are blocking idiomatic APIs (typed Navigation, `collectAsStateWithLifecycle`, predictive back, Material3 1.4 components). Each batch is shippable independently and verifiable via existing gradle tasks. No Compose BOM rework, no CI/Gradle wrapper changes — those land in later sections.

## 2. Inventory

| Catalog key | Current pin | Latest stable (May 2026) | Risk / notes |
|---|---|---|---|
| `androidx_activity_compose` | 1.8.0 | **1.13.0** | Source-breaking: `ActivityResultLauncher.getContract` is now an abstract Kotlin property. `enableEdgeToEdge()` re-invoked on config changes (1.13). New `PredictiveBackHandler` composable. Pulls in `core:core-pip`. |
| `appcompat` | 1.6.1 | **1.7.1** | Patch-only risk. 1.7.0 required Activity 1.8 (already satisfied post-bump). 1.7.1 fixes `AppCompatActivity` × NavigationEvent interaction. |
| `core_ktx` | 1.10.1 | **1.18.0** | Multi-major jump. From 1.19.0-alpha onward `core-ktx` becomes an empty alias for `core`; staying on stable 1.18 sidesteps that. Watch for tightened nullability in `Bundle`/`Intent` extensions. |
| `compose-lifecycle` (lifecycle-runtime-compose) | 2.6.2 | **2.10.0** | minSdk floor moves to **API 23** (project is 28 — fine). 2.7+ promoted `collectAsStateWithLifecycle` to stable; 2.8 added KMP source set; 2.10 adds `rememberLifecycleOwner`. ViewModel `KClass` overloads added. |
| `compose-navigation` | 2.7.5 | **2.9.8** | Major: 2.8 introduced type-safe `@Serializable` routes (additive — string routes still work). 2.8.1 closed an implicit deeplink security hole — verify any deeplinks declare `<deepLink>` explicitly. 2.8.x **requires Compose runtime ≥ 1.7.2** (handled by Section 2's BOM). |
| `compose_material3` | 1.1.2 | **1.4.0** | Largest visual-risk bump. 1.2 typography line-height adjustments, 1.3 added new components, 1.4 has `MaterialTheme.LocalMaterialTheme.current` access path; checkbox sizing/colors realigned to spec (flag `isCheckboxStylingFixEnabled` available). Removed deprecated `DropdownMenuItem` overload, deprecated `FilterChip`/`AssistChip` `horizontalSpacing`. UI smoke required. |
| `coroutines` | 1.7.3 | **1.10.2** *(verify)* | 1.8 changed `Dispatchers.Main` initialisation (immediate-by-default) and removed several deprecated APIs; `runBlockingTest` deprecated path removed; new `kotlinx-coroutines-test` `runTest` semantics (timeout default 60s). 1.9+ requires Kotlin 2.0+. Inspect tests using `runTest` / `TestCoroutineScheduler`. |
| `detekt` | 1.23.1 | **1.23.8** *(verify)* | 1.23.x is the current stable stream; 2.x is in beta. Stay on 1.23.8 for Kotlin 2.2 compiler compat (1.23.1 predates Kotlin 2.0). **`detekt-formatting` plugin must move in lockstep** — already shares `detekt` version.ref. |
| `androidx_test` | 1.5.0 | **1.7.0** | minSdk floor 21 (fine). Internal Kotlin upgrade to 1.9. |
| `androidx_test_ext` | 1.1.5 | **1.3.0** | Aligns with `androidx_test` 1.7.0. |
| `espresso_core` | 3.5.1 | **3.7.0** | Robolectric+Espresso deadlock fix. Move with the test bundle. |
| `junit-jupiter` | 5.8.1 | **5.11.x** *(verify)* | 5.10 dropped the legacy vintage runner default; AGP 8.13 supports JUnit5 platform natively. Confirm test discovery still works (`testDebugUnitTest`). Currently only consumed in `app` test source set. |
| `mixpanel-android` | 7.5.2 | **8.x** *(verify)* | Major bump. 8.x changed init API (`MixpanelAPI.getInstance(...)` signature added trackAutomaticEvents bool), and dropped some legacy endpoints. Audit every callsite before bumping. |
| `org-jetbrains-kotlin-jvm` (version) | 1.8.0 | **DELETE** | Confirmed dead: no module applies `org.jetbrains.kotlin.jvm`. Remove both the `[versions]` entry (line 46) and the `[plugins]` alias (line 107). Leftover from kotlin-android-template. |
| `kotlinx-serialization` plugin | hardcoded 1.9.0 | track `kotlin` version | Used by `data/build.gradle.kts`. Plugin coordinate `org.jetbrains.kotlin.plugin.serialization` ships with the Kotlin compiler — must equal Kotlin version. Change line 109 to `version.ref = "kotlin"`. |

Cross-reference (NOT in scope): `compose_foundation`, `compose_ui`, `compose_ui_tooling`, `compose_ui_test_junit4`, `compose_ui_test_manifest` all incorrectly point at `version.ref = "compose_compiler"` (1.5.4). Section 2 replaces these with the Compose BOM.

## 3. Sequencing

Six batches, intended to land as separate PRs. Each is independently revertible.

**Batch A — Catalog hygiene (zero runtime change).**
- Delete `org-jetbrains-kotlin-jvm` version + plugin entries.
- Repoint `kotlinx-serialization` plugin to `version.ref = "kotlin"`.
- Justification: pure cleanup, no library bytecode shifts. Land first to de-noise diffs.

**Batch B — Detekt toolchain.**
- `detekt` 1.23.1 → 1.23.8 (auto-propagates to `detekt-formatting`).
- Justification: build-tool only; no app code change. Validates the 2.2 Kotlin compiler is honoured by detekt's parser.

**Batch C — Foundational AndroidX (no Compose API surface).**
- `appcompat` 1.6.1 → 1.7.1
- `core_ktx` 1.10.1 → 1.18.0
- `androidx_activity_compose` 1.8.0 → 1.13.0
- Justification: `appcompat` 1.7+ pulls Activity ≥ 1.8 transitively; bumping all three at once avoids resolved-version surprises. None are Compose-runtime-pinned.

**Batch D — Coroutines.**
- `coroutines` 1.7.3 → latest 1.10.x stable.
- Justification: behavioural changes around `Dispatchers.Main.immediate` and `runTest` warrant isolating from UI library bumps so any test failures are unambiguously attributable.

**Batch E — Compose-adjacent (NOT Compose UI itself — that's Section 2).**
- `compose-lifecycle` 2.6.2 → 2.10.0
- `compose-navigation` 2.7.5 → 2.9.8
- `compose_material3` 1.1.2 → 1.4.0
- Justification: these three change Compose-rendered surfaces. **Compat constraint**: navigation 2.8.x requires Compose runtime ≥ 1.7.2. The current `compose_compiler = 1.5.4` references resolve to Compose UI 1.5.4 — Section 2's BOM will fix this. **Hold this batch until Section 2 lands**, OR temporarily pin Compose libraries to ≥ 1.7.2 explicitly. Material3 1.4 also wants Compose ≥ 1.7. Note this dependency in the PR description.

**Batch F — Test stack.**
- `androidx_test` 1.5.0 → 1.7.0
- `androidx_test_ext` 1.1.5 → 1.3.0
- `espresso_core` 3.5.1 → 3.7.0
- `junit-jupiter` 5.8.1 → 5.11.x
- Justification: AndroidX test artifacts share a release train and must move together. JUnit 5 lumps in here because it only affects the same source sets.

**Batch G — Mixpanel (independent vendor).**
- `mixpanel-android` 7.5.2 → 8.x.
- Justification: vendor SDK with non-trivial init API change. Land last and behind a manual verification build (analytics events visible in Mixpanel UI debug view).

Ordering rationale: A → B → C → D → F → E → G. E waits on Section 2 unless explicit Compose pins are added; G is last because runtime regressions only show in release telemetry.

## 4. Per-batch verification

Common gates for every batch:
```
./gradlew detekt lint testDebugUnitTest assembleDebug
```
Plus `./gradlew dependencies --configuration releaseRuntimeClasspath | grep -i <bumped-artifact>` to confirm resolved version matches the catalog (no transitive override surprises).

Per-batch additions:

- **A (catalog hygiene)**: `./gradlew help` to confirm the catalog still parses; `./gradlew :data:tasks` confirms serialization plugin still resolves.
- **B (detekt)**: `./gradlew detekt` on every module; spot-check that `config/detekt/detekt.yml` rule IDs all still exist (run with `--build-upon-default-config` if needed).
- **C (foundational AndroidX)**: full `./gradlew lint` (lint-baseline diffs are the canary); manual smoke: launch debug build, exercise back gesture (predictive back from Activity 1.13), edge-to-edge insets on a Pixel emulator API 33+.
- **D (coroutines)**: `./gradlew testDebugUnitTest` with `-Pkotlinx.coroutines.test.default_timeout=60s` if any test hangs; audit any `Dispatchers.Main.immediate` assumptions; grep for `runBlockingTest` (deprecated) and `TestCoroutineDispatcher` (removed).
- **E (Compose-adjacent)**: full instrumented run via Flank: `./gradlew :app:runFlank`; UI smoke on every top-level screen for typography/spacing regressions (Material3 1.2 line-height, 1.4 checkbox); navigate every nav graph entry to confirm string routes still resolve.
- **F (test stack)**: `./gradlew testDebugUnitTest connectedDebugAndroidTest`; if connected tests are skipped locally, run Flank.
- **G (mixpanel)**: build debug, exercise tracked flows, verify event arrival in Mixpanel debug/live view; confirm `MixpanelAPI.getInstance` callsite signature matches 8.x.

## 5. Risks & rollback

Migration gotchas to anticipate:

- **Material3 1.1 → 1.4**: typography line-height changes (1.2), checkbox/indicator size (1.4 — flagged via `isCheckboxStylingFixEnabled`), removed deprecated chip params. UI-test screenshot diffs likely; budget time for visual review.
- **Coroutines 1.8+**: `Dispatchers.Main` is now `Main.immediate` by default in some contexts; tests that asserted dispatcher ordering may flake. `runBlockingTest` removed — migrate to `runTest`.
- **Lifecycle 2.7+**: `collectAsStateWithLifecycle` is the canonical replacement for `collectAsState` for cold flows; current code likely still uses `collectAsState` — bump does not force a rewrite, but lint may warn.
- **Navigation 2.8.1 deeplink security fix**: any implicit deep links not declared via `<deepLink>` will stop matching. Audit `AndroidManifest.xml` and `composable(...)` deeplink params.
- **Activity 1.13**: source-breaking change to `ActivityResultLauncher.getContract` for any Kotlin code that overrides it.
- **Mixpanel 8.x**: init signature changed; missed callsite = silent telemetry loss in release builds.

Rollback per batch: each batch is a single PR/commit touching only `gradle/libs.versions.toml` (plus, for Batch G/E, possibly a few callsites). Revert is `git revert <sha>`. Renovate's automerge is scoped to minor — if Renovate races a manual major bump, lock the major bump behind a `packageRules` entry in `renovate.json` for that batch's duration (note: editing `renovate.json` is a Section 4 concern; for this section, just revert the catalog entry).

## 6. Out of scope

- Compose BOM adoption and replacement of `version.ref = "compose_compiler"` on `compose_foundation`/`compose_ui`/`compose_ui_tooling`/`compose_ui_test_*` → **Section 2**.
- GitHub Actions runner versions, action SHAs, JDK setup → **Section 3**.
- `gradle-wrapper.properties` Gradle version, `renovate.json` rules, Ben-Manes `dependencyUpdates` config → **Section 4**.
- Firebase BOM, Hilt, KSP, AGP, Kotlin compiler, `kaml`, `colormath`, `datastore`, `kotlin-math`, `truth`, `turbine`, `mockito-*`, `simple_flank`, `play_publisher`, `ktlint_gradle`, `benmanesversion`, `material` (Google MDC) — these are either current or covered elsewhere. Material (`com.google.android.material:material` 1.12.0) is current stable; do not touch.
