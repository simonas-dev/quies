# Section 2 — Build-config maintenance

## Goal

Clean up build-config drift in `gradle/libs.versions.toml`, `app/build.gradle.kts`, `data/build.gradle.kts`, `buildSrc/build.gradle.kts`, and `gradle.properties`. The work removes obsolete suppressions, dead version aliases, deprecated DSL, and incorrectly-pinned Compose library versions. Ordering favours behaviour-neutral cleanups first; the Compose BOM migration lands last because it actually changes the bytecode shipped to users.

---

## 1. Compose libraries pinned to compiler version → Compose BOM

**Problem.** `compose_foundation`, `compose_ui`, `compose_ui_tooling`, `compose_ui_test_junit4`, `compose_ui_test_manifest` all reference `version.ref = "compose_compiler"` (= `1.5.4`). The Kotlin Compose compiler plugin and Compose runtime/UI libraries are decoupled since Kotlin 2.0; the app is currently shipping Compose UI **1.5.4** (released Oct 2023) on Kotlin 2.2.20.

**Files to change.**
- `gradle/libs.versions.toml:13` (drop `compose_compiler`), `:62`, `:65`, `:66`, `:67`, `:68` (rewrite library entries).
- `app/build.gradle.kts:108–160` (dependencies block; add `platform(libs.compose.bom)` and androidTest counterpart).

**Concrete change.**

`gradle/libs.versions.toml` — add a single BOM coordinate, keep `compose_material3` versioned only if you intentionally want a different track (BOM `2026.05.00` ships material3 `1.4.0`, so prefer dropping the explicit pin):

```toml
# [versions]
compose_bom = "2026.05.00"
# remove: compose_compiler = "1.5.4"
# remove: compose_material3 = "1.1.2"   (let BOM manage it)

# [libraries]
compose_bom              = { module = "androidx.compose:compose-bom",                version.ref = "compose_bom" }
compose_foundation       = { module = "androidx.compose.foundation:foundation" }
compose_material3        = { module = "androidx.compose.material3:material3" }
compose_ui               = { module = "androidx.compose.ui:ui" }
compose_ui_tooling       = { module = "androidx.compose.ui:ui-tooling" }
compose_ui_test_junit4   = { module = "androidx.compose.ui:ui-test-junit4" }
compose_ui_test_manifest = { module = "androidx.compose.ui:ui-test-manifest" }
```

`app/build.gradle.kts` — add the BOM platform under `dependencies` next to the Firebase BOM (line 130):

```kotlin
implementation(platform(libs.compose.bom))
androidTestImplementation(platform(libs.compose.bom))
```

No other dependency lines change — the unversioned coordinates in the catalog resolve through the BOM.

**Verification.**
- `./gradlew :app:dependencies --configuration releaseRuntimeClasspath | grep "androidx.compose"` — every Compose artifact should show `1.11.1` (or whatever the BOM resolves to), not `1.5.4`.
- `./gradlew :app:assembleDebug` succeeds.
- `./gradlew :app:lintDebug` — Compose lint should not regress.
- Smoke-run the app; visually confirm onboarding screens, navigation, and material3 surfaces still render.

---

## 2. Remove dead `composeOptions { kotlinCompilerExtensionVersion = … }`

**Problem.** Once `org.jetbrains.kotlin.plugin.compose` is applied (it is — `gradle/libs.versions.toml:110`, `app/build.gradle.kts:12`), AGP ignores `composeOptions.kotlinCompilerExtensionVersion`. The Compose compiler version is governed by the Kotlin version.

**Files to change.** `app/build.gradle.kts:41–43`.

**Concrete change.**

```kotlin
// before (lines 41-43)
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}
// after
// (delete the whole block)
```

The `compose_compiler` version entry is already removed in item 1.

**Verification.**
- `./gradlew :app:assembleDebug` — build succeeds.
- `./gradlew :app:tasks` — no warnings about `kotlinCompilerExtensionVersion` being ignored.

---

## 3. Disable Jetifier

**Problem.** `android.enableJetifier=true` rewrites every transitive jar at build time to translate `android.support.*` → `androidx.*`. Every dependency in this project is AndroidX-native; Jetifier is dead weight.

**Files to change.** `gradle.properties:11`.

**Concrete change.**

```properties
# before
android.enableJetifier=true
# after
# (delete line; android.useAndroidX=true on line 9 stays)
```

**Verification.**
- Add the `com.github.plnice.canidropjetifier` plugin to root `build.gradle.kts` temporarily (or run as a one-shot init script), then `./gradlew canIDropJetifier`. Expect zero hits.
- Alternative one-liner without adding a plugin: `./gradlew :app:dependencies | grep -i "support-"` — should return nothing referring to `com.android.support:*`.
- Full clean build: `./gradlew clean :app:assembleRelease`.

---

## 4. `-Xcontext-receivers` → context parameters (or keep flag)

**Problem.** `-Xcontext-receivers` is the experimental Kotlin 1.x flag. Kotlin 2.2 promotes the feature under a new flag, `-Xcontext-parameters`, with a slightly different declaration syntax. Specifying both flags is a compile error.

**Files to change.** `app/build.gradle.kts:46`.

**Usage audit (already run).**
- `app/src/main/.../onboarding/OnboardingComponents.kt` — 8 `context(DrawScope)` declarations, including one on the public `interface CanvasComponent.draw(...)`.
- `app/src/test/.../utils/TurbineKtx.kt:9` — `context(TestScope)` on `suspend inline fun Flow<T>.testLast`.

This is a real refactor (interface signature + every call site). **Recommend two-step:**

