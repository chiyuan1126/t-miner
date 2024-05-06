package technology.tabula.pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class PDFSpliter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File fdir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testingfiles/");
		File[] files=fdir.listFiles();
		for(File f:files) {
			System.out.println(f.getName());
			String target=f.getName().substring(0, f.getName().length()-4);
			System.out.println(target);
			String dirpath="/Users/cy2465/Documents/datasets/testingfiles/"+target+"/";
			File file = new File(dirpath);
			if (!file.exists()) {
				// 如果目标文件所在的目录不存在，则创建父目录
				// System.out.println("目标文件所在目录不存在，准备创建它！");
				if (!file.mkdirs()) {
					System.out.println("创建目标文件所在目录失败！");
				}
			}
			try {
		        splitPDFOneByOne("/Users/cy2465/Documents/projects/2022_Tabular_Info/testingfiles/", f.getName(),dirpath );
		    } catch (Exception e) {
		       
		    }
		    
		}
		/*
		try {
	        splitPDFOneByOne("/Users/cy2465/Documents/datasets/", "W15-1506.pdf", "/Users/cy2465/Documents/splitpdf/");
	    } catch (Exception e) {
	       
	    }
	    */
	}
	
	public static void splitPDFOneByOne(String path, String fileName, String outputPath) {
	    String sep = java.io.File.separator;
	    PdfReader reader = null;
	    int numberOfPages = 0;
	    try {
	        reader = new PdfReader(path + sep + fileName);
	        numberOfPages = reader.getNumberOfPages();
	        for (int i = 1; i <= numberOfPages; i++) {
	            Document document = null;
	            PdfCopy copy = null;
	            try {
	                document = new Document(reader.getPageSize(1));
	                String savePath = outputPath + sep +
	                        fileName.substring(0, fileName.lastIndexOf(".")) + "_" + i + ".pdf";
	                copy = new PdfCopy(document, new FileOutputStream(savePath));
	                document.open();
	                document.newPage();
	                PdfImportedPage page = copy.getImportedPage(reader, i);
	                copy.addPage(page);
	            } finally {
	                if (document != null)
	                    document.close();
	                if (copy != null)
	                    copy.close();
	            }
	        }
	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	    } catch (DocumentException e) {
	    	System.out.println(e.getMessage());
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}

}
