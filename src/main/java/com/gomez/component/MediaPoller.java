package com.gomez.component;

import com.gomez.model.Media;
import com.gomez.service.ApiClient;
import java.awt.BorderLayout;
import java.awt.Color; // Importamos Color
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author Rubén Gómez Hernández
 */
public class MediaPoller extends JPanel implements Serializable {

    private String apiUrl;
    private boolean running;
    private int pollingInterval = 10;
    private String token;
    private String lastChecked;
    private ApiClient apiClient;
    private Timer timer;
    private JLabel label;
    // Creamos una lista que recibirá todos los listeners desde la clase NewMediaListener
    private final List<NewMediaListener> listeners = new ArrayList<>();

    public MediaPoller() {
        this.lastChecked = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).toString();
        this.setLayout(new BorderLayout());
        this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        this.label = new JLabel("Polling...");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(this.label, BorderLayout.CENTER);

        this.timer = new Timer(this.pollingInterval * 1000, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                performPoll();
            }
        });
    }

    public void setRunning(boolean running) {
        this.running = running; // Primero actualizamos la variable de estado

        // Implementación de la lógica de colores y texto
        if (running) {
            this.timer.start();
            this.setBackground(Color.GREEN);
            this.label.setForeground(Color.BLACK);
            this.label.setText("Polling: ACTIVE");
        } else {
            this.timer.stop();
            this.setBackground(Color.RED);
            this.label.setForeground(Color.WHITE);
            this.label.setText("Polling: STOPPED");
        }

        this.setOpaque(true);
        this.repaint();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            this.apiClient = new ApiClient(apiUrl);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
        this.timer.setDelay(this.pollingInterval * 1000);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(String lastChecked) {
        this.lastChecked = lastChecked;
    }

    // MÉTODOS
    private void performPoll() {
        if (this.token == null || this.token.isEmpty()) {
            return;
        }

        // 1. Log del Token simplificado (sin el chorizo de caracteres)
        System.out.println("Token sended.");

        try {
            // 2. Preparamos el formato de fecha y hora (yyyy-MM-dd HH:mm:ss)
            java.time.format.DateTimeFormatter customFormatter
                    = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 3. Convertimos la fecha actual de lastChecked para el log
            String readableDate = java.time.OffsetDateTime.parse(this.lastChecked)
                    .format(customFormatter);

            // 4. Imprimimos el log con el formato unificado que pediste
            System.out.println("Poller: checking for new archives from API since: " + readableDate);

            // Llamada a la API
            List<com.gomez.model.Media> newFiles = this.apiClient.getMediaAddedSince(this.lastChecked, token);

            int count = (newFiles != null) ? newFiles.size() : 0;

            // Solo imprimimos si hay éxito y cuántos hay, para no saturar la consola si hay 0
            if (count > 0) {
                System.out.println("Poller: Successful query. Detected [" + count + "] new files.");
                fireNewMediaEvent(newFiles);
            }

            // 5. Actualizamos el tiempo con el margen de seguridad (30 seg para Real-Time)
            this.lastChecked = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                    .minusSeconds(30)
                    .toString();

        } catch (Exception e) {
            System.err.println("Poller: API Error -> " + e.getMessage());
        }
    }

    //Añadir un listener a la lista
    public void addNewMediaListener(NewMediaListener listener) {
        listeners.add(listener);
    }

    //Quitar un listener de la lista.
    public void removeNewMediaListener(NewMediaListener listener) {
        listeners.remove(listener);
    }

    private void fireNewMediaEvent(List<Media> newFiles) {
        //1. convertir fecha desde el String para poder usar un tipo de dato válido
        OffsetDateTime lastDate = OffsetDateTime.parse(lastChecked);

        //2. Creamos el evento que se enviará.
        NewMediaEvent event = new NewMediaEvent(this, newFiles, lastDate);

        //3. Recorrer la lista con los listeners
        for (NewMediaListener listener : listeners) {
            listener.onNewMediaDetected(event);
        }
    }

    // WRAPPED METHODS
    public String login(String email, String password) throws Exception {
        if (this.apiClient == null) {
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.login(email, password);
    }

    public String getNickname(int userId) throws Exception {
        if (this.apiClient == null) {
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.getNickName(userId, this.token);
    }

    public List<Media> getAllMedia() throws Exception {
        if (this.apiClient == null) {
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.getAllMedia(token);
    }

    public void download(int id, java.io.File destination) throws Exception {
        if (this.apiClient == null) {
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        this.apiClient.download(id, destination, this.token);
    }

    public String uploadFileMultipart(java.io.File file, String downloadedFromUrl) throws Exception {
        if (this.apiClient == null) {
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.uploadFileMultipart(file, downloadedFromUrl, token);
    }
}
