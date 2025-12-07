package com.gomez.component;

import com.gomez.model.Media;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 *
 * @author Rubén Gómez Hernández
 */
public class NewMediaEvent extends EventObject {
    private List<Media> newFiles = new ArrayList<>();
    private OffsetDateTime fecha;
    
    public NewMediaEvent(Object source, List<Media> newFiles, OffsetDateTime fecha) {
        super(source);
        this.newFiles = newFiles;
        this.fecha = fecha; 
    }

    public List<Media> getNewFiles() {
        return newFiles;
    }

    public void setNewFiles(List<Media> newFiles) {
        this.newFiles = newFiles;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    } 
}
