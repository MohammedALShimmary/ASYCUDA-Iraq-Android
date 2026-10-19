#!/usr/bin/env bash
# ==============================================================================
# Reproducible Build Script for ASYCUDA World Android Java Bridge Archives
# ==============================================================================
# Compiles the open-source Java bridge sources in java-bridge/ into:
#   1. build/00_bootpatch.jar  (java.awt.Desktop bridge for OpenJDK 8 on Android)
#   2. build/00_jnlp.jar       (javax.jnlp.* compatibility layer without javaws)
#   3. build/00_boot.jar       (asycuda.AsycudaBoot wrapper & PDF print service)
#
# None of the official Iraqi Ministry of Finance / UNCTAD JARs are modified.
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="${SCRIPT_DIR}/build"
CLIENT_JAR="${1:-}"

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}/classes_bootpatch" "${OUT_DIR}/classes_jnlp" "${OUT_DIR}/classes_boot"

echo "[1/3] Compiling 00_bootpatch.jar (java.awt.Desktop Android bridge)..."
javac -source 8 -target 8 -Xlint:-options \
    -d "${OUT_DIR}/classes_bootpatch" \
    "${SCRIPT_DIR}/bootpatch/java/awt/Desktop.java"
cp "${SCRIPT_DIR}/bootpatch/java/awt/Desktop.java" "${OUT_DIR}/classes_bootpatch/java/awt/Desktop.java"
jar cf "${OUT_DIR}/00_bootpatch.jar" -C "${OUT_DIR}/classes_bootpatch" .

echo "[2/3] Compiling 00_jnlp.jar (javax.jnlp.ServiceManager & BasicService)..."
javac -source 8 -target 8 -Xlint:-options \
    -d "${OUT_DIR}/classes_jnlp" \
    "${SCRIPT_DIR}/jnlp/javax/jnlp/BasicService.java" \
    "${SCRIPT_DIR}/jnlp/javax/jnlp/UnavailableServiceException.java" \
    "${SCRIPT_DIR}/jnlp/javax/jnlp/ServiceManager.java"
cp "${SCRIPT_DIR}/jnlp/javax/jnlp/"*.java "${OUT_DIR}/classes_jnlp/javax/jnlp/"
jar cf "${OUT_DIR}/00_jnlp.jar" -C "${OUT_DIR}/classes_jnlp" .

echo "[3/3] Compiling 00_boot.jar (asycuda.AsycudaBoot)..."
javac -source 8 -target 8 -Xlint:-options \
    -d "${OUT_DIR}/classes_boot" \
    "${SCRIPT_DIR}/AsycudaBoot.java"
cp "${SCRIPT_DIR}/AsycudaBoot.java" "${OUT_DIR}/classes_boot/asycuda/AsycudaBoot.java"
jar cf "${OUT_DIR}/00_boot.jar" -C "${OUT_DIR}/classes_boot" .

echo "Build complete. SHA-256 digests:"
sha256sum "${OUT_DIR}/00_bootpatch.jar" "${OUT_DIR}/00_jnlp.jar" "${OUT_DIR}/00_boot.jar"
