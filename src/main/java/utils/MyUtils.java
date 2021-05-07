package utils;

import SKDTree.Rectangle;
import netscape.security.UserTarget;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MyUtils {
    //bug修复
    //根据维度获取各个簇的中心点并确定划分平面
    public static double getAllRecsMid(List<Rectangle> rectangles, int dim) {
        Map<String, Double> mids = new HashMap<>();
        double mid = 0;
        for (int i = 1; i < rectangles.size(); i++) {
            Rectangle rectangle = rectangles.get(i);
            double[] recs = rectangle.getRecs();
            //计算在dim维度上的中点
            mid = (recs[2 * dim] + recs[2 * dim + 1]) / 2;
            mids.put(Arrays.toString(rectangle.getRecs()), mid);
        }
        //获取中位数：得出划分平面
        Map<String, Double> divideMid = getMid(mids);
        double divideSpace = 0;
        Set<Map.Entry<String, Double>> entrySet = divideMid.entrySet();
        for (Map.Entry<String, Double> entry : entrySet) {
            divideSpace = entry.getValue();
        }
        return divideSpace;
    }

    //获取中位数：得出划分平面
    public static Map<String, Double> getMid(Map<String, Double> mids) {
        Map<String, Double> midsCopy = new HashMap<>();
        mapCopy(mids, midsCopy);
        double mid = 0;
        String minIndex = "";
        double min = 0;
        Set<Map.Entry<String, Double>> entrySet = midsCopy.entrySet();
        for (int i = 0; i <= entrySet.size() / 2; i++) {
            min = Double.MAX_VALUE;
            minIndex = "";
            for (Map.Entry<String, Double> entry : entrySet) {
                if (entry.getValue() != Double.MAX_VALUE && entry.getValue() < min) {
                    min = entry.getValue();
                    minIndex = entry.getKey();
                }
            }
            midsCopy.put(minIndex, Double.MAX_VALUE);
        }
        Map<String, Double> divideMid = new HashMap<>();
        divideMid.put(minIndex, min);
//        System.out.println(minIndex + " " + min);

        return divideMid;
    }

    //拷贝map
    public static <E, V> void mapCopy(Map<E, V> mids, Map<E, V> copyMids) {
        Set<Map.Entry<E, V>> entrySet = mids.entrySet();
        for (Map.Entry<E, V> entry : entrySet) {
            copyMids.put(entry.getKey(), entry.getValue());
        }
    }

    //根据划分平面进行划分
//    List<ArrayList<Rectangle>> rectanglesLeftAndRight = getRectanglesLeftAndRight(ArrayList<Rectangle> rectangles,double divideSpace)
    //
    public static List<List<Rectangle>> getRectanglesLeftAndRight(List<Rectangle> rectangles, double divideSpace, int dim) {
        List<List<Rectangle>> recLeftAndRight = new ArrayList<>();
        //用于存储划分在左边的框：其中第一行为最大框，其他为最大框内的簇
        List<Rectangle> recLeft = new ArrayList<>();
        //用于存储划分在右边的框：其中第一行为最大框，其他为最大框内的簇
        List<Rectangle> recRight = new ArrayList<>();
        //遍历最大框中的簇,判断其在左边还是右边
        Rectangle maxRec1 = (Rectangle) rectangles.get(0).clone();
        Rectangle maxRec2 = (Rectangle) rectangles.get(0).clone();
//        System.out.println(maxRec1.getRecs() == rectangles.get(0).getRecs());
        recLeft.add(maxRec1);
        recRight.add(maxRec2);
        double clusterLeftBorder = 0;
        double clusterRightBorder = 0;
        double midBorder = divideSpace;//key边界

        int leftNum = 0;//用于计算左边框的数据点个数
        int rightNum = 0;//用于计算右边框的数据点个数

        for (int i = 1; i < rectangles.size(); i++) {
            Rectangle cluster = (Rectangle) rectangles.get(i).clone();
            clusterLeftBorder = cluster.getRecs()[dim * 2];//簇左边界
            clusterRightBorder = cluster.getRecs()[dim * 2 + 1];//簇右边界
            //如果簇左边界大等于key => 在右边
            if (clusterLeftBorder >= midBorder) {
                recRight.add(cluster);
                rightNum += (int) (cluster.getArea() * cluster.getDensity());
            }
            //如果簇边界小等于key => 在左边
            if (clusterRightBorder <= midBorder) {
                recLeft.add(cluster);
                leftNum += (int) (cluster.getArea() * cluster.getDensity());
            }
            //如果在中间：要切分开来
            if (clusterLeftBorder < midBorder && clusterRightBorder > midBorder) {
                double[] leftRec = cluster.getRecs().clone();
                leftRec[dim * 2 + 1] = midBorder;
                double[] rightRec = cluster.getRecs().clone();
                rightRec[dim * 2] = midBorder;
                int num = cluster.getNum();
                char name = cluster.getName();
                String subName = cluster.getSubName();
                double density = cluster.getDensity();
                Rectangle newLeftRec = new Rectangle(leftRec, density, name, subName + "1");

                Rectangle newRightRec = new Rectangle(rightRec, density, name, subName + "2");
                newLeftRec.setNum((int) (num * (newLeftRec.getArea() / (newLeftRec.getArea() + newRightRec.getArea()))));
                newRightRec.setNum((int) (num * (newRightRec.getArea() / (newLeftRec.getArea() + newRightRec.getArea()))));
                leftNum += (num / 2);
                rightNum += (num / 2);
                recLeft.add(newLeftRec);
                recRight.add(newRightRec);
            }

        }
        double[] maxRecLeft = recLeft.get(0).getRecs();
        double[] maxRecRight = recRight.get(0).getRecs();
        maxRecLeft[dim * 2 + 1] = midBorder;
        maxRecRight[dim * 2] = midBorder;
        recLeft.get(0).setRecs(maxRecLeft);
        recLeft.get(0).setDensity(leftNum / recLeft.get(0).getArea());
        recRight.get(0).setRecs(maxRecRight);
        recRight.get(0).setDensity(rightNum / recRight.get(0).getArea());

        recLeftAndRight.add(recLeft);
        recLeftAndRight.add(recRight);
        return recLeftAndRight;
    }

    //写入文件
    public static void writeData(ArrayList<double[]> AllRecs, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for (int i = 0; i < AllRecs.size(); i++) {
                String recString = new String();
                for (int j = 0; j < AllRecs.get(i).length; j++) {
                    if (j != AllRecs.get(i).length - 1) {
                        recString += AllRecs.get(i)[j] + " ";
                    } else {
                        recString += AllRecs.get(i)[j] + "\n";
                    }
                }
//                System.out.println(recString);
                bw.write(recString);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //写入文件：追加
    public static void writeAppendData(double[] recs, String path) {
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            String recString = "";
            for (int i = 0; i < recs.length; i++) {
                recString += recs[i] + " ";
            }
            recString = recString + "\n";
            bw.write(recString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 序列化对象
     *
     * @param url 序列化文件名
     * @param t   需要序列化的对象
     * @return void
     * @author 13540
     * @date 2021-01-26 20:23
     */
    public static <T> void serializable(String url, T t) {
        try {
            FileOutputStream fos = new FileOutputStream(url);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(t);
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T deserializable(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            ObjectInputStream in = new ObjectInputStream(fis);
            T t = (T) in.readObject();
            in.close();
            fis.close();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    public static void deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取文件行数
    public static long getFileLineNum(String filePath) {
        try {
            return Files.lines(Paths.get(filePath)).count();
        } catch (IOException e) {
            return -1;
        }
    }

    //删除以指定字符串开头的文件
    public static void deleteFileStartWith(String filedir, String start) {
        File dirFile = new File(filedir);
        if(!dirFile.exists()){
            System.out.println("文件目录不存在："+filedir);
            return;
        }
        File[] files = dirFile.listFiles() ;
        for (File file : files) {
            if (file.getName().startsWith(start)) {
                file.delete();
//                    System.out.println("已删除文件：" + file.getAbsolutePath());
            }
        }
    }
}
