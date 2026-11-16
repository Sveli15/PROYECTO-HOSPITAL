package gui;

import fachada.PersistenciaFachada;
import java.awt.HeadlessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Consulta;
import modelo.EquipoMedico;
import modelo.Especialidad;
import modelo.Medico;
import modelo.Paciente;

public class ventana extends javax.swing.JFrame {

    PersistenciaFachada fachada = new PersistenciaFachada();

    public ventana() {
        initComponents();
    }

    private int pedirId(String mensaje) {
        String s = JOptionPane.showInputDialog(this, mensaje);
        if (s == null) {
            return -1;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void agregarPacienteGUI() {   
        int id = Integer.parseInt(txtIdPac.getText());
        if (id <= 0) {
            return;
        }   

         try{
            String nombre = JOptionPane.showInputDialog(this, "Nombre:"); 
            String edadStr = JOptionPane.showInputDialog(this, "Edad:");
            int edad = Integer.parseInt(edadStr);
            String direccion = JOptionPane.showInputDialog(this, "Dirección:");
            fachada.agregarPaciente(new Paciente(id, nombre, edad, direccion));
            mostrarMensaje("Paciente agregado correctamente.");
        } catch (HeadlessException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }
     
   private void Buscarpornombrepac(){

    String patron = txtbuscpac.getText().trim();

    List<Paciente> pacientes = fachada.buscarPorNombre(patron);

    DefaultTableModel modelo = (DefaultTableModel) tblbuscpac.getModel();

    modelo.setRowCount(0);

    for (Paciente p : pacientes) {

        modelo.addRow(new Object[]{
            p.getId(),
            p.getNombre(),
            p.getEdad(),
            p.getDireccion()
        });
    }
}
    
     private void buscarConsultasPorFecha(){
     String patron = txtbusccon.getText().trim();

    List<Consulta> consultas = fachada.listarConsultas();

    DefaultTableModel modelo = (DefaultTableModel) tblbusccon.getModel();

    modelo.setRowCount(0);

    try {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");

        LocalDate fechaBuscada = LocalDate.parse(patron, formato);

        String fechaConvertida = fechaBuscada.toString();

        for (Consulta c : consultas) {

            if (c.getFecha().startsWith(fechaConvertida)) {

                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getPaciente().getNombre(),
                    c.getMedico().getNombre(),
                    c.getFecha(),
                    c.getEstado()
                });

            }
        }

    } catch (DateTimeParseException e) {

        JOptionPane.showMessageDialog(this,
                "Ingresa la fecha en formato dd/MM/yyyy");

    }
}
     

    private void consultarPacienteGUI() {
        int id = Integer.parseInt(txtIdPac.getText());
        if (id <= 0) {
            return;
        }
        Paciente p = fachada.obtenerPacientePorId(id);
        if (p != null) {
            mostrarMensaje(p.toString());
        } else {
            mostrarMensaje("No existe un paciente con ID " + id + ".");
        }
    }

    private void actualizarPacienteGUI() {
        try {
            int id = Integer.parseInt(txtIdPac.getText());
            if (id <= 0) {
                return;
            }
            Paciente p = fachada.obtenerPacientePorId(id);
            if (p == null) {
                mostrarMensaje("No existe un paciente con ID " + id + ".");
                return;
            }
            mostrarMensaje("Datos actuales:\n" + p);
            if (!confirmar("¿Desea actualizar este paciente?")) {
                return;
            }
            String nombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", p.getNombre());
            String edadStr = JOptionPane.showInputDialog(this, "Nueva edad:", p.getEdad());
            String direccion = JOptionPane.showInputDialog(this, "Nueva dirección:", p.getDireccion());

            if (nombre == null || direccion == null || edadStr == null) {
                return;
            }
            int nuevaEdad = Integer.parseInt(edadStr);
            Paciente actualizado = new Paciente(id, nombre, nuevaEdad, direccion);
            fachada.actualizarPacientePorId(id, actualizado);
            mostrarMensaje("Paciente actualizado correctamente.");
        } catch (NumberFormatException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarPacienteGUI() {
        try {
            int id = Integer.parseInt(txtIdPac.getText());
            if (id <= 0) {
                return;
            }
            Paciente p = fachada.obtenerPacientePorId(id);
            if (p == null) {
                mostrarMensaje("No existe un paciente con ID " + id + ".");
                return;
            }
            mostrarMensaje("Datos del paciente:\n" + p);
            if (confirmar("¿Confirma la eliminación de este paciente?")) {
                fachada.eliminarPacientePorId(id);
                mostrarMensaje("Paciente eliminado correctamente.");
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex);
        }
    }

    private void listarPacientesGUI() {
        try {
        if (checkDirPac.isSelected()){
            String direccion = JOptionPane.showInputDialog(this,
                    "Filtrar por dirección (vacío = sin filtro):");
            if (direccion == null) {
                return;
            }
            List<Paciente> resultado = fachada.listarPacientes(direccion, null, null);
            if (resultado.isEmpty()) {
                mostrarMensaje("No se encontraron pacientes con los filtros indicados.");
            } else {
                StringBuilder sb = new StringBuilder("Pacientes encontrados:\n\n");
                for (Paciente p : resultado) {
                    sb.append(p).append("\n");
                }
                mostrarMensaje(sb.toString());
            }
        } else if (checkEdadPac.isSelected()) {
            String edadMinStr = JOptionPane.showInputDialog(this,
                    "Edad mínima (vacío = sin filtro):");
            String edadMaxStr = JOptionPane.showInputDialog(this,
                    "Edad máxima (vacío = sin filtro):");
            Integer edadMin = edadMinStr == null || edadMinStr.isBlank() ? null : Integer.valueOf(edadMinStr);
            Integer edadMax = edadMaxStr == null || edadMaxStr.isBlank() ? null : Integer.valueOf(edadMaxStr);
            List<Paciente> resultado = fachada.listarPacientes(null, edadMin, edadMax);
            if (resultado.isEmpty()) {
                mostrarMensaje("No se encontraron pacientes con los filtros indicados.");
            } else {
                StringBuilder sb = new StringBuilder("Pacientes encontrados:\n\n");
                for (Paciente p : resultado) {
                    sb.append(p).append("\n");
                }
                mostrarMensaje(sb.toString());
            }
        } else if (checkDirPac.isSelected() && checkEdadPac.isSelected()) {
            String direccion = JOptionPane.showInputDialog(this,
                    "Filtrar por dirección (vacío = sin filtro):");
            if (direccion == null) {
                return;
            }
            String edadMinStr = JOptionPane.showInputDialog(this,
                    "Edad mínima (vacío = sin filtro):");
            String edadMaxStr = JOptionPane.showInputDialog(this,
                    "Edad máxima (vacío = sin filtro):");
            Integer edadMin = edadMinStr == null || edadMinStr.isBlank() ? null : Integer.valueOf(edadMinStr);
            Integer edadMax = edadMaxStr == null || edadMaxStr.isBlank() ? null : Integer.valueOf(edadMaxStr);
            List<Paciente> resultado = fachada.listarPacientes(direccion, edadMin, edadMax);
            if (resultado.isEmpty()) {
                mostrarMensaje("No se encontraron pacientes con los filtros indicados.");
            } else {
                StringBuilder sb = new StringBuilder("Pacientes encontrados:\n\n");
                for (Paciente p : resultado) {
                    sb.append(p).append("\n");
                }
                mostrarMensaje(sb.toString());
            }
        } else {
            List<Paciente> resultado = fachada.listarPacientes(null, null, null);
            StringBuilder sb = new StringBuilder("Pacientes encontrados:\n\n");
                for (Paciente p : resultado) {
                    sb.append(p).append("\n");
                }
                mostrarMensaje(sb.toString());
        }
        } catch (HeadlessException | NumberFormatException e) {
            mostrarError(e);
        }
        
    }

    private void agregarMedicoGUI() {
        try {
            int id = Integer.parseInt(txtIdDoc.getText());
            if (id <= 0) {
                return;
            }
            String nombre = JOptionPane.showInputDialog(this, "Nombre del médico:");
            StringBuilder esp = new StringBuilder("Especialidades disponibles:\n");
            for (Especialidad e : fachada.listarEspecialidades()) {
                esp.append(e.getId()).append(" - ").append(e.getNombre()).append("\n");
            }
            int idEsp = pedirId(esp.toString() + "\nID de la especialidad:");
            if (idEsp <= 0) {
                return;
            }
            Especialidad especialidad = fachada.obtenerEspecialidadPorId(idEsp);
            fachada.agregarMedico(new Medico(id, nombre, especialidad));
            mostrarMensaje("Médico agregado correctamente.");
        } catch (HeadlessException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }
    
     private void agregarEspecialidadGUI(){
        try {
            int id = Integer.parseInt(txtEspID.getText());
            if (id <= 0) {
                return;
            }
            String nombre = JOptionPane.showInputDialog(this, "Nombre de la especialidad:");
            if (nombre.isBlank()) {
                JOptionPane.showConfirmDialog(this, "El nombre no puede estar vacío.");
                return;
            }
            fachada.agregarEspecialidad(new Especialidad(id, nombre));
            mostrarMensaje("Especialidad agregada correctamente.");
        } catch (HeadlessException | IllegalArgumentException ex) {
            mostrarError(ex);
        }
    }

    private void consultarMedicoGUI() {
        int id = Integer.parseInt(txtIdDoc.getText());
        if (id <= 0) {
            return;
        }
        Medico m = fachada.obtenerMedicoPorId(id);
        if (m != null) {
            mostrarMensaje(m.toString());
        } else {
            mostrarMensaje("No existe un médico con ID " + id + ".");
        }
    }
    
   

    private void consultarInventarioGUI() {
        try {
            String s = txtIdEM.getText();
            if (s == null) {
                return;
            }
            if (s.isBlank()) {
                List<EquipoMedico> todos = fachada.listarEquiposMedicos();
                if (todos.isEmpty()) {
                    mostrarMensaje("El inventario está vacío.");
                    return;
                }
                StringBuilder sb = new StringBuilder("Inventario completo:\n\n");
                for (EquipoMedico e : todos) {
                    sb.append(e).append("\n");
                }
                mostrarMensaje(sb.toString());
            } else {
                int id = Integer.parseInt(s.trim());
                EquipoMedico e = fachada.obtenerEquipoPorId(id);
                if (e != null) {
                    mostrarMensaje(e.toString());
                } else {
                    mostrarMensaje("No existe un equipo con ID " + id + ".");
                }
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex);
        }
    }

    private void agregarEquipoGUI(){
        try{
            int id = Integer.parseInt(txtIdEM3.getText());
            if(id <= 0){
                return;
            }
            String nombre = JOptionPane.showInputDialog(this,"Nombre del equipo: ");
            int cantidad = Integer.parseInt(JOptionPane.showInputDialog(this,"Cantidad del equipo: "));
            fachada.agregarEquipoMedico(new EquipoMedico(id,nombre,cantidad));
        } catch( HeadlessException | IllegalArgumentException ex){
            mostrarError(ex);
        }
    }
    
    private void inventariarEquipoGUI() {
        try {
            int id = Integer.parseInt(txtIdEM2.getText());
            if (id <= 0) {
                return;
            }
            EquipoMedico e = fachada.obtenerEquipoPorId(id);
            if (e == null) {
                mostrarMensaje("No existe un equipo con ID " + id + ".");
                return;
            }
            mostrarMensaje("Datos del equipo y stock actual:\n" + e);

            int unidades = pedirId("Cantidad a inventariar (sumar):");
            if (unidades <= 0) {
                mostrarMensaje("La cantidad debe ser mayor a cero.");
                return;
            }
            if (confirmar("¿Confirma sumar " + unidades + " unidades al stock?")) {
                e.inventariar(unidades);
                fachada.actualizarCantidadEquipo(id, e.getCantidad());
                mostrarMensaje("Stock actualizado. Nuevo inventario:\n" + e);
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex);
        }
    }

    private void desinventariarEquipoGUI() {
        try {
            int id = Integer.parseInt(txtIdEM2.getText());
            if (id <= 0) {
                return;
            }
            EquipoMedico e = fachada.obtenerEquipoPorId(id);
            if (e == null) {
                mostrarMensaje("No existe un equipo con ID " + id + ".");
                return;
            }
            mostrarMensaje("Datos del equipo y stock actual:\n" + e);

            int unidades = pedirId("Cantidad a desinventariar (restar):");
            if (unidades <= 0) {
                mostrarMensaje("La cantidad debe ser mayor a cero.");
                return;
            }
            if (confirmar("¿Confirma restar " + unidades + " unidades al stock?")) {
                e.desinventariar(unidades);  //valida que no quede negativo
                fachada.actualizarCantidadEquipo(id, e.getCantidad());
                mostrarMensaje("Stock actualizado. Nuevo inventario:\n" + e);
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex);
        }
    }

    private void consultasPorPacienteGUI() {
        int id = Integer.parseInt(txtIdPac.getText());
        if (id <= 0) {
            return;
        }
        List<Consulta> resultado = fachada.consultasPorPaciente(id);
        mostrarConsultas(resultado, "No hay consultas para ese paciente.");
    }

    private void consultasPorMedicoGUI() {
        int id = Integer.parseInt(txtIdDoc.getText());
        if (id <= 0) {
            return;
        }
        List<Consulta> resultado = fachada.consultasPorMedico(id);
        mostrarConsultas(resultado, "No hay consultas para ese médico.");
    }

    private void buscarConsultasPorFechaGUI() {
        try {
            String fecha = JOptionPane.showInputDialog(this, "Fecha de consulta (dd/MM/yyyy):");
            if (fecha == null) {
                return;
            }
            List<Consulta> resultado = fachada.buscarConsultasPorFecha(fecha);
            mostrarConsultas(resultado, "No hay consultas en esa fecha.");
        } catch (HeadlessException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarConsultas(List<Consulta> consultas, String vacio) {
        if (consultas.isEmpty()) {
            mostrarMensaje(vacio);
            return;
        }
        StringBuilder sb = new StringBuilder("Consultas:\n\n");
        for (Consulta c : consultas) {
            sb.append("ID ").append(c.getId())
                    .append(" | Paciente ").append(c.getPaciente().getNombre())
                    .append(" | Médico ").append(c.getMedico().getNombre())
                    .append(" | ").append(c.getFecha())
                    .append(" | ").append(c.getEstado()).append("\n");
        }
        mostrarMensaje(sb.toString());
    }

    private void agendarConsultaGUI() {
        try {
            int idConsulta = pedirId("ID de la nueva consulta:");
            if (idConsulta <= 0) {
                return;
            }
            int idPaciente = pedirId("ID del paciente:");
            if (idPaciente <= 0) {
                return;
            }
            Paciente p = fachada.obtenerPacientePorId(idPaciente);
            if (p == null) {
                mostrarMensaje("No existe un paciente con ID " + idPaciente + ".");
                return;
            }
            mostrarMensaje("Paciente encontrado:\n" + p);

            int idMedico = pedirId("ID del médico:");
            if (idMedico <= 0) {
                return;
            }
            Medico m = fachada.obtenerMedicoPorId(idMedico);
            if (m == null) {
                mostrarMensaje("No existe un médico con ID " + idMedico + ".");
                return;
            }
            mostrarMensaje("Médico encontrado:\n" + m);

            String fecha = JOptionPane.showInputDialog(this, "Fecha y hora (dd/MM/yyyy):");
            if (fecha == null) {
                return;
            }
            if (confirmar("¿Confirma agendar la consulta?")) {
                Consulta c = new Consulta(idConsulta, p, m, fecha);
                fachada.programarConsulta(c);
                mostrarMensaje("Consulta agendada:\n" + c);
            }
        } catch (HeadlessException ex) {
            mostrarError(ex);
        }
    }

    private void cancelarConsultaGUI() {
        try {
            int id = pedirId("ID de la consulta a cancelar:");
            if (id <= 0) {
                return;
            }
            Consulta c = fachada.listarConsultas().stream()
                    .filter(x -> x.getId() == id)
                    .findFirst()
                    .orElse(null);
            if (c == null) {
                mostrarMensaje("No existe una consulta con ID " + id + ".");
                return;
            }
            mostrarMensaje("Datos de la consulta:\n" + c);
            if (confirmar("¿Confirma cancelar esta consulta?")) {
                fachada.cancelarConsulta(id);
                mostrarMensaje("Consulta cancelada correctamente.");
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblImagenlol = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtIdPac = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbPacientes = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btnListPac = new javax.swing.JButton();
        checkDirPac = new javax.swing.JCheckBox();
        checkEdadPac = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtIdDoc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cmbMedicos = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        txtEspID = new javax.swing.JTextField();
        btnAgregarEsp = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtIdEM = new javax.swing.JTextField();
        btnInventario = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtIdEM2 = new javax.swing.JTextField();
        btnInventariar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtIdEM3 = new javax.swing.JTextField();
        BtnAgregarEM = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        txtIdConsultar = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        btnConsultar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnAgendarConsulta = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtbusccon = new javax.swing.JTextField();
        btnbuscarcon = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblbusccon = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtbuscpac = new javax.swing.JTextField();
        btnbuscpac = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblbuscpac = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel1.setText("Bienvenida a nuestro Sistema de Gestión Hospitalaria :D");

        jLabel3.setText("Use los menús para gestionar pacientes, médicos, equipos y consultas. ");

        jLabel4.setText("Los datos se guardan automáticamente en la carpeta 'datos'.");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblImagenlol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblImagenlol, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        jTabbedPane1.addTab("Inicio", jScrollPane1);

        jLabel5.setText("Para comenzar con el proceso, ingresa en el campo de texto el ID del paciente:");

        txtIdPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdPacActionPerformed(evt);
            }
        });

        jLabel6.setText("Selecciona el proceso que deseas realizar:");

        cmbPacientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Agregar paciente", "Consultar paciente", "Actualizar paciente", "Eliminar paciente" }));
        cmbPacientes.setName("Seleccione una opción"); // NOI18N
        cmbPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPacientesActionPerformed(evt);
            }
        });

        jLabel7.setText("Si desea ver el listado de pacientes, puede aplicar un filtro o solo dar clic en el botón:");

        btnListPac.setText("Ver listado de pacientes");
        btnListPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListPacActionPerformed(evt);
            }
        });

