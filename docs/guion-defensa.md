# Guion de defensa — Entrega #2

**Formato:** cuatro bloques de ~2.5 minutos, sin diapositivas. El tutor elige qué integrante explica cada punto,
por lo que **los cuatro deben poder explicar los cuatro bloques**.

## Reparto propuesto (con suplente)

| Bloque | Responsable principal | Suplente |
|---|---|---|
| 1. Presentación general del proyecto | Ronnie Portillo | Carlos Enoc Calzada |
| 2. Repositorio y trabajo colaborativo | Ángel Serrano | Azucena Castillo |
| 3. Presentación de la base de datos | Azucena Castillo | Ronnie Portillo |
| 4. Demo funcional en Postman | Carlos Enoc Calzada | Ángel Serrano |

---

## Bloque 1 — Presentación general (2.5 min)

> "Nuestro proyecto es la **API para Gestión de Eventos y Asistentes**. Resuelve un problema real de la facultad:
> organizar actividades con cupo limitado y llevar el control de quién está inscrito.
>
> El dominio tiene cuatro entidades principales. Una **ubicación** alberga muchos **eventos**; cada evento tiene una
> **capacidad máxima**; los **asistentes** son las personas que se inscriben; y la inscripción se guarda en
> **registro**, que es la tabla que une evento y asistente y donde se controla el aforo.
>
> La lógica de negocio gira alrededor de tres reglas: el cupo disponible se **calcula** como capacidad máxima menos
> registros activos; un asistente **no puede registrarse dos veces** en el mismo evento; y al cancelar un registro
> **se libera el cupo**. Además, solo se permite inscribirse en eventos **publicados y con fecha futura**.
>
> La API está hecha con **Spring Boot 3.3.5 y Java 21**, usa **PostgreSQL**, se construye con **Maven**, sigue la
> arquitectura en capas **Controller–Service–Repository**, expone **DTOs** con validaciones y usa **Lombok**.
> Los datos y el esquema se versionan con **Flyway**, así que cualquiera puede levantar el proyecto con dos comandos:
> `docker compose up -d` y `mvn spring-boot:run`."

**Palabras clave a mencionar:** cupo, registro, publicada, transacción, capas.

---

## Bloque 2 — Repositorio y trabajo colaborativo (2.5 min)

> "El repositorio está en GitHub, como **POO-Proyecto-Final-Grupo-6**, con los cuatro integrantes como colaboradores.
>
> Trabajamos con una rama por módulo: `feat/base-ubicacion`, `feat/evento`, `feat/asistente` y `feat/registro`.
> Cada quien desarrolla su módulo, hace *commits* describiendo el avance y luego abre un **pull request** que revisa
> el compañero del módulo vecino; así todos aparecemos en el historial de contribuciones.
>
> El `README.md` explica cómo clonar, levantar la base, arrancar la API, importar la colección de Postman y qué hacer
> si un puerto está ocupado. El repositorio también incluye `.gitignore` para Java y Maven, la carpeta `postman/`
> con la colección y el entorno, `docs/` con el guion y los documentos de la Entrega #1, y el código organizado por capas.
>
> Para verificar el avance: en la pestaña *Insights → Contributors* se ve el aporte de cada integrante, y en
> *Pull requests* quedan las revisiones cruzadas."

**Demostración:** mostrar la lista de ramas, un pull request y la gráfica de contribuciones.

---

## Bloque 3 — Base de datos (2.5 min)

> "La base es **PostgreSQL 16** y se levanta con Docker Compose. El esquema lo crea **Flyway** con dos migraciones:
> `V1__schema.sql` con las tablas y `V2__seed.sql` con datos de ejemplo. Hibernate corre en modo **validate**, o sea
> que si la base y las entidades no coinciden, la aplicación no arranca: eso nos garantiza que el modelo y el código
> estén siempre alineados.
>
> El modelo tiene siete tablas: `usuario`, `persona`, `administrador`, `asistente`, `ubicacion`, `evento` y `registro`.
> `persona` guarda los datos comunes y se especializa en `administrador` y `asistente` con la misma llave primaria
> (herencia JOINED). `evento` tiene la llave foránea a `ubicacion` y `registro` tiene llave foránea a `evento` y a
> `asistente`: así se resuelve la relación muchos a muchos.
>
> Lo más importante está en las restricciones: `email` y `username` son **únicos** (RN-09); `capacidad_maxima` y
> `capacidad` tienen un **CHECK mayor que cero**; los estados están limitados por un CHECK a los valores del enumerado;
> y en `registro` hay un **índice único parcial** sobre `(evento_id, asistente_id)` que solo aplica cuando el estado es
> `ACTIVO`. Ese índice es la clave: impide el registro duplicado a nivel de base de datos, pero **permite volver a
> inscribirse después de cancelar**.
>
> El cupo **no se guarda** en ninguna columna: se calcula con un `COUNT` de los registros activos, para que no pueda
> quedar desincronizado."

