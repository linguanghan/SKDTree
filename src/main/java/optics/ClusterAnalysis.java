package optics;

import SKDTree.Rectangle;
import com.sun.org.apache.bcel.internal.generic.FSUB;
import data.Data;
import data.ReadTxt;
import utils.MyUtils;

import java.lang.reflect.Array;
import java.util.*;

public class ClusterAnalysis {
    class ComparatorDp implements Comparator<DataPoint> {
        public int compare(DataPoint arg0, DataPoint arg1) {
            double temp = arg0.getReachableDistance()
                    - arg1.getReachableDistance();
            int a = 0;
            if (temp < 0) {
                a = -1;
            } else {
                a = 1;
            }
            return a;
        }
    }

    public List<ClusterDataPoint> startAnalysis(List<DataPoint> dataPoints,
                                                double radius, int ObjectNum) {
        List<DataPoint> dpList = new ArrayList<>();// 结果队列
        List<DataPoint> dpQue = new ArrayList<>();// 样本
        List<ClusterDataPoint> clusterDataPoints = new ArrayList<>();

        int total = 0;
        int clusterType = 0;
        while (total < dataPoints.size()) {
            if (isContainedInList(dataPoints.get(total), dpList) == -1) {
                List<DataPoint> tmpDpList = isKeyAndReturnObjects(
                        dataPoints.get(total), dataPoints, radius, ObjectNum);
                if (tmpDpList != null && tmpDpList.size() > 0) {
                    clusterType++;
                    DataPoint newDataPoint = new DataPoint(
                            dataPoints.get(total));
                    dpQue.add(newDataPoint);
                }
            }
            while (!dpQue.isEmpty()) {
                DataPoint tempDpfromQ = dpQue.remove(0);
                DataPoint newDataPoint = new DataPoint(tempDpfromQ);
                dpList.add(newDataPoint);
                List<DataPoint> tempDpList = isKeyAndReturnObjects(tempDpfromQ,
                        dataPoints, radius, ObjectNum);
//                System.out.println(newDataPoint.getName() + ":"
//                        + newDataPoint.getReachableDistance() + ":" + Arrays.toString(newDataPoint.getDimensioin()) + ": " + clusterType);

                clusterDataPoints.add(new ClusterDataPoint(newDataPoint.getName(), newDataPoint.getDimensioin(), clusterType));
                if (tempDpList != null && tempDpList.size() > 0) {
                    for (int i = 0; i < tempDpList.size(); i++) {
                        DataPoint tempDpfromList = tempDpList.get(i);
                        int indexInList = isContainedInList(tempDpfromList,
                                dpList);
                        int indexInQ = isContainedInList(tempDpfromList, dpQue);
                        if (indexInList == -1) {
                            if (indexInQ > -1) {
                                int index = -1;
                                for (DataPoint dataPoint : dpQue) {
                                    index++;
                                    if (index == indexInQ) {
                                        if (dataPoint.getReachableDistance() > tempDpfromList
                                                .getReachableDistance()) {
                                            dataPoint
                                                    .setReachableDistance(tempDpfromList
                                                            .getReachableDistance());
                                        }
                                    }
                                }
                            } else {
                                dpQue.add(new DataPoint(tempDpfromList));
                            }
                        }
                    }

                    // TODO：对Q进行重新排序
                    Collections.sort(dpQue, new ComparatorDp());
                }
            }
//            System.out.println("------" + total);
            total++;

        }

        return clusterDataPoints;
    }

    public void displayDataPoints(List<DataPoint> dps) {
        for (DataPoint dp : dps) {
            System.out.println(dp.getName() + ":" + dp.getReachableDistance() + ":" + dp.getCoreDistance());
        }
    }

    public void writeData(List<DataPoint> dps) {
        ArrayList<double[]> datas = new ArrayList<>();
        for (DataPoint dp : dps) {
            datas.add(new double[]{dp.getReachableDistance()});
        }
        MyUtils.writeData(datas, "reachableDistance.txt");
    }


    private int isContainedInList(DataPoint dp, List<DataPoint> dpList) {
        int index = -1;
        for (DataPoint dataPoint : dpList) {
            index++;
            if (dataPoint.getName().equals(dp.getName())) {
                return index;
            }
        }
        return -1;
    }

    private List<DataPoint> isKeyAndReturnObjects(DataPoint dataPoint,
                                                  List<DataPoint> dataPoints, double radius, int ObjectNum) {// 找所有直接密度可达点
        List<DataPoint> arrivableObjects = new ArrayList<DataPoint>(); // 用来存储所有直接密度可达对象
        List<Double> distances = new ArrayList<Double>(); // 欧几里得距离
        double coreDistance; // 核心距离

        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint dp = dataPoints.get(i);
            double distance = getDistance(dataPoint, dp);
            if (distance <= radius) {
                distances.add(distance);
                arrivableObjects.add(dp);
            }
        }

        if (arrivableObjects.size() >= ObjectNum) {
            List<Double> newDistances = new ArrayList<Double>(distances);
            Collections.sort(distances);
            coreDistance = distances.get(ObjectNum - 1);
            for (int j = 0; j < arrivableObjects.size(); j++) {
                if (coreDistance > newDistances.get(j)) {
                    if (newDistances.get(j) == 0) {
                        dataPoint.setReachableDistance(coreDistance);
                    }
                    arrivableObjects.get(j).setReachableDistance(coreDistance);
                } else {
                    arrivableObjects.get(j).setReachableDistance(
                            newDistances.get(j));
                }
            }
            return arrivableObjects;
        }

