#!/usr/bin/env bash
set -euo pipefail

ACTION="check"
JAR_DIR="forge/build/libs"
TARGET_CLASS="com.xxinvictus.teamshudplus.mixin.ServerPlayerMixin"
PATTERN=' serverLevel('

while [[ $# -gt 0 ]]; do
  case "$1" in
    --action)
      ACTION="$2"
      shift 2
      ;;
    --dir)
      JAR_DIR="$2"
      shift 2
      ;;
    --class)
      TARGET_CLASS="$2"
      shift 2
      ;;
    --pattern)
      PATTERN="$2"
      shift 2
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 1
      ;;
  esac
done

release_jar=$(find "$JAR_DIR" -maxdepth 1 -name '*-forge-*.jar' ! -name '*-javadoc.jar' ! -name '*-sources.jar' ! -name '*-dev.jar' | head -n 1)

if [ -z "$release_jar" ]; then
  echo "Error: Could not find Forge release jar in ${JAR_DIR}" >&2
  exit 1
fi

if javap -classpath "$release_jar" "$TARGET_CLASS" | grep -q "$PATTERN"; then
  if [ "$ACTION" = "check" ]; then
    echo "Forge release jar still in named namespace; needs sync" >&2
    echo "needs_sync=true" >> "$GITHUB_OUTPUT"
  else
    echo "Error: Forge release jar is still in named namespace (pattern ${PATTERN} matched)" >&2
    exit 1
  fi
else
  if [ "$ACTION" = "check" ]; then
    echo "Forge release jar already remapped" >&2
    echo "needs_sync=false" >> "$GITHUB_OUTPUT"
  else
    echo "Forge release jar verified remapped" >&2
  fi
fi
