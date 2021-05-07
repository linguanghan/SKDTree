package SKDTree;

import optics.DataPoint;
import utils.MyUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.*;

public class SKDTree implements Serializable {
    private NoneLeafNode tree;
    public static StringBuffer WAY = new StringBuffer("");//查找路径
    public ArrayList<SubLeafNode> subLeafNodes; //存储亚叶子节点
    public static StringBuffer[] resultTypes;

    public SKDTree() {

    }


    public SKDTree(double[][] recs, double[][] maxRec, int clusterNum, int dim, int minCluster) {
//        this.tree = build(recs, maxRec, clusterNum, dim, minCluster);
//        this.tree.setDensity(MyUtils.getDensityByRec(recs, maxRec));
    }

    /**
     * 划分：build(ArrayList<Rectangle> rectangles,int dim, int minCluster, int k){
     *          /* rectangles：第一维为最大框，剩下的就是其他簇的框
     *             dim：划分维度
     *             minCluster：划分标准（不能多于多少簇）
     *             k: 总维度
     *          */
    /**
     * 步骤 1
     * 步骤 2
     * //左侧划分
     * build(ArrayList<Rectangle> rectangles,int dim, minCluster);
     * //右侧划分
     * build(ArrayList<Rectangle> rectangles,int dim, minCluster);
     * }
     * 步骤：
     * 1. 获取各个簇的中心点并确定划分平面  double divideSpace = getAllRecsMid(ArrayList<Rectangle> rectangles,int dim){}
     * 2. 根据划分平面进行划分：Arraylist<ArrayList<Rectangle>> rectanglesLeftAndRight = getRectanglesLeftAndRight(ArrayList<Rectangle> rectangles,double divideSpace)
     */
    public NoneLeafNode build(List<Rectangle> rectangles, int dim, int minCluster, int k) {
        double divideSpace = MyUtils.getAllRecsMid(rectangles, dim);
        List<List<Rectangle>> recLeftAndRight = MyUtils.getRectanglesLeftAndRight(rectangles, divideSpace, dim);
        List<Rectangle> recLeft = recLeftAndRight.get(0);
        List<Rectangle> recRight = recLeftAndRight.get(1);
        //key、两个框
        double key = recLeft.get(0).getRecs()[dim * 2 + 1];
        double[] recsLeft = recLeft.get(0).getRecs();
        double[] recsRight = recRight.get(0).getRecs();

        MyUtils.writeAppendData(recsLeft, "AllLeftAndRightRecs.txt");
        MyUtils.writeAppendData(recsRight, "AllLeftAndRightRecs.txt");

        NoneLeafNode noneLeafNode = new NoneLeafNode(rectangles.get(0).getRecs(), dim, key, 0, rectangles.get(0).getDensity(), 0);
        dim = dim + 1;
        if (recLeft.size() - 1 > minCluster) {
            if (dim == k) {
                dim = 0;
            }
            noneLeafNode.ptr_left = build(recLeft, dim, minCluster, k);
            noneLeafNode.ptr_left.setDensity(recLeft.get(0).getDensity());
        } else {
            SubLeafNode subLeafNode = new SubLeafNode(recLeft.get(0).getRecs(), 0, recLeft.get(0).getDensity(), 0);
            //把数据添加到树节点：数据就在recLeft的索引值（1~末尾）
            for (int i = 1; i < recLeft.size(); i++) {
//                recLeft.get(i);
                subLeafNode.leafNodes.add(new LeafNode(recLeft.get(i).getRecs(), 0, recLeft.get(i).getDensity(), 0, recLeft.size() - 1, recLeft.get(i).getName() + recLeft.get(i).getSubName()));
            }
            subLeafNode.setNumOfLeafNodes(recLeft.size() - 1);
            noneLeafNode.ptr_sub_left = subLeafNode;
        }

        if (recRight.size() - 1 > minCluster) {
            if (dim == k) {
                dim = 0;
            }
            noneLeafNode.ptr_right = build(recRight, dim, minCluster, k);
            noneLeafNode.ptr_right.setDensity(recRight.get(0).getDensity());
        } else {
            SubLeafNode subLeafNode = new SubLeafNode(recRight.get(0).getRecs(), 0, recRight.get(0).getDensity(), 0);
            //把数据添加到树节点：数据就在recLeft的索引值（1~末尾）
            for (int i = 1; i < recRight.size(); i++) {
//                recLeft.get(i);
                subLeafNode.leafNodes.add(new LeafNode(recRight.get(i).getRecs(), 0, recRight.get(i).getDensity(), 0, i - 1, recRight.get(i).getName() + recRight.get(i).getSubName()));
            }
            subLeafNode.setNumOfLeafNodes(recRight.size() - 1);
            noneLeafNode.ptr_sub_right = subLeafNode;
        }

        return noneLeafNode;
    }

    public NoneLeafNode getTree() {
        return tree;
    }

    public void setTree(NoneLeafNode tree) {
        this.tree = tree;
    }

