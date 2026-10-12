# Instrucciones para subir tu parte — Entrega #2

Este documento explica, paso a paso, qué archivos le toca subir a cada integrante y los comandos exactos.
La rama `main` ya tiene la base del proyecto (Maven, configuración, migraciones, modelo, repositorios, DTOs y mappers);
cada integrante agrega en su rama la **capa de servicio, los endpoints, las pruebas y los casos de Postman** de su módulo.

## Archivos que sube cada integrante

| Integrante | Rama | Archivos |
| :--- | :--- | :--- |
| **Azucena Castillo** (`CZ08008`) | `feat/ubicacion` | `UbicacionService.java`, `UbicacionController.java`, `UbicacionServiceTest.java`, `postman/casos-ubicacion.postman_collection.json` |
| **Ronnie Portillo** (`PC15016`) | `feat/evento` | `EventoService.java`, `EventoController.java`, `EventoServiceTest.java`, `postman/casos-evento.postman_collection.json` |
| **Ángel Serrano** (`SB22013`) | `feat/asistente` | `AsistenteService.java`, `AsistenteController.java`, `AsistenteServiceTest.java`, `postman/casos-asistente.postman_collection.json` |
| **Carlos Enoc Calzada** (`CV19058`) | `feat/registro` | `RegistroService.java`, `RegistroController.java`, `RegistroServiceTest.java`, `postman/casos-registro.postman_collection.json` |

Todos los archivos `*.java` están en `src/main/java/com/ues/poo/eventos/service` y `.../controller`, y los de prueba en
`src/test/java/com/ues/poo/eventos/service`.

## Antes de empezar

1. Tener el proyecto clonado: `git clone https://github.com/pc15016-Ronnie/POO-Proyecto-Final-Grupo-6.git`
2. Configurar tu identidad, con **el mismo correo de tu cuenta de GitHub** (para que el aporte quede a tu nombre):

```bash
git config user.name "Tu Nombre"
git config user.email "tu-correo@ejemplo.com"
```

3. Instalar JDK 21, Maven, Docker y (opcional) Postman Desktop.

## Paso a paso (ejemplo con tu módulo)

Reemplaza `<modulo>` por `ubicacion`, `evento`, `asistente` o `registro`, y `<rutas>` por los archivos de tu fila
de la tabla de arriba. Los tres comandos son iguales para todos:

```bash
# 1) Traer la última versión de main y crear tu rama
git checkout main
git pull origin main
git checkout -b feat/<modulo>

# 2) Copiar desde la rama temporal los archivos de tu módulo
git checkout origin/code/modulos -- <rutas>

# 3) Revisar, probar y subir
git status
mvn test
git add .
git commit -m "feat(<modulo>): servicio, endpoints y pruebas de <modulo>"
git push -u origin feat/<modulo>
```

Ejemplo completo del módulo de ubicación:

```bash
git checkout origin/code/modulos -- \
  src/main/java/com/ues/poo/eventos/service/UbicacionService.java \
  src/main/java/com/ues/poo/eventos/controller/UbicacionController.java \
  src/test/java/com/ues/poo/eventos/service/UbicacionServiceTest.java \
  postman/casos-ubicacion.postman_collection.json
```

Luego abre el pull request en GitHub: **base `main`** ← **compare `feat/<modulo>`**.

## Qué revisar antes de subir

- Leer tu `Service` y tu `Controller` hasta poder explicar qué validaciones hace cada endpoint: la defensa individual
  depende de eso.
- Correr `mvn test` y confirmar que las pruebas de tu módulo pasan.
- Si tienes Postman, levantar la base (`docker compose up -d`), arrancar la app (`mvn spring-boot:run`) e importar
  `postman/local.postman_environment.json` junto con tu archivo `postman/casos-<modulo>.postman_collection.json`.
- Agregar tu aporte propio: una prueba adicional, un caso de Postman extra o una mejora en los mensajes de error.

## Revisión cruzada y merge

| Pull request | Revisa |
| :--- | :--- |
| `feat/registro` (Enoc) | Ronnie |
| `feat/evento` (Ronnie) | Enoc |
| `feat/ubicacion` (Azucena) | Ángel |
| `feat/asistente` (Ángel) | Azucena |

El revisor deja su comentario de aprobación en el pull request y luego se hace **Squash and merge** a `main`.
Cuando los cuatro estén mergeados, se corre `mvn test` en `main`, se ejecuta la colección completa de Postman y se
elimina la rama temporal `code/modulos` (la versión completa queda en el historial de `main`).

## Si algo falla

| Problema | Solución |
| :--- | :--- |
| `git checkout origin/code/modulos -- <rutas>` no encuentra archivos | Ejecutar `git fetch origin` y repetir. |
| `mvn test` falla por dependencias | Revisar que estés en `main` actualizado o pedir ayuda al grupo. |
| No aparece tu aporte en la gráfica de contribuciones | Verificar que el commit use el correo de tu cuenta de GitHub. |
| No tienes Postman | Usar la versión web (`web.postman.co`) o pedir el archivo a un compañero. |
