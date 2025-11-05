package b_estructuras;

import java.io.Serializable; // importar la interfaz

/**
 * excepcion personalizada para indicar que una estructura de datos esta vacia
 */
public class VacioException extends Exception implements Serializable { // implementa serializable
    private static final long serialVersionUID = 1L; // identificador de version

    public VacioException(String mensaje) {
        super(mensaje);
    }
}