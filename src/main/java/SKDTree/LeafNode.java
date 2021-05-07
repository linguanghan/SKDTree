package SKDTree;

import java.io.Serializable;

public class LeafNode implements Serializable {
    private double[] mk;//最小外包立方体
    private double f;//权值
    private double pm;//叶子节点密度
    private double t_last;//上次更新pm的时间
    private int nodeNum;//节点序号
    private String name;//节点名称
    private String type;//归结后的类型

    public LeafNode() {

    }

    public LeafNode(double[] mk, double f, double pm, double t_last, int nodeNum, String name) {
        this.mk = mk;
        this.f = f;
        this.pm = pm;
        this.t_last = t_last;
        this.nodeNum = nodeNum;
        this.name = name;
    }

    public LeafNode(Rectangle rectangle){
        this.mk = rectangle.getRecs();
        this.pm = rectangle.getDensity();
    }

    public double[] getMk() {
        return mk;
    }

    public double getF() {
        return f;
    }

    public double getPm() {
        return pm;
    }

    public double getT_last() {
        return t_last;
    }

    public void setMk(double[] mk) {
        this.mk = mk;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setPm(double pm) {
        this.pm = pm;
    }

    public void setT_last(double t_last) {
        this.t_last = t_last;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getArea() {
        double area = 1;
        for (int i = 0; i < this.mk.length / 2; i++) {
            area *= (this.mk[2 * i + 1] - this.mk[2 * i]);
        }
        return area;
    }
}
