package KMeans;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import data.ReadTxt;
import utils.MyUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        int dim = 2;
        ArrayList<float[]> dataSet = new ArrayList<>();
////        float[][] data = ReadTxt.readLineByNum("D:\\study\\offer\\Java\\SKDtree\\src\\data.txt", 100, dim);
//        float[][] data = null;
//        for (int i = 0; i < data.length; i++) {
//            dataSet.add(data[i]);
//        }
//        KMeansRun kRun = new KMeansRun(30, dataSet);
//        MyUtils.writeData(dataSet, "data.txt");
//        Set<Cluster> clusterSet = kRun.run();
//        System.out.println("单次迭代运行次数：" + kRun.getIterTimes());
//
//        ArrayList<float[]> AllRecs = new ArrayList<>();
//        for (Cluster cluster : clusterSet) {
//            float[] rec = new float[2 * dim + 1];
//            List<Point> members = cluster.getMembers();
//            //获取每个簇的最大框
//            for (int i = 0; i < dim; i++) {
//                float max = Float.MIN_VALUE;
//                float min = Float.MAX_VALUE;
//                for (Point member : members) {
//                    if (member.getlocalArray()[i] > max) {
//                        max = member.getlocalArray()[i];
//                    }
//
//                    if (member.getlocalArray()[i] < min) {
//                        min = member.getlocalArray()[i];
//                    }
//
//                    System.out.println("dist=" + Arrays.toString(member.getlocalArray()) + ", id=" + member.getId() + ", clusterID=" + member.getClusterid());
//                }
//                rec[i * 2] = min;
//                rec[i * 2 + 1] = max;
//            }
//            rec[2 * dim] = members.get(0).getClusterid();
//            AllRecs.add(rec);
//        }
//        for (int i = 0; i < AllRecs.size(); i++) {
//            for (int j = 0; j < AllRecs.get(i).length; j++) {
//                System.out.print(AllRecs.get(i)[j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("===============");
//        Collections.sort(AllRecs, new Comparator<float[]>() {
//            @Override
//            public int compare(float[] o1, float[] o2) {
//                int length = o1.length;
//                return (int) (o1[length - 1] - o2[length - 1]);
//            }
//        });
//
//        MyUtils.writeData(AllRecs, "recs.txt");
    }


}
