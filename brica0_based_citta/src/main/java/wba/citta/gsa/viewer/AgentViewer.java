/**
\ * AgentViewer.java
 *  エージェントの動作状況をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import wba.citta.gsa.*;
import wba.citta.gui.ViewerPanel;

/**
 *  エージェントの動作状況をグラフィック表示するクラス
 */
public class AgentViewer extends JPanel implements GSAAgentEventListener, ViewerPanel {
    private static final long serialVersionUID = 1L;
    private static final Set<String> roles = Collections.singleton("side");
    private GSA currentGSA;
    private JScrollPane scrollPane = null;
    private AgentViewerCanvas canvas = null;

    /**
     * コンストラクタ
     */
    public AgentViewer(Map<Integer, Color> colorTable, Color defaultColor) {
        super(new BorderLayout());
        canvas = new AgentViewerCanvas(colorTable, defaultColor, Color.BLUE);
        scrollPane = new JScrollPane(
            canvas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        add(scrollPane, BorderLayout.CENTER);
    }

    public synchronized void bind(GSA gsa) {
        if (currentGSA != null) {
            currentGSA.removeAgentEventListener(this); 
        }
        canvas.beginBatchUpdate();
        for (Agent agent: gsa.getAgents()) {
            canvas.addAgent(agent);
        }
        canvas.endBatchUpdate();
        currentGSA = gsa;
        gsa.addAgentEventListener(this);
        revalidate();
    }

    public void agentBeingExecuted(GSAAgentEvent evt) {
        canvas.setExecAgent(evt.getAgent());
        canvas.repaint();
    }

    @Override
    public void agentExecuted(GSAAgentEvent evt) {
        canvas.setExecAgent(null);        
        canvas.repaint();
    }

    public void agentRemoved(GSAAgentEvent evt) {
        canvas.markRemoved(evt.getAgent());    
        canvas.repaint();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Agent Viewer";
    }

    public Set<String> getViewerPanelRoles() {
        return roles;
    }
}


