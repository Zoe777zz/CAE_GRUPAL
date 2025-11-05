package b_estructuras;

import java.io.Serializable; // necesario para la persistencia

// se asume que nodo<t> y posicionexception estan definidos en b_estructuras

/**
 * implementacion de una lista enlazada simple usando nodos
 */
public class ListaEnlazada<T> implements Serializable { // implementa serializable
    private static final long serialVersionUID = 1L;

    private Nodo<T> cabeza; // referencia al primer nodo de la lista
    private int tamano;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /**
     * inserta un elemento al inicio de la lista
     */
    public void insertarInicio(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        nuevo.siguiente = cabeza; // el nuevo nodo apunta a la cabeza actual
        cabeza = nuevo;          // el nuevo nodo es la nueva cabeza
        tamano++;
    }

    /**
     * busca y retorna el nodo en la posicion (indice) especificada
     */
    private Nodo<T> buscarNodo(int index) throws PosicionException {
        if (index < 0 || index >= tamano) {
            // lanza nuestra excepcion personalizada si el indice es invalido
            throw new PosicionException("indice fuera del rango valido: " + index);
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.siguiente;
        }
        return actual;
    }

    /**
     * retorna el elemento en la posicion (indice)
     */
    public T get(int index) throws PosicionException {
        // el metodo buscarNodo ya valida el indice y lanza la excepcion
        return buscarNodo(index).dato;
    }

    /**
     * elimina el elemento en la posicion (indice)
     */
    public T delete(int index) throws PosicionException {
        if (index < 0 || index >= tamano) {
            throw new PosicionException("indice fuera del rango valido para eliminar: " + index);
        }

        T datoEliminado;
        if (index == 0) {
            // eliminacion en la cabeza
            datoEliminado = cabeza.dato;
            cabeza = cabeza.siguiente;
        } else {
            // eliminacion en el medio o final
            Nodo<T> anterior = buscarNodo(index - 1); // encontramos el nodo anterior
            datoEliminado = anterior.siguiente.dato;
            anterior.siguiente = anterior.siguiente.siguiente;
        }
        tamano--;
        return datoEliminado;
    }
    public void insertarFinal(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamano++;
    }

    public boolean isEmpty() {
        return tamano == 0; // o cabeza == null
    }

    public int size() {
        return tamano;
    }

    public void clear() {
        cabeza = null; // limpia la lista simplemente eliminando la referencia a la cabeza
        tamano = 0;
    }
}