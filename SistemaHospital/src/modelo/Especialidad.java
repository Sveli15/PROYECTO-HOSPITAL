package modelo;

public class Especialidad {
    private final int id;
    private final String nombre;

    public Especialidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    
    public String toArchivo() {
        return id + "|" + nombre;
    }

    public static Especialidad fromArchivo(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Especialidad(Integer.parseInt(p[0]), p[1]);
    }

    @Override
    public String toString() {
        return "Especialidad [ID= " + id + ", nombre= " + nombre + "]";
    }
    
}
