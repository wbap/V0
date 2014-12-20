package wba.rogue;

public class Rooms {
    private Room[] rooms;
    private int NUMCOLS;
    private int NUMLINES;
    private int MAXROOMS;
    private RNG rng;
    private Map map;

    Rooms(Map _map, int numcols, int numlines, int maxrooms, int level) {
        map = _map;
        NUMCOLS = numcols;
        NUMLINES = numlines;
        MAXROOMS = maxrooms;
        rooms = new Room[MAXROOMS];

        /* Initialize rooms */
        for(int i = 0; i < MAXROOMS; ++i) {
            rooms[i] = new Room();
        }

        rng = new RNG();

        /* Put the gone rooms */
        int goners = rng.nextInt(4);
        for(int i = 0; i < goners; ++i) {
            //int index = rndRoom();
            //rooms[index].setGone();
            rndRoom().setGone();
            //System.err.println("Room " + index + " is gone!");
        }

        /* Dig and populate rooms */
        for(int i = 0; i < MAXROOMS; ++i) {
            rooms[i].dig(map, i, NUMCOLS, NUMLINES, MAXROOMS, level);
        }

        /* Connect the rooms */
        System.err.println("Connect rooms: first pass");
        int index1 = rng.nextInt(MAXROOMS);
        int index2 = 0;
        int roomcount = 1;
        rooms[index1].setInGraph();
        do {
            int j = 0;
            for(int i = 0; i < MAXROOMS; ++i) {
                if(rooms[index1].canConnTo(i)) {
                    if(!rooms[i].isInGraph()) {
                        if(rng.nextInt(++j) == 0) {
                            index2 = i;
                        }
                    }
                }
            }

            if(j == 0) {
                do {
                    index1 = rng.nextInt(MAXROOMS);
                } while(!(rooms[index1].isInGraph()));
            } else {
                rooms[index2].setInGraph();
                rooms[index1].connTo(index2);
                rooms[index2].connTo(index1);
                connect(index1, index2);
                roomcount++;
            }
        } while(roomcount < MAXROOMS);

        for(roomcount = rng.nextInt(5); roomcount > 0; --roomcount) {
            System.err.println("Connect rooms: extra pass: " + roomcount);
            index1 = rng.nextInt(MAXROOMS);
            int j = 0;
            for(int i = 0; i < MAXROOMS; ++i) {
                if(rooms[index1].canConnTo(i)) {
                    if(!rooms[index1].isConnTo(i)) {
                        if(rng.nextInt(++j) == 0) {
                            index2 = i;
                        }
                    }
                }
            }

            if(j != 0) {
                rooms[index2].setInGraph();
                rooms[index1].connTo(index2);
                rooms[index2].connTo(index1);
                connect(index1, index2);
            }
        }

        rndRoom().putStairs(map);
        rndRoom().putKey(map);
    }

    void connect(int index1, int index2) {
        System.err.println("Connect " + index1 + " to " + index2);

        int distance = 0;
        int turn_spot;
        int turn_distance;
        boolean direction = true; // true: down, false: right
        int indexf, indext;
        Coord delta, curr, turn_delta, sdoor, edoor;

        if(index1 < index2) {
            indexf = index1;
            indext = index2;
            if(index1 + 1 == index2) {
                direction = false;
            }
        } else {
            indexf = index2;
            indext = index1;
            if(index2 + 1 == index1) {
                direction = false;
            }
        }

        System.err.println("From: " + indexf + " To: " + indext + " Direction: " + (direction ? "Down" : "Right"));

        if(direction) {
            Place place;
            delta = new Coord(0, 1);
            sdoor = rooms[indexf].doorPos(map, direction, false);
            edoor = rooms[indext].doorPos(map, direction, true);
            if(!rooms[indexf].isGone()) {
                map.setPlace(sdoor, '+');
            } else {
                map.setPlace(sdoor, '#');
            }
            if(!rooms[indext].isGone()) {
                map.setPlace(edoor, '+');
            } else {
                map.setPlace(edoor, '#');
            }
            distance = Math.abs(sdoor.y - edoor.y) - 1;
            turn_delta = new Coord((sdoor.x < edoor.x ? 1 : -1), 0);
            turn_distance = Math.abs(sdoor.x - edoor.x);
        } else {
            Place place;
            delta = new Coord(1, 0);
            sdoor = rooms[indexf].doorPos(map, direction, false);
            edoor = rooms[indext].doorPos(map, direction, true);
            if(!rooms[indexf].isGone()) {
                map.setPlace(sdoor, '+');
            } else {
                map.setPlace(sdoor, '#');
            }
            if(!rooms[indext].isGone()) {
                map.setPlace(edoor, '+');
            } else {
                map.setPlace(edoor, '#');
            }
            distance = Math.abs(sdoor.x - edoor.x) - 1;
            turn_delta = new Coord(0, (sdoor.y < edoor.y ? 1 : -1));
            turn_distance = Math.abs(sdoor.y - edoor.y);
        }

        if(distance == 1) {
            turn_spot = 1;
        } else {
            turn_spot = rng.nextInt(distance - 1) + 1;
        }

        curr = new Coord(sdoor.x, sdoor.y);

        while(distance > 0) {
            curr.x += delta.x;
            curr.y += delta.y;

            if(distance == turn_spot) {
                while(turn_distance-- > 0) {
                    map.setPlace(curr, '#');
                    curr.x += turn_delta.x;
                    curr.y += turn_delta.y;
                }
            }

            map.setPlace(curr, '#');
            distance--;
        }
    }

    Room rndRoom() {
        int i;

        do {
            i = rng.nextInt(MAXROOMS);
        } while(rooms[i].isGone());

        return rooms[i];
    }

    void drawRooms(Map map) {
        for(int i = 0; i < MAXROOMS; ++i) {
            if(!rooms[i].isGone()) {
                rooms[i].drawRoom(map);
            }
        }
    }

    boolean isInAnyRoom(Coord coord) {
        for(int i = 0; i < MAXROOMS; ++i) {
            if(rooms[i].isInRoom(coord)) {
                return true;
            }
        }

        return false;
    }

    Room isInRoom(Coord coord) {
        Room room = new Room();

        for(int i = 0; i < MAXROOMS; ++i) {
            if(rooms[i].isInRoom(coord)) {
                room = rooms[i];
            }
        }

        return room;
    }
}
