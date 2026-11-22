-- Solo para una base de desarrollo. No ejecutar en el proyecto Supabase compartido.
-- Es repetible, no sustituye contrasenas ni modifica registros ya existentes.
BEGIN;
SELECT pg_advisory_xact_lock(20261122);
INSERT INTO materia(nombre_materia,creditos)
SELECT v.nombre,v.creditos FROM (VALUES ('Programacion II',4),('Bases de Datos II',3)) v(nombre,creditos)
WHERE NOT EXISTS (SELECT 1 FROM materia m WHERE m.nombre_materia=v.nombre);
INSERT INTO docente(nombre,especialidad)
SELECT v.nombre,v.especialidad FROM (VALUES ('Carlos Gomez','Software'),('Marta Rodriguez','Bases de datos')) v(nombre,especialidad)
WHERE NOT EXISTS (SELECT 1 FROM docente d WHERE d.nombre=v.nombre AND d.especialidad=v.especialidad);
INSERT INTO estudiante(nombre,apellido,email)
VALUES ('Johhan','Gonzalez','johhan@universidad.edu.co'),('Andres','Perez','andres@universidad.edu.co')
ON CONFLICT DO NOTHING;
INSERT INTO grupo(id_materia,id_docente,aula,horario)
SELECT m.id_materia,d.id_docente,'Lab 301','Lunes 08:00 - 10:00'
FROM materia m,docente d WHERE m.nombre_materia='Programacion II' AND d.nombre='Carlos Gomez'
AND NOT EXISTS (SELECT 1 FROM grupo g WHERE g.id_materia=m.id_materia AND g.id_docente=d.id_docente AND g.aula='Lab 301' AND g.horario='Lunes 08:00 - 10:00');
INSERT INTO inscripcion_curso(id_estudiante,id_grupo,nota_final,estado)
SELECT e.id_estudiante,g.id_grupo,4.5,'Aprobado' FROM estudiante e
JOIN grupo g ON g.aula='Lab 301' AND g.horario='Lunes 08:00 - 10:00'
WHERE e.email='johhan@universidad.edu.co'
ON CONFLICT (id_estudiante,id_grupo) DO NOTHING;
-- Contrasena de demostracion: admin123. Nunca usar estas cuentas en una base real.
INSERT INTO usuario(username,password,rol) VALUES
('admin','7fcf4ba391c48784edde599889d6e3f1e47a27db36ecc050cc92f259bfac38afad2c68a1ae804d77075e8fb722503f3eca2b2c1006ee6f6c7b7628cb45fffd1d','ADMIN')
ON CONFLICT (username) DO NOTHING;
COMMIT;
