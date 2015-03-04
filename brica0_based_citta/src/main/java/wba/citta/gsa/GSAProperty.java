/**
 * GSAProp.java
 * エージェントの設定情報を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * エージェントの設定を管理するクラス
 */
public class GSAProperty {
    /* ノード数 */
    private int nodeNum;

    private AgentInfo[] agentInfoList;

    private boolean useMana;

    /* viewerに関する設定 */
    private boolean agentViewer = false;
    private boolean goalStackViewer = false;
    private boolean failAgentTreeViewer = false;

    private Map<Integer, Color> colorTable = new HashMap<Integer, Color>();

    private Dimension agentViewerInitSize = new Dimension(460, 120);
    private Dimension sharedMemoryViewerInitSize = new Dimension(320, 300);
    private Dimension treeViewerInitSize = new Dimension(460, 740);

    private static final Pattern AGENT_PROPERTY_PATTERN = Pattern.compile("^agent\\[([^]]+)\\](?:\\.(.+))", 0);

    ////////////////////////////////////////////////////////////////
    // コンストラクタ

    /**
     * コンストラクタ
     * @param String propFileName 設定ファイル名
     * @exception FileNotFoundException
     * @exception IOException
     * @exception Exception
     */
    public GSAProperty(String propFileName) throws IOException {
        loadProperty(propFileName);
    }

    ////////////////////////////////////////////////////////////////
    // public

    /**
     * ManualAgentを使用するかどうかのフラグを取得します。
     * @return boolean  true：使用する false：使用しない
     */
    public boolean getUseMana() {
        return useMana;
    }

    /**
     * 環境で使用されるノード数を取得します。
     * @return int ノード数
     */
    public int getNodeNum() {
        return nodeNum;
    }

    /**
     * GSAで使用するエージェントの数を取得します。
     * @return int エージェント数
     */
    public int getAgentNum() {
        return agentInfoList.length;
    }

    public AgentInfo[] getAgentInfoList() {
        return agentInfoList;
    }

    /**
     * エージェントの動作状況を表示するViewerを表示するかどうか。
     * @return boolean true 表示する  false 表示しない
     */
    public boolean isShowAgentViewer() {
         return agentViewer;
    }

    /**
     * ゴールスタックの状況を表示するViewerを表示するかどうか。
     * @return boolean true 表示する  false 表示しない
     */
    public boolean isShowGoalStackViewer() {
        return goalStackViewer;
    }

    /**
     * 失敗エージェントのツリーの状況を表示するViewerを表示するかどうか。
     * @return boolean true 表示する  false 表示しない
     */
    public boolean isShowFailAgentTreeViewer() {
        return failAgentTreeViewer;
    }

    public Map<Integer, Color> getColorTable() {
        return colorTable;
    }

    public Dimension getAgentViewerInitSize() {
        return agentViewerInitSize;
    }

    public Dimension getSharedMemoryViewerInitSize() {
        return sharedMemoryViewerInitSize;
    }

    public Dimension getTreeViewerInitSize() {
        return treeViewerInitSize;
    }

    /**
     * ファイルから情報の読み込み
     * @param String fileName ファイル名
     */
    private void loadProperty(String fileName) throws IOException {
        Properties props = new Properties();

        /* ファイルの読み込み */
        FileInputStream fin = new FileInputStream(fileName);
        try {
            props.load(fin);
        } finally {
            fin.close();
        }
        Map<Integer, Map<String, String>> agentProps = new HashMap<Integer, Map<String, String>>();
        for (final String key: props.stringPropertyNames()) {
            Matcher m = AGENT_PROPERTY_PATTERN.matcher(key);
            if (!m.find())
                continue;
            final String agentIdStr = m.group(1);
            final String subPropertyName = m.group(2);
            int agid;
            /* エージェントID取得 */
            try {
                agid = Integer.parseInt(agentIdStr);
            } catch (NumberFormatException e) {
                throw new GSAException(String.format("Invalid value for agent ID: %s", agentIdStr));
            }
            Map<String, String> propsForAgent = agentProps.get(agid);
            if (propsForAgent == null) {
                propsForAgent = new HashMap<String, String>();
                agentProps.put(agid, propsForAgent);
            }
            propsForAgent.put(subPropertyName, props.getProperty(key));
        }
        populateViewerProperties(props, agentProps);
        populateProperties(props, agentProps);
    }