**Step 4a (this pass — zero behavioural change).** Keep `-Xcontext-receivers` but stop putting it in `freeCompilerArgs` via the deprecated `kotlinOptions` block; move to the modern `kotlin { compilerOptions { … } }` DSL so the next refactor only edits one line.

```kotlin
// before (app/build.gradle.kts:44-47, inside android { })
kotlinOptions {
    jvmTarget = JavaVersion.VERSION_21.toString()
    freeCompilerArgs = listOf("-Xcontext-receivers")
}

// after — delete the block above; add at module top-level:
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
```

**Step 4b (separate ticket, out of scope here).** Migrate the 9 declaration sites to context parameters (`context(scope: DrawScope)`), flip the flag to `-Xcontext-parameters`. IntelliJ has an assisted migration. Touches a public interface, so do it in isolation.

**Verification.**
- `./gradlew :app:compileDebugKotlin` — no deprecation warnings about `kotlinOptions`.
- `./gradlew :app:compileReleaseKotlin --warning-mode all 2>&1 | grep -i context` — only the expected experimental-feature notice.

---

## 5. Drop dead `target_sdk_version` catalog entry

**Problem.** `app/build.gradle.kts:21` and `data/build.gradle.kts:10` (compileSdk) both read `compile.sdk.version`; nothing references `target.sdk.version`.

**Files to change.** `gradle/libs.versions.toml:43`.

**Concrete change.**

```toml
# before
target_sdk_version = "35"
# after
# (delete)
```

**Verification.**
- `grep -rn "target.sdk.version\|target_sdk_version" .` returns nothing.
- `./gradlew help` — catalog resolves cleanly.

---

## 6. Drop `@Suppress("DSL_SCOPE_VIOLATION")` and TODO

**Problem.** KTIJ-19369 was resolved years ago; the suppression and TODO are noise.

**Files to change.** `data/build.gradle.kts:1`.

**Concrete change.**

```kotlin
// before
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
// after
plugins {
```

**Verification.**
- `./gradlew :data:assembleDebug` succeeds.
- IDE re-syncs without surfacing a DSL-scope warning.

---

## 7. `buildSrc` — drop `kotlinOptions` block

**Problem.** `buildSrc/build.gradle.kts:17–21` configures `tasks.withType<KotlinCompile> { kotlinOptions { jvmTarget = … } }` but `kotlin { jvmToolchain(21) }` (lines 23–25) already pins both source/target levels for compile tasks. The `kotlinOptions` DSL is deprecated.

**Files to change.** `buildSrc/build.gradle.kts:17–21`.

**Concrete change.**

```kotlin
// before
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
}

kotlin {
    jvmToolchain(21)
}
// after — delete the tasks.withType block; keep:
kotlin {
    jvmToolchain(21)
}
```

**Verification.**
- `./gradlew help` — buildSrc still compiles, no deprecation warning.
- `./gradlew :app:assembleDebug` builds (proves Hilt/AGP plugins from buildSrc still resolve correctly).

---

## Suggested order

Land in two PRs.

**PR 1 — risk-free cleanups (items 5, 6, 7, 2, 3 in that order).**
1. Item 5 — delete unused `target_sdk_version` catalog entry.
2. Item 6 — strip the `@Suppress` / TODO from `data/build.gradle.kts`.
3. Item 7 — remove the deprecated `kotlinOptions` block from `buildSrc/build.gradle.kts`.
4. Item 2 — delete the dead `composeOptions {}` block.
5. Item 3 — disable Jetifier (after running `canIDropJetifier` audit).

**PR 2 — behaviour-affecting changes (items 4a, 1).**
6. Item 4a — migrate `kotlinOptions` block in `app/build.gradle.kts` to the `kotlin { compilerOptions {} }` DSL while keeping `-Xcontext-receivers`.
7. Item 1 — Compose BOM migration. Highest risk: bumps Compose UI from 1.5.4 to 1.11.x and material3 from 1.1.2 to 1.4.0; ship after manual smoke-test.

(Item 4b — context-parameters migration — is a separate refactor, tracked elsewhere.)

---

## Risks

- **Compose 1.5.4 → 1.11.x (item 1) is a six-version jump.** Likely API surface deltas: `pullRefresh` graduations, `BasicTextField` rewrite, deprecated `NavHost` overloads, `LazyLayout` API shifts. Plan one cycle of manual smoke-testing onboarding, navigation, and any custom `DrawScope` work in `OnboardingComponents.kt`.
- **material3 1.1.2 → 1.4.0** changes default tonal/elevation palettes; visual diffs in surfaces, buttons, dialogs are likely. Take screenshots before/after.
- **Jetifier off (item 3)** — if any *runtime* (not compile-time) reflection-based dep references `android.support.*`, this can fail at runtime. Mitigate by running existing instrumentation tests post-change.
- **`-Xcontext-receivers` removal accidentally tripped** during DSL migration in item 4a will break `OnboardingComponents.kt` and `TurbineKtx.kt` compilation. Build before committing.
- **Compose compiler / Kotlin alignment** — once item 1 lands, Compose UI version is no longer cross-checked against the Kotlin Compose plugin in the catalog. Document in `README.md` or `TROUBLESHOOTING.md` that bumping Kotlin requires verifying Compose BOM compatibility.

---

## Out of scope

- Section 1: stale dependency bumps (covered in `01-stale-dependencies.md`).
- Section 3: CI/CD workflow changes (covered in `03-ci-workflows.md`).
- Section 4: Gradle wrapper upgrade, AGP/Kotlin version bumps, Renovate config (covered in `04-misc.md`).
- Item 4b: context-parameters source migration (separate refactor ticket).
