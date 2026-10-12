-- =====================================================================
-- V2 - Datos de ejemplo para la demo de la Entrega #2
-- Para reiniciar la base: docker compose down -v && docker compose up -d
-- =====================================================================

INSERT INTO usuario (id, username, password_hash, rol, activo, fecha_creacion) VALUES
    (1, 'admin',      '$2a$10$K7L0oQ0h7Hn2m0Qd1nK4eeYy0eY3Ld3y6bW1sYq0d4mHqzXb1Zzq3S', 'ADMINISTRADOR', TRUE, now()),
    (2, 'asistente1', '$2a$10$K7L0oQ0h7Hn2m0Qd1nK4eeYy0eY3Ld3y6bW1sYq0d4mHqzXb1Zzq3S', 'ASISTENTE',     TRUE, now());

INSERT INTO persona (id, tipo_persona, nombre, apellido, email, telefono, fecha_creacion, fecha_actualizacion, usuario_id) VALUES
    (1, 'ADMINISTRADOR', 'Erick',   'Trigueros', 'admin.eventos@ues.edu.sv',   '7777-1111', now(), now(), 1),
    (2, 'ASISTENTE',     'Azucena', 'Castillo',  'azucena.castillo@ues.edu.sv','7777-2222', now(), now(), 2),
    (3, 'ASISTENTE',     'Ronnie',  'Portillo',  'ronnie.portillo@ues.edu.sv', '7777-3333', now(), now(), NULL),
    (4, 'ASISTENTE',     'Angel',   'Serrano',   'angel.serrano@ues.edu.sv',   '7777-4444', now(), now(), NULL),
    (5, 'ASISTENTE',     'Carlos',  'Calzada',   'carlos.calzada@ues.edu.sv',  '7777-5555', now(), now(), NULL);

INSERT INTO administrador (id, departamento) VALUES
    (1, 'Departamento de Ingenieria y Arquitectura');

INSERT INTO asistente (id) VALUES (2), (3), (4), (5);

INSERT INTO ubicacion (id, nombre, direccion, ciudad, capacidad, latitud, longitud) VALUES
    (1, 'Auditorio Central',        'Final Av. Independencia, Ciudad Universitaria', 'Santa Ana', 250, 13.9945, -89.5597),
    (2, 'Laboratorio de Computo 3', 'Edificio de Ingenieria, segundo nivel',         'Santa Ana', 40,  13.9950, -89.5602);

INSERT INTO evento (id, ubicacion_id, nombre, descripcion, fecha_inicio, fecha_fin, capacidad_maxima, estado, fecha_creacion, fecha_actualizacion) VALUES
    (1, 1, 'Conferencia de Ingenieria de Software', 'Charla sobre tendencias de desarrollo y DevOps.',
        now() + interval '45 days', now() + interval '45 days' + interval '3 hours', 150, 'BORRADOR', now(), now()),
    (2, 2, 'Taller de Spring Boot', 'Taller practico de APIs REST con Spring Boot y PostgreSQL.',
        now() + interval '30 days', now() + interval '30 days' + interval '5 hours', 3, 'PUBLICADO', now(), now()),
    (3, 1, 'Feria de Proyectos 2025', 'Presentacion de proyectos del ciclo anterior.',
        now() - interval '10 days', now() - interval '10 days' + interval '4 hours', 100, 'FINALIZADO', now(), now()),
    (4, 2, 'Charla de Seguridad Informatica', 'Evento publicado con el cupo ya completo (para probar el error 409).',
        now() + interval '60 days', now() + interval '60 days' + interval '2 hours', 1, 'PUBLICADO', now(), now());

INSERT INTO registro (id, evento_id, asistente_id, estado, fecha_registro, fecha_cancelacion) VALUES
    (1, 2, 2, 'ACTIVO',    now() - interval '2 days', NULL),
    (2, 2, 3, 'CANCELADO', now() - interval '3 days', now() - interval '1 day'),
    (3, 4, 4, 'ACTIVO',    now() - interval '1 day',  NULL);

-- Las columnas son IDENTITY: despues de insertar IDs explicitos hay que
-- reajustar la secuencia para que la API pueda seguir insertando.
SELECT setval(pg_get_serial_sequence('usuario',   'id'), (SELECT MAX(id) FROM usuario));
SELECT setval(pg_get_serial_sequence('persona',   'id'), (SELECT MAX(id) FROM persona));
SELECT setval(pg_get_serial_sequence('ubicacion', 'id'), (SELECT MAX(id) FROM ubicacion));
SELECT setval(pg_get_serial_sequence('evento',    'id'), (SELECT MAX(id) FROM evento));
SELECT setval(pg_get_serial_sequence('registro',  'id'), (SELECT MAX(id) FROM registro));
