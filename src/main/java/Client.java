import SKDTree.NoneLeafNode;
import SKDTree.Rectangle;
import SKDTree.SKDTree;
import data.ReadTxt;
import optics.ClusterAnalysis;
import optics.ClusterDataPoint;
import optics.DataPoint;
import utils.MyUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lgh 2020/12/01
 */
public class Client {
    public static void main(String[] args) {
        /**
         * k：维度
         * dim：在第几个维度划分
         * minCluster：划分标准
         * startLine：开始的行数
         * num: 点个数
         * radius：OPTICS聚类半径
         * clusterNum：OPTICS聚类每类里面的最少点个数
         * density：归结密度
         * initCount：= num
         * whileCount：窗口大小
         */
        String fileName = "D:\\study\\offer\\Java\\SKDtree\\src\\data.txt";
        String fileDir = "D:\\study\\offer\\Java\\SKD";
        int k = 4;
        int dim = 0;
        int minCluster = k << 1;
        int startLine = 0;
        int num = 100;
        int radius = 30;
        int pointNum = 2;
        double density = 0.000000009;
        int initCount = num;
        int whileCount = 100;
        int lineNum = whileCount;
        MyUtils.deleteFileStartWith(fileDir, "AllLeft");
        // OPTICS聚类
        ClusterAnalysis ca = new ClusterAnalysis();
        List<ClusterDataPoint> dps = ca.getClusterPoints(fileName, startLine, num, k, radius, pointNum);

        //获取：最大框和各个簇的框
        ArrayList<Rectangle> rectangles = ca.getMaxRecAndClustersRec(dps);

        //递归划分+建树
        SKDTree skd = new SKDTree();
        NoneLeafNode tree = skd.build(rectangles, dim, minCluster, k);

        int leftPoint = 0;
        startLine += initCount;
        //用来存没有被插入的点
        ArrayList<DataPoint> dpoints = new ArrayList<>();
        //文件内有多少行数据
        long fileLineNum = MyUtils.getFileLineNum(fileName);

        MyUtils.deleteFileStartWith(fileDir, "resolute");


        //for循环
        for (int circle = 0; ; circle++) {
            double[][] datas = ReadTxt.readLineByNum(fileName, startLine, lineNum, k);
            leftPoint = 0;
            //点插入：并标记哪些点没有被插入
            for (int i = 0; i < datas.length; i++) {
                DataPoint p = new DataPoint(datas[i], String.valueOf(startLine + i));
                if (!skd.insertDataPoint(tree, p)) {
                    leftPoint++;
                    dpoints.add(p);
                }
            }

            //点插入后，剩余的点进行OPTICS聚类
            List<ClusterDataPoint> subDps = ca.startAnalysis(dpoints, radius, pointNum);
            Set<String> names = new HashSet<>();
            for (int i = 0; i < subDps.size(); i++) {
                names.add(subDps.get(i).getName());
            }

            if (subDps == null || subDps.size() == 0) {
                break;
            }
            //聚类结果簇插入
            ArrayList<Rectangle> subRectangles = ca.getMaxRecAndClustersRec(subDps);
            subRectangles.remove(0);//去掉最大框
            for (int i = 0; i < subRectangles.size(); i++) {
                skd.insertCluster(tree, subRectangles.get(i), k);
            }
            //剩余的点留到下一次
            for (int i = 0; i < dpoints.size(); i++) {
                if (names.contains(dpoints.get(i).getName())) {
                    dpoints.remove(i);
                }
            }
            leftPoint -= subDps.size();
            startLine += lineNum;
            lineNum = whileCount - leftPoint;
            System.out.println("========================== 第" + (circle + 1) + "次 ==========================");
            //归结
            skd.resolute(tree, density, "", "", circle);
            //输出分类
            skd.classify(tree, fileDir, k, circle);
            StringBuffer[] resultTypes = SKDTree.resultTypes;

            for (int i = 0; i < resultTypes.length; i++) {
                System.out.println("类型" + (i + 1) + " =>" + resultTypes[i]);
            }
        }
    }
}
