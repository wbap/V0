package wba.citta;


public class StepEvent extends StatusEvent {
    private static final long serialVersionUID = 1L;
    private final int stepCount;
    private final int goalStepCount;

    public StepEvent(CittaRunner source, int stepCount, int goalStepCount) {
        super(source);
        this.stepCount = stepCount;
        this.goalStepCount = goalStepCount;
    }

    public int getGoalStepCount() {
        return goalStepCount;
    }

    public int getStepCount() {
        return stepCount;
    }
}