    private void populateViewerProperties(Properties prop, Map<Integer, Map<String, String>> agentProps) {
        /* viewerの表示に関する設定の読み込み */
        {
            final StringTokenizer contents = new StringTokenizer(prop.getProperty("AgentViewer"));            
            agentViewer = Boolean.parseBoolean(contents.nextToken());
        }
        {
            final StringTokenizer contents = new StringTokenizer(prop.getProperty("GoalStackViewer"));
            goalStackViewer = Boolean.parseBoolean(contents.nextToken());
        }
        {
            final StringTokenizer contents = new StringTokenizer(prop.getProperty("FailAgentTreeViewer"));
            failAgentTreeViewer = Boolean.parseBoolean(contents.nextToken());
        }

        if (agentViewer) {
            final String value = prop.getProperty("AgentViewerInitSize");
            if (value != null) {
                final StringTokenizer contents = new StringTokenizer(value, ",");
                String[] agentViewerInitSizeStr;
                try {
                    agentViewerInitSizeStr = new String[] {
                        contents.nextToken(),
                        contents.nextToken()
                    };
                    agentViewerInitSize = new Dimension(
                        Integer.parseInt(agentViewerInitSizeStr[0]),
                        Integer.parseInt(agentViewerInitSizeStr[1])
                    );
                } catch (Exception e) {
                    throw new GSAException(String.format("Invalid value for agent viewer size: %s", value));
                }
            }
        }
        if (goalStackViewer) {
            final String value = prop.getProperty("SharedMemoryViewerInitSize");
            if (value != null) {
                final StringTokenizer contents = new StringTokenizer(value, ",");
                String[] sharedMemoryViewerInitSizeStr;
                try {
                    sharedMemoryViewerInitSizeStr = new String[] {
                        contents.nextToken(),
                        contents.nextToken()
                    };
                    sharedMemoryViewerInitSize = new Dimension(
                        Integer.parseInt(sharedMemoryViewerInitSizeStr[0]),
                        Integer.parseInt(sharedMemoryViewerInitSizeStr[1])
                    );
                } catch (Exception e) {
                    throw new GSAException(String.format("Invalid value for shared memory viewer size: %s", value));
                }
            }
        }
        if (failAgentTreeViewer) {
            final String value = prop.getProperty("FailAgentTreeViewerInitSize");
            if (value != null) {
                final StringTokenizer contents = new StringTokenizer(value, "," );
                String[] treeViewerInitSizeStr;
                try {
                    treeViewerInitSizeStr = new String[] {
                        contents.nextToken(),
                        contents.nextToken()
                    };
                    treeViewerInitSize = new Dimension(
                        Integer.parseInt(treeViewerInitSizeStr[0]),
                        Integer.parseInt(treeViewerInitSizeStr[1])
                    );
                } catch (Exception e) {
                    throw new GSAException(String.format("Invalid value for tree viewer size: %s", value));
                }
            }
        }
        Map<Integer, Color> colorTable = new HashMap<Integer, Color>();
        /* エージェント毎の設定の読込み */
        int i = 0;
        for (final Map.Entry<Integer, Map<String, String>> pair: agentProps.entrySet()) {
            final Integer agid = pair.getKey();
            final String value = pair.getValue().get("color");
            Color color;
            if (value == null) {
                color = Color.getHSBColor((float)i / (float)agentProps.size(), 0.7f, 0.9f);
            } else {
                /* エージェントの色の設定取得 */
                {
                    final StringTokenizer stringTokenizer = new StringTokenizer(value, ",");
                    try {
                        final String[] rgbStr = {
                            stringTokenizer.nextToken(),
                            stringTokenizer.nextToken(),
                            stringTokenizer.nextToken()                        
                        };
                        color = new Color(
                            Integer.parseInt(rgbStr[0]),
                            Integer.parseInt(rgbStr[1]),
                            Integer.parseInt(rgbStr[2])
                        );
                    } catch (Exception e) {
                        throw new GSAException(String.format("invalid color value for %s: %s", agid, value));
                    }
                }
            }
            /* エージェントID毎の表示色をテーブルに設定 */
            colorTable.put(agid, color);
            i++;
        }        
        this.colorTable = colorTable;
    }

    private void populateProperties(Properties props, Map<Integer, Map<String, String>> agentProps) {
        int agentNum = 0;
        /* manaの使用 */
        {
            final String value = props.getProperty("UseMana");
            if (value != null) {
                useMana = Boolean.parseBoolean(value.trim());
                props.remove("useMana");
            }
        }
        /* ノード数の読込み */
        {
            final String value = props.getProperty("NodeNum");
            if (value == null) {
                throw new GSAException("No such property: NodeNum");
            }
            try {
                nodeNum = Integer.parseInt(value.trim());
            } catch (Exception e) {
                throw new GSAException(String.format("Invalid value for NodeNum: %s", value), e);
            }
            props.remove("NodeNum");
        }
        /* エージェント数の読込み */
        {
            final String value = props.getProperty("AgentNum");
            if (value == null) {
                throw new GSAException("No such property: AgentNum");
            }
            try {
                agentNum = Integer.parseInt(value.trim());
            } catch (Exception e) {
                throw new GSAException(String.format("Invalid value for AgentNum: %s", value), e);
            }
            props.remove("AgentNum");
        }

        agentInfoList = new AgentInfo[agentNum];
        for (int i = 0;i < agentNum;i++) {
            agentInfoList[i] = new AgentInfo();
        }

        /* ノード毎の設定の読込み */
        int index = 0;
        for (final Map.Entry<Integer, Map<String, String>> pair: agentProps.entrySet()) {
            final Integer agid = pair.getKey();
            final Map<String, String> propsForAgent = pair.getValue();
            AgentType agentType;
            boolean[] useNode = new boolean[nodeNum];
            String eventFileName = null;
            /* エージェントの種類取得 */
            {
                final String typeStr = propsForAgent.get("type");
                agentType = AgentType.valueOf(typeStr);
                if (agentType == null) {
                    throw new GSAException("Agent Type Error");
                }
            }
            /* 使用ノードの取得 */
            {
                final String value = propsForAgent.get("useNode");
                for (final StringTokenizer stringTokenizer = new StringTokenizer(value, ",");
                        stringTokenizer.hasMoreTokens();) {
                    int i = Integer.parseInt(stringTokenizer.nextToken().trim());
                    if (i < 0 || i >= nodeNum) {
                        throw new GSAException(String.format("index out of bounds (%d) for agent[%s].useNode", i, agid));
                    }
                    useNode[i] = true;
                }
            }
            /* イベントデータファイル名の取得 */
            {                
                eventFileName = propsForAgent.get("eventFileName");
            }
            final AgentInfo agentInfo = agentInfoList[index++];
            agentInfo.setId(agid);
            agentInfo.setType(agentType);
            agentInfo.setUseNode(useNode);
            agentInfo.setEventFileName(eventFileName);
        }
        /* Agent数のチェック */
        if(index != agentNum) {
            throw new GSAException("Agent Number Error");
        }
    }
}


