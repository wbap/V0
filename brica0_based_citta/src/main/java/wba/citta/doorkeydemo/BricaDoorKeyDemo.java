/**
 * DoorKeyDemo.java
 * Citta
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.doorkeydemo;

import java.awt.Color;
import java.io.*;
import java.util.*;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import wba.citta.CittaRunner;
import wba.citta.IterationEvent;
import wba.citta.IterationEventListener;
import wba.citta.StepCountViewer;
import wba.citta.StepEvent;
import wba.citta.LogViewer;
import wba.citta.StepEventListener;
import wba.citta.brica0.CognitiveArchitectureBuilder;
import wba.citta.brica0.GSARunner;
import wba.citta.doorkeydemo.environment.Disposition;
import wba.citta.doorkeydemo.environment.Environment;
import wba.citta.gsa.*;
import wba.citta.gsa.viewer.AgentViewer;
import wba.citta.gsa.viewer.SharedMemoryViewer;
import wba.citta.gsa.viewer.TreeViewer;
import wba.citta.gui.ViewerPanel;
import wba.citta.gui.ViewerPanelContainer;
import wba.citta.gui.ViewerPanelEvent;
import wba.citta.util.EventPublisherSupport;

/**
 * Citta
 */
public class BricaDoorKeyDemo extends JFrame implements CittaRunner {
    @SuppressWarnings("serial")
    public static class ApplicationException extends RuntimeException {
        public ApplicationException() {
            super();
        }

        public ApplicationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ApplicationException(String message) {
            super(message);
        }

        public ApplicationException(Throwable cause) {
            super(cause);
        }        
    }

    private static final Logger logger = LoggerFactory.getLogger(BricaDoorKeyDemo.class);
    private static final long serialVersionUID = 1L;
    /*  */
    private EnvironmentAdapter environmentAdapter = null;
    /* Citta */
    private GSARunner gsa = null;

    /*  */
    private DemoProperty props = null;

    /*  */
    private final int maxStepCount;
    private final int sleepTime;

    private int stepCount = 0;
    private int toGoalStepCount = 0;
    private int iteration = 0;
    private int goalCount = 0;

    private Goal goal = null;
    private Goal state = null;
    private Goal subgoal = null;

    private ViewerPanelContainer container;
    
    private final EventPublisherSupport<StepEvent, StepEventListener> stepEventListeners = new EventPublisherSupport<>(StepEvent.class, StepEventListener.class);
    private final EventPublisherSupport<IterationEvent, IterationEventListener> iterationEventListeners = new EventPublisherSupport<>(IterationEvent.class, IterationEventListener.class);

