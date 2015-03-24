package wba.rogue;

import wba.citta.gsa.Goal;

public class Avatar {
    private Rogue environment;
    private Goal state;

    public Avatar(Rogue environment) {
        this.environment = environment;
    }

    public Goal getState() {
        return state;
    }

    public Goal getGoal() {
        return environment.toGoalArray();
    }

    public Goal getVisibleGoal() {
        return environment.visibleGoal();
    }

    public Goal getReal() {
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

    public Goal move(int direction) {
        return environment.move(direction);
    }
}
