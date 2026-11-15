
package com.sistema.vista;

import com.sistema.controlador.GrupoControlador;
import com.sistema.modelo.Grupo;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GrupoVista {

    private final GrupoControlador controlador;
    private final Scanner sc;

    public GrupoVista() {
        this.controlador = new GrupoControlador();
        this.sc = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN DE GRUPOS ===");
            System.out.println("1. Crear grupo");
            System.out.println("2. Listar grupos");
            System.out.println("3. Buscar grupo por ID");
            System.out.println("4. Actualizar grupo");
            System.out.println("5. Eliminar grupo");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = leerEntero();

            try {
                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> actualizar();
                    case 5 -> eliminar();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción no válida");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void crear() throws SQLException {
        System.out.print("ID materia: ");
        int idMateria = leerEntero();
        System.out.print("ID docente: ");
        int idDocente = leerEntero();
        System.out.print("Aula: ");
        String aula = sc.nextLine();
        System.out.print("Horario: ");
        String horario = sc.nextLine();

        int id = controlador.crearGrupo(idMateria, idDocente, aula, horario);
        System.out.println("Grupo creado con ID: " + id);
    }

    private void listar() throws SQLException {
        List<Grupo> grupos = controlador.listarGrupos();
        if (grupos.isEmpty()) {
            System.out.println("No hay grupos registrados.");
            return;
        }
        System.out.println("\nID | Materia | Docente | Aula | Horario");
        for (Grupo g : grupos) {
            System.out.printf("%d | %d | %d | %s | %s%n",
                    g.getIdGrupo(), g.getIdMateria(), g.getIdDocente(), g.getAula(), g.getHorario());
        }
    }

    private void buscar() throws SQLException {
        System.out.print("ID del grupo: ");
        int id = leerEntero();
        Grupo g = controlador.buscarGrupo(id);
        System.out.println(g != null ? g : "No se encontró el grupo.");
    }

    private void actualizar() throws SQLException {
        System.out.print("ID del grupo a actualizar: ");
        int id = leerEntero();
        System.out.print("Nuevo ID materia: ");
        int idMateria = leerEntero();
        System.out.print("Nuevo ID docente: ");
        int idDocente = leerEntero();
        System.out.print("Nueva aula: ");
        String aula = sc.nextLine();
        System.out.print("Nuevo horario: ");
        String horario = sc.nextLine();

        Grupo g = new Grupo(id, idMateria, idDocente, aula.trim(), horario.trim());
        boolean ok = controlador.actualizarGrupo(g);
        System.out.println(ok ? "Grupo actualizado." : "No se encontró el grupo.");
    }

    private void eliminar() throws SQLException {
        System.out.print("ID del grupo a eliminar: ");
        int id = leerEntero();
        boolean ok = controlador.eliminarGrupo(id);
        System.out.println(ok ? "Grupo eliminado." : "No se encontró el grupo.");
    }

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingresa un número válido: ");
            }
        }
    }
}