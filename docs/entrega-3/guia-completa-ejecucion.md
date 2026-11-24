# Guía completa para ejecutar, probar y demostrar la Entrega #3

**Proyecto:** API para Gestión de Eventos y Asistentes

**Grupo:** 6

**Stack:** Java 21, Spring Boot 3.3.5, Maven, PostgreSQL 16, Docker, Kubernetes y act

**Estado final:** `main` en `ab98b56`, cinco PR fusionados y 58 pruebas automatizadas.

Esta guía está pensada para cualquier integrante del equipo. Explica desde cómo descargar el proyecto hasta cómo
demostrar la API, Docker, Kubernetes, Swagger, Postman y el pipeline de `act`.

---

## 1. Resumen de lo que ya está terminado

| Elemento | Estado |
| :--- | :--- |
| CRUD de Ubicación | Terminado |
| CRUD de Evento | Terminado |
| CRUD de Asistente | Terminado |
| CRUD de Registro | Terminado |
| CRUD de Usuario | Terminado |
| CRUD de Administrador | Terminado |
| Pruebas JUnit/Mockito/MockMvc | 58 pruebas en verde |
| Swagger/OpenAPI | 32 operaciones documentadas |
| Postman | 51 peticiones y 82 aserciones |
| Docker | Imagen multietapa y usuario no-root |
| Kubernetes | Deployment, Service NodePort y `imagePullPolicy: Never` |
| CI/CD local | Workflow de GitHub Actions ejecutado con `act` |
| GitHub | PR #6, #7, #8, #9 y #10 fusionados |

---

## 2. Conceptos que deben entender antes de la defensa

### Rama

Una rama es una línea de trabajo independiente. En este proyecto:

- `main`: versión final integrada.
- `code/e3-integracion`: fotografía temporal del proyecto completo.
- `feat/e3-base`: dependencias y configuración compartida.
- `feat/e3-*`: aportes individuales.

### Pull request

Un pull request solicita fusionar una rama en `main`. Permite revisar archivos, ejecutar checks, comentar y dejar
evidencia del trabajo colaborativo.

### Imagen y etiqueta Docker

Una imagen Docker es el paquete ejecutable de la aplicación. Una etiqueta identifica una versión de esa imagen:

```text
eventos-api:e3-ab98b56
└────┬────┘ └───┬───┘
   imagen    etiqueta
```

- `eventos-api` es el nombre de la imagen.
- `e3-ab98b56` es la etiqueta.
- `ab98b56` corresponde al commit corto de `main`.

La etiqueta no es una emoción ni una calificación: es un nombre que permite saber exactamente qué versión del
código está contenida en la imagen.

### `imagePullPolicy: Never`

Significa que Kubernetes no intentará descargar la imagen desde Docker Hub. Utilizará una imagen que ya exista
localmente en el motor de Docker Desktop. Si la imagen no existe, el Pod muestra `ErrImageNeverPull`.

### Kubeconfig

Es el archivo con las credenciales y la dirección del clúster. Se copia a `.kube-local/config` para que `act`
pueda conectarse. No debe subirse a GitHub.

### `act`

`act` ejecuta localmente un workflow de GitHub Actions. En este proyecto ejecuta:

1. Compilación y pruebas.
2. Construcción de imagen.
3. Despliegue en Kubernetes.
4. Smoke tests.

---

## 3. Requisitos de la computadora

| Herramienta | Verificación |
| :--- | :--- |
| Java 21 | `java -version` |
| Maven 3.9 o wrapper | `mvn -version` o `./mvnw -version` |
| Docker Desktop | `docker version` |
| Docker Compose | `docker compose version` |
| Kubernetes | `kubectl version --client` |
| Postman o Node.js | Importar colección o ejecutar Newman |
| act | `act --version` |

En Linux/macOS se pueden usar los comandos Bash de esta guía. En Windows se recomienda Git Bash; también se
incluyen indicaciones PowerShell.

---

## 4. Descargar el proyecto

Si todavía no existe la carpeta:

```bash
git clone https://github.com/pc15016-Ronnie/POO-Proyecto-Final-Grupo-6.git
cd POO-Proyecto-Final-Grupo-6
```

Para actualizar una copia existente:

```bash
git fetch origin
git switch main
git pull --ff-only origin main
git status
git log -1 --oneline
```

El commit final esperado es:

```text
ab98b56 Merge pull request #10 from pc15016-Ronnie/feat/e3-docker-k8s
```

No importa que el texto visible sea el mensaje del merge; la parte importante es `ab98b56`.

---

## 5. Levantar PostgreSQL

El proyecto incluye `docker-compose.yml` con PostgreSQL 16:

```bash
docker compose up -d postgres
docker compose ps
```

Resultado esperado:

```text
eventos-db postgres:16-alpine Up ... (healthy) 0.0.0.0:5432->5432/tcp
```

Credenciales locales:

```text
Base: eventosdb
Usuario: eventos
Contraseña: eventos123
Puerto: 5432
```

Si se quiere iniciar desde cero:

```bash
docker compose down -v
docker compose up -d postgres
```

