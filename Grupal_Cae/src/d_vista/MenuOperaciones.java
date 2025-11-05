package d_vista;

import a_modelo.Ticket;
import a_modelo.TipoTramite;
import a_modelo.Nota;
import d_historial.AccionTicket;
import d_historial.Accion;
import c_logica.GestorTickets;
import d_historial.GestorHistorial;
import b_estructuras.VacioException;
import b_estructuras.ListaEnlazada;
import java.util.Scanner;
import java.io.IOException;
import java.util.List;

public class MenuOperaciones {

    // Se mantiene la declaración como atributo de instancia
    private GestorTickets gestorTickets;
    private final Scanner scanner;
    private final GestorHistorial historial;

    public MenuOperaciones(Scanner scanner, GestorTickets gestor, GestorHistorial historial) {
        this.scanner = scanner;
        this.gestorTickets = gestor; // Asignación del atributo
        this.historial = historial;
    }

    // ELIMINACIÓN: El método setGestorTickets (Opción 7) ha sido eliminado.

    // --- MENÚ PRINCIPAL ---

    public void mostrarMenuPrincipal() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Crear nuevo ticket");
        System.out.println("2. Atender siguiente ticket");
        System.out.println("3. Gestionar ticket en atencion");
        System.out.println("4. Ver reporte");
        System.out.println("5. Exportar reporte (CSV para carga)");
        System.out.println("6. Cargar reporte desde archivo (CSV)");
        System.out.println("7. Salir"); // Opción 7 ahora es Salir
        System.out.print("Seleccione una opcion: ");
    }

    // --- MÉTODOS DE UTILIDAD ---

    public int leerOpcion(int min, int max) {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                int opcion = Integer.parseInt(linea);
                if (opcion >= min && opcion <= max) {
                    return opcion;
                } else {
                    System.out.printf("Opcion invalida, ingrese un numero entre %d y %d: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida, ingrese solo numeros: ");
            }
        }
    }

    private String leerNombreEstudiante() {
        String nombre;
        while (true) {
            System.out.print("Nombre del estudiante: ");
            nombre = scanner.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacio.");
                continue;
            }
            if (nombre.matches(".*\\d.*")) {
                System.out.println("El nombre no debe contener números. Reintente.");
            } else if (!nombre.matches("^[\\p{L} .'-]+$")) {
                System.out.println("El nombre contiene caracteres no válidos. Use solo letras, espacios, guiones, puntos o comillas simples.");
            } else {
                return nombre;
            }
        }
    }


    // --- OPERACIONES PRINCIPALES ---

    public void crearNuevoTicket() {
        System.out.println("\n=== CREAR NUEVO TICKET ===");

        String estudiante = leerNombreEstudiante();
        TipoTramite tipoTramite = seleccionarTipoTramite();
        int prioridad = seleccionarPrioridad(); // 0 o 1

        gestorTickets.crearTicket(estudiante, tipoTramite, prioridad);
        System.out.println("Ticket creado y en cola.");
    }

    private TipoTramite seleccionarTipoTramite() {
        while (true) {
            System.out.println("\nTipo de tramite:");
            TipoTramite[] valores = TipoTramite.values();
            for (int i = 0; i < valores.length; i++) {
                System.out.println((i + 1) + ". " + valores[i].name());
            }
            System.out.print("Seleccione el tipo (1-" + valores.length + "): ");

            try {
                int idTramite = leerOpcion(1, valores.length);
                return TipoTramite.porId(idTramite);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int seleccionarPrioridad() {
        System.out.println("Prioridad del ticket:");
        System.out.println("0. URGENTE (Maxima prioridad)");
        System.out.println("1. NORMAL");
        System.out.print("Seleccione la prioridad (0 o 1): ");
        return leerOpcion(0, 1);
    }

    public void atenderSiguienteTicket() {
        try {
            Ticket ticket = gestorTickets.atenderSiguienteTicket();
            System.out.println("\nAhora atendiendo: " + ticket);
            historial.limpiar(); // Limpiar historial al empezar nuevo ticket
        } catch (VacioException e) {
            System.out.println("No hay tickets en espera.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void gestionarTicketEnAtencion() {
        if (!gestorTickets.hayTicketEnAtencion()) {
            System.out.println("No hay ticket en atencion actualmente.");
            return;
        }

        boolean volver = false;
        while (!volver) {
            Ticket ticketActual = gestorTickets.getTicketEnAtencion();

            System.out.println("\n=== GESTIÓN DE TICKET EN ATENCION ===");
            System.out.printf("Ticket #%d | Estudiante: %s | Estado: %s\n",
                    ticketActual.getId(),
                    ticketActual.getEstudiante(),
                    ticketActual.getEstado());

            // Mostrar Notas
            try {
                ListaEnlazada<Nota> notas = ticketActual.getNotas();
                System.out.println("Notas actuales (" + notas.size() + "):");
                if (notas.isEmpty()) {
                    System.out.println("   (Ninguna nota)");
                } else {
                    for (int i = 0; i < notas.size(); i++) {
                        Nota nota = notas.get(i);
                        System.out.printf("   [%d] %s\n", i + 1, nota.getContenido());
                    }
                }
            } catch (Exception e) {
                // Manejar PosicionException si get(i) falla, aunque es ListaEnlazada
                System.out.println("Error al mostrar notas: " + e.getMessage());
            }

            System.out.println("\n1. Agregar nota");
            System.out.println("2. Eliminar nota");
            System.out.println("3. Deshacer ultima accion (Nota)");
            System.out.println("4. Rehacer ultima accion (Nota)");
            System.out.println("5. Completar ticket");
            System.out.println("6. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            try {
                switch (leerOpcion(1, 6)) {
                    case 1: agregarNota(); break;
                    case 2: eliminarNota(); break;
                    case 3: deshacerAccion(); break;
                    case 4: rehacerAccion(); break;
                    case 5: completarTicketActual(ticketActual); volver = true; break;
                    case 6: volver = true; break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void completarTicketActual(Ticket ticket) {
        try {
            gestorTickets.completarTicketActual();
            historial.limpiar();
            System.out.println("Ticket #" + ticket.getId() + " completado.");
        } catch (Exception e) {
            System.out.println("Error al completar ticket: " + e.getMessage());
        }
    }


    // --- MÉTODOS DE NOTAS Y HISTORIAL ---

    private void agregarNota() {
        System.out.print("Ingrese el contenido de la nota: ");
        String contenido = scanner.nextLine();
        if (contenido.trim().isEmpty()) {
            System.out.println("El contenido no puede estar vacio.");
            return;
        }
        try {
            gestorTickets.agregarNota(contenido);
            Accion accion = new Accion(AccionTicket.ACCION_AGREGAR_NOTA, gestorTickets.getTicketEnAtencion(), contenido, null, null);
            historial.registrarAccion(accion);
            System.out.println("Nota AGREGADA: '" + contenido + "'");
        } catch (Exception e) {
            System.out.println("Error al agregar nota: " + e.getMessage());
        }
    }

    private void eliminarNota() {
        Ticket ticket = gestorTickets.getTicketEnAtencion();
        try {
            ListaEnlazada<Nota> notas = ticket.getNotas();
            if (notas.isEmpty()) {
                System.out.println("El ticket no tiene notas para eliminar.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error al acceder a las notas.");
            return;
        }

        System.out.print("Ingrese el CONTENIDO EXACTO de la nota a eliminar: ");
        String contenido = scanner.nextLine();

        try {
            Nota notaEliminada = gestorTickets.eliminarNota(contenido);
            if (notaEliminada == null) {
                System.out.println("No se encontro una nota con el contenido exacto.");
            } else {
                Accion accion = new Accion(AccionTicket.ACCION_ELIMINAR_NOTA, gestorTickets.getTicketEnAtencion(), contenido, null, null);
                historial.registrarAccion(accion);
                System.out.println("Nota eliminada: '" + contenido + "'");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar nota: " + e.getMessage());
        }
    }

    private void deshacerAccion() {
        try {
            historial.deshacer();
            System.out.println("Accion deshecha exitosamente.");
        } catch (VacioException e) {
            System.out.println("No hay acciones para deshacer.");
        } catch (Exception e) {
            System.out.println("Error al deshacer: " + e.getMessage());
        }
    }

    private void rehacerAccion() {
        try {
            historial.rehacer();
            System.out.println("Accion rehecha exitosamente");
        } catch (VacioException e) {
            System.out.println("No hay acciones para rehacer");
        } catch (Exception e) {
            System.out.println("Error al rehacer: " + e.getMessage());
        }
    }


    // --- MÉTODOS DE REPORTE Y PERSISTENCIA ---

    public void mostrarEstadisticas() {
        System.out.println("\n=== REPORTE DEL SISTEMA ===");
        System.out.println("==========================================");

        // 1. Ticket en atención
        Ticket ticketEnAtencion = gestorTickets.getTicketEnAtencion();
        System.out.println("\nEN ATENCIÓN:");
        if (ticketEnAtencion != null) {
            System.out.print(gestorTickets.formatoReporteTicket(ticketEnAtencion));
        } else {
            System.out.println("   (Ninguno)");
        }

        // 2. Tickets en espera
        List<Ticket> ticketsEnEspera = gestorTickets.getTicketsEnEspera();
        System.out.println("\nTICKETS EN ESPERA (Total: " + ticketsEnEspera.size() + "):");
        if (ticketsEnEspera.isEmpty()) {
            System.out.println("   (Ninguno)");
        } else {
            for (Ticket t : ticketsEnEspera) {
                System.out.print(gestorTickets.formatoReporteTicket(t));
            }
        }

        // 3. Tickets completados
        List<Ticket> ticketsCompletados = gestorTickets.getTicketsCompletadosLista();
        System.out.println("\nTICKETS COMPLETADOS (Total: " + ticketsCompletados.size() + "):");
        if (ticketsCompletados.isEmpty()) {
            System.out.println("   (Ninguno)");
        } else {
            for (Ticket t : ticketsCompletados) {
                System.out.print(gestorTickets.formatoReporteTicket(t));
            }
        }
        System.out.println("==========================================");
    }

    public void exportarReporte() {
        System.out.print("Ingrese el nombre del archivo para el reporte (e.g., reporte.csv): ");
        String nombreArchivo = scanner.nextLine();
        if (nombreArchivo.trim().isEmpty()) {
            System.out.println("Nombre de archivo no puede estar vacio.");
            return;
        }
        try {
            // Llama al método que ahora genera CSV
            gestorTickets.exportarReporte(nombreArchivo);
            System.out.println("Archivo listo.");
        } catch (IOException e) {
            System.out.println("Error al exportar el reporte: " + e.getMessage());
        }
    }

    public void importarReporte() {
        System.out.print("Ingrese el nombre del archivo de donde cargar tickets: ");
        String nombreArchivo = scanner.nextLine();
        if (nombreArchivo.trim().isEmpty()) {
            System.out.println("Nombre de archivo no puede estar vacio.");
            return;
        }
        try {
            int ticketsCargados = gestorTickets.importarReporte(nombreArchivo);
            System.out.printf("%d Tickets cargados exitosamente desde %s.\n", ticketsCargados, nombreArchivo);
            historial.limpiar(); // Limpiar el historial de deshacer/rehacer después de una carga masiva
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}