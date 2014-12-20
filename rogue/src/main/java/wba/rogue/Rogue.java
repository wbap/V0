package wba.rogue;

public class Rogue {
    final private int NUMCOLS = 80;
    final private int NUMLINES = 24;
    final private int MAXROOMS = 9;

    private int level;

    private Map map;
    private Rooms rooms;

    private Coord coord;
    private Map visible;
    private int hunger;
    private int key;
    private int gold;

    private Room current;

    private int[] state;

    Rogue() {
        hunger = 1000;
        gold = 0;
        newLevel();
    }

    void newLevel() {
        map = new Map(NUMCOLS, NUMLINES);
        visible = new Map(NUMCOLS, NUMLINES);
        rooms = new Rooms(map, NUMCOLS, NUMLINES, MAXROOMS, level);

        key = 0;

        current = rooms.rndRoom();
        coord = current.findFloor(map);

        System.err.println("Initial position: (" + coord.x + ", " + coord.y + ")");

        rooms.drawRooms(map);

        map.print();

        current.drawRoom(visible);
        visible.setPlace(coord, '@');
        visible.print();

        state = new int[NUMCOLS * NUMLINES + 3];
    }

    int[] move(int direction) {
        visible = new Map(NUMCOLS, NUMLINES);

        Coord newCoord = new Coord();

        newCoord.x = coord.x;
        newCoord.y = coord.y;
        newCoord.x += ((direction % 3) - 1);
        newCoord.y += ((direction / 3) - 1);

        hunger--;

        Place newPlace = map.getPlace(newCoord);

        boolean flag = true;
        if(newPlace.type == '|' || newPlace.type == '-') {
            flag = false;
        }

        if(!rooms.isInAnyRoom(newCoord)) {
            if(newPlace.type == ' ') {
                flag = false;
            }
        }

        if(hunger == 0) {
            flag = false;
        }

        if(flag) {
            coord = newCoord;
        }

        if(rooms.isInAnyRoom(coord)) {
            current = rooms.isInRoom(coord);
            if(!current.isDark()) {
                current.drawRoom(visible);
            }

            if(newPlace.type == '*') {
                gold += current.getGold();
                current.drawRoom(map);
            }

            if(newPlace.type == '&') {
                current.getKey();
                key = 1;
                current.drawRoom(map);
            }

            if(newPlace.type == '%') {
                if(key > 0) {
                    newLevel();
                }
            }
        }

        for(int dx = -1; dx < 2; ++dx) {
            for(int dy = -1; dy < 2; ++dy) {
                Coord tmpCoord = new Coord(coord.x + dx, coord.y + dy);
                Place tmpPlace = map.getPlace(tmpCoord);
                if(tmpPlace.type == '#' || tmpPlace.type == '+') {
                    visible.setPlace(tmpCoord, tmpPlace.type);
                }
            }
        }

        visible.setPlace(coord, '@');
        visible.print();

        int i = 0;

        for(int y = 0; y < NUMLINES; ++y) {
            for(int x = 0; x < NUMCOLS; ++x) {
                Place tmpPlace = visible.getPlace(x, y);
                state[i++] = (int)tmpPlace.type;
            }
        }

        rooms.drawRooms(map);

        state[i++] = hunger;
        state[i++] = gold;
        state[i++] = key;

        return state;
    }
}
