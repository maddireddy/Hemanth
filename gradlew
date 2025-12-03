#!/usr/bin/env sh
# Gradle wrapper script (lightweight). After you run `gradle wrapper --gradle-version 7.6` locally this will be fully functional.
# Make executable: chmod +x gradlew
BASE_DIR=$(dirname "$0")
if [ -z "$GRADLE_HOME" ]; then
  echo "Gradle home not set; please run 'gradle wrapper --gradle-version 7.6' locally to bootstrap the wrapper or install Gradle." >&2
  exit 1
fi
exec "$GRADLE_HOME/bin/gradle" "$@"