    /**
     * 数据点的插入
     *
     * @param tree  输入的树
     * @param point 要插入的数据点
     * @return void
     * @author 13540
     * @date 2021-01-12 19:48
     */
    public boolean insertDataPoint(NoneLeafNode tree, DataPoint point) {
        //防止空指针异常
        if (tree == null) {
//            System.out.println("树为空");
            return false;
        }

        double[] PointDimension = point.getDimensioin();
        //判断是否在最大的框内
        if (!isPointInRec(tree.getRange(), PointDimension)) {
//            System.out.println("点不再最大框内，直接不进行插入操作！");
            return false;
        } else {
//            System.out.println("点在最大框内");
            //用于存储寻找过程中的节点
            Stack<NoneLeafNode> stack = new Stack<>();
            stack.push(tree);
            //查找点在那个框内（非叶子节点）
            NoneLeafNode po = tree;
            while (po.ptr_left != null || po.ptr_right != null) {
                int flag1 = 0;
                int flag2 = 0;
                if (po.ptr_left != null && isPointInRec(po.ptr_left.getRange(), PointDimension)) {
                    po = po.ptr_left;
                    flag1 = 1;
//                    continue;
                }

                if (po.ptr_right != null && isPointInRec(po.ptr_right.getRange(), PointDimension)) {
                    po = po.ptr_right;
                    flag2 = 1;
//                    continue;
                }

                if (flag1 == 0 && flag2 == 0) {
                    break;
                }
                stack.push(po);
            }

            //查找亚叶子节点
            NoneLeafNode p = stack.peek();
            SubLeafNode sub = null;
            if (p.ptr_sub_left != null && isPointInRec(p.ptr_sub_left.getRange(), PointDimension)) {
                sub = p.ptr_sub_left;
            }

            if (p.ptr_sub_right != null && isPointInRec(p.ptr_sub_right.getRange(), PointDimension)) {
                sub = p.ptr_sub_right;
            }

            if (sub == null) {
                return false;
            }

            //判断是否在簇内
            int flag = 0;
            for (int i = 0; i < sub.getNumOfLeafNodes(); i++) {
                LeafNode leafNode_i = sub.leafNodes.get(i);
                if (isPointInRec(leafNode_i.getMk(), PointDimension)) {
//                    System.out.println(i);
                    flag = 1;
                    //修改密度
//                    System.out.print("原簇{框/密度}：" + "{" + Arrays.toString(leafNode_i.getMk()) + leafNode_i.getPm() + "}");
                    double area = leafNode_i.getArea();
                    int numOfPoint = (int) (area * leafNode_i.getPm() + 1);
                    leafNode_i.setPm(numOfPoint / area);
//                    System.out.println("===>更新后簇{框/密度}：" + "{" + Arrays.toString(leafNode_i.getMk()) + leafNode_i.getPm() + "}");
                    break;
                }
            }

            if (flag == 0) {
//                System.out.println("点在最大框内，不在簇内：不执行插入");
                return false;
            } else {
                //回溯原来的那些经过的框
                //1、亚叶子节点：sub
//                System.out.print("原亚叶子节点{框/密度}：" + "{" + Arrays.toString(sub.getRange()) + sub.getDensity() + "}");
                double area = sub.getArea();
                int numPoint = (int) (area * sub.getDensity() + 1);
                sub.setDensity(numPoint / area);
//                System.out.println("===>更新原亚叶子节点{框/密度}：" + "{" + Arrays.toString(sub.getRange()) + sub.getDensity() + "}");

                //2. 栈内的节点
                while (stack.size() != 0) {
                    NoneLeafNode top = stack.peek();
//                    System.out.print("非叶子节点{框/密度}：" + "{" + Arrays.toString(top.getRange()) + top.getDensity() + "}");
                    double topArea = top.getArea();
                    int topNum = (int) (topArea * top.getDensity() + 1);
                    top.setDensity(topNum / topArea);
                    stack.pop();
//                    System.out.println("===>更新非叶子节点{框/密度}：" + "{" + Arrays.toString(top.getRange()) + top.getDensity() + "}");
                }

//                System.out.println("密度更新成功！");
                return true;
            }


        }
    }

