package modelo;

public class EquipoMedico {

    private final int id;
    private final String nombre;
    private int cantidad;

    public EquipoMedico(int id, String nombre, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void inventariar(int unidades) {
        if (unidades <= 0) {
            throw new IllegalArgumentException("La cantidad a inventariar debe ser mayor a cero.");
        }
        this.cantidad += unidades;
    }

    public void desinventariar(int unidades) {
        if (unidades <= 0) {
            throw new IllegalArgumentException("La cantidad a desinventariar debe ser mayor a cero.");
        }
        if (unidades > this.cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock para desinventariar esa cantidad.");
        }
        this.cantidad -= unidades;
    }

    public String toArchivo() {
        return id + "|" + nombre + "|" + cantidad;
    }

    public static EquipoMedico fromArchivo(String linea) {
        String[] p = linea.split("\\|", -1);
        return new EquipoMedico(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]));
    }

    @Override
    public String toString() {
        return "EquipoMedico [ID= " + id + ", nombre= " + nombre + ", cantidadDisponible= " + cantidad + "]";
    }
}
