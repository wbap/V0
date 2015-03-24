/**
 * AgentViewerCanvas.java
 *  エージェントの動作状況を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import wba.citta.gsa.*;

/**
 *  エージェントの動作状況を描画するクラス
 */
public class AgentViewerCanvas extends JPanel {
    private static final long serialVersionUID = 1L;

    static class AgentDescriptor {
        IGSAAgent agent;
        Color color;
        boolean isExecAgent;
        boolean isRemoved;

        AgentDescriptor(IGSAAgent agent, Color color) {
            this.agent = agent;
            this.color = color;
        }
    }

    /* エージェントIDの配列 */
    private List<AgentDescriptor> agents;

    private Map<IGSAAgent, AgentDescriptor> agentMap;
    
    private IGSAAgent currentExecAgent;

    /* 要素間の間隔 */
    private final int X_SPACE = 20;
    private final int Y_SPACE = 20;
    /* 要素のサイズ */
    private final int X_ELEMENT_SIZE = 30;
    private final int Y_ELEMENT_SIZE = 30;

    private Image offImage;

    private boolean dirty = false;

    private Map<Integer, Color> colorTable;
    private Font defaultFont = new Font("Dialog", Font.BOLD, 12);
    private Color defaultColor;
    private Color execAgentColor;
    
    private boolean batchUpdate;
    
    ////////////////////////////////////////////////////////////
    // コンストラクタ  初期化処理

