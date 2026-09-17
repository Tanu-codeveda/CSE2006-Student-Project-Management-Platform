#!/bin/sh
# Maven Wrapper runner for POSIX systems
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "[ERROR] Maven is not installed in PATH." >&2
    exit 1
fi
