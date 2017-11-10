public class Fan{
    /**
     *风扇的有效速度
     */
    public final static int SLOW = 1;
    public final static int MEDIUM = 2;
    public final static int FAST = 3;
    /**风扇的当前速度 默认slow*/
    private int speed = SLOW;
    /** 开关 */
    private boolean on = false;
    /** 半径 */
    private double radius = 5.0;
    /** 颜色 */
    private String color = "blue";

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Fan(){

    }

    public String toString(){
        if(on)
            return "Fan:[speed:" + speed  + ",radius:" + radius + ",color:" + color + "]";
        else
            return "Fan:[radius:" + radius + ",color:" + color + ",fan is off]";
    }

}