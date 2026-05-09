# Section 4 — Miscellaneous Maintenance Items

## Goal

Resolve a set of small, mostly-independent maintenance items that fall outside the dependency-bump and lint-cleanup work covered in Sections 1–3: Gradle wrapper refresh, Renovate config modernization, optional release version bump, lint baseline regeneration, type-safe project accessor cleanup, README/troubleshooting hygiene, and a `local.properties` gitignore confirmation.

---

## 1. Gradle wrapper upgrade

- **Problem:** `gradle-wrapper.properties` pins Gradle 8.13 (Mar 2024 era for the file timestamp). Latest stable 8.x is **8.14.5** (May 2026). AGP 8.13 (`gradle/libs.versions.toml:2`) requires minimum Gradle 8.13 and is forward-compatible within the 8.x line; Gradle 9.x is **not** an option without an AGP 9.x bump (out of scope here).
- **Files to change:**
  - `/Users/simonas/Projects/quies/gradle/wrapper/gradle-wrapper.properties:4`
- **Concrete change:** Update `distributionUrl` to `https\://services.gradle.org/distributions/gradle-8.14.5-bin.zip`. Run `./gradlew wrapper --gradle-version 8.14.5 --distribution-type bin` so the wrapper jar / scripts get regenerated consistently. Re-run once (`./gradlew wrapper`) to lock the version.
- **Verification:** `./gradlew --version` reports 8.14.5; `./gradlew :app:assembleDebug` and `./gradlew :app:lintDebug` succeed.

---

## 2. Renovate config modernization

- **Problem:** `renovate.json` extends `config:base`, which Renovate has deprecated in favor of `config:recommended`. The preset `:automergeMinor` still works but warrants explicit re-confirmation since it was inherited from the `kotlin-android-template` upstream and auto-merging minors on a small Android codebase has nontrivial risk (e.g., AGP/Kotlin minors).
- **Files to change:**
  - `/Users/simonas/Projects/quies/renovate.json` (entire file, 6 lines)
- **Concrete change:** Replace `"config:base"` with `"config:recommended"`. Decide whether to keep `:automergeMinor` (see Open Questions). If kept, consider scoping it away from `agp`, `kotlin`, and `gradle` packages via a `packageRules` block.
- **Verification:** Push a branch and open a Renovate PR or run the Renovate dry-run / config-validator (`npx --package renovate -c renovate-config-validator`). Confirm no config-deprecation warnings appear in the next Renovate dashboard issue.

---

## 3. App version bump (decision-gated)

- **Problem:** `Coordinates.kt` shows `APP_VERSION_NAME = "1.0.1"`, `APP_VERSION_CODE = 33`. If this maintenance pass is shipped to Play (internal track at minimum, given `play { track.set("internal") }` in `app/build.gradle.kts:182`), the version must bump. Not all maintenance passes ship.
- **Files to change:**
  - `/Users/simonas/Projects/quies/buildSrc/src/main/java/Coordinates.kt:5-6`
- **Concrete change:** Bump to `APP_VERSION_NAME = "1.0.2"` and `APP_VERSION_CODE = 34`. Skip if not shipping.
- **Verification:** `./gradlew :app:assembleRelease` produces an AAB whose `versionCode` matches; confirm via `aapt dump badging` or Play console upload preflight.

---

## 4. Lint baseline regeneration (depends on Sections 1 & 2)

- **Problem:** `app/build.gradle.kts:98` references `app/lint-baseline.xml`. After the dependency bumps in Sections 1–2 land, new lint findings introduced by upgraded androidx/Compose/Hilt/etc. will be silently absorbed by the old baseline rather than surfacing for review. Regenerating ensures `warningsAsErrors = true` (line 96) actually catches new issues.
- **Files to change:**
  - `/Users/simonas/Projects/quies/app/lint-baseline.xml` (delete, then regenerate)
- **Concrete change:**
  1. Delete `app/lint-baseline.xml`.
  2. Run `./gradlew :app:updateLintBaseline` (Gradle 8.x AGP task). If unavailable, temporarily comment out `baseline = ...` and run `./gradlew :app:lintDebug -Dlint.baselines.continue=true` to write a new baseline at `app/build/reports/lint-results-debug-baseline.xml`, then move it to `app/lint-baseline.xml`.
  3. Diff the new baseline against the old one. Triage any *newly-quieted* findings — fix in code rather than baselining when feasible.
  4. Commit the regenerated baseline.
- **Verification:** `./gradlew :app:lintRelease` passes with `warningsAsErrors = true`. New baseline diff committed alongside dep bumps.
- **Sequencing note:** Must run **after** Sections 1 (deps) and 2 (lint cleanup) merge, otherwise the baseline captures findings that those sections will eliminate.

---

## 5. Type-safe project accessors

- **Problem:** `settings.gradle.kts:18` enables `TYPESAFE_PROJECT_ACCESSORS`, but `app/build.gradle.kts:131` still uses the stringy form `implementation(project(":data"))`. Either use the preview or drop the flag.
- **Files to change:**
  - `/Users/simonas/Projects/quies/app/build.gradle.kts:131`
  - (or alternatively) `/Users/simonas/Projects/quies/settings.gradle.kts:18`
