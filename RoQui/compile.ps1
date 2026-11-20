# Verify the toolchain, compile without OpenAPI, then restore the source tree.
$requiredGradle = "9.8.2"
$requiredJava = 25

Set-Location -Path $PSScriptRoot

# --- Verify Gradle version ---
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) {
    Write-Host "Gradle is not installed or not on PATH." -ForegroundColor Red
    exit 1
}

$gradleInfo = & gradle --version 2>&1 | Out-String
$gradleMatch = [regex]::Match($gradleInfo, 'Gradle\s+(\d+(?:\.\d+)+)')
$gradleVersion = if ($gradleMatch.Success) { $gradleMatch.Groups[1].Value } else { "unknown" }
if ($gradleVersion -ne $requiredGradle) {
    Write-Host "Invalid Gradle version: $gradleVersion (expected $requiredGradle)." -ForegroundColor Red
    exit 1
}
Write-Host "Gradle $gradleVersion OK" -ForegroundColor Green

# --- Verify Java version used by Gradle for compilation (JAVA_HOME) ---
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    Write-Host "JAVA_HOME is not set to a valid JDK: '$env:JAVA_HOME'" -ForegroundColor Red
    exit 1
}

$javaExe = Join-Path $env:JAVA_HOME 'bin\java.exe'
$javaInfo = & $javaExe -version 2>&1 | Out-String
$javaMatch = [regex]::Match($javaInfo, 'version\s+"(\d+)')
$javaVersion = if ($javaMatch.Success) { [int]$javaMatch.Groups[1].Value } else { 0 }
if ($javaVersion -ne $requiredJava) {
    Write-Host "Invalid Java version: $javaVersion (expected $requiredJava; JAVA_HOME=$env:JAVA_HOME)." -ForegroundColor Red
    exit 1
}
Write-Host "Java $javaVersion OK ($javaExe)" -ForegroundColor Green

# --- Files changed temporarily for the compile ---
$buildFile = "build.gradle.kts"
$propertiesFile = "src/main/resources/application.properties"
$openApiConfigFile = "src/main/kotlin/dev/joguenco/roqui/security/OpenApiConfig.kt"
$sourceFiles = @($buildFile, $propertiesFile, $openApiConfigFile)

function Remove-TextPattern {
    param([string]$Path, [string]$Pattern)

    $fullPath = Join-Path $PSScriptRoot $Path
    $content = [System.IO.File]::ReadAllText($fullPath)
    $updated = [regex]::Replace(
        $content,
        $Pattern,
        "",
        [System.Text.RegularExpressions.RegexOptions]::Multiline
    )
    [System.IO.File]::WriteAllText($fullPath, $updated, (New-Object System.Text.UTF8Encoding $false))
}

$buildExitCode = 1
try {
    Write-Host "Preparing an OpenAPI-free compile..." -ForegroundColor Cyan

    Remove-TextPattern $buildFile '[ \t]*implementation\("org\.springdoc:springdoc-openapi-starter-webmvc-ui:[^"]+"\)\r?\n'
    Remove-TextPattern $propertiesFile '(?s)^# (?:Begin )?Swagger UI configuration\r?\n.*?^springdoc\.swagger-ui\.enabled=true\r?\n(?:# End Swagger UI configuration\r?\n)?\r?\n'
    Remove-TextPattern $propertiesFile '^spring\.jpa\.hibernate\.ddl-auto=validate\r?\n'
    if (Test-Path $openApiConfigFile) {
        Remove-Item $openApiConfigFile -Force
    }

    # Fail fast if any of the temporary removals did not take effect.
    $expectedRemovals = @(
        @{ Path = $buildFile; Pattern = 'springdoc' },
        @{ Path = $propertiesFile; Pattern = 'swagger-ui' },
        @{ Path = $propertiesFile; Pattern = 'ddl-auto=validate' }
    )
    foreach ($removal in $expectedRemovals) {
        if (Select-String -Path (Join-Path $PSScriptRoot $removal.Path) -Pattern $removal.Pattern -Quiet) {
            throw "Could not remove '$($removal.Pattern)' from $($removal.Path)."
        }
    }

    Write-Host "Running gradle build..." -ForegroundColor Cyan
    gradle build
    $buildExitCode = $LASTEXITCODE
}
finally {
    Write-Host "Restoring source files to their original state..." -ForegroundColor Cyan
    git restore -- $sourceFiles
    $restoreExitCode = $LASTEXITCODE
    if ($restoreExitCode -ne 0) {
        Write-Host "Failed to restore source files (exit code $restoreExitCode)." -ForegroundColor Red
        exit $restoreExitCode
    }
}

if ($buildExitCode -ne 0) {
    Write-Host "Build failed (exit code $buildExitCode)." -ForegroundColor Red
    exit $buildExitCode
}
