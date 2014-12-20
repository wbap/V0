package wba.rogue;

public class Avatar {
    private Rogue environment;
    private int[] state;

    Avatar() {
        environment = new Rogue();
    }

    int[] move(int direction) {
        return environment.move(direction);
    }
}
