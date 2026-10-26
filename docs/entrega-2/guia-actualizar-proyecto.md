# Guía para actualizar el proyecto a la última versión de `main`

> **Para quien recibe esta guía:** vas a actualizar la copia local del proyecto
> **API para Gestión de Eventos y Asistentes** (Grupo 6, POO135) en la computadora de un integrante del equipo.
>
> **Objetivo:** dejar el repositorio local exactamente en el último `main` publicado, con los cuatro módulos
> completos, el `pom.xml` correcto, la base de datos reproducible y las pruebas en verde. No hay que crear ramas
> nuevas ni volver a copiar archivos desde `code/modulos`: todo eso ya está mergeado en `main`.
>
> **Sistema operativo:** los comandos están escritos para **Windows PowerShell**. Si usas Git Bash, macOS o Linux,
> los comandos de Git son los mismos; cambian únicamente los comandos de verificación de archivos
> (`Test-Path`/`Select-String` vs `ls`/`grep`).

---

## 1. Datos del repositorio

- Repositorio: `https://github.com/pc15016-Ronnie/POO-Proyecto-Final-Grupo-6.git`
- Rama de trabajo: `main`
- Versión de referencia: el último commit de `main`. Al crear esta guía, `main` estaba en **`bea05a5`**;
  comprueba el valor actual con `git log -1 --oneline origin/main`.
- Stack: Java 21, Spring Boot 3.3.5, Maven, PostgreSQL 16, Flyway, Lombok, DTOs, Postman.

Si `main` tiene commits más nuevos cuando se ejecute esta guía, el objetivo es quedar en el **último** `main`,
no exactamente en `bea05a5`. La verificación correcta es que el `HEAD` local coincida con `origin/main`.

---

## 2. Reglas estrictas para el agente

- **No** ejecutes `git reset --hard`, `git clean -fd`, `git checkout -- .` ni borres el repositorio.
- **No** sobrescribas cambios locales sin avisar: si `git status` muestra archivos modificados, detente y repórtalo.
- **No** vuelvas a ejecutar la receta de `code/modulos`.
- **No** cambies el `pom.xml`, la versión de Spring Boot ni el nombre del paquete.
- **No** hagas commits ni push en esta tarea: solo actualizar y verificar.
- Si algún comando falla, copia el error completo y detente; no improvises otra solución.

---

## 3. Paso 0 — Verificar que la copia local está segura

Abrir PowerShell en la carpeta del repositorio (donde existe la carpeta `.git`) y ejecutar:

```powershell
git status
git branch --show-current
git log --oneline -3
```

Interpretación:

- Si `git status` dice *working tree clean*, se puede continuar.
- Si aparecen archivos modificados o sin seguimiento, **no continuar**: el agente debe guardar esos cambios
  (`git stash push -m "cambios locales antes de actualizar"`) o pedir instrucciones al integrante.
- La rama actual puede ser `main` o una rama `feat/...` ya mergeada; en el siguiente paso se cambia a `main`.

Si la persona no tiene el repositorio clonado:

```powershell
cd $env:USERPROFILE\Documents
git clone https://github.com/pc15016-Ronnie/POO-Proyecto-Final-Grupo-6.git
cd POO-Proyecto-Final-Grupo-6
```

---

## 4. Paso 1 — Actualizar `main`

```powershell
git checkout main
git fetch origin --prune
git pull --ff-only origin main
```

Resultado esperado: el pull debe ser **fast-forward** y terminar con `Already up to date` o mostrando los commits nuevos.

Si el pull falla porque la rama local divergió, **no forzar**: ejecutar `git status`, `git log --oneline --graph --all -10`
y reportar el estado.

---

## 5. Paso 2 — Verificar que quedó en la misma versión

```powershell
git log -1 --oneline
git rev-parse HEAD
git rev-parse origin/main
git status
```

Requisitos:

- `git rev-parse HEAD` y `git rev-parse origin/main` deben devolver **el mismo hash**.
- `git status` debe estar limpio (pueden aparecer `target/` o `.idea/` como ignorados; eso no cuenta).

Comprobar que los cuatro módulos y los archivos clave existen:

```powershell
Test-Path src/main/java/com/ues/poo/eventos/controller/UbicacionController.java
Test-Path src/main/java/com/ues/poo/eventos/controller/EventoController.java
Test-Path src/main/java/com/ues/poo/eventos/controller/AsistenteController.java
Test-Path src/main/java/com/ues/poo/eventos/controller/RegistroController.java
Test-Path src/test/java/com/ues/poo/eventos/service/AsistenteServiceTest.java
Test-Path docs/entrega-1/erd-conceptual.png
Test-Path docs/entrega-1/erd-relacional.png
Test-Path src/main/resources/application.properties
```

Los siete primeros deben devolver `True`. El último (`application.properties`) debe devolver **`False`**:
ese archivo se eliminó porque forzaba H2 en lugar de PostgreSQL.

Comprobar que el POM es el correcto:

```powershell
Select-String -Path pom.xml -Pattern "<version>3.3.5</version>"
Select-String -Path pom.xml -Pattern "<project " | Measure-Object
```

Debe aparecer **un solo** `<project>` y la versión `3.3.5`.

---

## 6. Paso 3 — Compilar y ejecutar las pruebas

```powershell
mvn test
```

Si no hay Maven instalado:

```powershell
.\mvnw.cmd test
```

Resultado esperado:

