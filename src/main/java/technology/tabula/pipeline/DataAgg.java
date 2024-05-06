package technology.tabula.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataAgg {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String content=FileUtil.readFile("/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/allinone.txt");
		System.out.println(content);
		String[] rows=content.split("\n");
		HashMap<String,Set<String>> hmap=new HashMap<String,Set<String>>();
		for(String r:rows) {
			String[] en=r.split("\t");
			if(en.length>3) {
				System.out.println(en[1]+"\t->\t"+en[2]+"\t->\t"+en[3]+"\t->\t");
				if(hmap.containsKey(en[1])) {
					Set<String> paperset=hmap.get(en[1]);
					paperset.add(en[0]);
					hmap.put(en[1], paperset);
				}else {
					Set<String> paperset=new HashSet<String>();
					paperset.add(en[0]);
					hmap.put(en[1], paperset);
				}
			}
		}
		
		HashMap<String,Integer> cmap=new HashMap<String,Integer>();
		for (Map.Entry<String,Set<String>> entry : hmap.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			Set<String> set=entry.getValue();
			cmap.put(entry.getKey(), set.size());
		}

		
		
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(cmap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Integer> mapping : list) {
			//System.out.println(mapping.getKey() + ":" + mapping.getValue());
			sb.append(mapping.getKey() + "\t" + mapping.getValue() + "\n");
		}
		FileUtil.write2File("/Users/cy2465/Documents/row_count.txt", sb.toString());
	}

}
