package com.sistema.vista;

import com.sistema.config.ConexionBD;
import com.sistema.modelo.*;
import com.sistema.servicio.*;
import java.io.PrintStream;
import java.util.Scanner;

/** Interfaz de consola integrada. Comparte servicios y permisos con Swing. */
public class ConsolaAcademica {
    private final Scanner entrada;
    private final PrintStream salida;
    private final UsuarioService cuentas=new UsuarioService();
    private final EstudianteService estudiantes=new EstudianteService();
    private final DocenteService docentes=new DocenteService();
    private final MateriaService materias=new MateriaService();
    private final GrupoService grupos=new GrupoService();
    private final InscripcionCursoService matriculas=new InscripcionCursoService();
    private Usuario sesion;

    public ConsolaAcademica(Scanner entrada,PrintStream salida) { this.entrada=entrada; this.salida=salida; }
    public void iniciar() {
        salida.println("Sistema Academico UNIAJC - Consola");
        while (sesion==null) {
            try {
                String usuario=leer("Usuario (0 para salir)"); if (usuario.equals("0")) return;
                String clave;
                if (System.console()!=null) { char[] chars=System.console().readPassword("Contrasena: "); if(chars==null)return; clave=new String(chars); java.util.Arrays.fill(chars,'\0'); }
                else clave=leerSinRecortar("Contrasena (esta terminal muestra los caracteres)");
                sesion=cuentas.autenticar(usuario,clave);
                if (sesion==null) salida.println("Usuario o contrasena incorrectos.");
            } catch (FinEntrada e) { return; }
            catch (RuntimeException e) { salida.println(e.getMessage()); }
        }
        boolean continuar=true;
        while (continuar) {
            try {
                salida.println("\n1 Consultar notas y matriculas | 2 Matricular | 3 Calificar | 4 Cancelar matricula");
                if (admin()) salida.println("5 Estudiantes | 6 Docentes | 7 Materias | 8 Grupos | 9 Usuarios");
                salida.println("0 Salir");
                switch (entero("Opcion")) {
                    case 0 -> continuar=false;
                    case 1 -> matriculas.listarParaUsuario(sesion).forEach(i -> salida.printf("%d | Estudiante %d | Grupo %d | Nota %s | %s%n",i.getIdInscripcion(),i.getIdEstudiante(),i.getIdGrupo(),i.getNotaFinal(),i.getEstado()));
                    case 2 -> matricular();
                    case 3 -> calificar();
                    case 4 -> { exigirAdmin(); int id=entero("ID matricula"); if(confirmar()) salida.println(matriculas.eliminarInscripcion(id,sesion)?"Matricula cancelada; historial conservado.":"No existe o ya estaba cancelada."); }
                    case 5 -> { exigirAdmin(); gestionarEstudiantes(); }
                    case 6 -> { exigirAdmin(); gestionarDocentes(); }
                    case 7 -> { exigirAdmin(); gestionarMaterias(); }
                    case 8 -> { exigirAdmin(); gestionarGrupos(); }
                    case 9 -> { exigirAdmin(); gestionarUsuarios(); }
                    default -> salida.println("Opcion no valida.");
                }
            } catch (FinEntrada e) { return; }
            catch (Exception e) { salida.println("No se completo la operacion: "+e.getMessage()); }
        }
    }
    private boolean admin(){return "ADMIN".equalsIgnoreCase(sesion.getRol());}
    private void exigirAdmin(){if(!admin())throw new IllegalArgumentException("Se requiere rol administrador.");}
    private int accion(){salida.println("1 Listar | 2 Crear | 3 Editar | 4 Eliminar | 0 Volver");return entero("Accion");}
    private void gestionarEstudiantes(){
        switch(accion()){
            case 1 -> estudiantes.obtenerTodosLosEstudiantes().forEach(salida::println);
            case 2 -> { estudiantes.registrarEstudiante(leer("Nombre"),leer("Apellido"),leer("Correo"));salida.println("Estudiante creado."); }
            case 3 -> { estudiantes.actualizarEstudiante(entero("ID"),leer("Nombre"),leer("Apellido"),leer("Correo"));salida.println("Estudiante actualizado."); }
            case 4 -> { int id=entero("ID");if(confirmar()) {estudiantes.eliminarEstudiante(id);salida.println("Estudiante eliminado.");} }
        }
    }
    private void gestionarDocentes(){
        switch(accion()){
            case 1 -> docentes.listarDocentes().forEach(salida::println);
            case 2 -> resultado(docentes.registrarDocente(new Docente(leer("Nombre"),leer("Especialidad"))));
            case 3 -> resultado(docentes.actualizarDocente(new Docente(entero("ID"),leer("Nombre"),leer("Especialidad"))));
            case 4 -> {int id=entero("ID");if(confirmar())resultado(docentes.eliminarDocente(id));}
        }
    }
    private void gestionarMaterias(){
        switch(accion()){
            case 1 -> materias.listarMaterias().forEach(salida::println);
            case 2 -> resultado(materias.registrarMateria(new Materia(leer("Nombre"),entero("Creditos 1 a 10"))));
            case 3 -> resultado(materias.actualizarMateria(new Materia(entero("ID"),leer("Nombre"),entero("Creditos 1 a 10"))));
            case 4 -> {int id=entero("ID");if(confirmar())resultado(materias.eliminarMateria(id));}
        }
    }
    private void gestionarGrupos() throws java.sql.SQLException {
        switch(accion()){
            case 1 -> grupos.listarGrupos().forEach(salida::println);
            case 2 -> salida.println("Grupo creado con ID "+grupos.crearGrupo(entero("ID materia"),entero("ID docente"),leer("Aula"),leer("Horario")));
            case 3 -> resultado(grupos.actualizarGrupo(new Grupo(entero("ID"),entero("ID materia"),entero("ID docente"),leer("Aula"),leer("Horario"))));
            case 4 -> {int id=entero("ID");if(confirmar())resultado(grupos.eliminarGrupo(id));}
        }
    }
    private void gestionarUsuarios(){
        int accion=accion();
        if(accion==1){cuentas.listarUsuarios().forEach(u -> salida.printf("%d | %s | %s | Estudiante %s | Docente %s%n",u.getIdUsuario(),u.getUsername(),u.getRol(),u.getIdEstudiante(),u.getIdDocente()));return;}
        if(accion==4){int id=entero("ID");if(confirmar())resultado(cuentas.eliminarUsuario(id));return;}
        if(accion!=2 && accion!=3)return;
        Usuario u=accion==3?cuentas.buscarPorId(entero("ID")):new Usuario();
        if(u==null)throw new IllegalArgumentException("Usuario no encontrado.");
        u.setUsername(leer("Usuario"));String clave=leerSinRecortar("Contrasena" + (accion==3?" (vacia conserva la actual)":""));
        if(!clave.isEmpty() || accion==2)u.setPassword(clave);
        u.setRol(leer("Rol ADMIN DOCENTE ESTUDIANTE").toUpperCase(java.util.Locale.ROOT));
        u.setIdEstudiante("ESTUDIANTE".equals(u.getRol())?entero("ID estudiante"):null);
        u.setIdDocente("DOCENTE".equals(u.getRol())?entero("ID docente"):null);
        resultado(accion==2?cuentas.registrarUsuario(u):cuentas.actualizarUsuario(u));
    }
    private void matricular(){
        if("DOCENTE".equals(sesion.getRol()))throw new IllegalArgumentException("El docente no puede matricular estudiantes.");
        int idEst=admin()?entero("ID estudiante"):new IdentidadAcademicaService().exigirEstudiante(sesion).getIdEstudiante();
        matriculas.obtenerComboGrupos().forEach(salida::println);
        resultado(matriculas.matricularEstudiante(new InscripcionCurso(idEst,entero("ID grupo"),null,"Matriculado"),sesion));
    }
    private void calificar(){
        if("ESTUDIANTE".equals(sesion.getRol()))throw new IllegalArgumentException("El estudiante no puede calificar.");
        InscripcionCurso i=new InscripcionCurso();i.setIdInscripcion(entero("ID matricula"));
        String texto=leer("Nota 0 a 5 (vacia para dejar sin calificar)");
        i.setNotaFinal(texto.isEmpty()?null:Double.valueOf(texto.replace(',','.')));i.setEstado("Matriculado");
        resultado(matriculas.actualizarCalificacion(i,sesion));
    }
    private void resultado(boolean ok){salida.println(ok?"Operacion completada.":"No existe el registro o no se pudo completar.");}
    private boolean confirmar(){return "SI".equalsIgnoreCase(leer("Escribe SI para confirmar"));}
    private int entero(String campo){try{return Integer.parseInt(leer(campo));}catch(NumberFormatException e){throw new IllegalArgumentException("Debes ingresar un numero entero.");}}
    private String leer(String campo){return leerSinRecortar(campo).trim();}
    private String leerSinRecortar(String campo){salida.print(campo+": ");if(!entrada.hasNextLine())throw new FinEntrada();return entrada.nextLine();}
    private static class FinEntrada extends RuntimeException { }
    public static void main(String[] args){try{new ConsolaAcademica(new Scanner(System.in),System.out).iniciar();}finally{ConexionBD.cerrarConexion();}}
}
