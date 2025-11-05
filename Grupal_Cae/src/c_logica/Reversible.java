package c_logica;

import d_historial.Accion;

/**
 * interfaz que define el contrato de las operaciones que se pueden deshacer y rehacer.
 * implementa el principio de inversion de dependencias (DIP) de SOLID.
 */
public interface Reversible {

    /**
     * aplica la logica para deshacer (revertir) la accion.
     * @param accion el objeto accion a deshacer.
     */
    void deshacerAccion(Accion accion);

    /**
     * aplica la logica para rehacer (volver a aplicar) la accion.
     * @param accion el objeto accion a rehacer.
     */
    void rehacerAccion(Accion accion);
}