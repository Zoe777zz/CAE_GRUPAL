package b_estructuras;

import java.io.Serializable; // necesario para la persistencia


public class Nodo<T> implements Serializable {
    private static final long serialVersionUID = 1L; // identificador de version

    T dato; // el dato que almacena el nodo (ej: un ticket)
    Nodo<T> siguiente; // referencia al siguiente nodo en la secuencia

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null; // inicialmente no apunta a nada
    }

    public T getDato() { // metodo get para acceder al dato
        return dato;
    }
}