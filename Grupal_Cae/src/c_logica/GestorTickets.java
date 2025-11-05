package c_logica;

import a_modelo.*;
import b_estructuras.*;
import d_historial.Accion;
import java.io.*;
import java.util.*;


public class GestorTickets implements Serializable, Reversible {
    private static final long serialVersionUID = 1L;

    private Cola<Ticket> colaUrgente;
    private Cola<Ticket> colaPrioritaria;
    private Ticket ticketEnAtencion;
    private ListaEnlazada<Ticket> ticketsCompletados;

    public GestorTickets() {
        this.colaUrgente = new Cola<>();
        this.colaPrioritaria = new Cola<>();
        this.ticketEnAtencion = null;
        this.ticketsCompletados = new ListaEnlazada<>();
    }

    // --- Hook de Serialización para contadorId ---

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        int maxId = 0;

        if (ticketEnAtencion != null) {
            maxId = Math.max(maxId, ticketEnAtencion.getId());
        }

        // Manejo de PosicionException en deserialización (checked exception)
        for (int i = 0; i < ticketsCompletados.size(); i++) {
            try {
                maxId = Math.max(maxId, ticketsCompletados.get(i).getId());
            } catch (PosicionException e) {
                // Si ocurre una PosicionException aquí, es un fallo de estructura grave
                throw new IOException("Error de PosicionException durante la deserialización de tickets.", e);
            }
        }