        checkDirPac.setText("Filtrar por dirección");
        checkDirPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDirPacActionPerformed(evt);
            }
        });

        checkEdadPac.setText("Filtrar por edad");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnListPac)
                    .addComponent(checkEdadPac)
                    .addComponent(jLabel7)
                    .addComponent(txtIdPac, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cmbPacientes, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkDirPac))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkDirPac)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkEdadPac)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnListPac)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pacientes", jPanel1);

        jLabel8.setText("Puede agregar o consultar a un médico con su ID:");

        jLabel9.setText("Seleccione el proceso que desea realizar:");

        cmbMedicos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Agregar médico", "Consultar médico" }));
        cmbMedicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMedicosActionPerformed(evt);
            }
        });

        jLabel19.setText("Para añadir una especialidad ingrese su ID:");

        btnAgregarEsp.setText("Aceptar");
        btnAgregarEsp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarEspActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(txtIdDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMedicos, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEspID, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarEsp, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMedicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEspID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarEsp)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Médicos", jPanel2);

        jLabel10.setText("Para consultar el inventario, usted puede ingresar el ID de un equipo específico,");

        jLabel11.setText("o bien, puede consultar el inventario completo:");

        jLabel12.setText("ID:");

        btnInventario.setText("Ver inventario");
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });

        jLabel13.setText("Si desea inventariar o desinventariar, ingrese el ID del equipo médico en cuestión:");

        txtIdEM2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEM2ActionPerformed(evt);
            }
        });

        btnInventariar.setText("Inventariar");
        btnInventariar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventariarActionPerformed(evt);
            }
        });

        jButton1.setText("Desinventariar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtIdEM3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEM3ActionPerformed(evt);
            }
        });

        BtnAgregarEM.setText("Agregar equipo medico");
        BtnAgregarEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAgregarEMActionPerformed(evt);
            }
        });

        jLabel17.setText("ID:");

        jLabel18.setText("Para agregar nuevo equipo medico, ingrese el ID:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdEM, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13)
                    .addComponent(txtIdEM2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtIdEM3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnAgregarEM))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(btnInventariar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(67, 67, 67)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdEM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(btnInventario))
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdEM2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInventariar)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtIdEM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BtnAgregarEM)))
                .addGap(0, 26, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inventario", jPanel4);

        jLabel14.setText("Si desea consultar una consulta médica, debe elegir por qué dato lo hará:");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Por ID del paciente");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Por ID del médico");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Por fecha");

        jLabel15.setText("ID:");

        btnConsultar.setText("Aceptar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        jLabel16.setText("En caso de querer agendar una nueva consulta médica, de clic en el botón:");

        btnAgendarConsulta.setText("Agendar nueva consulta médica");
        btnAgendarConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendarConsultaActionPerformed(evt);
            }
        });

        jLabel2.setText("En caso de querer cancelar una consulta médica, de clic:");

        jButton2.setText("Cancelar consulta médica");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jLabel2)
                    .addComponent(btnAgendarConsulta)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton3))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel15)
                    .addComponent(txtIdConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton3))
                    .addComponent(btnConsultar))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgendarConsulta)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Consultas", jPanel5);

        jLabel22.setText("BUSCAR CONSULTA");

        jLabel23.setText("ingrese la fecha de consulta formato DD/MM/ AAAA");

        btnbuscarcon.setText("BUSCAR");
        btnbuscarcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbuscarconActionPerformed(evt);
            }
        });

        tblbusccon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "PACIENTE", "MEDICO", "FECHA", "ESTADO"
            }
        ));
        jScrollPane3.setViewportView(tblbusccon);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(txtbusccon, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(193, 193, 193)
                        .addComponent(btnbuscarcon)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtbusccon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnbuscarcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Consultas", jPanel6);

        jLabel20.setText("BUSCAR PACIENTES");

        jLabel21.setText("Ingrese el nombre del paciente. Ejemplo: Mar | mario");

        btnbuscpac.setText("BUSCAR");
        btnbuscpac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbuscpacActionPerformed(evt);
            }
        });

        tblbuscpac.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "NOMBRE", "EDAD", "DIRECCION"
            }
        ));
        jScrollPane2.setViewportView(tblbuscpac);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 22, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(164, 164, 164))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(txtbuscpac, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(btnbuscpac)
                        .addGap(189, 189, 189))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtbuscpac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnbuscpac)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Pacientes", jPanel7);

        jTabbedPane1.addTab("Buscar", jTabbedPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 473, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdPacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdPacActionPerformed

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        consultarInventarioGUI();
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void cmbPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPacientesActionPerformed
        int opc = cmbPacientes.getSelectedIndex();
        switch (opc) {
            case 0 ->
                agregarPacienteGUI();
            case 1 ->
                consultarPacienteGUI();
            case 2 ->
                actualizarPacienteGUI();
            default ->
                eliminarPacienteGUI();
        }
    }//GEN-LAST:event_cmbPacientesActionPerformed

    private void btnListPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListPacActionPerformed
        listarPacientesGUI();
    }//GEN-LAST:event_btnListPacActionPerformed

    private void checkDirPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDirPacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkDirPacActionPerformed

    private void cmbMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMedicosActionPerformed
        int opc = cmbMedicos.getSelectedIndex();
        if (opc == 0) {
            agregarMedicoGUI();
        } else {
            consultarMedicoGUI();
        }
    }//GEN-LAST:event_cmbMedicosActionPerformed

    private void txtIdEM2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEM2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEM2ActionPerformed

    private void btnInventariarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventariarActionPerformed
        inventariarEquipoGUI();
    }//GEN-LAST:event_btnInventariarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        desinventariarEquipoGUI();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        if (jRadioButton1.isSelected()){
            consultasPorPacienteGUI();
        } else if (jRadioButton2.isSelected()) {
            consultasPorMedicoGUI();
        } else if (jRadioButton3.isSelected()) {
            buscarConsultasPorFechaGUI();
        } else {
            mostrarConsultas(null, null);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnAgendarConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendarConsultaActionPerformed
        agendarConsultaGUI();
    }//GEN-LAST:event_btnAgendarConsultaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cancelarConsultaGUI();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtIdEM3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEM3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEM3ActionPerformed

    private void BtnAgregarEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAgregarEMActionPerformed
        agregarEquipoGUI();
    }//GEN-LAST:event_BtnAgregarEMActionPerformed

    private void btnAgregarEspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarEspActionPerformed
        agregarEspecialidadGUI();
    }//GEN-LAST:event_btnAgregarEspActionPerformed

    private void btnbuscpacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbuscpacActionPerformed
       Buscarpornombrepac();
    }//GEN-LAST:event_btnbuscpacActionPerformed

    private void btnbuscarconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbuscarconActionPerformed
       buscarConsultasPorFecha();
    }//GEN-LAST:event_btnbuscarconActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnAgregarEM;
    private javax.swing.JButton btnAgendarConsulta;
    private javax.swing.JButton btnAgregarEsp;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnInventariar;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnListPac;
    private javax.swing.JButton btnbuscarcon;
    private javax.swing.JButton btnbuscpac;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkDirPac;
    private javax.swing.JCheckBox checkEdadPac;
    private javax.swing.JComboBox<String> cmbMedicos;
    private javax.swing.JComboBox<String> cmbPacientes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblImagenlol;
    private javax.swing.JTable tblbusccon;
    private javax.swing.JTable tblbuscpac;
    private javax.swing.JTextField txtEspID;
    private javax.swing.JTextField txtIdConsultar;
    private javax.swing.JTextField txtIdDoc;
    private javax.swing.JTextField txtIdEM;
    private javax.swing.JTextField txtIdEM2;
    private javax.swing.JTextField txtIdEM3;
    private javax.swing.JTextField txtIdPac;
    private javax.swing.JTextField txtbusccon;
    private javax.swing.JTextField txtbuscpac;
    // End of variables declaration//GEN-END:variables
}
