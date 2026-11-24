#!/usr/bin/env bash
set -Eeuo pipefail

BASE_URL="${BASE_URL:-http://localhost:30080}"

echo "Verificando health en ${BASE_URL}/actuator/health"
curl --fail --silent --show-error --retry 20 --retry-delay 3 \
  "${BASE_URL}/actuator/health" | grep -q '"status":"UP"'

echo "Verificando OpenAPI"
curl --fail --silent --show-error "${BASE_URL}/v3/api-docs" \
  | grep -q '"openapi"'

echo "Verificando Swagger UI"
curl --fail --silent --show-error --output /dev/null \
  "${BASE_URL}/swagger-ui/index.html"

echo "Verificando endpoint de eventos"
curl --fail --silent --show-error --output /dev/null \
  "${BASE_URL}/api/eventos"

echo "Smoke tests completados correctamente."
