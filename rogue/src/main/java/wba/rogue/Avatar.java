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

    public int[] getGoal() {
        return environment.toGoalArray();
    }

    public int[] getReal() {
        return environment.toRealArray();
    }

    public boolean checkGoal() {
        return environment.checkGoal();
    }

    public void restart() {
        environment.newLevel();
    }

    public int[] move(int direction) {
        state = environment.move(direction);
        return state;
    }
}