**Advertencia:** `down -v` elimina el volumen y todos los datos locales de prueba.

---

## 6. Compilar y ejecutar las pruebas

```bash
mvn clean verify
```

Resultado esperado:

```text
Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Se genera:

```text
target/eventos-api.jar
```

También se puede usar el wrapper:

```bash
./mvnw clean verify
```

---

## 7. Ejecutar la API en local

```bash
java -jar target/eventos-api.jar
```

También:

```bash
mvn spring-boot:run
```

Mensajes esperados:

```text
Database: jdbc:postgresql://localhost:5432/eventosdb (PostgreSQL 16)
Successfully validated 2 migrations
Tomcat started on port 8080
Started EventosApiApplication
```

Comprobar:

```bash
curl http://localhost:8080/actuator/health
```

Respuesta:

```json
{"status":"UP","groups":["liveness","readiness"]}
```

---

## 8. Abrir Swagger

Navegador:

```text
http://localhost:8080/swagger-ui/index.html
```

Contrato OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Debe mostrar etiquetas:

- Ubicaciones
- Eventos
- Asistentes
- Registros
- Usuarios
- Administradores

El contrato contiene 32 operaciones HTTP.

---

## 9. Ejecutar Postman

### Interfaz gráfica

Importar:

- `postman/Grupo6-eventos.postman_collection.json`
- `postman/local.postman_environment.json`

Seleccionar el entorno **Grupo 6 - Local** y ejecutar la colección completa.

Resultado:

```text
Requests: 51
Assertions: 82
Failed: 0
```

### Newman

Con Node.js instalado:

```bash
npx --yes newman run postman/Grupo6-eventos.postman_collection.json \
  -e postman/local.postman_environment.json
```

La colección crea datos de prueba. Si se desea dejar la base limpia después, ejecutar el reinicio con volumen.

---

## 10. Construir y ejecutar Docker

Construir imagen local:

```bash
docker build -t eventos-api:local .
```

Ejecutar el contenedor:

```bash
docker run --rm -p 8081:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/eventosdb \
  -e DB_USER=eventos \
  -e DB_PASSWORD=eventos123 \
  eventos-api:local
```

Probar:

```bash
curl http://localhost:8081/actuator/health
docker ps
docker inspect --format '{{.Config.User}}' eventos-api:local
```

La aplicación dentro de la imagen se ejecuta como UID/GID `10001`, no como root.

---

## 11. Habilitar Kubernetes en Docker Desktop

En Docker Desktop:

1. Abrir **Settings**.
2. Entrar a **Kubernetes**.
3. Activar **Enable Kubernetes**.
4. Aplicar y reiniciar.
5. Esperar a que Kubernetes aparezca en verde.

Verificar:

```bash
kubectl config current-context
kubectl cluster-info
```

El contexto normalmente se llama `docker-desktop`.

---

## 12. Preparar el Kubeconfig local

Linux/macOS/Git Bash:

```bash
mkdir -p .kube-local
kubectl config view --raw --minify > .kube-local/config
chmod 600 .kube-local/config
```

PowerShell:

```powershell
New-Item -ItemType Directory -Force .kube-local
kubectl config view --raw --minify | Set-Content -Encoding ascii .kube-local/config
```

`.kube-local/` está en `.gitignore`. Nunca debe subirse.

---

## 13. Desplegar con Kubernetes

Asegurarse de tener PostgreSQL local:

```bash
docker compose up -d postgres
```

Después:

```bash
./scripts/deploy-k8s.sh
```

PowerShell:

```powershell
.\scripts\deploy-k8s.ps1
```

El script:

1. Espera PostgreSQL.
2. Construye una etiqueta basada en el commit. Con `ab98b56`, la imagen manual será `eventos-api:ab98b56`.
3. Crea el Secret.
4. Aplica Namespace, ConfigMap, Deployment y Service.
5. Espera el rollout.
6. Ejecuta los smoke tests.

Verificar:

```bash
kubectl -n eventos get pods,deploy,svc
kubectl -n eventos get deployment eventos-api \
  -o jsonpath='{.spec.template.spec.containers[0].image}{" "}{.spec.template.spec.containers[0].imagePullPolicy}{"\n"}'
curl http://localhost:30080/actuator/health
```

Resultados esperados:

```text
Pod: 1/1 Running
Deployment: 1/1
Service: NodePort 80:30080/TCP
imagePullPolicy: Never
```

Si aparece `ErrImageNeverPull`, revisar que la imagen exista en Docker Desktop y que el Deployment use la etiqueta
correcta.

---

## 14. Ejecutar el pipeline completo con act

Linux/macOS/Git Bash:

```bash
./scripts/run-act.sh
```

PowerShell:

```powershell
.\scripts\run-act.ps1
```

El workflow local está en:

```text
.github/workflows/entrega-3-local.yml
```

El pipeline construye:

```text
eventos-api:e3-ab98b56
```

La etiqueta sigue el patrón:

```text
e3-<primeros 7 caracteres del commit>
```

Cuando `act` termina correctamente:

- Las 58 pruebas están en verde.
- La imagen se construye y actualiza.
- El Deployment apunta a la nueva imagen.
- El rollout termina correctamente.
- `imagePullPolicy` se valida como `Never`.
- Health, OpenAPI, Swagger y `/api/eventos` responden.

Verificar la imagen:

```bash
docker images eventos-api
```

Verificar la imagen usada por Kubernetes:

```bash
kubectl -n eventos get deployment eventos-api \
  -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
