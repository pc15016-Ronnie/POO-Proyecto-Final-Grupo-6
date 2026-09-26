# CASOS DE USO
## API para Gestión de Eventos y Asistentes

**Asignatura:** Programación Orientada a Objetos — Ciclo II/2026
**Universidad de El Salvador — Facultad Multidisciplinaria de Occidente**
**Entrega #1 — Proyecto de Ciclo**

---

## 1. Descripción general del sistema

La **API para Gestión de Eventos y Asistentes** permite administrar eventos que se
realizan en una ubicación determinada, gestionar el catálogo de ubicaciones y de
asistentes, y controlar el registro de asistentes a los eventos respetando la
**capacidad máxima** definida para cada evento.

La API expone operaciones **GET, POST, PUT y DELETE** para cada entidad principal y
concentra su lógica de negocio en dos reglas críticas:

1. **Control de capacidad:** un evento no puede tener más asistentes registrados que su capacidad máxima.V
2. **Registro único:** un mismo asistente no puede registrarse dos veces (de forma activa) en el mismo evento.

---

## 2. Actores

| Actor | Tipo | Descripción |
|---|---|---|
| **Administrador** | Primario | Personal que administra el sistema. Gestiona el CRUD completo de eventos, ubicaciones y asistentes, y monitorea la ocupación de los eventos. |
| **Asistente** | Primario | Persona que consulta los eventos disponibles, se registra en un evento y puede cancelar su registro. |

**Actores secundarios / casos incluidos:** el sistema ejecuta internamente las
validaciones `Validar capacidad disponible` y `Validar registro único`, invocadas
de forma obligatoria (`<<include>>`) por el caso de uso `Registrarse en un evento`.

---

## 3. Diagrama de casos de uso

*(Insertar aquí la imagen `diagrama-casos-uso.png`)*

**Resumen del diagrama:**
- 2 actores: **Administrador** y **Asistente**
- 19 casos de uso, de los cuales 17 son de negocio y 2 son casos incluidos (`<<include>>`)
- 12 casos corresponden al **CRUD de las 3 entidades principales** (Evento, Ubicación, Asistente)
- 2 casos son **compartidos** por ambos actores (Consultar disponibilidad de cupo / Listar asistentes de un evento)

---

## 4. Listado de casos de uso

| Código | Caso de uso | Actor(es) | Tipo |
|---|---|---|---|
| CU-01 | Crear evento | Administrador | CRUD |
| CU-02 | Consultar eventos | Administrador | CRUD |
| CU-03 | Actualizar evento | Administrador | CRUD |
| CU-04 | Eliminar evento | Administrador | CRUD |
| CU-05 | Crear ubicación | Administrador | CRUD |
| CU-06 | Consultar ubicaciones | Administrador | CRUD |
| CU-07 | Actualizar ubicación | Administrador | CRUD |
| CU-08 | Eliminar ubicación | Administrador | CRUD |
| CU-09 | Crear asistente | Administrador | CRUD |
| CU-10 | Consultar asistentes | Administrador | CRUD |
| CU-11 | Actualizar asistente | Administrador | CRUD |
| CU-12 | Eliminar asistente | Administrador | CRUD |
| CU-13 | Consultar eventos disponibles | Asistente | Negocio |
| CU-14 | **Registrarse en un evento** | Asistente | Negocio (crítico) |
| CU-15 | Cancelar registro | Asistente | Negocio |
| CU-16 | Consultar disponibilidad de cupo | Administrador y Asistente | Negocio (compartido) |
| CU-17 | Listar asistentes de un evento | Administrador y Asistente | Negocio (compartido) |
| CU-18 | Validar capacidad disponible | *(incluido)* | `<<include>>` |
| CU-19 | Validar registro único | *(incluido)* | `<<include>>` |

---

## 5. Reglas de negocio

