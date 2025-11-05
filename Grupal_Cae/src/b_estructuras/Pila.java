package b_estructuras;

import java.io.Serializable; // necesario para la persistencia

// nota: se asume que vacioexception y nodo<t> estan definidos en b_estructuras

/**
 * implementacion de una pila (stack) usando nodos
 */
public class Pila<T> implements Serializable { // implementa serializable
    private static final long serialVersionUID = 1L;

    private Nodo<T> cima; // referencia a la cima (head) de la pila
    private int tamano;

    public Pila() {
        this.cima = null;
        this.tamano = 0;
    }

    /**
     * agrega un elemento a la cima de la pila (push)
     */
    public void push(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);

        // el nuevo nodo siempre apunta a la antigua cima
        nuevo.siguiente = cima;

        // el nuevo nodo se convierte en la nueva cima
        cima = nuevo;
        tamano++;
    }

    /**
     * saca y retorna el elemento de la cima de la pila (pop)
     */
    public T pop() throws VacioException {
        if (isEmpty()) {
            throw new VacioException("la pila esta vacia");
        }

        T datoCima = cima.getDato(); // guarda el dato a devolver
        cima = cima.siguiente;      // la cima se mueve al siguiente nodo (lo elimina)
        tamano--;
        return datoCima;
    }

    /**
     * verifica si la pila esta vacia
     */
    public boolean isEmpty() {
        return cima == null;
    }

    /**
     * limpia la pila
     */
    public void clear() {
        cima = null; // elimina la referencia a la cima
        tamano = 0;
    }

    /**
     * retorna el numero de elementos
     */
    public int size() {
        return tamano;
    }
}