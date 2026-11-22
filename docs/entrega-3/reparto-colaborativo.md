# Reparto colaborativo - Entrega #3

## Objetivo

Distribuir la implementación completa en ramas individuales, con commits propios, pull request y revisión
cruzada. Ninguna persona debe limitarse a aprobar: cada rama debe contener código, pruebas o infraestructura
verificable.

## Preparación de la rama de integración

La versión completa se conserva temporalmente en `code/e3-integracion`. Esto permite repartir cada aporte sin
perder el trabajo local ya realizado.

```bash
git checkout -b code/e3-integracion
git add .
git commit -m "feat(e3): integracion completa de la entrega 3"
git push -u origin code/e3-integracion
```

El primer PR, `feat/e3-base`, sale de `main` e incorpora únicamente la infraestructura compartida:

- Dependencias de Springdoc, Actuator y Spring Security Crypto.
- `OpenApiConfig`, `PasswordConfig`, `PersonaRepository` y métodos de `UsuarioRepository`.
- Configuración de Actuator/OpenAPI y `.gitignore`.
- Nombre final del JAR `eventos-api.jar`.

Responsable: Carlos Enoc Calzada. Revisor: Ronnie Portillo.

## Ramas individuales

Después de fusionar `feat/e3-base`, cada integrante crea su rama desde el nuevo `main`.

| Integrante | Rama | Archivos principales | Revisor |
| :--- | :--- | :--- | :--- |
| Ángel Serrano | `feat/e3-usuario-swagger` | DTOs, mapper, servicio y controlador de Usuario; anotaciones Swagger; pruebas de Usuario y Asistente; casos Postman | Azucena Castillo |
| Azucena Castillo | `feat/e3-administrador-db` | DTOs, mapper, repositorio, servicio y controlador de Administrador; reglas de correo; pruebas de Administrador y Ubicación | Ángel Serrano |
| Ronnie Portillo | `feat/e3-docker-k8s` | Dockerfile, `.dockerignore`, manifiestos K8s, scripts de despliegue/smoke test y pruebas de Evento | Enoc Calzada |
| Enoc Calzada | `feat/e3-act-ci-docs` | Workflow, scripts de act, pruebas HTTP y Registro, colección final, README y documentos | Ronnie Portillo |

Para recuperar los archivos de una rama de integración sin copiar trabajo de otros:

```bash
git checkout main
git pull --ff-only origin main
git checkout -b feat/e3-<modulo>
git checkout origin/code/e3-integracion -- <lista-exacta-de-archivos>
git status
git add <lista-exacta-de-archivos>
git commit -m "feat(e3): <descripcion del aporte>"
git push -u origin feat/e3-<modulo>
```

No usar `git add .` si la rama de integración contiene archivos que pertenecen a otros integrantes.

## Orden de integración

1. `feat/e3-base`
2. `feat/e3-usuario-swagger`
3. `feat/e3-administrador-db`
4. `feat/e3-docker-k8s`
5. `feat/e3-act-ci-docs`

Si `main` cambió mientras una rama estaba abierta:

```bash
git fetch origin
git rebase origin/main
```

Resolver conflictos con revisión del compañero asignado y ejecutar `mvn clean verify` antes de actualizar el PR.

## Contenido mínimo de cada PR

- Resumen del aporte y tecnologías utilizadas.
- Lista de pruebas ejecutadas y resultado.
- Captura o salida de Postman si el PR modifica endpoints.
- Confirmación de que Swagger sigue accesible.
- Riesgos o limitaciones conocidas.
- Revisor asignado y aprobación.

## Evidencia de participación

Antes de la tutoría, abrir en GitHub:

- **Insights > Contributors**: los cuatro integrantes deben mostrar aportes.
- **Pull requests > Closed**: deben aparecer los PR con revisión y merge.
- **Commits de `main`**: deben incluir aportes de las cuatro cuentas.

El tutor puede preguntar por cualquier archivo a cualquiera; el objetivo del reparto es trazabilidad, no reducir
el conocimiento del equipo.