        return null;
    }

    private double getDistance(DataPoint dp1, DataPoint dp2) {
        double distance = 0.0;
        double[] dim1 = dp1.getDimensioin();
        double[] dim2 = dp2.getDimensioin();
        if (dim1.length == dim2.length) {
            for (int i = 0; i < dim1.length; i++) {
                double temp = Math.pow((dim1[i] - dim2[i]), 2);
                distance = distance + temp;
            }
            distance = Math.pow(distance, 0.5);
            return distance;
        }
        return distance;
    }

    public List<ClusterDataPoint> getClusterPoints(String fileName, int startLine, int num, int k, int radius, int pointNum) {
        ArrayList<DataPoint> dpoints = new ArrayList<>();
        double[][] datas = ReadTxt.readLineByNum(fileName, startLine, num, k);

        for (int i = 0; i < datas.length; i++) {
            dpoints.add(new DataPoint(datas[i], String.valueOf(i)));
        }

        ClusterAnalysis ca = new ClusterAnalysis();
        List<ClusterDataPoint> dps = ca.startAnalysis(dpoints, radius, pointNum);
        return dps;
    }

    public static void main(String[] args) {
        ArrayList<DataPoint> dpoints = new ArrayList<>();
        int dim = 2;
        double[][] datas = ReadTxt.readLineByNum("D:\\study\\offer\\Java\\SKDtree\\src\\data.txt", 2, 50, dim);

        for (int i = 0; i < datas.length; i++) {
            dpoints.add(new DataPoint(datas[i], String.valueOf(i)));
//            System.out.println(Arrays.toString(datas[i]));
        }

        ClusterAnalysis ca = new ClusterAnalysis();
        List<ClusterDataPoint> dps = ca.startAnalysis(dpoints, 25, 2);
        System.out.println(dps.size());

//        for (ClusterDataPoint data : dps) {
//            System.out.println(data.getName() + " " + Arrays.toString(data.getDimension()) + " " + data.getClusterType());
//        }
    }

    public ArrayList<Rectangle> getMaxRecAndClustersRec(List<ClusterDataPoint> dps) {
        //获取簇个数
        int recNum = dps.get(dps.size() - 1).getClusterType();
        //几维的数据
        int dim = dps.get(0).getDimension().length;
        //末尾加一个数据，防止空指针异常
        dps.add(new ClusterDataPoint("", dps.get(0).getDimension(), recNum + 1));
        //用来存框
        ArrayList<double[]> recs = new ArrayList<>();
        //用来存框内点个数
        ArrayList<Integer> num = new ArrayList<>();
        //用来存框内的点（用于划分时判断点被划分在哪个部分）
        ArrayList<String> pointsList = new ArrayList<>();
        recs.add(new double[dim * 2]);

        for (int i = 0; i < dim; i++) {
            int flag = 1;
            int cluster = 1;
            double OutMin = Double.MAX_VALUE;
            double OutMax = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            String points = "";
            int count = -1;
            int allCount = 0;
            for (int j = 0; j < dps.size(); j++) {
                if (i == 0) {
                    count++;
                }
                if (i == 0 && flag == 1) {
                    recs.add(new double[dim * 2]);
                }
                if (flag == 1) {
                    flag = 0;
                }
                if (cluster != dps.get(j).getClusterType()) {
                    double[] da = recs.get(cluster);
                    da[2 * i] = min;
                    da[2 * i + 1] = max;
                    recs.set(cluster, da);
                    flag = 1;
                    min = Double.MAX_VALUE;
                    max = Double.MIN_VALUE;
                    cluster++;
//                    System.out.println(count);
                    if (i == 0) {
                        num.add(count);
                        allCount += count;
//                        System.out.println("count:" + count + ",allCount: " + allCount);
                        for (int k = allCount - count; k < allCount; k++) {
                            points += dps.get(k).getName() + " ";
                        }
                        pointsList.add(points);
                        points = "";
                        count = 0;


                    }
                }

                if (dps.get(j).getDimension()[i] < min) {
                    min = dps.get(j).getDimension()[i];

                }

                if (dps.get(j).getDimension()[i] > max) {
                    max = dps.get(j).getDimension()[i];

                }

                if (dps.get(j).getDimension()[i] < OutMin) {
                    OutMin = dps.get(j).getDimension()[i];
                }

                if (dps.get(j).getDimension()[i] > OutMax) {
                    OutMax = dps.get(j).getDimension()[i];
                }

                double[] OutRec = recs.get(0);
                OutRec[2 * i] = OutMin;
                OutRec[2 * i + 1] = OutMax;
                recs.set(0, OutRec);
            }

//            System.out.println(OutMin + " === " + OutMax);
        }

        //写入文件
        MyUtils.writeData(recs, "recs.txt");

        ArrayList<Rectangle> rectangles = new ArrayList<>();

        String allPoints = "";
        for (ClusterDataPoint p : dps) {
            allPoints += p.getName() + " ";
        }
        Rectangle rectangleAll = new Rectangle(recs.get(0), dps.size() - 1, allPoints);
        rectangles.add(rectangleAll);
        for (int i = 1; i < recs.size(); i++) {
            Rectangle rectangle = new Rectangle(recs.get(i), num.get(i - 1), pointsList.get(i - 1));
            rectangles.add(rectangle);
        }

        return rectangles;
    }

}
