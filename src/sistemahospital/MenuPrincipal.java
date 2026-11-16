package sistemahospital;

import modelo.*;
import fachada.IPersistenciaFachada;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {

    private final IPersistenciaFachada persistencia;
    private final Scanner scanner;

    public MenuPrincipal(IPersistenciaFachada persistencia) {
        this.persistencia = persistencia;
        this.scanner = new Scanner(System.in);
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\nSISTEMA DE GESTIÓN HOSPITALARIA");
            System.out.println("1. Agregar paciente");
            System.out.println("2. Agregar médico");
            System.out.println("3. Agregar especialidad");
            System.out.println("4. Agregar equipo médico");
            System.out.println("5. Agregar consulta");
            System.out.println("6. Listar datos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            int opcion = leerEntero();
            switch (opcion) {
                case 1 ->
                    opcionAgregarPaciente();
                case 2 ->
                    opcionAgregarMedico();
                case 3 ->
                    opcionAgregarEspecialidad();
                case 4 ->
                    opcionAgregarEquipoMedico();
                case 5 ->
                    opcionAgregarConsulta();
                case 6 ->
                    opcionListarDatos();
                case 0 ->
                    salir = true;
                default ->
                    System.out.println("Opción no válida.");
            }
        }
        System.out.println("¡Hasta luego :D!");
    }

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Debe ingresar un número entero. Intente de nuevo: ");
            }
        }
    }

    private int leerIdPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            int id = leerEntero();
            if (id > 0) {
                return id;
            }
            System.out.println("El ID debe ser un número positivo.");
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private boolean confirmar(String mensaje) {
        System.out.print(mensaje + " (s/n): ");
        String r = scanner.nextLine().trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí");
    }

    private void opcionAgregarPaciente() {
        try {
            System.out.println("\n--- Agregar paciente ---");
            int id = leerIdPositivo("ID del paciente: ");
            if (persistencia.obtenerPacientePorId(id) != null) {
                System.out.println("Ya existe un paciente con ese ID.");
                return;
            }
            String nombre = leerTexto("Nombre: ");
            if (nombre.isBlank()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            System.out.print("Fecha de nacimiento (AAAA-MM-DD): ");
            int edad = leerEntero();
            if (edad <= 0) {
                System.out.println("Edad no reconocida.");
                return;
            }
            String direccion = leerTexto("Dirección: ");

            if (confirmar("¿Confirma el registro del paciente?")) {
                persistencia.agregarPaciente(
                        new Paciente(id, nombre, edad, direccion));
                System.out.println("Paciente agregado correctamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void opcionAgregarMedico() {
        try {
            System.out.println("\n--- Agregar médico ---");
            int id = leerIdPositivo("ID del médico: ");
            if (persistencia.obtenerMedicoPorId(id) != null) {
                System.out.println("Ya existe un médico con ese ID.");
                return;
            }
            String nombre = leerTexto("Nombre: ");
            if (nombre.isBlank()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }

            List<Especialidad> especialidades = persistencia.listarEspecialidades();
            if (especialidades.isEmpty()) {
                System.out.println("Debe registrar especialidades antes de agregar un médico.");
                return;
            }
            System.out.println("Especialidades disponibles:");
            for (Especialidad e : especialidades) {
                System.out.println("  " + e.getId() + " - " + e.getNombre());
            }
            int idEsp = leerIdPositivo("ID de la especialidad: ");
            Especialidad especialidad = persistencia.obtenerEspecialidadPorId(idEsp);
            if (especialidad == null) {
                System.out.println("La especialidad indicada no existe.");
                return;
            }

            if (confirmar("¿Confirma el registro del médico?")) {
                persistencia.agregarMedico(new Medico(id, nombre, especialidad));
                System.out.println("Médico agregado correctamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void opcionAgregarEspecialidad() {
        try {
            System.out.println("\n--- Agregar especialidad ---");
            int id = leerIdPositivo("ID de la especialidad: ");
            if (persistencia.obtenerEspecialidadPorId(id) != null) {
                System.out.println("Ya existe una especialidad con ese ID.");
                return;
            }
            String nombre = leerTexto("Nombre: ");
            if (nombre.isBlank()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            if (confirmar("¿Confirma el registro de la especialidad?")) {
                persistencia.agregarEspecialidad(new Especialidad(id, nombre));
                System.out.println("Especialidad agregada correctamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void opcionAgregarEquipoMedico() {
        try {
            System.out.println("\n--- Agregar equipo médico ---");
            int id = leerIdPositivo("ID del equipo: ");
            if (persistencia.listarEquiposMedicos().stream()
                    .anyMatch(e -> e.getId() == id)) {
                System.out.println("Ya existe un equipo con ese ID.");
                return;
            }
            String nombre = leerTexto("Nombre del equipo: ");
            if (nombre.isBlank()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            int cantidad = leerIdPositivo("Cantidad disponible: ");

            if (confirmar("¿Confirma el registro del equipo?")) {
                persistencia.agregarEquipoMedico(new EquipoMedico(id, nombre, cantidad));
                System.out.println("Equipo agregado correctamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void opcionAgregarConsulta() {
        try {
            System.out.println("\n--- Agendar consulta ---");
            int id = leerIdPositivo("ID de la consulta: ");
            if (persistencia.listarConsultas().stream()
                    .anyMatch(c -> c.getId() == id)) {
                System.out.println("Ya existe una consulta con ese ID.");
                return;
            }

            int idPaciente = leerIdPositivo("ID del paciente: ");
            Paciente paciente = persistencia.obtenerPacientePorId(idPaciente);
            if (paciente == null) {
                System.out.println("No existe un paciente con ID " + idPaciente + ".");
                return;
            }
            System.out.println("Paciente encontrado: " + paciente);

            int idMedico = leerIdPositivo("ID del médico: ");
            Medico medico = persistencia.obtenerMedicoPorId(idMedico);
            if (medico == null) {
                System.out.println("No existe un médico con ID " + idMedico + ".");
                return;
            }
            System.out.println("Médico encontrado: " + medico);

            System.out.print("Fecha (dd/MM/yyyy): ");
            String fecha= scanner.nextLine();
            

            if (confirmar("¿Confirma agendar la consulta?")) {
                Consulta consulta = new Consulta(id, paciente, medico, fecha);
                persistencia.programarConsulta(consulta);
                System.out.println("Consulta agendada correctamente: " + consulta);
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void opcionListarDatos() {
        System.out.println("\nListar datos");
        System.out.println("1. Pacientes");
        System.out.println("2. Médicos");
        System.out.println("3. Especialidades");
        System.out.println("4. Equipos médicos");
        System.out.println("5. Consultas");
        System.out.print("Opción: ");

        int opcion = leerEntero();
        switch (opcion) {
            case 1 ->
                listarPacientes();
            case 2 ->
                listarMedicos();
            case 3 ->
                listarEspecialidades();
            case 4 ->
                listarEquipos();
            case 5 ->
                listarConsultas();
            default ->
                System.out.println("Opción no válida.");
        }
    }

    private void listarPacientes() {
        System.out.println("\nListado de pacientes");
        System.out.println("Filtros (deje vacío para omitir):");
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine().trim();
        System.out.print("Edad mínima: ");
        String edadMinStr = scanner.nextLine().trim();
        System.out.print("Edad máxima: ");
        String edadMaxStr = scanner.nextLine().trim();

        Integer edadMin = edadMinStr.isBlank() ? null : Integer.valueOf(edadMinStr);
        Integer edadMax = edadMaxStr.isBlank() ? null : Integer.valueOf(edadMaxStr);

        List<Paciente> pacientes = persistencia.listarPacientes();
        List<Paciente> resultado = pacientes.stream().filter(p -> direccion.isBlank()
                || p.getDireccion().toLowerCase().contains(direccion.toLowerCase()))
                .filter(p -> edadMin == null || p.getEdad() >= edadMin)
                .filter(p -> edadMax == null || p.getEdad() <= edadMax).toList();

        if (resultado.isEmpty()) {
            System.out.println("No se encontraron pacientes con los filtros indicados.");
            return;
        }
        for (Paciente p : resultado) {
            System.out.println(p);
        }
    }

    private void listarMedicos() {
        System.out.println("\nListado de médicos");
        List<Medico> medicos = persistencia.listarMedicos();
        if (medicos.isEmpty()) {
            System.out.println("No hay médicos registrados.");
            return;
        }
        for (Medico m : medicos) {
            System.out.println(m);
        }
    }

    private void listarEspecialidades() {
        System.out.println("\nListado de especialidades");
        List<Especialidad> especialidades = persistencia.listarEspecialidades();
        if (especialidades.isEmpty()) {
            System.out.println("No hay especialidades registradas.");
            return;
        }
        for (Especialidad e : especialidades) {
            System.out.println(e);
        }
    }

    private void listarEquipos() {
        System.out.println("\nInventario de equipos médicos");
        System.out.print("ID del equipo (vacío = inventario completo): ");
        String idStr = scanner.nextLine().trim();

        List<EquipoMedico> equipos = persistencia.listarEquiposMedicos();
        if (idStr.isBlank()) {
            if (equipos.isEmpty()) {
                System.out.println("No hay equipos registrados.");
                return;
            }
            for (EquipoMedico e : equipos) {
                System.out.println(e);
            }
            return;
        }

        int id = Integer.parseInt(idStr);
        EquipoMedico encontrado = equipos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (encontrado == null) {
            System.out.println("No existe un equipo con ID " + id + ".");
        } else {
            System.out.println(encontrado);
        }
    }

    private void listarConsultas() {
        System.out.println("\nConsultas médicas");
        System.out.println("1. Por paciente");
        System.out.println("2. Por médico");
        System.out.println("3. Por fecha");
        System.out.println("4. Todas");
        System.out.print("Opción: ");

        int opcion = leerEntero();
        List<Consulta> consultas = persistencia.listarConsultas();

        switch (opcion) {
            case 1 -> {
                int idPaciente = leerIdPositivo("ID del paciente: ");
                List<Consulta> resultado = consultas.stream().filter(c -> c.getPaciente().getId() == idPaciente).toList();
                mostrarConsultas(resultado, "No hay consultas para ese paciente.");
            }
            case 2 -> {
                int idMedico = leerIdPositivo("ID del médico: ");
                List<Consulta> resultado = consultas.stream().filter(c -> c.getMedico().getId() == idMedico).toList();
                mostrarConsultas(resultado, "No hay consultas para ese médico.");
            }
            case 3 -> {
                System.out.println("Fecha de consulta: ");
                String fechaC= scanner.nextLine();
                if(fechaC == null || fechaC.isBlank()){
                    throw new IllegalArgumentException("La fecha no puede estar vacia.");
                }
                if(fechaC.matches("(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[0,1,2])\\/(19|20)\\d{2}")){
                    List<Consulta> resultado = consultas.stream().filter(c -> (c.getFecha().equals(fechaC))).toList();
                    mostrarConsultas(resultado, "No hay consultas en esa fecha.");
                } else{
                    throw new IllegalArgumentException("Fecha invalida.");
                }
            }
            case 4 ->
                mostrarConsultas(consultas, "No hay consultas registradas.");
            default ->
                System.out.println("Opción no válida.");
        }
    }

    private void mostrarConsultas(List<Consulta> consultas, String vacio) {
        if (consultas.isEmpty()) {
            System.out.println(vacio);
            return;
        }
        for (Consulta c : consultas) {
            System.out.println(c);
        }
    }
}