    public BricaDoorKeyDemo(String propFileName) throws Exception {
        props = new DemoProperty(propFileName);
        maxStepCount = props.getTimeOutStepNum();
        sleepTime = props.getSleepTime();
        initComponents();
        initEnvironmentAdapter();
        initLogViewer();
        initStepCountViewer();
        initGoal();
        initGSA();
        firePanelPopulated();
    }

    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(getClass().getName());
        container = new ViewerPanelContainer(this);
        getContentPane().add(container);
        setSize(720, 500);
        setVisible(true);
    }

    private void initGSA() throws IOException {
        final GSAProperty gsaProps = new GSAProperty(props.getGSAPropFileName());
        CognitiveArchitectureBuilder builder = new CognitiveArchitectureBuilder();
        builder.populateWithGSAProperty(gsaProps);
        gsa = builder.buildAgentModules();
        if (gsaProps.isShowAgentViewer()) {
            AgentViewer agentViewer = new AgentViewer(gsaProps.getColorTable(), Color.GRAY);
            agentViewer.bind(gsa);
            agentViewer.setSize(gsaProps.getAgentViewerInitSize());
            firePanelCreated(agentViewer);
        }
        if (gsaProps.isShowGoalStackViewer()) {
            SharedMemoryViewer sharedMemoryViewer = new SharedMemoryViewer(gsaProps.getColorTable());
            sharedMemoryViewer.bind((IListenableSharedMemory)gsa.getSharedMemory());
            sharedMemoryViewer.setSize(gsaProps.getSharedMemoryViewerInitSize());
            firePanelCreated(sharedMemoryViewer);
        }
        if (gsaProps.isShowFailAgentTreeViewer()) {
            TreeViewer treeViewer = new TreeViewer(gsaProps.getColorTable());
            treeViewer.bind(gsa.getFailAgentTree());
            firePanelCreated(treeViewer);
        }
        gsa.setGoal(goal);
        fireIterationStartedEvent();
    }


    private void initEnvironmentAdapter() throws IOException {
        final String fileName = props.getEnvFileName();
        logger.info(String.format("Loading map file %s", fileName));
        final Environment environment = new Environment(Disposition.load(fileName));
        environmentAdapter = new EnvironmentAdapter(
            environment, 
            props.getDoorOpenMode(),
            1
        );
        addIterationEventListener(environment);
        firePanelCreated(environment);
    }

    private void initLogViewer() {
        final ViewerPanel logViewer = new LogViewer();
        firePanelCreated(logViewer);
    }

    private void initStepCountViewer() {
        final StepCountViewer stepCountViewer = new StepCountViewer();
        addStepEventListener(stepCountViewer);
        addIterationEventListener(stepCountViewer);
        firePanelCreated(stepCountViewer);
    }

    private void initGoal() {
        goal = new Goal(null, null, null, 1, null, null, null);
    }

    protected void firePanelCreated(ViewerPanel panel) {
        container.panelCreated(new ViewerPanelEvent(this, panel));
    }

    protected void firePanelPopulated() {
        container.panelPopulated(new ViewerPanelEvent(this));
    }

    public void step() {
        if (isGoalReached(state, goal)) {
            logger.info(String.format("Goal reached (total step count: %d)", toGoalStepCount));
            goalCount++;
            fireIterationEndedEvent(true);
            restart();
        } else if (toGoalStepCount >= maxStepCount) {
            logger.info(String.format("Step count reached the maximum step count (step count: %d)", toGoalStepCount));
            fireIterationEndedEvent(false);
            restart();
        }

        toGoalStepCount++;
        stepCount++;

        if (logger.isTraceEnabled())
            logger.trace(String.format("iteration: %d", stepCount));
        fireStepEvent();

        int action = 1;
        if (subgoal != null) {
            /* 3action */
            if(subgoal.get(2) != 0) {
                action = ((Integer)subgoal.get(2)).intValue();
            }
        }
        state = environmentAdapter.move(action);

        /* state */
        subgoal = gsa.exec(state);

    }

    public void repeatProcess() {
        while (true) {
            try {
                Thread.sleep(sleepTime);
            } catch(InterruptedException e) {
                throw new ApplicationException(e);
            }
            step();
        }
    }

    private void fireIterationStartedEvent() {        
        iterationEventListeners.fire("iterationStarted", new IterationEvent(this, iteration, goalCount, false));
    }

    private void fireIterationEndedEvent(boolean achieved) {        
        iterationEventListeners.fire("iterationEnded", new IterationEvent(this, iteration, goalCount, achieved));
    }

    private void fireStepEvent() {
        stepEventListeners.fire("nextStep", new StepEvent(this, stepCount, toGoalStepCount));
    }

    private boolean isGoalReached(List<Integer> state, List<Integer> goal) {
        if(goal == null || state == null) {
            return false;
        }

        for(int i = 0; i < state.size(); i++) {
            Integer sElement = (Integer)state.get(i);
            Integer gElement = (Integer)goal.get(i);
            if(gElement != null && !sElement.equals(gElement)) {
                return false;
            }
        }
        return true;
    }

    /*
    private void reset() {
        stepCount = 0;
        iteration = 0;
        goalCount = 0;
        restart();
    }
    */

    private void restart() {
        gsa.reset();
        gsa.setGoal(goal);
        environmentAdapter.restart();
        toGoalStepCount = 0;
        state = null;
        subgoal = null;
        iteration++;
        fireIterationStartedEvent();
    }

    private static void setupLogging() {
        final LoggerContext ctx = (LoggerContext)LoggerFactory.getILoggerFactory();
        final ch.qos.logback.classic.Logger rootLogger = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
        // rootLogger.setLevel(Level.TRACE);
        final Appender<ILoggingEvent> appender = LogViewer.getAppender();
        appender.setContext(ctx);
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setContext(ctx);
        filter.setLevel("INFO");
        filter.start();
        appender.addFilter(filter);
        rootLogger.addAppender(appender);
        appender.start();
    }

    public void addStepEventListener(StepEventListener listener) {
        stepEventListeners.addEventListener(listener);
    }

    public void removeStepEventListener(StepEventListener listener) {
        stepEventListeners.removeEventListener(listener);
    }

    public void addIterationEventListener(IterationEventListener listener) {
        iterationEventListeners.addEventListener(listener);
    }

    public void removeIterationEventListener(IterationEventListener listener) {
        iterationEventListeners.removeEventListener(listener);
    }

    public static void main(String[] args) throws Exception {
        setupLogging();
        BricaDoorKeyDemo dkDemo = new BricaDoorKeyDemo(args[0]);
        dkDemo.repeatProcess();
    }
}
