package sistemahospital;
import fachada.IPersistenciaFachada;
import fachada.PersistenciaFachada;
import gui.ventana;

public class SistemaHospital {

    public static void main(String[] args) {
        IPersistenciaFachada persistencia = new PersistenciaFachada();
        
        MenuPrincipal menu= new MenuPrincipal(persistencia);
        menu.mostrar();
        ventana ven = new ventana();
        ven.setVisible(true);
    }
}

