/**
 * Environment.java
 *  環境の処理を行うクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.doorkeydemo.environment;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Collections;
import java.util.Set;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import wba.citta.IterationEvent;
import wba.citta.IterationEventListener;
import wba.citta.gui.ViewerPanel;

/**
 * 環境の処理を行います。
 */
public class Environment extends JPanel implements ViewerPanel, IterationEventListener {
    private static String resourceBase = Environment.class.getPackage().getName().replace('.', '/');
    private static final Set<String> roles = Collections.singleton("main");
    private static final long serialVersionUID = 1L;

    private JScrollPane scrollPane;

    /* 環境の描画を行なうクラス */
    private EnvironmentCanvas canvas;

    /* 地図情報から行動を管理するクラス */
    private ActionController actionController;

    /* 地図情報を管理するクラス */
    private Disposition map;

    /* ロボットの位置 */
    private int[] robotState;

    /* 地図に対応した報酬のテーブル */
    private String[][] rewardMap;

    String[][] colorMap = null;

    /* 持ち物 */
    public final int NOTHING = 0;
    public final int KEY = 1;
    public final int TELEPHON = 2;
    public final int A = 3;
    public final int B = 4;
    public final int a = 5;
    public final int b = 6;

    /* クリック時に設定する状況 ""=空白 "W"=壁 "O"=報酬 "n"=設定しない */
    private String renewValue = "n";

    /* ゴールを複数設定可能にするか */
    private boolean flagGoals;

    /* 文字列の設定 */
    private MessageCanvas itemCanvas;

    private static Image telephoneImage = null;
    private static Image keyImage = null;
    private static Image zoomInImage = null;
    private static Image zoomOutImage = null;

    private static void initImage() throws IOException {
        final ClassLoader ldr = Environment.class.getClassLoader();
        telephoneImage = loadImage(ldr.getResourceAsStream(resourceBase + "/telephone2.gif"));
        keyImage = loadImage(ldr.getResourceAsStream(resourceBase + "/key2.gif"));
        zoomInImage = loadImage(ldr.getResourceAsStream(resourceBase + "/gtk-zoom-in.png"));
        zoomOutImage = loadImage(ldr.getResourceAsStream(resourceBase + "/gtk-zoom-out.png"));
    }

    static {
        try {
            initImage();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("serial")
    private Action zoomInAction = new AbstractAction("Zoom-in", new ImageIcon(zoomInImage)) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            zoomInPressed();
        }
        
    };
    
