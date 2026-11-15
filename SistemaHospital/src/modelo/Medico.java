package modelo;

import fachada.IPersistenciaFachada;

public class Medico {

    private final int id;
    private final String nombre;
    private Especialidad especialidad;

    public Medico(int id, String nombre, Especialidad especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String toArchivo() {
        return id + "|" + nombre + "|" + especialidad.getId();
    }

    public static Medico fromArchivo(String linea, IPersistenciaFachada fachada) {
        String[] p = linea.split("\\|", -1);
        Especialidad esp = fachada.obtenerEspecialidadPorId(Integer.parseInt(p[2]));
        return new Medico(Integer.parseInt(p[0]), p[1], esp);
    }

    @Override
    public String toString() {
        return "Médico [ID= " + id + ", nombre= " + nombre + ", especialidad= " + especialidad.getNombre() + "]";
    }
}