| Código | Regla | Aplica a |
|---|---|---|
| RN-01 | La capacidad máxima de un evento debe ser mayor que cero. | CU-01, CU-03 |
| RN-02 | El cupo disponible = capacidad máxima − registros activos. | CU-14, CU-16 |
| RN-03 | No se permite registrar a un asistente si el cupo disponible es 0. | CU-14, CU-18 |
| RN-04 | Un asistente no puede tener dos registros activos en el mismo evento. | CU-14, CU-19 |
| RN-05 | No se puede reducir la capacidad máxima por debajo del número de inscritos activos. | CU-03 |
| RN-06 | No se puede eliminar un evento que tenga registros activos. | CU-04 |
| RN-07 | No se puede eliminar una ubicación que tenga eventos asociados. | CU-08 |
| RN-08 | No se puede eliminar un asistente que tenga registros activos. | CU-12 |
| RN-09 | El correo electrónico de un asistente debe ser único. | CU-09, CU-11 |
| RN-10 | Solo se puede registrar a un evento en estado PUBLICADO y con fecha futura. | CU-13, CU-14 |
| RN-11 | Al cancelar un registro se libera un cupo del evento. | CU-15 |
| RN-12 | Cada evento se realiza en una única ubicación (relación N:1). | CU-01, CU-05 |

---

## 6. FICHAS DE CASOS DE USO

---

### CU-14 · Registrarse en un evento ⭐ *(caso de uso crítico)*

| Campo | Descripción |
|---|---|
| **Código** | CU-14 |
| **Nombre** | Registrarse en un evento |
| **Actor** | Asistente |
| **Descripción** | Permite que un asistente registrado se inscriba en un evento publicado, siempre que exista cupo disponible y que no se haya inscrito antes en ese mismo evento. |
| **Precondiciones** | • El evento existe y está en estado PUBLICADO.<br>• La fecha y hora del evento no han pasado.<br>• El asistente existe en el sistema.<br>• El asistente se encuentra autenticado. |
| **Postcondiciones** | • **Éxito:** se crea un registro con estado ACTIVO y la fecha/hora del registro; el cupo disponible del evento disminuye en 1.<br>• **Fallo:** no se crea ningún registro y el cupo del evento permanece sin cambios. |
| **Casos relacionados** | `<<include>>` CU-18 Validar capacidad disponible · `<<include>>` CU-19 Validar registro único |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El asistente consulta los eventos disponibles (CU-13). |
| 2 | El asistente selecciona un evento y solicita registrarse en él. |
| 3 | El sistema valida que el evento exista y esté en estado PUBLICADO. |
| 4 | El sistema valida que el asistente exista en el sistema. |
| 5 | El sistema valida que el asistente no tenga un registro activo en ese evento (CU-19). |
| 6 | El sistema valida que exista cupo disponible (CU-18). |
| 7 | El sistema crea el registro con estado ACTIVO y la fecha/hora actual. |
| 8 | El sistema actualiza la ocupación del evento (cupo disponible − 1). |
| 9 | El sistema confirma el registro y muestra los datos del registro y el cupo restante. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 3a | El evento no existe | Error 404 — "El evento indicado no existe". Fin del caso de uso. |
| 3b | El evento no está publicado (borrador, cancelado o finalizado) | Error 409 — "El evento no está disponible para registro". Fin del caso de uso. |
| 3c | La fecha del evento ya pasó | Error 409 — "El evento ya se realizó". Fin del caso de uso. |
| 4a | El asistente no existe | Error 404 — "El asistente no está registrado". Fin del caso de uso. |
| 5a | El asistente ya está registrado en ese evento | Error 409 — "El asistente ya está registrado en este evento". No se crea el registro. |
| 6a | El evento alcanzó su capacidad máxima (cupo disponible = 0) | Error 409 — "El evento alcanzó su capacidad máxima". No se crea el registro. |
| 7a | Falla inesperada al guardar | Error 500 — "Ocurrió un error al procesar el registro". Se revierte la transacción. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| POST | `/api/eventos/{idEvento}/registros` | `201 Created` con el registro creado |

---

### CU-15 · Cancelar registro

