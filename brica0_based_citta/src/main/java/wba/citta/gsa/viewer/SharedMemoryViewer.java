/**
 * SharedMemoryViewer.java
 *  ゴールスタックの状態をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import wba.citta.gsa.IListenableSharedMemory;
import wba.citta.gsa.SharedMemoryEvent;
import wba.citta.gsa.SharedMemoryEventListener;
import wba.citta.gui.ViewerPanel;

/**
 *  ゴールスタックの状態をグラフィック表示するクラス
 */
public class SharedMemoryViewer extends JPanel implements SharedMemoryEventListener, ViewerPanel {
    private static final long serialVersionUID = 1L;
    private static final Set<String> roles = Collections.singleton("info");
    private IListenableSharedMemory currentSharedMemory = null;
    private JScrollPane scrollPane = null;
    private SharedMemoryViewerCanvas canvas = null;

    /**
     * コンストラクタ
     * @param Integer[] stateArray 共有メモリの現在の状態への参照
     * @param LinkedList[] goalStackArray 共有メモリのゴールスタックへの参照
     */
    public SharedMemoryViewer(Map<Integer, Color> colorTable) {
        super(new BorderLayout());
        canvas = new SharedMemoryViewerCanvas(colorTable);
        scrollPane = new JScrollPane(
            canvas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        add(scrollPane, BorderLayout.CENTER);
    }

    public synchronized void bind(IListenableSharedMemory memory) {
        if (currentSharedMemory != null) {
            currentSharedMemory.removeChangeListener(this);
        }
        memory.addChangeListener(this);
        currentSharedMemory = memory;
    }

    @Override
    public void sharedMemoryChanged(SharedMemoryEvent evt) {
        canvas.snapshot(evt.getSource());
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Goal Stack Viewer";
    }

    @Override
    public Set<String> getViewerPanelRoles() {
        return roles;
    }
}


