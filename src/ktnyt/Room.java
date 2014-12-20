public class Room {
    private Coord pos;
    private Coord max;
    private Coord gold;
    private Coord key;
    private Coord stairs;

    private int goldval;
    private int nexits;
    private Coord[] exit;

    private boolean haskey;
    private boolean hasstairs;

    private boolean dark;
    private boolean gone;
    private boolean maze;

    private boolean[] conn;
    private boolean[] isconn;
    private boolean ingraph;

    private RNG rng;

    Room() {
        pos = new Coord(0, 0);
        max = new Coord(0, 0);
        gold = new Coord(0, 0);
        goldval = 0;
        nexits = 0;
        exit = new Coord[4];
        hasstairs = false;
        dark = false;
        gone = false;
        maze = false;
        ingraph = false;

        rng = new RNG();
    }

    void dig(Map map, int index, int numcols, int numlines, int maxrooms, int level) {
        Coord bsze = new Coord(numcols / 3, numlines / 3);
        Coord top = new Coord((index % 3) * bsze.x + 1, (index / 3) * bsze.y);

        conn = new boolean[maxrooms];
        isconn = new boolean[maxrooms];

        for(int j = 0; j < 3; ++j) {
            for(int i = 0; i < 3; ++i) {
                conn[j * 3 + i] = false;
                isconn[j * 3 + i] = false;
                int x = index % 3;
                int y = index / 3;
                if(x - 1 == i && y == j) {
                    conn[j * 3 + i] = true;
                }
                if(x + 1 == i && y == j) {
                    conn[j * 3 + i] = true;
                }
                if(x == i && y - 1 == j) {
                    conn[j * 3 + i] = true;
                }
                if(x == i && y + 1 == j) {
                    conn[j * 3 + i] = true;
                }
            }
        }

        if(isGone()) {
            System.err.println("Room: " + index + " is gone!");
            do {
                pos.x = top.x + rng.nextInt(bsze.x - 2) + 1;
                pos.y = top.y + rng.nextInt(bsze.y - 2) + 1;
                max.x = -numcols;
                max.y = -numlines;
            } while(!(0 < pos.y && pos.y < numlines - 1));
            return;
        }

        if(rng.nextInt(10) < level - 1) {
            setDark();
            if(rng.nextInt(15) == 0) {
                setMaze();
            }
        }

        if(isMaze()) {
            System.err.println("Room: " + index + " is a maze!");
            max.x = bsze.x - 1;
            max.y = bsze.y - 1;

            if((pos.x = top.x) == 1) {
                pos.x = 0;
            }

            if((pos.y = top.y) == 0) {
                pos.y++;
                max.y--;
            }
        } else {
            do {
                max.x = rng.nextInt(bsze.x - 4) + 4;
                max.y = rng.nextInt(bsze.y - 4) + 4;
                pos.x = top.x + rng.nextInt(bsze.x - max.x);
                pos.y = top.y + rng.nextInt(bsze.y - max.y);
            } while(pos.y == 0);
        }

        System.err.println("Room " + index + " Pos(" + pos.x + ", " + pos.y + ") Size(" + max.x + ", " + max.y + ")");

        drawRoom(map);

        if(rng.nextInt(2) == 0) {
            gold = findFloor(map);
            int value = rng.nextInt(50 + 10 * level) + 2;
            map.setPlace(gold, '*');
            map.setPlace(gold, value);
            goldval = value;
        }
    }

    void drawRoom(Map map) {
        drawVertical(map, pos.x);
        drawVertical(map, pos.x + max.x - 1);
        drawHorizontal(map, pos.y);
        drawHorizontal(map, pos.y + max.y - 1);
        drawFloor(map);
        drawDoors(map);
        if(goldval > 0) {
            map.setPlace(gold, '*');
        }
        if(hasstairs) {
            map.setPlace(stairs, '%');
        }
        if(haskey) {
            map.setPlace(key, '&');
        }
    }

    void drawFloor(Map map) {
        if(dark || gone || maze) {
            return;
        }

        for(int x = pos.x + 1; x < pos.x + max.x - 1; ++x) {
            for(int y = pos.y + 1; y < pos.y + max.y - 1; ++y) {
                map.setPlace(x, y, '.');
            }
        }
    }

    void drawVertical(Map map, int startx) {
        for(int y = pos.y + 1; y <= pos.y + max.y - 1; ++y) {
            map.setPlace(startx, y, '|');
        }
    }

    void drawHorizontal(Map map, int starty) {
        for(int x = pos.x; x <= pos.x + max.x - 1; ++x) {
            map.setPlace(x, starty, '-');
        }
    }

    void drawDoors(Map map) {
        for(int i = 0; i < nexits; ++i) {
            map.setPlace(exit[i], '+');
        }
    }

    Coord findFloor(Map map) {
        char compchar = maze ? '#' : dark ? ' ' : '.';
        Coord floor;
        do {
            floor = rndPos();
        } while(map.getPlace(floor).type != compchar);
        return floor;
    }

    Coord rndPos() {
        int x = pos.x + rng.nextInt(max.x - 2) + 1;
        int y = pos.y + rng.nextInt(max.y - 2) + 1;
        return new Coord(x, y);
    }

    Coord doorPos(Map map, boolean direction, boolean destination) {
        Coord tmp = new Coord(pos.x, pos.y);
        Place place;

        if(!gone) {
            if(direction) {
                if(!destination) {
                    do {
                        tmp.x = pos.x + rng.nextInt(max.x - 2) + 1;
                        tmp.y = pos.y + max.y - 1;
                        place = map.getPlace(tmp.x, tmp.y);
                    } while(maze && place.type != '#');
                } else {
                    do {
                        tmp.x = pos.x + rng.nextInt(max.x - 2) + 1;
                        place = map.getPlace(tmp.x, tmp.y);
                    } while(maze && place.type != '#');
                }
            } else {
                if(!destination) {
                    do {
                        tmp.x = pos.x + max.x - 1;
                        tmp.y = pos.y + rng.nextInt(max.y - 2) + 1;
                        place = map.getPlace(tmp.x, tmp.y);
                    } while(maze && place.type != '#');
                } else {
                    do {
                        tmp.y = pos.y + rng.nextInt(max.y - 2) + 1;
                        place = map.getPlace(tmp.x, tmp.y);
                    } while(maze && place.type != '#');
                }
            }

            exit[nexits++] = tmp;
        }

        return tmp;
    }

    Coord roomPos() {
        return pos;
    }

    Coord roomSize() {
        return max;
    }

    void setDark() {
        dark = true;
    }

    void setGone() {
        gone = true;
    }

    void setMaze() {
        maze = true;
    }

    boolean isDark() {
        return dark;
    }

    boolean isGone() {
        return gone;
    }

    boolean isMaze() {
        return maze;
    }

    int getGold() {
        int tmp = goldval;
        goldval = 0;
        return tmp;
    }

    void getKey() {
        haskey = false;
    }

    void putStairs(Map map) {
        hasstairs = true;
        stairs = findFloor(map);
    }

    void putKey(Map map) {
        haskey = true;
        key = findFloor(map);
    }

    boolean canConnTo(int index) {
        return conn[index];
    }

    boolean isConnTo(int index) {
        return isconn[index];
    }

    boolean connTo(int index) {
        if(canConnTo(index)) {
            isconn[index] = true;
            return true;
        }
        return false;
    }

    void setInGraph() {
        ingraph = true;
    }

    boolean isInGraph() {
        return ingraph;
    }

    boolean isInRoom(Coord coord) {
        int x = coord.x;
        int y = coord.y;

        if(pos.x <= x && x <= pos.x + max.x - 1 && pos.y <= y && y <= pos.y + max.y - 1) {
            return true;
        }

        return false;
    }
}
