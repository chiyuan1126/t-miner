package technology.tabula.pipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TestImage {

	static {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        //compareHist_2();
        String con=FileUtil.readFile("/Users/cy2465/Downloads/LANDrop/yolo/all.txt");
        String[] rows=con.split("\n");
        Set<String> set=new HashSet<String>();
        List<String> list=new ArrayList<String>();
        for(String r:rows) {
        	//System.out.println(r);
        	String[] en=r.split("\t");
        	//System.out.println(en[1]);
        	set.add(en[1]);
        	list.add(en[1]);
        }
        for(int i=0;i<list.size();i++) {
        	 for(int j=i;j<list.size();j++) {
        		 System.out.println(list.get(i)+"\tvs\t"+list.get(j));
             }
        } 
    }

    /**
     * OpenCV-4.0.0 直方图比较
     *    
     * @return: void  
     * @date: 2020年1月14日20:15:39
     */
    public static void compareHist_1() {
        Mat src = Imgcodecs.imread("C:\\Users\\Administrator\\Pictures\\3.jpeg");
        
        Mat hsv = new Mat();
        
        //图片转HSV
        Imgproc.cvtColor(src, hsv,Imgproc.COLOR_BGR2HSV);

        Mat hist = new Mat();
        //直方图计算
        Imgproc.calcHist(Stream.of(hsv).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist,new MatOfInt(255) ,new MatOfFloat(0,256));
        //图片归一化
        Core.normalize(hist, hist, 1, hist.rows() , Core.NORM_MINMAX, -1, new Mat() );
        //直方图比较
        double a = Imgproc.compareHist(hist,hist,Imgproc.CV_COMP_CORREL);
        System.out.println("越接近1越相识度越高\n比较结果："+a);
    }

    /**
     * OpenCV-4.0.0 直方图比较
     *    
     * @return: void  
     * @date: 2020年1月14日20:15:39
     */
    public static double compareHist_2(String path1,String path2) {
        Mat src_1 = Imgcodecs.imread("/Users/cy2465/Downloads/20190227004643.jpeg");// 图片 1
        Mat src_2 = Imgcodecs.imread("/Users/cy2465/Downloads/20190227004642.jpeg");// 图片 2

        Mat hvs_1 = new Mat();
        Mat hvs_2 = new Mat();
        //图片转HSV
        Imgproc.cvtColor(src_1, hvs_1,Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src_2, hvs_2,Imgproc.COLOR_BGR2HSV);

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //直方图计算
        Imgproc.calcHist(Stream.of(hvs_1).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist_1,new MatOfInt(255) ,new MatOfFloat(0,256));
        Imgproc.calcHist(Stream.of(hvs_2).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist_2,new MatOfInt(255) ,new MatOfFloat(0,256));

        //图片归一化
        Core.normalize(hist_1, hist_1, 1, hist_1.rows() , Core.NORM_MINMAX, -1, new Mat() );
        Core.normalize(hist_2, hist_2, 1, hist_2.rows() , Core.NORM_MINMAX, -1, new Mat() );

        //直方图比较
       
        double b = Imgproc.compareHist(hist_1,hist_2,Imgproc.CV_COMP_CORREL);
        return b;
    }
    

}
