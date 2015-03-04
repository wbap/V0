/**
 * DemoProperty.java
 * 
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.doorkeydemo;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class DemoProperty {
    /*  */
    private String envFileName;

    /*  */
    private int doorOpenMode;

    /*  */
    private int saveStepNum;     /*  */
    private String saveFileName; /*  */
    private String loadFileName; /*  */

    private int timeOutStepNum;  /* 1 */
    private int sleepTime;       /* 1 */

    /* GSA */
    private String gsaPropFileName;

    ////////////////////////////////////////////////////////////////
    // 

    /**
     * 
     * @param String propFileName 
     */
    public DemoProperty(String propFileName)
            throws FileNotFoundException, IOException, Exception {
        loadProperty(propFileName);
    }

    ////////////////////////////////////////////////////////////////
    // public

    /**
     * GSA
     * @return String 
     */
    public String getGSAPropFileName() {
        return gsaPropFileName;
    }

    /**
     * 
     * @return int 
     */
    public int getSaveStepNum() {
        return saveStepNum;
    }

    /**
     * 
     * @return String 
     */
    public String getSaveFileName() {
        return saveFileName;
    }

    /**
     * 
     * @return String 
     */
    public String getLoadFileName() {
        return loadFileName;
    }

    /**
     * 1
     * @return int 
     */
    public int getTimeOutStepNum() {
        return timeOutStepNum;
    }

    /**
     * 
     * @return int 
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * 
     * @return String 
     */
    public String getEnvFileName()  {
        return envFileName;
    }

    /**
     * 
     * @return int 
     */
    public int getDoorOpenMode() {
        return doorOpenMode;
    }

    /**
     * 
     * @return int 
     */
//    public int getNodeNum() {
//        return nodeNum;
//    }


    ////////////////////////////////////////////////////////////////
    // private

    /**
     * 
     */
    private void loadProperty(String fileName) throws FileNotFoundException, 
            IOException, NullPointerException, NumberFormatException, 
            NoSuchElementException, Exception {
        Properties prop = new Properties();

        /*  */
        try {
            FileInputStream fin = new FileInputStream(fileName);
            prop.load(fin);
            fin.close();
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            throw ioe;
        }

        StringTokenizer contents;
        try {
            /*  */
            contents = new StringTokenizer( prop.getProperty("Environment") );
            envFileName = contents.nextToken();

            contents = new StringTokenizer( prop.getProperty(
                    "GSAPropFileName") );
            gsaPropFileName = contents.nextToken();

            /*  */
            contents = new StringTokenizer( prop.getProperty("DoorOpenMode") );
            doorOpenMode = new Integer(contents.nextToken()).intValue();

            /*  */
//            contents = new StringTokenizer(prop.getProperty("NodeNum") );
//            nodeNum = new Integer(contents.nextToken()).intValue();

        } catch (NullPointerException e){
            NullPointerException ne = new NullPointerException
                ("Format Error: on property file " + fileName);
            throw ne;
        } catch (NumberFormatException e) {
            NumberFormatException nfe = new NumberFormatException
                ("Format Error: on property file " + fileName);
            throw nfe;
        }

        try {
            /*  */
            contents = new StringTokenizer(prop.getProperty("SaveStepNum") );
            saveStepNum = new Integer(contents.nextToken()).intValue();

            saveFileName = prop.getProperty("SaveFileNeme", "");
            loadFileName = prop.getProperty("LoadFileName", "");

            contents = new StringTokenizer(prop.getProperty("TimeOutStepNum"));
            timeOutStepNum = new Integer(contents.nextToken()).intValue();

            contents = new StringTokenizer(prop.getProperty("SleepTime"));
            sleepTime = new Integer(contents.nextToken()).intValue();
        } catch (NumberFormatException e) {
            NumberFormatException nfe = new NumberFormatException
                ("Format Error: on property file " + fileName);
            throw nfe;
        }

    }

}


