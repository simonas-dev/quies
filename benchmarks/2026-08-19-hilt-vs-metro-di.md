# Hilt vs Metro DI — build benchmark, 2026-08-19

Gradle Profiler comparison of the Dagger Hilt baseline against the Metro DI
migration PoC.

- Hilt commit: `c83c3b6b787986cce1458afa51ea78a31185adcc` (main, Release
  1.0.2) — the script's "baseline"
- Metro commit: `5c29ed9bba52fa220b66387c6ce0cc77c32a5f62`
  (metro-migration-poc) — the script's "candidate"
- Run with `benchmarks/bench-build.sh` (see "How to repeat"):
  `assembleDebug`, `--no-build-cache`, `--warmups 3 --iterations 6`,
  non-ABI mutation on `MainActivity.kt` for the incremental scenarios.

## Results (build cache disabled, n=6 each)

| Scenario                     | min     | median  | max     |
| ---------------------------- | ------- | ------- | ------- |
| Hilt: clean build            | 3508 ms | 3736 ms | 4424 ms |
| Metro: clean build           | 2110 ms | 2202 ms | 2370 ms |
| Hilt: incremental (non-ABI)  | 1021 ms | 1092 ms | 1244 ms |
| Metro: incremental (non-ABI) | 550 ms  | 590 ms  | 646 ms  |

Metro is faster on both scenarios: **~41% on clean and ~46% on incremental
build medians**. The ranges don't overlap in either comparison — every
Metro build beat every Hilt build of the same scenario — so the direction
is solid even at n=6 (rank-sum p ≈ 0.002).

Raw measured values (ms, build #1 → #6):

- Hilt clean: 4424, 4109, 3755, 3716, 3699, 3508
- Metro clean: 2308, 2370, 2173, 2232, 2131, 2110
- Hilt incremental: 1244, 1160, 1121, 1062, 1046, 1021
- Metro incremental: 646, 641, 602, 563, 578, 550

Caveat: every scenario still trends downward across its measured builds
(Hilt clean drops ~21% from build #1 to #6), so 3 warm-ups didn't fully
reach steady state and the medians are somewhat inflated. Both variants
carry the same drift and the non-overlap argument holds regardless, so
this affects the absolute numbers, not the comparison. For a tighter
magnitude estimate, re-run with `WARMUPS=5 ITERATIONS=10`.

## Superseded first run (build cache left on), same day

The first pass was run with the Gradle build cache enabled
(`org.gradle.caching=true` in `gradle.properties`); the profiler's log
showed ~600 `FROM-CACHE` task outcomes, so its "clean" rows mostly
measured cache-restore performance plus whatever tasks each DI setup makes
non-cacheable — not true from-source compilation. The tell was in its own
table: Hilt's "clean" median (1075 ms) beat its incremental one (1404 ms),
which is impossible for real from-source builds. The corrected run above
(via `bench-build.sh`, which forces `--no-build-cache`) supersedes these
numbers; they're kept because the pair shows how much the cache distorts
the clean-build picture (~1 s vs ~4 s) while the direction survives either
way.

| Scenario                     | min     | median  | max     |
| ---------------------------- | ------- | ------- | ------- |
| Hilt: clean build            | 983 ms  | 1075 ms | 1206 ms |
| Metro: clean build           | 817 ms  | 850 ms  | 910 ms  |
| Hilt: incremental (non-ABI)  | 1286 ms | 1404 ms | 1643 ms |
| Metro: incremental (non-ABI) | 711 ms  | 806 ms  | 875 ms  |

This run's Metro incremental row came from a separate retry in a different
session: the scenario failed on a gradle-profiler quirk — its
`git-checkout` mutator runs after file mutators regardless of declaration
order, so an incremental scenario that switches commits dies on a dirty
checkout. That failure is why `bench-build.sh` now checks out each
scenario's commit itself and runs one profiler invocation per scenario.

## How to repeat this benchmark

Prerequisites: `brew install gradle-profiler`; a `local.properties` with a
valid `sdk.dir`; a quiet machine (no other builds or heavy processes).
Then, from anywhere in the repo:

```bash
# baseline = Hilt, candidate = Metro
benchmarks/bench-build.sh \
  c83c3b6b787986cce1458afa51ea78a31185adcc \
  5c29ed9bba52fa220b66387c6ce0cc77c32a5f62
```

The script is the whole procedure: it creates a disposable detached
worktree and all other scratch under the system temp dir
(`/tmp/quies-bench/`), generates the four gradle-profiler scenarios
(clean + incremental non-ABI per commit, all with `--no-build-cache`),
runs each scenario as its own profiler invocation with `--warmups 3
--iterations 6` after checking out that scenario's commit, prints
min/median/max per scenario from the measured rows, and cleans up after
itself. Raw profiler output (`benchmark.csv`/`.html`) lands in
`/tmp/quies-bench/<timestamp>/<scenario>/` and survives only until the OS
cleans temp — transcribe numbers you want to keep into a dated note here.

If a scenario fails partway, re-run just that one by appending its name
(`baseline_clean_build`, `candidate_clean_build`,
`baseline_incremental_build`, `candidate_incremental_build`). Pass
`--keep` as the first argument to leave the worktree in place between such
runs, and set `WARMUPS`/`ITERATIONS` env vars to override the defaults.
The script's Gradle user home is isolated from `~/.gradle`, so the first
run after a reboot (or ever) starts with a multi-minute download of the
Gradle distribution and all dependencies — that happens before the
warm-ups and doesn't affect the measured numbers; later runs reuse the
cache.

Comparing against a newer Metro (or other DI) state: keep the Hilt SHA as
the baseline anchor, pass your new commit as the candidate (any ref works —
the script resolves it to a full SHA), and file the numbers as a new dated
note in `benchmarks/`.
