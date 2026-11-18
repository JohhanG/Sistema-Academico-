package com.sistema.servicio;

import com.sistema.modelo.Materia;
import com.sistema.modelo.MateriaDAO;

import java.util.List;

// Servicio con las validaciones de negocio para Materia
public class MateriaService {

    private final MateriaDAO materiaDAO;

    public MateriaService() {
        this.materiaDAO = new MateriaDAO();
    }

    public MateriaService(MateriaDAO materiaDAO) {
        if (materiaDAO == null) {
            throw new IllegalArgumentException("El DAO de materia no puede ser nulo.");
        }
        this.materiaDAO = materiaDAO;
    }

    public boolean registrarMateria(Materia materia) {
        validarMateria(materia);
        materia.setNombreMateria(materia.getNombreMateria().trim());
        return materiaDAO.insertar(materia);
    }

    public List<Materia> listarMaterias() {
        return materiaDAO.listar();
    }

    public Materia buscarMateriaPorId(int idMateria) {
        if (idMateria <= 0) {
            throw new IllegalArgumentException("El ID de la materia debe ser mayor a cero.");
        }
        return materiaDAO.buscarPorId(idMateria);
    }

    public boolean actualizarMateria(Materia materia) {
        if (materia == null || materia.getIdMateria() <= 0) {
            throw new IllegalArgumentException("El ID de la materia no es valido.");
        }
        validarMateria(materia);
        materia.setNombreMateria(materia.getNombreMateria().trim());
        return materiaDAO.actualizar(materia);
    }

    public boolean eliminarMateria(int idMateria) {
        if (idMateria <= 0) {
            throw new IllegalArgumentException("El ID de la materia debe ser mayor a cero.");
        }
        return materiaDAO.eliminar(idMateria);
    }

    private void validarMateria(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia no puede ser nula.");
        }
        if (materia.getNombreMateria() == null || materia.getNombreMateria().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        if (materia.getCreditos() <= 0) {
            throw new IllegalArgumentException("Los creditos deben ser mayores a 0.");
        }
    }
}
