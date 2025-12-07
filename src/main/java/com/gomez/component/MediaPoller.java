package com.gomez.component;

import com.gomez.model.Media;
import com.gomez.service.ApiClient;
import java.awt.BorderLayout;
import java.io.Serializable;
import java.time.OffsetDateTime;
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
    private int pollingInterval;
    private String token;
    private String lastChecked;
    private ApiClient apiClient;
    private Timer timer;
    private JLabel label;
    
    public MediaPoller(){
        this.setLayout(new BorderLayout());
        this.label = new JLabel("Etiqueta");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(this.label, BorderLayout.CENTER);
        this.timer = new Timer(pollingInterval, null);
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        if (apiUrl != null && !apiUrl.isEmpty()){
            this.apiClient = new ApiClient(apiUrl);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (running){
            timer.start();
        } else {
            timer.stop();
        }
        this.running = running;
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
    private void performPoll(){
        try {
            List<Media> newFiles = this.apiClient.getMediaAddedSince(OffsetDateTime.MIN, token);
        } catch (Exception e) {
            
        }
    } 
}
