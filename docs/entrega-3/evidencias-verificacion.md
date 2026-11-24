# Evidencias de verificación

**Fecha de ejecución:** 15 de noviembre de 2026  
**Entorno:** Linux, Java 21.0.13, Maven 3.9.9, Docker Engine 29.9.3.

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

## Validación pendiente del equipo antes de la tutoría

- Repetir el despliegue en el Docker Desktop de la máquina de presentación.
- Publicar las cuatro ramas y abrir los pull requests con revisión cruzada.
- Confirmar que las cuatro cuentas aparecen en GitHub Contributors.
- Reiniciar la base si se desea iniciar la demostración con solo los datos de Flyway.
