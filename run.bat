@echo off
setlocal enabledelayedexpansion

REM Check for JDK
if exist "C:\Users\munmu\.jdks\openjdk-26.0.2\bin\java.exe" (
    set JAVA_BIN="C:\Users\munmu\.jdks\openjdk-26.0.2\bin\java.exe"
) else (
    set JAVA_BIN=java
)

%JAVA_BIN% -cp bin com.fleetflow.FleetFlowApp %*

endlocal
