package technology.tabula.pipeline;

import java.io.File;

public class TablePostprocessing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String table_dir="/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre2/tables/";
		File dir=new File(table_dir);
		File[] files=dir.listFiles();
		for(File f:files) {
			System.out.println("File:"+f.getAbsolutePath());
			String tablestr=FileUtil.readFile(f.getAbsolutePath());
			System.out.println(tablestr);
			String[] trows=tablestr.split("\n");
			System.out.println("REVISED-------");
			for(String tr:trows) {
				if(tr.startsWith("Table ")) {
					continue;
				}else {
					System.out.println(tr);
					String[] en=tr.split("\t");
					System.out.println(en.length);
					if(en.length>0) {
						System.out.println("first one:"+en[0]);
						System.out.println("first one len:"+en[0].length());
					}
				}
			}
		}
	}
}
