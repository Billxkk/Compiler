package model;

import java.io.Serializable;
import java.util.*;

/**
 * @author PuHaiyang
 * @createTime 2016年6月10日 下午7:46:33
 * @email 761396462@qq.com
 * @function LL(1)文法
 *
 */
public class Gs implements Serializable {

    private static final long serialVersionUID = 1L;

    public Gs() {
        super();
        gsArray = new ArrayList<String>();
        nvSet = new TreeSet<Character>();
        ntSet = new TreeSet<Character>();
        firstMap = new HashMap<Character, TreeSet<Character>>();
        followMap = new HashMap<Character, TreeSet<Character>>();
        selectMap = new TreeMap<Character, HashMap<String, TreeSet<Character>>>();
    }

    /**
     * 分析预测表
     */
    private String[][] analyzeTable;
    /**
     * LL（1）文法产生集合
     */
    private ArrayList<String> gsArray;
    /**
     * 表达式集合    左部 -> 同一个左部的右部集合
     */
    private HashMap<Character, ArrayList<String>> expressionMap;
    /**
     * 开始符
     */
    private Character s;
    /**
     * Vn非终结符集合
     */
    private TreeSet<Character> nvSet;
    /**
     * Vt终结符集合
     */
    private TreeSet<Character> ntSet;
    /**
     * First集合  非终结符 -> 终结符集合
     */
    private HashMap<Character, TreeSet<Character>> firstMap;
    /**
     * Follow集合  非终结符 -> 终结符集合
     */
    private HashMap<Character, TreeSet<Character>> followMap;
    /**
     * Select集合
     */
    private TreeMap<Character, HashMap<String, TreeSet<Character>>> selectMap;



    public TreeMap<Character, HashMap<String, TreeSet<Character>>> getSelectMap() {
        return selectMap;
    }

    public void setGsArray(ArrayList<String> gsArray) {
        this.gsArray = gsArray;
    }

    public void setS(Character s) {
        this.s = s;
    }

    public HashMap<Character, TreeSet<Character>> getFirstMap() {
        return firstMap;
    }

    public HashMap<Character, TreeSet<Character>> getFollowMap() {
        return followMap;
    }

