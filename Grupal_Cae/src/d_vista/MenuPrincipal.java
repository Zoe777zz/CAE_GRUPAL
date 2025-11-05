package d_vista;

import c_logica.GestorTickets;
import d_historial.GestorHistorial;
import d_persistencia.GestorPersistencia;
import java.io.IOException;
import java.util.Scanner;

public class MenuPrincipal {

    private static final GestorPersistencia persistencia = new GestorPersistencia();

    public static void main(String[] args) {

        // 1. INICIALIZACIÓN LIMPIA: El gestor comienza vacío.
        GestorTickets gestorTickets = new GestorTickets();

        // 2. INYECCIÓN DE DEPENDENCIAS
        GestorHistorial gestorHistorial = new GestorHistorial(gestorTickets);
        gestorHistorial.setReversible(gestorTickets);

        Scanner scanner = new Scanner(System.in);
        MenuOperaciones menuOperaciones = new MenuOperaciones(scanner, gestorTickets, gestorHistorial);

        System.out.println("=== BIENVENIDO AL SISTEMA CAE DE GESTIÓN DE TICKETS ===");


        boolean salir = false;

        while (!salir) {

            mostrarMenuPrincipal(menuOperaciones);
            // La opción máxima ahora es 7
            int opcion = menuOperaciones.leerOpcion(1, 7);

            try {
                switch (opcion) {
                    case 1:
                        menuOperaciones.crearNuevoTicket();
                        break;
                    case 2:
                        menuOperaciones.atenderSiguienteTicket();
                        break;
                    case 3:
                        menuOperaciones.gestionarTicketEnAtencion();
                        break;
                    case 4:
                        menuOperaciones.mostrarEstadisticas();
                        break;
                    case 5:
                        menuOperaciones.exportarReporte();
                        break;
                    case 6:
                        menuOperaciones.importarReporte();
                        break;
                    case 7: // OPCIÓN FINAL DE SALIR
                        salir = true;
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error del sistema: " + e.getMessage());
            }
        }

        // 3. GUARDADO DE DATOS: Ocurre antes de terminar el programa.
        guardarDatos(gestorTickets);
        System.out.println("Gracias. Programa finalizado.");
        scanner.close();
    }

    // Mostramos el menú ajustado a 7 opciones
    private static void mostrarMenuPrincipal(MenuOperaciones menuOperaciones) {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Crear nuevo ticket");
        System.out.println("2. Atender siguiente ticket");
        System.out.println("3. Gestionar ticket en atencion");
        System.out.println("4. Ver reporte/Estadísticas");
        System.out.println("5. Exportar reporte (CSV para carga)");
        System.out.println("6. Cargar tickets desde archivo (CSV)");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opcion: ");
    }


    // --- Lógica de Persistencia (SOLO GUARDADO) ---
    // Mantenemos la lógica de guardado, aunque la carga no se ofrezca en el menú.

    private static void guardarDatos(GestorTickets gestor) {
        System.out.println("Guardando estado actual...");
        try {
            persistencia.guardar(gestor);
            System.out.println("Estado guardado exitosamente");
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo guardar el estado: " + e.getMessage());
        }
    }
}