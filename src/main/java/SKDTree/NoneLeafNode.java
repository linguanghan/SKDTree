package SKDTree;

import java.io.Serializable;

public class NoneLeafNode implements Serializable {
    private double[] range;//结点所表示的超平面范围
    private int dim;//划分维度
    private double key;//划分关键值
    private double f;//划分权值
    private double density;//节点密度
    private double t_last;//上次更新密度时间
    public NoneLeafNode ptr_left;//左指针
    public NoneLeafNode ptr_right;//右指针
    public SubLeafNode ptr_sub_left;//亚叶子节点的左指针
    public SubLeafNode ptr_sub_right;//亚叶子节点的左指针
    private boolean resoluteOrNot = false;//判断是否归结：true：已归结，false：未归结
    private String type;//归结后的类别
    private boolean findOrNot = false;//判断是否被找过

    public NoneLeafNode() {
        this.ptr_left = null;
        this.ptr_right = null;
        this.ptr_sub_left = null;
        this.ptr_sub_right = null;
    }

    public NoneLeafNode(double[] range) {
        this();
        this.range = range;
    }

    public NoneLeafNode(double[] range, int dim, double key, double f, double density, double t_last) {
        this();
        this.range = range;
        this.dim = dim;
        this.key = key;
        this.f = f;
        this.density = density;
        this.t_last = t_last;
    }

    public double[] getRange() {
        return range;
    }

    public int getDim() {
        return dim;
    }

    public double getKey() {
        return key;
    }

    public double getF() {
        return f;
    }

    public double getDensity() {
        return density;
    }

    public void setRange(double[] range) {
        this.range = range;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public void setKey(double key) {
        this.key = key;
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
