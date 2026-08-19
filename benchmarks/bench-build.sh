#!/usr/bin/env bash
#
# Benchmark Gradle build performance of two commits with gradle-profiler:
# a clean build and an incremental (non-ABI) build for each, always with
# --no-build-cache, in a disposable detached worktree so the checkout you
# are working in is never touched. All scratch (worktree, Gradle user home,
# scenario file, results) lives under the system temp dir, so no git-ignore
# setup is needed at any commit the scenarios check out and nothing lingers:
# the OS eventually reclaims whatever a run leaves behind. That includes raw
# results — copy numbers you want to keep into a dated note in benchmarks/.
#
# Usage:
#   benchmarks/bench-build.sh [--keep] <baseline-ref> <candidate-ref> [scenario ...]
#
#   scenario   optional scenario name(s) to (re-)run alone:
#              baseline_clean_build | candidate_clean_build |
#              baseline_incremental_build | candidate_incremental_build
#   --keep     leave the worktree in place afterwards, so a follow-up run
#              (e.g. retrying one failed scenario) skips re-setup
#
# The isolated Gradle user home (kept between runs, reclaimed by the OS with
# the rest of tmp) starts empty on first use: expect a one-off download of
# the Gradle distribution plus all dependencies before any build runs.
#
# Each scenario runs as its own gradle-profiler invocation, with the script
# checking out that scenario's commit first — see the note above the
# scenario file below for why the profiler can't be left to do it.
#
# Env overrides: WARMUPS (default 3), ITERATIONS (default 6).
#
# Prints min/median/max per scenario from the measured rows; raw profiler
# output (benchmark.csv/html) lands in /tmp/<repo>-bench/<timestamp>/.

set -euo pipefail

MUTATION_FILE="app/src/main/java/dev/simonas/quies/MainActivity.kt"
WARMUPS="${WARMUPS:-3}"
ITERATIONS="${ITERATIONS:-6}"

die() { echo "bench-build: $*" >&2; exit 1; }

