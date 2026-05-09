$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Set-Location $root

$scanFiles = Get-ChildItem -Recurse -File -Force |
    Where-Object {
        $_.FullName -notmatch "\\.git\\" -and
        $_.FullName -notmatch "\\.gradle\\" -and
        $_.FullName -notmatch "\\build\\" -and
        $_.FullName -notmatch "\\.agents\\settings.json" -and
        $_.FullName -notmatch "\\.agents\\hooks\\preflight.ps1"
    }

$forbidden = @(
    ("Just " + "Hammers"),
    ("just" + "hammers"),
    ("just" + "-hammers"),
    ("Error" + "Mikey"),
    ("Curse" + "Forge"),
    ("curse" + "forge")
)

$hits = foreach ($term in $forbidden) {
    $scanFiles | Select-String -SimpleMatch -Pattern $term | ForEach-Object {
        "{0}:{1}: {2}" -f $_.Path, $_.LineNumber, $_.Line.Trim()
    }
}

if ($hits) {
    Write-Host "Forbidden legacy references found:" -ForegroundColor Red
    $hits | ForEach-Object { Write-Host $_ }
    exit 1
}

$requiredPaths = @(
    "common/src/main/resources/assets/proximahammers/textures/item",
    "common/src/main/resources/assets/proximahammers/lang",
    "common/src/main/generated/data/proximahammers/recipe",
    "common/src/main/generated/assets/proximahammers/models/item",
    "fabric/src/main/resources/proximahammers.mixins.json",
    "neoforge/src/main/resources/proximahammers.mixins.json"
)

foreach ($path in $requiredPaths) {
    if (-not (Test-Path -LiteralPath (Join-Path $root $path))) {
        Write-Host "Missing required path: $path" -ForegroundColor Red
        exit 1
    }
}

$previousErrorActionPreference = $ErrorActionPreference
$ErrorActionPreference = "Continue"
$javaVersionOutput = & java -version 2>&1
$ErrorActionPreference = $previousErrorActionPreference
$javaMajor = $null
foreach ($line in $javaVersionOutput) {
    if ($line -match 'version "([0-9]+)') {
        $javaMajor = [int]$matches[1]
        break
    }
}

if ($null -eq $javaMajor -or $javaMajor -lt 25) {
    Write-Host "JDK 25 or newer is required. Current java -version output:" -ForegroundColor Red
    $javaVersionOutput | ForEach-Object { Write-Host $_ }
    exit 1
}

& .\gradlew.bat build
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Preflight passed for Proxima Hammers." -ForegroundColor Green
