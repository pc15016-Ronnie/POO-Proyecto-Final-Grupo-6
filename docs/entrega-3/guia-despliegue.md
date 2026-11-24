# Guía de despliegue - Entrega #3

## Requisitos

- Java 21 y Maven 3.9 o el wrapper incluido.
- Docker Desktop con Docker Compose.
- Kubernetes habilitado en el mismo Docker Desktop.
- `kubectl`, `act` y Postman o Newman.
- Contexto Kubernetes de Docker Desktop activo.

```bash
java -version
docker version
docker compose version
kubectl config current-context
act --version
```

## 1. Ejecución local con Maven

```bash
docker compose up -d
mvn clean verify
mvn spring-boot:run
```

Comprobar:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/v3/api-docs
```

Abrir `http://localhost:8080/swagger-ui/index.html`.

## 2. Pruebas funcionales con Postman

Importar:

- `postman/Grupo6-eventos.postman_collection.json`
- `postman/local.postman_environment.json`

Ejecutar primero **Flujo demo (en orden)** y después las carpetas de entidad. La colección final tiene
51 peticiones y 82 aserciones.

También se puede ejecutar por consola:

```bash
npx --yes newman run postman/Grupo6-eventos.postman_collection.json \
  -e postman/local.postman_environment.json
```

## 3. Docker

```bash
docker build -t eventos-api:local .
docker image inspect eventos-api:local
docker run --rm -p 8080:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/eventosdb \
  -e DB_USER=eventos \
  -e DB_PASSWORD=eventos123 \
  eventos-api:local
```

La imagen ejecuta la aplicación con un usuario no-root y verifica `/actuator/health`.

## 4. Kubernetes local

Despliegue automatizado:

```bash
./scripts/deploy-k8s.sh
```

Windows PowerShell:

```powershell
.\scripts\deploy-k8s.ps1
```

El script:

1. Levanta PostgreSQL con Compose y espera `healthy`.
2. Construye una imagen etiquetada con el SHA corto.
3. Crea o actualiza el Secret de base de datos.
4. Aplica Namespace, ConfigMap, Deployment y Service.
5. Actualiza la imagen del Deployment.
6. Espera el rollout.
7. Ejecuta `scripts/smoke-test.sh`.

Verificación manual:

```bash
kubectl -n eventos get all
kubectl -n eventos describe deployment eventos-api
kubectl -n eventos get deployment eventos-api \
  -o jsonpath='{.spec.template.spec.containers[0].image}{" "}{.spec.template.spec.containers[0].imagePullPolicy}{"\n"}'
curl http://localhost:30080/actuator/health
curl -I http://localhost:30080/swagger-ui/index.html
```

Resultados obligatorios:

- Pod `1/1 Running`.
- Rollout completado.
- Imagen reciente con etiqueta del commit.
- `imagePullPolicy` igual a `Never`.
- Health `UP` y Swagger accesible.

## 5. Pipeline con act

El Kubeconfig permanece fuera de Git:

```bash
mkdir -p .kube-local
kubectl config view --raw --minify > .kube-local/config
chmod 600 .kube-local/config
```

Ejecutar:

```bash
./scripts/run-act.sh
```

Windows PowerShell:

```powershell
.\scripts\run-act.ps1
```

El script pasa `KUBECONFIG_B64` y usa `--network host`; act conecta automáticamente el socket de Docker.
El workflow local se encuentra en
`.github/workflows/entrega-3-local.yml`; el workflow de solo calidad es `.github/workflows/entrega-3.yml`.

En Docker Desktop no hace falta configuración adicional para `host.docker.internal`. Si se usa otro clúster
compatible, se puede establecer `K8S_HOST_ALIAS_IP` al gateway del host para que el pipeline agregue un
`hostAlias` temporal. En `kind`, `KIND_NODE_NAME` permite cargar automáticamente la imagen construida en el nodo;
estas opciones no modifican los manifiestos versionados.

## Diagnóstico

| Problema | Verificación |
| :--- | :--- |
| La API no conecta con PostgreSQL | `docker compose ps`; el contenedor debe estar `healthy`. |
| Pod en `ImagePullBackOff` | La imagen debe existir localmente y la política debe ser `Never`. |
| `act` no encuentra Kubernetes | Comprobar `kubectl config current-context` y ejecutar con red del host. |
| Error de permisos del socket | Confirmar acceso a `/var/run/docker.sock`. |
| Swagger 404 | Verificar que se reconstruyó el JAR y que Springdoc está en el POM. |
| Usuario vinculado no se elimina | Es intencional: primero debe eliminarse el perfil o desvincularlo. |
