package data;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTxt {
    // 根据输入读取多少行
    public static double[][] readLineByNum(String name, int startLine, int lineNum, int k) {
        /*
        file: 文件名
        lineNum: 要读取的行数
        k: 维度
        * */
        double[][] result = new double[lineNum][k];
        ArrayList<String> arrayList = new ArrayList();
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            int i = 0;
            while ((str = bf.readLine()) != null && i < lineNum + startLine) {
                i++;
                if (i <= startLine) {
                    continue;
                }
                arrayList.add(str);

            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对ArrayList中存储的字符串进行处理
        for (int i = 0; i < lineNum; i++) {
            String s = arrayList.get(i);
//            System.out.println(s);
//            array[i] = Integer.parseInt(s);
            String[] arr = s.split("\\s+");
            int kk = 0;
            for (String ss : arr) {
                kk++;
                if (kk == 1) {
                    continue;
                }
                if (kk > k + 1) {
                    break;
                }
//                System.out.print(ss + " ");
                result[i][kk - 2] = Float.parseFloat(ss);
            }

//            System.out.println();

        }
        // 返回数组
        return result;
    }
}
