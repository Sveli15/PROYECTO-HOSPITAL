package modelo;

public class Paciente {

    private final int id;
    private final String nombre;
    private final int edad;
    private String direccion;

    public Paciente(int id, String nombre, int edad, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String toArchivo() {
        return id + "|" + nombre + "|" + edad + "|" + direccion;
    }

    public static Paciente fromArchivo(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Paciente(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3]);
    }

    @Override
    public String toString() {
        return "Paciente [ID= " + id + ", nombre= " + nombre + ", edad= " + getEdad() 
                + ", direccion= " + direccion + "]";
    }
}
