#!/usr/bin/env bash

# ---
# Remove all build directories.
# ---

set -euo pipefail

ROOT="$(dirname "$0")/../.."
DIRECTORIES=(android common desktop)

rm -rf "${ROOT}/build"

for dir in "${DIRECTORIES[@]}"; do
  find "${ROOT}/${dir}" -type d -name 'build' -print0 | xargs -0 -I{} rm -rf {}
done