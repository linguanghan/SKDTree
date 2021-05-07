package optics;

public class ClusterDataPoint {
    private String name;
    private double[] dimension;
    private int clusterType;

    public ClusterDataPoint() {
    }

    public ClusterDataPoint(String name, double[] dimension, int clusterType) {
        this.name = name;
        this.dimension = dimension;
        this.clusterType = clusterType;
    }

    public ClusterDataPoint(int clusterType) {
        this.clusterType = clusterType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getDimension() {
        return dimension;
    }

    public void setDimension(double[] dimension) {
        this.dimension = dimension;
    }

    public int getClusterType() {
        return clusterType;
    }

    public void setClusterType(int clusterType) {
        this.clusterType = clusterType;
    }
}
