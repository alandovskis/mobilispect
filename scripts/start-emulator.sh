#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 1 ]; then
    AVD="Pixel_5_API_33"
else
    AVD=$1
fi

~/Library/Android/sdk/emulator/emulator -avd "${AVD}" -dns-server 8.8.8.8 