| Campo | Descripción |
|---|---|
| **Código** | CU-15 |
| **Nombre** | Cancelar registro |
| **Actor** | Asistente (o Administrador en representación del asistente) |
| **Descripción** | Permite anular la inscripción de un asistente a un evento, cambiando el estado del registro a CANCELADO y liberando el cupo ocupado. |
| **Precondiciones** | • Existe un registro con estado ACTIVO para el evento y el asistente indicados.<br>• El registro pertenece al asistente que solicita la cancelación (o el solicitante es Administrador). |
| **Postcondiciones** | • **Éxito:** el registro cambia a estado CANCELADO y el cupo disponible del evento aumenta en 1.<br>• **Fallo:** el registro permanece en estado ACTIVO y el cupo no cambia. |
| **Casos relacionados** | Extiende el resultado de CU-14 (aplica la regla RN-11). |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El asistente ingresa a "Mis registros" y selecciona el evento que desea cancelar. |
| 2 | El sistema muestra el detalle del registro y solicita confirmación. |
| 3 | El asistente confirma la cancelación. |
| 4 | El sistema valida que el registro exista y esté ACTIVO. |
| 5 | El sistema cambia el estado del registro a CANCELADO. |
| 6 | El sistema libera el cupo del evento (cupo disponible + 1). |
| 7 | El sistema confirma la cancelación y muestra el nuevo cupo disponible. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El registro no existe | Error 404 — "El registro indicado no existe". |
| 4b | El registro ya estaba cancelado | Error 409 — "El registro ya fue cancelado anteriormente". |
| 4c | El registro no pertenece al asistente solicitante | Error 403 — "No tiene permiso para cancelar este registro". |
| 4d | El evento ya se realizó | Error 409 — "No se puede cancelar un registro de un evento finalizado". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| PUT | `/api/registros/{idRegistro}/cancelar` | `200 OK` con el registro actualizado |
| DELETE | `/api/registros/{idRegistro}` | `204 No Content` |

---

### CU-16 · Consultar disponibilidad de cupo *(compartido)*

| Campo | Descripción |
|---|---|
| **Código** | CU-16 |
| **Nombre** | Consultar disponibilidad de cupo |
| **Actores** | Administrador y Asistente |
| **Descripción** | Permite conocer la capacidad máxima de un evento, la cantidad de asistentes inscritos y cuántos cupos quedan disponibles. |
| **Precondiciones** | El evento existe en el sistema. |
| **Postcondiciones** | No modifica datos (caso de uso de solo consulta). Se muestra la información de ocupación del evento. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El actor selecciona un evento. |
| 2 | El sistema recupera la capacidad máxima del evento. |
| 3 | El sistema cuenta los registros con estado ACTIVO del evento. |
| 4 | El sistema calcula: cupo disponible = capacidad máxima − inscritos activos. |
| 5 | El sistema muestra: capacidad máxima, inscritos y cupos disponibles. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 2a | El evento no existe | Error 404 — "El evento indicado no existe". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/eventos/{idEvento}/disponibilidad` | `200 OK` con `{capacidadMaxima, inscritos, disponibles}` |

---

### CU-17 · Listar asistentes de un evento *(compartido)*

| Campo | Descripción |
|---|---|
| **Código** | CU-17 |
| **Nombre** | Listar asistentes de un evento |
| **Actores** | Administrador y Asistente |
| **Descripción** | Permite obtener la lista de asistentes inscritos (activos) en un evento determinado. |
| **Precondiciones** | El evento existe en el sistema. |
| **Postcondiciones** | No modifica datos. Se devuelve el listado de asistentes inscritos con su fecha de registro. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El actor selecciona un evento y solicita ver sus asistentes. |
| 2 | El sistema valida que el evento exista. |
| 3 | El sistema recupera los registros ACTIVOS del evento y los asistentes asociados. |
| 4 | El sistema muestra el listado (nombre, correo y fecha de registro de cada asistente). |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 2a | El evento no existe | Error 404 — "El evento indicado no existe". |
| 3a | El evento no tiene asistentes inscritos | El sistema muestra el listado vacío con un mensaje informativo. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/eventos/{idEvento}/asistentes` | `200 OK` con el listado de asistentes |

---

### CU-13 · Consultar eventos disponibles

