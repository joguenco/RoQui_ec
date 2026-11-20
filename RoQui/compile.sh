#!/usr/bin/env bash
# Verify the toolchain, compile without OpenAPI, then restore the source tree.
set -uo pipefail

required_gradle="9.8.2"
required_java=25

cd "$(dirname "$0")" || exit 1

build_file="build.gradle.kts"
properties_file="src/main/resources/application.properties"
open_api_config_file="src/main/kotlin/dev/joguenco/roqui/security/OpenApiConfig.kt"
source_files=("$build_file" "$properties_file" "$open_api_config_file")

restore_sources() {
    local status=$?
    trap - EXIT
    echo "Restoring source files to their original state..."
    if ! git restore -- "${source_files[@]}"; then
        echo "Failed to restore source files." >&2
        exit 1
    fi
    exit "$status"
}
trap restore_sources EXIT

# --- Verify Gradle version ---
if ! command -v gradle >/dev/null 2>&1; then
    echo "Gradle is not installed or not on PATH." >&2
    exit 1
fi

if ! gradle_info=$(LC_ALL=C gradle --version 2>&1); then
    echo "Could not determine Gradle version." >&2
    exit 1
fi

gradle_version=$(sed -n 's/^Gradle \([0-9][0-9.]*\).*/\1/p' <<< "$gradle_info")
if [[ "$gradle_version" != "$required_gradle" ]]; then
    echo "Invalid Gradle version: ${gradle_version:-unknown} (expected $required_gradle)." >&2
    exit 1
fi
echo "Gradle $gradle_version OK"

# --- Verify Java version used by Gradle for compilation (JAVA_HOME) ---
if [[ -z "${JAVA_HOME:-}" || ! -x "$JAVA_HOME/bin/java" ]]; then
    echo "JAVA_HOME is not set to a valid JDK: '${JAVA_HOME:-}'" >&2
    exit 1
fi

java_exe="$JAVA_HOME/bin/java"
if ! java_info=$(LC_ALL=C "$java_exe" -version 2>&1); then
    echo "Could not determine Java version." >&2
    exit 1
fi

java_version=$(sed -n 's/.*version "\([0-9][0-9]*\).*/\1/p' <<< "$java_info")
if [[ "$java_version" != "$required_java" ]]; then
    echo "Invalid Java version: ${java_version:-unknown} (expected $required_java; JAVA_HOME=$JAVA_HOME)." >&2
    exit 1
fi
echo "Java $java_version OK ($java_exe)"

# --- Compile without OpenAPI ---
echo "Preparing an OpenAPI-free compile..."
perl -0pi -e 's/[ \t]*implementation\("org\.springdoc:springdoc-openapi-starter-webmvc-ui:[^"]+"\)\r?\n//g' "$build_file" || exit 1
perl -0pi -e 's/^# (?:Begin )?Swagger UI configuration\r?\n.*?^springdoc\.swagger-ui\.enabled=true\r?\n(?:# End Swagger UI configuration\r?\n)?\r?\n//ms' "$properties_file" || exit 1
perl -0pi -e 's/^spring\.jpa\.hibernate\.ddl-auto=validate\r?\n//mg' "$properties_file" || exit 1
rm -f "$open_api_config_file"

# Fail fast if any of the temporary removals did not take effect.
if grep -q 'springdoc' "$build_file"; then
    echo "Could not remove 'springdoc' from $build_file." >&2
    exit 1
fi
if grep -q 'swagger-ui' "$properties_file"; then
    echo "Could not remove 'swagger-ui' from $properties_file." >&2
    exit 1
fi
if grep -q 'ddl-auto=validate' "$properties_file"; then
    echo "Could not remove 'ddl-auto=validate' from $properties_file." >&2
    exit 1
fi

echo "Running gradle build..."
gradle build
build_status=$?
exit "$build_status"
