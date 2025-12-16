#!/bin/bash
# Validate test artifacts for Fabric/Forge modloaders
# Usage: validate-test-artifacts.sh --modloader <fabric|forge> [--check-refmap]

set -euo pipefail

# Default values
MODLOADER=""
CHECK_REFMAP=false

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --modloader)
      MODLOADER="$2"
      shift 2
      ;;
    --check-refmap)
      CHECK_REFMAP=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Validate required parameters
if [ -z "$MODLOADER" ]; then
  echo "Error: --modloader is required"
  exit 1
fi

# Set modloader-specific patterns
case "$MODLOADER" in
  fabric)
    JAR_PATTERN="*-fabric-*.jar"
    EXCLUDE_PATTERN="! -name '*-javadoc.jar' ! -name '*-sources.jar'"
    ;;
  forge)
    JAR_PATTERN="*-forge-*.jar"
    EXCLUDE_PATTERN="! -name '*-javadoc.jar' ! -name '*-sources.jar' ! -name '*-dev.jar'"
    ;;
  *)
    echo "Error: Unknown modloader: $MODLOADER"
    exit 1
    ;;
esac

# Setup directories
rm -rf run/mods
mkdir -p run/mods

# Copy artifacts
echo "Copying $MODLOADER artifacts to run/mods..."
eval "find build/libs -name '$JAR_PATTERN' $EXCLUDE_PATTERN -exec cp {} run/mods/ \\;"

# Verify artifacts were copied
if ! ls run/mods/$JAR_PATTERN >/dev/null 2>&1; then
  echo "Error: $MODLOADER release jar not found in build/libs"
  exit 1
fi

echo "✓ $MODLOADER jar copied successfully"

# Check refmap if requested
if [ "$CHECK_REFMAP" = "true" ]; then
  echo "Checking for refmap in release jar..."
  if ! jar tf run/mods/$JAR_PATTERN | grep -i refmap; then
    echo "Error: refmap missing from release jar"
    exit 1
  fi
  echo "✓ Refmap found in release jar"
fi

echo "✓ Test artifacts validated successfully"
