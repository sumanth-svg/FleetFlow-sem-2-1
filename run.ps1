# FleetFlow - Intelligent Logistics Management Platform Runner
$jdkPath = "C:\Users\munmu\.jdks\openjdk-26.0.2\bin"

if (Test-Path "$jdkPath\java.exe") {
    $javaBin = "$jdkPath\java.exe"
} else {
    $javaBin = "java"
}

if (!(Test-Path "bin\com\fleetflow\FleetFlowApp.class")) {
    Write-Host "Compiling FleetFlow project..." -ForegroundColor Yellow
    if (Test-Path "$jdkPath\javac.exe") {
        $javacBin = "$jdkPath\javac.exe"
    } else {
        $javacBin = "javac"
    }
    New-Item -ItemType Directory -Force -Path "bin" | Out-Null
    $sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
    & $javacBin -d bin $sources
}

& $javaBin -cp bin com.fleetflow.FleetFlowApp $args
