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

public class TextExtractionCaseStudy {
	private static final int RELATIVE_AREA_CALCULATION_MODE = 0;
	private static final int ABSOLUTE_AREA_CALCULATION_MODE = 1;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String json_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/casestudyjson2/";
		String img_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/casestudyimgs/";
		String pdf_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/usecasepdfs/";
		String table_dir="/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/tables/";
		String tablemeta_dir="/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/tabledata/";
		File dir = new File(pdf_dir);
		File[] files = dir.listFiles();
		StringBuffer tpl=new StringBuffer();
		StringBuffer tpkeys=new StringBuffer();
		StringBuffer tpcolkeys=new StringBuffer();
		int tablenum=0;
		for (File pf : files) {
			System.out.println(pf.getAbsolutePath());
			if(pf.getName().endsWith(".pdf")==false) {
				continue;
			}					
			PDDocument pdfDocument = PDDocument.load(new File(pf.getAbsolutePath()));
			String fname = pf.getName();
			String fid = fname.substring(0, fname.length() - 4);
			int tableid=1;
			for (int i = 0; i < 20; i++) {
				String json_path = json_dir + fid + "-" + i + ".json";
				File jsonfile = new File(json_path);
				if (jsonfile.exists() == false) {
					continue;
				}
				String jsonstr = FileUtil.readFile(json_path);
				System.out.println("jsonfile:" + json_path);
				JSONObject jobj=JSONObject.fromObject(jsonstr);	
				
				JSONArray ja = JSONArray.fromObject(jobj.get("points"));

				tablenum =tablenum +ja.size();
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
					String updated_img_path=img_dir + fid + "-" + i + ".jpg";			
					System.out.println("----------------------------->"+img_path);				
					float x1 = x_from;
					float x2 =x_to;
					float y1 = y_from;
					float y2 = y_to;
					
					write2imgfile(img_dir, pf,i, k, x_from, x_to, y_from, y_to, updated_img_path);

					System.out.println(x1 + "\t" + x2 + ";\t" + y1 + "\t" + y2);

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
					StringBuffer nsb=new StringBuffer();
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
								nsb.append(r+"\t"+c+"\t"+ta.getCell(r, c).getText()+"\n");
							}
							sb.append("\n");
							System.out.println();
						}
						// writeTables(tables, outFile);
					} catch (IOException e) {
						throw new ParseException(e.getMessage());
					}	
					System.err.println(table_dir+pf.getName()+"-"+k+".txt");
					
					FileUtil.write2File(table_dir+pf.getName()+"-"+tableid+".txt", sb.toString());
					FileUtil.write2File(tablemeta_dir+pf.getName()+"-"+tableid+".txt", nsb.toString());
					tableid++;
				}
			}
			try {
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			} catch (IOException e) {
				System.out.println("Error in closing pdf document" + e);
			}
			
		}
		System.out.println("tablenum="+tablenum);
	}
	
	private static void write2imgfile(String img_dir, File pf, int i,int k, float x_from, float x_to, float y_from,
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
		
		System.out.println("write to "+img_dir+pf.getName()+"-"+i+"-"+k+".jpg");
		Imgcodecs.imwrite(img_dir+pf.getName()+"-"+i+"-"+k+".jpg", target);
	}


	
	private static String nearestLine(String imgpath, int startrow, int endrow) {
		Mat src = Imgcodecs.imread(imgpath);
		Mat img = new Mat();
		Imgproc.threshold(src, img,210,255,Imgproc.THRESH_BINARY);
		List<Integer> linepos=new ArrayList<Integer>();
		List<Integer> startindex=new ArrayList<Integer>();
		List<Integer> endindex=new ArrayList<Integer>();
		List<Integer> line=new ArrayList<Integer>();
		int blackdot=0;
		for(int i=startrow;i<endrow;i++) {
			blackdot=0;
			for(int j=0;j<img.size().width;j++) {
				if(img.get(i,j)[0]==0 &&  img.get(i, j+1)[0]==0) {
					blackdot++;
					line.add(j);
				}else {
					blackdot++;
					if(blackdot>10) {
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
			return list.get(0).getKey();
		}else {
			return null;
		}
	}
	
	private static PageIterator getPageIterator(PDDocument pdfDocument, List<Integer> pages) throws IOException {
		ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
		return (pages == null) ? extractor.extract() : extractor.extract(pages);
	}

}
