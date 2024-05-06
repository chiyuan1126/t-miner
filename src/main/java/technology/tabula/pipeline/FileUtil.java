package technology.tabula.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String content=readFile("/Users/cy2465/Downloads/qimo_name.txt");
//		System.out.println(content);
//		write2File("/Users/cy2465/Downloads/qimo_name111.txt","123");
		
		String dir1="/Volumes/BackUp/tempbackup/";
		File tdir=new File(dir1);	
		File[] files=tdir.listFiles();
		
		String dir2="/Volumes/BackUp/tmp/";
		File tdir2=new File(dir2);
		File[] files2=tdir2.listFiles();
		
		String dir3="/Volumes/BackUp/oldback/tempbackup/";
		File tdir3=new File(dir3);
		File[] files3=tdir3.listFiles();
		
		String dir4="/Volumes/FilesBK/videos/";
		File tdir4=new File(dir4);
		File[] files4=tdir4.listFiles();
		
		
		
		List<String> list=new ArrayList<String>();
		
		List list1 = Arrays.asList(files);
		List list2 = Arrays.asList(files2);
		List list3 = Arrays.asList(files3);
		List list4 = Arrays.asList(files4);
		
		List<File> allfiles=new ArrayList<File>();
		
		allfiles.addAll(list1);
		allfiles.addAll(list2);
		allfiles.addAll(list3);
		allfiles.addAll(list4);	
		
		int count=0;
		StringBuffer sb=new StringBuffer();
		
		for(File f:allfiles) {
			System.out.println(f.getAbsolutePath());
			String name=f.getAbsolutePath();
			int end=name.indexOf("-C");
			int start=name.lastIndexOf("/");
			if(end!=-1) {
				String code=name.substring(start+1,end).toLowerCase();
				System.out.println("code="+code);
				sb.append(code+"\t"+name+"\n");
			}
		}
		write2File("/Users/cy2465/Downloads/allfiles.txt",sb.toString());
		System.out.println("count :"+count);
		
		
		
		
	}
	
	public static String readFile(String path){
		try {

			StringBuffer readsb = new StringBuffer();
			InputStream in = new FileInputStream(new File(path));
			ArrayList<String> alist = new ArrayList<String>();

			int count;
			byte[] b = new byte[1024 * 1024];
			while ((count = in.read(b)) != -1) {
				if (count != b.length) {
					byte[] t = new byte[count];
					for (int i = 0; i < count; ++i)
						t[i] = b[i];
					readsb.append(new String(t));
				} else
					readsb.append(new String(b));
			}
			in.close();
			return readsb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static int write2File(String fileName, String content) {
		File file = new File(fileName);
		try {
			// if the file is not exist, create it!
			if (file.exists() == false) {
				file.createNewFile();

			}
			// the second parameter is 'true' means add contents at the end of
			// the file
			FileWriter writer = new FileWriter(fileName);
			writer.write(content);
			writer.close();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}

}
