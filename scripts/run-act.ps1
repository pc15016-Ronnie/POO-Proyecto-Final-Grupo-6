$ErrorActionPreference = "Stop"

$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $ProjectRoot

if (-not (Get-Command act -ErrorAction SilentlyContinue)) {
    throw "Instala act: https://nektosact.com/installation/"
}
if (-not (Test-Path ".kube-local/config")) {
    throw "Falta .kube-local/config. Ejecuta: kubectl config view --raw --minify > .kube-local/config"
}

$KubeConfigB64 = [Convert]::ToBase64String(
    [System.IO.File]::ReadAllBytes((Resolve-Path ".kube-local/config")))
$DbPassword = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "eventos123" }
$ActArguments = @(
    "workflow_dispatch",
    "--workflows", ".github/workflows/entrega-3-local.yml",
    "--network", "host",
    "--secret", "KUBECONFIG_B64=$KubeConfigB64",
    "--secret", "DB_PASSWORD=$DbPassword"
)
if ($env:K8S_HOST_ALIAS_IP) {
    $ActArguments += @("--secret", "K8S_HOST_ALIAS_IP=$env:K8S_HOST_ALIAS_IP")
}
if ($env:KIND_NODE_NAME) {
    $ActArguments += @("--secret", "KIND_NODE_NAME=$env:KIND_NODE_NAME")
}

act @ActArguments
