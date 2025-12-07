package com.gomez.component;

import com.gomez.model.Media;
import com.gomez.service.ApiClient;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.time.OffsetDateTime;
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
    
    public MediaPoller(){
        this.setLayout(new BorderLayout());
        this.label = new JLabel("Etiqueta");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(this.label, BorderLayout.CENTER);
        this.timer = new Timer(this.pollingInterval * 1000, new ActionListener(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                performPoll();
            }
        });
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
            List<Media> newFiles = this.apiClient.getMediaAddedSince(this.lastChecked, token);
            this.lastChecked = OffsetDateTime.now().toString();
            if (newFiles != null && !newFiles.isEmpty()){
               fireNewMediaEvent(newFiles);
            }
        } catch (Exception e) {
            System.err.print("No se ha podido cargar los archivos." + e.getMessage());
        }
    } 
    
    //Añadir un listener a la lista
    public void addNewMediaListener(NewMediaListener listener) {
        listeners.add(listener);
    }
    
    //Quitar un listener de la lista.
    public void removeNewMediaListener(NewMediaListener listener){
        listeners.remove(listener);
    }
    
    private void fireNewMediaEvent(List<Media> newFiles){
        //1. convertir fecha desde el String para poder usar un tipo de dato válido
        OffsetDateTime lastDate = OffsetDateTime.parse(lastChecked);
        
        //2. Creamos el evento que se enviará.
        NewMediaEvent event = new NewMediaEvent(this, newFiles, lastDate);
        
        //3. Recorrer la lista con los listeners
        for (NewMediaListener  listener : listeners){
            listener.onNewMediaDetected(event);
        }
    }
    
    
    // WRAPPED METHODS
    public String login(String email, String password) throws Exception {
        if (this.apiClient == null){
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.login(email, password);
    }
    
    public String getNickname(int userId) throws Exception {
        if (this.apiClient == null){
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.getNickName(userId, this.token);
    }
    
    public List<Media> getAllMedia() throws Exception {
         if (this.apiClient == null){
            throw new IllegalStateException("API URL no se ha configurado.");
        }
         return this.apiClient.getAllMedia(token);
    }
    
    public void download(int id, java.io.File destination) throws Exception{
        if (this.apiClient == null){
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        this.apiClient.download(id, destination, this.token);
    }
    
    public String uploadFileMultipart (java.io.File file, String downloadedFromUrl) throws Exception{
        if (this.apiClient == null){
            throw new IllegalStateException("API URL no se ha configurado.");
        }
        return this.apiClient.uploadFileMultipart(file, downloadedFromUrl, token);
    }
 }
