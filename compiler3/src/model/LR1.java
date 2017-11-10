package model;

import java.util.*;

/**
 * Created by Administrator on 2017/10/21.
 * LR1 项目集构造  分析表构造
 */
public class LR1 {

    public ArrayList<String> getLR1Arrays() {
        return LR1Arrays;
    }

    public Character getS() {
        return S;
    }

    public TreeSet<Character> getVnSet() {
        return VnSet;
    }

    public TreeSet<Character> getVtSet() {
        return VtSet;
    }

    public HashMap<Character, ArrayList<String>> getExpressionMap() {
        return expressionMap;
    }

    public HashMap<Character, TreeSet<Character>> getFirstMap() {
        return firstMap;
    }

    public HashMap<Integer, ItemSet> getItemSetFamily() {
        return ItemSetFamily;
    }

    /**
     * LR 文法表达式 存储数据
     */
    private ArrayList<String> LR1Arrays;
    /**
     * S 文法开始符
     */
    private Character S;
    /**
     * 非终结符集合
     */
    private TreeSet<Character> VnSet;
    /**
     * 终结符集合
     */
    private TreeSet<Character> VtSet;
    /**
     * 非终结符对应表达式 的集合
     */
    private HashMap<Character, ArrayList<String>> expressionMap;
    /**
     * 非终结符的 first集合
     */
    private HashMap<Character, TreeSet<Character>> firstMap;
    /**
     * LR1的 项目集族
     * Integer--状态编号；ItemSet 项目集对象
     */
    private HashMap<Integer,ItemSet> ItemSetFamily;
    /**
     * 规范LR分析表
     * Integer--状态；     Character--action中的终结符 or goto中的非终结符 ；
     *  String--内容                      [k,#]               [k,A]
     */
    private HashMap<Integer,HashMap<Character,String>> analysisTable;

    public HashMap<Integer, HashMap<Character, String>> getAnalysisTable() {
        return analysisTable;
    }

    public void setS(char e) {
        S = e;
    }

    public void setLL1Array(ArrayList<String> array) {
        LR1Arrays = array;
    }

    public LR1(){
        ItemSetFamily = new HashMap<>();//项目集族
        analysisTable = new HashMap<>();//分析表
        VtSet = new TreeSet<>();//终结符集合
        VnSet = new TreeSet<>();//非终结符集合
        expressionMap = new HashMap<>();//表达式集合
        firstMap = new HashMap<>();//first集合

        LR1Arrays = new ArrayList<>();
    }


    public void init() {
        initVnVt();
        initExpressionMaps();
        InitFirstSet();
        Init_ItemSetFamily();
        Init_table();

        System.out.println("终止符集合" + VtSet);
        System.out.println("非终止符集合" + VnSet);
        System.out.println("first集合" + firstMap);
        System.out.println("项目集族" + ItemSetFamily.toString());
        System.out.println("分析表" + analysisTable.toString());
    }

