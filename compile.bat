@echo off
setlocal enabledelayedexpansion

echo ===================================================================
echo   FleetFlow - Logistics Management Platform Build Script
echo ===================================================================

REM Check for JDK
if exist "C:\Users\munmu\.jdks\openjdk-26.0.2\bin\javac.exe" (
    set JAVAC_BIN="C:\Users\munmu\.jdks\openjdk-26.0.2\bin\javac.exe"
) else (
    set JAVAC_BIN=javac
)

if not exist bin mkdir bin

echo Compiling Java source files...
dir /s /b src\*.java > sources.txt
%JAVAC_BIN% -d bin @sources.txt
del sources.txt

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Build succeeded! Compiled .class files located in /bin
) else (
    echo.
    echo [ERROR] Compilation failed.
)

endlocal
