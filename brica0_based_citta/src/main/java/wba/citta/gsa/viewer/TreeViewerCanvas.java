/**
 * TreeViewerCanvas.java
 *  ツリーの状態を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.*;

import javax.swing.JPanel;

import wba.citta.gsa.*;

/**
 *  ツリーの状態を描画するクラス
 */
public class TreeViewerCanvas extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** カラーテーブル */
    private Map<Integer, Color> colorTable;
    
    /** ツリーのルート */
    private FailAgentTreeElement rootElement = null;
    /** ツリーのカレント */
    private FailAgentTreeElement currentElement = null;

    /** 描画する要素の間隔 */
    private final int X_SPACE = 30;
    private final int Y_SPACE = 20;
    /** 描画する要素のサイズ */
    private final int X_ELEMENT_SIZE = 30;
    private final int Y_ELEMENT_SIZE = 30;

    /** ダブルバッファリング用 オフスクリーンイメージ */
    private Image offImage;

    /** 文字のフォント */
    private Font agidFont = new Font("Dialog", Font.BOLD, 12);
    /** 文字のフォント */
    private Font agrFont = new Font("Dialog", Font.PLAIN , 12);

    /** y方向の描画位置 */
    private int yPos = 0;

    private boolean dirty;
    
    private Dimension preferredSize;

    ////////////////////////////////////////////////////////////
    // コンストラクタ

    /**
     * コンストラクタ
     * @param FailAgentTreeElement rootElement ツリーのルート
     */
    public TreeViewerCanvas(Map<Integer, Color> colorTable) {
        super();
        this.colorTable = colorTable;
        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {}

            @Override
            public void componentMoved(ComponentEvent arg0) {}

            @Override
            public void componentResized(ComponentEvent arg0) {}

            @Override
            public void componentShown(ComponentEvent arg0) {
                dirty = true;
                updateIfNecessary();
                repaint();
            }        
        });
    }

    ////////////////////////////////////////////////////////////
    // public 

    /**
     * ツリーのカレントを設定します。
     * @param FailAgentTreeElement currentElement ツリーのカレント
     */
    public void setCurrentElement(FailAgentTreeElement currentElement) {
        if (rootElement == null)
            this.rootElement = currentElement;
        this.currentElement = currentElement;
        dirty = true;
        updateIfNecessary();
    }

    private void calculatePreferredSize() {
        final int[] drawNum = new int[] { 0, 0 };
        childSize(rootElement, drawNum);
        
        // 要素数をサイズに変換
        preferredSize = new Dimension(
            (drawNum[0] * (X_SPACE + X_ELEMENT_SIZE)) + X_SPACE,
            (drawNum[1] * (Y_SPACE + Y_ELEMENT_SIZE)) + Y_SPACE
        );
    }

    private void updateIfNecessary() {
        if (!dirty || !isVisible())
            return;
        calculatePreferredSize();
        setSize(preferredSize);
        if (offImage == null || preferredSize.width != offImage.getWidth(this) || preferredSize.height != offImage.getHeight(this)) {
            /* オフスクリーンイメージの作成 */
            offImage = createImage(preferredSize.width, preferredSize.height);
        }
        /* オフスクリーンへの描画 */
        if (offImage != null)
            drawOffImage();
        dirty = false;
    }
    /**
     * paintメソッドのオーバーライド
     * @param Graphics g
     */
    public void paint(Graphics g) {
        if (offImage != null) {
            /* オフスクリーンイメージを描画 */
            g.drawImage(offImage, 0, 0, this);
        }
    }

    ////////////////////////////////////////////////////////////
    // private

    /**
     * オフスクリーンへの描画
     * @param Graphics graphics
     */
    private void drawOffImage() {
        Graphics2D graphics = (Graphics2D)offImage.getGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            /* イメージのクリア */
            clearOffImage(graphics);
            /* ツリーの描画 */
            drawTree(graphics);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * オフスクリーン上のイメージのクリア
     * @param Graphics graphics
     */
    private void clearOffImage(Graphics graphics) {
        graphics.setColor(getBackground());
        graphics.fillRect(0, 0, offImage.getWidth(this), offImage.getHeight(this));
    }

    /**
     * ツリー全体を描画します。
     * @param Graphics g
     */
    private void drawTree(Graphics graphics) {
        yPos = 0;

        drawRoot(graphics);
        drawChild(graphics, 0, 0, rootElement);
    }


    /**
     * 指定された要素の子のツリーを描画します。
     * @param Graphics g
     * @param int x elementのツリー上のx方向の位置
     * @param int y elementのツリー上のy方向の位置
     * @param int FailAgentTreeElement element 描画する子の親となる要素
     */
    private void drawChild(Graphics graphics, int x, int y, FailAgentTreeElement element) {
        if (element == null)
            return;
        // 子のリストを逆にたどる
        for(int i = element.next.size()-1; i >= 0 ; i--) {
            FailAgentTreeElement nextElement = 
                    (FailAgentTreeElement)element.next.get(i);

            drawTreeElement(graphics, nextElement, x+1, yPos);
            drawLink(graphics, x, y, x+1, yPos);
            drawChild(graphics, x+1, yPos, nextElement);
            yPos = yPos + 1;
        }
    }

    /**
     * 親子関係のリンクを描画
     * @param Graphics graphics
     * @param int parentX 親のx方向の位置
     * @param int parentY 親のy方向の位置
     * @param int childX 子のx方向の位置
     * @param int childY 子のy方向の位置
     */
    private void drawLink(Graphics graphics, int parentX, int parentY,
            int childX, int childY) {
        int parentRectInfo[] = getTreeElementRectSize(parentX, parentY);
        int childRectInfo[] = getTreeElementRectSize(childX, childY);
        int startX = parentRectInfo[0] + parentRectInfo[2];
        int startY = parentRectInfo[1] + (parentRectInfo[3]/2);
        int endX = childRectInfo[0];
        int endY = childRectInfo[1] + (childRectInfo[3]/2);

        graphics.drawLine(startX, startY, endX, endY);
    }

    /**
     * ツリーの指定された要素を描画します。
     * @param Graphics graphics
     * @param FailAgentTreeElement element 描画する要素
     * @param int x x方向の位置
     * @param int y y方向の位置
     */
    private void drawTreeElement(Graphics graphics,
            FailAgentTreeElement element, int x, int y) {
        /* 描画する情報の取得 */
        int rectInfo[] = getTreeElementRectSize(x, y);
        String agidAndValue = element.agentId + "";
        String agr = " " + element.agr;

        /* 描画処理 */

        /* ツリーのカレントなら青枠で囲む */
        if(element == currentElement) {
            graphics.setColor(Color.blue);
            for(int i = 0; i < 5; i++) {
                graphics.drawRect(rectInfo[0]-i, rectInfo[1]-i,
                        rectInfo[2]+(2*i), rectInfo[3]+(2*i));
            }
        }

        /* エージェントごとの色をテーブルから取得 */
        Color color = colorTable.get(element.agentId);

        /* 各要素の描画 */
        if (element.agr == IGSAAgent.Status.AGR_SUCCESS) {
            if(color != null) {
                graphics.setColor(color);
                graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                        rectInfo[3]);
            }
            graphics.setColor(Color.black);
            graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
                    rectInfo[3]);

        } else if (
                element.agr == IGSAAgent.Status.AGR_FAIL_AGENT
                || element.agr == IGSAAgent.Status.AGR_REACH_GOAL
                || element.agr == IGSAAgent.Status.AGR_UNREACH_SUBGOAL) {
            if(color != null) {
                graphics.setColor(color);
                graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
                        rectInfo[3], 0, 360);
            }
            graphics.setColor(Color.black);
            graphics.drawArc(rectInfo[0], rectInfo[1], rectInfo[2],
                    rectInfo[3], 0, 360);
            graphics.setFont(agrFont);
            graphics.drawString(agr, rectInfo[0]+rectInfo[2],
                    rectInfo[1]+(rectInfo[3]/2));

        } else if(
                element.agr == IGSAAgent.Status.AGR_SEARCH_FAIL
                || element.agr == IGSAAgent.Status.AGR_SAME_SUBGOAL
                || element.agr == IGSAAgent.Status.AGR_SAME_GOAL) {
            if(color != null) {
                graphics.setColor(color);
                graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
                        rectInfo[3], 0, 360);
            }
            graphics.setColor(Color.black);
            graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
                    rectInfo[3]);
            graphics.setFont(agrFont);
            Rectangle2D bBox = graphics.getFontMetrics().getStringBounds(agidAndValue, graphics);
            graphics.drawString(
                agr,
                rectInfo[0] + rectInfo[2],
                (int)(rectInfo[1] + rectInfo[3] / 2 + bBox.getHeight() / 2)
            );

        }

        /* エージェントIDの描画 */
        graphics.setFont(agidFont);
        Rectangle2D bBox = graphics.getFontMetrics().getStringBounds(agidAndValue, graphics);
        graphics.drawString(
            agidAndValue,
            (int)(rectInfo[0] + rectInfo[2] / 2 - bBox.getWidth() / 2),
            (int)(rectInfo[1] + rectInfo[3] / 2 + bBox.getHeight() / 2)
        );
    }


    /*
     * 描画を行なう要素数を取得します。
     * @param FailAgentTreeElement element
     * @param int[] drawNum
     */
    private void childSize(FailAgentTreeElement element, int[] drawNum) {
        if (element == null)
            return;
        for(int i = 0; i < element.next.size(); i++) {
            FailAgentTreeElement nextElement = null;
            nextElement = (FailAgentTreeElement)element.next.get(i);
            drawNum[0] ++;
            childSize(nextElement, drawNum);
            drawNum[1] ++;
        }
    }

    /**
     * ツリーのルートを描画します。
     * @param Graphics g
     */
    private void drawRoot(Graphics graphics) {
        graphics.setFont(agrFont);
        graphics.setColor(Color.black);
        graphics.drawString("root", X_SPACE-(X_ELEMENT_SIZE/2),
                Y_SPACE+(Y_ELEMENT_SIZE/4)-5);
        graphics.fillArc(X_SPACE-(X_ELEMENT_SIZE/2),
                Y_SPACE+(Y_ELEMENT_SIZE/4), X_ELEMENT_SIZE/2, Y_ELEMENT_SIZE/2,
                0, 360);
    }

    /**
     * ツリー上の指定された位置の要素を描画する矩形の情報を取得します。
     * @param int x 要素のx方向の位置
     * @param int y 要素のy方向の位置
     * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
     */
    private int[] getTreeElementRectSize(int x, int y) {
        int[] rectInfo = new int[4];
        rectInfo[0] = X_SPACE + (x*(X_SPACE+X_ELEMENT_SIZE) - X_ELEMENT_SIZE);
        rectInfo[1] = Y_SPACE + (y*(Y_SPACE+Y_ELEMENT_SIZE));
        rectInfo[2] = X_ELEMENT_SIZE;
        rectInfo[3] = Y_ELEMENT_SIZE;
        return rectInfo;
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }
}