KEEP=0
if [[ "${1:-}" == "--keep" ]]; then KEEP=1; shift; fi
[[ $# -ge 2 ]] || die "usage: bench-build.sh [--keep] <baseline-ref> <candidate-ref> [scenario ...]"

command -v gradle-profiler >/dev/null || die "gradle-profiler not found (brew install gradle-profiler)"
command -v python3 >/dev/null || die "python3 not found"

ROOT="$(git rev-parse --show-toplevel)"
[[ -f "$ROOT/local.properties" ]] || die "no local.properties in $ROOT (need one with a valid sdk.dir)"

# Resolve to full SHAs: a branch name that is checked out in another worktree
# (e.g. main) refuses to check out in the bench worktree; a raw SHA never does.
BASELINE="$(git -C "$ROOT" rev-parse --verify --quiet "$1^{commit}")" || die "cannot resolve baseline ref: $1"
CANDIDATE="$(git -C "$ROOT" rev-parse --verify --quiet "$2^{commit}")" || die "cannot resolve candidate ref: $2"
shift 2
SCENARIOS=("$@")
if [[ ${#SCENARIOS[@]} -eq 0 ]]; then
    SCENARIOS=(baseline_clean_build candidate_clean_build
               baseline_incremental_build candidate_incremental_build)
fi

# Plain /tmp: same path on macOS and Linux, easy to navigate to;
# BENCH_SCRATCH overrides it (e.g. for testing the script itself).
SCRATCH="${BENCH_SCRATCH:-/tmp/$(basename "$ROOT")-bench}"
BENCH_DIR="$SCRATCH/worktree"
GRADLE_HOME="$SCRATCH/gradle-user-home"
SCENARIO_FILE="$SCRATCH/bench-build.scenarios"
OUT_DIR="$SCRATCH/$(date +%Y%m%d-%H%M%S)"

mkdir -p "$SCRATCH"

# All runs share this one scratch dir, so refuse to start while another run
# is using it. Self-healing: a lock whose process is gone is ignored.
LOCKFILE="$SCRATCH/bench-build.pid"
if [[ -f "$LOCKFILE" ]] && kill -0 "$(cat "$LOCKFILE")" 2>/dev/null; then
    die "another bench-build run is active (pid $(cat "$LOCKFILE")); wait for it or stop it first"
fi
echo $$ > "$LOCKFILE"

# Drop any worktree registration whose tmp dir the OS already reclaimed;
# otherwise `worktree add` refuses to reuse the path.
git -C "$ROOT" worktree prune

if [[ -d "$BENCH_DIR" ]]; then
    git -C "$BENCH_DIR" rev-parse --is-inside-work-tree >/dev/null 2>&1 \
        || die "$BENCH_DIR exists but is not a git worktree; remove it and re-run"
    echo "bench-build: reusing existing worktree $BENCH_DIR"
    git -C "$BENCH_DIR" checkout -- .   # discard mutations left by a failed run
else
    git -C "$ROOT" worktree add --detach "$BENCH_DIR" "$BASELINE"
fi
cp "$ROOT/local.properties" "$BENCH_DIR/local.properties"

cleanup() {
    rm -f "$LOCKFILE"
    [[ -d "$BENCH_DIR" ]] && (cd "$BENCH_DIR" && ./gradlew --stop --gradle-user-home "$GRADLE_HOME" >/dev/null 2>&1) || true
    # The Gradle home stays either way: it holds the downloaded Gradle
    # distribution and dependency caches, which later runs reuse instead of
    # re-downloading everything. The OS reclaims it with the rest of tmp.
    if [[ $KEEP -eq 0 ]]; then
        git -C "$ROOT" worktree remove --force "$BENCH_DIR" 2>/dev/null || true
        rm -f "$SCENARIO_FILE"
    else
        echo "bench-build: kept worktree $BENCH_DIR (--keep)"
    fi
}
trap cleanup EXIT

# No git-checkout blocks: gradle-profiler applies file mutators before its
# git-checkout mutator regardless of declaration order in this file, so any
# incremental scenario that switches commits dies with "local changes would
# be overwritten". Instead the script checks out each scenario's commit
# itself and runs one gradle-profiler invocation per scenario.
cat > "$SCENARIO_FILE" <<EOF
baseline_clean_build {
    title = "baseline: clean build"
    tasks = ["assembleDebug"]
    cleanup-tasks = ["clean"]
    gradle-args = ["--no-build-cache"]
}

candidate_clean_build {
    title = "candidate: clean build"
    tasks = ["assembleDebug"]
    cleanup-tasks = ["clean"]
    gradle-args = ["--no-build-cache"]
}

baseline_incremental_build {
    title = "baseline: incremental build (non-ABI change)"
    tasks = ["assembleDebug"]
    gradle-args = ["--no-build-cache"]
    apply-non-abi-change-to = "$MUTATION_FILE"
}

candidate_incremental_build {
    title = "candidate: incremental build (non-ABI change)"
    tasks = ["assembleDebug"]
    gradle-args = ["--no-build-cache"]
    apply-non-abi-change-to = "$MUTATION_FILE"
}
EOF

echo "bench-build: baseline  = $BASELINE"
echo "bench-build: candidate = $CANDIDATE"

CSVS=()
for sc in "${SCENARIOS[@]}"; do
    case "$sc" in
        baseline_*)  sha="$BASELINE" ;;
        candidate_*) sha="$CANDIDATE" ;;
        *) die "unknown scenario: $sc" ;;
    esac
    # Pre-position the worktree; --force also discards any mutation a
    # previous crashed run left behind.
    git -C "$BENCH_DIR" checkout --quiet --force "$sha"
    (cd "$BENCH_DIR" && gradle-profiler --benchmark \
        --project-dir . \
        --scenario-file "$SCENARIO_FILE" \
        --output-dir "$OUT_DIR/$sc" \
        --gradle-user-home "$GRADLE_HOME" \
        --warmups "$WARMUPS" --iterations "$ITERATIONS" \
        "$sc")
    CSVS+=("$OUT_DIR/$sc/benchmark.csv")
done

# Measured rows only — warm-up rows are expected outliers (first daemon start
# of a session can be off by 10-100x).
python3 - "${CSVS[@]}" <<'EOF'
import csv, statistics as s, sys
for path in sys.argv[1:]:
    with open(path) as f:
        rows = list(csv.reader(f))
    measured = [r for r in rows if r[0].startswith("measured build")]
    for i, name in enumerate(rows[0][1:]):
        vals = [float(r[i + 1]) for r in measured if r[i + 1]]
        print(f"{name}: min={min(vals):.0f}ms median={s.median(vals):.0f}ms max={max(vals):.0f}ms")
EOF

echo "bench-build: raw output in $OUT_DIR"
