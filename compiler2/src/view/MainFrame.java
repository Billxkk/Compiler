package view;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by Administrator on 2017/10/13.
 * 主界面
 */
public class MainFrame extends JFrame{

    private Controller controller = null;
    /**
     * 输入文法 文本区
     */
    private JTextArea textArea_getLang = null;
    /**
     * 输入句子 文本框
     */
    private JTextField textF_getStr = null;
    /**
     * 分析语法 按钮
     */
    private JButton analysis_btn = null;
    /**
     * 分析句子 按钮
     */
    private JButton getStr_btn = null;

    /**
     * first表的 数据模型
     */
    private DefaultTableModel firstModel;
    /**
     * follow表的 数据模型
     */
    private DefaultTableModel followModel;
    /**
     * 预测分析表 的表格
     */
    private JTable Table_analysis;
    /**
     * 分析栈 表格
     */
    private DefaultTableModel stackModel;

    public DefaultTableModel getFirstModel() {
        return firstModel;
    }

    public DefaultTableModel getFollowModel() {
        return followModel;
    }

    public DefaultTableModel getStackModel() {
        return stackModel;
    }


    public MainFrame(){
        controller = new Controller(this);
        init();

        setTitle("主界面");
        setSize(950,750);
        setLocation(100,50);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//结束窗口所在的应用程序

        validate();
    }

    /**
     * 初始化界面
     */
    private void init() {
        setLayout(null);
        /**标签**/
        JLabel label_input = new JLabel("输入文法及分析语句");
        label_input.setBounds(22,14,118,25);
        add(label_input);
        JLabel label_output = new JLabel("分析结果输出");
        label_output.setBounds(253,14,118,25);
        add(label_output);
        JLabel label_first = new JLabel("first表");
        label_first.setBounds(22,390,118,25);
        add(label_first);
        JLabel label_follow = new JLabel("follow表");
        label_follow.setBounds(204,390,118,25);
        add(label_follow);
        JLabel label_table = new JLabel("预测分析表");
        label_table.setBounds(427,390,118,25);
        add(label_table);

        /** 输入文法 文本区**/
        textArea_getLang = new JTextArea();
        JScrollPane inputScroll = new JScrollPane(textArea_getLang);
        inputScroll.setBounds(12,56,206,240);
        add(inputScroll);

        /** 句子 输入框 **/
        textF_getStr = new JTextField();
        textF_getStr.setBounds(12,306,206,20);
        add(textF_getStr);

        /** 分析文法 按钮**/
        analysis_btn = new JButton("分析文法");
        analysis_btn.setBounds(12,336,95,20);
        analysis_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analysisLang();
            }
        });
        add(analysis_btn);

        /** 分析语句 按钮**/
        getStr_btn = new JButton("分析句子");
        getStr_btn.setBounds(123,336,95,20);
        getStr_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analysisStr();
            }
        });
        add(getStr_btn);

        /** 输出结果的 表格 **/
        JTable Table_out = new JTable();
        stackModel = new DefaultTableModel();
        stackModel.addColumn("步骤");
        stackModel.addColumn("分析栈");
        stackModel.addColumn("剩余输入串");
        stackModel.addColumn("所用产生式");
        stackModel.addColumn("动作");
        for (int i = 0; i < 50; i++) {
            stackModel.addRow(new Object[5]);
        }
        Table_out.setEnabled(false);
        Table_out.setModel(stackModel);
        JScrollPane outputScroll = new JScrollPane(Table_out);
        outputScroll.setBounds(243,56,671,300);
        add(outputScroll);

        /** 输出first 表**/
        firstModel = new DefaultTableModel();
        firstModel.addColumn("非终结符");
        firstModel.addColumn("集合");
        for (int i = 0; i < 15; i++) {
            firstModel.addRow(new Object[]{"",""});
        }
        JTable Table_first = new JTable();
        Table_first.setModel(firstModel);
        Table_first.setEnabled(false);
        JScrollPane firstScroll = new JScrollPane(Table_first);
        firstScroll.setBounds(12,425,165,250);
        add(firstScroll);

        /** 输出 follow 表 **/
        followModel = new DefaultTableModel();
        followModel.addColumn("非终结符");
        followModel.addColumn("集合");
        for (int i = 0; i < 15; i++) {
            followModel.addRow(new Object[]{"",""});
        }
        JTable Table_follow = new JTable();
        Table_follow.setModel(followModel);
        Table_follow.setEnabled(false);
        JScrollPane followScroll = new JScrollPane(Table_follow);
        followScroll.setBounds(194,425,207,250);
        add(followScroll);

        /** 输出 预测分析表 **/
        Table_analysis = new JTable();
        Table_analysis.setEnabled(false);
        JScrollPane analysisScroll = new JScrollPane(Table_analysis);
        analysisScroll.setBounds(417,425,491,250);
        add(analysisScroll);

        validate();
    }

    /**
     * 分析文法
     */
    private void analysisLang() {
        if(textArea_getLang.getText().equals("")){
            // TODO: 2017/10/17 文法为空 报错
        }else{
            controller.analysisLang(textArea_getLang.getText());
        }
    }

    /**
     * 分析句子
     */
    private void analysisStr() {
        if(textF_getStr.getText().equals("")){
            // TODO: 2017/10/17 句子为空 报错
        }else{
            controller.analysisStr(textF_getStr.getText());
        }
    }


    public void updateAnalysis(DefaultTableModel selectModel) {
        Table_analysis.setModel(selectModel);
        validate();
    }
}
