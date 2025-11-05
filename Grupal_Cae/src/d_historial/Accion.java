package d_historial;

import a_modelo.EstadoTicket;
import a_modelo.Ticket;
import java.io.Serializable;

public class Accion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AccionTicket tipo;
    private final Ticket ticket;
    private final String notaContenido;
    private final EstadoTicket estadoAnterior;
    private final EstadoTicket estadoNuevo;

    public Accion(AccionTicket tipo, Ticket ticket,
                  String notaContenido,
                  EstadoTicket estadoAnterior, EstadoTicket estadoNuevo) {
        this.tipo = tipo;
        this.ticket = ticket;
        this.notaContenido = notaContenido;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }

    public AccionTicket getTipo() { return tipo; }
    public Ticket getTicket() { return ticket; }
    public String getNotaContenido() { return notaContenido; }
    public EstadoTicket getEstadoAnterior() { return estadoAnterior; }
    public EstadoTicket getEstadoNuevo() { return estadoNuevo; }
}