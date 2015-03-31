/**
 * TreeViewer.java
 *  ツリーの状態をグラフィックス表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import wba.citta.gsa.*;
import wba.citta.gui.ViewerPanel;

/**
 *  ツリーの状態をグラフィックス表示するクラス
 */
public class TreeViewer extends JPanel implements ViewerPanel, FailAgentTreeEventListener {
    private static final long serialVersionUID = 1L;
    private static final Set<String> roles = Collections.singleton("info");
    private JScrollPane scrollPane = null;
    private TreeViewerCanvas canvas = null;

    /**
     * コンストラクタ
     * @param FailAgentTreeElement rootElement ツリーのルートへの参照
     */
    public TreeViewer(Map<Integer, Color> colorTable) {
        super(new BorderLayout());
        canvas = new TreeViewerCanvas(colorTable);
        scrollPane = new JScrollPane(
            canvas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        add(scrollPane, BorderLayout.CENTER);
    }

    public void bind(FailAgentTree failAgentTree) {
        canvas.setCurrentElement(failAgentTree.getRootElement());
        failAgentTree.addFailAgentTreeEventListener(this);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Fail Agent Tree Viewer";
    }

    @Override
    public void treeChanged(FailAgentTreeEvent evt) {
        canvas.setCurrentElement(evt.getSource().getRootElement());
        canvas.repaint();
    }

    @Override
    public Set<String> getViewerPanelRoles() {
        return roles;
    }
}


