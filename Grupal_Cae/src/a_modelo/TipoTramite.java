package a_modelo;

import java.io.Serializable;

public enum TipoTramite implements Serializable {
    // 1. Asignamos los IDs y las descripciones en el constructor
    CERTIFICADOS(1, "Tramites de certificados"),
    CONSTANCIAS(2, "Solicitud de constancias"),
    HOMOLOGACIONES(3, "Tramite de homologaciones");

    private final int id;
    private final String descripcion;

    // Constructor que asocia el ID y la descripción a cada elemento del enum
    TipoTramite(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

  
    public int getId() {
        return id;
    }

    
    public String getDescripcion() {
        return descripcion;
    }

    public static TipoTramite porId(int id) {
        // Recorremos los valores para buscar el ID, ya que no son ordinales
        for (TipoTramite tramite : values()) {
            if (tramite.id == id) {
                return tramite;
            }
        }
        throw new IllegalArgumentException("ID de tramite invalido: " + id + ".");
    }
}
