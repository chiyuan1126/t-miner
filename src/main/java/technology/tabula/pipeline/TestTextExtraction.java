package technology.tabula.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;

import net.sf.json.JSONObject;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Pair;
import technology.tabula.Rectangle;
import technology.tabula.Table;
import technology.tabula.CommandLineApp.TableExtractor;

public class TestTextExtraction {
	private static final int RELATIVE_AREA_CALCULATION_MODE = 0;
	private static final int ABSOLUTE_AREA_CALCULATION_MODE = 1;

	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		List<Pair<Integer, Rectangle>> pageAreas = new ArrayList<Pair<Integer, Rectangle>>();
		List<Integer> pages = new ArrayList<Integer>();
		PDDocument pdfDocument = PDDocument.load(new File("/Users/cy2465/Downloads/usecasefiles/2011.jeptalnrecital-long.27.pdf"));
//		float x_from = Float.parseFloat("");
//		float x_to = Float.parseFloat("");
//		float y_from = Float.parseFloat("");
//		float y_to = Float.parseFloat("");
//
//		float x1 = Float.parseFloat("");
//		float x2 = Float.parseFloat("");
//		float y1 = Float.parseFloat("");
//		float y2 =Float.parseFloat("");

		//System.out.println(x1 + "\t" + x2 + ";\t" + y1 + "\t" + y2);

		
		
//		float top = Float.parseFloat("53.83");
//		float left = Float.parseFloat("134.15");
//		float width = Float.parseFloat("160.06");
//		float height = Float.parseFloat("136.43");
		
		float x_from = Float.parseFloat("162.34");
		float x_to = Float.parseFloat("425.08");
		float y_from = Float.parseFloat("451.18");
		float y_to = Float.parseFloat("616.71");
		
		float x1 = x_from;
		float x2 = x_to;
		float y1 = y_from;
		float y2 = y_to;
		
		
		
		float top = y1;
		float left = x1;
		float width = x2 - x1;
		float height = y2 - y1;
		
		System.out.println("x1:"+x1+"\t x2:"+x2+"\t y1:"+y1+"\t y2:"+y2);
		
		System.out.println("top:"+top+"\t left:"+left+"\t width:"+width+"\t height:"+height);
		
		pages.add(9);
		// pageTables.add(new Rectangle(top, left, width, height));
		pageAreas.add(new Pair<Integer, Rectangle>(ABSOLUTE_AREA_CALCULATION_MODE,
				new Rectangle(top, left, width, height)));
		TableExtractor tableExtractor = new TableExtractor();
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
					
				}
				System.out.println();
				
			}
			// writeTables(tables, outFile);
		} catch (IOException e) {
			throw new ParseException(e.getMessage());
		}
	}
	private static PageIterator getPageIterator(PDDocument pdfDocument, List<Integer> pages) throws IOException {
		ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
		return (pages == null) ? extractor.extract() : extractor.extract(pages);
	}

}