- **Concrete change:** **Preferred:** replace `implementation(project(":data"))` with `implementation(projects.data)`. Sweep other `build.gradle.kts` modules (`data/`, `buildSrc/`) for similar string accessors. **Alternative:** delete the `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` line if the team prefers stable-only features.
- **Verification:** `./gradlew :app:dependencies | grep -i ':data'` resolves the module dependency identically; `./gradlew :app:assembleDebug` succeeds.

---

## 6. README expansion (optional)

- **Problem:** `README.md` is 18 lines: a logo, two epigraphs, and a single acknowledgement line pointing at the upstream template. There's no build/setup, no description of what the app does, no badge/CI block. Acceptable for a personal project; suboptimal for onboarding or open-source visibility.
- **Files to change:**
  - `/Users/simonas/Projects/quies/README.md`
- **Concrete change (if pursued):** Add sections — *What it does* (1 paragraph), *Build & run* (`./gradlew :app:installDebug`, JDK 21, Android SDK requirements per `compileSdk`), *Module layout* (`app/`, `data/`, `buildSrc/`), *CI/CD* (link to `.github/workflows`), preserve the epigraphs and template acknowledgement.
- **Verification:** `markdownlint README.md` clean (if used); manual readability review.
- **Optional flag:** Skip if the user prefers the current minimalist tone.

---

## 7. `local.properties` gitignore confirmation

- **Problem:** `local.properties` exists at repo root and would leak the local SDK path / arbitrary developer overrides if tracked.
- **Files to change:** None expected — confirmation only.
- **Concrete change:** Confirmed gitignored. `.gitignore:38` lists `local.properties`, and `.gitignore:254` lists `/*/local.properties` for module-level files. The file is **not** tracked by git (it appears in `ls -la` only because gitignore doesn't delete files; it just excludes them from indexing).
- **Verification:** Run `git ls-files | grep local.properties` — should return empty. Run `git check-ignore -v local.properties` — should print the matching `.gitignore` rule. No action needed unless either check fails.

---

## 8. `TROUBLESHOOTING.md` review

- **Problem:** The doc has two sections, both stale:
  1. *"agp incompatibility with IntelliJ IDEA"* references AGP 8.0.0 vs IDEA's max-supported 7.4.0 — irrelevant in 2026 with AGP 8.13.
  2. *"Use correct JVM version"* shows `jvmTarget = JavaVersion.VERSION_17` and a sample error listing JVM targets up to 18 — but `app/build.gradle.kts:38-45` now uses **JDK 21**.
- **Files to change:**
  - `/Users/simonas/Projects/quies/TROUBLESHOOTING.md`
- **Concrete change:** Either (a) delete the file entirely (preferred — both items are obsolete and the IDE-version pain is a non-issue at this AGP/IDE pairing), or (b) rewrite: drop section 1 outright; in section 2 update the example to `VERSION_21` and update the error message snippet to a current-era one (or just say "your local JDK must be ≥ 21; check `java --version`").
- **Verification:** Manual read; ensure no other doc references `TROUBLESHOOTING.md` (grep the repo).

---

## Sequencing

- **Item 4** (lint baseline regen) **must run after Sections 1 and 2** are merged so the regenerated baseline reflects the post-bump, post-cleanup state.
- **Items 1, 2, 3, 5, 6, 7, 8** are mutually independent and can ship in any order or as a single PR.
- Suggested grouping for one PR: 1 + 2 + 5 + 8 (all small mechanical edits). Items 3, 6 are decision-gated (see below). Item 7 needs no change. Item 4 lands in a follow-up.

---

## Open questions for the user

1. **Item 3 (release):** Is this maintenance pass shipping to Play? If yes, bump to `1.0.2` / `34`. If no, leave as-is.
2. **Item 2 (Renovate automerge):** Keep `:automergeMinor` for all packages, restrict it (exclude `agp`, `kotlin`, `gradle`, `compose-*`), or drop it? Recommendation: restrict — minor bumps to AGP/Kotlin can break Compose compiler compatibility.
3. **Item 5 (preview flag):** Adopt `projects.data` everywhere, or drop the preview flag? Recommendation: adopt; the feature is widely used and unlikely to be removed.
4. **Item 6 (README):** Expand with build/setup section, or keep the minimalist poetic tone?
5. **Item 8 (TROUBLESHOOTING):** Delete entirely, or keep a slimmed-down version?

---

## Out of scope

- Section 1 — dependency bumps in `gradle/libs.versions.toml`.
- Section 2 — build-config cleanup (Compose BOM, Jetifier, dead `composeOptions`, `kotlinOptions` migration).
- Section 3 — GitHub Actions / CI workflow upgrades.
- AGP 9.x / Gradle 9.x migration (would require coordinated AGP, JDK, and possibly Kotlin upgrades; not a maintenance task).
