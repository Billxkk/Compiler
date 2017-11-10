
import model.Analyzer;
import model.Gs;

import java.util.*;


public class Main {


    public static void main(String[] args) throws Exception {
        // LL（1）文法产生集合 存储文法语句  默认为LL(1)文法
        ArrayList<String> gsArray = new ArrayList<String>();

        Gs gs = new Gs();
        initGs(gsArray);
        gs.setGsArray(gsArray);

        gs.getNvNt();//获取 终结符 和 非终结符 的集合
        gs.initExpressionMaps();//构造 表达式 的数据结构
        gs.setS('E');// 设置开始符
        gs.getFirst();//构造所有first集合
        gs.getFollow();
        gs.getSelect();
        // 创建一个分析器
        Analyzer analyzer = new Analyzer();
        analyzer.setStartChar('E');
        analyzer.setLl1Gs(gs);
        analyzer.setStr("i+i*i#");
        analyzer.analyze();
        gs.genAnalyzeTable();
        System.out.println("program finished ");



        HashMap<Character, TreeSet<Character>> first = gs.getFirstMap();
        Iterator iter = first.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key.toString() + "---" + val.toString());
        }

        System.out.println("________________________");

        HashMap<Character, TreeSet<Character>> follow = gs.getFollowMap();
        Iterator iter2 = follow.entrySet().iterator();
        while (iter2.hasNext()) {
            Map.Entry entry = (Map.Entry) iter2.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key.toString() + "---" + val.toString());
        }

    }



    /**
     * 初始化LL(1)文法
     *
     * @param gsArray
     */
    private static void initGs(ArrayList<String> gsArray) {
        gsArray.add("E->TG");
        gsArray.add("G->+TG");
        gsArray.add("G->-TG");
        gsArray.add("G->ε");
        gsArray.add("T->FS");
        gsArray.add("S->*FS");
        gsArray.add("S->/FS");
        gsArray.add("S->ε");
        gsArray.add("F->(E)");
        gsArray.add("F->i");
    }
}

