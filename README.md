# Centro de Atención al Estudiante (CAE)

Este Sistema CAE gestiona la atención de tickets en un Centro de Atención al Estudiante implementando estructuras propias como colas, pilas y listas enlazadas simples para controlar el flujo de atención, el registro de notas y la prioridad de los tickets. El sistema simula un entorno real de atención con persistencia entre ejecuciones y reportes exportables.

---

## Características Principales

- `Gestión de Tickets` — Crear, listar y actualizar tickets con estados definidos.  
- `Cola de Espera Prioritaria` — Diferencia entre atención normal y urgente, manteniendo el orden FIFO.  
- `Notas y Seguimiento` — Cada ticket puede tener un historial de notas almacenado en listas enlazadas.  
- `Control de Flujo` — Catálogo de estados para el proceso completo de atención con validación interna.  
- `Persistencia Automática` — Los datos se guardan y cargan en formato UTF-8 desde archivos `.dat`.  
- `Reportes Exportables` — Genera informes `.csv` con estadísticas y resumen de atención.  
- `Arquitectura MVC` — Código modular en capas para facilitar mantenimiento y escalabilidad.  


---

# Decisiones de Diseño

### Paquetes (Separación por Responsabilidad)

| Paquete | Descripción |
|----------|-------------|
| `a_modelo` | Entidades del dominio: `Ticket`, `Nota`, `EstadoTicket`, `TipoTramite`. |
| `b_estructuras` | Implementaciones propias de estructuras: `Cola`, `Pila`, `ListaEnlazada`, `NodoLista`. |
| `c_logica` | Reglas de negocio y control del flujo: `GestorTickets`, `GestorHistorial`, manejo de excepciones. |
| `d_persistencia` | Control de lectura/escritura de datos en archivos (`PersistenciaCae`). |
| `d_historial` | Registro de acciones y reportes generados por sesión. |
| `d_vista` | Interfaz de usuario por consola: `MenuPrincipal`, `MenuOperaciones`. |

### *Estructuras propias.* 

- `Lista Enlazada (SLL):` Para notas e historial de reportes.  
- `Cola Normal y Urgente:` Controla la prioridad de atención manteniendo el orden FIFO.  
- `Pila:` Gestiona acciones UNDO y REDO.  
- `Persistencia UTF-8:` Guarda y carga automáticamente tickets, notas e historial.  
- `Excepciones Personalizadas:` `VacioException`, `PosicionException` para evitar errores lógicos.

---

# Catálogo de Estados (EstadoTicket)

Constantes del enum usado por los tickets:

- `EN_COLA` — Ticket en espera.  
- `EN_ATENCION` — Ticket que se está atendiendo.  
- `COMPLETADO` — Ticket finalizado.

--- 

# Casos Borde

- `Intentar atender cuando ambas colas están vacías` → lanza `VacioException`.  
- `Realizar UNDO/REDO con pila vacía` → mensaje controlado sin error.  
- `Intentar eliminar una nota inexistente` → no altera la lista.  
- `Transición inválida (por ejemplo, `COMPLETADO → EN_ATENCION`)` → bloqueada por validación lógica.  
- `Fallos de exportación` → manejo controlado con mensaje descriptivo.  
- `Manejo de entradas inválidas en menús` → Lectura desde teclado.

---
# Guía de Ejecución

## Requisitos Previos
- JDK 11 o superior  
- IDE recomendado: Visual Studio Code o IntelliJ IDEA  
- Git instalado (para clonar el repositorio)  

#### Pasos para Ejecutar el Proyecto

## 1. ` Clonar el Repositorio`
git clone https://github.com/Zoe777zz/CAE_GRUPAL  
cd CAE_GRUPAL  

## 2. ` Compilar el Proyecto`
Desde la carpeta raíz, se compilan todos los archivos fuente:  
javac -d bin src/**/*.java  
Esto creará las clases compiladas en la carpeta bin/.  

## 3. ` Ejecutar el Programa`
Inicia el sistema desde la clase principal MenuPrincipal:  
java -cp bin d_vista.MenuPrincipal  

## 4. ` Interacción en Consola`
El sistema mostrará el menú principal:

`==  BIENVENIDO AL SISTEMA CAE DE GESTIÓN DE TICKETS  ===`
- `1. Crear nuevo ticket` → **(nombre, tipo de trámite y prioridad)**  
- `2. Atender siguiente ticket` → **(priorizando los urgentes)**  
- `3. Gestionar ticket en atención` → **(añadir notas o cambiar estado)**  
- `4. Ver reporte/estadísticas` → **(visualizar reportes en consola)**  
- `5. Exportar reporte` → **(exportar reportes en formato .csv)**  
- `6. Cargar tickets desde archivo (CSV)` → **(importa tickets previamente guardados)**  
- `7. Salir` → **(guardar y salir del sistema persistencia automática)**  



  
## 5. ` Reanudación de Sesión`
Al volver a ejecutar el programa, los datos previos se cargan automáticamente desde los archivos .csv de persistencia, permitiendo continuar con el mismo estado anterior.

---

## Créditos del Proyecto
**Proyecto desarrollado por el grupo conformado por:**  
- Buri Camacho Maria Soledad    
- Panamito Flores Ana Cristina  
- Roa Carrion Victor Fernando
- Maldonado Machuca Martina Alejandra

# Universidad Nacional de Loja
## Carrera de Computación — Tercer ciclo
