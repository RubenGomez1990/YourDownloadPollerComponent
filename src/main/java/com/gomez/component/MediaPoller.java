package com.gomez.component;

import com.gomez.service.ApiClient;
import java.awt.BorderLayout;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author LionKeriot
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
        this.timer = new Timer(pollingInterval * 1000, null);
    }
}
