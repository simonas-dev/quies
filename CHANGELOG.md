# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2026-05-10

### Changed
- Updated dependencies and Gradle wrapper.
- Streamlined CI workflows.
- Cleaned up Gradle properties and project configuration.

### Removed
- Removed TROUBLESHOOTING.md and Renovate configuration.

## [1.0.1] - 2025-09-11

### Added
- Parallelized CI jobs for faster builds.

### Changed
- Bumped Kotlin, KSP, and Hilt versions.
- Upgraded Android SDK and Android Gradle Plugin.
- Bumped JVM target to 21 and updated GitHub Actions JDK.
- Migrated annotation processing from KSP to kapt.
- Increased pagefile size for CI stability.

### Removed
- Removed GitHub issue/PR templates.

### Fixed
- Caught out-of-bounds exceptions in onboarding text rendering.

## [1.0.0] - 2024-06-15

First public stable release of Quies.

### Added
- Public 1.0 release published to the Play Store.
- Extracted revealing-text rendering into a reusable component.

### Fixed
- UI tests no longer fail on release builds.
- Menu now reacts reliably to user clicks.
- Animations no longer require tap-and-hold to play correctly.

## [0.13.0] - 2024-06-11

### Changed
- Lowered minimum supported Android version to API 28 (Android 9).
- Updated Android Gradle Plugin.

### Fixed
- Various fixes around onboarding and main activity behavior.

## [0.12.2] - 2024-06-10

### Changed
- Lowered minimum SDK requirement to broaden device support.

## [0.12.1] - 2024-05-31

### Fixed
- Disabled misleading drag animation on the home screen.
- Onboarding text no longer gets clipped by cards.
- Card shadow now fades correctly.
- Card side text no longer gets clipped while fading out.

## [0.12.0] - 2024-05-30

### Added
- Unveiling effect for cards.

## [0.11.2] - 2024-05-23

### Fixed
- Disabled minification for the data module to prevent runtime issues.
- Restored Crashlytics ProGuard rules that had been removed.

## [0.11.0] - 2024-05-23

### Added
- Analytics integration with Mixpanel for tracking question, game set, and onboarding events.
- New abstract river logo.

### Changed
- Updated Play Store listing assets, screenshots, descriptions, and contact info.
- Updated dependencies and Gradle wrapper.

## [0.10.3] - 2024-03-04

### Fixed
- Corrected misuse of the `replaceSecrets` step in the build pipeline.

## [0.10.2] - 2024-03-03

### Changed
- Updated CI actions.

### Fixed
- Resolved execute permission issue on the create-assets script.

## [0.10.1] - 2024-03-03

### Fixed
- Fixed confusing orientation behavior after level selection.
- Fixed the secrets download step in CI.

## [0.10.0] - 2024-03-03

### Added
- Onboarding cards.

### Fixed
- Corrected onboarding splash pacing.
- Fixed visual regression caused by soft shadows.

## [0.9.2] - 2024-03-03

### Changed
- Softened onboarding text using a shadow effect.
- Disabled tablet support and added tablet spacing workaround.
- Updated Android Gradle Plugin.

### Fixed
- Fixed an issue with replacing production assets during build.

## [0.9.1] - 2024-02-26

### Changed
- Shortened the "Let It Out" onboarding copy.

### Fixed
- Miscellaneous onboarding fixes.

## [0.9.0] - 2024-02-25

### Added
- Blurry text rendering on canvas during onboarding.

## [0.8.0] - 2024-02-19

### Added
- Onboarding story pager.
- Persistence for onboarding state.

## [0.7.0] - 2024-02-08

### Added
- Screensaver mode.
- Endgame question.
- Shaders playground with OpenGL surface view and wave shader.
- Persistent storage layer backed by Jetpack DataStore.
- Keep-screen-on support to allow sleeping behavior.

### Fixed
- Glitchy text layouting.
- Unintended drag event consumption on cards.
- Font rendering issue.

## [0.6.1] - 2023-11-25

### Changed
- Updated launcher logo.

## [0.6.0] - 2023-11-18

### Added
- Golden responsive design.

### Changed
- Updated launcher icon.
- Coalesced game sets screen strings and layout.
- Bumped Android Gradle Plugin and dependencies.

### Removed
- Lint step in pre-merge CI workflow.

## [0.5.0] - 2023-09-23

### Added
- Drag-to-proceed gesture.

## [0.4.3] - 2023-09-15

### Added
- Next-level suggestion menu on the card screen.

### Changed
- Refreshed app branding and updated README with the project name.

## [0.4.2] - 2023-09-06

### Fixed
- Improved discoverability of the menu button.

## [0.4.1] - 2023-09-02

### Fixed
- Twitchy card animations.
- Duplicate questions appearing in a session.

## [0.4.0] - 2023-09-01

### Added
- Color theming throughout the app, including custom fonts.
- Level descriptions on the game sets screen.

## [0.3.1] - 2023-08-29

### Added
- Keep-screen-on behavior on the card screen.

### Changed
- Card screen now animates based on individual card state changes.

### Fixed
- Card animation performance.
- Animation-related deadlock affecting UI tests.

## [0.3.0] - 2023-08-26

### Added
- Indicator for the number of used cards on the card screen.
- Visual communication of level changes.
- Visual cue indicating cards are clickable.

### Changed
- Temporarily disabled UI tests during deploy due to animation blocking.

### Fixed
- Glitchy card animation caused by reliance on non-global time.

## [0.2.1] - 2023-08-24

### Changed
- Replaced example content with the real (encrypted) question content on release builds.
- Switched CI to Ubuntu for faster runs.

## [0.2.0] - 2023-08-19

### Added
- Difficulty levels for questions.
- Question sets and segregation of questions by set.
- Fullscreen mode.
- Store listing assets, published automatically via `publishListing`.

### Changed
- Major UI upgrade to the card and game sets screens.
- CI deploys directly to the internal release track instead of drafts.
- Replaced build step with a faster compile step in CI.

## [0.1.0] - 2023-08-16

### Added
- Initial release. Compose Material3 UI with Hilt DI, a question/card screen, automated publishing to Google Play, and CI running tests on Firebase Test Lab.
