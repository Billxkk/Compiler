package model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/26.
 * 项目类
 */
public class Item {
    /**
     * 产生式左部
     */
    public Character startVn = '!';
    /**
     * 圆点左部
     */
    public String dot_left = "";
    /**
     * 圆点右部
     */
    public String dot_right = "";
    /**
     * 搜索符 集合
     */
    public ArrayList<Character> searchList;
    /**构造空数据 的项目**/
    public Item(){
        searchList = new ArrayList<>();
    }
    /** 按数据构造 项目 **/
    public Item(Character startVn,String dot_left,String dot_right,ArrayList<Character> searchList){
        this.searchList = new ArrayList<>();
        this.startVn = startVn;
        this.dot_right = dot_right;
        this.dot_left = dot_left;
        this.searchList = searchList;
    }
    /** 克隆项目**/
    public Item(Item item1) {
        searchList = new ArrayList<>();
        this.startVn = item1.startVn;
        this.dot_right = item1.dot_right;
        this.dot_left = item1.dot_left;
        this.searchList = (ArrayList<Character>) item1.searchList.clone();
    }

    /**
     *判断圆点 左部是否为空
     * @return
     */
    public boolean isLeftEmpty(){
        if(dot_left.equals(""))
            return true;
        else
            return false;
    }
    /**
     *判断圆点 右部是否为空
     * @return
     */
    public boolean isRightEmpty(){
        if (dot_right.equals(""))
            return true;
        else
            return false;
    }

    //arraylist的contain方法调用equals方法，需要重写，否则使用Objects类的equals方法返回为false
    //http://blog.csdn.net/u010015108/article/details/52798966
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item) {
            Item temp = (Item) obj;
            if (dot_right.equals(temp.dot_right) && dot_left.equals(temp.dot_left) && startVn.equals(temp.startVn)
                    && searchList.equals(temp.searchList))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public String toString(){
        return "Item[" + startVn + "," + dot_left + "," + dot_right + "," + searchList.toString() + "]";
    }
}
