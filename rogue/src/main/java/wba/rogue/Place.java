package wba.rogue;

public class Place {
    public char type;
    public int value;

    public Place(char _type) {
        type = _type;
        value = 0;
    }

    public Place(char _type, int _value) {
        type = _type;
        value = _value;
    }
}
