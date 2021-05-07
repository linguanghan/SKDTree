package data;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jason on 2016/4/17.
 */
public class Data {
    private static DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    public static ArrayList<Point> generateSinData(int size) {
        ArrayList<Point> points = new ArrayList<Point>(size);
        Random rd = new Random(size);
        for (int i = 0; i < size / 2; i++) {
            double x = format(Math.PI / (size / 2) * (i + 1));
            double y = format(Math.sin(x));
            points.add(new Point(x, y));
        }
        for (int i = 0; i < size / 2; i++) {
            double x = format(1.5 + Math.PI / (size / 2) * (i + 1));
            double y = format(Math.cos(x));
            points.add(new Point(x, y));
        }
        return points;
    }

    public static ArrayList<Point> generateSpecialData() {
        ArrayList<Point> points = new ArrayList<Point>();
        ReadTxt t = new ReadTxt();
        double[][] txt = t.readLineByNum("D:\\study\\offer\\Java\\SKDtree\\src\\data.txt", 0, 100, 3);
        for (int i = 0; i < txt.length; i++) {
            points.add(new Point(txt[i][0], txt[i][1]));
        }
        return points;
    }

    public static void writeData(ArrayList<Point> points, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for (Point point : points) {
                bw.write(point.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double format(double x) {
        return Double.valueOf(df.format(x));
    }

    public static void writeData(double[][] recs, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for (double[] rec : recs) {
                for (int i = 0; i < rec.length; i++) {
                    if (i != rec.length - 1) {
                        bw.write(rec[i] + " ");
                    } else {
                        bw.write(rec[i] + "\n");
                    }

                }

            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendData(String fileName, String conent) {
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("Create file succeed!");
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
                out.write(conent);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}