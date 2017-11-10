package model;

import view.myFrame;

import javax.swing.table.DefaultTableModel;
import java.util.*;

/**
 * Created by Administrator on 2017/10/29.
 * 控制器
 */
public class Controller {


    private LR1 lang = null;
    private myFrame mainFrame = null;

    public Controller(myFrame mainFrame){
        this.mainFrame = mainFrame;
    }

    public void analysisStr(String text) {
        Analyzer analyzer = new Analyzer();
        analyzer.setLang(lang);
        analyzer.setTextStr(text);
        LinkedList<Object[]> stack = analyzer.analysis();
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

    public void analysisLang(String text) {
        lang = new LR1();
        ArrayList<String> array = new ArrayList<String>();
        Character c = text.charAt(0);
        text = text.substring(2);//切除首行开始符
        setLR1(array,text);
        lang.setLL1Array(array);//文法表达式集合
        lang.setS(c);//开始符
        lang.init();

        if (lang != null) {
            DefaultTableModel selectModel = new DefaultTableModel();

            TreeSet<Character> ntSet = lang.getVtSet();//终结符集合
            ntSet.remove('ε');
            selectModel.addColumn("状态");//第一列
            int column = 1;
            for (Character nt :ntSet) {
                selectModel.addColumn(nt);
                column++;
            }
            selectModel.addColumn('#');//最后一列
            TreeSet<Character> VnSet = lang.getVnSet();//非终结符集合
            for (Character nt :VnSet) {
                selectModel.addColumn(nt);
                column++;
            }
            column++;

            HashMap<Integer,HashMap<Character,String>> table = lang.getAnalysisTable();
            Set<Integer> keySet = table.keySet();//状态集合
            int row = 0;
            for (Integer key :keySet) {//状态
                selectModel.addRow(new Object[column]);
                selectModel.setValueAt(key,row,0);//第一列 状态数字
                HashMap<Character,String>value =  table.get(key);//内容
                Set<Character> strSet = value.keySet();//Vn and Vt
                for (Character nt :strSet) {
                    for (int i = 1; i < column ; i++) {
                        String tempNt = selectModel.getColumnName(i);
                        if (tempNt.equals(nt.toString())) {//对应的列
                            selectModel.setValueAt(value.get(nt), row, i);
                        }
                    }
                }
                row++;
            }
            mainFrame.updateAnalysis(selectModel);//更新到界面
        }
    }

    private void setLR1(ArrayList<String> array, String text) {
        String[] item = text.split("\\s+");
        for (String str :item) {
            array.add(str);
        }
    }
}
