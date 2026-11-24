# Evidencias de verificación

**Fecha de ejecución:** 15 de noviembre de 2026  
**Entorno:** Linux, Java 21.0.13, Maven 3.9.9, Docker Engine 29.9.3.

**Actualización final:** 23 de noviembre de 2026, `main` en `ab98b56`.

## Compilación y pruebas

```text
Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

El comando `mvn clean verify` generó `target/eventos-api.jar`.

Distribución:

| Suite | Pruebas |
| :--- | ---: |
| ApiErrorHandlingTest | 8 |
| UbicacionServiceTest | 9 |
| EventoServiceTest | 9 |
| UsuarioServiceTest | 6 |
| AsistenteServiceTest | 7 |
| AdministradorServiceTest | 6 |
| RegistroServiceTest | 13 |

## Swagger y OpenAPI

- `/actuator/health`: `UP`.
- `/swagger-ui/index.html`: HTTP `200`.
- `/v3/api-docs`: 16 rutas y 32 operaciones.
- Etiquetas: Usuarios, Registros, Administradores, Asistentes, Eventos y Ubicaciones.
- `UsuarioRequestDTO.password.writeOnly`: `true`.

## Postman

Contra la aplicación local:

```text
requests:    51
assertions: 82
failed:      0
```

La misma colección se ejecutó posteriormente contra Kubernetes mediante
`http://localhost:30080` con el mismo resultado.

## Docker

- Imagen `eventos-api:e3-local` construida correctamente.
- Contenedor ejecutado como UID/GID `10001`.
- Estado del healthcheck: `healthy`.
- `/actuator/health`: `UP`.

## Kubernetes

El host de validación no tenía Docker Desktop, por lo que se creó un clúster temporal compatible con Kubernetes
1.33.1. Se validaron los manifiestos y, mediante parámetros temporales de entorno, la conexión al PostgreSQL del host.

```text
pod/eventos-api  1/1  Running
deployment eventos-api  1/1
service eventos-api  NodePort 80:30080/TCP
imagePullPolicy: Never
runAsUser: 10001
health: UP
Swagger UI: HTTP 200
```

La opción `K8S_HOST_ALIAS_IP` solo fue necesaria para el clúster alternativo. Docker Desktop resuelve directamente
`host.docker.internal` según la configuración entregada.

## Pipeline con act

Se utilizó `act 0.2.97`. Ambos jobs terminaron correctamente:

```text
Compilar y probar: SUCCESS
Construir imagen y desplegar en Kubernetes local: SUCCESS
```

Resultados del pipeline:

- 58 pruebas JUnit en verde.
- Imagen local construida con etiqueta `e3-<sha>`.
- Imagen importada/cargada en el nodo local.
- Deployment actualizado automáticamente.
- Rollout completado.
- `imagePullPolicy` verificado como `Never`.
- Health, OpenAPI, Swagger y endpoint de eventos verificados.
- Smoke tests completados.

## Integración final de GitHub

Los cuatro aportes individuales y la base compartida fueron fusionados:

| PR | Rama | Resultado |
| :---: | :--- | :--- |
| #6 | `feat/e3-base` | Fusionado |
| #7 | `feat/e3-act-ci-docs` | Fusionado |
| #8 | `feat/e3-usuario-swagger` | Fusionado |
| #9 | `feat/e3-administrador-db` | Fusionado |
| #10 | `feat/e3-docker-k8s` | Fusionado |

La verificación repetida sobre el merge final `ab98b56` confirmó:

- 58 pruebas JUnit en verde.
- 32 operaciones OpenAPI.
- Swagger y Actuator accesibles.
- Postman con 51 peticiones y 82 aserciones, sin fallos.

## Validación final en Docker Desktop

- Ejecutar `./scripts/run-act.sh` en la máquina de presentación y guardar la captura del pipeline.
- Confirmar que la imagen generada se llama `eventos-api:e3-ab98b56`.
- Capturar Docker Desktop mostrando la imagen y el Pod en ejecución.
- Reiniciar la base si se desea iniciar la demostración con solo los datos de Flyway.