**Demostración:** `docker compose ps` y una consulta `SELECT * FROM registro;` o el diagrama de tablas en pgAdmin.

---

## Bloque 4 — Demo funcional en Postman (2.5 min)

Ejecutar la carpeta **Flujo demo (en orden)** y narrar:

1. **Crear ubicación (201).** "El catálogo de ubicaciones es el punto de partida: un evento necesita un lugar."
2. **Crear evento en BORRADOR (201).** "Todo evento nace en borrador y con los cupos iguales a su capacidad."
3. **Publicar evento (200).** "Lo pasamos a PUBLICADO: solo así se puede inscribir gente (RN-10)."
4. **Crear asistente (201).** "El correo se guarda en minúsculas y es único (RN-09)."
5. **Registrar asistente (201).** "Aquí está la regla central: el sistema valida el estado, la fecha, el duplicado y
   el cupo, y descuenta un cupo disponible."
6. **Consultar disponibilidad (200).** "Devuelve capacidad, inscritos y disponibles: inscritos + disponibles siempre
   suma la capacidad."
7. **Listar asistentes del evento (200).** "Solo aparecen los registros ACTIVOS."
8. **Registro duplicado (409).** "El mismo asistente no puede inscribirse dos veces (RN-04)."
9. **Evento lleno (409).** "Si el cupo llega a cero, el sistema rechaza con 409 (RN-03)."
10. **Cancelar registro (200).** "El registro pasa a CANCELADO, se guarda la fecha y **se libera el cupo** (RN-11)."
11. **Reinscribir (201).** "Y esto demuestra el índice parcial: después de cancelar sí se puede volver a inscribir."
12. **Eliminar el registro cancelado (204).** "Solo se pueden borrar registros cancelados."

**Cierre:** "Además de la carpeta de flujo, la colección tiene los casos de error de cada entidad: validaciones 400,
recursos inexistentes 404 y conflictos de reglas de negocio 409."

---

## Checklist previo a la tutoría

- [ ] `docker compose up -d` y contenedor en estado `healthy`.
- [ ] `mvn spring-boot:run` arranca sin errores de Flyway ni de validación de esquema.
- [ ] Postman abierto con la colección y el entorno **Grupo 6 - Local** seleccionados.
- [ ] Ejecutar la carpeta *Flujo demo (en orden)* completa antes de la tutoría (deja datos y variables cargados).
- [ ] Base sembrada y, si se ensayó mucho, reiniciar con `docker compose down -v && docker compose up -d`.
- [ ] Repositorio abierto en el navegador: pestañas *Commits*, *Pull requests* y *Contributors*.
- [ ] pgAdmin o `psql` conectado a `eventosdb` con la tabla `registro` a la vista.
- [ ] Los cuatro integrantes con el proyecto clonado y funcionando en su máquina.

## Preguntas previsibles

| Pregunta | Respuesta corta |
|---|---|
| ¿Cómo garantizan que no se pase el cupo si dos personas se inscriben a la vez? | La fila del evento se bloquea con `PESSIMISTIC_WRITE` dentro de la transacción del registro, así que la validación y la inserción ocurren en serie. |
| ¿Por qué el cupo no es una columna? | Porque es un valor derivado: si se guardara habría que mantenerlo sincronizado en cada alta, cancelación y borrado. Se calcula con `COUNT` de registros activos (RN-02). |
| ¿Qué pasa si alguien cancela y se quiere volver a inscribir? | Se puede: el índice único es **parcial** y solo aplica a los registros `ACTIVO`. |
| ¿Por qué existe `usuario` si no hay login? | Porque el modelo alineado de la Entrega #1 lo define para las credenciales; la autenticación queda fuera de los 19 casos de uso. La tabla existe y el repositorio está listo para un CU-20. |
| ¿Por qué `persona` está separada de `asistente`? | Para no duplicar los datos personales: `persona` es abstracta y `administrador` y `asistente` la especializan con la misma llave (herencia JOINED). |
| ¿Dónde está la lógica de negocio? | En la capa `service`, no en los controladores; los controladores solo reciben y devuelven DTO. |
| ¿Cómo manejan los errores? | Un `@RestControllerAdvice` central convierte las excepciones en un `ErrorResponse` uniforme con 400, 404, 409 o 500. |

## Plan alterno

| Si falla... | Alternativa |
|---|---|
| Docker no arranca | Levantar el PostgreSQL local instalado y arrancar con `DB_URL=jdbc:postgresql://localhost:5432/eventosdb`. |
| La app no compila en la máquina | Mostrar el video/capturas del flujo ya probado y el log de `mvn test` en verde. |
| Postman no está instalado | Usar Postman web (`web.postman.co`) importando la misma colección. |
| Se cae la red | Usar datos ya sembrados y las capturas guardadas del flujo completo. |
