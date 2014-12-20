public class Map {
    final private int NUMCOLS;
    final private int NUMLINES;

    private Place map[];

    Map(int numcols, int numlines) {
        NUMCOLS = numcols;
        NUMLINES = numlines;
        map = new Place[NUMCOLS * NUMLINES];
        for(int x = 0; x < NUMCOLS; ++x) {
            for(int y = 0; y < NUMLINES; ++y) {
                map[x + y * NUMCOLS] = new Place(' ');
            }
        }
    }

    Place getPlace(int x, int y) {
        return map[x + y * NUMCOLS];
    }

    void setPlace(int x, int y, char type) {
        map[x + y * NUMCOLS].type = type;
    }

    void setPlace(int x, int y, int value) {
        map[x + y * NUMCOLS].value = value;
    }

    Place getPlace(Coord coord) {
        int x = coord.x;
        int y = coord.y;
        return map[x + y * NUMCOLS];
    }

    void setPlace(Coord coord, char type) {
        int x = coord.x;
        int y = coord.y;
        map[x + y * NUMCOLS].type = type;
    }

    void setPlace(Coord coord, int value) {
        int x = coord.x;
        int y = coord.y;
        map[x + y * NUMCOLS].value = value;
    }

    void print() {
        for(int y = 0; y < NUMLINES; ++y) {
            for(int x = 0; x < NUMCOLS; ++x) {
                System.out.print(map[x + y * NUMCOLS].type);
            }
            System.out.println("");
        }
    }
}
