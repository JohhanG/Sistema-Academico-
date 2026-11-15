package com.sistema.modelo;

import java.util.Objects;

/**
 * Entidad que representa una Inscripción / Matrícula de un Estudiante a un Curso (Grupo).
 * Mapea la tabla 'inscripcion_curso' de PostgreSQL (Supabase).
 */
public class InscripcionCurso {

    private int idInscripcion;
    private int idEstudiante;
    private int idGrupo;
    private Double notaFinal; // Nullable hasta que se registre la nota
    private String estado;    // 'Matriculado', 'Aprobado', 'Reprobado', 'Cancelado'

    // Atributos informativos para visualización en vistas / tablas (JOINs)
    private String nombreEstudiante;
    private String infoGrupo; // ej: "Programación II - Lab 301"

    // Constructor vacío
    public InscripcionCurso() {
        this.estado = "Matriculado";
    }

    // Constructor para nueva matrícula (sin ID ni nota inicial)
    public InscripcionCurso(int idEstudiante, int idGrupo) {
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.estado = "Matriculado";
        this.notaFinal = null;
    }

    // Constructor con nota y estado
    public InscripcionCurso(int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado != null ? estado : "Matriculado";
    }

    // Constructor completo con ID
    public InscripcionCurso(int idInscripcion, int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getInfoGrupo() {
        return infoGrupo;
    }

    public void setInfoGrupo(String infoGrupo) {
        this.infoGrupo = infoGrupo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripcionCurso that = (InscripcionCurso) o;
        return idInscripcion == that.idInscripcion &&
                idEstudiante == that.idEstudiante &&
                idGrupo == that.idGrupo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInscripcion, idEstudiante, idGrupo);
    }

    @Override
    public String toString() {
        return "InscripcionCurso{" +
                "idInscripcion=" + idInscripcion +
                ", idEstudiante=" + idEstudiante +
                ", idGrupo=" + idGrupo +
                ", notaFinal=" + notaFinal +
                ", estado='" + estado + '\'' +
                '}';
    }
}
