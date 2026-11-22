BEGIN;
-- ==========================================================
-- SISTEMA ACADÉMICO - MODELO DE BASE DE DATOS (SUPABASE / POSTGRESQL)
-- Basado en el diagrama de clases del profesor
-- Incluye Procedimientos/Funciones Almacenadas y Contraseñas con Hash SHA-512
-- ==========================================================

-- 1. Tabla: Materia
CREATE TABLE IF NOT EXISTS materia (
    id_materia SERIAL PRIMARY KEY,
    nombre_materia VARCHAR(100) NOT NULL,
    creditos INT NOT NULL CHECK (creditos BETWEEN 1 AND 10)
);

-- 2. Tabla: Docente
CREATE TABLE IF NOT EXISTS docente (
    id_docente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);

-- 3. Tabla: Estudiante
CREATE TABLE IF NOT EXISTS estudiante (
    id_estudiante SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL
);

-- 4. Tabla: Grupo
-- Relaciona Docente y Materia (Aula y Horario)
CREATE TABLE IF NOT EXISTS grupo (
    id_grupo SERIAL PRIMARY KEY,
    id_materia INT NOT NULL REFERENCES materia(id_materia) ON DELETE RESTRICT,
    id_docente INT NOT NULL REFERENCES docente(id_docente) ON DELETE RESTRICT,
    aula VARCHAR(50) NOT NULL,
    horario VARCHAR(100) NOT NULL
);

-- 5. Tabla: Inscripcion_Curso (Matrículas y Calificaciones)
-- Relaciona Estudiante y Grupo
CREATE TABLE IF NOT EXISTS inscripcion_curso (
    id_inscripcion SERIAL PRIMARY KEY,
    id_estudiante INT NOT NULL REFERENCES estudiante(id_estudiante) ON DELETE RESTRICT,
    id_grupo INT NOT NULL REFERENCES grupo(id_grupo) ON DELETE RESTRICT,
    nota_final NUMERIC(3, 1) CHECK (nota_final >= 0.0 AND nota_final <= 5.0),
    estado VARCHAR(30) NOT NULL DEFAULT 'Matriculado' CHECK (estado IN ('Matriculado', 'Aprobado', 'Reprobado', 'Cancelado')),
    CONSTRAINT uq_estudiante_grupo UNIQUE (id_estudiante, id_grupo)
);

-- 6. Tabla: Usuario / Roles (Autenticación y Seguridad con Hash SHA-512 / MD5)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'DOCENTE', 'ESTUDIANTE')),
    id_estudiante INT UNIQUE REFERENCES estudiante(id_estudiante) ON DELETE RESTRICT,
    id_docente INT UNIQUE REFERENCES docente(id_docente) ON DELETE RESTRICT,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ==========================================================
-- PROCEDIMIENTOS / FUNCIONES ALMACENADAS (STORED PROCEDURES)
-- Para carga de JComboBox / Selects en las Vistas Swing
-- ==========================================================

-- Procedimiento Almacenado 1: Cargar ComboBox de Estudiantes
CREATE OR REPLACE FUNCTION sp_combos_estudiantes()
RETURNS TABLE(id_estudiante INT, descripcion TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT e.id_estudiante,
           (e.id_estudiante || ' - ' || e.nombre || ' ' || e.apellido)::TEXT AS descripcion
    FROM estudiante e
    ORDER BY e.id_estudiante ASC;
END;
$$;

-- Procedimiento Almacenado 2: Cargar ComboBox de Grupos con Materia y Aula
CREATE OR REPLACE FUNCTION sp_combos_grupos()
RETURNS TABLE(id_grupo INT, descripcion TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT g.id_grupo,
           (g.id_grupo || ' - ' || m.nombre_materia || ' | Aula: ' || g.aula || ' (' || g.horario || ')')::TEXT AS descripcion
    FROM grupo g
    JOIN materia m ON g.id_materia = m.id_materia
    ORDER BY g.id_grupo ASC;
END;
$$;

-- Procedimiento Almacenado 3: Cargar ComboBox de Materias
CREATE OR REPLACE FUNCTION sp_combos_materias()
RETURNS TABLE(id_materia INT, descripcion TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT m.id_materia,
           (m.id_materia || ' - ' || m.nombre_materia || ' (' || m.creditos || ' cr.)')::TEXT AS descripcion
    FROM materia m
    ORDER BY m.id_materia ASC;
END;
$$;

-- Procedimiento Almacenado 4: Cargar ComboBox de Docentes
CREATE OR REPLACE FUNCTION sp_combos_docentes()
RETURNS TABLE(id_docente INT, descripcion TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT d.id_docente,
           (d.id_docente || ' - ' || d.nombre || ' (' || d.especialidad || ')')::TEXT AS descripcion
    FROM docente d
    ORDER BY d.id_docente ASC;
END;
$$;


CREATE UNIQUE INDEX IF NOT EXISTS uq_estudiante_email_normalizado ON estudiante (LOWER(email));
COMMIT;
