package controller;

import model.Analyzer;
import model.LL1;
import view.MainFrame;

import javax.swing.table.DefaultTableModel;
import java.util.*;

/**
 * Created by Administrator on 2017/10/16.
 *  界面模块控制器
 */
public class Controller {

    private MainFrame mainFrame = null;
    private Analyzer analyzer = null;
    private LL1 lang = null;

    public Controller(MainFrame frame){
        mainFrame = frame;

    }

    /**
     * 开始分析文法
     * @param text 传入的文法string
     */
    public void analysisLang(String text){

        lang = new LL1();
        ArrayList<String> array = new ArrayList<String>();
        setLL1(array,text);
        lang.setLL1Array(array);//文法表达式集合
        lang.setS('E');//开始符
        lang.init();

        setFirstTable();
        setFollowTable();
        setAnalysisTable();
    }

    /**
     * 开始分析句子
     * @param text
     */
    public void analysisStr(String text){

        analyzer = new Analyzer(this);
        analyzer.setStartChar('E');
        analyzer.setLang(lang);
        analyzer.setStr(text);
        LinkedList<Object[]> stack = analyzer.analyze();
        DefaultTableModel stackModel = mainFrame.getStackModel();

        if (stack == null) {
            // TODO: 2017/10/17 错误
        }else{
            // TODO: 2017/10/17 输出结果
            int row = 0;
            for (Object[] value :stack) {
                for (int i = 0; i < 5; i++) {
                    if (value[i] != null){
                        stackModel.setValueAt(value[i].toString(),row,i);
                    }
                }
                row++;
            }
        }

        System.out.println("program finished ");
    }

    /**
     * 设置界面分析表
     */
    private void setAnalysisTable() {
        if (lang != null) {
            HashMap<Character,HashMap<String,TreeSet<Character>>> selectMap =  lang.getSelectMap();
            DefaultTableModel selectModel = new DefaultTableModel();

            TreeSet<Character> ntSet = lang.getNtSet();//终结符集合
            ntSet.remove('ε');
            selectModel.addColumn("");//第一列
            int column = 1;
            for (Character nt :ntSet) {
                selectModel.addColumn(nt);
                column++;
            }
            selectModel.addColumn('#');//最后一列
            column++;

            Set<Character> keySet = selectMap.keySet();//非终结符集合
            int row = 0;
            for (Character key :keySet) {//非终结符
                selectModel.addRow(new Object[column]);
                selectModel.setValueAt(key,row,0);
                HashMap<String,TreeSet<Character>>value =  selectMap.get(key);
                Set<String> strSet = value.keySet();//该非终结符的 所有表达式
                for (String str : strSet) {
                    TreeSet<Character> ntValue = value.get(str);//表达式对应的终结符列
                    for (Character nt :ntValue) {
                        if(nt == '#'){
                            selectModel.setValueAt(key+"->"+str,row,column-1);
                        }else{
                            for (int i = 1; i < column - 1; i++) {
                                String tempNt = selectModel.getColumnName(i);
                                if(tempNt.equals(nt.toString())){//对应的列
                                    selectModel.setValueAt(key+"->"+str,row,i);
                                }
                            }
                        }
                    }
                }
                row++;
            }
            mainFrame.updateAnalysis(selectModel);//更新到界面
        }
    }

    private void setFollowTable() {
        if (lang != null) {
            HashMap<Character,TreeSet<Character>> followMap =  lang.getFollowMap();
            DefaultTableModel followModel = mainFrame.getFollowModel();
            Set<Character> keySet = followMap.keySet();
            int row = 0;
            for (Character key : keySet) {
                followModel.setValueAt(key,row,0);
                followModel.setValueAt(followMap.get(key),row,1);
                row++;
            }
        }
    }

    private void setFirstTable(){
        if (lang != null) {
            HashMap<Character,TreeSet<Character>> firstMap =  lang.getFirstMap();
            DefaultTableModel firstModel = mainFrame.getFirstModel();
            Set<Character> keySet = firstMap.keySet();
            int row = 0;
            for (Character key : keySet) {
                firstModel.setValueAt(key,row,0);
                firstModel.setValueAt(firstMap.get(key),row,1);
                row++;
            }
        }
    }



    /**
     * 设置文法的 表达式数据
     * @param array
     * @param text 读取到的文法文本（文本格式：每行一个表达式，回车换行）
     */
    private static void setLL1(ArrayList<String> array, String text) {
        String[] item = text.split("\\s+");
        for (String str :item) {
            array.add(str);
        }
    }
}