```

---

## 15. Capturas recomendadas para la tutoría

Antes de la defensa, preparar estas capturas:

### Captura 1: versión final de Git

Ejecutar:

```bash
git switch main
git pull --ff-only origin main
git log -1 --oneline
```

Debe aparecer `ab98b56`.

### Captura 2: pruebas

Ejecutar:

```bash
mvn clean verify
```

Capturar el resumen con 58 pruebas y `BUILD SUCCESS`.

### Captura 3: PostgreSQL

Ejecutar:

```bash
docker compose ps
```

Capturar `eventos-db` como `healthy`.

### Captura 4: pipeline con act

Ejecutar `./scripts/run-act.sh` y capturar:

- Job de pruebas en verde.
- Job de despliegue en verde.
- Smoke tests exitosos.

### Captura 5: imagen en Docker Desktop

Abrir Docker Desktop, sección **Images**, buscar `eventos-api`.

La captura debe mostrar una etiqueta basada en el commit, preferentemente:

```text
eventos-api:e3-ab98b56
```

También es válida la imagen construida por el script manual:

```text
eventos-api:ab98b56
```

### Captura 6: Kubernetes en Docker Desktop

Abrir Docker Desktop, sección **Kubernetes > Workloads > Pods**.

Debe aparecer:

```text
eventos-api-...  1/1  Running
```

En una terminal capturar además:

```bash
kubectl -n eventos get pods,deploy,svc
```

### Captura 7: Swagger

Abrir:

```text
http://localhost:30080/swagger-ui/index.html
```

Debe observarse la documentación de las seis entidades.

### Captura 8: Postman

Ejecutar la colección completa y capturar:

```text
Requests: 51
Assertions: 82
Failed: 0
```

### Captura 9: GitHub

Capturar:

- PR #6 a #10 fusionados.
- Pull Requests cerrados y aprobados.
- Contributors con los cuatro integrantes.

---

## 16. Flujo corto para la demostración

1. Mostrar `git log -1 --oneline` en `main`.
2. Mostrar PostgreSQL `healthy`.
3. Ejecutar `mvn clean verify`.
4. Abrir Swagger.
5. Ejecutar Postman.
6. Mostrar Docker Desktop con la imagen etiquetada.
7. Mostrar el Pod `1/1 Running`.
8. Mostrar `imagePullPolicy: Never`.
9. Mostrar la ejecución completa de `act`.
10. Mostrar PRs, revisiones y Contributors.

---

## 17. Explicación breve para los compañeros

> La aplicación se construyó con Spring Boot y seis entidades. La lógica está separada en Controller, Service y
> Repository. PostgreSQL conserva las relaciones y Flyway crea el esquema. Dos reglas críticas son el control de
> capacidad y la inscripción única por evento. Los DTO evitan exponer entidades y el manejo global devuelve errores
> uniformes. Las 58 pruebas verifican las reglas principales. Swagger documenta las 32 operaciones y Postman valida
> 51 flujos con 82 aserciones. Docker empaqueta la aplicación. Kubernetes usa una imagen local con
> `imagePullPolicy: Never` y NodePort 30080. `act` automatiza pruebas, imagen, despliegue y smoke tests.

---

## 18. Problemas frecuentes

| Problema | Solución |
| :--- | :--- |
| `Connection refused` en PostgreSQL | `docker compose up -d`; revisar `healthy`. |
| Puerto 5432 ocupado | Detener otro PostgreSQL o cambiar el puerto y `DB_URL`. |
| Puerto 8080 ocupado | Detener el proceso o usar `--server.port=8081`. |
| Swagger 404 | Recompilar después de actualizar dependencias. |
| `imagePullPolicy: Never` con error | Construir la imagen en el mismo Docker Desktop. |
| Pod en `CreateContainerConfigError` | Revisar Secret y variables. |
| Pod no conecta con DB | Confirmar `host.docker.internal` y PostgreSQL `healthy`. |
| act no alcanza Kubernetes | Usar red del host y Kubeconfig correcto. |
| Checksum de Flyway | Reiniciar volumen solo si acepta perder datos locales. |
| Rama atrasada | `git fetch origin` y `git pull --ff-only origin main`. |

---

## 19. Apagar servicios

Eliminar Deployment y Service de Kubernetes:

```bash
kubectl delete -f k8s/service.yaml
kubectl delete -f k8s/deployment.yaml
kubectl delete -f k8s/configmap.yaml
kubectl delete -f k8s/namespace.yaml
```

Detener PostgreSQL sin borrar datos:

```bash
docker compose down
```

Detener y borrar los datos locales de prueba:

```bash
docker compose down -v
```

La segunda opción es destructiva y debe usarse solo cuando el equipo esté de acuerdo.
