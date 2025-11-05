package b_estructuras;

import java.io.Serializable; // importar la interfaz

/**
 * excepcion personalizada para indicar que la posicion o indice es invalido
 */
public class PosicionException extends Exception implements Serializable { // implementa serializable
    private static final long serialVersionUID = 1L; // identificador de version

    public PosicionException() {
        super("no existe la posicion en tu lista");
    }

    public PosicionException(String msg) {
        super(msg);
    }
}