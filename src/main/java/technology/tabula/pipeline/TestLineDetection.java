package technology.tabula.pipeline;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TestLineDetection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testingfiles");
		File[] fs=dir.listFiles();
		int count=0;
		for(File f:fs) {
			if(f.getName().endsWith(".pdf")) {
				count++;
			}
		}
		System.out.println(count);
		
		
		File direxcel=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/testing2excel");
		File[] exs=direxcel.listFiles();
		Set<String> set=new HashSet<String>();
		for(File e:exs) {
			if(e.getName().endsWith(".xlsx")) {
				int k=e.getName().indexOf(".pdf");
				String prefix=e.getName().substring(0,k);
				System.out.println(prefix);
				set.add(prefix);
			}
		}
		System.out.println(set.size());
		
	}

}