    @SuppressWarnings("serial")
    private Action zoomOutAction = new AbstractAction("Zoom-out", new ImageIcon(zoomOutImage)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            zoomOutPressed();
        }        
    };

    public Environment(Disposition map) throws IOException {
        super(new BorderLayout(5, 5));
        this.map = map;
        initComponents();
        initMap(false/*true*/, false);
        initMapState();
    }

    public void initMap(boolean isShowReward, boolean flagGoals) {
        /* 地図情報から行動を管理するクラスの生成 */
        actionController = new ActionController(map.getMap());

        robotState = new int[3];
        this.flagGoals = flagGoals;
        initRobotPos();
        initCanvas(isShowReward);
    }

    public void initMapState() {
        clearItem();
        closeDoor();
    }

    /**
     * フレームの初期化
     */
    private void initComponents() {
        canvas = new EnvironmentCanvas(telephoneImage, keyImage);
        canvas.addMouseListener(new MouseAdapter() {
            /**
             * マウスがクリックされた時の処理
             */
            public void mouseClicked(MouseEvent e) {
                /* クリックされた位置の取得 */
                int xPos = e.getX();
                int yPos = e.getY();

                /* 間隔を取得 */
                int xSpace = canvas.getXSpace();
                int ySpace = canvas.getYSpace();

                int[] size = map.getSize();

                /* 地図の範囲内であれば処理を行う */
                if( ((xPos>xSpace)&&(yPos>ySpace)) &&
                        ((xPos<(xSpace*(size[0]+1))) &&
                        (yPos<(ySpace*(size[1]+1)))) ) {

                    /* クリックされた位置を地図上の座標に変換 */
                    int x = xPos / xSpace;
                    int y = yPos / ySpace;

                    if(!renewValue.equals("n")) {

                        /* 複数のゴールを設定しない場合は以前の値を削除 */
                        if(!flagGoals) {
                            if(renewValue.equals("O(1)")) {
                                int[] pos = map.getPos("O");
                                if(pos != null) {
                                    map.set(pos[0], pos[1], "");
                                }
                            }
                        }

                        /* 地図情報の更新 */
                        map.set(x-1, y-1, renewValue);

                        /* 再描画 */
                        canvas.repaint();
                    }
                    /* 報酬の更新 */
                    renewReward();
                }
            }
        });
        scrollPane = new JScrollPane(
            canvas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        itemCanvas = new MessageCanvas(8, 5);
        itemCanvas.setMessage("Items");
        final JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(itemCanvas);
        toolbar.add(zoomInAction);
        toolbar.add(zoomOutAction);
        /* フレームの作成 */
        add(scrollPane, "Center");
        add(toolbar, "North");
    }

    private static Image loadImage(InputStream s) throws IOException {
        byte[] buf = new byte[256];
        int o = 0;
        for (;;) {
            if (o >= buf.length) {
                byte[] newBuf = new byte[buf.length + buf.length / 2];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                buf = newBuf;
            }
            int r = s.read(buf, o, buf.length - o);
            if (r == -1)
                break;
            o += r;
        }
        {
            byte[] newBuf = new byte[o];
            System.arraycopy(buf, 0, newBuf, 0, newBuf.length);
            buf = newBuf;
        }
        return Toolkit.getDefaultToolkit().createImage(buf);
    }

    protected void zoomInPressed() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final double zoomRatio = canvas.getZoomRatio();
                canvas.setZoomRatio(zoomRatio + 0.1);
            }
        });
    }

    protected void zoomOutPressed() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final double zoomRatio = canvas.getZoomRatio();
                canvas.setZoomRatio(Math.max(zoomRatio - 0.1, 0));
            }
        });
    }

    private void showItemImage() {
        int itemID = getItem();
        if(itemID == 2) {
            itemCanvas.setImage(telephoneImage);
        }else if(itemID == 1) {
            itemCanvas.setImage(keyImage);
        }else {
            itemCanvas.setImage(null);
        }
        itemCanvas.repaint();
    }

    public void flash() {
        canvas.flash();
    }

    /**
     * 地図上の指定された位置の属性を取得します。
     * @param int x  x座標
     * @param int y  y座標
     * @return String 位置の属性 
     */
    public String getAttribute(int x, int y) {
        String str = getMapInfo(x, y);
        String attr = null;
        if(str.equals("K")) {
            attr = "K";
        }else if(str.equals("d")) {
            attr = "d";
        }else if(str.equals("O(1)")) {
            attr = "O";
        }else if(str.equals("T")) {
            attr = "T";
        }else {
            attr = "N";
        }
        return attr;
    }

    public String getFloorColor(int x, int y) {
        String str = getColorInfo(x, y);
        String color = null;
        if(str == null) {
        }else if(str.equals("r")) {
            color = "r";
        }else if(str.equals("g")) {
            color = "g";
        }else if(str.equals("b")) {
            color = "b";
        }else {
            color = "w";
        }
        return color;
    }
    
    /**
     * ロボットの位置の初期化
     */
    public void initRobotPos() {
        int[] pos = map.getPos("S");
        robotState[0] = pos[0];
        robotState[1] = pos[1];
    }

    /**
     * 描画処理部分の初期化
     * @param boolean 報酬の表示を行なうかどうか
     */
    private void initCanvas(boolean b) {
        /* 報酬の表示を行なう場合は報酬用のテーブルを初期化 */
        if (b) {
            int[] size = map.getSize();
            rewardMap = new String[size[0]][size[1]];
            /* 報酬の更新 */
            renewReward();
        }
        canvas.initCanvas(map.getMap(), robotState, rewardMap, colorMap);
    }

    ////////////////////////////////////////////////////////////
    // public

    /**
     * ロボットを指定された方向へ移動します。
     * @param int action  移動方向 ０〜７で設定
     * @return boolean    true=移動成功 false=移動失敗
     */
    public boolean run(int action) {

        int[] newState = actionController.move(robotState[0], robotState[1],
                action);

        /* 実際に移動できたかチェックし 移動していれば状態を変化 */
        boolean isMove = true;
        if( (robotState[0] == newState[0]) && (robotState[1]==newState[1]) ) {
            isMove = false;
        }else{
            robotState[0] = newState[0];
            robotState[1] = newState[1];
            checkItem(robotState[0], robotState[1]);
            canvas.setRobotPos(robotState);
            canvas.repaint();
        }
        showItemImage();
        return isMove;
    }


    /**
     * ロボットを指定された方向へ移動します。
     * @param int[] xy    移動先の座標
     * @param int action  移動方向 ０〜７で設定
     * @return boolean    true=引数で設定された座標に、引数で設定されたAction
     *                         で移動成功
     *                    false=引数で設定された座標に、引数で設定されたAction
     *                         で移動失敗
     */
    public boolean run(int[] xy, int action) {

        int[] newState = actionController.move(robotState[0], robotState[1],
                action);

        /*
         * 引数で指定された位置に実際に移動できるのかチェックし移動できれば
         * 状態を変化
         */
        boolean isMove = false;

        if( (xy[0] == newState[0]) && (xy[1]==newState[1]) ) {
            robotState[0] = newState[0];
            robotState[1] = newState[1];
            checkItem(robotState[0], robotState[1]);
            canvas.setRobotPos(robotState);
            canvas.repaint();
            isMove = true;
        }
        return isMove;
    }


    /**
     * 指定された座標をスタート位置にします。
     * @param int x X座標
     * @param int y Y座標
     */
    public void setStart(int x, int y) {
        /* 前のスタートを消す */
        int[] pos = map.getPos("S");
        map.set(pos[0], pos[1], "");
        /* 新しいスタートを設定 */
        map.set(x, y, "S");
    }

    // 2001.09.05 追加 miyamoto
    public void setItem(int newItem) {
        robotState[2] = newItem;
        controlDoor(newItem);
    }

    /**
     * 指定された座標にゴールを設定します。
     * @param int x X座標
     * @param int y Y座標
     */
    public void setGoal(int x, int y) {

        /* ゴールが複数設定不可なら以前のゴールを削除 */
        if(!flagGoals) {
            int[] goalState = getXYGoalState();
            if(goalState != null) {
                map.set(goalState[0], goalState[1], "");
            }
        }
        map.set(x, y, "O(1)");

        /* 報酬の更新 */
        renewReward();
    }

    /**
     * ゴールをクリアします。
     */
    public void clearGoal() {
        int[] goalState = getXYGoalState();
        if(goalState != null) {
            map.set(goalState[0], goalState[1], "");
        }
    }

    /**
     * 環境の描画領域をクリックして環境を変更するときの、変更する情報を
     * 設定します
     * @param String str  環境に設定する文字列
     */
    public void setRenewValue(String str) {
        renewValue = str;
    }

    /**
     * ロボットの位置をセンサ情報で取得します。
     * @return int[8] 
     */
    public int[] getSenserState() {
        return map.getState(robotState[0], robotState[1]);
    }


    private int[] robotPos = new int[2];
    /**
     * ロボットの位置をＸＹ座標で取得します。
     * @return int[] 現在の座標
     *               int[0] x座標
     *               int[1] y座標
     */
    public int[] getXYState() {
//        return robotState;
        robotPos[0] = robotState[0];
        robotPos[1] = robotState[1];
        return robotPos;
    }

    /**
     * ゴールの位置をセンサ情報で取得します。
     * @return int[] ゴールの位置
     */
    public int[] getSensorGoalState() {
        int[] goalState = getXYGoalState();
        if(goalState == null) {
            return null;
        }
        return map.getState(goalState[0], goalState[1]);
    }

    /**
     * ゴールの位置をＸＹ座標で取得します。
     * @return int[] ゴールの座標
     *               int[0] x座標
     *               int[1] y座標
     */
    public int[] getXYGoalState() {
        int[] goalState = map.getPos("O");
        return goalState;
    }

    /**
     * キーの位置をＸＹ座標で取得します。
     * @return int[] キーの位置
     *               int[0] x座標
     *               int[1] y座標
     */
    public int[] getXYKeyState() {
        int[] keyState = map.getPos("K");
        return keyState;
    }

    /**
     * スタートの位置をＸＹ座標で取得します。
     * @return int[] スタートの位置
     *               int[0] x座標
     *               int[1] y座標
     */
    public int[] getXYStartState() {
        int[] startState = map.getPos("S");
        return startState;
    }

    /**
     * 報酬を取得します。
     * @return double 報酬
     */
    public double getReward() {
        String rewardStr = map.getReward(robotState[0],
                robotState[1]);
        int reward = 0;
        if(!rewardStr.equals("")) {
            reward = Integer.parseInt(rewardStr);
        }
        return reward;
    }

    /**
     * カギを取得しているか確認します。
     * @param boolean  true:カギを持っている false:カギを持っていない
     */
