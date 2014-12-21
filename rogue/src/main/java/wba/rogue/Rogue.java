package wba.rogue;

public class Rogue {
    private Coord size;
    private int maxRooms;
    private RNG rng;

    private int level;

    private Map map;
    private Rooms rooms;

    private Coord coord;
    private Map visible;
    private int action;
    private int hunger;
    private int key;
    private int gold;

    private Room current;

    Rogue(Coord size, int maxRooms, RNG rng) {
        hunger = 1000;
        gold = 0;
        this.size = size;
        this.maxRooms = maxRooms;
        this.rng = rng;
        newLevel();
    }

    void newLevel() {
        visible = new Map(size);
        map = new Map(size);
        rooms = new Rooms(map, maxRooms, level, rng);

        key = 0;

        current = rooms.rndRoom();
        coord = current.findFloor(map);

        System.err.println("Initial position: (" + coord.x + ", " + coord.y + ")");

        rooms.drawRooms(map);
        current.drawRoom(visible);
    }

    public Coord getCoord() {
        return coord;
    }

    public int getHunger() {
        return hunger;
    }

    public int getKey() {
        return key;
    }

    public int getGold() {
        return gold;
    }

    public Map getVisible() {
        return visible;
    }

    public Map getMap() {
        return map;
    }

    public Coord getSize() {
        return size;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public int[] toStateArray() {
        int i = 0;
        final Coord size = map.getSize();
        final int[] state = new int[size.y * size.x + 4];

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = visible.getPlace(x, y);
                state[i++] = (int)tmpPlace.type;
            }
        }

        state[i++] = action;
        state[i++] = hunger;
        state[i++] = gold;
        state[i++] = key;

        return state;
    }
    
    public int[] move(int direction) {
        visible.reset();

        Coord newCoord = new Coord();

        newCoord.x = coord.x;
        newCoord.y = coord.y;
        newCoord.x += ((direction % 3) - 1);
        newCoord.y += ((direction / 3) - 1);

        action = direction;
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

        rooms.drawRooms(map);
        return toStateArray();
    }
}
