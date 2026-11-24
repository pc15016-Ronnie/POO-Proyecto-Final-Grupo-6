# Matriz de rúbrica - Entrega #3

| Criterio de Excelencia | Evidencia implementada | Verificación |
| :--- | :--- | :--- |
| Todos los endpoints de entidades UML son funcionales | CRUD de Ubicación, Evento, Asistente, Registro, Usuario y Administrador; 32 operaciones | Ejecutar Postman y revisar `/v3/api-docs` |
| Relaciones de base de datos | Ubicación 1:N Evento, Evento N:M Asistente vía Registro, Persona 1:0..1 Usuario | Demo de registro, perfiles, borrados y consultas relacionadas |
| Código limpio | Arquitectura Controller-Service-Repository, DTOs, mappers, Lombok y transacciones | Revisión de paquetes y defensa del código |
| Manejo centralizado de errores | `ErrorResponse` uniforme para 400, 404, 405, 409, 415 y 500; causa 500 registrada sin exponerla | `ApiErrorHandlingTest` y casos de error Postman |
| Pipeline completo en act | Workflow `verify` y `deploy-local` | `./scripts/run-act.sh` |
| Despliegue Kubernetes local | Namespace, ConfigMap, Deployment, NodePort 30080 y probes | `kubectl -n eventos get all` |
| Imagen local actualizada automáticamente | Etiqueta `e3-<sha>` y `kubectl set image` | `kubectl -n eventos get deployment ...` |
| `imagePullPolicy: Never` | Declarado en `k8s/deployment.yaml` y verificado por el workflow | `jsonpath` del Deployment |
| Kubeconfig correctamente enlazado | `.kube-local/config` ignorado y enviado como `KUBECONFIG_B64` | Ejecución de `scripts/run-act.sh` |
| Pruebas JUnit al 100% | 58 pruebas: servicios, reglas, relaciones, roles y contrato HTTP | `mvn clean verify` |
| README final | Dominio, instalación, endpoints, Postman, Swagger, Docker, K8s, act y defensa | Revisión del README |
| Swagger accesible | Springdoc 2.6.0, etiquetas y operaciones documentadas | `/swagger-ui/index.html` y `/v3/api-docs` |
| Participación individual | Cuatro ramas, PR y revisión cruzada | GitHub Insights, Pull Requests y commits |

## Reglas de negocio demostrables

- Cupo disponible calculado con registros `ACTIVO`.
- Registro duplicado activo rechazado con `409`.
- Evento no publicado o ya realizado rechazado con `409`.
- Cancelación libera cupo y permite reinscripción.
- No se eliminan eventos, ubicaciones o asistentes con relaciones activas incompatibles.
- Correo único en toda la jerarquía Persona.
- Usuario y perfil comparten una relación 1:0..1 y el rol debe coincidir.
- Contraseña BCrypt nunca aparece en respuesta u OpenAPI.
