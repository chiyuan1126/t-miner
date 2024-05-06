package technology.tabula.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import technology.tabula.ObjectExtractor;
import technology.tabula.PageIterator;

public class EvalTableRecognition {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File golddir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/wujiazhen_ann_0620/json");
		File[] fs=golddir.listFiles();
		int allp=0;
		int allcorrect=0;
		int allg=0;
		double iouset=0.85; 
		for(File f:fs) {
			if(f.getName().endsWith(".json")) {
				System.out.println(f.getAbsolutePath());
				String predict_base_dir="/Users/cy2465/Documents/projects/2022_Tabular_Info/predictjson0620/";
				System.out.println(predict_base_dir+f.getName());
				String predict_file=predict_base_dir+f.getName();
				String content=FileUtil.readFile(predict_file);
				System.out.println("----------------------");
				System.out.println(content);
				JSONObject predicjo=JSONObject.fromObject(content);
				System.out.println("----------points------------");
				System.out.println(predicjo.get("points"));
				System.out.println("----------------------");
				JSONArray ja=JSONArray.fromObject(predicjo.get("points"));
				String goldcontent=FileUtil.readFile(f.getAbsolutePath());
				JSONObject gjo=JSONObject.fromObject(goldcontent);
				JSONArray pja=JSONArray.fromObject(gjo.get("shapes"));
				int matchcount=0;
				for(int i=0;i<ja.size();i++) {
					System.out.println("---->"+ja.get(i));
					JSONObject tablejson=JSONObject.fromObject(ja.get(i));
					JSONObject tablecontent=JSONObject.fromObject(tablejson.get("content"));
					double p_x_from=Double.valueOf(tablecontent.get("x_from").toString());
					double p_x_to=Double.valueOf(tablecontent.get("x_to").toString());
					double p_y_from=Double.valueOf(tablecontent.get("y_from").toString());
					double p_y_to=Double.valueOf(tablecontent.get("y_to").toString());
					
					/*
					//----------rule-based methods start----------------
					String img_path="/Users/cy2465/Documents/projects/2022_Tabular_Info/wujiazhen_ann_0620/json/"+f.getName().substring(0,f.getName().length()-5)+".jpg";
					System.out.println("img_path:"+img_path);
					double x_from=p_x_from;
					double x_to=p_x_to;
					double y_from=p_y_from;
					double y_to=p_y_to;
					System.out.println("------------OLD-----------------");
					System.out.println("x:"+x_from+"\t"+x_to);
					System.out.println("y:"+y_from+"\t"+y_to);
					System.out.println("--------------------------------");
					
					int fixedwidthbotton=0;
					int fixedwidthtop=0;
					int fixedbottomstartcol=0;
					int fixedbottomendcol=0;
					int startrow=(int) y_to;
					System.out.println("Scanning from:"+(startrow-5)+"\t"+(startrow+5));
					System.out.println("img path:"+img_path);
					String linepos=nearestLine(img_path,startrow-5,startrow+5,x_from, x_to);
					System.out.println("底端:"+startrow);
					System.out.println("before width:"+(x_to-x_from));
					System.out.println("yolo start:"+x_from+"\tto"+x_to);
					System.out.println("------------------------------>"+linepos);
					if (linepos != null) {
						String[] loc = linepos.split(" ");
						int fixed_y_to = Integer.valueOf(loc[0]);
						int nb=Integer.valueOf(loc[1]);
						int nt=Integer.valueOf(loc[2]);
						System.out.println("fixed width:"+(nt-nb));
						fixedwidthbotton=nt-nb;
						fixedbottomstartcol=nb;
						fixedbottomendcol=nt;
						System.out.println("start:"+fixedbottomstartcol+"\tto\t"+fixedbottomendcol);
						if(fixed_y_to>y_to) {
							y_to=fixed_y_to;
						}
					}				
					int oldwidth=(int) (x_to-x_from);	
					
					int fixedtopstartcol=0;
					int fixedtopendcol=0;
					startrow=(int) y_from;
					System.out.println("Scanning from:"+(startrow-5)+"\t"+(startrow+5));
					linepos=nearestLine(img_path,startrow-5,startrow+5,x_from, x_to);
					System.out.println("顶端:"+startrow);
					System.out.println("before width:"+(x_to-x_from));
					System.out.println("------------------------------>"+linepos);
					
					if (linepos != null) {
						String[] loc = linepos.split(" ");
						int fixed_y_from = Integer.valueOf(loc[0]);
						int nb=Integer.valueOf(loc[1]);
						int nt=Integer.valueOf(loc[2]);
						System.out.println("fixed width:"+(nt-nb));
						fixedwidthtop=nt-nb;
						fixedtopstartcol=nb;
						fixedtopendcol=nt;
						System.out.println("start:"+fixedtopstartcol+"\tto\t"+fixedtopendcol);
						if(fixed_y_from<y_from) {
							y_from=fixed_y_from;
						}
					}
					//fixed box with pixel location
					int fixed_x_from=0;
					int fixed_x_to=99999;
					System.out.println("fixedwidthtop:"+fixedwidthtop);
					System.out.println("fixedwidthbotton:"+fixedwidthbotton);
					//if(Math.abs(fixedwidthtop-fixedwidthbotton)<5) {
						System.out.println("Perfect Match!!!");
						fixed_x_from= fixedtopstartcol>fixedbottomstartcol?fixedbottomstartcol:fixedbottomstartcol;
						fixed_x_to=fixedtopendcol>fixedbottomendcol?fixedtopendcol:fixedbottomendcol;
					//}
					if(fixed_x_from>0) {
						x_from=x_from>fixed_x_from?fixed_x_from:x_from;
					}
					if(fixed_x_to>0) {
						if(fixed_x_to<99999) {
						x_to=x_to>fixed_x_to?x_to:fixed_x_to;
						}
					}
									
					System.out.println("------------NEW-----------------");
					System.out.println("x:"+x_from+"\t"+x_to);
					System.out.println("y:"+y_from+"\t"+y_to);
					System.out.println("--------------------------------");
					
					p_x_from=x_from;
					p_x_to=x_to;
					p_y_from=y_from;
					p_y_to=y_to;
					
					//----------------rule-based methods end-----------
					*/		
					for(int j=0;j<pja.size();j++) {
						JSONObject pointjson=JSONObject.fromObject(pja.get(j));
						String xy=pointjson.get("points").toString();
						int midindex=xy.indexOf("],[");
						String[] fromxy=xy.substring(2,midindex).split(",");
						double g_x_from=Double.valueOf(fromxy[0]);
						double g_y_from=Double.valueOf(fromxy[1]);
						String[] toxy=xy.substring(midindex+3,xy.length()-2).split(",");
						double g_x_to=Double.valueOf(toxy[0]);
						double g_y_to=Double.valueOf(toxy[1]);
						double iou=calculate(p_x_to,p_y_to,p_x_from,p_y_from,g_x_to,g_y_to,g_x_from,g_y_from);
						System.out.println("IoU:"+iou);
						if(iou>=iouset) {
							matchcount++;
						}
					}
				}
				System.out.println("matchcount:"+matchcount);
				System.out.println("gold:"+pja.size());
				System.out.println("predict:"+ja.size());
				allg=allg+pja.size();
				allp=allp+ja.size();
				allcorrect=allcorrect+matchcount;
			}
		}
		System.out.println("----------------------------");
		System.out.println("IoU:"+iouset);
		System.out.println("G_num:"+allg);
		System.out.println("P_num:"+allp);
		System.out.println("Correct:"+allcorrect);
		float precision=(float)allcorrect/allp;
		float recall=(float)allcorrect/allg;
		float  f1= 2*precision*recall/(precision+recall);
		System.out.println();
		