| Campo | Descripción |
|---|---|
| **Código** | CU-13 |
| **Nombre** | Consultar eventos disponibles |
| **Actor** | Asistente |
| **Descripción** | Permite al asistente ver únicamente los eventos publicados, con fecha futura y con cupo disponible. |
| **Precondiciones** | Ninguna (consulta pública). |
| **Postcondiciones** | No modifica datos. Se muestra el listado de eventos disponibles. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El asistente ingresa a la sección de eventos disponibles. |
| 2 | El sistema filtra los eventos con estado PUBLICADO. |
| 3 | El sistema descarta los eventos cuya fecha ya pasó. |
| 4 | El sistema calcula el cupo disponible de cada evento. |
| 5 | El sistema muestra únicamente los eventos con cupo disponible mayor que cero. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 5a | No hay eventos que cumplan los criterios | El sistema muestra el listado vacío con un mensaje informativo. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/eventos/disponibles` | `200 OK` con el listado de eventos |

---

### CU-01 · Crear evento

| Campo | Descripción |
|---|---|
| **Código** | CU-01 |
| **Nombre** | Crear evento |
| **Actor** | Administrador |
| **Descripción** | Permite registrar un nuevo evento indicando sus datos generales, la ubicación donde se realizará y la capacidad máxima de asistentes. |
| **Precondiciones** | • El administrador se encuentra autenticado.<br>• Existe al menos una ubicación registrada en el sistema. |
| **Postcondiciones** | • **Éxito:** el evento queda registrado con estado BORRADOR y su cupo disponible igual a la capacidad máxima.<br>• **Fallo:** no se crea ningún evento. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona la opción "Crear evento". |
| 2 | El sistema muestra el formulario con los campos requeridos. |
| 3 | El administrador ingresa nombre, descripción, fecha, hora, capacidad máxima y ubicación. |
| 4 | El sistema valida los datos ingresados (RN-01, RN-10 y RN-12). |
| 5 | El sistema registra el evento con estado BORRADOR. |
| 6 | El sistema confirma la creación y muestra el evento con su identificador. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | Campos obligatorios vacíos o con formato inválido | Error 400 — detalle de los campos con error. |
| 4b | La capacidad máxima es menor o igual a cero | Error 400 — "La capacidad máxima debe ser mayor que cero". |
| 4c | La fecha del evento es anterior a la fecha actual | Error 400 — "La fecha del evento debe ser futura". |
| 4d | La ubicación indicada no existe | Error 404 — "La ubicación indicada no existe". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| POST | `/api/eventos` | `201 Created` con el evento creado |

---

### CU-03 · Actualizar evento

| Campo | Descripción |
|---|---|
| **Código** | CU-03 |
| **Nombre** | Actualizar evento |
| **Actor** | Administrador |
| **Descripción** | Permite modificar los datos de un evento existente (nombre, descripción, fecha, hora, capacidad máxima, ubicación y estado). |
| **Precondiciones** | El evento existe en el sistema. |
| **Postcondiciones** | • **Éxito:** los datos del evento quedan actualizados.<br>• **Fallo:** el evento conserva sus datos originales. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona un evento y elige "Editar". |
| 2 | El sistema muestra el formulario con los datos actuales del evento. |
| 3 | El administrador modifica los campos deseados y confirma. |
| 4 | El sistema valida que el evento exista y que los datos sean válidos. |
| 5 | El sistema verifica que la nueva capacidad máxima no sea menor que el número de inscritos activos (RN-05). |
| 6 | El sistema guarda los cambios. |
| 7 | El sistema confirma la actualización y muestra el evento actualizado. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El evento no existe | Error 404 — "El evento indicado no existe". |
| 4b | Datos inválidos | Error 400 — detalle de los campos con error. |
| 5a | La nueva capacidad es menor que los inscritos activos | Error 409 — "La capacidad no puede ser menor que el número de inscritos". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| PUT | `/api/eventos/{idEvento}` | `200 OK` con el evento actualizado |

---

### CU-02 · Consultar eventos

| Campo | Descripción |
|---|---|
| **Código** | CU-02 |
| **Nombre** | Consultar eventos |
| **Actor** | Administrador |
| **Descripción** | Permite listar todos los eventos del sistema (con filtros por estado, fecha o ubicación) y consultar el detalle de uno en particular. |
| **Precondiciones** | El administrador se encuentra autenticado. |
| **Postcondiciones** | No modifica datos. Se muestra el listado o el detalle solicitado. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador ingresa a la gestión de eventos. |
| 2 | El sistema recupera todos los eventos registrados. |
| 3 | El administrador aplica filtros (opcional): estado, fecha o ubicación. |
| 4 | El sistema muestra el listado de eventos que cumplen los criterios. |
| 5 | El administrador selecciona un evento para ver su detalle. |
| 6 | El sistema muestra los datos completos del evento, su ubicación y su ocupación. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 5a | El evento no existe | Error 404 — "El evento indicado no existe". |
| 3a | Ningún evento cumple los filtros | El sistema muestra el listado vacío. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/eventos` | `200 OK` con el listado de eventos |
| GET | `/api/eventos/{idEvento}` | `200 OK` con el evento solicitado |

---

### CU-04 · Eliminar evento

