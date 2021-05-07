package SKDTree;

import java.util.Arrays;

public class Rectangle implements Cloneable {
    public static int numberOfRecs = 0;
    private char name;
    private double[] recs;//框
    private double density;//密度
    private int num;//框内点个数
    private String pointName;//框内点名称
    private String subName = "";
    private String divide_Name = "";//分割名
    private String divideSubName = "";//分割子名

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName += subName;
    }

    public Rectangle() {
    }

    public Rectangle(double[] recs, int num, String pointName) {
        this.name = (char) (64 + numberOfRecs);
        this.recs = recs;
        this.num = num;
        this.pointName = pointName;
        double area = 1.0;
        for (int i = 0; i < recs.length / 2; i++) {
            area *= (recs[i * 2 + 1] - recs[i * 2]);
        }

        this.density = this.num / area;
        numberOfRecs++;
    }

    public Rectangle(double[] recs,double density) {
        this.recs = recs;
        this.name = (char) (64 + numberOfRecs);
        this.density = density;
        numberOfRecs++;
    }

    public Rectangle(double[] recs) {
        this.recs = recs;
    }

    public Rectangle(double[] recs, double density, char name, String subName) {
        this.name = name;
        this.recs = recs;
        this.density = density;
        this.subName += subName;
    }

    public static int getNumberOfRecs() {
        return numberOfRecs;
    }

    public static void setNumberOfRecs(int numberOfRecs) {
        Rectangle.numberOfRecs = numberOfRecs;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getDivide_Name() {
        return divide_Name;
    }

    public void setDivide_Name(String divide_Name) {
        this.divide_Name = divide_Name;
    }

    public double[] getRecs() {
        return recs;
    }

    public void setRecs(double[] recs) {
        this.recs = recs;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public String getDivideSubName() {
        return divideSubName;
    }

    public void setDivideSubName(String divideSubName) {
        this.divideSubName = divideSubName;
    }

    public double getArea() {
        double area = 1;
        for (int i = 0; i < this.recs.length / 2; i++) {
            area *= (this.recs[2 * i + 1] - this.recs[2 * i]);
        }
        return area;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "name=" + name +
                ", recs=" + Arrays.toString(recs) +
                ", density=" + density +
                ", num=" + num +
                ", pointName='" + pointName + '\'' +
                ", subName='" + subName + '\'' +
                ", divide_Name='" + divide_Name + '\'' +
                ", divideSubName='" + divideSubName + '\'' +
                '}';
    }

    public void setRecByIndex(double newBorder, int index) {
        this.recs[index] = newBorder;
    }

    @Override
    public Object clone() {
        Rectangle rec = null;
        try {
            rec = (Rectangle) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        rec.recs = (double[]) recs.clone();
        return rec;
    }

    public int getNumberOfPoints() {
        return (int) (this.density * this.getArea());
    }
}
