package technology.tabula.pipeline;

import java.io.File;

public class EvalTableInterpret {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		File dir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testingfiles");
		File[] files=dir.listFiles();
		for(File f:files) {
			System.out.println(f.getAbsolutePath());
		}
		System.out.println(files.length);
		File dir2=new File("/Users/cy2465/Downloads/goldentxt");
		File[] dirs2=dir2.listFiles();
		System.out.println(dirs2.length);
		*/
		
		/*
		String content=FileUtil.readFile("/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre3/tables/2021.findings-emnlp.204.pdf-0.txt");
		System.out.println(content);
		String tocsv=content.replace("\t", ",");
		System.out.println(tocsv);
		*/
		
		File tabledir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre3/tables/");
		File[] files=tabledir.listFiles();
		for(File f:files) {
			System.out.println(f.getName());
			String tcontent=FileUtil.readFile(f.getAbsolutePath());
			System.out.println(tcontent);
			String tocsv=tcontent.replace("\t", ",");
			System.out.println(tocsv);
		}
		
		
	}

}
