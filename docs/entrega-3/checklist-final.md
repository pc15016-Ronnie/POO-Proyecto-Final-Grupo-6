# Checklist final - Entrega #3

## Código y pruebas

- [x] `git status` revisado y sin archivos accidentales.
- [x] `mvn clean verify` termina con 58 pruebas en verde.
- [x] Swagger carga en `/swagger-ui/index.html`.
- [x] OpenAPI contiene los endpoints de las seis entidades.
- [x] Las respuestas no incluyen contraseñas.
- [x] Postman ejecuta 51 peticiones y 82 aserciones sin fallos.

## Base de datos

- [x] `eventos-db` está `healthy`.
- [x] Flyway aplica V1 y V2.
- [x] Registro duplicado activo devuelve `409`.
- [x] Cancelación libera cupo y permite reinscribirse.
- [x] Usuario con perfil no puede eliminarse.
- [x] Administrador y Asistente validan el rol del usuario.

## Docker y Kubernetes

- [ ] Imagen construida con el SHA actual.
- [x] Pod `eventos-api` en `1/1 Running`.
- [x] Deployment actualizado y rollout exitoso.
- [x] Service NodePort `30080` responde.
- [x] `imagePullPolicy` es `Never`.
- [x] Health `UP`, Swagger y API accesibles desde Kubernetes.
- [x] Secret no está versionado.

## CI/CD

- [x] `.kube-local/config` existe y está ignorado.
- [x] `act` conecta el socket de Docker y usa red del host.
- [x] Job `verify` termina con éxito.
- [x] Job `deploy-local` termina con éxito.
- [x] El pipeline actualiza la imagen y el Deployment.
- [x] Smoke tests pasan.

## GitHub y documentación

- [x] Cada integrante subió su rama y abrió su PR.
- [x] Cada PR tiene el revisor cruzado asignado.
- [x] Los cuatro integrantes aparecen en Contributors.
- [x] README actualizado.
- [x] Documentos de `docs/entrega-3` revisados.
- [ ] Los cuatro integrantes ensayaron la demo completa.

## Defensa

- [ ] Docker Desktop inicia antes de la tutoría.
- [ ] Base reiniciada y sembrada.
- [ ] API y Swagger abiertos.
- [ ] Postman importado con el entorno correcto.
- [ ] Kubernetes abierto en terminal.
- [ ] GitHub abierto en Pull Requests y Contributors.
- [ ] Plan alterno preparado con capturas de logs y respuestas.
