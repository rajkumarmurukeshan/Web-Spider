package webcrawlers.assignment1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FocusedCrawling {

	public final static int maxDepth = 5;
	public static int pagesCrawled = 0; 
	public static FileWriter output;
	public static ArrayList<String> pagesTraversed = new ArrayList<String>();
	public static ArrayList<String> links = new ArrayList<String>();
	public static Queue<String> queue = new LinkedList<String>() ;
	public static String seed;
	public static String searchKey;
	private static Scanner s;


	public static void main(String[] args) {
		s = new Scanner(System.in);
		System.out.println("Enter the seed :");
		seed = s.nextLine();
		System.out.println("Enter the search string :");
		searchKey = s.nextLine().toLowerCase();
		
		System.out.println("BFS Web Crawler is initiated......");
		focusedBFS(seed);
		
		System.out.println("DFS Web Crawler is initiated......");
		focusedDFS(seed);
	}

	public static void focusedBFS(String seed){			
		// Clearing the Collections before the Crawler is initiated
		pagesTraversed.clear();
		links.clear();
		queue.clear();
		pagesCrawled = 0;
		
		// Seed is registered to the output
		registerURL(seed);
		
		// HyperLinks available in the seed are added to the Queue
		addToQueue(seed);
		
		// Elements in the queue are iterated until the queue is empty or the pages registered reaches 1000
		// If the element being iterated is valid, i.e. if it contains the string "solar", it is being registered in the output ArrayList
		while(!queue.isEmpty() && pagesCrawled < 1000){
			String url = queue.remove();
			if (isValidURL(url)){
				registerURL(url);
				addToQueue(url);
			}
			else {
				pagesTraversed.add(url);
				addToQueue(url);
			}		
		}
		
		// This block writes the registered 1000 links to the output file
		try {
			int c = 1;
			output = new FileWriter("FocusedBFSCrawling.txt");
			System.out.println("File is opened");
			for (String s : links){
				output.write(c + "." + s + "\n");
				c++;
			}
			System.out.println("BFS Crawling is performed and file is written with " + links.size() + " unique URLs");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @param url : The URL from where the hyperLinks are fetched and added to the queue
	// @returns : void
	// This function connects to the given URL and fetches all the hyperLinks present in 
	// the URL and adds it to the queue
	public static void addToQueue(String url){
		try {
			Document doc = Jsoup.connect(url).get();
			Thread.sleep(1000);
			Elements ele = doc.select("a[href]");
			for (Element e : ele){
				String nextURL = e.attr("abs:href");
				nextURL = nextURL.split("#")[0];
				if ((nextURL.lastIndexOf(":") < 6 ) 
						&& nextURL.startsWith("https://en.wikipedia.org/wiki")
						&& !pagesTraversed.contains(nextURL)){
					queue.add(nextURL);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	// @param url : The URL that has to be registered to the final ArrayList
	// @returns : Void
	// This function registers the given url to the final ArrayList, increments the pagesCrawled 
	// and add the URL to the pages traversed list
	public static void registerURL(String url){
		links.add(url);
		pagesCrawled++;
		pagesTraversed.add(url);
	}

	// @param url : The URL that has be checked
	// @returns : true if the the given URL is valid, i.e. it contains the search String.
	//            else, false.
	public static boolean isValidURL(String URL){
		boolean result = false;		
		try {
			Document doc = Jsoup.connect(URL).get();
			if((URL.toLowerCase().contains(searchKey))
					|| doc.title().toLowerCase().contains(searchKey)
					|| doc.body().text().toLowerCase().contains(searchKey))
				result = true;
			else 
				result = false;	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public static void focusedDFS(String seed){
		// Clearing the Collections before the Crawler is initiated
		pagesTraversed.clear();
		links.clear();
		pagesCrawled = 0;
		
		proceedDFSCrawl(seed,1);
		
		try {
			int c = 1;
			output = new FileWriter("FocusedDFSCrawling.txt");
			for (String s : links){
				output.write(c + "." + s + "\n");
				c++;
			}
			System.out.println("DFS Crawling is performed and file is written with " + links.size() + " unique URLs");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void proceedDFSCrawl(String url, int depth){
		if (pagesCrawled < 1000 && !pagesTraversed.contains(url)){
			if(isValidURL(url)){
				registerURL(url);
			}
			else {
				pagesTraversed.add(url);
			}			
			processHyperLinks(url,depth);
		}
		else return;
	}

	public static void processHyperLinks(String url, int depth){
		if(depth < maxDepth){
			try{
				Document doc = Jsoup.connect(url).timeout(0).get();
				Thread.sleep(1000);
				Elements ele = doc.select("a[href]");
				for (Element e : ele){
					String nextURL = e.attr("abs:href");
					nextURL = nextURL.split("#")[0];
					if ((nextURL.lastIndexOf(":") < 6 ) && nextURL.startsWith("https://en.wikipedia.org/wiki")){
						proceedDFSCrawl(nextURL, depth+1);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}


}