    /**
     * 初始化分析表
     */
    private void Init_table() {
        Iterator iter = ItemSetFamily.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ItemSet tempItemSet = (ItemSet) entry.getValue();
            HashMap<Character,String> table = analysisTable.get(tempItemSet.getNo());//获取状态的 分析表 对应列
            if (table == null) {
                table = new HashMap<>();
            }
            ArrayList<Item> itemList = tempItemSet.getItemList();//项目集数据
            for (Item item : itemList) {
                if(item.dot_right.equals("")){//归约
                    if(item.startVn.equals('@') && item.dot_left.equals(S.toString())){
                        table.put('#',"acc");
                    }else {
                        ArrayList<Character> nextCharList = item.searchList;
                        for (Character c : nextCharList) {
                            String Str = "r" + item.startVn + "->" + item.dot_left;//归约标志 和 产生式
                            table.put(c, Str);
                        }
                    }
                }
            }
            if (tempItemSet.getOutSet_list().size() != 0){//出度表
                LinkedList<Object[]> outRelation = tempItemSet.getOutSet_list();
                for (Object[] oSet :outRelation) {
                    if (VnSet.contains((Character) oSet[1])){
                        table.put((Character) oSet[1], oSet[0].toString());//非终结符
                    }else{
                        table.put((Character) oSet[1], "s" + oSet[0]);//终结符
                    }
                }
            }
            analysisTable.put(tempItemSet.getNo(),table);
        }
    }

    /**
     * 获取 first 集合
     * 实现方法：使用一个linkedList存储Nv，当某个Nv1的first集合为空时，需要这个集合的那个Nv2加到尾部，
     *          等Nv1计算完成后，在计算Nv2的first集合
     *
     * 待优化--------》递归函数
     */
    private void InitFirstSet() {
        //所有非终结符遍历 ，求其 first 集合
        Iterator<Character> iterator = VnSet.iterator();
        LinkedList<Character> saveNvUnable = new LinkedList<Character>();
        while (iterator.hasNext()){
            saveNvUnable.add(iterator.next());
        }
        while (!saveNvUnable.isEmpty()) {
            Character charItem = saveNvUnable.pop();//非终结符
            ArrayList<String> arrayList = expressionMap.get(charItem);//同一个非终结符的 所有表达式
            for (String str :arrayList) {
                char firstChar = str.charAt(0);//第一个字符
                TreeSet<Character> itemSet = firstMap.get(charItem);//非终结符 对应的first集合
                if (itemSet == null) {
                    itemSet = new TreeSet<>();
                }
                //////////////////////
                if(VtSet.contains(firstChar)){//第一个符号 终结符 or ε
                    itemSet.add(firstChar);
                    firstMap.put(charItem, itemSet);
                }else if(VnSet.contains(firstChar)){//第一个符号 非终结符
                    // TODO:遍历整个表达式
                    boolean toNext;//是否继续读下一个字符 true->是 , false->否
                    int place = 0;//遍历式子的位置
                    do {
                        if(VtSet.contains(firstChar) && firstChar!='ε'){//如果在遍历式子时 扫到了一个终结符
                            itemSet.add(firstChar);
                            firstMap.put(charItem, itemSet);
                            break;
                        }
                        TreeSet<Character> firstNv = firstMap.get(firstChar);
                        toNext = false;
                        if (firstNv == null) {
                            saveNvUnable.add(charItem);
                            break;// 跳出 for (String str :arrayList) 循环
                        } else {// 他的first集合不为空,遍历扫描 把不是空符号的字符存过去
                            Iterator<Character> it = firstNv.iterator();
                            while (it.hasNext()) {
                                Character member = it.next();
                                if (member == 'ε') {
                                    //有空字符 判断是否是最后一个字符 不是就要扫描下一个字符
                                    //是就要把ε加进去 然后结束
                                    if(place+1 == str.length()){
                                        itemSet.add(member);
                                        firstMap.put(charItem, itemSet);
                                        break;
                                    }
                                    toNext = true;
                                    firstChar = str.charAt(++place);
                                }
                                else {
                                    itemSet.add(member);
                                    firstMap.put(charItem, itemSet);
                                }
                            }
                        }
                    }while (toNext);
                }
                ///////////////////////
            }
        }
    }


    /**
     * 初始化 项目集族
     * 函数大概逻辑：
     *      初始化I0；
     *      I0.闭包()；
     *          add I0 to queue；
     *      if queue not 空：
     *          get top Ik；
     *          for each 文法符号：
     *          if(文法符号 在项目集Ik中的项目圆点右部第一个)：
     *              get each 符合要求的项目；
     *              create 项目集J；link Ik 出度 到 J ； get link J 入度 到 Ik；
     *              add each 项目 to J；
     *              J.闭包()；
     *              add J to queue；
     */
    private void Init_ItemSetFamily() {
        ItemSet I0 = new ItemSet();//项目集I0
        I0.setNo(0);//编号
        ArrayList<Character> temp = new ArrayList<>();
        temp.add('#');
        I0.addItemToList('@',"",S.toString(),temp);//[@->·S,#] 项目集I0中的第一个项目//使用@代表S'
        I0.closure(expressionMap,VnSet,firstMap);//闭包
        ItemSetFamily.put(0,I0);//初始化项目集族

        LinkedList<ItemSet> queue = new LinkedList<>();//项目集队列
        queue.add(I0);
        int No = 1;//项目集的编号
        while (!queue.isEmpty()){
            ItemSet Set = queue.pop();
            ArrayList<Item>itemList = (ArrayList<Item>)Set.getItemList().clone();
            // TODO: 2017/10/26 itemList使用的是项目集族中的项目集数据，删除itemList会改变 项目集族，要使用copy
            while (!itemList.isEmpty()) {
                ArrayList<Item> nextIkList = new ArrayList<>();//下一个项目集对象 的项目集合
                Character first = null;
                for (Iterator<Item> it = itemList.iterator(); it.hasNext(); ) {//每一次扫所有 右部第一个字符相同的项目
                    Item item1 = it.next();
//                        Item item = it.next();
                    Item item = new Item(item1);//要将项目克隆下，不然修改也会同步到原来的项目集中
                    if (item.isRightEmpty()) {
                        it.remove();
                        continue;
                    }
                    if (first == null) {
                        first = item.dot_right.charAt(0);
                        item.dot_left += first;
                        item.dot_right = item.dot_right.substring(1);
                        nextIkList.add(item);//对应弧上为first文法符号的 指向项目集 加入项目
                        it.remove();
                    }else if (first == item.dot_right.charAt(0)){
                        item.dot_left += first;
                        item.dot_right = item.dot_right.substring(1);
                        nextIkList.add(item);//对应弧上为first文法符号的 指向项目集 加入项目
                        it.remove();
                    }
                }
                if (nextIkList.size() != 0) {//判断项目集 是否为空

                    // 判断现在的项目集族 中是否有目前这个即将新建的项目集
                    // 项目集可能也会指向 自身，
                    boolean checkFlag = false;
                    Iterator iter = ItemSetFamily.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        ItemSet tempItemSet = (ItemSet) entry.getValue();//搜索的项目集
                        if (tempItemSet.getInSet_list().size() != 0) {//入弧集合不为空 //只能排除掉最初的那个项目集I0
//                            LinkedList<Object[]> linkedList = tempItemSet.getInSet_list();//查看入弧有没有上面的first符号
//                            for (Object[] relation :linkedList) {
//                                if(relation[1] == first){//存在first 检查是不是含有对应的项目
                            if (nextIkList.size() != 0) {//判断项目集 是否为空
                                Item item1 = nextIkList.get(0);//随便取一个
                                ArrayList<Item> tempitemList = tempItemSet.getItemList();
                                if (tempitemList.contains(item1)) {//确实对应了一个项目集
                                    // TODO: 2017/10/26 只添加了项目集之间的连接关系，不确定这次扫描出来的项目是否也都存在于项目集中
                                    LinkedList<Object[]> tempRelaionChange1 = Set.getOutSet_list();
                                    Object[] relationArray1 = {tempItemSet.getNo(), first};
                                    tempRelaionChange1.add(relationArray1);
                                    Set.setOutSet_list(tempRelaionChange1);
                                    ItemSetFamily.put(Set.getNo(), Set);
                                    tempItemSet = ItemSetFamily.get(tempItemSet.getNo());//也许上一步put的项目集是同一个，要同步一下
                                    LinkedList<Object[]> tempRelationChange2 = tempItemSet.getInSet_list();
                                    Object[] relationArray2 = {Set.getNo(), first};
                                    tempRelationChange2.add(relationArray2);
                                    tempItemSet.setInSet_list(tempRelationChange2);
                                    ItemSetFamily.put(tempItemSet.getNo(), tempItemSet);
                                    checkFlag = true;
                                    break;//扫描到后 立即跳出，否则会出现Concurrent Modification Exception；
                                    //即对 linkedList出现了并发同步修改 190line:for循环出错
                                }
                            }
                        }
                    }
                    if (!checkFlag) {//没找到，要新建项目集
                        ItemSet nextItemSet = new ItemSet();
                        nextItemSet.setNo(No);
                        nextItemSet.setItemList(nextIkList);
                        nextItemSet.closure(expressionMap, VnSet, firstMap);//闭包
                        //处理 项目集 之间的关系
                        LinkedList<Object[]> tempRelaionChange1 = Set.getOutSet_list();
                        Object[] relationArray1 = {nextItemSet.getNo(), first};
                        tempRelaionChange1.add(relationArray1);
                        Set.setOutSet_list(tempRelaionChange1);
                        ItemSetFamily.put(Set.getNo(), Set);
                        LinkedList<Object[]> tempRelationChange2 = nextItemSet.getInSet_list();
                        Object[] relationArray2 = {Set.getNo(), first};
                        tempRelationChange2.add(relationArray2);
                        nextItemSet.setInSet_list(tempRelationChange2);
                        ItemSetFamily.put(nextItemSet.getNo(), nextItemSet);

                        ItemSetFamily.put(No, nextItemSet);//初始化项目集族
                        No++;
                        queue.add(nextItemSet);
                    }
                }
            }
        }
    }

    /**
     * 获取非终结符集与终结符集
     */
    public void initVnVt() {
        for (String gsItem : LR1Arrays){
            String[] nvNtItem = gsItem.split("->");
            String charItemStr = nvNtItem[0];
            char charItem = charItemStr.charAt(0);
            // nv在左边
            VnSet.add(charItem);
        }
        for (String gsItem : LR1Arrays) {
            String[] nvNtItem = gsItem.split("->");
            // nt在右边
            String nvItemStr = nvNtItem[1];
            // 遍历每一个字
            for (int i = 0; i < nvItemStr.length(); i++) {
                char charItem = nvItemStr.charAt(i);
                if (!VnSet.contains(charItem)) {
                    VtSet.add(charItem);
                }
            }
        }
    }
    /**
     * 初始化表达式集合
     */
    public void initExpressionMaps() {
        expressionMap = new HashMap<Character, ArrayList<String>>();
        for (String gsItem : LR1Arrays) {
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

}
