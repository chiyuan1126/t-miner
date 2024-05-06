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
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;


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


//1111


public class TableDataExtractionNaive {
	private static final int RELATIVE_AREA_CALCULATION_MODE = 0;
	private static final int ABSOLUTE_AREA_CALCULATION_MODE = 1;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		//json_dir是二级目录
		//String json_dir = "D:\\test\\X152\\josnfile";
		String json_dir = args[0];
		//img_dir是二级目录
		//String img_dir = "D:\\test\\X152\\pdf2img";
		String img_dir = args[1];
		//reimg_dir是二级目录 重绘图片路径
		//String reimg_dir = "D:\\test\\X152\\reimg";
		String reimg_dir = args[2];
		//String pdf_dir = "D:\\test\\X152\\pdfdata\\";
		String pdf_dir = args[3];
		//String imgad_dir = "D:\\test\\X152\\imgnoh";//该路径是没有重定位后的路径
		//String imgad_dir =  args[4];
		//String imgad_dir = "D:\\test\\X152\\imgnoh\\";//该路径是经过重定位的直接预测的图片路径
		//String table_dir = "D:\\test\\X152\\table_dir_noh";//该路径是重定位后抽取的表格数据
		String table_dir = args[4];
		//String table_dir = "D:\\test\\detection\\table_dir_non\\";//该路径是没有重定位的表格数据
		//String agg_dir = "/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/aggdata/";
		File dir = new File(pdf_dir);
		File[] files = dir.listFiles();
		StringBuffer tpl = new StringBuffer();
		StringBuffer tpkeys = new StringBuffer();
		StringBuffer tpcolkeys = new StringBuffer();
		int tablenum = 0;
		for (File pf : files) {
			try {
			System.out.println(pf.getAbsolutePath());
			if (pf.getName().endsWith(".pdf") == false) {
				continue;
			}
			PDDocument pdfDocument = PDDocument.load(new File(pf.getAbsolutePath()));
			String fname = pf.getName();
			String fid = fname.substring(0, fname.length() - 4);

			for (int i = 0; i < 20; i++) {
				//jsonfile是二级目录
				String json_path = json_dir + File.separator + fid + File.separator + i + ".json";
				System.out.println(json_path);
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
				
				tablenum = tablenum + ja.size();
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

					
					System.out.println("------------OLD-----------------");
					System.out.println("x:"+x_from+"\t"+x_to);
					System.out.println("y:"+y_from+"\t"+y_to);
					System.out.println("--------------------------------");
					
					
									
					System.out.println("------------NEW-----------------");
					System.out.println("x:"+x_from+"\t"+x_to);
					System.out.println("y:"+y_from+"\t"+y_to);
					System.out.println("--------------------------------");
					
					float x1 = x_from;
					float x2 = x_to;
					float y1 = y_from;
					float y2 = y_to ;

					System.out.println(x1 + "\t" + x2 + ";\t" + y1 + "\t" + y2);
					
					System.out.println("k=\t"+i);				
					//write2imgfile(reimg_dir, pf,i, k, x_from, x_to, y_from, y_to, updated_img_path);

					//等比率缩放到pdf文件中 宽和高：868*1227
					PDPage pdfpage = pdfDocument.getPage(i);
					float pageWidth = pdfpage.getMediaBox().getWidth();
					float pageHeight = pdfpage.getMediaBox().getHeight();
					//System.out.println(pageWidth);
					//System.out.println(pageHeight);



					// 该代码是重定位的坐标
					/**
					 * float pdfX1 = (x1 / 868) * pageWidth;
					 * 					float pdfY1 = ((y1) / 1227) * pageHeight;
					 * 					float pdfX2 = (x2 / 868) * pageWidth;
					 * 					float pdfY2 = ((y2) / 1227) * pageHeight;
					 * 					System.out.println("------------------------分割线---------------------------");
					 * 					System.out.println("pdfX1 :"+ pdfX1);
					 * 					System.out.println("pdfY1 :"+ pdfY1);
					 * 					System.out.println("pdfX2 :"+ pdfX2);
					 * 					System.out.println("pdfY2 :"+ pdfY2);
					 */



					 //该代码是没有经过重定位的坐标
					float pdfX1 = (x_from / 868) * pageWidth;
					float pdfY1 = ((y_from) / 1227) * pageHeight;
					float pdfX2 = (x_to / 868) * pageWidth;
					float pdfY2 = ((y2) / 1227) * pageHeight;



					 float top = pdfY1;
					 float left = pdfX1;
					 float width = pdfX2 - pdfX1;
					 float height = pdfY2 - pdfY1;

					
					pages.add(i + 1);
					// pageTables.add(new Rectangle(top, left, width, height));
					pageAreas.add(new Pair<Integer, Rectangle>(ABSOLUTE_AREA_CALCULATION_MODE,
							new Rectangle(top, left, width, height)));
					TableExtractor tableExtractor = new TableExtractor();
					StringBuffer sb = new StringBuffer();
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
					String filePath = table_dir + File.separator + pf.getName() + File.separator + pf.getName()+"-"+i+"-"+k+".txt";
					File output = new File(filePath);
					output.getParentFile().mkdirs();
					FileUtil.write2File(filePath, sb.toString());
				}
			}
			}catch(Exception ex) {
				System.out.println(ex.getMessage());
				}
		}
	}

	

	

	private static PageIterator getPageIterator(PDDocument pdfDocument, List<Integer> pages) throws IOException {
		ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
		return (pages == null) ? extractor.extract() : extractor.extract(pages);
	}

}
