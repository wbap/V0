/**
 * EnvironmentCanvas.java
 *  環境グラフィック処理を行うクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.doorkeydemo.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 *  環境グラフィック処理を行うクラスです
 */
public class EnvironmentCanvas extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int XSPACE_UNIT = 20;
    private static final int YSPACE_UNIT = 20;
    
    /* 地図情報 */
    private String[][] map;

    /* ロボットの位置情報 */
    private int[] robotState;

    /* 報酬の情報 */
    private String[][] rewardMap;

    /* 床の色情報 */
    private String[][] colorMap;

    /* 罫線の間隔 */
    private int xSpace = XSPACE_UNIT;
    private int ySpace = YSPACE_UNIT;

    /* サイズ変更のフラグ */
    private boolean dirty = false;

    /* ダブルバッファリング用 オフスクリーンイメージ */
    private Image offImage;

    private Color redFloor = new Color(255, 200, 200);
    private Color greenFloor = new Color(200, 255, 200);
    private Color blueFloor = new Color(200, 200, 255);

    private Image telephoneImage = null;
    private Image keyImage = null;
    
    private Dimension preferredSize;

    private double zoomRatio = 0.5;

    /**
     * コンストラクタ
     */
    public EnvironmentCanvas(Image telephoneImage, Image keyImage) {
        super();
        this.telephoneImage = telephoneImage;
        this.keyImage = keyImage;
    }

    /**
     * 初期化処理
     * @param String[][] map    環境の地図情報
     * @param int[] robotState  ロボットの位置  int[0]=x座標  int[1]=y座標
     */
    public void initCanvas(String[][] map, int[] robotState, String[][] rewardMap) {
        this.map = map;
        this.rewardMap = rewardMap;
        int[] _robotState = new int[robotState.length];
        System.arraycopy(robotState,  0,  _robotState,  0, robotState.length);
        this.robotState = _robotState;
        this.dirty = true;
        updateIfNecessary();
    }

    /**
     * 初期化処理
     * @param String[][] map       環境の地図情報
     * @param int[] robotState     ロボットの位置  int[0]=x座標  int[1]=y座標
     */
    public void initCanvas(String[][] map, int[] robotState) {
        initCanvas(map, robotState, null);
    }

    /**
     * 初期化処理
     * @param String[][] map       環境の地図情報
     * @param int[] robotState     ロボットの位置  int[0]=x座標  int[1]=y座標
     * @param String[][] rewardMap 報酬のテーブル
     *                             地図情報と同じサイズのテーブルで対応する
     *                             位置に報酬が設定されたもの
     * @param String[][] colorMap  フロアの色を設定したテーブル
     *                             地図情報と同じサイズのテーブルで対応する
     *                             位置に色が設定されたもの
     */
    public void initCanvas(String[][] map, int[] robotState,
            String[][] rewardMap, String[][] colorMap) {
        initCanvas(map, robotState, rewardMap);
        this.colorMap = colorMap;
    }

    public void updateIfNecessary() {
        if (!dirty && !isVisible())
            return;
        this.xSpace = (int)(XSPACE_UNIT * zoomRatio);
        this.ySpace = (int)(YSPACE_UNIT * zoomRatio);

        final Dimension preferredSize = new Dimension(xSpace * (map.length + 2), ySpace * (map[0].length + 2));
        if (!preferredSize.equals(this.preferredSize)) {
            this.preferredSize = preferredSize;
            this.setSize(preferredSize); // this revalidates the parent so the preferredSize must be set prior to this
        }
        if (offImage == null || preferredSize.width != offImage.getWidth(this) || preferredSize.height != offImage.getHeight(this)) {
            if (preferredSize.width >= 0 && preferredSize.height >= 0)
                offImage = createImage(preferredSize.width, preferredSize.height);
        }
        /* オフスクリーンへの描画 */
        if (offImage != null)
            drawOffImage();
        dirty = false;
    }
    
    /**
     * paintメソッドのオーバーライド
     */
    public void paint(final Graphics g) {
        /* オフスクリーンイメージを描画 */
        final Dimension size = getSize();
        g.clearRect(0, 0, size.width, size.height);
        if (offImage != null) {
            g.drawImage(offImage, 0, 0, this);
        }
    }

    public void setRobotPos(int[] state) {
        int[] _robotState = new int[state.length];
        System.arraycopy(state, 0, _robotState,  0, robotState.length);
        this.robotState = _robotState;
        dirty = true;
        updateIfNecessary();
    }
    
    /**
     * 各マスのx軸方向のサイズ
     * @return int
     */
    public int getXSpace() {
        return xSpace;
    }

    /**
     * 各マスのy軸方向のサイズ
     * @return int
     */
    public int getYSpace() {
        return ySpace;
    }

    private boolean flagFlash = false;
    /**
     * 画面を点滅させます。
     */
    public void flash() {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            private int count = 0;
 
            @Override
            public void run() {
                flagFlash = count % 2 == 0;
                if (++count >= 8) {
                    t.cancel();
                }
                dirty = true;
                updateIfNecessary();
                repaint();
            }
        }, 100, 100);
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
            if (flagFlash) {
                /* 点滅させる場合 */
                graphics.setColor(Color.RED);
            } else {
                graphics.setColor(getBackground());
            }    
            /* イメージのクリア */
            graphics.fillRect(0, 0, offImage.getWidth(this), offImage.getHeight(this));
    
            /* 罫線の描画 */
            graphics.setColor(Color.black);
    
            int xNum = map.length+1;    /* x軸方向のます数 */
            int yNum = map[0].length+1; /* y軸方向のます数 */
    
            /* ｘ軸方向の罫線 */
            for(int i = ySpace; i <= ySpace * yNum; i = i+ySpace) {
                graphics.drawLine(xSpace, i, xSpace * xNum, i);
            }
            /* ｙ軸方向の罫線 */
            for(int i = xSpace; i <= xSpace * xNum; i = i+xSpace) {
                graphics.drawLine(i, ySpace, i, ySpace * yNum);
            }
    
            /* 地図の描画 */
            for(int x = 0; x < map.length; x++) {
                for(int y = 0; y < map[0].length; y++) {
    
                    /* 地図上の一マスのサイズに関する情報を取得 */
                    int[] rectInfo = getMapRectInfo(x, y);
    
                    /* 床の描画 */
                    if(colorMap != null) {
                        drawFloor(graphics, x, y, rectInfo);
                    }
    
                    String mapID = "";
                    if(map[x][y].length() > 0) {
                        mapID = map[x][y].substring(0, 1);
                    }
    
                    /* 報酬 */
                    if( mapID.equals("O") ) {
                        graphics.setColor(Color.green);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("O", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    // 2001.03.22 追加 miyamoto
                    // ドアとカギの表示を追加
                    /* ドア クローズ */
                    if( mapID.equals("D") ) {
                        graphics.setColor(Color.darkGray);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("D", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    /* ドア オープン*/
                    if( mapID.equals("d") ) {
                        graphics.setColor(Color.lightGray);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("d", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    /* カギ */
                    if( mapID.equals("K") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        if(keyImage != null) {
                            graphics.drawImage(keyImage, rectInfo[0], 
                                    rectInfo[1], this);
                        }
                    }
                    if( mapID.equals("T") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        if(telephoneImage != null) {
                            graphics.drawImage(telephoneImage, rectInfo[0],
                                    rectInfo[1], this);
                        }
                    }
    
                    // アイテムを増やす
                    if( mapID.equals("A") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("A", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    if( mapID.equals("B") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("B", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    if( mapID.equals("a") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("a", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    if( mapID.equals("b") ) {
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        graphics.setColor(Color.black);
                        graphics.drawString("b", rectInfo[0],
                                rectInfo[1]+rectInfo[3]);
                    }
                    
                    /* スタート */
                    if( mapID.equals("S") ) {
                        graphics.setColor(Color.pink);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                    }
                    /* 2001.07.06 追加 ランダムに動作 */
                    if( mapID.equals("R") ) {
                        graphics.setColor(Color.magenta);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                    }
                    /* ジャンプ */
                    if( mapID.equals("J") ) {
                        graphics.setColor(Color.gray);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                    }
                    /* 流れ */
                    if( mapID.equals("F") ) {
                        graphics.setColor(Color.cyan);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        /* 方向の取得 */
                        int startIndex = map[x][y].indexOf("(");
                        int endIndex = map[x][y].indexOf(")");
                        String dir = map[x][y].substring(startIndex+1, endIndex);
                        /* 方向を描画 */
                        drawDirection(graphics, rectInfo, dir);
                    }
                    /* 崖 */
                    if( mapID.equals("C") ) {
                        graphics.setColor(Color.orange);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                        /* 方向の取得 */
                        int startIndex = map[x][y].indexOf("(");
                        int endIndex = map[x][y].indexOf(")");
                        String dir = map[x][y].substring(startIndex+1, endIndex);
                        /* 方向を描画 */
                        drawDirection(graphics, rectInfo, dir);
                    }
                    /* 壁 */
                    if( mapID.equals("W") ) {
                        graphics.setColor(Color.black);
                        graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
                                rectInfo[3]);
                    }
    
                }
            }
    
            /* ロボットの描画 */
            int[] rectInfo = getMapRectInfo(robotState[0], robotState[1]);
            graphics.setColor(Color.blue);
            graphics.fillOval(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
            /* ロボット位置にアイテムを表示 */
            // 文字で表示
            graphics.setColor(Color.white);
            String item = null;
            if(robotState[2] == 3) {
                item = "A";
            }else if(robotState[2] == 4) {
                item = "B";
            }else {
                item = "";
            }
            graphics.drawString(item, rectInfo[0], rectInfo[1]+rectInfo[3]);
        
            /* 報酬のテーブルが無ければ(==null)なら表示をしない */
            if(rewardMap != null) {
                drawRewardValue(graphics);
            }
        } finally {
            graphics.dispose();
        }
    }


    /**
     * Flow・Cliffの矢印の描画
     * @param Graphics graphics 
     * @param int[] rectInfo 矩形の情報
     * @param Stirng dir     方向
     */
    private void drawDirection(Graphics graphics, int[] rectInfo, String dir) {
        graphics.setColor(Color.black);
        /* 上方向の矢印 */
        if( dir.equals("U") ) {
            graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
                    rectInfo[0], rectInfo[1]+rectInfo[3]);
            graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
                    rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
        }
        /* 下方向の矢印 */
        if( dir.equals("D") ) {
            graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
                    rectInfo[1]+rectInfo[3], rectInfo[0], rectInfo[1]);
            graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
                    rectInfo[1]+rectInfo[3], rectInfo[0]+rectInfo[2],
                    rectInfo[1]);
        }
        /* 左方向の矢印 */
        if( dir.equals("L") ) {
            graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
                    rectInfo[0]+rectInfo[2], rectInfo[1]);
            graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
                    rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
        }
        /* 右方向の矢印 */
        if( dir.equals("R") ) {
            graphics.drawLine(rectInfo[0]+rectInfo[2],
                    rectInfo[1]+rectInfo[3]/2, rectInfo[0], rectInfo[1]);
            graphics.drawLine(rectInfo[0]+rectInfo[2],
                    rectInfo[1]+rectInfo[3]/2, rectInfo[0],
                    rectInfo[1]+rectInfo[3]);
        }
    }


    /**
     * 報酬の値を描画します。
     */
    private void drawRewardValue(Graphics graphics) {
        /* x軸方向への繰り返し */
        for(int x = 0; x < map.length; x++) {
            /* y軸方向への繰り返し */
            for(int y = 0; y < map[0].length; y++) {
                int[] rectInfo = getMapRectInfo(x, y);
                /* 報酬を取得 */
                String reward = rewardMap[x][y];
                graphics.setColor(Color.black);
                graphics.drawString(reward, rectInfo[0],
                        rectInfo[1]+rectInfo[3]);
            }
        }
    }


    int[] xPoints = new int[4];
    int[] yPoints = new int[4];

    private void drawFloor(Graphics graphics, int x, int y, int[] rectInfo) {

        String color = "";
        if(colorMap[x][y].length() > 0) {
            color = colorMap[x][y].substring(0, 1);
        }

        // 2001.08.08 追加 miyamoto
        /* 床に色を設定 */
        if( color.equals("w") ) {
//            graphics.setColor(Color.black);
//            graphics.fillRect(rectInfo[0], rectInfo[1],
//                    rectInfo[2], rectInfo[3]);
        }
        if( color.equals("r") ) {
            graphics.setColor(redFloor);
            graphics.fillRect(rectInfo[0], rectInfo[1],
                    rectInfo[2], rectInfo[3]);
        }
        if( color.equals("g") ) {
            graphics.setColor(greenFloor);
            graphics.fillRect(rectInfo[0], rectInfo[1],
                    rectInfo[2], rectInfo[3]);
        }
        if( color.equals("b") ) {
            graphics.setColor(blueFloor);
            graphics.fillRect(rectInfo[0], rectInfo[1],
                    rectInfo[2], rectInfo[3]);
        }
    }


    /**
     * 指定された地図上のＸＹ座標に対応する矩形の情報を取得します。
     * @param int x 地図上のＸ座標
     * @param int y 地図上のＹ座標
     * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
     */
    private int[] getMapRectInfo(int x, int y) {
        int[] rectInfo = null;
        /* 範囲内かチェック */
        if( (x >= 0)&&(y >= 0) && (x < map.length)&&(y < map[0].length) ) {
            rectInfo = new int[4];
            rectInfo[0] = ((x+1)*xSpace) + 1;
            rectInfo[1] = ((y+1)*ySpace) + 1;
            rectInfo[2] = xSpace-1;
            rectInfo[3] = ySpace-1;
        }
        return rectInfo;
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }


    public double getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(double zoomRatio) {
        this.zoomRatio = zoomRatio;
        dirty = true;
        updateIfNecessary();
    }
}