        // Si se cargan datos, el contador debe continuar desde el ID más alto + 1
        Ticket.actualizarContadorId(maxId + 1);
    }


    // --- Métodos Internos de Notas para Historial ---

    private Nota _agregarNota(Ticket ticket, String contenido) throws PosicionException {
        Nota nuevaNota = new Nota(contenido);
        // La nota se inserta al inicio de la lista de notas del ticket
        ticket.getNotas().insertarInicio(nuevaNota);
        return nuevaNota;
    }

    public void restaurarNota(Ticket ticket, Nota nota) throws PosicionException {
        ticket.getNotas().insertarInicio(nota);
    }

    public Nota eliminarNotaInterno(Ticket ticket, String contenidoNota) throws PosicionException {
        ListaEnlazada<Nota> notas = ticket.getNotas();
        for (int i = 0; i < notas.size(); i++) {
            Nota nota = notas.get(i);
            if (nota.getContenido().equals(contenidoNota)) {
                notas.delete(i);
                return nota;
            }
        }
        return null;
    }


    @Override
    public void deshacerAccion(Accion accion) {
        try {
            switch (accion.getTipo()) {
                case ACCION_AGREGAR_NOTA:
                    // DESHACER AGREGAR NOTA = ELIMINAR la nota que se acaba de agregar.
                    if (accion.getNotaContenido() != null) {
                        // Usamos el contenido de la nota que fue agregada para eliminarla
                        eliminarNotaInterno(accion.getTicket(), accion.getNotaContenido());
                    } break;
                case ACCION_ELIMINAR_NOTA:
                    // DESHACER ELIMINAR NOTA = RESTAURAR la nota eliminada.
                    if (accion.getNotaContenido() != null) {
                        Nota notaRestaurar = new Nota(accion.getNotaContenido());
                        restaurarNota(accion.getTicket(), notaRestaurar);
                    } break;
                case ACCION_CAMBIAR_ESTADO:
                    if (accion.getEstadoAnterior() != null) {
                        accion.getTicket().setEstado(accion.getEstadoAnterior());
                    } break;
            }
        } catch (PosicionException e) {

        }
    }

    @Override
    public void rehacerAccion(Accion accion) {
        try {
            switch (accion.getTipo()) {
                case ACCION_AGREGAR_NOTA:
                    // REHACER AGREGAR NOTA = RESTAURAR la nota que fue eliminada por el deshacer.
                    if (accion.getNotaContenido() != null) {
                        Nota notaRestaurar = new Nota(accion.getNotaContenido());
                        restaurarNota(accion.getTicket(), notaRestaurar);
                    } break;
                case ACCION_ELIMINAR_NOTA:
                    // REHACER ELIMINAR NOTA = ELIMINAR la nota que fue restaurada por el deshacer.
                    if (accion.getNotaContenido() != null) {
                        // Usamos el contenido de la nota para volver a eliminarla
                        eliminarNotaInterno(accion.getTicket(), accion.getNotaContenido());
                    } break;
                case ACCION_CAMBIAR_ESTADO:
                    if (accion.getEstadoNuevo() != null) {
                        accion.getTicket().setEstado(accion.getEstadoNuevo());
                    } break;
            }
        } catch (PosicionException e) {
            // Ignorado
        }
    }

    // --- Métodos de Flujo y Gestión ---

    public void crearTicket(String estudiante, TipoTramite tipoTramite, int prioridad) {
        Ticket nuevoTicket = new Ticket(estudiante, tipoTramite, prioridad);
        if (prioridad == 0) {
            colaUrgente.enqueue(nuevoTicket);
        } else {
            colaPrioritaria.enqueue(nuevoTicket);
        }
    }

    public Nota agregarNota(String contenido) {
        if (ticketEnAtencion == null) throw new IllegalStateException("no hay ticket en atencion");
        try {
            return _agregarNota(ticketEnAtencion, contenido);
        } catch (PosicionException e) {
            throw new RuntimeException("Fallo interno de estructura al agregar nota.", e);
        }
    }

    public Nota eliminarNota(String contenidoNota) {
        if (ticketEnAtencion == null) throw new IllegalStateException("no hay ticket en atencion");
        try {
            return eliminarNotaInterno(ticketEnAtencion, contenidoNota);
        } catch (PosicionException e) {
            throw new RuntimeException("Fallo interno de estructura al eliminar nota.", e);
        }
    }

    public Ticket atenderSiguienteTicket() throws VacioException, IllegalStateException {
        if (ticketEnAtencion != null) {
            throw new IllegalStateException("ya hay un ticket en atencion (#" + ticketEnAtencion.getId() + ") debe completarlo primero.");
        }

        Ticket siguiente;
        if (!colaUrgente.isEmpty()) {
            siguiente = colaUrgente.dequeue();
        } else if (!colaPrioritaria.isEmpty()) {
            siguiente = colaPrioritaria.dequeue();
        } else {
            throw new VacioException("no hay tickets en espera");
        }
        siguiente.setEstado(EstadoTicket.EN_ATENCION);
        ticketEnAtencion = siguiente;
        return siguiente;
    }

    public void completarTicketActual() {
        if (ticketEnAtencion == null) {
            throw new IllegalStateException("no hay ticket en atencion");
        }
        ticketEnAtencion.setEstado(EstadoTicket.COMPLETADO);

        // La lista enlazada permite insertar al inicio sin PosicionException chequeada
        ticketsCompletados.insertarInicio(ticketEnAtencion);

        ticketEnAtencion = null;
    }

    // --- Métodos de Consulta y Reporte ---

    public List<Ticket> getTicketsEnEspera() {
        List<Ticket> listaEspera = new ArrayList<>();

        // 1. Desencolar y reencolar Cola Urgente
        Cola<Ticket> colaUrgenteTmp = new Cola<>();
        try {
            while (!colaUrgente.isEmpty()) {
                Ticket t = colaUrgente.dequeue();
                listaEspera.add(t);
                colaUrgenteTmp.enqueue(t);
            }
            while (!colaUrgenteTmp.isEmpty()) {
                colaUrgente.enqueue(colaUrgenteTmp.dequeue());
            }
        } catch (VacioException e) {}

        // 2. Desencolar y reencolar Cola Prioritaria
        Cola<Ticket> colaNormalTmp = new Cola<>();
        try {
            while (!colaPrioritaria.isEmpty()) {
                Ticket t = colaPrioritaria.dequeue();
                listaEspera.add(t);
                colaNormalTmp.enqueue(t);
            }
            while (!colaNormalTmp.isEmpty()) {
                colaPrioritaria.enqueue(colaNormalTmp.dequeue());
            }
        } catch (VacioException e) { }
        return listaEspera;
    }

    /**
     * Obtiene la lista de tickets completados.
     */
    public List<Ticket> getTicketsCompletadosLista() {
        List<Ticket> lista = new ArrayList<>();

        // Manejamos la PosicionException para evitar el error 'unreported exception...'
        try {
            for (int i = 0; i < ticketsCompletados.size(); i++) {
                lista.add(ticketsCompletados.get(i));
            }
        } catch (PosicionException e) {
            throw new RuntimeException("Error de estructura interna al generar la lista de tickets completados.", e);
        }

        return lista;
    }

    public Ticket getTicketEnAtencion() { return ticketEnAtencion; }
    public boolean hayTicketEnAtencion() { return ticketEnAtencion != null; }
    public int getCantidadTicketsEnEspera() { return colaUrgente.size() + colaPrioritaria.size(); }
    public ListaEnlazada<Ticket> getTicketsCompletados() { return ticketsCompletados; }

    // En c_logica/GestorTickets.java

    public String formatoReporteTicket(Ticket t) {
        StringBuilder sb = new StringBuilder();

        // --- SECCIÓN DE DATOS PRINCIPALES ---
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format("id: #%d | estudiante: %s\n", t.getId(), t.getEstudiante()));
        sb.append(String.format("estado: %s | prioridad: %s | tramite: %s\n",
                t.getEstado(),
                (t.getPrioridad() == 0 ? "URGENTE" : "NORMAL"),
                t.getTipoTramite()));

        // --- SECCIÓN DE NOTAS (NUEVO) ---
        ListaEnlazada<Nota> notas = t.getNotas();
        sb.append(String.format("Notas (%d): ", notas.size()));

        if (notas.isEmpty()) {
            sb.append("(Ninguna)\n");
        } else {
            sb.append("\n");
            try {
                // Recorrer la lista enlazada de notas e incluirlas
                for (int i = 0; i < notas.size(); i++) {
                    Nota nota = notas.get(i);
                    // Añadimos un prefijo (ej. ' - ') para que se vea indentado
                    sb.append(String.format(" - [%d] %s\n", i + 1, nota.getContenido()));
                }
            } catch (PosicionException e) {
                sb.append(" [ERROR al leer notas]\n");
            }
        }

        sb.append("----------------------------------------------------------------------\n");

        return sb.toString();
    }

    // --- Métodos de Persistencia de Reporte (Importación) ---

    // En c_logica/GestorTickets.java, dentro del método importarReporte(String nombreArchivo)

    // En c_logica/GestorTickets.java

    public int importarReporte(String nombreArchivo) throws IOException {
        int ticketsCargados = 0;

        // 1. Limpieza de colas y reinicio de ID (para evitar duplicados y IDs incorrectos)
        this.colaUrgente = new Cola<>();
        this.colaPrioritaria = new Cola<>();
        this.ticketEnAtencion = null;
        Ticket.actualizarContadorId(1);

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {

            // Omitir la primera línea (Encabezado)
            br.readLine();

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                // Usamos split(",", -1) para asegurar que las comas finales (campos vacíos) sean contadas.
                String[] partes = linea.split(",", -1);

                // Verificamos que tengamos al menos 3 campos esenciales (Estudiante, ID_Tramite, Prioridad)
                if (partes.length < 3) {
                    System.err.println("Advertencia: Linea con formato incorrecto, saltando: " + linea);
                    continue;
                }

                try {
                    String estudiante = partes[0].trim();
                    int idTramite = Integer.parseInt(partes[1].trim());
                    int prioridad = Integer.parseInt(partes[2].trim());

                    // Obtenemos la cadena de notas (partes[3]) solo si existe (si partes.length >= 4).
                    // Si no existe (length es 3), usamos una cadena vacía "".
                    String notasCsv = (partes.length >= 4) ? partes[3].trim() : "";

                    TipoTramite tipoTramite = TipoTramite.porId(idTramite);

                    // 1. Crear el ticket (usará el nuevo ID correcto)
                    crearTicket(estudiante, tipoTramite, prioridad);
                    ticketsCargados++;

                    // 2. Restaurar las notas (solo si hay contenido)
                    if (!notasCsv.isEmpty()) {
                        // Obtenemos el último ticket creado de la lista de espera
                        List<Ticket> listaEspera = getTicketsEnEspera();
                        Ticket ticketRecienCreado = listaEspera.get(listaEspera.size() - 1);
                        ticketRecienCreado.notasFromCsvString(notasCsv);
                    }

                } catch (IllegalArgumentException e) {
                    System.err.println("Advertencia: Datos invalidos en la linea, saltando: " + linea + " (" + e.getMessage() + ")");
                }
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Archivo no encontrado: " + nombreArchivo);
        }
        return ticketsCargados;
    }

    // En c_logica/GestorTickets.java

    public void exportarReporte(String nombreArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {

            // **ENCABEZADO ACTUALIZADO**
            bw.write("Estudiante,ID_Tramite,Prioridad,Notas");
            bw.newLine();

            // 1. Recorrer y restaurar la cola urgente (Prioridad 0)
            Cola<Ticket> colaUrgenteTmp = new Cola<>();
            try {
                while (!colaUrgente.isEmpty()) {
                    Ticket t = colaUrgente.dequeue();
                    // **FORMATO CSV ACTUALIZADO: Estudiante,ID_Tramite,Prioridad,Notas**
                    bw.write(String.format("%s,%d,%d,%s",
                            t.getEstudiante(),
                            t.getTipoTramite().getId(),
                            t.getPrioridad(),
                            t.notasToCsvString()));
                    bw.newLine();
                    colaUrgenteTmp.enqueue(t);
                }


                while (!colaUrgenteTmp.isEmpty()) {
                    colaUrgente.enqueue(colaUrgenteTmp.dequeue());
                }


            } catch (VacioException e) { /* No sucede */ }


            // 2. Recorrer y restaurar la cola prioritaria (Prioridad 1)
            Cola<Ticket> colaNormalTmp = new Cola<>();
            try {
                while (!colaPrioritaria.isEmpty()) {
                    Ticket t = colaPrioritaria.dequeue();
                    // **FORMATO CSV ACTUALIZADO**
                    bw.write(String.format("%s,%d,%d,%s",
                            t.getEstudiante(),
                            t.getTipoTramite().getId(),
                            t.getPrioridad(),
                            t.notasToCsvString()));
                    bw.newLine();
                    colaNormalTmp.enqueue(t);
                }


                while (!colaNormalTmp.isEmpty()) {
                    colaPrioritaria.enqueue(colaNormalTmp.dequeue());
                }
                // --------------------------------------------

            } catch (VacioException e) {

            }

        } catch (IOException e) {
            throw new IOException("Error al escribir el archivo CSV: " + e.getMessage(), e);
        }
    }
}