package technology.tabula;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.CommandLineApp.TableExtractor;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import technology.tabula.writers.CSVWriter;
import technology.tabula.writers.JSONWriter;
import technology.tabula.writers.TSVWriter;
import technology.tabula.writers.Writer;

public class CYTest {
	private static final int RELATIVE_AREA_CALCULATION_MODE = 0;
    private static final int ABSOLUTE_AREA_CALCULATION_MODE = 1;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		PDDocument pdfDocument = PDDocument.load(new File("/Users/cy2465/Documents/softwares/Parsing-PDFs-using-YOLOV3-master/test.pdf"));
	    ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
		Integer page1 = Integer.decode("2");
        float x1 = Float.parseFloat("10.419129521735185");
        float y1 = Float.parseFloat("721.4400032667801");
        float x2 = Float.parseFloat("287.9386726723398");
        float y2 = Float.parseFloat("182.85718930249433");

        //List<Rectangle> pageTables = new ArrayList<Rectangle>();
        
        //Page extractedPage = extractor.extractPage(page1);

        float top = y1;
        float left = x1;
        float width = x2 - x1;
        float height = y2 - y1;

        //pageTables.add(new Rectangle(top, left, width, height));
        
        List<Pair<Integer, Rectangle>> pageAreas = new ArrayList<Pair<Integer, Rectangle>>();
        
        pageAreas.add(new Pair<Integer, Rectangle>(ABSOLUTE_AREA_CALCULATION_MODE,new Rectangle(top, left, width, height)));
		
        List<Integer> pages=new ArrayList<Integer>();
        pages.add(6);
        
        TableExtractor tableExtractor = new TableExtractor();
        try {
            //pdfDocument = this.password == null ? PDDocument.load(pdfFile) : PDDocument.load(pdfFile, this.password);
            PageIterator pageIterator = getPageIterator(pdfDocument,pages);
            List<Table> tables = new ArrayList<>();

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
            System.out.println("size:"+tables.size());
            Table ta=tables.get(0);
            System.out.println(ta.toString());
            System.out.println("cells="+ta.cells);
            System.out.println("row="+ta.getRowCount());
            System.out.println("col="+ta.getColCount());
            
            for(int r=0;r<ta.getRowCount();r++) {
            	for(int c=0;c<ta.getColCount();c++) 
            	{
            		System.out.print("["+ta.getCell(r, c).getText()+"]\t");
            	}
            	System.out.println();
            }
           // writeTables(tables, outFile);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
            } catch (IOException e) {
                System.out.println("Error in closing pdf document" + e);
            }
        }
        
        //OutputFormat oft=OutputFormat.
        /*
       
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(CommandLineApp.buildOptions(), new String[]{
                "/Users/cy2465/Documents/softwares/Parsing-PDFs-using-YOLOV3-master/test.pdf",
                "-p", "3", "-a",
                "286.157,542.821,578.324,348.041", "-f",
                "CSV","-o", "/Users/cy2465/Documents/softwares/Parsing-PDFs-using-YOLOV3-master/tt.csv"
        });

        StringBuilder stringBuilder = new StringBuilder();
        new CommandLineApp(stringBuilder, cmd).extractTables(cmd);

        */
	}

	private static PageIterator getPageIterator(PDDocument pdfDocument, List<Integer> pages) throws IOException {
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        return (pages == null) ?
                extractor.extract() :
                extractor.extract(pages);
    }
	
	
}
