package webcrawlers.assignment1;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UnfocusedCrawling {


	public static String seed = "https://en.wikipedia.org/wiki/Sustainable_energy";
	public final static int maxDepth = 5;
	public static int pagesCrawled; 
	public static FileWriter output;
	public static ArrayList<String> frontier = new ArrayList<String>();

	public static void main(String[] args) {
		unfocusedCrawling();
	}

	public static void unfocusedCrawling(){
		
		// Clearing the Collections before the Crawler is initiated
		pagesCrawled = 0;
		int c = 1;
		frontier.clear();
		
		// Seed is registered to the output
		registerURL(seed,1);
		
		// This block writes the URLs registered to the output file
		try {
			output = new FileWriter("UnfocusedCrawling.txt");
			for (String s : frontier){
				output.write(c + "." + s + "\n");
				c++;
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// This block downloads the content(raw HTML, in text) for the 1000 URLs
		try {
			output = new FileWriter("UnfocusedData.txt");			
			String inputLine;
			int cnt = 1;
	        for (String s : frontier){
	        	output.write(cnt + "." + s + "\n");
	        	URL file = new URL(s);
	        	BufferedReader input = new BufferedReader(new InputStreamReader(file.openStream()));
	        	while ((inputLine = input.readLine()) != null)
		            output.write(inputLine+"\n");
		        input.close();	
		        cnt++;
	        }
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		frontier.clear();
	}

	
	public static void registerURL(String url, int depth){
		if(pagesCrawled < 1000 && !frontier.contains(url)){
			frontier.add(url);
			pagesCrawled++;
			processHyperLinks(url,depth);
		}
		else return;

	}

	public static void processHyperLinks(String url, int depth){
		if (depth < maxDepth){
			try {
				Document doc = Jsoup.connect(url).get();
				Thread.sleep(1000);
				Elements links = doc.select("a[href]");
				for (Element link : links){
					String nextURL = link.attr("abs:href");
					nextURL = nextURL.split("#")[0];
					if ((nextURL.lastIndexOf(":") < 6 ) && nextURL.startsWith("https://en.wikipedia.org/wiki")){
						registerURL(nextURL,depth+1);  // recursive call 
					}					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else return;
	}
}
