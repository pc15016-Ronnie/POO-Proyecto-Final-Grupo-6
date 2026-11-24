#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${PROJECT_ROOT}"

IMAGE_NAME="${IMAGE_NAME:-eventos-api}"
IMAGE_TAG="${IMAGE_TAG:-$(git rev-parse --short HEAD)}"
DB_USER="${DB_USER:-eventos}"
DB_PASSWORD="${DB_PASSWORD:-eventos123}"

for command_name in docker kubectl curl git; do
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "Falta la herramienta obligatoria: ${command_name}" >&2
    exit 1
  fi
done

echo "Contexto Kubernetes: $(kubectl config current-context)"
docker compose up -d postgres

echo "Esperando PostgreSQL saludable..."
for attempt in $(seq 1 30); do
  health="$(docker inspect --format '{{.State.Health.Status}}' eventos-db 2>/dev/null || true)"
  if [[ "${health}" == "healthy" ]]; then
    break
  fi
  if [[ "${attempt}" -eq 30 ]]; then
    echo "PostgreSQL no alcanzo el estado healthy." >&2
    exit 1
  fi
  sleep 2
done

echo "Construyendo ${IMAGE_NAME}:${IMAGE_TAG}"
docker build --tag "${IMAGE_NAME}:${IMAGE_TAG}" .
docker image inspect "${IMAGE_NAME}:${IMAGE_TAG}" >/dev/null

kubectl apply -f k8s/namespace.yaml
kubectl --namespace eventos create secret generic eventos-db-secret \
  --from-literal=DB_USER="${DB_USER}" \
  --from-literal=DB_PASSWORD="${DB_PASSWORD}" \
  --dry-run=client --output=yaml | kubectl apply -f -

kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl --namespace eventos set image deployment/eventos-api \
  api="${IMAGE_NAME}:${IMAGE_TAG}"
kubectl --namespace eventos rollout status deployment/eventos-api --timeout=180s

BASE_URL="http://localhost:30080" "${PROJECT_ROOT}/scripts/smoke-test.sh"
echo "Despliegue completado: ${IMAGE_NAME}:${IMAGE_TAG}"
