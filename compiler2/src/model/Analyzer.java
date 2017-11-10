package model;

import controller.Controller;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Administrator on 2017/10/15.
 * 语句分析器
 */
public class Analyzer {
    /**
     * 控制器
     */
    private Controller controller;
    /**
     * LL（1）文法
     */
    private LL1 Lang;
    /**
     * 开始符
     */
    private Character startChar;
    /**
     * 分析栈
     */
    private Stack<Character> analyzeStatck;
    /**
     * 剩余输入串
     */
    private String str;
    /**
     * 推导所用产生或匹配
     */
    private String useExp;

    public LL1 getLang() {
        return Lang;
    }

    public void setLang(LL1 lang) {
        Lang = lang;
    }

    public Character getStartChar() {
        return startChar;
    }

    public void setStartChar(Character startChar) {
        this.startChar = startChar;
    }

    public Stack<Character> getAnalyzeStatck() {
        return analyzeStatck;
    }

    public void setAnalyzeStatck(Stack<Character> analyzeStatck) {
        this.analyzeStatck = analyzeStatck;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getUseExp() {
        return useExp;
    }

    public void setUseExp(String useExp) {
        this.useExp = useExp;
    }

    public Analyzer(Controller controller) {
        this.controller = controller;
        analyzeStatck = new Stack<Character>();
        // 结束符进栈
        analyzeStatck.push('#');
    }

    /**
     * 分析
     */
    public LinkedList<Object[]> analyze() {

        LinkedList<Object[]> stack = new LinkedList<>();
        // 开始符进栈
        analyzeStatck.push(startChar);
        System.out.println("开始符:" + startChar);
        int index = 1;
        Object[] value = new Object[5];
        value[0] = index;
        value[1] = analyzeStatck.toString();
        value[2] = str;
        value[4] = "初始化";
        stack.add(value);

        while (!analyzeStatck.empty()) {
            index++;
            if (analyzeStatck.peek() != str.charAt(0)) {
                // 到分析表中找到这个产生式
                String nowUseExpStr = LL1.findUseExp(Lang.getSelectMap(), analyzeStatck.peek(), str.charAt(0));

                if (nowUseExpStr == null){
                    System.out.println("无法匹配");
                    return null;
                }
                // 将之前的分析栈中的栈顶出栈
                Character c = analyzeStatck.peek();
                analyzeStatck.pop();
                // 将要用到的表达式入栈,反序入栈  //为空串时不推入栈
                if (null != nowUseExpStr && nowUseExpStr.charAt(0) != 'ε') {
                    for (int j = nowUseExpStr.length() - 1; j >= 0; j--) {
                        char currentChar = nowUseExpStr.charAt(j);
                        analyzeStatck.push(currentChar);
                    }
                }

                /***************/
                Object[] value1 = new Object[5];
                value1[0] = index;
                value1[1] = analyzeStatck.toString();
                value1[2] = str;
                value1[3] = c+"->"+nowUseExpStr;
                value1[4] = "";
//                value[4] = "初始化";
                // TODO: 2017/10/17 操作
                stack.add(value1);
                /****************/

                System.out.println(index + "\t\t\t" + analyzeStatck.toString() + "\t\t\t" + str + "\t\t\t"
                        + c + "->" + nowUseExpStr);
            }
            // 如果可以匹配,分析栈出栈，串去掉一位
            if (analyzeStatck.peek() == str.charAt(0)) {
                Object[] value2 = new Object[5];
                value2[0] = index;
                value2[1] = analyzeStatck.toString();
                value2[2] = str;
                value2[3] = "";
                value2[4] = "“"+ str.charAt(0) + "”匹配";
//                value[4] = "初始化";
                // TODO: 2017/10/17 操作
                stack.add(value2);

                System.out.println(index + "\t\t\t" + analyzeStatck.toString() + "\t\t\t" + str + "\t\t\t" + "“"
                        + str.charAt(0) + "”匹配");

                analyzeStatck.pop();
                str = str.substring(1);
            }
        }

        return stack;
    }

}
