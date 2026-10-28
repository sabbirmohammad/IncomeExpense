#!/usr/bin/env bash
# ==============================================================================
# Expense Tracker - Version Bumping & Release Preparation Script
#
# Usage:
#   ./scripts/bump_version.sh major [optional_commit_message]
#       Increments major version (e.g. 6.5 -> 7.0) and advances versionCode
#   ./scripts/bump_version.sh minor [optional_commit_message]
#       Increments minor version (e.g. 6.5 -> 6.6) and advances versionCode
#   ./scripts/bump_version.sh patch [optional_commit_message]
#       Increments patch version (e.g. 6.5 -> 6.5.1) and advances versionCode
#   ./scripts/bump_version.sh <version> [optional_commit_message]
#       Explicit version string (e.g. 7.0, 8.0)
# ==============================================================================

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

BUILD_GRADLE="app/build.gradle.kts"
APP_CONSTANTS="app/src/main/java/com/example/expensetracker/common/AppConstants.kt"

if [ ! -f "$BUILD_GRADLE" ] || [ ! -f "$APP_CONSTANTS" ]; then
    echo "Error: Required files not found. Are you in the ExpenseTracker root directory?"
    exit 1
fi

BUMP_TYPE="${1:-}"
COMMIT_MSG="${2:-}"

if [ -z "$BUMP_TYPE" ]; then
    echo "Usage: $0 [major|minor|patch|<explicit_version>] [optional_commit_message]"
    echo ""
    echo "Examples:"
    echo "  $0 major 'feat: major redesign and cloud sync overhaul'"
    echo "  $0 minor 'feat: new analytics insights sheet'"
    echo "  $0 patch 'fix: resolve edge-case in budget alert'"
    echo "  $0 7.0   'feat: bump to 7.0'"
    exit 1
fi

# Extract current version
CURRENT_CODE=$(grep -E 'versionCode\s*=\s*' "$BUILD_GRADLE" | head -1 | sed -E 's/.*=\s*([0-9]+).*/\1/')
CURRENT_NAME=$(grep -E 'versionName\s*=\s*' "$BUILD_GRADLE" | head -1 | sed -E 's/.*"([^"]+)".*/\1/')

echo "=============================================="
echo "Current Version: v${CURRENT_NAME} (code: ${CURRENT_CODE})"
echo "Bump Type: ${BUMP_TYPE}"
echo "=============================================="

# Parse semantic components
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_NAME"
MAJOR="${MAJOR:-0}"
MINOR="${MINOR:-0}"
PATCH="${PATCH:-0}"

if [ "$BUMP_TYPE" = "major" ]; then
    NEW_MAJOR=$((MAJOR + 1))
    NEW_MINOR=0
    NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}"
    TARGET_TIER=$((NEW_MAJOR * 10))
    if [ "$TARGET_TIER" -gt "$CURRENT_CODE" ]; then
        NEW_CODE=$TARGET_TIER
    else
        NEW_CODE=$((CURRENT_CODE + 1))
    fi
elif [ "$BUMP_TYPE" = "minor" ]; then
    NEW_MAJOR="$MAJOR"
    NEW_MINOR=$((MINOR + 1))
    NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}"
    NEW_CODE=$((CURRENT_CODE + 1))
elif [ "$BUMP_TYPE" = "patch" ]; then
    NEW_MAJOR="$MAJOR"
    NEW_MINOR="$MINOR"
    NEW_PATCH=$((PATCH + 1))
    NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}.${NEW_PATCH}"
    NEW_CODE=$((CURRENT_CODE + 1))
elif [[ "$BUMP_TYPE" =~ ^[0-9]+(\.[0-9]+)+$ ]]; then
    NEW_NAME="$BUMP_TYPE"
    NEW_CODE=$((CURRENT_CODE + 1))
else
    echo "Error: Invalid bump type '$BUMP_TYPE'. Expected 'major', 'minor', 'patch', or a valid version (e.g. 7.0)."
    exit 1
fi

echo "Target Version: v${NEW_NAME} (code: ${NEW_CODE})"
echo ""

# 1. Update app/build.gradle.kts
sed -i '' -E "s/(versionCode\s*=\s*)[0-9]+/\1${NEW_CODE}/" "$BUILD_GRADLE"
sed -i '' -E "s/(versionName\s*=\s*\")[^\"]+(\")/\1${NEW_NAME}\2/" "$BUILD_GRADLE"

# 2. Update AppConstants.kt
sed -i '' -E "s/(const val APP_VERSION = \"v)[^\"]+(\")/\1${NEW_NAME}\2/" "$APP_CONSTANTS"

echo "✓ Updated ${BUILD_GRADLE}"
echo "✓ Updated ${APP_CONSTANTS}"

# 3. Verify tests
echo ""
echo "Running unit tests to verify..."
./gradlew testDebugUnitTest --quiet

echo "✓ Unit tests passed successfully!"
echo ""
echo "=============================================="
echo "Version successfully bumped to v${NEW_NAME} (${NEW_CODE})!"
echo "=============================================="
echo ""
echo "Next Git Steps:"
echo "  git add app/build.gradle.kts app/src/main/java/com/example/expensetracker/common/AppConstants.kt"
if [ -n "$COMMIT_MSG" ]; then
    echo "  git commit -m \"${COMMIT_MSG}\""
else
    echo "  git commit -m \"feat(release): bump to v${NEW_NAME}\""
fi
echo "  git tag -a \"v${NEW_NAME}\" -m \"Release v${NEW_NAME}\""
echo "  git push origin main --tags"
echo "=============================================="
