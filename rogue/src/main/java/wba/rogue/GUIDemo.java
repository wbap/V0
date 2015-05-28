package wba.rogue;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class RoguePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    BufferedImage fb;
    GraphicsRenderer gr;
    final Rogue rogue;

    static int keyCodeToDirection(int keyCode) {
        int direction = 0;
        switch (keyCode) {
        case KeyEvent.VK_H:                    
            direction = 3;
            break;
        case KeyEvent.VK_J:
            direction = 7;
            break;
        case KeyEvent.VK_K:
            direction = 1;
            break;
        case KeyEvent.VK_L:
            direction = 5;
            break;
        }
        return direction;
    }
    public RoguePanel(final Rogue rogue) {
        super();
        this.rogue = rogue;
        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        resetFramebuffer();                        
                        renderToFramebuffer();                        
                    }
                });
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                componentResized(arg0);
            }
        });
        
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                final int direction = keyCodeToDirection(arg0.getKeyCode());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (direction != 0)
                            RoguePanel.this.rogue.move(direction);
                        renderToFramebuffer();                        
                    }
                });
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {                
            }
        });
        enableEvents(KeyEvent.KEY_EVENT_MASK);
    }

    public void resetFramebuffer() {
        final Dimension d = getSize();
        if (d.getWidth() > 0 && d.getHeight() > 0) {
            final BufferedImage newFb = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
            final GraphicsRenderer newGr = new GraphicsRenderer(
                new Point2D.Double(
                    d.getWidth(),
                    d.getHeight()
                ),
                rogue.getSize()
            );
            synchronized (this) {
                this.fb = newFb;
                this.gr = newGr;
            }
        }
    }

    public void renderToFramebuffer() {
        if (gr == null || fb == null)
            return;
        Graphics2D g = (Graphics2D)fb.getGraphics();
        assert g != null;
        try {
            gr.render(g, rogue);
        } finally {
            g.dispose();
        }
    }

    public void paintComponent(Graphics g) {
        BufferedImage fb;
        synchronized (this) {
            fb = this.fb;
        }
        g.drawImage(fb, 0, 0, this);        
    }
}

public class GUIDemo {
    final static int NUMCOLS = 80;
    final static int NUMLINES = 24;
    final static int MAXROOMS = 9;

    JFrame mainWindow;
    Rogue rogue;
    RoguePanel panel;

    public GUIDemo(Rogue rogue) {
        this.rogue = rogue;
    }
    
    private void initialize() {
        mainWindow = new JFrame(GUIDemo.class.getName());
        mainWindow.setSize(80 * 10, 24 * 20);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new RoguePanel(rogue);
        mainWindow.getContentPane().add(panel);
        panel.setFocusable(true);
    }

    public static void main(String[] args) {
        final RNG rng = new RNG();
        Rogue rogue = new Rogue(new Coord(NUMCOLS, NUMLINES), MAXROOMS, rng);
        GUIDemo demo = new GUIDemo(rogue);
        demo.initialize();
        demo.mainWindow.setVisible(true);
    }
}