    /**
     * 获取非终结符集与终结符集
     *
     * @param gsArray
     * @param nvSet
     * @param ntSet
     */
    public void getNvNt() {
        for (String gsItem : gsArray){
                String[] nvNtItem = gsItem.split("->");
                String charItemStr = nvNtItem[0];
                char charItem = charItemStr.charAt(0);
                // nv在左边
                nvSet.add(charItem);
            }

            for (String gsItem : gsArray) {
                String[] nvNtItem = gsItem.split("->");
                // nt在右边
                String nvItemStr = nvNtItem[1];
                // 遍历每一个字
                for (int i = 0; i < nvItemStr.length(); i++) {
                char charItem = nvItemStr.charAt(i);
                if (!nvSet.contains(charItem)) {
                    ntSet.add(charItem);
                }
            }
        }

        Iterator<Character> iterator = nvSet.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("--------------");
        iterator = ntSet.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

    /**
     * 初始化表达式集合
     */
    public void initExpressionMaps() {
        expressionMap = new HashMap<Character, ArrayList<String>>();
        for (String gsItem : gsArray) {
            String[] nvNtItem = gsItem.split("->");
            String charItemStr = nvNtItem[0];
            String charItemRightStr = nvNtItem[1];//表达式的右部，结果
            char charItem = charItemStr.charAt(0);//表达式的左部，非终结符 字符
            if (!expressionMap.containsKey(charItem)) {
                ArrayList<String> expArr = new ArrayList<String>();
                expArr.add(charItemRightStr);
                expressionMap.put(charItem, expArr);
            } else {
                ArrayList<String> expArr = expressionMap.get(charItem);
                expArr.add(charItemRightStr);
                expressionMap.put(charItem, expArr);
            }
        }
    }

    /**
     * 获取First集
     */
    public void getFirst() {
        // 遍历所有Nv 非终结符,求出它们的First集合
        Iterator<Character> iterator = nvSet.iterator();
        while (iterator.hasNext()) {
            Character charItem = iterator.next();
            ArrayList<String> arrayList = expressionMap.get(charItem);//同一个非终结符的 所有表达式
            for (String itemStr : arrayList) {
                boolean shouldBreak = false;
                // Y1Y2Y3...Yk
                for (int i = 0; i < itemStr.length(); i++) {
                    char itemitemChar = itemStr.charAt(i);// 逐个获取 表达式的字符
                    TreeSet<Character> itemSet = firstMap.get(charItem);//非终结符 对应的first集合
                    if (null == itemSet) {
                        itemSet = new TreeSet<Character>();
                    }
                    shouldBreak = calcFirst(itemSet, charItem, itemitemChar);
                    if (shouldBreak) {
                        System.out.println("test--------break");
                        break;
                    }
                }
            }
        }


    }

    /**
     * 计算First函数
     *  若itemitemChar为终结符或空串，返回true ; else 返回false
     * @param itemSet   first集合
     * @param charItem  first集合所属的 非终结符
     * @param itemitemChar 表达式 当前正在识别的字符
     * @return
     */
    private boolean calcFirst(TreeSet<Character> itemSet, Character charItem, char itemitemChar) {
        if (itemitemChar == 'ε' || ntSet.contains(itemitemChar)) {
            itemSet.add(itemitemChar);
            firstMap.put(charItem, itemSet);
            return true;
        } else if (nvSet.contains(itemitemChar)) {// 这一位是一个非终结符
            ArrayList<String> arrayList = expressionMap.get(itemitemChar);
            for (int i = 0; i < arrayList.size(); i++) {
                String string = arrayList.get(i);
                char tempChar = string.charAt(0);
                calcFirst(itemSet, charItem, tempChar);
            }
        }
        return true;
    }

    /**
     * 获取Follow集合
     */
    public void getFollow() {
        for (Character tempKey : nvSet) {
            TreeSet<Character> tempSet = new TreeSet<Character>();
            followMap.put(tempKey, tempSet);
        }
        // 遍历所有Nv,求出它们的Follow集合
        Iterator<Character> iterator = nvSet.descendingIterator();
        // nvSet.descendingIterator();

        while (iterator.hasNext()) {
            Character charItem = iterator.next();
            System.out.println("charItem:" + charItem);
            Set<Character> keySet = expressionMap.keySet();
            for (Character keyCharItem : keySet) {
                ArrayList<String> charItemArray = expressionMap.get(keyCharItem);
                for (String itemCharStr : charItemArray) {
                    System.out.println(keyCharItem + "->" + itemCharStr);
                    TreeSet<Character> itemSet = followMap.get(charItem);
                    calcFollow(charItem, charItem, keyCharItem, itemCharStr, itemSet);
                }
            }
        }
    }

    /**
     * 计算Follow集
     *
     * @param putCharItem
     *            正在查询item
     * @param charItem
     *            待找item
     * @param keyCharItem
     *            节点名
     * @param itemCharStr
     *            符号集
     * @param itemSet
     *            结果集合
     */
    private void calcFollow(Character putCharItem, Character charItem, Character keyCharItem, String itemCharStr,
                            TreeSet<Character> itemSet) {
        ///////
        // （1）A是S（开始符)，加入#
        if (charItem.equals(s)) {
            itemSet.add('#');
            System.out.println("---------------find S:" + charItem + "   ={#}+Follow(E)");
            followMap.put(putCharItem, itemSet);
            // return;
        }
        // (2)Ab,=First(b)-ε,直接添加终结符
        if (TextUtil.containsAb(ntSet, itemCharStr, charItem)) {
            Character alastChar = TextUtil.getAlastChar(itemCharStr, charItem);
            System.out.println("---------------find Ab:" + itemCharStr + "    " + charItem + "   =" + alastChar);
            itemSet.add(alastChar);
            followMap.put(putCharItem, itemSet);
            // return;
        }
        // (2).2AB,=First(B)-ε，添加first集合
        if (TextUtil.containsAB(nvSet, itemCharStr, charItem)) {
            Character alastChar = TextUtil.getAlastChar(itemCharStr, charItem);
            System.out.println(
                    "---------------find AB:" + itemCharStr + "    " + charItem + "   =First(" + alastChar + ")");
            TreeSet<Character> treeSet = firstMap.get(alastChar);
            itemSet.addAll(treeSet);
            if (treeSet.contains('ε')) {
                itemSet.add('#');
            }
            itemSet.remove('ε');
            followMap.put(putCharItem, itemSet);
            ///////////////////////
            if (TextUtil.containsbAbIsNull(nvSet, itemCharStr, charItem, expressionMap)) {
                char tempChar = TextUtil.getAlastChar(itemCharStr, charItem);
                System.out.println("tempChar:" + tempChar + "  key" + keyCharItem);
                if (!keyCharItem.equals(charItem)) {
                    System.out.println("---------------find tempChar bA: " + "tempChar:" + tempChar + keyCharItem
                            + "   " + itemCharStr + "    " + charItem + "   =Follow(" + keyCharItem + ")");
                    Set<Character> keySet = expressionMap.keySet();
                    for (Character keyCharItems : keySet) {
                        ArrayList<String> charItemArray = expressionMap.get(keyCharItems);
                        for (String itemCharStrs : charItemArray) {
                            calcFollow(putCharItem, keyCharItem, keyCharItems, itemCharStrs, itemSet);
                        }
                    }
                }
            }
        }
        // (3)B->aA,=Follow(B),添加followB
        if (TextUtil.containsbA(nvSet, itemCharStr, charItem, expressionMap)) {
            if (!keyCharItem.equals(charItem)) {
                System.out.println("---------------find bA: " + keyCharItem + "   " + itemCharStr + "    " + charItem
                        + "   =Follow(" + keyCharItem + ")");
                Set<Character> keySet = expressionMap.keySet();
                for (Character keyCharItems : keySet) {
                    ArrayList<String> charItemArray = expressionMap.get(keyCharItems);
                    for (String itemCharStrs : charItemArray) {
                        calcFollow(putCharItem, keyCharItem, keyCharItems, itemCharStrs, itemSet);
                    }
                }
            }
        }
    }

    /**
     * 获取Select集合
     */
    public void getSelect() {
        // 遍历每一个表达式
        // HashMap<Character, HashMap<String, TreeSet<Character>>>
        Set<Character> keySet = expressionMap.keySet();
        for (Character selectKey : keySet) {
            ArrayList<String> arrayList = expressionMap.get(selectKey);
            // 每一个表达式
            HashMap<String, TreeSet<Character>> selectItemMap = new HashMap<String, TreeSet<Character>>();
            for (String selectExp : arrayList) {
                /**
                 * 存放select结果的集合
                 */
                TreeSet<Character> selectSet = new TreeSet<Character>();
                // set里存放的数据分3种情况,由selectExp决定
                // 1.A->ε,=follow(A)
                if (TextUtil.isEmptyStart(selectExp)) {
                    selectSet = followMap.get(selectKey);
                    selectSet.remove('ε');
                    selectItemMap.put(selectExp, selectSet);
                }
                // 2.Nt开始,=Nt
                // <br>终结符开始
                if (TextUtil.isNtStart(ntSet, selectExp)) {
                    selectSet.add(selectExp.charAt(0));
                    selectSet.remove('ε');
                    selectItemMap.put(selectExp, selectSet);
                }
                // 3.Nv开始，=first(Nv)
                if (TextUtil.isNvStart(nvSet, selectExp)) {
                    selectSet = firstMap.get(selectKey);
                    selectSet.remove('ε');
                    selectItemMap.put(selectExp, selectSet);
                }
                selectMap.put(selectKey, selectItemMap);
            }
        }
    }

    /**
     * 生成预测分析表
     */
    public void genAnalyzeTable() throws Exception {
        Object[] ntArray = ntSet.toArray();
        Object[] nvArray = nvSet.toArray();
        // 预测分析表初始化
        analyzeTable = new String[nvArray.length + 1][ntArray.length + 1];

        // 输出一个占位符
        System.out.print("Nv/Nt" + "\t\t");
        analyzeTable[0][0] = "Nv/Nt";
        // 初始化首行
        for (int i = 0; i < ntArray.length; i++) {
            if (ntArray[i].equals('ε')) {
                ntArray[i] = '#';
            }
            System.out.print(ntArray[i] + "\t\t");
            analyzeTable[0][i + 1] = ntArray[i] + "";
        }

        System.out.println("");
        for (int i = 0; i < nvArray.length; i++) {
            // 首列初始化
            System.out.print(nvArray[i] + "\t\t");
            analyzeTable[i + 1][0] = nvArray[i] + "";
            for (int j = 0; j < ntArray.length; j++) {
                String findUseExp = TextUtil.findUseExp(selectMap, Character.valueOf((Character) nvArray[i]),
                        Character.valueOf((Character) ntArray[j]));
                if (null == findUseExp) {
                    System.out.print("\t\t");
                    analyzeTable[i + 1][j + 1] = "";
                } else {
                    System.out.print(nvArray[i] + "->" + findUseExp + "\t\t");
                    analyzeTable[i + 1][j + 1] = nvArray[i] + "->" + findUseExp;
                }
            }
            System.out.println();
        }
    }
}
