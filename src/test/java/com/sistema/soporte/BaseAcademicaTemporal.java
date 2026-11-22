package com.sistema.soporte;

import com.sistema.config.ConexionBD;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

/** Conexion aislada para pruebas. Nunca lee las credenciales de config.properties. */
public final class BaseAcademicaTemporal implements AutoCloseable {
    private final Field campo;
    private final Field modoUI;
    private final boolean modoAnterior;
    private final Connection anterior;
    public final Connection conexion;

    public BaseAcademicaTemporal() throws Exception {
        modoUI=com.sistema.vista.TrabajoUI.class.getDeclaredField("sincronoParaPruebas");
        modoUI.setAccessible(true);
        modoAnterior=modoUI.getBoolean(null);
        modoUI.setBoolean(null,true);
        conexion = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL", "sa", "");
        campo = ConexionBD.class.getDeclaredField("conexion");
        campo.setAccessible(true);
        anterior = (Connection) campo.get(null);
        try (Statement s = conexion.createStatement()) {
            s.execute("CREATE TABLE estudiante (id_estudiante SERIAL PRIMARY KEY, nombre VARCHAR(100), apellido VARCHAR(100), email VARCHAR(150) UNIQUE)");
            s.execute("CREATE TABLE docente (id_docente SERIAL PRIMARY KEY, nombre VARCHAR(100), especialidad VARCHAR(100))");
            s.execute("CREATE TABLE materia (id_materia SERIAL PRIMARY KEY, nombre_materia VARCHAR(100), creditos INT)");
            s.execute("CREATE TABLE grupo (id_grupo SERIAL PRIMARY KEY, id_materia INT REFERENCES materia, id_docente INT REFERENCES docente, aula VARCHAR(50), horario VARCHAR(100))");
            s.execute("CREATE TABLE inscripcion_curso (id_inscripcion SERIAL PRIMARY KEY, id_estudiante INT REFERENCES estudiante, id_grupo INT REFERENCES grupo, nota_final NUMERIC(3,1), estado VARCHAR(30), UNIQUE(id_estudiante,id_grupo))");
            s.execute("CREATE TABLE usuario (id_usuario SERIAL PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(255), rol VARCHAR(20), id_estudiante INT UNIQUE REFERENCES estudiante, id_docente INT UNIQUE REFERENCES docente)");
            s.execute("INSERT INTO estudiante(nombre,apellido,email) VALUES ('Ana','Prueba','ana@example.test'),('Luis','Prueba','luis@example.test')");
            s.execute("INSERT INTO docente(nombre,especialidad) VALUES ('Docente Uno','Programacion'),('Docente Dos','Bases de datos')");
            s.execute("INSERT INTO materia(nombre_materia,creditos) VALUES ('Programacion',3),('Bases de datos',3)");
            s.execute("INSERT INTO grupo(id_materia,id_docente,aula,horario) VALUES (1,1,'A1','Lunes'),(2,2,'A2','Martes')");
            s.execute("INSERT INTO inscripcion_curso(id_estudiante,id_grupo,nota_final,estado) VALUES (1,1,4.0,'Aprobado'),(1,2,3.5,'Aprobado'),(2,2,NULL,'Matriculado')");
        }
        campo.set(null, conexion);
    }

    @Override public void close() throws Exception {
        campo.set(null, anterior);
        modoUI.setBoolean(null,modoAnterior);
        conexion.close();
    }
}
