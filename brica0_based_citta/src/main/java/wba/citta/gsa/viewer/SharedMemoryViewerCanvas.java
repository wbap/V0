/**
 * SharedMemoryViewerCanvas.java
 *  共有メモリの状態を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wba.citta.gsa.*;

/**
 *  共有メモリの状態を描画するクラス
 */
public class SharedMemoryViewerCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    /* 現在の状態への参照 */
    private Integer[] stateArray;
    /* ゴールスタックへの参照 */
    private List<IGoalStack.GoalStackElement>[] goalStackArray;

    /* 左右の間隔 */
    private final int X_SPACE = 40;
    /* 下の間隔 */
    private final int Y_SPACE = 10;

    /* 描画する要素のサイズ */
    private final int X_ELEMENT_SIZE = 30;
    private final int Y_ELEMENT_SIZE = 30;

    /* ダブルバッファリング用 オフスクリーンイメージ */
    private Image offImage;

    /* 文字のフォント */
    private Font agidFont = new Font("Dialog", Font.BOLD, 12);
    private Font valueFont = new Font("Dialog", Font.PLAIN , 12);

    private Map<Integer, Color> colorTable;

    private boolean dirty = false;

    /**
     * コンストラクタ
     */
    public SharedMemoryViewerCanvas(Map<Integer, Color> colorTable) {
        super();
        this.colorTable = colorTable;
        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {}

            @Override
            public void componentMoved(ComponentEvent arg0) {}

            @Override
            public void componentResized(ComponentEvent arg0) {
                dirty = true;                
                updateIfNecessary();
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                dirty = true;
                updateIfNecessary();
                repaint();
            }
        });
    }

    public void snapshot(ISharedMemory sharedMemory) {
        final int size = sharedMemory.getSize();
        Integer[] stateArray;
        {
            final ILatch latch = sharedMemory.getLatch();
            stateArray = (Integer[])latch.getState().toArray();
        }
        @SuppressWarnings("unchecked")
        List<IGoalStack.GoalStackElement>[] goalStackArray = new List[size];
        {
            final IGoalStack goalStack = sharedMemory.getGoalStack();
            for (int i = 0; i < size; i++) {
                goalStackArray[i] = new ArrayList<IGoalStack.GoalStackElement>(goalStack.getGoalStackForNode(i));
            }
        }
        this.stateArray = stateArray;
        this.goalStackArray = goalStackArray;
        dirty = true;
        updateIfNecessary();
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

    private void updateIfNecessary() {
        if (!dirty && !isVisible())
            return;
        Dimension size = getDrawAreaSize();
        setSize(size);
        if (offImage == null || size.width != offImage.getWidth(this) || size.height != offImage.getHeight(this)) {
            offImage = createImage(size.width, size.height);
        }
        /* オフスクリーンへの描画 */
        if (offImage != null) {
            drawOffImage();
        }
        dirty = false;
    }

    ////////////////////////////////////////////////////////////
    // private

    /**
     * オフスクリーンへの描画
     * @param Graphics graphics
     */
    private void drawOffImage() {
        final Graphics2D graphics = (Graphics2D)offImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            /* イメージのクリア */
            clearOffImage(graphics);
    
            /* ベース部分の描画 */
            drawBase(graphics);
            /* 現在の状態の描画 */
            drawState(graphics);
            /* ゴールスタック全体の描画 */
            drawStackArray(graphics);
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
     * 現在の状態を描画します。
     * @param Graphics graphics
     */
    private void drawState(Graphics graphics) {
        if (stateArray != null) {
            for(int i = 0; i < stateArray.length; i++) {
                if(stateArray[i] != null) {
                    drawStateElement(graphics, i, stateArray[i]);
                }
            }
        }
    }

    /**
     * 現在の状態の各要素を描画します。
     * @param Graphics g
     * @param int nodeIndex 描画するノード
     * @param Integer element 描画する値
     */
    private void drawStateElement(Graphics graphics, int nodeIndex,
            Integer element) {

        /* 描画するCanvas上の位置を取得 */
        int rectInfo[] = getStackElementRectSize(nodeIndex, 1);

        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawRoundRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3], 3, 3);

        graphics.setColor(Color.BLACK);
        graphics.setFont(valueFont);
        String text = element.toString();
        final FontMetrics fm = graphics.getFontMetrics();
        graphics.drawString(
            text,
            rectInfo[0] + rectInfo[2] - fm.stringWidth(text),
            rectInfo[1] + rectInfo[3] - fm.getDescent()
        );
    }


    /**
     * ゴールスタック全体を描画します。
     * @param Graphics g
     */
    private void drawStackArray(Graphics graphics) {
        if (goalStackArray != null) {
            for(int i = 0; i < goalStackArray.length; i++) {
                drawStack(graphics, i);
            }
        }
    }

    /**
     * ゴールスタックの指定されたノードを描画します。
     * @param Graphics g
     * @param int nodeIndex 描画するノード
     */
    private void drawStack(Graphics graphics, int nodeIndex) {
        for(int i = 0; i < goalStackArray[nodeIndex].size(); i++) {
            IGoalStack.GoalStackElement element
                    = (IGoalStack.GoalStackElement)goalStackArray[nodeIndex].get(i);
            drawStackElement(graphics, nodeIndex, i, element);
        }
    }

    /**
     * ゴールスタックの指定された要素を描画します。
     * @param Graphics g
     * @param int x 描画するノード
     * @param int y 描画する要素のノード中の位置
     * @param SharedMemory.GoalStackElement element 描画する要素
     */
    private void drawStackElement(Graphics graphics, int x, int y,
            IGoalStack.GoalStackElement element) {

        String value = Integer.toString(element.value);
        String agid = Integer.toString(element.agid);

        /* 描画するCanvas上の位置を取得 */
        int rectInfo[] = getStackElementRectSize(x, y+2);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

        /* 描画処理 */
        /* エージェントごとの色をテーブルから取得 */
        graphics.setFont(agidFont);
        {
            final FontMetrics fm = graphics.getFontMetrics();
            final int height = fm.getHeight();
            final Color color = colorTable.get(element.agid);
            if (color != null) {
                graphics.setColor(color);
                graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                        height);
            }
            graphics.setColor(Color.black);
            graphics.drawString(
                agid,
                rectInfo[0] + ((rectInfo[2] - fm.stringWidth(agid)) /2),
                rectInfo[1] + height);
        }
        graphics.setColor(Color.black);
        graphics.setFont(valueFont);
        {
            final FontMetrics fm = graphics.getFontMetrics();
            graphics.drawString(value, rectInfo[0] + rectInfo[2] - fm.stringWidth(value), rectInfo[1] + rectInfo[3] - fm.getDescent());
        }
        graphics.setColor(Color.black);
        graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
    }

    /**
     * スタックのベース部分を描画します。
     * @param Graphics g
     */
    private void drawBase(Graphics graphics) {

        graphics.setFont(valueFont);
        final FontMetrics fm = graphics.getFontMetrics();

        if (goalStackArray != null) {
            /* ノードのIDの描画 */
            for(int i = 0; i < goalStackArray.length; i++) {
                final int[] rectInfo = getStackElementRectSize(i, 0);
                final String text = Integer.toString(i);
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.fillOval(rectInfo[0] + rectInfo[2] / 4, rectInfo[1] + rectInfo[3] / 4, rectInfo[2] / 2, rectInfo[3] / 2);
                graphics.setColor(Color.BLACK);
                int xPos = rectInfo[0] + (rectInfo[2] - fm.stringWidth(text)) / 2;
                int yPos = rectInfo[1] + (rectInfo[3] - fm.getHeight()) / 2 + fm.getAscent();
                graphics.drawString(text, xPos, yPos);
            }
        }

        graphics.setColor(Color.BLACK);
        /* "GoalStack","State"の描画 */
        int[] rectInfo = getStackElementRectSize(0, 2);
        graphics.drawString("Goal", 5, rectInfo[1] + fm.getHeight());
        graphics.drawString("Stack", 5, rectInfo[1] + fm.getHeight() * 2);

        rectInfo = getStackElementRectSize(0, 1);
        graphics.drawString("State", 5, rectInfo[1] + (rectInfo[3] + fm.getHeight()) / 2);
    }

    /**
     * 指定された共有メモリの位置に描画する矩形の情報を取得します。
     * @param int x x方向への位置
     * @param int y y方向への位置
     * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
     */
    private int[] getStackElementRectSize(int x, int y) {
        int[] rectInfo = new int[4];
        rectInfo[0] = X_SPACE + (x * X_ELEMENT_SIZE);
        rectInfo[1] = Y_SPACE + (y * Y_ELEMENT_SIZE);
        rectInfo[2] = X_ELEMENT_SIZE;
        rectInfo[3] = Y_ELEMENT_SIZE;
        return rectInfo;
    }

    /**
     * 描画に必要なサイズを取得します。
     * @return int[] [0]幅  [1]高さ
     */
    private Dimension getDrawAreaSize() {
        int[] drawNum = getDrawNum();
        return new Dimension(
            (drawNum[0] * X_ELEMENT_SIZE) + (X_SPACE*2),
            (drawNum[1]+1) * Y_ELEMENT_SIZE
        );
    }

    /**
     * 描画する要素数を取得します。
     * @return int[] 描画する要素数 [0]x軸方向への数 [1]y軸方向への数
     */
    private int[] getDrawNum() {
        int[] drawNum = new int[2];
        if (goalStackArray != null) {
            drawNum[0] = goalStackArray.length;
            for(int i = 0; i < goalStackArray.length; i++) {
                if(drawNum[1] < goalStackArray[i].size()) {
                    drawNum[1] = goalStackArray[i].size();
                }
            }
    
            /* ベースと、ステイトの分を追加 */
            drawNum[1] += 2;
        }
        return drawNum;
    }

}