    /**
     * コンストラクタ
     */
    public AgentViewerCanvas(Map<Integer, Color> colorTable, Color defaultColor, Color execAgentColor) {
        super();
        agents = new ArrayList<AgentDescriptor>();
        agentMap = new HashMap<IGSAAgent, AgentDescriptor>();
        this.colorTable = colorTable;
        this.defaultColor = defaultColor;
        this.execAgentColor = execAgentColor;
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                markDirty();
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                dirty = true;
                updateIfNecessary();
                repaint();
            }}
        );
    }

    ////////////////////////////////////////////////////////////
    // public 

    public void beginBatchUpdate() {
        batchUpdate = true;
    }

    public void endBatchUpdate() {
        batchUpdate = false;
        updateIfNecessary();
    }

    protected void markDirty() {
        dirty = true;
        if (!batchUpdate) {
            updateIfNecessary();
        }
    }

    /**
     * 実行エージェントを設定します。
     * @param AbstractGSAAgent execAgent 実行エージェント
     */
    public void setExecAgent(IGSAAgent execAgent) {
        if (currentExecAgent != null) {
            final AgentDescriptor desc = agentMap.get(currentExecAgent);
            assert desc != null;
            desc.isExecAgent = false;
            currentExecAgent = null;
        }
        if (execAgent != null) {
            AgentDescriptor desc = agentMap.get(execAgent);
            assert desc != null;
            desc.isExecAgent = true;
        }
        currentExecAgent = execAgent;
        markDirty();
    }
    

    /**
     * 削除済みエージェントを設定します。
     * @param AbstractGSAAgent agent 実行エージェント
     */
    public void markRemoved(IGSAAgent agent) {
        AgentDescriptor desc = agentMap.get(agent);
        assert desc != null;
        desc.isRemoved = true;
        markDirty();
    }

    public boolean addAgent(IGSAAgent agent) {
        if (agentMap.get(agent) != null)
            return false;
        Color color = colorTable.get(agent.getId());
        if (color == null)
            color = defaultColor;
        final AgentDescriptor desc = new AgentDescriptor(agent, color);
        agents.add(desc);
        agentMap.put(agent, desc);
        markDirty();
        return true;
    }

    public boolean removeAgent(IGSAAgent agent) {
        AgentDescriptor desc = agentMap.get(agent);
        if (desc == null)
            return false;
        agents.remove(desc);
        agentMap.remove(agent);
        markDirty();
        return true;
    }

    private void calculatePreferredSize() {
        final Dimension size = getParent().getSize();
        int drawNum = agents.size();
        final int hCount = Math.max((size.width - X_SPACE) / (X_ELEMENT_SIZE + X_SPACE), 1);
        final int vCount = (drawNum + hCount - 1) / hCount;
        setPreferredSize(new Dimension(
            X_SPACE + (X_ELEMENT_SIZE + X_SPACE) * hCount,
            Y_SPACE + (Y_ELEMENT_SIZE + Y_SPACE) * vCount
        ));
    }

    private void updateIfNecessary() {
        if (!dirty || !isVisible())
            return;
        calculatePreferredSize();
        /* 描画するエリアのサイズを取得 */
        final Dimension size = getPreferredSize();
        setSize(size);
        if (offImage == null || size.width != offImage.getWidth(this) || size.height != offImage.getHeight(this)) {
            offImage = createImage(size.width, size.height);
        }
        if (offImage != null)
            drawOffscreen();
        dirty = false;
    }

    private void drawOffscreen() {
        assert offImage != null;
        final Graphics2D graphics = (Graphics2D)offImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        /* オフスクリーンへの描画 */
        try {
            clearOffImage(graphics);
            drawAgents(graphics);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * paintメソッドのオーバーライド
     * @param Graphics g
     */
    public void paint(Graphics g) {
        /* オフスクリーンイメージを描画 */
        final Rectangle r = g.getClipBounds();
        g.clearRect(r.x, r.y, r.width, r.height);
        if (offImage != null) {
            g.drawImage(offImage, 0, 0, this);
        }
    }

    ////////////////////////////////////////////////////////////
    // private

    private void clearOffImage(Graphics graphics) {
        graphics.setColor(getBackground());
        graphics.fillRect(0, 0, offImage.getWidth(this), offImage.getHeight(this));
    }

    /**
     * エージェントの動作状況の描画を行ないます。
     * @param Graphics g
     */
    private void drawAgents(Graphics2D graphics) {
        for (int i = 0; i < agents.size(); i++) {
            drawAgent(graphics, agents.get(i), i);
        }
    }

    /**
     * 指定されたエージェントを描画します。
     * @param Graphics g
     * @param int index 描画するエージェントの配列上の位置
     */
    private void drawAgent(Graphics2D graphics, AgentDescriptor desc, int index) {
        /* 描画する情報の取得 */
        Rectangle rectInfo = getAgentRectSize(desc, index);
        String agid = " " + desc.agent.getId();

        /* 描画処理 */
        if (desc.color != null) {
            graphics.setColor(desc.color);
            graphics.fillRect(rectInfo.x, rectInfo.y, rectInfo.width, rectInfo.height);
        }

        /* 実行エージェンの場合青枠で囲む */
        if (desc.isExecAgent) {
            graphics.setColor(execAgentColor);
            for (int i = 0; i < 5; i++) {
                graphics.drawRect(rectInfo.x + i, rectInfo.y + i,
                        rectInfo.width - 2 * i, rectInfo.height - 2 * i);
            }
        }

        /* 到達ゴール削除エージェントの場合の灰枠で囲む */
        if (desc.isRemoved == true) {
            graphics.setColor(Color.GRAY);
            for (int i = 0; i < 5; i++) {
                graphics.drawRect(rectInfo.x + i, rectInfo.y + i,
                        rectInfo.width - 2 * i, rectInfo.height - 2 * i);
            }
        }

        graphics.setColor(Color.black);
        graphics.drawRect(rectInfo.x, rectInfo.y, rectInfo.width, rectInfo.height);

        graphics.setFont(defaultFont);
        Rectangle2D bBox = graphics.getFontMetrics().getStringBounds(agid, graphics);
        graphics.drawString(agid, (int)(rectInfo.x + rectInfo.width / 2 - bBox.getWidth() / 2), (int)(rectInfo.y + rectInfo.height / 2 + bBox.getHeight() / 2));
    }

    /**
     * 指定された位置に描画する矩形の情報を取得します。
     * @param int index 配列上の位置
     * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
     */
    private Rectangle getAgentRectSize(AgentDescriptor desc, int index) {
        final Dimension size = getSize();
        final int hCount = Math.max((size.width - X_SPACE) / (X_ELEMENT_SIZE + X_SPACE), 1);
        final int x = index % hCount, y = index / hCount;
        return new Rectangle(
            X_SPACE + x * (X_ELEMENT_SIZE + X_SPACE),
            Y_SPACE + y * (Y_ELEMENT_SIZE + Y_SPACE),
            X_ELEMENT_SIZE,
            Y_ELEMENT_SIZE
        );
    }
}


