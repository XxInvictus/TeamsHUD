#!/usr/bin/env bash
set -euo pipefail

PROP_FILE="gradle.properties"

declare -a OUTPUT_KEYS=()

append_keys() {
  local key
  for key in "$@"; do
    [ -n "$key" ] && OUTPUT_KEYS+=("$key")
  done
}

append_preset() {
  case "$1" in
    build)
      append_keys \
        minecraft_version mod_version mod_name mod_id \
        fabric_loader_version fabric_version cloth_config_version \
        forge_version full_version
      ;;
    fabric-test)
      append_keys minecraft_version fabric_loader_version fabric_version cloth_config_version
      ;;
    forge-test)
      append_keys minecraft_version forge_version
      ;;
    release)
      append_keys minecraft_version mod_version mod_name mod_id full_version
      ;;
    custom)
      ;;
    *)
      echo "Unknown preset: $1" >&2
      exit 1
      ;;
  esac
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --file)
      PROP_FILE="$2"
      shift 2
      ;;
    --keys)
      IFS=',' read -r -a REQUESTED_KEYS <<<"$2"
      append_keys "${REQUESTED_KEYS[@]}"
      shift 2
      ;;
    --preset)
      append_preset "$2"
      shift 2
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 1
      ;;
  esac
done

if [ ${#OUTPUT_KEYS[@]} -eq 0 ]; then
  append_preset build
fi

if [ ! -f "$PROP_FILE" ]; then
  echo "Property file ${PROP_FILE} not found" >&2
  exit 1
fi

read_prop() {
  local key="$1"
  local line
  line=$(grep -m1 "^${key}=" "$PROP_FILE" || true)
  if [ -z "$line" ]; then
    echo "Missing ${key} in ${PROP_FILE}" >&2
    exit 1
  fi
  echo "${line#*=}"
}

declare -A VALUES
VALUES[minecraft_version]=$(read_prop minecraft_version)
VALUES[mod_version]=$(read_prop mod_version)
VALUES[mod_name]=$(read_prop mod_name)
VALUES[mod_id]=$(read_prop mod_id)
VALUES[fabric_loader_version]=$(read_prop fabric_loader_version)
VALUES[fabric_version]=$(read_prop fabric_version)
VALUES[cloth_config_version]=$(read_prop cloth_config_version)
VALUES[forge_version]=$(read_prop forge_version)
VALUES[full_version]="${VALUES[minecraft_version]}-${VALUES[mod_version]}"

for key in "${OUTPUT_KEYS[@]}"; do
  value=${VALUES[$key]:-}
  if [ -z "$value" ]; then
    echo "Unable to resolve value for ${key}" >&2
    exit 1
  fi
  echo "${key}=${value}" >> "$GITHUB_OUTPUT"
done

echo "Extracted properties: ${OUTPUT_KEYS[*]}" >&2
