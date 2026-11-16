package persistencia;

import modelo.EquipoMedico;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaInventarios {

    private final List<EquipoMedico> inventarios;
    private final Path archivo;

    public PersistenciaInventarios() {
        this.archivo = Paths.get("datos", "inventarios.txt");
        this.inventarios = new ArrayList<>();
        cargar();
    }

    private void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        inventarios.add(EquipoMedico.fromArchivo(linea));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar inventarios: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (EquipoMedico e : inventarios) {
                lineas.add(e.toArchivo());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error al guardar inventarios: " + e.getMessage());
        }
    }

    private void validarIdPositivo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
    }

    private void validarNombreEquipo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if(nombre.matches("([a-zA-ZáéíóúñÑ\\s]){1,30}")){
            System.out.println("El nombre del equipo es valido");
        } else {
            throw new IllegalArgumentException("El nombre del equipo es invalido");
        }
    }

    private void validarCantidad(int cantidad) {
        String cantidadSt = Integer.toString(cantidad);
        if (cantidadSt.matches("[1-9]+")) {
            System.out.println("Cantidad valida");
        } else{
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
    }

    public void agregarEquipoMedico(EquipoMedico equipo) {
        validarIdPositivo(equipo.getId());
        validarNombreEquipo(equipo.getNombre());
        validarCantidad(equipo.getCantidad());
        if (obtenerEquipoPorId(equipo.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un equipo con ese ID.");
        }
        inventarios.add(equipo);
        guardar();
    }

    public EquipoMedico obtenerEquipoPorId(int id) {
        validarIdPositivo(id);
        return inventarios.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void actualizarCantidadEquipo(int id, int cantidad) {
        validarIdPositivo(id);
        validarCantidad(cantidad);
        for (EquipoMedico e : inventarios) {
            if (e.getId() == id) {
                e.setCantidad(cantidad);
                guardar();
                return;
            }
        }
        throw new IllegalArgumentException("No existe un equipo con ID " + id);
    }

    public List<EquipoMedico> listarEquiposMedicos() {
        return new ArrayList<>(inventarios);
    }
}
