package wba.rogue;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.Graphics2D;
import java.util.HashMap;

public class GraphicsRenderer {
    Point2D size;
    Coord mapSize;
    Font f;
    Color foregroundColor;
    Color backgroundColor;
    
    public GraphicsRenderer(Point2D size, Coord mapSize) {
        assert size != null && mapSize != null;
        this.size = size;
        this.mapSize = mapSize;
        this.f = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        this.foregroundColor = Color.BLACK;
        this.backgroundColor = Color.WHITE;
    }

    public void render(Graphics2D g, Rogue r) {
        int key, gold, hunger;
        Coord coord;
        Map visible;
        synchronized (r) {
            key = r.getKey();
            gold = r.getGold();
            hunger = r.getHunger();
            coord = r.getCoord();
            visible = (Map)r.getVisible().clone();
        }
        new Instance(g).render(key, gold, hunger, coord, visible);
    }

    class Instance {
        Graphics2D g;
        java.util.Map<Integer, GlyphVector> vectors = new HashMap<Integer, GlyphVector>();
        Font f;
        FontRenderContext frc;
        float cWidth, cHeight;

        public Instance(Graphics2D g) {
            this.g = g;
            this.f = GraphicsRenderer.this.f.deriveFont((float)size.getY() / mapSize.y); // XXX
            this.frc = g.getFontRenderContext();
            final GlyphVector gv = getGlyphVectorFor((int)'#');
            final GlyphMetrics gm = gv.getGlyphMetrics(0);
            this.cWidth = gm.getAdvanceX();
            this.cHeight = (float)gm.getBounds2D().getHeight();
        }

        public GlyphVector getGlyphVectorFor(int c) {
            GlyphVector gv = vectors.get(c);
            if (gv == null) {
                gv = this.f.createGlyphVector(frc, new char[] { (char)c });
                vectors.put(c, gv);
            }
            return gv;
        }

        void clearChar(int x, int y) {
            g.clearRect((int)this.cWidth * x, (int)this.cHeight * y, (int)this.cWidth, (int)this.cHeight);
        }

        void drawChar(int x, int y, Place p) {
            if (p.type == ' ')
                return;
            final GlyphVector gv = getGlyphVectorFor(p.type);
            g.setColor(foregroundColor);
            g.drawGlyphVector(gv, this.cWidth * x, this.cHeight * y + this.cHeight);
        }

        void drawAvatar(int x, int y) {
            final GlyphVector gv = getGlyphVectorFor((int)'@');
            g.setColor(foregroundColor);
            g.drawGlyphVector(gv, this.cWidth * x, this.cHeight * y + this.cHeight);
        }

        void render(int key, int gold, int hunger, Coord coord, Map visible) {
            g.setBackground(backgroundColor);
            g.clearRect(0, 0, (int)size.getX(), (int)size.getY());
            final Coord size = visible.getSize();
            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                    final Place p = visible.getPlace(x, y);
                    drawChar(x, y, p);
                }
            }
            clearChar(coord.x, coord.y);
            drawAvatar(coord.x, coord.y);
        }
    }
}

