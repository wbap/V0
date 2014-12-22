/**
 * DoorKeyDemo.java
 * Citta
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.roguedemo;

import java.io.*;
import java.util.*;
import gsa.*;
import wba.rogue.*;

/**
 * Citta
 */
    public class RogueDemo {

    /*  */
    private Rogue rogue;
    private RNG rng;
    private Avatar environmentAgent = null;
    /* Citta */
    private GSA gsa = null;

    /*  */
    private DemoProperty prop = null;

    /*  */
    private int saveCount;
    private String saveFileName;
    private String loadFileName;
    private final int TIME_OUT_STEP;
    private final int SLEEP_TIME;

    /*  */
    private Vector goal = null;


    ////////////////////////////////////////////////////////////////
    // 

    /**
     * 
     */
    public RogueDemo(String propFileName) {

        /*  */
        try {
            prop = new DemoProperty(propFileName);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        gsa = new GSA(prop.getGSAPropFileName());

        rng = new RNG(1);
        rogue = new Rogue(new Coord(80, 24), 9, rng, true);
        rogue.setSpawn(2);
        environmentAgent = new Avatar(rogue);
        Coord coord = rogue.getCoord();
        System.err.println(coord.x + ", " + coord.y);
        while(1) {
        }

        return;

        /*  */
        saveCount = prop.getSaveStepNum();
        saveFileName = prop.getSaveFileName();
        loadFileName = prop.getLoadFileName();
        TIME_OUT_STEP = prop.getTimeOutStepNum();
        SLEEP_TIME = prop.getSleepTime();

        /*  */
        if(!loadFileName.equals("")) {
            gsa.load(loadFileName);
        }

        /* gsa */
        initGoal();
        setGoal();

        // 
        initLogFile("Step.log");
    }

    /**
     * 
     */
    private void initGoal() {
        goal = arr2vec(environmentAgent.getGoal());
//        goal = new Vector();
//        goal.add(null);
//        goal.add(null);
//        goal.add(null);
//        goal.add(new Integer(1));
//        goal.add(null);
//        goal.add(null);
//        goal.add(null);
    }

    ////////////////////////////////////////////////////////////////
    // public

    /**
     * 
     */
    public void repeatProcess() {

        int toGoalStepCount = 0; /*  */
        int stepCount = 0;       /*  */

        /*
         * 
         * xyID
         */
        Vector state = null;    /*  */
        Vector subgoal = null;  /* CITTA */

        /* Educational Environment */
        int[] roomIndex = {2, 5, 4, 3, 0, 7, 8};
        int index = 0;
        int count = 0;

        /* GSA */
        while(true) {

            /*   */
            try {
                Thread.sleep(SLEEP_TIME);
            }catch(InterruptedException e) {
                System.out.println(e);
            }


            /*    */
            if( isReachGoal(state, goal) || toGoalStepCount==TIME_OUT_STEP ) {

                /*  */
                System.out.println("++++++++++++++++++++++++++++++++++++++++");
                System.out.println("   " + toGoalStepCount);
                logOut(toGoalStepCount);
                System.out.println("++++++++++++++++++++++++++++++++++++++++");

                toGoalStepCount = 0;
                restart();
                subgoal = null;

                if(isReachGoal(state, goal) ) {
                    if(count++ == 10) {
                        index++;
                        count = 0;
                    }
                    rogue.setSpawn(roomIndex[index]);
                    goalReachProcess();
                }
            }

            /*  */
            toGoalStepCount++;
            stepCount++;
            System.out.println("");
            System.out.println(" step count " + stepCount);

            /*  */
            /*
             * CITTA 
             * 
             */
            int action = -1;
            if(subgoal != null) {
                /* 3action */
                if(subgoal.get(2) != null) {
                    action = ((Integer)subgoal.get(2)).intValue();
                }
            }
            state = arr2vec(environmentAgent.move(action));

            /* CITTA */
            /*  */
            if(stepCount == saveCount) {
                if(!saveFileName.equals("")) {
                    gsa.save(saveFileName);
                }
            }
            /* state */
            subgoal = gsa.exec(state);

            /*  */
            //			gsa.printStack();
            //			gsa.printGoalTree();

        }
    }


    ////////////////////////////////////////////////////////////////
    // private

    /**
     * 
     * (null)Statetrue
     */
    private boolean isReachGoal(Vector state, Vector goal) {
        return environmentAgent.checkGoal();
//        if(goal == null || state == null) {
//            return false;
//        }
//
//        for(int i = 0; i < state.size(); i++) {
//            Integer sElement = (Integer)state.get(i);
//            Integer gElement = (Integer)goal.get(i);
//            if(gElement != null && !sElement.equals(gElement)) {
//                return false;
//            }
//        }
//        return true;
    }

    /**
     * 
     */
    private void goalReachProcess() {
        /*  */
        //environmentAgent.goalReach();
    }

    /**
     * 
     */
    private void restart() {
        gsa.reset();
        environmentAgent.restart();
        /* gsa */
        setGoal();
    }

    /**
     * CITTA
     */
    private void setGoal() {
        gsa.setGoal(goal);
    }

    private Vector arr2vec(int[] arr) {
        return new Vector(Arrays.asList(arr));
    }


    //////////////////////////////////////////////////////////////
    // 

    private PrintWriter printWriter; // 

    private void initLogFile(String fileName) {
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(fileName,
                                                                     false);
            printWriter = new PrintWriter(fileOutputStream, true);
        }catch(Exception e) {
        }
    }

    private void logOut(int stepNum) {
        printWriter.println(stepNum);
    }


    ////////////////////////////////////////////
    //  

    public static void main(String[] args) {
        RogueDemo rogueDemo = new RogueDemo(args[0]);
        rogueDemo.repeatProcess();
    }


}
