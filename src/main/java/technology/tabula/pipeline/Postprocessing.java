package technology.tabula.pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Postprocessing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir=new File("/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/tabledata");///Users/cy2465/Documents/projects/2022_Tabular_Info/toannimgre3/tabledata
		String evaldir="/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/updatedata/";
		StringBuffer sb=new StringBuffer();
		StringBuffer allinonesb=new StringBuffer();
		for(File f:dir.listFiles()) {
			System.out.println(f.getName());
			if(f.getName().endsWith(".txt")==false) {
				continue;
			}
			String content=FileUtil.readFile(f.getAbsolutePath());
			System.out.println("--------------------------------");
			System.out.println(content);
			String[][] mdata=new String[100][100];
			int[][] flagm=new int[100][100];
			String[] rows=content.split("\n");
			if(content.length()==0) {
				continue;
			}
			Set<Integer> merged=new HashSet<Integer>();
			List<List<String>> alist=new ArrayList<List<String>>();
			int maxh=0;
			int maxv=0;
			for(String r:rows) {
				System.out.println("r="+r);
				String[] en=r.split("\t");
				System.out.println(en.length);
				if(en.length==1) {
					continue;
				}
				int h=Integer.valueOf(en[0]);
				int v=Integer.valueOf(en[1]);
				if(h>maxh) {
					maxh=h;
				}
				if(v>maxv) {
					maxv=v;
				}
				String celltext="";
				if(en.length>2) {
					celltext=en[2];
					mdata[h][v]=celltext;
				}else if(en.length==2) {
					mdata[h][v]=celltext;
				}
				int countlen=0;
				for(int a=0;a<celltext.length();a++) {			
					if(Character.isDigit(celltext.charAt(a))||celltext.charAt(a)=='-'||celltext.charAt(a)=='.'||celltext.charAt(a)==' ') {
						countlen++;
					}
				}
				if(countlen==celltext.length() && celltext.contains(" ")) {
					System.out.println("MERGED:"+celltext);
					merged.add(v);
					flagm[h][v]=1;
				}
				if(h>0 && v>0) {
					System.out.println("--------------->"+mdata[h][0]+"|"+mdata[0][v]+"|"+celltext);
				}
			}
			for(int ki:merged) {
				System.out.println("merged pos:"+ki);
			}
			for(Integer a=0;a<100;a++) {
				if(merged.contains(a-1)) {
					System.out.println("------->!!!!"+(a-1));
					for(int b=0;b<100;b++) {
						if(flagm[b][a-1]>0 &&flagm[b+1][a-1]>0 ) {
							System.out.println(b+"\t"+(a-1)+"\t:"+flagm[b][a-1]);
							int tp=b-1;
							while(tp>=0) {
								if(mdata[tp][a-1].contains(" ")&&mdata[tp][a-1].split(" ").length==mdata[b][a-1].split(" ").length) {
									System.out.println(tp+"\t"+(a-1)+"\t:"+mdata[tp][a-1]);
									flagm[tp][a-1]=1;	
								}
								tp--;
							}
						}
					}
				}		
			}
			System.out.println("max h="+maxh);
			System.out.println("max v="+maxv);
			List<List<String>> ll=new ArrayList<List<String>>();
			for(int hi=0;hi<=maxh;hi++) {
				System.out.print("--->\t");
				List<String> hline=new ArrayList<String>();
				for(int vi=0;vi<=maxv;vi++) {
					System.out.print(mdata[hi][vi]+"("+flagm[hi][vi]+")"+"\t");
					if(flagm[hi][vi]==0) {
						hline.add(mdata[hi][vi]);
					}else {
						String enb=mdata[hi][vi];
						String[] enbarr=enb.split(" ");
						for(String en:enbarr) {
							hline.add(en);
						}
					}
				}
				ll.add(hline);
				System.out.println();
			}
			StringBuffer evalsb=new StringBuffer();
			System.out.println("--------UPDATE TABLE-------");
			for(List<String> al:ll) {
				System.out.print("--->\t");
				for(int k=0;k<al.size();k++) {
					String bl=al.get(k);
					System.out.print(bl+"\t");
					if(k<al.size()-1) {
						evalsb.append(bl+"\t");
					}else {
						evalsb.append(bl);
					}
				}
				evalsb.append("\n");
				System.out.println();
			}
			System.out.println("--------LAST TABLE ROW-------");
			List<String> lastrow=ll.get(ll.size()-1);
			
			for(int p=lastrow.size()-1;p>=0;p--) {
				System.out.print(lastrow.get(p)+"\t");
			}
			System.out.println();
			System.out.println("--------LAST TABLE LEN-------"+lastrow.get(0).length());
			int hnum=1;
			if(lastrow.get(0).length()==0) {
				hnum=2;
			}
			System.out.println("--------H TITLE NUM-------"+hnum);
			
			int inoneblank=0;
			for(String ire:lastrow) {
				if(ire.length()>0) {
					inoneblank++;
				}
			}
			int last=inoneblank;
			int vnum=0;
			for(int q=ll.size()-1;q>=0;q--) {      
				List<String> rlist=ll.get(q);
				int noneblank=0;
				for(String re:rlist) {
					if(re.length()>0) {
						noneblank++;
					}
				}
				System.out.println(q+":"+noneblank);
				if(Math.abs(last-noneblank) >= hnum && noneblank!=1) {
					vnum=q;
					break;
				}
				last=noneblank;
				
				
			}
			System.out.println("--------V TITLE NUM-------"+(vnum));
			
			System.out.println("--------NEAREST ROW TITLE-------");
			for(String lr:ll.get(vnum)) {
				System.out.print(lr+"\t");
			}
			System.out.println();
			System.out.println("--------NEAREST ROW TITLE (Key-Value)-------");
			List<String> nrtl=ll.get(vnum);
			for(int ni=1;ni<nrtl.size();ni++) {
				System.out.println(nrtl.get(0)+"->"+nrtl.get(ni));
				sb.append(f.getName().substring(0, f.getName().length()-10)+"\t"+nrtl.get(0)+"\t"+nrtl.get(ni)+"\n");
			}
			System.out.println();
			
			
			System.out.println("--------NEAREST COLUMN TITLE-------");
			for(List<String> lr:ll) {
				System.out.println(lr.get(hnum-1));
			}
			
			System.out.println("--------NEAREST COLUMN TITLE (Key-Value)-------");
			List<List<String>> nctl=ll;
			for(int nc=1;nc<nctl.size();nc++) {
				System.out.println(nctl.get(0).get(hnum-1)+"->"+nctl.get(nc).get(hnum-1));
				sb.append(f.getName().substring(0, f.getName().length()-10)+"\t"+nctl.get(0).get(hnum-1)+"\t"+nctl.get(nc).get(hnum-1)+"\n");
			}
			System.out.println();
			
			
			System.out.println("--------DATA AREA-------");
			for(int p=vnum+1;p<ll.size();p++) {
				for(int q=hnum;q<ll.get(p).size();q++) {
					System.out.print(ll.get(p).get(q)+"\t");
					
				}
				System.out.println();
			}
			
			System.out.println("--------DATA WITH KEY-------");
			for(int p=vnum+1;p<ll.size();p++) {
				for(int q=hnum;q<ll.get(p).size();q++) {
					if((hnum-1 < ll.get(p).size())&& (q< (ll.get(vnum).size()))) {
						System.out.println(ll.get(p).get(hnum-1)+","+ll.get(vnum).get(q)+"\t->\t"+ll.get(p).get(q)+"\t");
						allinonesb.append(f.getName().substring(0, f.getName().length()-10)+"\t"+ll.get(p).get(hnum-1)+"\t"+ll.get(vnum).get(q)+"\t"+ll.get(p).get(q)+"\n");
					}
					
				}
			}
			
			System.out.println();
			
			
			System.out.println("--------EVALUATE DATA-------");
			
			for(List<String> al:ll) {
				for(int i=0;i<al.size()-1;i++) {
					String bl=al.get(i);
					//System.out.print(bl);
					String nbl=al.get(i+1);
					if(bl.length()!=0 && nbl.length()!=0) {
						//System.out.print("(blank)");
						System.out.println(bl+"->"+nbl);
						//evalsb.append(bl+"\t"+nbl+"\n");
					}
					
				}
				//System.out.println();
			}
			System.out.println(f.getAbsolutePath());
			
			System.out.println("-----evalsb-----");
			System.out.println(evalsb.toString());
			//FileUtil.write2File(evaldir+f.getName(), evalsb.toString());
		}
		//FileUtil.write2File("/Users/cy2465/Documents/allmatrix.txt", sb.toString());
		//FileUtil.write2File("/Users/cy2465/Documents/projects/2022_Tabular_Info/usecase/allinone.txt", allinonesb.toString());
	}
}
