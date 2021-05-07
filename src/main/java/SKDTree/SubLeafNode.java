package SKDTree;

import java.io.Serializable;
import java.util.ArrayList;

public class SubLeafNode implements Serializable {
    private double[] range;//节点所代表的超平面范围
    private double f;//生命权值
    private double density;//密度
    private double t_last;//上次跟更新密度的时间
    private int numOfLeafNodes;//用来存储叶子节点总个数
    public ArrayList<LeafNode> leafNodes = new ArrayList<>();//存储叶子节点
    private boolean resoluteOrNot = false; //是否归结
    private String type; //归结后的类别
    private boolean findOrNot = false;//判断是否被找过

    public SubLeafNode() {

    }

    public SubLeafNode(double[] range, double f, double density, double t_last) {
        this();
        this.range = range;
        this.f = f;
        this.density = density;
        this.t_last = t_last;
    }

    public void setRangeI(double rangeI, int i) {
        this.range[i] = rangeI;
    }

    public double[] getRange() {
        return range;
    }

    public double getF() {
        return f;
    }

    public double getDensity() {
        return density;
    }

    public double getT_last() {
        return t_last;
    }

    public void setRange(double[] range) {
        this.range = range;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public void setT_last(double t_last) {
        this.t_last = t_last;
    }

    public int getNumOfLeafNodes() {
        return numOfLeafNodes;
    }

    public void setNumOfLeafNodes(int numOfLeafNodes) {
        this.numOfLeafNodes = numOfLeafNodes;
    }

    public boolean isResoluteOrNot() {
        return resoluteOrNot;
    }

    public void setResoluteOrNot(boolean resoluteOrNot) {
        this.resoluteOrNot = resoluteOrNot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFindOrNot() {
        return findOrNot;
    }

    public void setFindOrNot(boolean findOrNot) {
        this.findOrNot = findOrNot;
    }

    public double getArea() {
        double area = 1;
        for (int i = 0; i < this.range.length / 2; i++) {
            area *= (this.range[2 * i + 1] - this.range[2 * i]);
        }
        return area;
    }

    public int getNumberOfPoints() {
        return (int) (this.density * this.getArea());
    }

    public void setRecByIndex(double newBorder, int index) {
        this.range[index] = newBorder;
    }
}
