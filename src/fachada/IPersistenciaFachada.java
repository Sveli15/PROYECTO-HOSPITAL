package fachada;

import modelo.*;
import java.util.List;

public interface IPersistenciaFachada {
    void agregarPaciente(Paciente paciente);
    Paciente obtenerPacientePorId(int id);
    Paciente actualizarPacientePorId(int id, Paciente actualizado);
    void eliminarPacientePorId(int id);
    List<Paciente> listarPacientes();
    List<Paciente> listarPacientes(String direccion, Integer edadMin, Integer edadMax);
    List<Paciente> buscarPorNombre(String patron);
    
    void agregarMedico(Medico medico);
    Medico obtenerMedicoPorId(int id);
    List<Medico> listarMedicos();

    
    void agregarEspecialidad(Especialidad especialidad);
    Especialidad obtenerEspecialidadPorId(int id);
    List<Especialidad> listarEspecialidades();

    
    void agregarEquipoMedico(EquipoMedico equipo);
    void actualizarCantidadEquipo(int id, int cantidad);
    List<EquipoMedico> listarEquiposMedicos();
    EquipoMedico obtenerEquipoPorId(int id);

    
    void programarConsulta(Consulta consulta);
    List<Consulta> listarConsultas();
    List<Consulta> consultasPorPaciente(int idPaciente);
    List<Consulta> consultasPorMedico(int idMedico);
    List<Consulta> buscarConsultasPorFecha(String patronFecha);
    void cancelarConsulta(int idConsulta);
}
