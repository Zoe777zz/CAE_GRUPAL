package b_estructuras;

import java.io.Serializable; // necesario para la persistencia


public class Cola<T> implements Serializable { // implementa serializable
    private static final long serialVersionUID = 1L;

    // referencias a los extremos de la cola (frente y final)
    private Nodo<T> frente;
    private Nodo<T> finalCola;
    private int tamano;

    public Cola() {
        this.frente = null;
        this.finalCola = null;
        this.tamano = 0;
    }

    // agrega un elemento al final de la cola (enqueue)
    public void enqueue(T elemento) {
        // usamos la clase nodo<t> para crear el nuevo elemento
        Nodo<T> nuevo = new Nodo<>(elemento);

        if (isEmpty()) {
            frente = nuevo; // si esta vacia, el frente y el final son el nuevo nodo
        } else {
            finalCola.siguiente = nuevo; // sino, el anterior final apunta al nuevo
        }
        finalCola = nuevo; // el nuevo nodo siempre es el final de cola
        tamano++;
    }

    // saca y retorna el elemento del frente de la cola (dequeue)
    public T dequeue() throws VacioException {
        if (isEmpty()) {
            throw new VacioException("la cola esta vacia");
        }

        T datoFrente = frente.getDato(); // obtiene el dato a devolver
        frente = frente.siguiente;  // el frente se mueve al siguiente nodo (lo elimina)

        // si la cola queda vacia el final de cola tambien es null
        if (frente == null) {
            finalCola = null;
        }
        tamano--;
        return datoFrente;
    }

    public boolean isEmpty() {
        return frente == null;
    }

    public int size() {
        return tamano;
    }
}
