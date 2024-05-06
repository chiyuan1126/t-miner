package technology.tabula.pipeline;

import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Pair;
import technology.tabula.Rectangle;
import technology.tabula.Table;
import technology.tabula.CommandLineApp.TableExtractor;
import technology.tabula.Utils;

public class TextExtractionAD {
	private static final int RELATIVE_AREA_CALCULATION_MODE = 0;
	private static final int ABSOLUTE_AREA_CALCULATION_MODE = 1;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String json_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/json/";
		String img_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/auto/";
		String reimg_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/image/";
		String pdf_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/usecasepdfs/";
		String imgad_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/image_ad/";
		String table_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/tables_ad/";
		String agg_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/aggdata/";
		File dir = new File(pdf_dir);
		File[] files = dir.listFiles();
		StringBuffer tpl = new StringBuffer();
		StringBuffer tpkeys = new StringBuffer();
		StringBuffer tpcolkeys = new StringBuffer();
		int tablenum = 0;
		for (File pf : files) {
			System.out.println(pf.getAbsolutePath());
			if (pf.getName().endsWith(".pdf") == false) {
				continue;
			}

			PDDocument pdfDocument = PDDocument.load(new File(pf.getAbsolutePath()));
			String fname = pf.getName();
			String fid = fname.substring(0, fname.length() - 4);

			for (int i = 0; i < 20; i++) {
				String json_path = json_dir + fid + "-" + i + ".json";
				File jsonfile = new File(json_path);
				if (jsonfile.exists() == false) {
					continue;
				}
				System.out.println("|--------------------------------------------------------|");
				System.out.println("|--------------------"+pf.getName()+"--------------------|");
				System.out.println("|--------------------------------------------------------|");
				
				System.out.println(">>>>>>>>>>>>>>>Page:"+i+"<<<<<<<<<<<<<<<");
				String jsonstr = FileUtil.readFile(json_path);
				System.out.println("jsonfile:" + json_path);
				JSONArray ja = JSONArray.fromObject(jsonstr);
				
				tablenum =tablenum +ja.size();
				System.out.println("Table count:"+ja.size());
				for (int k = 0; k < ja.size(); k++) {
					
					
					List<Pair<Integer, Rectangle>> pageAreas = new ArrayList<Pair<Integer, Rectangle>>();
					List<Integer> pages = new ArrayList<Integer>();
					JSONObject jo = JSONObject.fromObject(ja.get(k));
					JSONObject con = jo.getJSONObject("content");
					float x_from = Float.parseFloat(con.getString("x_from"));
					float x_to = Float.parseFloat(con.getString("x_to"));
					float y_from = Float.parseFloat(con.getString("y_from"));
					float y_to = Float.parseFloat(con.getString("y_to"));

					String img_path = img_dir + fid + "-" + i + ".jpg";
					//System.out.println("----------------------------->"+img_path);
					System.out.println(">>>>>>>>>>>>>>>Table:"+k+"\t"+img_path+"<<<<<<<<<<<<<<<");
					String updated_img_path=reimg_dir + fid + "-" + i + ".jpg";
					System.out.println("------------OLD-----------------");
					System.out.println("x:"+x_from+"\t"+x_to);
					System.out.println("y:"+y_from+"\t"+y_to);
					System.out.println("--------------------------------");
					
					int fixedwidthbotton=0;
					int fixedwidthtop=0;
					int fixedbottomstartcol=0;
					int fixedbottomendcol=0;
					int startrow=(int) y_to;
					System.out.println("Scanning from:"+(startrow-12)+"\t"+(startrow+8));
					System.out.println("img path:"+img_path);
					String linepos=nearestLine(img_path,startrow-12,startrow+8,x_from, x_to);
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
					System.out.println("Scanning from:"+(startrow-12)+"\t"+(startrow+8));
					linepos=nearestLine(img_path,startrow-12,startrow+8,x_from, x_to);
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
					
					float x1 = x_from;
					float x2 =x_to;
					float y1 = y_from;
					float y2 = y_to ;

					System.out.println(x1 + "\t" + x2 + ";\t" + y1 + "\t" + y2);
					
					System.out.println("k=\t"+i);				
					write2imgfile(imgad_dir, pf,i, k, x_from, x_to, y_from, y_to, updated_img_path);
					
					float top = y1;
					float left = x1;
					float width = x2 - x1;
					float height = y2 - y1;
					
					pages.add(i + 1);
					// pageTables.add(new Rectangle(top, left, width, height));
					pageAreas.add(new Pair<Integer, Rectangle>(ABSOLUTE_AREA_CALCULATION_MODE,
							new Rectangle(top, left, width, height)));
					TableExtractor tableExtractor = new TableExtractor();
					StringBuffer sb=new StringBuffer();
					try {
						// pdfDocument = this.password == null ? PDDocument.load(pdfFile) :
						// PDDocument.load(pdfFile, this.password);
						PageIterator pageIterator = getPageIterator(pdfDocument, pages);
						List<Table> tables = new ArrayList<>();
						System.out.println("pages:" + pages);
						while (pageIterator.hasNext()) {
							Page page = pageIterator.next();
							if (pageAreas != null) {
								for (Pair<Integer, Rectangle> areaPair : pageAreas) {
									Rectangle area = areaPair.getRight();
									System.out.println(area);
									System.out.println(page);
									tables.addAll(tableExtractor.extractTables(page.getArea(area)));
								}
							} else {
								tables.addAll(tableExtractor.extractTables(page));
							}
						}
						Table ta = tables.get(0);
						System.out.println("cells=" + ta.cells);
						System.out.println("row=" + ta.getRowCount());
						System.out.println("col=" + ta.getColCount());
						
						for (int r = 0; r < ta.getRowCount(); r++) {
							for (int c = 0; c < ta.getColCount(); c++) {
								System.out.print(r + "," + c + " " + "[" + ta.getCell(r, c).getText() + "]\t");
								sb.append(ta.getCell(r, c).getText()+"\t");
							}
							sb.append("\n");
							System.out.println();
						}
						// writeTables(tables, outFile);
					} catch (IOException e) {
						throw new ParseException(e.getMessage());
					}	
					
					FileUtil.write2File(table_dir+pf.getName()+"-"+k+".txt", sb.toString());
				}
			}
		}
	}

	private static void write2imgfile(String table_dir, File pf, int i,int k, float x_from, float x_to, float y_from,
			float y_to, String updated_img_path) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		double[] newbox= {255.0,0,255.0};
		Mat target = Imgcodecs.imread(updated_img_path);
		
		for(int p=(int) x_from;p<x_to;p++) {
			target.put((int) y_from, p, newbox);
			target.put((int) y_to, p, newbox);
		}
		for(int q=(int) y_from;q<y_to;q++) {
			target.put(q, (int) x_from, newbox);
			target.put(q, (int) x_to, newbox);
		}
		
		System.out.println("write to "+table_dir+pf.getName()+"-"+i+"-"+k+".jpg");
		Imgcodecs.imwrite(table_dir+pf.getName()+"-"+i+"-"+k+".jpg", target);
	}

	private static String nearestLine(String imgpath, int startrow, int endrow,float x_from,float x_to) {
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
