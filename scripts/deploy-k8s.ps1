$ErrorActionPreference = "Stop"

$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $ProjectRoot

$ImageName = if ($env:IMAGE_NAME) { $env:IMAGE_NAME } else { "eventos-api" }
$ImageTag = if ($env:IMAGE_TAG) { $env:IMAGE_TAG } else { (git rev-parse --short HEAD).Trim() }
$DbUser = if ($env:DB_USER) { $env:DB_USER } else { "eventos" }
$DbPassword = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "eventos123" }

docker compose up -d postgres

$healthy = $false
for ($attempt = 1; $attempt -le 30; $attempt++) {
    $health = docker inspect --format "{{.State.Health.Status}}" eventos-db 2>$null
    if ($health -eq "healthy") {
        $healthy = $true
        break
    }
    Start-Sleep -Seconds 2
}
if (-not $healthy) {
    throw "PostgreSQL no alcanzo el estado healthy."
}

docker build --tag "${ImageName}:${ImageTag}" .

kubectl apply -f k8s/namespace.yaml
@"
apiVersion: v1
kind: Secret
metadata:
  name: eventos-db-secret
  namespace: eventos
type: Opaque
stringData:
  DB_USER: "$DbUser"
  DB_PASSWORD: "$DbPassword"
"@ | kubectl apply -f -

kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl --namespace eventos set image deployment/eventos-api "api=${ImageName}:${ImageTag}"
kubectl --namespace eventos rollout status deployment/eventos-api --timeout=180s

Invoke-WebRequest -UseBasicParsing "http://localhost:30080/actuator/health" | Out-Null
Invoke-WebRequest -UseBasicParsing "http://localhost:30080/swagger-ui/index.html" | Out-Null
Write-Host "Despliegue completado: ${ImageName}:${ImageTag}"
