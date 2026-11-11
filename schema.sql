-- ==========================================================
-- SISTEMA ACADÉMICO - MODELO DE BASE DE DATOS (SUPABASE / POSTGRESQL)
-- Basado en el diagrama de clases del profesor
-- ==========================================================

-- 1. Tabla: Materia
CREATE TABLE IF NOT EXISTS materia (
    id_materia SERIAL PRIMARY KEY,
    nombre_materia VARCHAR(100) NOT NULL,
    creditos INT NOT NULL CHECK (creditos > 0)
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
    id_materia INT NOT NULL REFERENCES materia(id_materia) ON DELETE CASCADE,
    id_docente INT NOT NULL REFERENCES docente(id_docente) ON DELETE RESTRICT,
    aula VARCHAR(50) NOT NULL,
    horario VARCHAR(100) NOT NULL
);

-- 5. Tabla: Inscripcion_Curso (Matrículas y Calificaciones)
-- Relaciona Estudiante y Grupo
CREATE TABLE IF NOT EXISTS inscripcion_curso (
    id_inscripcion SERIAL PRIMARY KEY,
    id_estudiante INT NOT NULL REFERENCES estudiante(id_estudiante) ON DELETE CASCADE,
    id_grupo INT NOT NULL REFERENCES grupo(id_grupo) ON DELETE CASCADE,
    nota_final NUMERIC(3, 1) CHECK (nota_final >= 0.0 AND nota_final <= 5.0),
    estado VARCHAR(30) NOT NULL DEFAULT 'Matriculado' CHECK (estado IN ('Matriculado', 'Aprobado', 'Reprobado', 'Cancelado')),
    CONSTRAINT uq_estudiante_grupo UNIQUE (id_estudiante, id_grupo)
);

-- 6. Tabla: Usuario / Roles (Autenticación y Seguridad)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'DOCENTE', 'ESTUDIANTE')),
    id_estudiante INT UNIQUE REFERENCES estudiante(id_estudiante) ON DELETE CASCADE,
    id_docente INT UNIQUE REFERENCES docente(id_docente) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ==========================================================
-- DATOS DE PRUEBA INICIALES (SEMILLAS)
-- ==========================================================

-- Insertar Materias
INSERT INTO materia (nombre_materia, creditos) VALUES
('Programación II', 4),
('Bases de Datos II', 3),
('Estructura de Datos', 4)
ON CONFLICT DO NOTHING;

-- Insertar Docentes
INSERT INTO docente (nombre, especialidad) VALUES
('Carlos Gómez', 'Ingeniería de Software'),
('Marta Rodríguez', 'Gestión de Bases de Datos')
ON CONFLICT DO NOTHING;

-- Insertar Estudiantes
INSERT INTO estudiante (nombre, apellido, email) VALUES
('Johhan', 'González', 'johhan@universidad.edu.co'),
('Andrés', 'Pérez', 'andres@universidad.edu.co'),
('Valentina', 'López', 'valentina@universidad.edu.co')
ON CONFLICT (email) DO NOTHING;

-- Insertar Grupos
INSERT INTO grupo (id_materia, id_docente, aula, horario) VALUES
(1, 1, 'Laboratorio 301', 'Lunes y Miércoles 08:00 - 10:00'),
(2, 2, 'Aula 204', 'Martes y Jueves 10:00 - 12:00')
ON CONFLICT DO NOTHING;

-- Insertar Inscripciones
INSERT INTO inscripcion_curso (id_estudiante, id_grupo, nota_final, estado) VALUES
(1, 1, 4.5, 'Aprobado'),
(2, 1, 3.8, 'Aprobado'),
(3, 2, NULL, 'Matriculado')
ON CONFLICT (id_estudiante, id_grupo) DO NOTHING;

-- Insertar Usuarios / Roles
INSERT INTO usuario (username, password, rol, id_estudiante, id_docente) VALUES
('admin', 'admin123', 'ADMIN', NULL, NULL),
('cgomez', 'docente123', 'DOCENTE', NULL, 1),
('johhang', 'estudiante123', 'ESTUDIANTE', 1, NULL)
ON CONFLICT (username) DO NOTHING;
