package com.sistema.modelo;

/**
 * Representa la entidad Estudiante del Sistema Académico.
 * Mapea la tabla 'estudiante' de la base de datos PostgreSQL en Supabase.
 */
public class Estudiante {

    private int idEstudiante;
    private String nombre;
    private String apellido;
    private String email;

    // Constructor vacío
    public Estudiante() {
    }

    // Constructor sin ID (usado para inserciones donde el ID es SERIAL / autoincremental)
    public Estudiante(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Constructor completo
    public Estudiante(int idEstudiante, String nombre, String apellido, String email) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Getters y Setters
    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

