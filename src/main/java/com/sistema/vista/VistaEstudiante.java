package com.sistema.vista;

import com.sistema.controlador.EstudianteController;
import com.sistema.modelo.Estudiante;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Vista de consola (Scanner) para la gestión de Estudiantes.
 * Interactúa con el usuario por terminal y delega la orquestación a EstudianteController.
 */
public class VistaEstudiante {

    private final EstudianteController controlador;
    private final Scanner scanner;

    public VistaEstudiante() {
        this.controlador = new EstudianteController();
        this.scanner = new Scanner(System.in);
    }

    public VistaEstudiante(EstudianteController controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
    }

    public VistaEstudiante(EstudianteController controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    /**
     * Inicia el menú interactivo de gestión de estudiantes en consola.
     */
    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n==========================================");
            System.out.println("     GESTIÓN DE ESTUDIANTES - UNIAJC      ");
            System.out.println("==========================================");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Listar todos los estudiantes");
            System.out.println("3. Buscar estudiante por ID");
            System.out.println("4. Buscar estudiante por Email");
            System.out.println("5. Actualizar estudiante");
            System.out.println("6. Eliminar estudiante");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String entrada = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Ingrese un número.");
                continue;
            }

            switch (opcion) {
                case 1 -> registrarEstudiante();
                case 2 -> listarEstudiantes();
                case 3 -> buscarPorId();
                case 4 -> buscarPorEmail();
                case 5 -> actualizarEstudiante();
                case 6 -> eliminarEstudiante();
                case 0 -> System.out.println("Saliendo del módulo de estudiantes...");
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    public void registrarEstudiante() {
        try {
            System.out.println("\n--- Registrar Nuevo Estudiante ---");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            Estudiante creado = controlador.registrarEstudiante(nombre, apellido, email);
            System.out.println("Estudiante registrado exitosamente: " + creado);
        } catch (IllegalArgumentException e) {
            System.out.println("Validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al registrar estudiante: " + e.getMessage());
        }
    }

    public void listarEstudiantes() {
        try {
            System.out.println("\n--- Lista de Estudiantes ---");
            List<Estudiante> estudiantes = controlador.obtenerTodosLosEstudiantes();
            if (estudiantes.isEmpty()) {
                System.out.println("No hay estudiantes registrados.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-20s | %-30s%n", "ID", "Nombre", "Apellido", "Email");
            System.out.println("---------------------------------------------------------------------------------");
            for (Estudiante e : estudiantes) {
                System.out.printf("%-5d | %-20s | %-20s | %-30s%n",
                        e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail());
            }
        } catch (Exception e) {
            System.out.println("Error al listar estudiantes: " + e.getMessage());
        }
    }

    public void buscarPorId() {
        try {
            System.out.println("\n--- Buscar Estudiante por ID ---");
            System.out.print("Ingrese el ID del estudiante: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Optional<Estudiante> resultado = controlador.buscarEstudiantePorId(id);
            if (resultado.isPresent()) {
                Estudiante e = resultado.get();
                System.out.println("Estudiante encontrado: " + e);
            } else {
                System.out.println("No se encontró ningún estudiante con ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al buscar estudiante: " + e.getMessage());
        }
    }

    public void buscarPorEmail() {
        try {
            System.out.println("\n--- Buscar Estudiante por Email ---");
            System.out.print("Ingrese el email del estudiante: ");
            String email = scanner.nextLine().trim();

            Optional<Estudiante> resultado = controlador.buscarEstudiantePorEmail(email);
            if (resultado.isPresent()) {
                System.out.println("Estudiante encontrado: " + resultado.get());
            } else {
                System.out.println("No se encontró ningún estudiante con el email: " + email);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al buscar estudiante: " + e.getMessage());
        }
    }

    public void actualizarEstudiante() {
        try {
            System.out.println("\n--- Actualizar Estudiante ---");
            System.out.print("Ingrese el ID del estudiante a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Nuevo Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Nuevo Apellido: ");
            String apellido = scanner.nextLine().trim();

            System.out.print("Nuevo Email: ");
            String email = scanner.nextLine().trim();

            controlador.actualizarEstudiante(id, nombre, apellido, email);
            System.out.println("Estudiante actualizado exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al actualizar estudiante: " + e.getMessage());
        }
    }

    public void eliminarEstudiante() {
        try {
            System.out.println("\n--- Eliminar Estudiante ---");
            System.out.print("Ingrese el ID del estudiante a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            controlador.eliminarEstudiante(id);
            System.out.println("Estudiante eliminado exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número entero válido.");
        } catch (IllegalArgumentException e) {
            System.out.println("Validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
        }
    }
}
