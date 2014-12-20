package wba.rogue;

public class Avatar {
    private Rogue environment;

    Avatar(Rogue environment) {
        this.environment = environment;
    }

    int[] move(int direction) {
        return environment.move(direction);
    }
}
