package persistencia;

import fachada.IPersistenciaFachada;
import modelo.Consulta;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
         if (fecha == null || fecha.isBlank()) {
        throw new IllegalArgumentException("La fecha no puede estar vacía.");
    }

    Pattern patron = Pattern.compile(
        "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$"
    );

    Matcher matcher = patron.matcher(fecha);

    if (!matcher.matches()) {
        throw new IllegalArgumentException("Formato inválido. Usa dd/MM/yyyy.");
    }

    try {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        LocalDate.parse(fecha, formato);

    } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("La fecha no existe en el calendario.");
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
    
   public List<Consulta> buscarConsultasPorFecha(String patronFecha) {
     List<Consulta> encontradas = new ArrayList<>();

    Pattern patronEntrada = Pattern.compile(
        "^(\\d{2})/(\\d{2})/(\\d{4})$"
    );

    Matcher matcherEntrada = patronEntrada.matcher(patronFecha);

    if (!matcherEntrada.matches()) {
        throw new IllegalArgumentException("Formato inválido. Usa dd/MM/yyyy.");
    }

    String diaBuscado = matcherEntrada.group(1);
    String mesBuscado = matcherEntrada.group(2);
    String anioBuscado = matcherEntrada.group(3);

    // Reconoce yyyy-MM-ddTHH:mm:ss o yyyy-MM-dd
    Pattern patronISO = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})(?:T.*)?$"
    );

    // Reconoce dd/MM/yyyy
    Pattern patronNormal = Pattern.compile(
        "^(\\d{2})/(\\d{2})/(\\d{4})$"
    );

    for (Consulta c : consultas) {

        String fechaGuardada = c.getFecha();

        Matcher matcherISO = patronISO.matcher(fechaGuardada);
        Matcher matcherNormal = patronNormal.matcher(fechaGuardada);

        String dia = "";
        String mes = "";
        String anio = "";

        if (matcherISO.matches()) {

            anio = matcherISO.group(1);
            mes = matcherISO.group(2);
            dia = matcherISO.group(3);

        } else if (matcherNormal.matches()) {

            dia = matcherNormal.group(1);
            mes = matcherNormal.group(2);
            anio = matcherNormal.group(3);
        }

        if (dia.equals(diaBuscado)
                && mes.equals(mesBuscado)
                && anio.equals(anioBuscado)) {

            encontradas.add(c);
        }
    }

    return encontradas;
}
}