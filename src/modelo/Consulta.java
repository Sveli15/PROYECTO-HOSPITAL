package modelo;

import fachada.IPersistenciaFachada;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consulta {

    public enum Estado {
        PROGRAMADA, CANCELADA, RESUELTA
    }

    private final int id;
    private final Paciente paciente;
    private final Medico medico;
    private final LocalDateTime fechaHora;
    private Estado estado;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Consulta(int id, Paciente paciente, Medico medico, LocalDateTime fechaHora) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String toArchivo() {
        return id + "|" + paciente.getId() + "|" + medico.getId() + "|" + fechaHora.format(FMT) + "|" + estado.name();
    }

    public static Consulta fromArchivo(String linea, IPersistenciaFachada fachada) {
        String[] p = linea.split("\\|", -1);
        Paciente paciente = fachada.obtenerPacientePorId(Integer.parseInt(p[1]));
        Medico medico = fachada.obtenerMedicoPorId(Integer.parseInt(p[2]));
        Consulta c = new Consulta(Integer.parseInt(p[0]), paciente, medico, LocalDateTime.parse(p[3], FMT));
        c.setEstado(Estado.valueOf(p[4]));
        return c;
    }

    @Override
    public String toString() {
        return "Consulta [ID= " + id + ", Paciente= " + paciente + ", Médico= " 
                + medico + ", fechaHora= " + fechaHora + ", estado= " + estado + "]";
    }
}
