import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 */

/**
 * @author Handan
 *
 */
public class Rank {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int size = 183811;
		double d = 0.85;

		HashMap<String, Page> P = new HashMap<String, Page>(); //Pages
		HashMap<String, Page> S = new HashMap<String, Page>(); //Sink Pages
		HashMap<String,Double> probs = new HashMap<String,Double>(); //Page ranks
		
		FileReader sites = new FileReader("wt2g_inlinks.txt");
		BufferedReader reader = new BufferedReader(sites);
		String name = "";
		double rank = 1 / (double) size; //initial rank for each page
		
		//reads wt2g_inlinks.txt and creates pages with their input and output links.
		//It gives initial rank point to each page
		while((name = reader.readLine())!= null){
			String[] line = name.split(" ");
			if(!probs.containsKey(line[0]))
				probs.put(line[0], rank);
			if(!P.containsKey(line[0])){
				Page p = new Page(line[0]);
				p.setRank(rank);
				for(int i = 1; i < line.length; i++){					
					Page pIn = new Page(line[i]);
					p.getInlinks().add(pIn);
					if(!P.containsKey(line[i])){
						pIn.getOutlinks().add(p);
						probs.put(line[i], rank);
						pIn.setRank(rank);
						if(!line[0].equals(line[i]))							
							P.put(line[i], pIn);
						else{
							p.getOutlinks().add(pIn);
						}

					}else{
						P.get(line[i]).getOutlinks().add(p);
					}						
				}
				P.put(p.getName(), p);
			}else{
				for(int i = 1; i < line.length; i++){					
					Page pIn = new Page(line[i]);
					P.get(line[0]).getInlinks().add(pIn);
					if(!P.containsKey(line[i])){
						pIn.getOutlinks().add(P.get(line[0]));
						pIn.setRank(rank);
						probs.put(line[i], rank);
						if(!line[0].equals(line[i]))							
							P.put(line[i], pIn);
						else{
							P.get(line[0]).getOutlinks().add(pIn);
						}			
					}else{
						P.get(line[i]).getOutlinks().add(P.get(line[0]));
					}						
				}
			}			
		}
		System.out.println(probs.size());
		Iterator<String> keySetIterator = P.keySet().iterator();

		//		FileWriter result1 = new FileWriter("results1.txt");			
		//		BufferedWriter w1 = new BufferedWriter(result1);	
		//		FileWriter result10 = new FileWriter("results10.txt");			
		//		BufferedWriter w10 = new BufferedWriter(result10);	
		//		FileWriter result100 = new FileWriter("results100.txt");			
		//		BufferedWriter w100 = new BufferedWriter(result100);	
		FileWriter perp = new FileWriter("Perp.txt");			
		BufferedWriter wPerp = new BufferedWriter(perp);	

		while(keySetIterator.hasNext()){
			String key = keySetIterator.next();
			if(P.get(key).getOutlinks().size() == 0)
				S.put(key, P.get(key));
		}
		int counter = 1;
		Double perplexity = 0.0;
		wPerp.write("0. \t"+calculatePerplexity(probs)); //first perplexity value
		wPerp.newLine();
		int iteration = 0;
		
		//Pseudo code in the Project description
		while(counter < 3){
			iteration++;
			double sinkPr = 0;
			Iterator<String> i = S.keySet().iterator();
			while(i.hasNext()){
				String key = i.next();
				sinkPr += S.get(key).getRank();
			}
			Iterator<String> j = P.keySet().iterator();
			while(j.hasNext()){
				String pagekey = j.next();
				double newPr = (1 - d) / (double) size;
				newPr += d * sinkPr / (double) size;
				for(int k = 0; k < P.get(pagekey).getInlinks().size(); k++){
					newPr += (d * P.get(P.get(pagekey).getInlinks().get(k).getName()).getRank())
							/P.get(P.get(pagekey).getInlinks().get(k).getName()).getOutlinks().size();
				}
				P.get(pagekey).setNewPR(newPr);					
			}
			Iterator<String> t = P.keySet().iterator();
			while(t.hasNext()){
				String key = t.next();
				P.get(key).setRank(P.get(key).getNewPR());
				probs.put(key, P.get(key).getNewPR());
				//				if(iteraration == 1){
				//					w1.write(key+"\t"+P.get(key).getRank());
				//					w1.newLine();
				//				}else if(iteraration == 10){
				//					w10.write(key+"\t"+P.get(key).getRank());
				//					w10.newLine();
				//				}else if(iteraration == 100){
				//					w100.write(key+"\t"+P.get(key).getRank());
				//					w100.newLine();
				//				}
			}
			Double tempPerplexity = calculatePerplexity(probs);
			wPerp.write(iteration+". \t"+tempPerplexity);
			wPerp.newLine();
			if(tempPerplexity.intValue() == perplexity.intValue())
				counter++;
			else if((counter > 0) && tempPerplexity != perplexity)
				counter = 0;

			perplexity = tempPerplexity;
			System.out.println(perplexity);
		}
		ArrayList<Page> pageList = new ArrayList<Page>();
		Iterator<String> j = P.keySet().iterator();
		while(j.hasNext()){
			String key = j.next();
			pageList.add(P.get(key));
		}
		
		//		w1.close();
		//		w10.close();
		//		w100.close();
		wPerp.close();
		sort(pageList);
	}

	public static double calculatePerplexity(HashMap<String,Double> probs){
		double entropy = 0;
		Iterator<String> keySetIterator = probs.keySet().iterator();
		while(keySetIterator.hasNext()){
			String key = keySetIterator.next();
			entropy += (probs.get(key)* (Math.log(probs.get(key)) / Math.log(2.0)))*(-1.0);
		}
		return Math.pow(2.0, entropy);
	}
	
	public static void sort(ArrayList<Page> pages) throws IOException {
		ArrayList<Page> page10 = new ArrayList<Page>();
		for(int i = 0; i < 10; i++)
			page10.add(pages.get(i));
		page10 = insertionSort(page10);
		for(int i = 10; i < pages.size(); i++){
			for(int j = 0; j < 10; j++){
				if(page10.get(j).getRank() < pages.get(i).getRank()){
					page10.add(j, pages.get(i));		
					break;
				}
			}
		}
		//writes first 10 pages having highest values
		FileWriter rank = new FileWriter("ranks.txt");			
		BufferedWriter wRank = new BufferedWriter(rank);
		for(int i = 0; i < 10; i++){
			wRank.write(page10.get(i).getName()+" \t"+page10.get(i).getRank());
			wRank.newLine();
		}
		wRank.close();
	}

	public static ArrayList<Page> insertionSort(ArrayList<Page>  pageList10) {
		int n = pageList10.size();
		//insertion sort
		for (int i = 1; i < n; i++){
			int j = i;
			Page B = pageList10.get(i);			
			while ((j > 0) && (pageList10.get(j-1).getRank() > B.getRank())){
				pageList10.add(j, pageList10.get(j-1));
				pageList10.remove(j+1);
				j--;
			}

			pageList10.add(j, B);
			pageList10.remove(j+1);
		}
		ArrayList<Page> tempList = new ArrayList<Page>();
		int j = n - 1;
		while(j >= 0){
			tempList.add(pageList10.get(j));
			j--;
		}
		pageList10 = tempList;
		return pageList10;
	}
}