		System.out.println("Precision:"+precision);
		System.out.println("Recall:"+recall);
		System.out.println("F-1:"+f1);
	}
	
	public static double calculate(double prtx,double prty,double plbx,double plby, double grtx,double grty, double glbx,double glby) {
		double W = Math.min(prtx, grtx) - Math.max(plbx, glbx);
		double H = Math.min(prty, grty) - Math.max(plby, glby);
		if (W <= 0 || H <= 0)
		{
			return 0;
			        
		}
		double SA = (prtx - plbx) * (prty - plby);
		double SB = (grtx - glbx) * (grty - glby);
		double cross = W * H;
		return cross/(SA + SB - cross);
	}
	
	private static String nearestLine(String imgpath, int startrow, int endrow,double x_from,double x_to) {
		Mat src = Imgcodecs.imread(imgpath);
		Mat img = new Mat();
		Imgproc.threshold(src, img,210,255,Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite("/Users/cy2465/Documents/111111.jpg", img);
		
		List<Integer> linepos=new ArrayList<Integer>();
		List<Integer> startindex=new ArrayList<Integer>();
		List<Integer> endindex=new ArrayList<Integer>();
		List<Integer> line=new ArrayList<Integer>();
		int blackdot=0;
		for(int i=startrow;i<endrow;i++) {
			blackdot=0;
			for(int j=0;j<img.size().width;j++) {
				if(img.get(i,j)!=null &&img.get(i,j+1)!=null && img.get(i,j)[0]==0 &&  img.get(i, j+1)[0]==0) {
					blackdot++;
					line.add(j);
				}else {
					blackdot++;
					if(blackdot>50) {
						endindex.add(j);
						startindex.add(line.get(0));
						linepos.add(i);
					}
					blackdot=0;
					line.clear();
				}
			}
		}
		Map<String,Integer> hmap=new HashMap<String,Integer>();
		for(int a=0; a<linepos.size();a++) {
			//System.out.println("row:"+linepos.get(a)+"\tfrom:"+startindex.get(a)+"\tto\t"+endindex.get(a));
			String k=linepos.get(a)+" "+startindex.get(a)+" "+endindex.get(a);
			Integer w=endindex.get(a)-startindex.get(a);
			hmap.put(k, w);
		}
		
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hmap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
//		for (Map.Entry<String, Integer> mapping : list) {
//			System.out.println(mapping.getKey() + ":" + mapping.getValue());
//		}
		if(list.size()>0) {
			System.out.println("--------BEFORE Filtering----list size:"+list.size());
			System.out.println("------------line pos list---------------");
			for(int a =0;a<list.size();a++) {
				Entry<String,Integer> resss=list.get(a);
				System.out.println(resss.getKey());
				String[] arrinfo=resss.getKey().split(" ");
				int rownum=Integer.valueOf(arrinfo[0]);
				int linexfrom=Integer.valueOf(arrinfo[1]);
				int linexto=Integer.valueOf(arrinfo[2]);
				if(linexto<x_from || linexfrom>x_to) {	
					System.out.println("REMOVE!!!!--->"+a);
					list.remove(a);
				}
			}
			System.out.println("------------line pos list end---------------");
			
			System.out.println("--------AFTER Filtering----list size:"+list.size());
			if(list.size()>0) {
				return list.get(0).getKey();
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	private static PageIterator getPageIterator(PDDocument pdfDocument, List<Integer> pages) throws IOException {
		ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
		return (pages == null) ? extractor.extract() : extractor.extract(pages);
	}

}
