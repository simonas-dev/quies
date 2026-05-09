# Plan: CI Workflows

## 1. Goal

Refresh the four GitHub Actions workflows under `.github/workflows/` to use currently-supported action versions, fix a copy-paste workflow name in `post-merge.yaml`, migrate off the deprecated `gradle/gradle-build-action` and standalone `gradle/wrapper-validation-action`, and clean up a no-op conditional. No behavioural changes to what gets built/tested/published.

## 2. Per-file change list

### 2.1 `.github/workflows/pre-merge.yaml`

Diffs:
- L14, L29: `actions/checkout@v4.1.1` → `actions/checkout@v4` (float to major). Alternative: pin to a full SHA; see Open Questions.
- L16, L31: `actions/setup-java@v4.1.0` → `actions/setup-java@v4`.
- L21, L36: `gradle/gradle-build-action@v3.1.0` → `gradle/actions/setup-gradle@v4` (action was renamed/relocated; v3 of the old action is the last release).
- L11, L26: remove `if: ${{ !contains(github.event.head_commit.message, 'ci skip') }}` — `head_commit` is null on `pull_request` events, so this never triggers. See Open Questions for the alternative (use PR title).

Side effects:
- `gradle/actions/setup-gradle@v4` enables Gradle's built-in build cache reporting and uses a slightly different cache key scheme than `gradle-build-action@v3`. First run on the new action produces a cache miss; subsequent runs should hit. No config required — defaults are fine for this repo's two-step (`detekt lint`, then `compileX testDebugUnitTest flankRun`) usage.
- `setup-gradle@v4` adds a job summary by default with build scan / cache stats. Harmless; can be silenced with `add-job-summary: 'never'` if noisy.

### 2.2 `.github/workflows/post-merge.yaml`

Diffs:
- L1: `name: Pre Merge Checks` → `name: Post Merge Checks` (or `Validate Build`). User to pick — recommend `Post Merge Checks` for symmetry with `pre-merge.yaml`.
- L14: `actions/checkout@v4.1.1` → `actions/checkout@v4`.
- L16: `actions/setup-java@v4.1.0` → `actions/setup-java@v4`.
- L21: `gradle/gradle-build-action@v3.1.0` → `gradle/actions/setup-gradle@v4`.
- L11: the `head_commit` conditional is valid here (this workflow runs on `push`), so leave it. But note it skips the *whole job* — if intent was per-step skipping, that's fine; just be aware merge-commit messages on main rarely contain `ci skip` in practice.

Side effects: same `setup-gradle@v4` notes as above.

### 2.3 `.github/workflows/deploy.yml`

Diffs:
- L14: `actions/checkout@v4.1.1` → `actions/checkout@v4`.
- L16: `actions/setup-java@v4.1.0` → `actions/setup-java@v4`.
- L21: `gradle/gradle-build-action@v3.1.0` → `gradle/actions/setup-gradle@v4`.
- L11: `head_commit` conditional — for tag pushes, `head_commit` is populated from the tagged commit, so the check works. Low value (you don't typically tag a release with `ci skip` in the message), but harmless. Suggest leaving it to keep the diff minimal.

Side effects: none beyond the `setup-gradle@v4` cache-key change. `publishBundle` does not interact with the action upgrades.

### 2.4 `.github/workflows/gradle-wrapper-validation.yml`

Diffs:
- L16: `actions/checkout@v4.1.1` → `actions/checkout@v4`.
- L18: `gradle/wrapper-validation-action@v1` → `gradle/actions/wrapper-validation@v4`. The standalone action is in maintenance mode and has been merged into the umbrella `gradle/actions` repo. Behaviour is equivalent (verifies `gradle/wrapper/gradle-wrapper.jar` checksum against known-good list).
- L19: trailing whitespace cleanup (cosmetic).

Side effects: none — same validation, same exit codes.

## 3. Open questions (for the user to decide)

1. **Pin actions to SHAs vs. float to major?** Floating to `@v4` (`actions/checkout`, `actions/setup-java`, `gradle/actions/*`) is simpler and gets security patches for free. Pinning to a full 40-char SHA (e.g. `actions/checkout@a5ac7e5...`) is the OpenSSF Scorecard recommendation and protects against tag-rewrite supply-chain attacks. For a personal/small project, floating to major is the pragmatic default. Decide once and apply uniformly.
2. **`pre-merge.yaml` trigger `branches: '*'`** — this fires on every PR regardless of base branch, including stacked feature-to-feature PRs. Most repos use `branches: [main]` to only run on PRs targeting main. Is the wildcard intentional (e.g. you stack PRs)? If not, narrow to `main`.
3. **`if: ${{ !contains(github.event.head_commit.message, 'ci skip') }}` on `pre-merge.yaml`** — this is a no-op on `pull_request` events (`head_commit` is null, so `contains(null, 'ci skip')` is false, so the negation is true, so the job always runs — i.e. it works by accident). Three options:
   - (a) Remove it from `pre-merge.yaml` entirely (cleanest).
   - (b) Replace with `!contains(github.event.pull_request.title, 'ci skip')` so the skip mechanism actually works for PRs.
   - (c) Leave as is. Recommend (a) unless you actively use `ci skip` on PRs.
4. **Workflow name for `post-merge.yaml`** — `Post Merge Checks` vs. `Validate Build`. No functional difference; pick one.

## 4. Verification

1. Open a throwaway branch (`chore/ci-bumps`) with the diffs and push a no-op commit (e.g. whitespace in `README.md`). Confirm:
   - `Pre Merge Checks` runs both jobs to completion when the branch is opened as a PR.
   - `Validate Gradle Wrapper` runs and succeeds on both `pull_request` and (after merge) `push` to main.
2. Inspect the `Setup Gradle` step's job summary — `gradle/actions/setup-gradle@v4` emits a summary with cache restore/save sizes. First run = miss, second run on the same branch = hit.
3. Merge to main and confirm `Post Merge Checks` (renamed) runs `testDebugUnitTest flankRun` end-to-end.
4. (Optional, when you cut the next release) tag `vX.Y.Z` on a throwaway and observe `Deploy` running through `publishBundle`. Skip if you don't want to burn a Play Store internal-track upload — the deploy workflow shares the same three action upgrades as the others, so success on `pre-merge` + `post-merge` is strong evidence it'll work.
5. Check the Actions tab for any deprecation warnings — should be zero after this pass.

## 5. Out of scope

- Bumping the Gradle wrapper version itself — covered in Section 4.
- Bumping the JDK version — already on Zulu 21, current LTS, no change needed.
- Migrating from Flank to GMD (Gradle Managed Devices) or another runner — orthogonal, larger discussion.
- Reworking the `data/downloadProdSource.sh` / `replaceSecrets.sh` flow used by `post-merge` and `deploy` — separate concern.
- Adding concurrency groups, build matrices, or caching of `~/.android` — possible follow-up, not part of this maintenance pass.
