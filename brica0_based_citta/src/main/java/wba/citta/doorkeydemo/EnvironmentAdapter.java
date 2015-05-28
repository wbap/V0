/**
 * EnvironmentAgent.java
 * 
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.doorkeydemo;

import wba.citta.doorkeydemo.environment.*;
import wba.citta.gsa.Goal;

import java.io.IOException;
import java.util.*;

/**
 * 
 */
public class EnvironmentAdapter {

    /*  */
    private Environment environment = null;

    /*  */
    private int doorOpenMode = 1;

    /*  */
    private Random randomMoveAction = new Random(0);
    /*  */
    private Random randomStart = new Random(1);
    /*  */
    private Random randomItem = new Random(0);

    /*  */
    Deque<Integer> actionList = new ArrayDeque<Integer>();

    //////////////////////////////////////////////////
    // 

    /**
     * 
     * @param String fileName 
     * @param int doorOpenMode  0: 1:
     * @param int agentNum 
     */
    public EnvironmentAdapter(Environment environment, int doorOpenMode, int agentNum) throws IOException {
        this.environment = environment;
        this.doorOpenMode = doorOpenMode;
        environment.changeDoorOpenItem(doorOpenMode);
    }

    public Environment getEnvironment() {
        return environment;
    }

    //////////////////////////////////////////////////
    // public

    /**
     * action
     * action-1
     * @param int action 
     * @return Vector 
     */
    public Goal move(int action) {

        if (action == 1) {
            /*  */
            Integer integer = getInitAction();
            if(integer != null) {
                action = integer.intValue();
            }else {
                action = getRandomAction();
            }
        }

        environment.run(action);

        return getState(action);
    }

    public void restart() {
        resetStartPosition();
        environment.initRobotPos();
        environment.initMap(true, true);
        resetItemPossesionState();
    }

    public void goalReach() {
        environment.flash();
    }

    //////////////////////////////////////////////////
    // private

    private static int mapInfoToInteger(String str) {
        if (str.equals("T")) {
            return 4;
        } else if (str.equals("d")) {
            return 3;
        } else if (str.equals("K")) {
            return 2;
        } else if (str.equals("O(1)")) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * action
     * @param int action 
     * @return Vector 
     */
    private Goal getState(int action) {
        final int[] xystate = environment.getXYState();
        return new Goal(
            xystate[0],
            xystate[1],
            action,
            mapInfoToInteger(environment.getMapInfo(xystate[0], xystate[1])),
            environment.getItem(),
            1,
            doorOpenMode
        );
    }

    /**
     * 
     */
    private void resetStartPosition() {
        /*  */
        int[] newStart = getRandomState(randomStart);
        environment.setStart(newStart[0], newStart[1]);
    }


    /**
     * 
     */
    private void resetItemPossesionState() {
        int newItem = randomItem.nextInt(3);
        environment.setItem(newItem);
    }


    /**
     * 
     * @return int 
     */
    private int getRandomAction() {
        /*  */
        int randomNum = randomMoveAction.nextInt(4)*2 + 2;
        return randomNum;
    }

    /**
     * 
     * @return int[] 
     *               int[0] x
     *               int[1] y
     */
    private int[] getRandomState(Random random) {

        int[] randomState = new int[2];

        /*
         * 
         * 
         */
        while(true) {
            /*  */
            int[] mapSize = environment.getMapSize();
            randomState[0] = random.nextInt(mapSize[0]-1) + 1;
            randomState[1] = random.nextInt(mapSize[1]-1) + 1;
            String newState = environment.getMapInfo(randomState[0],
                    randomState[1]);
            if(newState.equals("")) {
                break;
            }
        }

        return randomState;
    }

    /**
     * action
     */
    private Integer getInitAction() {
        Integer manualAction = null;
        if(actionList.size() > 0) {
            manualAction = (Integer)actionList.removeFirst();
        }
        return manualAction;
    }
}

