package wba.rogue;

public class Avatar {
    private Rogue environment;
    private int[] state;

    Avatar(Rogue environment) {
        this.environment = environment;
    }

    public int[] getState() {
        return state;
    }

    public int[] move(int direction) {
        state = environment.move(direction);
        return state;
    }
}
