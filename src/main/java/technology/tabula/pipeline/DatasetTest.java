package technology.tabula.pipeline;

import java.io.File;

public class DatasetTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File xmldir=new File("/Users/cy2465/Documents/dataset/papertable-onlytables/labels");
		File[] files=xmldir.listFiles();
		int tcount=0;
		for(File f:files) {
			if(f.getName().endsWith(".xml")) {
				String s=FileUtil.readFile(f.getAbsolutePath());
				String str="<name>table</name>";
				int i = s.length() - s.replace(str, "").length();
				System.out.println("方法一 ---> 个数" + i / str.length());
				tcount=tcount +( i / str.length());
			}
		}
		System.out.println(tcount);
	}

}
