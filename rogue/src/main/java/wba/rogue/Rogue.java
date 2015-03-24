package wba.rogue;

import wba.citta.gsa.Goal;

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

    private boolean test;

    public Rogue(Coord size, int maxRooms, RNG rng) {
        hunger = 1000;
        gold = 0;
        this.size = size;
        this.maxRooms = maxRooms;
        this.rng = rng;
        this.test = false;
        newLevel();
    }

    public Rogue(Coord size, int maxRooms, RNG rng, boolean test) {
        hunger = 1000;
        gold = 0;
        this.size = size;
        this.maxRooms = maxRooms;
        this.rng = rng;
        this.test = test;
        newLevel();
    }

    public void newLevel() {
        visible = new Map(size);
        map = new Map(size);

        if(test) {
            System.err.println("Test mode is on, resetting RNG");
            rng.reset();
        }

        rooms = new Rooms(map, maxRooms, level, rng);

        key = 0;

        if(test) {
            rng.setSeed(System.currentTimeMillis());
        }

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

    public void setSpawn(int i) {
        current = rooms.getRoom(i);
        coord = current.findFloor(map);
    }

    public Goal visibleGoal() {
        int i = 0;
        final Coord size = map.getSize();
        final Goal state = new Goal(size.y * size.x);
        Map tmpMap = new Map(size);
        rooms.roomWithStairs().drawRoom(tmpMap);

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = tmpMap.getPlace(x, y);
                char type = tmpPlace.type;
                if(type == '%') {
                    type = '@';
                }
                state.set(i++, (int)type);
            }
        }

        return state;
    }

    public Goal toGoalArray() {
        int i = 0;
        final Coord size = map.getSize();
        final Goal state = new Goal(size.y * size.x);

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = map.getPlace(x, y);
                char type = tmpPlace.type;
                if(type == '%') {
                    type = '@';
                }
                state.set(i++, (int)type);
            }
        }

        return state;
    }

    public Goal toRealArray() {
        int i = 0;
        final Coord size = map.getSize();
        final Goal state = new Goal(size.y * size.x);

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = map.getPlace(x, y);
                char type = tmpPlace.type;
                if(coord.x == x && coord.y == y) {
                    type = '@';
                }
                state.set(i++, (int)type);
            }
        }

        return state;
    }

    public boolean checkGoal() {
        Goal goal = toGoalArray();
        Goal real = toRealArray();
        for(int i = 0; i < goal.size(); ++i) {
            if(goal.get(i) != real.get(i)) {
                return false;
            }
        }
        return true;
    }

    public Goal toStateArray() {
        int i = 0;
        final Coord size = map.getSize();
        final Goal state = new Goal(size.y * size.x + 4);

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = visible.getPlace(x, y);
                state.set(i++, (int)tmpPlace.type);
            }
        }

        state.set(i++, action);
        state.set(i++, hunger);
        state.set(i++, gold);
        state.set(i++, key);

        return state;
    }
    
    public Goal move(int direction) {
        visible.reset();

        Coord newCoord = new Coord();

        direction = direction % 9;

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
                if(!test) {
                    gold += current.getGold();
                    current.drawRoom(map);
                }
            }

            if(newPlace.type == '&') {
                if(!test) {
                    current.getKey();
                    key = 1;
                    current.drawRoom(map);
                }
            }

            if(newPlace.type == '%') {
                if(!test) {
                    if(key > 0) {
                        newLevel();
                    }
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

    public void printVisible() {
        Coord size = visible.getSize();

        for(int y = 0; y < size.y; ++y) {
            for(int x = 0; x < size.x; ++x) {
                Place tmpPlace = visible.getPlace(x, y);
                char type = tmpPlace.type;
                System.out.print(type);
            }
            System.out.println();
        }
    }
}
