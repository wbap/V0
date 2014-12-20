public class Coord {
    public int x;
    public int y;

    Coord() {
        set(-1, -1);
    }

    Coord(int _x, int _y) {
        set(_x, _y);
    }

    void set(int _x, int _y) {
        x = _x;
        y = _y;
    }
}
