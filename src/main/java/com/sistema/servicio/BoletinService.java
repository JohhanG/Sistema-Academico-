package com.sistema.servicio;

import com.sistema.modelo.*;
import java.util.*;
import java.util.stream.Collectors;

/** Compone los datos del boletin fuera de la vista y respeta los permisos de la sesion. */
public class BoletinService {
    public record Boletin(Estudiante estudiante,List<InscripcionCurso> materias) { }
    private final EstudianteDao estudiantes = new EstudianteDao();
    private final InscripcionCursoService inscripciones = new InscripcionCursoService();

    public List<Boletin> cargar(Usuario usuario) {
        List<InscripcionCurso> permitidas=inscripciones.listarParaUsuario(usuario);
        List<Estudiante> personas=new ArrayList<>();
        if ("ESTUDIANTE".equalsIgnoreCase(usuario.getRol())) {
            personas.add(new IdentidadAcademicaService(estudiantes).exigirEstudiante(usuario));
        } else if ("ADMIN".equalsIgnoreCase(usuario.getRol())) personas=estudiantes.obtenerTodos();
        else {
            Set<Integer> ids=permitidas.stream().map(InscripcionCurso::getIdEstudiante).collect(Collectors.toCollection(LinkedHashSet::new));
            for (int id:ids) estudiantes.buscarPorId(id).ifPresent(personas::add);
        }
        List<Boletin> boletines=new ArrayList<>();
        for (Estudiante e:personas) boletines.add(new Boletin(e,permitidas.stream()
                .filter(i -> i.getIdEstudiante()==e.getIdEstudiante()).collect(Collectors.toList())));
        return boletines;
    }
}
