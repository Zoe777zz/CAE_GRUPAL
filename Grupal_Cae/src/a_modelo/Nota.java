package a_modelo;

import java.io.Serializable;

public class Nota implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String contenido;

    public Nota(String contenido) {
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }

    @Override
    public String toString() {
        return contenido;
    }
}