/**
 * 手動でスタックにゴールを設定するためユーザーインターフェース
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Button;
import java.awt.event.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 手動でスタックにゴールを設定するためユーザーインターフェース
 */
public class ManualAgentFrame extends Frame {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ManualAgentFrame.class);
    private TextField[] textFields = null;
    private Button okButton = null;

    /**
     * コンストラクタ
     * @param int nodeNum ノード数
     */
    public ManualAgentFrame(int nodeNum) {
        super("Manual Agent");

        setLayout(new GridLayout(1, nodeNum+1));

        textFields = new TextField[nodeNum];
        for(int i = 0; i < nodeNum; i++) {
            textFields[i] = new TextField("null");
            add(textFields[i]);
        }

        okButton = new Button("OK");
        add(okButton);
        okButton.addActionListener(new ButtonActionListener());

        setSize(200, 50);
        setVisible(true);

    }

    ///////////////////////////////////////////////////////////////////////
    // public

    List<Integer> subgoal = null;
    /**
     * 手動で設定されたサブゴールを取得します。
     * @return Vector サブゴール
     */
    public List<Integer> getSubgoal() {
        return subgoal;
    }

    /**
     * 内部で保持しているサブゴールをクリアします。
     */
    public void clearSubgoal() {
        subgoal = null;
    }

    ///////////////////////////////////////////////////////////////////////
    // private

    /**
     * 引数で設定された文字列の配列をゴールとして内部の変数に設定します。
     * @param String[] input 文字列の配列
     */
    private void setSubgoal(String[] input) {
        List<Integer> subgoal = new LinkedList<Integer>();
        for(int i = 0; i < input.length; i++) {
            if(input[i].equals("null")) {
                subgoal.add(null);
            }else {
                Integer elm = null;
                try {
                    elm = Integer.valueOf(input[i]);
                } catch (NumberFormatException e) {
                    logger.warn("%s", input[i]);
                    // 数値以外が設定されている場合はnullを設定
                }
                subgoal.add(elm);
            }
        }
        this.subgoal = subgoal;
    }

    /**
     * テキストフィールドの初期化をします。
     * 全てをnullで埋めます。
     */
    private void initTextFields() {
        for(int i = 0; i < textFields.length; i++) {
            textFields[i].setText("null");
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // イベント処理

    /**
     * ボタンのイベント処理を行なうインナークラス
     */
    class ButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            if(okButton == evt.getSource()) {
                String[] str = new String[textFields.length];
                for(int i = 0; i < textFields.length; i++) {
                    str[i] = textFields[i].getText();
                }
                initTextFields();
                setSubgoal(str);
            }
        }
    }
}

