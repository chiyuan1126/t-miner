package technology.tabula.pipeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DataAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		///Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre/aggdata/allinone.txt
		String content=FileUtil.readFile("/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/allinone.txt");//
		String[] rows=content.split("\n");
		Map<String, Set<String>> map=new HashMap<String, Set<String>>();
		for(String r:rows) {
			String[] en=r.split("\t");
			if(en.length==4) {
				//System.out.println(r);
				//Set<String> map.=map.get(en[1]);
				if(map.containsKey(en[1])) {
					Set<String> pset=map.get(en[1]);
					pset.add(en[0]);
					map.put(en[1], pset);
				}else {
					Set<String> pset=new HashSet<String>();
					pset.add(en[0]);
					map.put(en[1], pset);
				}
			}
		}
		StringBuffer sb=new StringBuffer();
		Iterator<Map.Entry<String, Set<String>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            System.out.println(entry.getKey()+"\t"+entry.getValue().size());
            sb.append(entry.getKey()+"\t"+entry.getValue().size()+"\n");
            //System.out.println(entry.getValue());
        }
        FileUtil.write2File("/Users/cy2465/Documents/teNEW.txt", sb.toString());
	}

}
