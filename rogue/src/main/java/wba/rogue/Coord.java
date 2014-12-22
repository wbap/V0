package wba.rogue;

public class Coord {
    public int x;
    public int y;

    public Coord() {
        set(-1, -1);
    }

    public Coord(int _x, int _y) {
        set(_x, _y);
    }

    public void set(int _x, int _y) {
        x = _x;
        y = _y;
    }
}
