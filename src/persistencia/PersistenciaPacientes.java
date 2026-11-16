package persistencia;

import modelo.Paciente;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PersistenciaPacientes {

    private final List<Paciente> pacientes;
    private final Path archivo;

    public PersistenciaPacientes() {
        this.archivo = Paths.get("datos", "pacientes.txt");
        this.pacientes = new ArrayList<>();
        cargar();
    }

    private void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        pacientes.add(Paciente.fromArchivo(linea));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar pacientes: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            List<String> lineas = new ArrayList<>();
            for (Paciente p : pacientes) {
                lineas.add(p.toArchivo());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error al guardar pacientes: " + e.getMessage());
        }
    }

    private void validarIdPositivo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        } 
        if(nombre.matches("([a-zA-ZáéíóúñÑ\\s]){1,50}")){
            System.out.println("Nombre válido");
        }else{
            throw new IllegalArgumentException("El nombre no es valido."); 
        }
    }
    
    private void validarEdad(int edad){
        String edadSt = Integer.toString(edad);
        if(edadSt.matches("[0-9]{1}[0-9]?[0]?")){
            System.out.println("Edad valida");
        } else {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120");
        }
    }
    
    private void validarDireccion(String direccion){
        if(direccion == null || direccion.isBlank()){
            throw new IllegalArgumentException("La direccion no puede estar vacia");
        }
        if(direccion.matches("([.,a-zA-ZáéíóúñÑ0-9\\s]){1,50}")){
           System.out.println("Direccion valida");
        } else{
            throw new IllegalArgumentException("La direccion es invalida");
        }
    }

    public void agregarPaciente(Paciente paciente) {
        validarIdPositivo(paciente.getId());
        validarNombre(paciente.getNombre());
        validarEdad(paciente.getEdad());
        validarDireccion(paciente.getDireccion());
        if (obtenerPacientePorId(paciente.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente con ese ID.");
        }
        pacientes.add(paciente);
        guardar();
    }

    public Paciente obtenerPacientePorId(int id) {
        return pacientes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public Paciente actualizarPaciente(int id, Paciente actualizado) {
        validarIdPositivo(id);
        validarNombre(actualizado.getNombre());
        validarEdad(actualizado.getEdad());
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId() == id) {
                pacientes.set(i, actualizado);
                guardar();
                return actualizado;
            }
        }
        throw new IllegalArgumentException("No existe un paciente con ese ID.");
    }

    public void eliminarPaciente(int id) {
        validarIdPositivo(id);
        boolean eliminado = pacientes.removeIf(p -> p.getId() == id);
        if (!eliminado) {
            throw new IllegalArgumentException("No existe un paciente con ese ID.");
        }
        guardar();
    }

    public List<Paciente> listarPacientes(String direccion, Integer edadMin, Integer edadMax) {
        return pacientes.stream().filter(p -> direccion == null || direccion.isBlank()
                || p.getDireccion().toLowerCase().contains(direccion.toLowerCase()))
                .filter(p -> edadMin == null || p.getEdad() >= edadMin)
                .filter(p -> edadMax == null || p.getEdad() <= edadMax).collect(Collectors.toList());
    }

    public List<Paciente> listarPacientes() {
        return new ArrayList<>(pacientes);
    }
    
    public List<Paciente> buscarPorNombre(String patron) {

    List<Paciente> encontrados = new ArrayList<>();

    Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);

    for (Paciente p : pacientes) {

        Matcher matcher = pattern.matcher(p.getNombre());

        if (matcher.find()) {
            encontrados.add(p);
        }
    }

    return encontrados;
}
}
