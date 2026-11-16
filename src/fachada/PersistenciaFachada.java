package fachada;

import modelo.*;
import persistencia.*;
import java.util.List;

public class PersistenciaFachada implements IPersistenciaFachada {

    private final PersistenciaPacientes persistenciaPacientes;
    private final PersistenciaMedicos persistenciaMedicos;
    private final PersistenciaEspecialidades persistenciaEspecialidades;
    private final PersistenciaInventarios persistenciaInventarios;
    private final PersistenciaConsultas persistenciaConsultas;

    public PersistenciaFachada() {
        this.persistenciaEspecialidades = new PersistenciaEspecialidades();
        this.persistenciaPacientes = new PersistenciaPacientes();
        this.persistenciaMedicos = new PersistenciaMedicos(this);
        this.persistenciaInventarios = new PersistenciaInventarios();
        this.persistenciaConsultas = new PersistenciaConsultas(this);
    }

    @Override
    public void agregarPaciente(Paciente paciente) {
        persistenciaPacientes.agregarPaciente(paciente);
    }

    @Override
    public Paciente obtenerPacientePorId(int id) {
        return persistenciaPacientes.obtenerPacientePorId(id);
    }

    @Override
    public Paciente actualizarPacientePorId(int id, Paciente actualizado) {
        return persistenciaPacientes.actualizarPaciente(id, actualizado);
    }

    @Override
    public void eliminarPacientePorId(int id) {
        persistenciaPacientes.eliminarPaciente(id);
    }

    @Override
    public List<Paciente> listarPacientes() {
        return persistenciaPacientes.listarPacientes();
    }

    @Override
    public List<Paciente> listarPacientes(String direccion, Integer edadMin, Integer edadMax) {
        return persistenciaPacientes.listarPacientes(direccion, edadMin, edadMax);
    }

    @Override
    public void agregarMedico(Medico medico) {
        persistenciaMedicos.agregarMedico(medico);
    }

    @Override
    public Medico obtenerMedicoPorId(int id) {
        return persistenciaMedicos.obtenerMedicoPorId(id);
    }

    @Override
    public List<Medico> listarMedicos() {
        return persistenciaMedicos.listarMedicos();
    }

    @Override
    public void agregarEspecialidad(Especialidad especialidad) {
        persistenciaEspecialidades.agregarEspecialidad(especialidad);
    }

    @Override
    public Especialidad obtenerEspecialidadPorId(int id) {
        return persistenciaEspecialidades.obtenerEspecialidadPorId(id);
    }

    @Override
    public List<Especialidad> listarEspecialidades() {
        return persistenciaEspecialidades.listarEspecialidades();
    }

    @Override
    public void agregarEquipoMedico(EquipoMedico equipo) {
        persistenciaInventarios.agregarEquipoMedico(equipo);
    }

    @Override
    public void actualizarCantidadEquipo(int id, int cantidad) {
        persistenciaInventarios.actualizarCantidadEquipo(id, cantidad);
    }

    @Override
    public List<EquipoMedico> listarEquiposMedicos() {
        return persistenciaInventarios.listarEquiposMedicos();
    }
    
    @Override
    public EquipoMedico obtenerEquipoPorId(int id) {
        return persistenciaInventarios.obtenerEquipoPorId(id);
    }


    @Override
    public void programarConsulta(Consulta consulta) {
        persistenciaConsultas.programarConsulta(consulta);
    }

    @Override
    public List<Consulta> listarConsultas() {
        return persistenciaConsultas.listarConsultas();
    }

    @Override
    public List<Consulta> consultasPorPaciente(int idPaciente) {
        return persistenciaConsultas.consultasPorPaciente(idPaciente);
    }

    @Override
    public List<Consulta> consultasPorMedico(int idMedico) {
        return persistenciaConsultas.consultasPorMedico(idMedico);
    }

    @Override
    public List<Consulta> buscarConsultasPorFecha(String patronFecha) {
        return persistenciaConsultas.buscarConsultasPorFecha(patronFecha);
    }

    @Override
    public void cancelarConsulta(int idConsulta) {
        persistenciaConsultas.cancelarConsulta(idConsulta);
    }

    @Override
    public List<Paciente> buscarPorNombre(String patron) {
         return persistenciaPacientes.buscarPorNombre(patron); 
    }

    @Override
    public List<Consulta> buscarConsultasPorFechaa(String patronFecha) {
        
         return persistenciaConsultas.buscarConsultasPorFecha(patronFecha);
        
    }
}
