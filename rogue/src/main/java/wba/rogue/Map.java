package wba.rogue;

public class Map implements Cloneable {
    final Coord size;
    
    private Place[] map;

    Map(Coord size) {
        this.size = size;
        map = new Place[size.x * size.y];
        reset();
    }

    public Object clone() {
        Map cloned = new Map(size);
        System.arraycopy(map, 0, cloned.map, 0, map.length); 
        return cloned;
    }
    
    void reset() {
        for(int x = 0; x < size.x; ++x) {
            for(int y = 0; y < size.y; ++y) {
                map[x + y * size.x] = new Place(' ');
            }
        }
    }

    Place getPlace(int x, int y) {
        return map[x + y * size.x];
    }

    void setPlace(int x, int y, char type) {
        map[x + y * size.x].type = type;
    }

    void setPlace(int x, int y, int value) {
        map[x + y * size.x].value = value;
    }

    Place getPlace(Coord coord) {
        int x = coord.x;
        int y = coord.y;
        return map[x + y * size.x];
    }

    void setPlace(Coord coord, char type) {
        int x = coord.x;
        int y = coord.y;
        map[x + y * size.x].type = type;
    }

    void setPlace(Coord coord, int value) {
        int x = coord.x;
        int y = coord.y;
        map[x + y * size.x].value = value;
    }

    Coord getSize() {
        return size;
    }
}
