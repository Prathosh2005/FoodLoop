
$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Error ".env file not found!"
    exit 1
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()

    if ($line -and -not $line.StartsWith("#")) {
        $parts = $line -split "=", 2

        if ($parts.Count -eq 2) {
            $key = $parts[0].Trim()
            $value = $parts[1].Trim()

            [Environment]::SetEnvironmentVariable(
                $key,
                $value,
                "Process"
            )
        }
    }
}

if (-not $env:DB_PASSWORD -or -not $env:FOODLOOP_JWT_SECRET) {
    Write-Error "Required environment variables are missing from .env"
    exit 1
}

Write-Host "FoodLoop environment variables loaded."
& "$PSScriptRoot\mvnw.cmd" spring-boot:run

# Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
# .\start-foodloop.ps1