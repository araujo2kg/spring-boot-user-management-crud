$ErrorActionPreference = 'Stop'

Push-Location $PSScriptRoot
try {
    if (-not (Test-Path .env)) {
        Copy-Item .env.example .env
        throw 'Created .env. Set its passwords, then run this script again.'
    }

    docker compose up -d --wait --wait-timeout 120 mysql
    if ($LASTEXITCODE -ne 0) { throw 'MySQL failed to start. Check Docker and container status.' }
}
finally {
    Pop-Location
}
