package wba.citta.gui;

import java.util.EventListener;

public interface ViewerPanelEventListener extends EventListener {
    public void panelCreated(ViewerPanelEvent evt);
    
    public void panelPopulated(ViewerPanelEvent evt);
}
