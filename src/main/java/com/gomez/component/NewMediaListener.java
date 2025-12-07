package com.gomez.component;

import com.gomez.model.Media;
import com.gomez.service.ApiClient;
import java.util.EventListener;
import java.util.List;

/**
 *
 * @author Rubén Gómez Hernández
 */
public interface NewMediaListener extends EventListener{
    void onNewMediaDetected(NewMediaEvent evt);
}
