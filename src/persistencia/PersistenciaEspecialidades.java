package persistencia;

import modelo.Especialidad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaEspecialidades {

    private final List<Especialidad> especialidades;
    private final Path archivo;

    public PersistenciaEspecialidades() {
        this.archivo = Paths.get("datos", "especialidades.txt");
        this.especialidades = new ArrayList<>();
        cargar();
    }

    private void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        especialidades.add(Especialidad.fromArchivo(linea));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar especialidades: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (Especialidad e : especialidades) {
                lineas.add(e.toArchivo());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error al guardar especialidades: " + e.getMessage());
        }
    }

    public void agregarEspecialidad(Especialidad especialidad) {
        especialidades.add(especialidad);
        guardar();
    }

    public Especialidad obtenerEspecialidadPorId(int id) {
        return especialidades.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public List<Especialidad> listarEspecialidades() {
        return new ArrayList<>(especialidades);
    }
}
