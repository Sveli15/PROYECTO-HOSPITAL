package modelo;

import fachada.IPersistenciaFachada;

public class Consulta {

    public enum Estado {
        PROGRAMADA, CANCELADA, RESUELTA
    }

    private final int id;
    private final Paciente paciente;
    private final Medico medico;
    private final String fecha;
    private Estado estado;


    public Consulta(int id, Paciente paciente, Medico medico, String fecha) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.estado = Estado.PROGRAMADA;
    }

    public int getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public String getFecha(){
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String toArchivo() {
        return id + "|" + paciente.getId() + "|" + medico.getId() + "|" + fecha + "|" + estado.name();
    }

    public static Consulta fromArchivo(String linea, IPersistenciaFachada fachada) {
        String[] p = linea.split("\\|", -1);
        Paciente paciente = fachada.obtenerPacientePorId(Integer.parseInt(p[1]));
        Medico medico = fachada.obtenerMedicoPorId(Integer.parseInt(p[2]));
        Consulta c = new Consulta(Integer.parseInt(p[0]), paciente, medico, String.valueOf(p[3]));
        c.setEstado(Estado.valueOf(p[4]));
        return c;
    }

    @Override
    public String toString() {
        return "Consulta [ID= " + id + ", Paciente= " + paciente + ", Médico= " 
                + medico + ", fecha = " + fecha + ", estado= " + estado + "]";
    }
}
