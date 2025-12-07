package com.gomez.test; // Ajusta el paquete si es necesario

import com.gomez.component.MediaPoller;
import com.gomez.component.NewMediaEvent;
import com.gomez.component.NewMediaListener;
import com.gomez.model.Media;
import java.awt.BorderLayout;
import java.time.OffsetDateTime;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TestFrame extends JFrame {

    private MediaPoller poller;
    private JTextArea logArea;

    public TestFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Banco de Pruebas - MediaPoller");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Instanciar el componente
        poller = new MediaPoller();
        
        // 2. Configurar propiedades básicas
        poller.setApiUrl("https://dimedianetapi9.azurewebsites.net"); 
        poller.setPollingInterval(5); // Chequear cada 5 segundos
        
        // 3. Añadir el Listener (Escuchar al vigilante)
        poller.addNewMediaListener(new NewMediaListener() {
            @Override
            public void onNewMediaDetected(NewMediaEvent evt) {
                log("¡EVENTO RECIBIDO! Archivos nuevos detectados a las " + evt.getFecha());
                for (Media m : evt.getNewFiles()) {
                    log(" - Archivo: " + m.mediaFileName + " (ID: " + m.id + ")");
                }
            }
        });

        // Área de texto para ver qué pasa (Log)
        logArea = new JTextArea();
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        // Añadir el poller al Norte (se verá el icono/texto)
        add(poller, BorderLayout.NORTH);
        
        // Iniciar la prueba
        startTest();
    }
    
    private void startTest() {
        // Ejecutamos en un hilo separado para no bloquear la interfaz gráfica
        new Thread(() -> {
            try {
                log("Iniciando prueba de Login...");
                
                String YOUR_EMAIL = "rubengomez@paucasesnovescifp.cat"; 
                String YOUR_PASSWORD = "postman"; 
                
                String token = poller.login(YOUR_EMAIL, YOUR_PASSWORD);
                
                log("Login correcto. Token recibido: " + token.substring(0, 10) + "...");
                
                // Configuramos el token y la fecha de inicio
                poller.setToken(token);
                
                // Retrocedemos la fecha para forzar que el vigilante encuentre archivos
                poller.setLastChecked(OffsetDateTime.now().toString());
                
                log("Arrancando vigilancia (Running = true)...");
                poller.setRunning(true);
                
            } catch (Exception e) {
                log("ERROR CRÍTICO DURANTE EL ARRANQUE: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void log(String text) {
        SwingUtilities.invokeLater(() -> logArea.append(text + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestFrame().setVisible(true));
    }
}