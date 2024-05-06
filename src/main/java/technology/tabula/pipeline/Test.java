package technology.tabula.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File dir=new File("/Users/cy2465/Downloads/usecasefileimg/");
		File[] fs=dir.listFiles();
		for(File f:fs) {
			if(f.isDirectory()) {
				File[] sfs=f.listFiles();
				for(File fff:sfs) {
					copyFileUsingFileStreams(fff,new File("/Users/cy2465/Downloads/toannimg/"+fff.getParentFile().getName()+"-"+fff.getName()));
					//System.out.println(fff.getParentFile().getName()+"-"+fff.getName());
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

}
