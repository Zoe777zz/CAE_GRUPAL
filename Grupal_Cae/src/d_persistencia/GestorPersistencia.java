package d_persistencia;

import c_logica.GestorTickets;
import java.io.*;

    //maneja la serializacion y deserializacion del objeto gestortickets

public class GestorPersistencia {

    // nombre fijo del archivo binario
    private final String RUTA_ARCHIVO = "estado_cae.dat";

   //guarda el estado actual del gestortickets en un archivo

    public void guardar(GestorTickets gestor) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(gestor);
        }
    }


    public GestorTickets cargar() throws IOException, ClassNotFoundException {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists() || file.length() == 0) {
            // el archivo no existe o esta vacio, retorna null para crear uno nuevo
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_ARCHIVO))) {
            return (GestorTickets) ois.readObject();
        }
    }
}