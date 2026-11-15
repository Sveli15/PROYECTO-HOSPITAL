package persistencia;

import fachada.IPersistenciaFachada;
import modelo.Medico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaMedicos {

    private final List<Medico> medicos;
    private final Path archivo;
    private final IPersistenciaFachada fachada;  //para resolver especialidad

    public PersistenciaMedicos(IPersistenciaFachada fachada) {
        this.archivo = Paths.get("datos", "medicos.txt");
        this.medicos = new ArrayList<>();
        this.fachada = fachada;
        cargar();
    }

    private void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        medicos.add(Medico.fromArchivo(linea, fachada));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar médicos: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (Medico m : medicos) {
                lineas.add(m.toArchivo());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error al guardar médicos: " + e.getMessage());
        }
    }

    public void agregarMedico(Medico medico) {
        medicos.add(medico);
        guardar();
    }

    public Medico obtenerMedicoPorId(int id) {
        return medicos.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    public List<Medico> listarMedicos() {
        return new ArrayList<>(medicos);
    }
}