//    public boolean hasKey() {
//        return key;
//    }
    // 2001.08.08 追加 miyamoto
    /**
     * 保持しているものを取得
     * @return 
     */
    public int getItem() {
//        return item;
        return robotState[2];
    }

    /**
     * カギをなくします。
     */
//    public void clearKey() {
//        key = false;
//    }
    public void clearItem() {
//        item = NOTHING;
        robotState[2] = NOTHING;
    }

    /**
     * ドアが開いているか確認します。
     * @param boolean  true:ドアが開いている false:ドアが閉じている
     */
    public boolean isDoorOpen() {
        // 2001.08.08 修正 miyamoto
//        return key;
//        if(item == KEY ) {
        if(robotState[2] == KEY ) {
            return true;
        }
        return false;
    }

    // 2001.08.03 追加 miyamoto
    /**
     * 開いているドアを閉じる
     */
    public void closeDoor() {
//    private void closeDoor() {
        /*
         * 地図上の全位置をチェック
         * 閉じているドア("D")があれば開いているドア("d")に換える
         */
        int[] size = map.getSize();
        for(int x = 0; x < size[0]; x++) {
            for(int y = 0; y < size[1]; y++) {
                if( (map.getString(x, y)).equals("d") ) {
                    map.set(x, y, "D");
                }
            }
        }
    }

    /**
     * 地図上の指定された位置の情報を取得します。
     * @param int x  x座標
     * @param int y  y座標
     */
    public String getMapInfo(int x, int y) {
        return map.getString(x, y);
    }

    public String getColorInfo(int x, int y) {
        if(colorMap == null) {
            return null;
        }
        return colorMap[x][y];
    }

    /**
     * 地図のサイズを取得します。
     * @return int[] int[0] x軸方向のサイズ
     *               int[0] y軸方向のサイズ
     */
    public int[] getMapSize() {
        return map.getSize();
    }

    public boolean isCollisionDoor() {
        return actionController.isCollisionDoor();
    }

    /**
     * 報酬のテーブルを現在の地図に合わせて更新します。
     */
    public void renewReward() {
        if( rewardMap != null) {
            int[] size = map.getSize();
            for(int x = 0; x < size[0]; x++) {
                for(int y = 0; y < size[1]; y++) {
                    String rewardStr = map.getReward(x, y);
                    rewardMap[x][y] = rewardStr;
                }
            }
        }
    }

    /**
     * 引数で設定された位置にカギがあるかチェックし、
     * カギがあれば閉じているドアを開きます。
     * @param int x x座標
     * @param int y y座標
     */
    private void checkItem(int x, int y) {
        /* カギの場所 */
        if( (map.getString(x, y)).equals("K") ) {
            /*
             * カギがなければ、カギ取得・ドア開く
             * カギがあれば、カギをなくし・ドアを閉じる
             */
            if(robotState[2] != KEY ) {
                robotState[2] = KEY;
            }else {
                robotState[2] = NOTHING;
            }
        }
        /* 電話の場所 */
        if( (map.getString(x, y)).equals("T") ) {
            /*
             * 電話がなければ、電話取得・ドア閉じる
             * 電話があれば、電話をなくす
             */
            if(robotState[2] != TELEPHON ) {
                robotState[2] = TELEPHON;
//                closeDoor();
            }else {
                robotState[2] = NOTHING;
            }
        }
        // アイテムを増やした場合用
        if((map.getString(x, y)).equals("A")) {
            if(robotState[2] != A ) {
                robotState[2] = A;
            }else {
                robotState[2] = NOTHING;
            }
        }
        if((map.getString(x, y)).equals("B")) {
            if(robotState[2] != B ) {
                robotState[2] = B;
            }else {
                robotState[2] = NOTHING;
            }
        }
        if((map.getString(x, y)).equals("a")) {
            if(robotState[2] != a ) {
                robotState[2] = a;
            }else {
                robotState[2] = NOTHING;
            }
        }
        if((map.getString(x, y)).equals("b")) {
            if(robotState[2] != b ) {
                robotState[2] = b;
            }else {
                robotState[2] = NOTHING;
            }
        }
        // ここまで
        controlDoor(robotState[2]);
    }

    /* ドアを開けるアイテム */
    private int doorOpenItem = NOTHING;
    /**
     * ドアを開けるアイテムを変更します。
     * NOTHING = 0;
     * KEY = 1;
     * TELEPHON = 2;
     */
    public void changeDoorOpenItem(int doorOpenItem) {
        this.doorOpenItem = doorOpenItem;
    }

    /**
     * アイテムによってドアの制御を行います。
     */
    private void controlDoor(int item) {
        if(item == doorOpenItem) {
            openDoor();
        }else {
            closeDoor();
        }
    }


    /**
     * 閉じているドアを開ける
     */
    private void openDoor() {
        /*
         * 地図上の全位置をチェック
         * 閉じているドア("D")があれば開いているドア("d")に換える
         */
        int[] size = map.getSize();
        for(int x = 0; x < size[0]; x++) {
            for(int y = 0; y < size[1]; y++) {
                if( (map.getString(x, y)).equals("D") ) {
                    map.set(x, y, "d");
                }
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getPreferredTitle() {
        return "Environment";
    }

    @Override
    public Set<String> getViewerPanelRoles() {
        return roles;
    }

    @Override
    public void iterationStarted(IterationEvent evt) {
    }

    @Override
    public void iterationEnded(IterationEvent evt) {
        if (evt.isAchieved())
            flash();
    }
}



