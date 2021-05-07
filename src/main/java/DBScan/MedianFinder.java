package DBScan;

public class MedianFinder {
    public static void main(String[] args) {


    }

    public static double[][] medianFinder(double[][] recs, double[][] middles, double[][] maxRec, int clusterNum, int dim) {
        int k = 2;//维度
        double[][] recBykey = new double[2][2 * k + 1];//用于存储根据key返回的两个框大小+1用于存key
        int min_index = 0;

        for (int j = 1; j <= clusterNum / 2 + 1; j++) {
            min_index = findMin(middles);
        }

        double key = middles[min_index][0];
        System.out.println("最小位置：" + min_index);
        System.out.println("key：" + key);
        double min_left = Double.MAX_VALUE;
        double max_right = Double.MIN_VALUE;
        // 寻找key产生的框
        for (int i = 1; i < recs.length; i++) {
            // 判断是否在框内
            if (maxRec[0][0] <= recs[i][0] && maxRec[0][1] >= recs[i][1] && maxRec[0][2] <= recs[i][2] && maxRec[0][3] >= recs[i][3]) {
                switch (dim) {
                    case 0:
                        if (recs[i][0] <= key && recs[i][1] >= key) {
                            if (recs[i][0] < min_left) {
                                min_left = recs[i][0];
                            }
                            if (recs[i][1] > max_right) {
                                max_right = recs[i][1];
                            }
                        }
                        break;
                    case 1:
                        if (recs[i][2] <= key && recs[i][3] >= key) {
                            if (recs[i][2] < min_left) {
                                min_left = recs[i][2];
                            }
                            if (recs[i][3] > max_right) {
                                max_right = recs[i][3];
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

        }
        System.out.println("min_left: " + min_left + "max_left: " + max_right);
        for (int i = 0; i < recBykey.length; i++) {
            if (dim == 0) {
                if (i == 0) {
                    recBykey[i][0] = maxRec[0][0];
                    recBykey[i][1] = min_left;
                } else {
                    recBykey[i][0] = max_right;
                    recBykey[i][1] = maxRec[0][1];
                }
                recBykey[i][2] = maxRec[0][2];
                recBykey[i][3] = maxRec[0][3];
                recBykey[i][4] = key;
            } else {
                recBykey[i][0] = maxRec[0][0];
                recBykey[i][1] = maxRec[0][1];
                if (i == 0) {
                    recBykey[i][2] = maxRec[0][2];
                    recBykey[i][3] = min_left;
                } else {
                    recBykey[i][2] = max_right;
                    recBykey[i][3] = maxRec[0][3];
                }
                recBykey[i][4] = key;
            }


        }
        return recBykey;

    }

    public static int findMin(double[][] middles) {
        int len = middles.length;
        double min = Double.MAX_VALUE;
        int min_index = 0;
        for (int i = 1; i < len; i++) {
            if (middles[i][1] == 1 && middles[i][0] < min) {
                min = middles[i][0];
                min_index = i;
            }
        }

        middles[min_index][1] = 0;
        return min_index;
    }

}