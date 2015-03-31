/**
 * MessageCanvas.java
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.doorkeydemo.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

/**
 *  環境グラフィック処理を行うクラスです
 */
public class MessageCanvas extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* ダブルバッファリング用 オフスクリーンイメージ */
    private Image offImage;

    private String message = "";
    private int xMargin = 0;
    private int yMargin = 0;

    private Image image = null;
    private boolean dirty = true;

    private Font font = new Font("Dialog", Font.BOLD, 20);
    private Dimension bounds;

    ////////////////////////////////////////////////////////////
    // コンストラクタ  初期化処理

    /**
     * コンストラクタ
     */
    public MessageCanvas(int x, int y) {
        super();
        /* 表示位置 */
        xMargin = x;
        yMargin = y;
        addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent arg0) { }

            @Override
            public void componentMoved(ComponentEvent arg0) { }

            @Override
            public void componentResized(ComponentEvent arg0) { }

            @Override
            public void componentShown(ComponentEvent arg0) {
                dirty = true;
                updateIfNecessary();
            }
        });
    }

    ////////////////////////////////////////////////////////////
    // public 

    public void setMessage(String message) {
        this.message = message;
        this.dirty = true;
        updateIfNecessary();
    }

    public void setImage(Image image) {
        this.image = image;
        dirty = true;
        updateIfNecessary();
    }

    void updateIfNecessary() {
        if (!dirty)
            return;
        final FontMetrics fm = getFontMetrics(font);
        bounds = new Dimension(
            fm.stringWidth(message),
            fm.getHeight()
        );
        Dimension d = new Dimension(
            xMargin * 2 + bounds.width + 10 + 20,
            yMargin * 2 + bounds.height
        );
        setMinimumSize(d);
        setPreferredSize(d);
        d = getSize();
        if (offImage == null || d.width != offImage.getWidth(this) || d.height != offImage.getHeight(this)) {
            offImage = createImage(d.width, d.height);
        }
        if (offImage != null) {
            drawOffImage();
        }
        dirty = false;
    }

    /**
     * paintメソッドのオーバーライド
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
        final Graphics2D graphics = (Graphics2D)offImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            graphics.setColor(Color.orange);
            graphics.fillRect(0, 0, offImage.getWidth(this), offImage.getHeight(this));
    
            graphics.setFont(font);
            graphics.setColor(Color.black);
            graphics.drawString(message, xMargin, yMargin + bounds.height);
            if(image != null) {
                graphics.drawImage(image, xMargin + bounds.width + 10, offImage.getHeight(this) - yMargin - image.getHeight(this), this);
            }
        } finally {
            graphics.dispose();
        }
    }
}


