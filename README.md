# PvP Quiz (Fabric, Minecraft 1.21.11)

Client-side Fabric mod. After a PvP fight on mcpvp.club, you must answer a quiz question to continue.

- Minecraft 1.21.11, Fabric Loader >= 0.18.4, Fabric API 0.141.3+1.21.11, Java 21
- Commands: `/pvpquiz` (question manager GUI), `/pvpquiz test`, `/pvpquiz log`, `/pvpquiz keyword add|remove|list`
- Config: `config/pvpquiz.json`

## Build
Push to GitHub (the included Actions workflow builds it) or run locally with Gradle 9.2+ and JDK 21:

    gradle build

Jar ends up in `build/libs/pvp-quiz-1.0.0.jar` (not the `-sources` or `-dev` jar). Drop it in `.minecraft/mods` with Fabric API.
