package wba.rogue;

public class Avatar {
    private Rogue environment;
    private int[] state;

    public Avatar(Rogue environment) {
        this.environment = environment;
    }

    public int[] getState() {
        return state;
    }

    public int[] getGoal() {
        return environment.toGoalArray();
    }

    public int[] getVisibleGoal() {
        return environment.visibleGoal();
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

    public void printVisible() {
        environment.printVisible();
    }

    public int[] move(int direction) {
        state = environment.move(direction);
        return state;
    }
}
