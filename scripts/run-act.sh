#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}"

if ! command -v act >/dev/null 2>&1; then
  echo "Instala act: https://nektosact.com/installation/" >&2
  exit 1
fi
if [[ ! -f .kube-local/config ]]; then
  echo "Falta .kube-local/config. Ejecuta:" >&2
  echo "  mkdir -p .kube-local" >&2
  echo "  kubectl config view --raw --minify > .kube-local/config" >&2
  exit 1
fi

kubeconfig_b64="$(base64 -w 0 .kube-local/config)"
db_password="${DB_PASSWORD:-eventos123}"

act_args=(
  workflow_dispatch
  --workflows .github/workflows/entrega-3-local.yml
  --network host
  --secret "KUBECONFIG_B64=${kubeconfig_b64}"
  --secret "DB_PASSWORD=${db_password}"
)

if [[ -n "${K8S_HOST_ALIAS_IP:-}" ]]; then
  act_args+=(--secret "K8S_HOST_ALIAS_IP=${K8S_HOST_ALIAS_IP}")
fi
if [[ -n "${KIND_NODE_NAME:-}" ]]; then
  act_args+=(--secret "KIND_NODE_NAME=${KIND_NODE_NAME}")
fi

exec act "${act_args[@]}"
