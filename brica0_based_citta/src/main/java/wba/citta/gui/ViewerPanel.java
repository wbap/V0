package wba.citta.gui;

import java.util.Set;
import javax.swing.JComponent;

public interface ViewerPanel {
    public Set<String> getViewerPanelRoles();
    
    public JComponent getComponent();

    public String getPreferredTitle();
}
