package model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Administrator on 2017/10/29.
 * 分析器
 */
public class Analyzer {
    /**
     * LR 分析表
     */
    private HashMap<Integer,HashMap<Character,String>> analysisTable;
    /**
     * 输入串
     */
    private String textStr;
    /**
     * 符号栈
     */
    private LinkedList<Character> SymbolStack;
    /**
     * 状态栈
     */
    private LinkedList<Integer> stateStack;
    /**
     * 文法规则
     */
    LR1 lang;

    public void setTextStr(String textStr) {
        this.textStr = textStr;
    }
    public void setLang(LR1 lang) {
        this.lang = lang;
    }

    public Analyzer(){
        analysisTable = new HashMap<>();
        SymbolStack = new LinkedList<>();
        stateStack = new LinkedList<>();

    }

    public LinkedList<Object[]> analysis(){
        analysisTable = lang.getAnalysisTable();
        LinkedList<Object[]> stack = new LinkedList<>();
        int index = 1;

        stateStack.push(0);
        SymbolStack.push('#');

        while (true){
            System.out.print(stateStack.toString() + "------" + SymbolStack.toString() + "----------" + textStr);
            Object[] value = new Object[5];
            value[0] = index;
            index++;
            String str = "";
            for (int i = stateStack.size()-1; i >-1 ; i--) {
                str += stateStack.get(i) + ".";
            }
            value[1] = str;
            str = "";
            for (int i = SymbolStack.size()-1; i >-1 ; i--) {
                str += SymbolStack.get(i);
            }
            value[2] = str;
            value[3] = textStr;

            int state = Integer.valueOf(stateStack.peekFirst().toString());//状态
            Character inSymA = textStr.charAt(0);//现行输入符号
            HashMap<Character,String> analysisLine = analysisTable.get(state);
            String analysisStr = analysisLine.get(inSymA);
            if (analysisStr == null){//错误
                System.out.println("error");
                value[4] = "错误";
                stack.add(value);
                break;
            }else if(analysisStr.charAt(0) == 's'){//推进
                SymbolStack.push(inSymA);
                textStr = textStr.substring(1);
                String s = analysisStr.substring(1);
                value[4] = "推进，ACTION[" + stateStack.peekFirst() + "," + inSymA + "]=" + analysisStr + ",状态" +
                                        s + "入栈";
                stateStack.push(Integer.valueOf(s));
                System.out.print("--------推进\n");
                stack.add(value);
            }else if(analysisStr.charAt(0) == 'r'){//归约
                String str1 = analysisStr.substring(1);
                String[] nvNtItem = str1.split("->");
                int size = nvNtItem[1].length();
                for (int i = 0; i < size; i++) {
                    stateStack.pop();
                    SymbolStack.pop();
                }
                int frontState = Integer.valueOf(stateStack.peekFirst().toString());//状态
                HashMap<Character,String> analysisLine1 = analysisTable.get(frontState);//状态对应的列
                String analysVn = analysisLine1.get(nvNtItem[0].charAt(0));//获取s
                Integer s = Integer.valueOf(analysVn);
                value[4] = analysisStr + ":" + str1 + "归约，GOTO(" + frontState + "," + nvNtItem[0] + ")=" + s + "入栈";
                stateStack.push(s);
                SymbolStack.push(nvNtItem[0].charAt(0));
                System.out.println("--------归约\n");
                stack.add(value);
            }else if(analysisStr.equals("acc")){
                System.out.println("成功");
                value[4] = "Acc：分析成功";
                stack.add(value);
                break;
            }
        }
        return stack;
    }



}
