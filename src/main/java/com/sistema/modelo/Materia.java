package com.sistema.modelo;

import java.util.Objects;

/**
 * Entidad que representa una Materia / Asignatura en el sistema académico.
 * Mapea la tabla 'materia' en la base de datos PostgreSQL (Supabase).
 */
public class Materia {

    private int idMateria;
    private String nombreMateria;
    private int creditos;

    // Constructor vacío
    public Materia() {
    }

    // Constructor sin ID (usado para nuevas inserciones donde el ID es SERIAL)
    public Materia(String nombreMateria, int creditos) {
        this.nombreMateria = nombreMateria;
        this.creditos = creditos;
    }

    // Constructor completo
    public Materia(int idMateria, String nombreMateria, int creditos) {
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
        this.creditos = creditos;
    }

    // Getters y Setters
    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        return idMateria == materia.idMateria &&
                creditos == materia.creditos &&
                Objects.equals(nombreMateria, materia.nombreMateria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateria, nombreMateria, creditos);
    }

    @Override
    public String toString() {
        return "Materia{" +
                "idMateria=" + idMateria +
                ", nombreMateria='" + nombreMateria + '\'' +
                ", creditos=" + creditos +
                '}';
    }
}
