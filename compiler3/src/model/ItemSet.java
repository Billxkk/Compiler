package model;

import java.util.*;

/**
 * Created by Administrator on 2017/10/21.
 * 存储一个项目集相关数据的 类
 */
public class ItemSet {

    /**
     * 项目集的 序号
     */
    private int No;
    /**
     * 项目集 中的 项目
     */
    private ArrayList<Item> itemList;
    /**
     * 指向该项目集的 项目集序号列表
     * Object[] 存储 源头的序号 and 指向弧上的文法符号
     */
    private LinkedList<Object[]> inSet_list;
    /**
     * 该项目指向其他的项目集 的序号列表
     * Object[] 存储 指向的序号 and 指向弧上的文法符号
     */
    private LinkedList<Object[]> outSet_list;

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    public LinkedList<Object[]> getInSet_list() {
        return inSet_list;
    }

    public void setInSet_list(LinkedList<Object[]> inSet_list) {
        this.inSet_list = inSet_list;
    }

    public LinkedList<Object[]> getOutSet_list() {
        return outSet_list;
    }

    public void setOutSet_list(LinkedList<Object[]> outSet_list) {
        this.outSet_list = outSet_list;
    }


    public ItemSet(){
        itemList = new ArrayList<>();
        inSet_list = new LinkedList<>();
        outSet_list = new LinkedList<>();

    }


    /**
     * 构造闭包
     * @param expressionMap
     * @param vnSet
     * @param firstMap
     * 函数大概逻辑：
     *      将当前有的所有项目加入队列；
     *      for each item in queue：
     *          get 项目；
     *          if（项目圆点后为Vn）：
     *              加入新项目{
     *                  get first集合 of βa;
     *                  加入 [B -> ·γ，b]
     *              }
     *              新项目加入队列；
     *          若都队列不空，继续直到队列为空；
     * 注意！：还有项目产生式相同，但是后缀不同，要合并下
     */
    public void closure(HashMap<Character, ArrayList<String>> expressionMap, TreeSet<Character> vnSet,
                        HashMap<Character, TreeSet<Character>> firstMap) {
        LinkedList<Item> queue = new LinkedList<>();
        for (Item item :itemList) {
            queue.add(item);
        }

        while (!queue.isEmpty()){
            Item item = queue.pop();
            String dot_right = item.dot_right;//圆点右部字符串
            if(!dot_right.equals("") && vnSet.contains(dot_right.charAt(0))){//不是归约项目
                Character B = dot_right.charAt(0);//圆点右部第一个非终结字符 B
                ArrayList<String> StrList =  expressionMap.get(B);//非终结符B对应的 产生式集合
                dot_right = dot_right.substring(1);//除去非终结符 B 后的圆点右部

                if(dot_right.equals("")){//除去B 后圆点右部为空
                    for (String str : StrList) {
                        if(!str.equals("ε")){
                            if(addItemToList(B,"",str,item.searchList)){
                                Item item1 = new Item(B,"",str,item.searchList);
                                queue.add(item1);
                            }
                        }else{
                            if(addItemToList(B,"","",item.searchList)){
                                Item item1 = new Item(B,"","",item.searchList);
                                queue.add(item1);
                            }
                        }
                    }
                }else{//第一个字符为 终结符 or 非终结符
                                                    // TODO: 2017/10/21 未编写终结符为空串的情况
                    ArrayList<Character> temp = new ArrayList<>();
                    if(!vnSet.contains(dot_right.charAt(0))){//终结符
                        temp.add(dot_right.charAt(0));
                    }else{      //非终结符
                        Character Vn = dot_right.charAt(0);
                        TreeSet<Character> temp1 = firstMap.get(Vn);
                        Iterator its = temp1.iterator();// 迭代器，set集合中每个元素依次计算其出现次数
                        while (its.hasNext()) {
                            temp.add((Character) its.next());// 可能会有 并发修改异常 不知道是什么
                        }
                    }
                    for (String str : StrList) {
                        if(!str.equals("ε")){
                            if(addItemToList(B,"",str,temp)){
                                Item item1 = new Item(B,"",str,temp);
                                queue.add(item1);
                            }
                        }else{
                            if(addItemToList(B,"","",temp)){
                                Item item1 = new Item(B,"","",temp);
                                queue.add(item1);
                            }
                        }
                    }
                }
            }
        }
        //合并产生式相同 ，但是后缀不同的项目

    }


    /**
     * 添加项目 到项目集
     * @param startVn 左部
     * @param dot_left  圆点左部 为空时设置为""
     * @param dot_right 圆点右部 为空时设置为""
     * @param searchList  搜索符集合
     */
    public boolean addItemToList(Character startVn,String dot_left,String dot_right,ArrayList<Character> searchList){
        Item temp = new Item();
        temp.startVn = startVn;
        temp.dot_left = dot_left;
        temp.dot_right = dot_right;
        temp.searchList = searchList;
        if(!itemList.contains(temp)) {//没有的话 就添加
            int index;
            if((index = checkForProduce(temp)) != -1){//有项目但是只是后缀不同
//                Item item = itemList.get(index);
//                item.searchList.addAll(temp.searchList);
                //todo:上面的代码会将itemList中的所有item的searchList同时addAll，也就是所有的item都会变。。。不明白原因
                Item item = new Item(itemList.get(index));
                boolean flag = false;
                for (Character c :searchList) {
                    if (!item.searchList.contains(c)){
                        item.searchList.add(c);
                        flag = true;
                    }
                }
                if (flag) {
                    itemList.remove(index);
                    itemList.add(item);
                    return true;
                }else
                    return false;
            }
            itemList.add(temp);
            return true;
        }
        return false;
    }

    /**
     * 检查项目集中项目产生式相同而后缀不同，对应的那个项目的位置
     * @return
     * @param temp
     */
    private int checkForProduce(Item temp) {
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (temp.startVn.equals(item.startVn) && temp.dot_left.equals(item.dot_left) && temp.dot_right.equals(item.dot_right))
                return i;
        }
        return -1;
    }


    public String toString(){
        String inStr = "";
        for (Object[] a : inSet_list) {
            inStr += Arrays.toString(a);
        }
        String outStr = "";
        for (Object[] a : outSet_list) {
            outStr += Arrays.toString(a);
        }
        return "ItemSet{编号" + No + ",项目集" + itemList.toString() + ",入度集[" + inStr + "],出度集["
                + outStr +"]}\n";
    }
}
