package com.sistema.servicio;

import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;

import java.util.List;

// Servicio con las validaciones de negocio para Docente
public class DocenteService {

    private final DocenteDAO docenteDAO;

    public DocenteService() {
        this.docenteDAO = new DocenteDAO();
    }

    public DocenteService(DocenteDAO docenteDAO) {
        if (docenteDAO == null) {
            throw new IllegalArgumentException("El DAO de docente no puede ser nulo.");
        }
        this.docenteDAO = docenteDAO;
    }

    public boolean registrarDocente(Docente docente) {
        validarDocente(docente);
        docente.setNombre(docente.getNombre().trim());
        docente.setEspecialidad(docente.getEspecialidad().trim());
        return docenteDAO.insertar(docente);
    }

    public List<Docente> listarDocentes() {
        return docenteDAO.listar();
    }

    public Docente buscarDocentePorId(int idDocente) {
        if (idDocente <= 0) {
            throw new IllegalArgumentException("El ID del docente debe ser mayor a cero.");
        }
        return docenteDAO.buscarPorId(idDocente);
    }

    public boolean actualizarDocente(Docente docente) {
        if (docente == null || docente.getIdDocente() <= 0) {
            throw new IllegalArgumentException("El ID del docente no es valido.");
        }
        validarDocente(docente);
        docente.setNombre(docente.getNombre().trim());
        docente.setEspecialidad(docente.getEspecialidad().trim());
        return docenteDAO.actualizar(docente);
    }

    public boolean eliminarDocente(int idDocente) {
        if (idDocente <= 0) {
            throw new IllegalArgumentException("El ID del docente debe ser mayor a cero.");
        }
        return docenteDAO.eliminar(idDocente);
    }

    private void validarDocente(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("El docente no puede ser nulo.");
        }
        if (docente.getNombre() == null || docente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del docente es obligatorio.");
        }
        if (docente.getEspecialidad() == null || docente.getEspecialidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad del docente es obligatoria.");
        }
    }
}
