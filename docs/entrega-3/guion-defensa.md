# Guion de defensa - Entrega #3

**Duración objetivo:** 12 minutos de demostración y 5 minutos de preguntas.

El tutor puede elegir a cualquier integrante. Cada bloque tiene responsable y suplente, pero todos deben ensayar
las seis secciones.

| Bloque | Responsable | Suplente |
| :--- | :--- | :--- |
| Arquitectura y código | Ángel Serrano | Enoc Calzada |
| Base de datos y relaciones | Azucena Castillo | Ronnie Portillo |
| Demo Postman | Enoc Calzada | Ángel Serrano |
| Pruebas JUnit y errores | Enoc Calzada | Azucena Castillo |
| Docker y Kubernetes | Ronnie Portillo | Enoc Calzada |
| Pipeline con act y GitHub | Ronnie Portillo | Ángel Serrano |

## 1. Arquitectura y código

Explicar:

- Spring Boot 3.3.5, Java 21, Maven y PostgreSQL 16.
- Controller recibe DTO, Service aplica reglas y transacciones, Repository accede a JPA.
- Los controladores no contienen lógica de negocio.
- `RegistroService` bloquea la fila del evento con `PESSIMISTIC_WRITE`.
- Usuario usa BCrypt y una respuesta sin `passwordHash`.

## 2. Base de datos

Mostrar:

- `docker compose ps` con `eventos-db` en `healthy`.
- Tablas `persona`, `administrador`, `asistente`, `usuario`, `ubicacion`, `evento` y `registro`.
- Herencia JOINED de Persona.
- Índice único parcial `uq_registro_activo`.
- Relación `persona.usuario_id` única.

Consulta sugerida:

```sql
SELECT p.email, p.tipo_persona, p.usuario_id
FROM persona p
ORDER BY p.id;

SELECT evento_id, asistente_id, estado
FROM registro
ORDER BY evento_id, asistente_id;
```

## 3. Demo Postman

Secuencia:

1. Crear ubicación y evento.
2. Publicar evento.
3. Crear asistente y registrar.
4. Consultar disponibilidad.
5. Intentar duplicado: `409`.
6. Cancelar y reinscribir.
7. Crear Usuario y Administrador, vincularlos y consultar ambos.
8. Intentar eliminar el usuario mientras tiene perfil: `409`.
9. Eliminar perfil y después usuario.

Cambiar `baseUrl` a `http://localhost:30080` para demostrar la misma colección contra Kubernetes.

## 4. JUnit y errores

Ejecutar:

```bash
mvn clean verify
```

Resultado esperado:

```text
Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Explicar que `ApiErrorHandlingTest` usa MockMvc y demuestra 400, 404, 405, 409, 415 y 500. El detalle técnico
del 500 queda en logs, no en la respuesta HTTP.

## 5. Docker y Kubernetes

Mostrar:

```bash
docker image inspect eventos-api:<tag>
kubectl -n eventos get pods,deploy,svc
kubectl -n eventos rollout status deployment/eventos-api
curl http://localhost:30080/actuator/health
```

Explicar por qué se usa `imagePullPolicy: Never`: la imagen se construye localmente y Docker Desktop la comparte
con el nodo local. PostgreSQL sigue en Compose y se alcanza mediante `host.docker.internal`.

## 6. Pipeline y GitHub

Mostrar:

- `.github/workflows/entrega-3.yml` y `.github/workflows/entrega-3-local.yml`.
- `scripts/run-act.sh` o `.ps1`.
- Ejecución de `verify` y `deploy-local`.
- `.kube-local/config` no aparece en Git.
- Pull requests y revisión cruzada.

## Preguntas previsibles

| Pregunta | Respuesta corta |
| :--- | :--- |
| ¿Por qué Persona no tiene endpoint? | Es una superclase abstracta; se administran sus especializaciones Administrador y Asistente. |
| ¿Cómo evitan sobrecupo concurrente? | La transacción bloquea la fila del evento antes de contar registros e insertar. |
| ¿Por qué la contraseña no se devuelve? | El DTO de respuesta no posee ese campo y el DTO de entrada lo marca como `WRITE_ONLY`. |
| ¿Por qué no se guarda el cupo? | Es derivado; se calcula con capacidad menos registros activos para evitar desincronización. |
| ¿Por qué no se puede borrar un usuario vinculado? | La FK protege la relación 1:0..1 y la API devuelve `409` con una acción clara. |
| ¿Por qué Kubernetes no descarga la imagen? | Se usa una imagen local compartida y `imagePullPolicy: Never`. |
| ¿Por qué act necesita red del host? | El runner es un contenedor y debe alcanzar NodePort/Kubernetes del host. |
| ¿Qué evidencia de colaboración tienen? | Ramas, commits, PR revisados, Contributors y documentación por responsable. |
