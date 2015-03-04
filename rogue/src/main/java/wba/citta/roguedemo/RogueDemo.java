/**
 * DoorKeyDemo.java
 * Citta
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.roguedemo;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import wba.citta.gsa.GSA;
import wba.rogue.Avatar;
import wba.rogue.Coord;
import wba.rogue.RNG;
import wba.rogue.Rogue;
import wba.rogue.RogueViewer;

/**
 * Citta
 */
public class RogueDemo {

    /*  */
    private Rogue rogue;
    private RNG rng;
    private RNG arng;
    private Avatar environmentAgent = null;
    private RogueViewer viewer;
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
        environmentAgent = new Avatar(rogue);

        viewer = new RogueViewer(rogue);
        viewer.initialize();

        arng = new RNG();

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
        goal = arr2vec(environmentAgent.getVisibleGoal());
        printvec(goal);
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

                state = arr2vec(environmentAgent.getState());

                if(isReachGoal(state, goal) ) {
                    goalReachProcess();
                }
            }

            /*  */
            toGoalStepCount++;
            stepCount++;
            //System.out.println("");
            //System.out.println(" step count " + stepCount);

            /*  */
            /*
             * CITTA 
             * 
             */
            
            int[] directions = {1, 3, 5, 7};
            int action = directions[arng.nextInt(4)];
            if(subgoal != null) {
                /* 3action */
                if(subgoal.get(2) != null) {
                    action = ((Integer)subgoal.get(2)).intValue();
                }
            }
            state = arr2vec(environmentAgent.move(action));
            //environmentAgent.printVisible();
            viewer.render();

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

//            final long INTERVAL = 33333;
//            long start = System.nanoTime();
//            long end=0;
//            do{
//                end = System.nanoTime();
//            }while(start + INTERVAL >= end);
//            System.out.println(end - start);
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
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < arr.length; ++i) {
            list.add((Integer)arr[i]);
        }
        Vector<Integer> vec = new Vector<Integer>(list);
        return vec;
    }

    private void printvec(Vector vec) {
        for(int i = 0; i < vec.size(); ++i) {
            Integer t = (Integer)vec.get(i);
            System.out.print((char)(t & 255));
            if(i % 80 == 79) {
                System.out.println("");
            }
        }
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