    /**
     * 判断点是否在框内
     *
     * @param rec       框
     * @param dimension 数据点坐标
     * @return boolean
     * @author 13540
     * @date 2021-01-12 21:45
     */
    public boolean isPointInRec(double[] rec, double[] dimension) {
        for (int i = 0; i < rec.length / 2; i++) {
            //如果出现点维度最小值小于该维度边界的最小值，或者点维度最大值大于该维度边界的最大值=>点不再框内
            if (rec[2 * i] > dimension[i] || rec[2 * i + 1] < dimension[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 簇插入
     *
     * @param tree      树
     * @param rectangle 要插入的簇
     * @return void
     * @author 13540
     * @date 2021-01-14 20:41
     */
    public void insertCluster(NoneLeafNode tree, Rectangle rectangle, int k) {
        int dim = tree.getRange().length / 2; //获取维度信息
        //判断是否在最大框内
        if (inOrNot(tree, rectangle)) {
            //1. 将簇插入，并返回亚叶子节点
            insertClusterAndDivide(tree, rectangle, k);
            //2. 簇相交（可能亚叶子节点分裂）
        } else {//不在最大框内的情况(有可能与最大框相交)：先更新范围，使其在最大框内
            //1. 首先更新最大框以及以下所有框，密度（遍历树）
            //1.1 更新最大框，以及密度:放回最大框以及划分维度和关键值
            List<double[]> newMaxRec = updateMaxRec(tree, rectangle, dim);
            traverseUpdate(tree, newMaxRec.get(0), dim);
            //2. 将簇插入
            insertClusterAndDivide(tree, rectangle, k);
        }
    }

    /**
     * 将簇插入到树中
     *
     * @param tree      树
     * @param rectangle 要插入的簇
     * @param k         总维度
     * @return void
     * @author 13540
     * @date 2021-01-24 20:50
     */
    public void insertClusterAndDivide(NoneLeafNode tree, Rectangle rectangle, int k) {
        //遍历树
        if (tree.ptr_sub_left != null || tree.ptr_sub_right != null) {
            if (tree.ptr_sub_left != null) {
                if (inOrNot(tree.ptr_sub_left, rectangle)) {
                    /**
                     判断是否与簇相交/相交继续分割
                     */
                    Map<NoneLeafNode, SubLeafNode> res = divide(tree, rectangle, k, 0);
                    for (Map.Entry<NoneLeafNode, SubLeafNode> entry : res.entrySet()) {
                        tree.ptr_left = entry.getKey();
                        tree.ptr_sub_left = entry.getValue();
                    }
                }
            }

            if (tree.ptr_sub_right != null) {
                if (inOrNot(tree.ptr_sub_right, rectangle)) {
                    /**
                     判断是否与簇相交/相交继续分割
                     */
                    Map<NoneLeafNode, SubLeafNode> res = divide(tree, rectangle, k, 1);
                    for (Map.Entry<NoneLeafNode, SubLeafNode> entry : res.entrySet()) {
                        tree.ptr_right = entry.getKey();
                        tree.ptr_sub_right = entry.getValue();
                    }
                }
            }
            return;
        }
        Rectangle leftRec = (Rectangle) rectangle.clone();
        Rectangle rightRec = (Rectangle) rectangle.clone();
        //判断是否在范围内：先分割，然后判断在哪个框内
        if (inOrNot(tree, rectangle)) {
            //更新密度
            double area = tree.getArea();
            int numberOfPoints = (int) ((area * tree.getDensity()) + (rectangle.getDensity() * rectangle.getArea()));
            tree.setDensity(numberOfPoints / area);
//            int rectanglePoints = (int) (rectangle.getDensity() * rectangle.getArea());
            //判断是否被key割到
            double key = tree.getKey();
            int dim = tree.getDim();
            //分割
            if (rectangle.getRecs()[dim * 2] < key && rectangle.getRecs()[dim * 2 + 1] > key) {
                //左边
                leftRec.setRecByIndex(key, dim * 2 + 1);
                leftRec.setDivide_Name(key + "left");

                //右边
                rightRec.setRecByIndex(key, dim * 2);
                rightRec.setDivide_Name(key + "right");

                leftRec.setDensity(rectangle.getDensity());
                rightRec.setDensity(rectangle.getDensity());
            }
        }
        insertClusterAndDivide(tree.ptr_left, leftRec, k);
        insertClusterAndDivide(tree.ptr_right, rightRec, k);
    }

    public Map<NoneLeafNode, SubLeafNode> divide(NoneLeafNode tree, Rectangle rectangle, int k, int flag) {
        NoneLeafNode P = null;
        SubLeafNode PS = null;
        if (flag == 0) {
            P = tree.ptr_left;
            PS = tree.ptr_sub_left;
        } else {
            P = tree.ptr_right;
            PS = tree.ptr_sub_right;
        }
        ArrayList<Rectangle> allCluster = new ArrayList<>();//用来存所有的簇
        //计算簇的最大框
        Rectangle rec = turnSubLeafNodesToRec(PS);
        rec.setDensity((rec.getNumberOfPoints() + rectangle.getNumberOfPoints()) / rec.getArea());
        allCluster.add(rec);
        int count = 0;
        for (int i = 0; i < PS.getNumOfLeafNodes(); i++) {
            LeafNode leaf = PS.leafNodes.get(i);
            List<Rectangle> divideCluster = divideCluster(leaf, rectangle);
            if (divideCluster.size() == 0) {
                count++;
            }
            allCluster.add(turnLeafNodesToRec(leaf));
            allCluster.addAll(divideCluster);
        }
        //当都没有被分割时，把该节点加进去
        if (count == PS.getNumOfLeafNodes()) {
            allCluster.add(rectangle);
        }

        if (allCluster.size() - 1 <= 2 * k) {
            PS.setNumOfLeafNodes(PS.getNumOfLeafNodes() + 1);
            //更新亚叶子节点密度
            int PSNumberOfPoints = PS.getNumberOfPoints();
            int recNumberOfPoints = rectangle.getNumberOfPoints();
            PS.setDensity((PSNumberOfPoints + recNumberOfPoints) / PS.getArea());
            PS.leafNodes.add(new LeafNode(rectangle));
        } else {
            //分裂
            NoneLeafNode newNode = build(allCluster, 0, 4, 2);
            P = newNode;
            PS = null;
        }
        Map<NoneLeafNode, SubLeafNode> map = new HashMap<>();
        map.put(P, PS);
        return map;
    }

    public Rectangle turnLeafNodesToRec(LeafNode leafNode) {
        Rectangle rectangle = new Rectangle();
        rectangle.setRecs(leafNode.getMk());
        rectangle.setDensity(leafNode.getPm());
        rectangle.setDivideSubName(Math.random() + "p");
        return rectangle;
    }

    public Rectangle turnSubLeafNodesToRec(SubLeafNode subLeafNode) {
        Rectangle rectangle = new Rectangle();
        rectangle.setRecs(subLeafNode.getRange());
        rectangle.setDensity(subLeafNode.getDensity());
        rectangle.setDivideSubName(Math.random() + "p");
        return rectangle;
    }

    /**
     * 对插入的簇框进行分割
     *
     * @param leafNode  原来的簇
     * @param rectangle 要插入的簇
     * @return java.util.List<SKDTree.Rectangle>：返回被分割出来的框
     * @author 13540
     * @date 2021-01-21 20:39
     */
    public List<Rectangle> divideCluster(LeafNode leafNode, Rectangle rectangle) {
        List<Rectangle> rectangles = new ArrayList<>();
        if (crossOrNot(leafNode, rectangle)) {
            //遍历簇边界，判断是否在要插入的簇中间
            for (int i = 0; i < leafNode.getMk().length / 2; i++) {
                double upBorder = rectangle.getRecs()[2 * i + 1];
                double downBorder = rectangle.getRecs()[2 * i];
                //如果簇的i维下界在界内=>rectangle分割为：[Xmin(被分割), 分割线, 分割线, Xmax(被分割)]
                if (downBorder < leafNode.getMk()[2 * i] && leafNode.getMk()[2 * i] < upBorder) {
                    //分割线
                    double divideLine = leafNode.getMk()[2 * i];
                    //划分出来的新框为：
                    Rectangle newRec = (Rectangle) rectangle.clone();
                    newRec.setRecByIndex(downBorder, 2 * i);//新下界
                    newRec.setRecByIndex(divideLine, 2 * i + 1);//新上界
                    newRec.setDivideSubName(i + "" + Math.random());
                    newRec.setName((char) (64 + Rectangle.numberOfRecs));
                    rectangles.add(newRec);//将划分出来的框加导List里面

                    //然后rec改变
                    rectangle.setRecByIndex(divideLine, 2 * i);//新下界
                    rectangle.setRecByIndex(upBorder, 2 * i + 1);//新上界
                }
                upBorder = rectangle.getRecs()[2 * i + 1];
                downBorder = rectangle.getRecs()[2 * i];
                if (downBorder < leafNode.getMk()[2 * i + 1] && leafNode.getMk()[2 * i + 1] < upBorder) {
                    double divideLine = leafNode.getMk()[2 * i + 1];
                    Rectangle newRec = (Rectangle) rectangle.clone();
                    newRec.setRecByIndex(divideLine, 2 * i);//新下界
                    newRec.setRecByIndex(upBorder, 2 * i + 1);//新上界
                    newRec.setDivideSubName(i + "" + Math.random());
                    newRec.setName((char) (64 + Rectangle.numberOfRecs));
                    rectangles.add(newRec);

                    //改变原来的rec
                    rectangle.setRecByIndex(downBorder, 2 * i);//新下界
                    rectangle.setRecByIndex(divideLine, 2 * i + 1);//新上界
                }
            }
        }

        return rectangles;
    }

    /**
     * 判断某个簇是否在指定的非叶子节点内
     *
     * @param tree      框
     * @param rectangle 指定的簇
     * @return boolean
     * @author 13540
     * @date 2021-01-14 21:23
     */
    public boolean inOrNot(NoneLeafNode tree, Rectangle rectangle) {
        for (int i = 0; i < rectangle.getRecs().length / 2; i++) {
            //不在框内或者与框交叉
            if (tree.getRange()[2 * i] > rectangle.getRecs()[2 * i] || tree.getRange()[2 * i + 1] < rectangle.getRecs()[2 * i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断某个簇是否在指定的亚叶子节点内
     *
     * @param tree      框
     * @param rectangle 指定的簇
     * @return boolean
     * @author 13540
     * @date 2021-01-14 21:23
     */
    public boolean inOrNot(SubLeafNode tree, Rectangle rectangle) {
        for (int i = 0; i < rectangle.getRecs().length / 2; i++) {
            //不在框内或者与框交叉
            if (tree.getRange()[2 * i] > rectangle.getRecs()[2 * i] || tree.getRange()[2 * i + 1] < rectangle.getRecs()[2 * i + 1]) {
                return false;
            }
        }
        return true;
    }

    public boolean inOrNot(LeafNode tree, Rectangle rectangle) {
        for (int i = 0; i < rectangle.getRecs().length / 2; i++) {
            //不在框内或者与框交叉
            if (tree.getMk()[2 * i] > rectangle.getRecs()[2 * i] || tree.getMk()[2 * i + 1] < rectangle.getRecs()[2 * i + 1]) {
                return false;
            }
        }
        return true;
    }

    public boolean inOrNot(LeafNode tree, double[] range) {
        for (int i = 0; i < range.length / 2; i++) {
            //不在框内或者与框交叉
            if (tree.getMk()[2 * i] > range[2 * i] || tree.getMk()[2 * i + 1] < range[2 * i + 1]) {
                return false;
            }
        }
        return true;
    }

    public boolean crossOrNot(LeafNode tree, Rectangle rectangle) {
        //交叉或者在框外
        if (!inOrNot(tree, rectangle)) {
            for (int i = 0; i < rectangle.getRecs().length / 2; i++) {
                //两个框在维度上的距离
                double distance = Math.abs((tree.getMk()[2 * i] + tree.getMk()[2 * i + 1]) / 2 - (rectangle.getRecs()[2 * i] + rectangle.getRecs()[2 * i + 1]) / 2);
                double radiusTree = (tree.getMk()[2 * i + 1] - tree.getMk()[2 * i]) / 2;
                double radiusRec = (rectangle.getRecs()[2 * i + 1] - rectangle.getRecs()[2 * i]) / 2;
                //说明没交差
                if (distance > radiusTree + radiusRec) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean crossOrNot(LeafNode tree, double[] range) {
        //交叉或者在框外
        if (!inOrNot(tree, range)) {
            for (int i = 0; i < range.length / 2; i++) {
                //两个框在维度上的距离
                double distance = Math.abs((tree.getMk()[2 * i] + tree.getMk()[2 * i + 1]) / 2 - (range[2 * i] + range[2 * i + 1]) / 2);
                double radiusTree = (tree.getMk()[2 * i + 1] - tree.getMk()[2 * i]) / 2;
                double radiusRec = (range[2 * i + 1] - range[2 * i]) / 2;
                //说明没交叉
                if (distance > radiusTree + radiusRec) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 更新最大框，以及密度，返回最大框
     *
     * @param tree      输入的树
     * @param rectangle 要插入的簇
     * @param dim       总维度
     * @return List<double [ ]> res 返回最大框以及划分维度和关键值
     * @author 13540
     * @date 2021-01-18 21:48
     */
    public List<double[]> updateMaxRec(NoneLeafNode tree, Rectangle rectangle, int dim) {
        List<double[]> res = new ArrayList<>();
        double areaOrigin = tree.getArea();
        int numOfPoints = (int) (areaOrigin * tree.getDensity());
        //判断各个维度是否有超过界限的框

        for (int i = 0; i < dim; i++) {
            if (rectangle.getRecs()[2 * i] < tree.getRange()[2 * i]) {
                //设置新的框大小
                tree.setRecByIndex(rectangle.getRecs()[2 * i], 2 * i);
            }
            if (rectangle.getRecs()[2 * i + 1] > tree.getRange()[2 * i + 1]) {
                tree.setRecByIndex(rectangle.getRecs()[2 * i + 1], 2 * i + 1);
            }
        }
        //计算密度
        double newDensity = numOfPoints / tree.getArea();
        //设置密度
        tree.setDensity(newDensity);
        double key = tree.getKey();
        double dim_dvivide = tree.getDim();
        double[] dimAndKey = new double[]{dim_dvivide, key};
        res.add(tree.getRange());
        res.add(dimAndKey);
        return res;
    }

    /**
     * @param tree 节点
     * @param rec  外框
     * @param dim  划分总维度
     * @return void
     * @author 13540
     * @date 2021-01-18 20:13
     */
    public void traverseUpdate(NoneLeafNode tree, double[] rec, int dim) {
        if (tree == null) {
            return;
        }
        double key = tree.getKey();
        double dim_divide = tree.getDim();//获取划分维度
        if (tree.ptr_sub_left != null || tree.ptr_sub_right != null) {
            //更新亚叶子节点的框和密度
            if (tree.ptr_sub_left != null) {
                updateLeftRecAndDensity(tree.ptr_sub_left, rec, dim, key, dim_divide, 0);
            }
            if (tree.ptr_sub_right != null) {
                updateLeftRecAndDensity(tree.ptr_sub_right, rec, dim, key, dim_divide, 1);
            }
            return;
        }

        //更新非叶子节点的框和密度
        if (tree.ptr_left != null) {
            updateLeftRecAndDensity(tree.ptr_left, rec, dim, key, dim_divide, 0);
        }
        if (tree.ptr_right != null) {
            updateLeftRecAndDensity(tree.ptr_right, rec, dim, key, dim_divide, 1);
        }

        traverseUpdate(tree.ptr_left, tree.ptr_left.getRange(), dim);
        traverseUpdate(tree.ptr_right, tree.ptr_right.getRange(), dim);
    }

    //更新亚叶子框和密度
    public void updateLeftRecAndDensity(SubLeafNode LeftNode, double[] rec, int dim, double key, double dim_divide, int choose) {
        double PLDensity = LeftNode.getDensity();
        double PLArea = LeftNode.getArea();
        int PLNumOfPoints = (int) (PLArea * PLDensity);
        //更新框
        for (int j = 0; j < dim; j++) {
            if (choose == 0) {//左子树
                LeftNode.setRecByIndex(rec[2 * j], 2 * j);
                if (j == dim_divide) {//如果刚好等于划分的那个维度时，该维度的上界为key
                    LeftNode.setRecByIndex(key, 2 * j + 1);
                } else {
                    LeftNode.setRecByIndex(rec[2 * j + 1], 2 * j + 1);
                }
            } else if (choose == 1) {//右子树
                LeftNode.setRecByIndex(rec[2 * j + 1], 2 * j + 1);
                if (j == dim_divide) {//如果刚好等于划分的那个维度时，该维度的下界为key
                    LeftNode.setRecByIndex(key, 2 * j);
                } else {
                    LeftNode.setRecByIndex(rec[2 * j], 2 * j);
                }
            }
        }
        //更新密度
        double newPLArea = LeftNode.getArea();
        LeftNode.setDensity(PLNumOfPoints / newPLArea);
    }

    //更新非叶子框和密度
    public void updateLeftRecAndDensity(NoneLeafNode LeftNode, double[] rec, int dim, double key, double dim_divide, int choose) {
        double PLDensity = LeftNode.getDensity();
        double PLArea = LeftNode.getArea();
        int PLNumOfPoints = (int) (PLArea * PLDensity);
        //更新框
        for (int j = 0; j < dim; j++) {
            if (choose == 0) {//左子树
                LeftNode.setRecByIndex(rec[2 * j], 2 * j);
                if (j == dim_divide) {//如果刚好等于划分的那个维度时，该维度的上界为key
                    LeftNode.setRecByIndex(key, 2 * j + 1);
                } else {
                    LeftNode.setRecByIndex(rec[2 * j + 1], 2 * j + 1);
                }
            } else if (choose == 1) {//右子树
                LeftNode.setRecByIndex(rec[2 * j + 1], 2 * j + 1);
                if (j == dim_divide) {//如果刚好等于划分的那个维度时，该维度的下界为key
                    LeftNode.setRecByIndex(key, 2 * j);
                } else {
                    LeftNode.setRecByIndex(rec[2 * j], 2 * j);
                }
            }
        }
        //更新密度
        double newPLArea = LeftNode.getArea();
        LeftNode.setDensity(PLNumOfPoints / newPLArea);
    }

    /**
     * 归结
     *
     * @param tree    树
     * @param density 归结密度
     * @return void
     * @author 13540
     * @date 2021-01-26 20:05
     */
    public void resolute(NoneLeafNode tree, double density, String path, String leftOrRight, int num) {
        path += leftOrRight;
        if (tree.ptr_sub_left != null || tree.ptr_sub_right != null) {
            int flag1 = 0;
            int flag2 = 0;
            if (tree.ptr_sub_left != null) {
                if (tree.ptr_sub_left.getDensity() >= density) {
                    for (int i = 0; i < tree.ptr_sub_left.getNumOfLeafNodes(); i++) {
                        if (tree.ptr_sub_left.leafNodes.get(i).getPm() < density) {
                            return;
                        }
                    }
                    flag1 = 1;
                    MyUtils.serializable("resolute_" + path + "0_" + num + "_.txt", tree.ptr_sub_left);
                }
            }

            if (tree.ptr_sub_right != null) {
                if (tree.ptr_sub_right.getDensity() >= density) {
                    for (int i = 0; i < tree.ptr_sub_right.getNumOfLeafNodes(); i++) {
                        if (tree.ptr_sub_right.leafNodes.get(i).getPm() < density) {
                            return;
                        }
                    }
                    flag2 = 1;
                    MyUtils.serializable("resolute_" + path + "1_" + num + "_.txt", tree.ptr_sub_right);
                }
            }
            if (tree.getDensity() >= density && flag1 == 1 && flag2 == 1) {
                MyUtils.serializable("resolute_" + path + "_" + num + "_.txt", tree);
                MyUtils.deleteFile("resolute_" + path + "0_" + num + "_.txt");
                MyUtils.deleteFile("resolute_" + path + "1_" + num + "_.txt");
            }
            return;
        }

        //先序遍历找到合适的节点
        if (tree.getDensity() >= density) {
            //继续遍历下面的所以有节点判断是否归结
            //如果归结
            if (resoluteOrNot(tree, density)) {
                //保存
                MyUtils.serializable("resolute_" + path + "_" + num + "_.txt", tree);
                return;
            }
        }
        resolute(tree.ptr_left, density, path, "0", num);
        resolute(tree.ptr_right, density, path, "1", num);
//        return;
    }

    /**
     * 判断子树是否归结
     *
     * @param tree
     * @param density
     * @return boolean true：归结， false：不归结
     * @author 13540
     * @date 2021-01-26 21:23
     */
    public boolean resoluteOrNot(NoneLeafNode tree, double density) {
        if (tree.ptr_sub_left != null || tree.ptr_sub_right != null) {
            //遍历叶子节点
            if (tree.ptr_sub_left != null) {
                if (tree.ptr_sub_left.getDensity() < density) {
                    return false;
                }
                for (int i = 0; i < tree.ptr_sub_left.getNumOfLeafNodes(); i++) {
                    if (tree.ptr_sub_left.leafNodes.get(i).getPm() < density) {
                        return false;
                    }
                }
            }

            if (tree.ptr_sub_right != null) {
                if (tree.ptr_sub_right.getDensity() < density) {
                    return false;
                }
                for (int i = 0; i < tree.ptr_sub_right.getNumOfLeafNodes(); i++) {
                    if (tree.ptr_sub_right.leafNodes.get(i).getPm() < density) {
                        return false;
                    }
                }
            }

            return (tree.getDensity() >= density);
        }
        boolean left = resoluteOrNot(tree.ptr_left, density);
        boolean right = resoluteOrNot(tree.ptr_right, density);
        return (tree.getDensity() >= density) && left && right;
    }

    /**
     * 分类
     *
     * @param tree     树
     * @param filePath 归结文件所在路径
     * @param k        维度
     * @return void
     * 步骤：首先查找文件名以resolute开头的文件，提取出其中的中间部分（0表示左子树，1表示右子树）
     * 节点归结：
     * 先找到已经归结的节点：把resoluteOrNot标记为true，后面把该节点用一个叶子节点合亚叶子节点替换掉
     * 根据分割出来的文件名，进行分类
     * 分类原则：相邻不相邻
     * @author 13540
     * @date 2021-04-30 16:33
     */
    public void classify(NoneLeafNode tree, String filePath, int k, int circle) {
        String[] fileName = getFileName(filePath, circle);
//        String[] fileName = new String[fileNameAndCircle.length];
//        String[] circle = new String[fileNameAndCircle.length];

//        for (int i = 0; i < fileNameAndCircle.length; i++) {
//            fileName[i] = fileNameAndCircle[i].split("_")[0];
//            circle[i] = fileNameAndCircle[i].split("_")[1];
//        }

        ArrayList<Stack<NoneLeafNode>> pathNodeStack = new ArrayList<>();
        resultTypes = new StringBuffer[fileName.length];
        Arrays.fill(resultTypes, new StringBuffer(""));
        //进行节点替换，并将路径存入栈中
        int typeNum = 0;
        for (String s : fileName) {
            NoneLeafNode p = tree;
            Stack<NoneLeafNode> stack = new Stack<>();
            stack.push(p);
            System.out.println("类型" + (typeNum + 1) + " => 归结点所在位置：" + s + "（0：左、1：右）");
            for (int i = 0; i < s.length(); i++) {
                if ('0' == s.charAt(i)) {
                    if (p.ptr_left != null) {
                        p = p.ptr_left;
                        stack.push(p);
                        //如果最后一个节点是非叶子节点、该节点替换成亚叶子节点
                        if (i == s.length() - 1) {
                            NoneLeafNode popTop = stack.pop();
                            NoneLeafNode top = stack.peek();
                            SubLeafNode newSubLeafNode = new SubLeafNode(popTop.getRange(), popTop.getF(), popTop.getDensity(), 0);
                            newSubLeafNode.leafNodes.add(new LeafNode(newSubLeafNode.getRange(), newSubLeafNode.getF(), newSubLeafNode.getDensity(), 0, 0, "new"));
                            top.ptr_sub_left = newSubLeafNode;
                            top.ptr_left = null;
                            top.ptr_sub_left.setResoluteOrNot(true);
                            top.ptr_sub_left.setType("类型" + (++typeNum));
                            resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append(" " + Arrays.toString(top.ptr_sub_left.getRange()));
//                            System.out.println("类型" + typeNum + ": " + Arrays.toString(top.ptr_sub_left.getRange()));
                            //栈顶元素：用于存放范围
                            stack.push(new NoneLeafNode(p.getRange()));
                        }
                    } else {
                        p.ptr_sub_left.setResoluteOrNot(true);
                        p.ptr_sub_left.setType("类型" + (++typeNum));
                        resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append(" " + Arrays.toString(p.ptr_sub_left.getRange()));
//                        System.out.println("类型" + typeNum + ": " + Arrays.toString(p.ptr_sub_left.getRange()));
                        stack.push(new NoneLeafNode(p.ptr_sub_left.getRange()));
                    }
                } else if ('1' == s.charAt(i)) {
                    if (p.ptr_right != null) {
                        p = p.ptr_right;
                        stack.push(p);
                        if (i == s.length() - 1) {
                            NoneLeafNode popTop = stack.pop();
                            NoneLeafNode top = stack.peek();
                            SubLeafNode newSubLeafNode = new SubLeafNode(popTop.getRange(), popTop.getF(), popTop.getDensity(), 0);
                            newSubLeafNode.leafNodes.add(new LeafNode(newSubLeafNode.getRange(), newSubLeafNode.getF(), newSubLeafNode.getDensity(), 0, 0, "new"));
                            top.ptr_sub_right = newSubLeafNode;
                            top.ptr_right = null;
                            top.ptr_sub_right.setResoluteOrNot(true);
                            top.ptr_sub_right.setType("类型" + (++typeNum));
                            resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append("" + Arrays.toString(top.ptr_sub_right.getRange()));
//                            System.out.println("类型" + typeNum + ": " + Arrays.toString(top.ptr_sub_right.getRange()));
                            //栈顶元素：用于存放范围
                            stack.push(new NoneLeafNode(p.getRange()));
                        }
                    } else {
                        p.ptr_sub_right.setResoluteOrNot(true);
                        p.ptr_sub_right.setType("类型" + (++typeNum));
                        resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append(" " + Arrays.toString(p.ptr_sub_right.getRange()));
//                        System.out.println("类型" + typeNum + ": " + Arrays.toString(p.ptr_sub_right.getRange()));
                        stack.push(new NoneLeafNode(p.ptr_sub_right.getRange()));
                    }
                }

            }
            pathNodeStack.add(stack);
        }

        //遍历栈，弹出前2k个进行分类
        for (int i = 0; i < pathNodeStack.size(); i++) {
            Stack<NoneLeafNode> tempStack = pathNodeStack.get(i);
            int num = 0;
            NoneLeafNode rangeNode = tempStack.pop();
            NoneLeafNode lastNode = null;//最后一个弹出栈的节点
            while (!tempStack.empty() && num < (k << 1)) {
                if (tempStack.size() == 1 || num == (k << 1) - 1) {
                    lastNode = tempStack.peek();
                }
                NoneLeafNode tempNode = tempStack.pop();
                //分类
                doClassify(tempNode, rangeNode.getRange(), "类型" + (i + 1), i + 1);
                num++;
            }
            //已查询清空类型清空
            clearFindAndType(lastNode);
        }
    }

    /**
     * 根据归结的文件名分割出每个归结点的位置在哪里。
     *
     * @param filePath
     * @return java.lang.String[]
     * @author 13540
     * @date 2021-04-30 17:23
     */
    public String[] getFileName(String filePath, int circle) {
        File dirs = new File(filePath);
        String[] res = dirs.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isFile() && file.getName().startsWith("resolute_") && file.getName().endsWith(circle + "_.txt");
            }
        });
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].split("_")[1];
        }
        return res;
    }

    /**
     * 分类
     *
     * @param head  栈中弹出的节点
     * @param range 一个类的框
     * @param type  设置的类型
     * @return void
     * @author 13540
     * @date 2021-05-01 15:02
     */
    public void doClassify(NoneLeafNode head, double[] range, String type, int typeNum) {
        if (head == null || head.isResoluteOrNot() || head.isFindOrNot()) {
            return;
        }
        //叶子节点：查看是否相交
        if (head.ptr_sub_left != null && !head.ptr_sub_left.isResoluteOrNot() && !head.ptr_sub_left.isFindOrNot()) {
//            crossOrNot(head.ptr_sub_left, range);
            for (int i = 0; i < head.ptr_sub_left.getNumOfLeafNodes(); i++) {
                LeafNode leafNode = head.ptr_sub_left.leafNodes.get(i);
                if (crossOrNot(leafNode, range)) {
                    head.setFindOrNot(true);
                    //设置为一类
                    if (head.ptr_sub_left.leafNodes.get(i).getType() == null) {
                        resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append(" " + Arrays.toString(head.ptr_sub_left.leafNodes.get(i).getMk()));
//                        System.out.println(type + ": " + Arrays.toString(head.ptr_sub_left.leafNodes.get(i).getMk()));
                        head.ptr_sub_left.leafNodes.get(i).setType(type);
                    }
                }
            }
        }
        //叶子节点：查看是否相交
        if (head.ptr_sub_right != null && !head.ptr_sub_right.isResoluteOrNot() && !head.ptr_sub_right.isFindOrNot()) {
            for (int i = 0; i < head.ptr_sub_right.getNumOfLeafNodes(); i++) {
                LeafNode leafNode = head.ptr_sub_right.leafNodes.get(i);
                if (crossOrNot(leafNode, range)) {
                    head.setFindOrNot(true);
                    //设置为一类
                    if (head.ptr_sub_right.leafNodes.get(i).getType() == null) {
                        resultTypes[typeNum - 1] = new StringBuffer(resultTypes[typeNum - 1]).append(" " + Arrays.toString(head.ptr_sub_right.leafNodes.get(i).getMk()));
//                        System.out.println(type + ": " + Arrays.toString(head.ptr_sub_right.leafNodes.get(i).getMk()));
                        head.ptr_sub_right.leafNodes.get(i).setType(type);
                    }

                }
            }
        }

        doClassify(head.ptr_left, range, type, typeNum);
        doClassify(head.ptr_right, range, type, typeNum);
    }

    public void clearFindAndType(NoneLeafNode head) {
        if (head == null) {
            return;
        }

        if (head.ptr_sub_left != null) {
            head.setFindOrNot(false);
        }

        if (head.ptr_sub_right != null) {
            head.setFindOrNot(false);
        }
        head.setFindOrNot(false);
        clearFindAndType((head.ptr_left));
        clearFindAndType((head.ptr_right));
    }
}
