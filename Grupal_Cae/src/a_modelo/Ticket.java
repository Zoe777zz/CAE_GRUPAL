package a_modelo;

import b_estructuras.ListaEnlazada;
import b_estructuras.PosicionException;
import java.io.Serializable;


public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int contadorId = 1;

    private final int id;
    private final String estudiante;
    private final TipoTramite tipoTramite;
    private final int prioridad;
    private EstadoTicket estado;
    private final ListaEnlazada<Nota> notas;

    public Ticket(String estudiante, TipoTramite tipoTramite, int prioridad) {
        this.id = contadorId++;
        this.estudiante = estudiante;
        this.tipoTramite = tipoTramite;
        this.prioridad = prioridad;
        this.estado = EstadoTicket.EN_COLA;
        this.notas = new ListaEnlazada<>();
    }

    public static void actualizarContadorId(int nuevoValor) {
        contadorId = nuevoValor;
    }

    // --- Métodos Auxiliares para Persistencia CSV ---

    public String notasToCsvString() {
        if (notas.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < notas.size(); i++) {
                sb.append(notas.get(i).getContenido());
                if (i < notas.size() - 1) {
                    sb.append("|"); // Delimitador secundario: Pipe
                }
            }
        } catch (PosicionException e) {
            return "";
        }
        return sb.toString();
    }

    public void notasFromCsvString(String notasCsv) {
        // Asumiendo que ListaEnlazada tiene un método clear() para vaciar la lista.
        // Si no lo tiene, usa el bucle manual de eliminación.
        while (!this.notas.isEmpty()) {
            try {
                this.notas.delete(0);
            } catch (PosicionException e) {
                break;
            }
        }

        if (notasCsv == null || notasCsv.trim().isEmpty()) {
            return;
        }

        String[] partes = notasCsv.split("\\|"); // Escapar el pipe
        for (String contenido : partes) {
            if (!contenido.trim().isEmpty()) {
                this.notas.insertarFinal(new Nota(contenido.trim()));
            }
        }
    }

    // Getters, Setters y toString... (asegúrate de que estén)
    public int getId() { return id; }
    public String getEstudiante() { return estudiante; }
    public TipoTramite getTipoTramite() { return tipoTramite; }
    public int getPrioridad() { return prioridad; }
    public EstadoTicket getEstado() { return estado; }
    public void setEstado(EstadoTicket estado) { this.estado = estado; }
    public ListaEnlazada<Nota> getNotas() { return notas; }

    @Override
    public String toString() {
        return String.format("#%d - Estudiante: %s | Prioridad: %d | Estado: %s", id, estudiante, prioridad, estado);
    }
}