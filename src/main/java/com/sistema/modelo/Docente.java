package com.sistema.modelo;

import java.util.Objects;

/**
 * Entidad que representa a un Docente en el sistema académico.
 * Mapea la tabla 'docente' en la base de datos PostgreSQL (Supabase).
 */
public class Docente {

    private int idDocente;
    private String nombre;
    private String especialidad;

    // Constructor vacío
    public Docente() {
    }

    // Constructor sin ID (para nuevas inserciones)
    public Docente(String nombre, String especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Constructor completo
    public Docente(int idDocente, String nombre, String especialidad) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Getters y Setters
    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Docente docente = (Docente) o;
        return idDocente == docente.idDocente &&
                Objects.equals(nombre, docente.nombre) &&
                Objects.equals(especialidad, docente.especialidad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDocente, nombre, especialidad);
    }

    @Override
    public String toString() {
        return "Docente{" +
                "idDocente=" + idDocente +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}
