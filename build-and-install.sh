#!/bin/sh
# Builds the mod and copies the jar into your Minecraft mods folder.
gradle build || exit 1
MODS="${MODS_DIR:-$HOME/.minecraft/mods}"
mkdir -p "$MODS" && cp build/libs/pvp-quiz-1.0.0.jar "$MODS/" && echo "Copied to $MODS"
