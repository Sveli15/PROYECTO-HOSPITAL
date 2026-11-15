package persistencia;

import fachada.IPersistenciaFachada;
import modelo.Consulta;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaConsultas {

    private final List<Consulta> consultas;
    private final Path archivo;
    private final IPersistenciaFachada fachada;

    public PersistenciaConsultas(IPersistenciaFachada fachada) {
        this.archivo = Paths.get("datos", "consultas.txt");
        this.consultas = new ArrayList<>();
        this.fachada = fachada;
        cargar();
    }

    private void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        consultas.add(Consulta.fromArchivo(linea, fachada));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar consultas: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (Consulta c : consultas) {
                lineas.add(c.toArchivo());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error al guardar consultas: " + e.getMessage());
        }
    }

    public void programarConsulta(Consulta consulta) {
        consultas.add(consulta);
        guardar();
    }

    public List<Consulta> listarConsultas() {
        return new ArrayList<>(consultas);
    }

    public List<Consulta> consultasPorPaciente(int idPaciente) {
        return consultas.stream().filter(c -> c.getPaciente().getId() == idPaciente).toList();
    }

    public List<Consulta> consultasPorMedico(int idMedico) {
        return consultas.stream().filter(c -> c.getMedico().getId() == idMedico).toList();
    }

    public List<Consulta> consultasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return consultas.stream().filter(c -> !c.getFechaHora().isBefore(inicio)).filter(c -> !c.getFechaHora().isAfter(fin)).toList();
    }

    public void cancelarConsulta(int idConsulta) {
        for (Consulta c : consultas) {
            if (c.getId() == idConsulta) {
                c.setEstado(Consulta.Estado.CANCELADA);
                guardar();
                return;
            }
        }
        throw new IllegalArgumentException("No existe una consulta con ID " + idConsulta);
    }
}
