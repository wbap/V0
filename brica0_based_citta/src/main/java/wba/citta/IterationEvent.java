package wba.citta;

public class IterationEvent extends StatusEvent {
    private static final long serialVersionUID = 1L;

    final int iteration;
    final int goalCount;
    final boolean achieved;

    public IterationEvent(CittaRunner source, int iteration, int goalCount, boolean achieved) {
        super(source);
        this.iteration = iteration;
        this.goalCount = goalCount;
        this.achieved = achieved;
    }

    public int getIteration() {
        return iteration;
    }

    public int getGoalCount() {
        return goalCount;
    }

    public boolean isAchieved() {
        return achieved;
    }
}
