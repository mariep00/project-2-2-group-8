public class Teleport {

    private int index;

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    private int xExit;
    private int yExit;
    private double rotation;
    
    public Teleport (int x1, int y1, int x2, int y2, int x3, int y3, double rotation) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        xExit = x3;
        yExit = y3;
        this.rotation = rotation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public int getxExit() {
        return xExit;
    }

    public int getyExit() {
        return yExit;
    }

    public double getRotation() {
        return rotation;
    }
}
