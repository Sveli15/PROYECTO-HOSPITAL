package persistencia;

import fachada.IPersistenciaFachada;
import modelo.Consulta;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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

    
    private void validaFechaConsulta(String fecha){
        if(fecha == null || fecha.isBlank()){
            throw new IllegalArgumentException("La fecha no puede estar vacia.");
        }
        if(fecha.matches("(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[0,1,2])\\/(19|20)\\d{2}")){
            System.out.println("Fecha valida.");
        } else{
            throw new IllegalArgumentException("Fecha invalida.");
        }
    }
    
    
    private void validaIdConsulta(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número entero mayor que cero");
        }
        System.out.println("ID valido.");
    }
    
    
    public void programarConsulta(Consulta consulta) {
        validaIdConsulta(consulta.getId());
        validaFechaConsulta(consulta.getFecha());
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

    public List<Consulta> buscarConsultasPorFecha(String patronFecha) {
        return consultas.stream().filter(c -> (c.getFecha() == null ? patronFecha == null : c.getFecha().equals(patronFecha))).toList();
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