| Campo | Descripción |
|---|---|
| **Código** | CU-04 |
| **Nombre** | Eliminar evento |
| **Actor** | Administrador |
| **Descripción** | Permite eliminar de forma definitiva un evento que no tenga asistentes inscritos de forma activa. |
| **Precondiciones** | El evento existe y no posee registros con estado ACTIVO. |
| **Postcondiciones** | • **Éxito:** el evento deja de existir en el sistema.<br>• **Fallo:** el evento se mantiene sin cambios. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona un evento y elige "Eliminar". |
| 2 | El sistema solicita confirmación de la acción. |
| 3 | El administrador confirma la eliminación. |
| 4 | El sistema valida que el evento exista. |
| 5 | El sistema verifica que el evento no tenga registros activos (RN-06). |
| 6 | El sistema elimina el evento (y sus registros cancelados asociados). |
| 7 | El sistema confirma la eliminación. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El evento no existe | Error 404 — "El evento indicado no existe". |
| 5a | El evento tiene registros activos | Error 409 — "No se puede eliminar un evento con asistentes inscritos". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| DELETE | `/api/eventos/{idEvento}` | `204 No Content` |

---

### CU-05 · Crear ubicación

| Campo | Descripción |
|---|---|
| **Código** | CU-05 |
| **Nombre** | Crear ubicación |
| **Actor** | Administrador |
| **Descripción** | Permite registrar una nueva ubicación (lugar físico) donde pueden realizarse eventos. |
| **Precondiciones** | El administrador se encuentra autenticado. |
| **Postcondiciones** | • **Éxito:** la ubicación queda registrada y disponible para asociarla a eventos.<br>• **Fallo:** no se crea la ubicación. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona "Crear ubicación". |
| 2 | El sistema muestra el formulario. |
| 3 | El administrador ingresa nombre, dirección, ciudad y capacidad del lugar. |
| 4 | El sistema valida los datos obligatorios y el formato. |
| 5 | El sistema registra la ubicación. |
| 6 | El sistema confirma la creación y muestra la ubicación con su identificador. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | Campos obligatorios vacíos o con formato inválido | Error 400 — detalle de los campos con error. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| POST | `/api/ubicaciones` | `201 Created` con la ubicación creada |

---

### CU-06 · Consultar ubicaciones

