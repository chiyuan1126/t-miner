package technology.tabula.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.CommandLineApp.TableExtractor;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.detectors.DetectionAlgorithm;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class TestTabula {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestTabula tt=new TestTabula();
		try {
			//tt.extractTable("/Users/cy2465/Documents/W15-1506-1.pdf");
			File dir=new File("/Users/cy2465/Documents/datasets/testingfiles/");
			File[] files=dir.listFiles();
			for(File f:files) {
				if(f.isDirectory()) {
					System.out.println(f.getAbsolutePath());
					deleteFile(new File(f.getAbsolutePath()+"/predict"));
					String dirpath=f.getAbsolutePath()+"/predict";
					File file = new File(dirpath);
					if (!file.exists()) {
						// 如果目标文件所在的目录不存在，则创建父目录
						// System.out.println("目标文件所在目录不存在，准备创建它！");
						if (!file.mkdirs()) {
							System.out.println("创建目标文件所在目录失败！");
						}
					}
					File[] fs=f.listFiles();
					
					int tid=0;
					for(File fff:fs) {
						System.out.println(fff.getAbsolutePath());
						if(fff.getName().endsWith(".pdf")) {
							File pdffile = new File(fff.getAbsolutePath());
						    InputStream in =new FileInputStream(pdffile);
							try (PDDocument document = PDDocument.load(in)) {
							    SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
							    PageIterator pi = new ObjectExtractor(document).extract();
							    while (pi.hasNext()) {
							        // iterate over the pages of the document
							        Page page = pi.next();
							        //List<Table> table = sea.extract(page);
							        
							        TableExtractor te=new TableExtractor();
							        List<Table> table =te.extractTables(page);
							        // iterate over the tables of the page
							        System.out.println("-------Table------>>>"+table.size());
							        for(Table tables: table) {
							        	StringBuffer sb=new StringBuffer();
							            List<List<RectangularTextContainer>> rows = tables.getRows();
							            // iterate over the rows of the table
							            for (List<RectangularTextContainer> cells : rows) {
							                // print all column-cells of the row plus linefeed
							                for (int k=0;k<cells.size();k++ ) {
							                    // Note: Cell.getText() uses \r to concat text chunks
							                	RectangularTextContainer content=cells.get(k);
							                    String text = content.getText().replace("\r", " ");
							                    System.out.print(text + "|");
							                    
							                    if(k<(cells.size()-1)) {
							                    	sb.append(text+"\t");
							                    }else {
							                    	sb.append(text);
							                    }
							                }
							                System.out.println();
							                sb.append("\n");
							            }
							            String targetfile=dirpath+"/"+f.getName().substring(0, f.getName().length()-4)+"-"+tid+".txt";
							            System.out.println(targetfile);
							            FileUtil.write2File(targetfile, sb.toString());
							            tid++;
							        }
							    }
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean deleteFile(File dirFile) {
	    // 如果dir对应的文件不存在，则退出
	    if (!dirFile.exists()) {
	        return false;
	    }

	    if (dirFile.isFile()) {
	        return dirFile.delete();
	    } else {

	        for (File file : dirFile.listFiles()) {
	            deleteFile(file);
	        }
	    }

	    return dirFile.delete();
	}
	
	public void extractTable(String path) throws IOException {
		//InputStream in = this.getClass().getResourceAsStream(path);
		
		File f = new File(path);
	    InputStream in =new FileInputStream(f);
		try (PDDocument document = PDDocument.load(in)) {
		    SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
		    PageIterator pi = new ObjectExtractor(document).extract();
		    while (pi.hasNext()) {
		        // iterate over the pages of the document
		        Page page = pi.next();
		        List<Table> table = sea.extract(page);
		        // iterate over the tables of the page
		        System.out.println("-------------");
		        for(Table tables: table) {
		            List<List<RectangularTextContainer>> rows = tables.getRows();
		            // iterate over the rows of the table
		            for (List<RectangularTextContainer> cells : rows) {
		                // print all column-cells of the row plus linefeed
		                for (RectangularTextContainer content : cells) {
		                    // Note: Cell.getText() uses \r to concat text chunks
		                    String text = content.getText().replace("\r", " ");
		                    System.out.print(text + "|");
		                }
		                System.out.println();
		            }
		        }
		    }
		}
	}

}
