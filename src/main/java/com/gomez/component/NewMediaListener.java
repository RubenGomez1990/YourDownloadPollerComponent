package com.gomez.component;

import java.util.EventListener;

/**
 *
 * @author Rubén Gómez Hernández
 */
public interface NewMediaListener extends EventListener{
    void onNewMediaDetected(NewMediaEvent evt);
}