```text
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Distribución: 6 pruebas de Ubicación, 5 de Evento, 3 de Asistente y 8 de Registro.

Si el número es menor, o hay errores de compilación, detenerse y reportar el error completo.

---

## 7. Paso 4 — Dejar la base de datos limpia y reproducible

Con Docker Desktop abierto:

```powershell
docker compose down -v
docker compose up -d
docker compose ps
```

> **Advertencia:** `down -v` borra la base local de pruebas de este proyecto y su volumen.
> Es lo recomendado antes de la tutoría para que Flyway vuelva a sembrar los datos de ejemplo.
> Si la persona tiene datos que quiere conservar, usar solo `docker compose down` y `docker compose up -d`.

Debe aparecer el contenedor `eventos-db` en estado **`healthy`**.

Si el puerto 5432 está ocupado, detener el otro PostgreSQL local o cambiar el puerto en `docker-compose.yml`
y en `DB_URL`; no continuar hasta que el contenedor esté sano.

---

## 8. Paso 5 — Arrancar la API y hacer una prueba rápida

En una terminal:

```powershell
mvn spring-boot:run
```

O:

```powershell
.\mvnw.cmd spring-boot:run
```

Esperar estos mensajes:

```text
Successfully applied 2 migrations
Started EventosApiApplication
Tomcat started on port 8080
```

En **otra** terminal:

```powershell
Invoke-RestMethod http://localhost:8080/api/eventos | ConvertTo-Json
```

Debe devolver una lista de eventos (200).

Crear un asistente de prueba:

```powershell
$email = "equipo." + [DateTimeOffset]::UtcNow.ToUnixTimeSeconds() + "@ues.edu.sv"
$body = '{"nombre":"Equipo","apellido":"Prueba","email":"' + $email + '","telefono":"7000-0000"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/asistentes" -Method Post -ContentType "application/json" -Body $body
```

Debe responder con `201` y un objeto con `id`, `nombre`, `apellido` y `email`.

Si responde `409`, el correo ya existía: cambiar el correo y repetir.

---

## 9. Paso 6 — Probar la colección de Postman (opcional pero recomendado)

Con la API corriendo:

1. Abrir Postman (o `web.postman.co`).
2. Importar `postman/local.postman_environment.json`.
3. Importar `postman/Grupo6-eventos.postman_collection.json`.
4. Ejecutar la carpeta **Flujo demo (en orden)**.

Debe completar sin errores: crear ubicación, evento, asistente, registrar, consultar disponibilidad, probar el
duplicado (409), llenar el cupo (409), cancelar (200), reinscribir (201) y eliminar el registro cancelado (204).

Alternativa por consola, si se tiene Node.js:

```powershell
npx --yes newman run postman/Grupo6-eventos.postman_collection.json -e postman/local.postman_environment.json
```

Resultado esperado:

```text
requests:    36
assertions: 59
failed:      0
```

---

## 10. Paso 7 — Limpieza opcional de ramas ya mergeadas

Cada integrante puede borrar su rama local ya mergeada (no es obligatorio):

```powershell
git branch -d feat/ubicacion
git branch -d feat/evento
git branch -d feat/asistente
git branch -d feat/registro
```

Si una rama no está completamente mergeada, `git branch -d` se negará; **no usar `-D`**, solo reportar.
Las ramas remotas se pueden borrar desde GitHub, pero no es necesario para la entrega.

---

## 11. Checklist final

- [ ] `git checkout main` y `git pull --ff-only origin main` ejecutados sin errores.
- [ ] `HEAD` local y `origin/main` tienen el mismo hash.
- [ ] `git status` limpio.
- [ ] Los cuatro controladores y los cuatro tests existen.
- [ ] `application.properties` **no** existe; solo `application.yml`.
- [ ] `pom.xml` tiene un solo `<project>` y Spring Boot 3.3.5.
- [ ] `mvn test` → **22 pruebas, 0 errores**.
- [ ] `docker compose ps` → `eventos-db` en `healthy`.
- [ ] La API arranca en el puerto 8080.
- [ ] `GET /api/eventos` → 200.
- [ ] `POST /api/asistentes` → 201.
- [ ] La colección de Postman (o Newman) corre sin fallos.
- [ ] No se hizo ningún commit ni push en esta tarea.

---

## 12. Problemas frecuentes

| Síntoma | Solución |
|---|---|
| `git pull` falla por cambios locales | Detenerse, guardar los cambios con `git stash` o commit, y reportar. No forzar. |
| `HEAD` no coincide con `origin/main` | Revisar la rama actual (`git branch --show-current`) y repetir el Paso 1. |
| `mvn` no se reconoce | Usar `.\mvnw.cmd` o instalar Maven 3.9. |
| Error de Java | Verificar `java -version`: debe ser 21. |
| `Port 5432 already in use` | Detener otro PostgreSQL local o cambiar el puerto en `docker-compose.yml` y `DB_URL`. |
| `Port 8080 already in use` | Cerrar el proceso anterior o arrancar con `--server.port=8081`. |
| Error de checksum de Flyway | `docker compose down -v` y `docker compose up -d`. |
| `Schema validation: missing column` | Reiniciar la base con `docker compose down -v`. |
| Postman responde 409 al crear asistente | El correo ya existe: usar uno nuevo. |

---

## 13. Mensaje final que debe devolver el agente

Al terminar, reportar exactamente:

1. Hash de `HEAD` y de `origin/main` (deben coincidir).
2. Resultado de `git status`.
3. Resultado de `mvn test` (número de pruebas y estado).
4. Estado de `docker compose ps`.
5. Resultado de la prueba rápida (`GET /api/eventos` y `POST /api/asistentes`).
6. Resultado de la colección de Postman o Newman, si se ejecutó.
7. Cualquier error o diferencia encontrada respecto a esta guía.
