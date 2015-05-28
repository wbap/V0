package wba.citta.gui;

import java.util.EventObject;


public class ViewerPanelEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    ViewerPanel panel;

    public ViewerPanelEvent(Object source, ViewerPanel panel) {
        super(source);
        this.panel = panel;
    }

    public ViewerPanelEvent(Object source) {
        super(source);
    }

    public ViewerPanel getViewerPanel() {
        return panel;
    }
}
