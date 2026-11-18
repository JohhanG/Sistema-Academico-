package com.sistema.modelo;

import java.util.Objects;

// Entidad para la tabla inscripcion_curso (matriculas y calificaciones)
public class InscripcionCurso {

    private int idInscripcion;
    private int idEstudiante;
    private int idGrupo;
    private Double notaFinal;
    private String estado;

    // Datos adicionales para mostrar en las tablas y boletin de notas
    private String nombreEstudiante;
    private String infoGrupo;
    private String nombreMateria;
    private int creditos;
    private String nombreDocente;
    private String aulaHorario;

    public InscripcionCurso() {
        this.estado = "Matriculado";
    }

    public InscripcionCurso(int idEstudiante, int idGrupo) {
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.estado = "Matriculado";
        this.notaFinal = null;
    }

    public InscripcionCurso(int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado != null ? estado : "Matriculado";
    }

    public InscripcionCurso(int idInscripcion, int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

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

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public String getAulaHorario() {
        return aulaHorario;
    }

    public void setAulaHorario(String aulaHorario) {
        this.aulaHorario = aulaHorario;
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