| Campo | Descripción |
|---|---|
| **Código** | CU-06 |
| **Nombre** | Consultar ubicaciones |
| **Actor** | Administrador |
| **Descripción** | Permite listar todas las ubicaciones registradas y consultar el detalle de una en particular, incluyendo los eventos asociados. |
| **Precondiciones** | El administrador se encuentra autenticado. |
| **Postcondiciones** | No modifica datos. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador ingresa a la gestión de ubicaciones. |
| 2 | El sistema recupera todas las ubicaciones registradas. |
| 3 | El sistema muestra el listado con nombre, dirección y ciudad. |
| 4 | El administrador selecciona una ubicación para ver su detalle. |
| 5 | El sistema muestra los datos de la ubicación y los eventos que se realizan en ella. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | La ubicación no existe | Error 404 — "La ubicación indicada no existe". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/ubicaciones` | `200 OK` con el listado |
| GET | `/api/ubicaciones/{idUbicacion}` | `200 OK` con la ubicación solicitada |

---

### CU-07 · Actualizar ubicación

| Campo | Descripción |
|---|---|
| **Código** | CU-07 |
| **Nombre** | Actualizar ubicación |
| **Actor** | Administrador |
| **Descripción** | Permite modificar los datos de una ubicación existente. |
| **Precondiciones** | La ubicación existe en el sistema. |
| **Postcondiciones** | • **Éxito:** los datos de la ubicación quedan actualizados.<br>• **Fallo:** la ubicación conserva sus datos originales. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona una ubicación y elige "Editar". |
| 2 | El sistema muestra el formulario con los datos actuales. |
| 3 | El administrador modifica los campos deseados y confirma. |
| 4 | El sistema valida que la ubicación exista y que los datos sean válidos. |
| 5 | El sistema guarda los cambios. |
| 6 | El sistema confirma la actualización. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | La ubicación no existe | Error 404 — "La ubicación indicada no existe". |
| 4b | Datos inválidos | Error 400 — detalle de los campos con error. |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| PUT | `/api/ubicaciones/{idUbicacion}` | `200 OK` con la ubicación actualizada |

---

### CU-08 · Eliminar ubicación

| Campo | Descripción |
|---|---|
| **Código** | CU-08 |
| **Nombre** | Eliminar ubicación |
| **Actor** | Administrador |
| **Descripción** | Permite eliminar una ubicación que no tenga eventos asociados. |
| **Precondiciones** | La ubicación existe y no tiene eventos asociados. |
| **Postcondiciones** | • **Éxito:** la ubicación deja de existir en el sistema.<br>• **Fallo:** la ubicación se mantiene sin cambios. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona una ubicación y elige "Eliminar". |
| 2 | El sistema solicita confirmación. |
| 3 | El administrador confirma la eliminación. |
| 4 | El sistema valida que la ubicación exista. |
| 5 | El sistema verifica que no tenga eventos asociados (RN-07). |
| 6 | El sistema elimina la ubicación. |
| 7 | El sistema confirma la eliminación. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | La ubicación no existe | Error 404 — "La ubicación indicada no existe". |
| 5a | La ubicación tiene eventos asociados | Error 409 — "No se puede eliminar una ubicación con eventos asociados". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| DELETE | `/api/ubicaciones/{idUbicacion}` | `204 No Content` |

---

### CU-09 · Crear asistente

| Campo | Descripción |
|---|---|
| **Código** | CU-09 |
| **Nombre** | Crear asistente |
| **Actor** | Administrador |
| **Descripción** | Permite registrar un nuevo asistente en el sistema con sus datos personales y de contacto. |
| **Precondiciones** | El administrador se encuentra autenticado. |
| **Postcondiciones** | • **Éxito:** el asistente queda registrado y habilitado para inscribirse en eventos.<br>• **Fallo:** no se crea el asistente. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona "Crear asistente". |
| 2 | El sistema muestra el formulario. |
| 3 | El administrador ingresa nombre, apellido, correo electrónico y teléfono. |
| 4 | El sistema valida los campos y que el correo no esté registrado (RN-09). |
| 5 | El sistema registra al asistente. |
| 6 | El sistema confirma la creación y muestra el asistente con su identificador. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | Campos obligatorios vacíos o con formato inválido | Error 400 — detalle de los campos con error. |
| 4b | El correo electrónico ya está registrado | Error 409 — "El correo electrónico ya está registrado". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| POST | `/api/asistentes` | `201 Created` con el asistente creado |

---

### CU-10 · Consultar asistentes

| Campo | Descripción |
|---|---|
| **Código** | CU-10 |
| **Nombre** | Consultar asistentes |
| **Actor** | Administrador |
| **Descripción** | Permite listar todos los asistentes registrados y consultar el detalle de uno en particular, incluyendo los eventos en los que se ha inscrito. |
| **Precondiciones** | El administrador se encuentra autenticado. |
| **Postcondiciones** | No modifica datos. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador ingresa a la gestión de asistentes. |
| 2 | El sistema recupera todos los asistentes registrados. |
| 3 | El sistema muestra el listado con nombre, correo y teléfono. |
| 4 | El administrador selecciona un asistente para ver su detalle. |
| 5 | El sistema muestra los datos del asistente y sus eventos inscritos. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El asistente no existe | Error 404 — "El asistente indicado no existe". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| GET | `/api/asistentes` | `200 OK` con el listado |
| GET | `/api/asistentes/{idAsistente}` | `200 OK` con el asistente solicitado |

---

### CU-11 · Actualizar asistente

| Campo | Descripción |
|---|---|
| **Código** | CU-11 |
| **Nombre** | Actualizar asistente |
| **Actor** | Administrador |
| **Descripción** | Permite modificar los datos de un asistente existente. |
| **Precondiciones** | El asistente existe en el sistema. |
| **Postcondiciones** | • **Éxito:** los datos del asistente quedan actualizados.<br>• **Fallo:** el asistente conserva sus datos originales. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona un asistente y elige "Editar". |
| 2 | El sistema muestra el formulario con los datos actuales. |
| 3 | El administrador modifica los campos deseados y confirma. |
| 4 | El sistema valida que el asistente exista y que los datos sean válidos. |
| 5 | El sistema verifica que el nuevo correo no pertenezca a otro asistente (RN-09). |
| 6 | El sistema guarda los cambios. |
| 7 | El sistema confirma la actualización. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El asistente no existe | Error 404 — "El asistente indicado no existe". |
| 4b | Datos inválidos | Error 400 — detalle de los campos con error. |
| 5a | El correo ya pertenece a otro asistente | Error 409 — "El correo electrónico ya está registrado". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| PUT | `/api/asistentes/{idAsistente}` | `200 OK` con el asistente actualizado |

---

### CU-12 · Eliminar asistente

| Campo | Descripción |
|---|---|
| **Código** | CU-12 |
| **Nombre** | Eliminar asistente |
| **Actor** | Administrador |
| **Descripción** | Permite eliminar un asistente que no tenga registros activos en eventos. |
| **Precondiciones** | El asistente existe y no posee registros con estado ACTIVO. |
| **Postcondiciones** | • **Éxito:** el asistente deja de existir en el sistema.<br>• **Fallo:** el asistente se mantiene sin cambios. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El administrador selecciona un asistente y elige "Eliminar". |
| 2 | El sistema solicita confirmación. |
| 3 | El administrador confirma la eliminación. |
| 4 | El sistema valida que el asistente exista. |
| 5 | El sistema verifica que no tenga registros activos (RN-08). |
| 6 | El sistema elimina el asistente. |
| 7 | El sistema confirma la eliminación. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El asistente no existe | Error 404 — "El asistente indicado no existe". |
| 5a | El asistente tiene registros activos | Error 409 — "No se puede eliminar un asistente con registros activos". |

**Trazabilidad técnica**

| Método | Endpoint | Respuesta exitosa |
|---|---|---|
| DELETE | `/api/asistentes/{idAsistente}` | `204 No Content` |

---

### CU-18 · Validar capacidad disponible *(caso incluido)*

| Campo | Descripción |
|---|---|
| **Código** | CU-18 |
| **Nombre** | Validar capacidad disponible |
| **Actor** | Ninguno (caso de uso incluido por CU-14) |
| **Descripción** | Verifica que el evento tenga cupo disponible antes de permitir un nuevo registro. |
| **Precondiciones** | El evento existe y se ha recuperado su capacidad máxima y el número de inscritos activos. |
| **Postcondiciones** | Se determina si el registro puede continuar (hay cupo) o debe rechazarse (evento lleno). |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El sistema obtiene la capacidad máxima del evento. |
| 2 | El sistema cuenta los registros con estado ACTIVO del evento. |
| 3 | El sistema calcula: cupo disponible = capacidad máxima − inscritos activos. |
| 4 | El sistema verifica que el cupo disponible sea mayor que cero (RN-03). |
| 5 | El sistema informa que existe cupo disponible y el caso de uso continúa. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 4a | El cupo disponible es igual a cero | El sistema informa "El evento alcanzó su capacidad máxima" y CU-14 termina con error 409. |

**Trazabilidad técnica:** lógica de negocio del servicio de registros (`RegistroService`), no expone un endpoint propio.

---

### CU-19 · Validar registro único *(caso incluido)*

| Campo | Descripción |
|---|---|
| **Código** | CU-19 |
| **Nombre** | Validar registro único |
| **Actor** | Ninguno (caso de uso incluido por CU-14) |
| **Descripción** | Verifica que el asistente no tenga ya un registro activo en el evento al que intenta inscribirse. |
| **Precondiciones** | El evento y el asistente existen. |
| **Postcondiciones** | Se determina si el registro puede continuar o si debe rechazarse por duplicidad. |

**Flujo normal**

| # | Acción |
|---|---|
| 1 | El sistema consulta los registros existentes del asistente en el evento indicado. |
| 2 | El sistema verifica que no exista un registro con estado ACTIVO (RN-04). |
| 3 | El sistema informa que el registro es único y el caso de uso continúa. |

**Flujos alternativos y excepciones**

| # | Condición | Respuesta del sistema |
|---|---|---|
| 2a | Ya existe un registro ACTIVO para ese asistente y evento | El sistema informa "El asistente ya está registrado en este evento" y CU-14 termina con error 409. |

**Trazabilidad técnica:** lógica de negocio del servicio de registros (`RegistroService`), no expone un endpoint propio.

---

## 7. Matriz CRUD (resumen de los 12 casos principales)

| Entidad | Caso de uso | Método | Endpoint | Actor | Error principal |
|---|---|---|---|---|---|
| Evento | Crear evento (CU-01) | POST | `/api/eventos` | Administrador | 400 datos inválidos |
| Evento | Consultar eventos (CU-02) | GET | `/api/eventos` · `/api/eventos/{id}` | Administrador | 404 no existe |
| Evento | Actualizar evento (CU-03) | PUT | `/api/eventos/{id}` | Administrador | 409 capacidad < inscritos |
| Evento | Eliminar evento (CU-04) | DELETE | `/api/eventos/{id}` | Administrador | 409 tiene inscritos |
| Ubicación | Crear ubicación (CU-05) | POST | `/api/ubicaciones` | Administrador | 400 datos inválidos |
| Ubicación | Consultar ubicaciones (CU-06) | GET | `/api/ubicaciones` · `/api/ubicaciones/{id}` | Administrador | 404 no existe |
| Ubicación | Actualizar ubicación (CU-07) | PUT | `/api/ubicaciones/{id}` | Administrador | 400 datos inválidos |
| Ubicación | Eliminar ubicación (CU-08) | DELETE | `/api/ubicaciones/{id}` | Administrador | 409 tiene eventos |
| Asistente | Crear asistente (CU-09) | POST | `/api/asistentes` | Administrador | 409 correo duplicado |
| Asistente | Consultar asistentes (CU-10) | GET | `/api/asistentes` · `/api/asistentes/{id}` | Administrador | 404 no existe |
| Asistente | Actualizar asistente (CU-11) | PUT | `/api/asistentes/{id}` | Administrador | 409 correo duplicado |
| Asistente | Eliminar asistente (CU-12) | DELETE | `/api/asistentes/{id}` | Administrador | 409 tiene registros |

---

## 8. Matriz de trazabilidad: caso de uso → endpoint

| Código | Caso de uso | Método HTTP | Endpoint | Actor |
|---|---|---|---|---|
| CU-01 | Crear evento | POST | `/api/eventos` | Administrador |
| CU-02 | Consultar eventos | GET | `/api/eventos` | Administrador |
| CU-03 | Actualizar evento | PUT | `/api/eventos/{id}` | Administrador |
| CU-04 | Eliminar evento | DELETE | `/api/eventos/{id}` | Administrador |
| CU-05 | Crear ubicación | POST | `/api/ubicaciones` | Administrador |
| CU-06 | Consultar ubicaciones | GET | `/api/ubicaciones` | Administrador |
| CU-07 | Actualizar ubicación | PUT | `/api/ubicaciones/{id}` | Administrador |
| CU-08 | Eliminar ubicación | DELETE | `/api/ubicaciones/{id}` | Administrador |
| CU-09 | Crear asistente | POST | `/api/asistentes` | Administrador |
| CU-10 | Consultar asistentes | GET | `/api/asistentes` | Administrador |
| CU-11 | Actualizar asistente | PUT | `/api/asistentes/{id}` | Administrador |
| CU-12 | Eliminar asistente | DELETE | `/api/asistentes/{id}` | Administrador |
| CU-13 | Consultar eventos disponibles | GET | `/api/eventos/disponibles` | Asistente |
| CU-14 | Registrarse en un evento | POST | `/api/eventos/{id}/registros` | Asistente |
| CU-15 | Cancelar registro | PUT / DELETE | `/api/registros/{id}/cancelar` · `/api/registros/{id}` | Asistente |
| CU-16 | Consultar disponibilidad de cupo | GET | `/api/eventos/{id}/disponibilidad` | Ambos |
| CU-17 | Listar asistentes de un evento | GET | `/api/eventos/{id}/asistentes` | Ambos |
| CU-18 | Validar capacidad disponible | — | Interno (servicio) | Sistema |
| CU-19 | Validar registro único | — | Interno (servicio) | Sistema |

---

## 9. Glosario

| Término | Definición |
|---|---|
| **Evento** | Actividad programada en una fecha, hora y ubicación determinadas, con una capacidad máxima de asistentes. |
| **Ubicación** | Lugar físico donde se realiza un evento (nombre, dirección, ciudad). |
| **Asistente** | Persona registrada en el sistema que puede inscribirse en eventos. |
| **Registro** | Inscripción de un asistente a un evento. Entidad que resuelve la relación muchos a muchos entre Evento y Asistente. |
| **Capacidad máxima** | Número máximo de asistentes que puede tener un evento. |
| **Cupo disponible** | Capacidad máxima menos el número de registros activos. |
| **Estado del evento** | BORRADOR, PUBLICADO, CANCELADO o FINALIZADO. |
| **Estado del registro** | ACTIVO o CANCELADO. |
