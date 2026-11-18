@echo off
REM Builds the mod and copies the jar straight into your Minecraft mods folder.
REM Requires: JDK 21 (https://adoptium.net) and Gradle 9.2+ (https://gradle.org/install) on PATH.
call gradle build || (echo BUILD FAILED & pause & exit /b 1)
if not exist "%APPDATA%\.minecraft\mods" mkdir "%APPDATA%\.minecraft\mods"
copy /Y build\libs\pvp-quiz-1.0.0.jar "%APPDATA%\.minecraft\mods\"
echo Done. Jar copied to %APPDATA%\.minecraft\mods
pause
