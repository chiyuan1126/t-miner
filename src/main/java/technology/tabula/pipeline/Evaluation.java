package technology.tabula.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Evaluation {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//xlstxtComp();
		//fileOrg();
		
		System.out.println("------------XLS-------------->");
		File myFile = new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testing2excel/2021.naacl-main.452.pdf-4.xlsx");
		FileInputStream fis = new FileInputStream(myFile);
		// Finds the workbook instance for XLSX file
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		// Return first sheet from the XLSX workbook
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);
		// Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();
		//Traversing over each row of XLSX file
		List<String> list=new ArrayList<String>();
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
		
			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				//System.out.println("--->"+cell.getCellType());
				if (cell.getCellType() == CellType.STRING) {
					System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getStringCellValue() + "\t");
					list.add(cell.getStringCellValue());
				} else if (cell.getCellType() == CellType.NUMERIC) {
					System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getNumericCellValue() + "\t");
					list.add(String.valueOf(cell.getNumericCellValue()));
				} else if (cell.getCellType() == CellType.BOOLEAN) {
					System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getBooleanCellValue() + "\t");
					list.add(String.valueOf(cell.getBooleanCellValue()));
				}
			}
			System.out.println("");
		}
		XSSFRow xr=mySheet.getRow(0);
		System.out.println("0:"+xr.getCell(0));
		System.out.println("1:"+xr.getCell(1));
		System.out.println("2:"+xr.getCell(2));
		System.out.println("3:"+xr.getCell(3));
		System.out.println("4:"+xr.getCell(4));
		System.out.println("5:"+xr.getCell(5));
		//System.out.println("------------XLS---END---------->"+fk.getName());
	}

	private static void fileOrg() throws IOException {
		File dir = new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testing2excel/");
		File predic=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/tables_ad/");
		for (File fk : dir.listFiles()) {
			if (fk.getName().endsWith(".xlsx") == false) {
				continue;
			}
			System.out.println("------------XLS---END---------->"+fk.getName());
			
			int pos=fk.getName().indexOf(".pdf");
			String prefix=fk.getName().substring(0,pos);
			System.out.println(fk.getName()+"--------------------------------->"+prefix);
			String todir="/Users/cy2465/Documents/projects/2022_Tabular_Info/testdataset/"+prefix+"/predict";
			File directory=new File(todir);
			directory.mkdir();
			String todir2="/Users/cy2465/Documents/projects/2022_Tabular_Info/testdataset/"+prefix+"/gold";
			File directory2=new File(todir2);
			directory2.mkdir();
			String todirxls=todir2+"/"+fk.getName();
			File txlsf=new File(todirxls);
			copyFileUsingFileStreams(fk,txlsf);
			for(File pref:predic.listFiles()) {
				if(pref.getName().startsWith(prefix)) {
					System.out.println(pref.getAbsolutePath());
					String target=todir+"/"+pref.getName();
					System.out.println(target);
					File sf=new File(pref.getAbsolutePath());
					File tf=new File(target);
					copyFileUsingFileStreams(sf,tf);
				}
			}
		}
	}
	
	public static void copyFileUsingFileStreams(File source, File dest)
	        throws IOException {    
	    InputStream input = null;    
	    OutputStream output = null;    
	    try {
	           input = new FileInputStream(source);
	           output = new FileOutputStream(dest);        
	           byte[] buf = new byte[1024];        
	           int bytesRead;        
	           while ((bytesRead = input.read(buf)) > 0) {
	               output.write(buf, 0, bytesRead);
	           }
	    } finally {
	        input.close();
	        output.close();
	    }
	}

	private static void xlstxtComp() throws FileNotFoundException, IOException {
		File predic=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/tables_ad/");
		File dir = new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testing2excel/");
		for (File fk : dir.listFiles()) {
			if (fk.getName().endsWith(".xlsx") == false) {
				continue;
			}
			System.out.println("------------XLS-------------->"+fk.getName());
			File myFile = new File(fk.getAbsolutePath());
			FileInputStream fis = new FileInputStream(myFile);
			// Finds the workbook instance for XLSX file
			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = mySheet.iterator();
			//Traversing over each row of XLSX file
			List<String> list=new ArrayList<String>();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					//System.out.println("--->"+cell.getCellType());
					if (cell.getCellType() == CellType.STRING) {
						System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getStringCellValue() + "\t");
						list.add(cell.getStringCellValue());
					} else if (cell.getCellType() == CellType.NUMERIC) {
						System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getNumericCellValue() + "\t");
						list.add(String.valueOf(cell.getNumericCellValue()));
					} else if (cell.getCellType() == CellType.BOOLEAN) {
						System.out.print("("+cell.getRowIndex()+","+cell.getColumnIndex()+")"+cell.getBooleanCellValue() + "\t");
						list.add(String.valueOf(cell.getBooleanCellValue()));
					}
				}
				System.out.println("");
			}
			
			
			System.out.println("------------XLS---END---------->"+fk.getName());
			int pos=fk.getName().indexOf(".pdf");
			String prefix=fk.getName().substring(0,pos);
			System.out.println(fk.getName()+"--------------------------------->"+prefix);
			for(File pref:predic.listFiles()) {
				if(pref.getName().startsWith(prefix)) {
					System.out.println("Correspond File:"+pref.getAbsolutePath());
					String precontent=FileUtil.readFile(pref.getAbsolutePath());
					List<String> prelist=new ArrayList<String>();
					String[] prerows=precontent.split("\n");
					for(int k=0;k<prerows.length;k++) {
						String pr=prerows[k];
						//System.out.println("->"+pr);
						String[] ele=pr.split("\t");
						for(int z=0;z<ele.length ;z++) {
							prelist.add(ele[z]);
							System.out.print("("+k+","+z+")"+ele[z]+"\t");
						}
						System.out.println();
					}
					System.out.println("==========ANSWER=============>"+list.size());
					List<String> alist=new ArrayList<String>();
					alist.addAll(list);
					alist.retainAll(prelist);
					System.out.println("==========OVERLAP============>"+alist.size());
					
				}
			}
		}
	}

}
