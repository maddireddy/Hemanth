#!/usr/bin/env bash
# Lightweight helper to download and run Gradle 7.6 if a compatible Gradle isn't available.
set -euo pipefail
ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
GRADLE_VERSION=7.6
GRADLE_DIST_NAME=gradle-${GRADLE_VERSION}-bin.zip
GRADLE_DIR="$ROOT_DIR/.gradle/gradle-${GRADLE_VERSION}"

if [ -x "${GRADLE_DIR}/bin/gradle" ]; then
  exec "${GRADLE_DIR}/bin/gradle" "$@"
fi

mkdir -p "${ROOT_DIR}/.gradle"
TMP_ZIP="${ROOT_DIR}/.gradle/${GRADLE_DIST_NAME}"
if [ ! -f "${TMP_ZIP}" ]; then
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  curl -sSLo "${TMP_ZIP}" "https://services.gradle.org/distributions/${GRADLE_DIST_NAME}"
fi
echo "Unpacking Gradle..."
unzip -q -o "${TMP_ZIP}" -d "${ROOT_DIR}/.gradle"
mv -f "${ROOT_DIR}/.gradle/gradle-${GRADLE_VERSION}" "${GRADLE_DIR}" 2>/dev/null || true
chmod +x "${GRADLE_DIR}/bin/gradle"
exec "${GRADLE_DIR}/bin/gradle" "$@"
#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST_DIR="$ROOT_DIR/.gradle-dist"
GRADLE_VERSION=7.6
DIST_ZIP_NAME="gradle-${GRADLE_VERSION}-bin.zip"
DIST_URL="https://services.gradle.org/distributions/${DIST_ZIP_NAME}"
GRADLE_HOME="$DIST_DIR/gradle-${GRADLE_VERSION}"

mkdir -p "$DIST_DIR"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  echo "Gradle $GRADLE_VERSION not found locally. Downloading..."
  cd "$DIST_DIR"
  if [ ! -f "$DIST_ZIP_NAME" ]; then
    curl -L -o "$DIST_ZIP_NAME" "$DIST_URL"
  fi
  unzip -q -o "$DIST_ZIP_NAME"
  cd - >/dev/null
fi

export GRADLE_HOME="$GRADLE_HOME"
export PATH="$GRADLE_HOME/bin:$PATH"

# Run gradle with passed args
gradle "$@"
