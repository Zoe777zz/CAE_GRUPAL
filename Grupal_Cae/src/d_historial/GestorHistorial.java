package d_historial;

import b_estructuras.Pila;
import b_estructuras.VacioException;
import c_logica.Reversible;
import java.io.Serializable;

public class GestorHistorial implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Pila<Accion> pilaUndo;
    private final Pila<Accion> pilaRedo;
    private transient Reversible reversible;

    public GestorHistorial(Reversible reversible) {
        this.pilaUndo = new Pila<>();
        this.pilaRedo = new Pila<>();
        this.reversible = reversible;
    }

    public void setReversible(Reversible reversible) { this.reversible = reversible; }
    public void registrarAccion(Accion accion) { pilaUndo.push(accion); pilaRedo.clear(); }
    public void limpiar() { pilaUndo.clear(); pilaRedo.clear(); }
    public boolean puedeDeshacer() { return !pilaUndo.isEmpty(); }
    public boolean puedeRehacer() { return !pilaRedo.isEmpty(); }

    public void deshacer() throws VacioException, IllegalStateException {
        if (!puedeDeshacer()) throw new VacioException("no hay acciones para deshacer");
        if (this.reversible == null) throw new IllegalStateException("el sistema reversible no ha sido inicializado");
        Accion accion = pilaUndo.pop();
        this.reversible.deshacerAccion(accion);
        pilaRedo.push(accion);
    }

    public void rehacer() throws VacioException, IllegalStateException {
        if (!puedeRehacer()) throw new VacioException("no hay acciones para rehacer");
        if (this.reversible == null) throw new IllegalStateException("el sistema reversible no ha sido inicializado");
        Accion accion = pilaRedo.pop();
        this.reversible.rehacerAccion(accion);
        pilaUndo.push(accion);
    }
}