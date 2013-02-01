package ir.assignments.three;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	private static TreeMap<String, Integer> ht,tm;
	private static File file;
	private static PrintWriter pw;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		try {
			
			ht = new TreeMap<String, Integer>();
			tm = new TreeMap<String, Integer>();
			file = new File("trial.txt");
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)),true);
			
			// TODO Auto-generated method stub
			String crawlStorageFolder = "/data/crawl/root";
	        int numberOfCrawlers = 7;
	
	        CrawlConfig config = new CrawlConfig();
	        config.setCrawlStorageFolder(crawlStorageFolder);
	        config.setMaxDepthOfCrawling(4);
	        config.setPolitenessDelay(300);
	        config.toString();
	        //config.setMaxOutgoingLinksToFollow(15);
	
	        /*
	         * Instantiate the controller for this crawl.
	         */
	        PageFetcher pageFetcher = new PageFetcher(config);
	        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	        CrawlController controller;
			
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
			
	
	        /*
	         * For each crawl, you need to add some seed urls. These are the first
	         * URLs that are fetched and then the crawler starts following links
	         * which are found in these pages
	         */
			//controller.addSeed("http://www.vision.ics.uci.edu/");
	        controller.addSeed("http://www.ics.uci.edu/");
	        controller.addSeed("http://www.ics.uci.edu/~lopes/");
	
	        /*
	         * Start the crawl. This is a blocking operation, meaning that your code
	         * will reach the line after this only when crawling is finished.
	         */
	        
	        String userAgentString = "UCI IR crawler 85886028 60043295 ";
	        config.setUserAgentString(userAgentString);
	        
	        long startTime = System.currentTimeMillis();
	        controller.start(Crawler.class, numberOfCrawlers);
	        long stopTime = System.currentTimeMillis();
	        long elapsedTime = stopTime - startTime;
	        
	        System.out.println("Elapsed time to crawl: " + elapsedTime/1000/60 + " minutes");
	        
	        startTime = System.currentTimeMillis();
	        createTreeMapFromFile();
	        List<String> words = getFirstX("words",500);
	        List<String> twograms = getFirstX("twograms",20);
	        stopTime = System.currentTimeMillis();
	        
	        //System.out.println("words : " + words);
	        //System.out.println("twograms : " + twograms);
	        elapsedTime = stopTime - startTime;
	        
	        System.out.println("Elapsed time to generate two grams: " + elapsedTime/1000/60 + " minutes");
	        System.out.println("No of unique urls : " + Crawler.getnoOfURLS());
	        
		}
		finally {
			 //out.close();
			pw.close();
		}
	}
	
	public static void createTreeMapFromFile()
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File("trial.txt")));
			//PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File("content.txt"))),true);
			String S= " ";
			do{
			
				S=br.readLine();
				Matcher m=Pattern.compile("(.*)(\\bcontent:\\b)(.*)").matcher(S);
				while(m.find())
				{
					String S1=m.group(3).toString();
					//System.out.println("S1 : " + S1);
					if(!S1.trim().equals(""))
					{
						List<String> tokens = tokenizeString(S1);
						
						//System.out.println("tokens of S1: " + tokens);
						List<String> twograms =	computeTwoGrams(tokens);
						List<String> cleanedTokens = cleanTokens(tokens);
						//System.out.println("two grams : " + twograms);
						insertToHashmap(cleanedTokens);
						//insertToHashmap(tokens);
						insertToTree2Grams(twograms);
					}
				}
			}while(S.length()!=0);
			
			//System.out.println("Here.....");
			
		}
		catch(Exception ex)
		{
			ex.getMessage();
		}
		finally
		{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private static List<String> computeTwoGrams(List<String> words)
	{
		int i = 0;
		List<String> twograms = new ArrayList<String>();
		//System.out.println("words");
		while(i < (words.size()-1))
		{
			if(!(words.get(i).equals("#")||words.get(i+1).equals("#")))
			//System.out.print(words.get(i) + ",");
				twograms.add(words.get(i) + " " + words.get(i+1));
			i++;
		}
		
		return twograms;
	}
	
	
	public static List<String> cleanTokens(List<String> tokens)
	{
		List<String> hash = new ArrayList<String>();
		hash.add("#");
		tokens.removeAll(hash);
		return tokens;
	}
	
	public static ArrayList<String> tokenizeString(String toTokenize) {
		try {
			
			List <Pattern> patterns = new ArrayList <Pattern> ();
		    patterns.add (Pattern.compile ("[a-zA-Z]+|#"));
			// patterns.add (Pattern.compile ("[a-zA-Z]+"));
		    
			ArrayList<String> arrayToken = new ArrayList<String>();
			// Read through file one line at time. Print line # and line
			
				for (Pattern p : patterns) 
	            {
	                Matcher m = p.matcher(toTokenize);
	                while (m.find ()){ 
	                    arrayToken.add(m.group());
	                }
	            }
				
			return arrayToken;
		}catch (ArrayIndexOutOfBoundsException e){
			/* If no file was passed on the command line, this exception is
	              generated. */
			System.out.println("Usage: java ReadFile filename\n");          
			  
		}
		// TODO Write body!
		return null;
	}
	
	public static void fillToTextFile(String title, String url, String content)
	{
		
		pw.println("title:"+title.trim()+"$$"+"url:"+url.trim()+"$$"+"content:"+content.trim() );
		pw.flush();
		
       
	}
	
	public static List<String> getFirstX(String s, int x)
	{

		List<String> words = null;
		if(s.toLowerCase().equals("twograms"))
		{
			Map<String, Integer> sortedMapDesc = sortByComparator(tm, false);
			words = printMap(sortedMapDesc,x,s);
		}
		else
		{
			Map<String, Integer> sortedMapDesc2 = sortByComparator(ht, false);
			words = printMap(sortedMapDesc2,x,s);
		}
		
		return words;
	}
	
	public static void insertToHashmap(List<String> tokens)
	{
		//System.out.println("In insert to Hash map");
		for(String token : tokens)
		{
			if(!token.trim().equals(""))
			{
				if(ht.containsKey(token))
				{
					ht.put(token, ht.get(token) + 1);
				}
				else 
				{
						ht.put(token,0);
				}
			}
		}
	}
	
	public static void insertToTree2Grams(List<String> twograms)
	{
		//System.out.println("In insert to Hash map");
		for(String token : twograms)
		{
			if(!token.trim().equals(""))
			{
				if(tm.containsKey(token))
				{
					tm.put(token, tm.get(token) + 1);
				}
				else 
				{
						tm.put(token,0);
				}
			}
		}
	}
	
	
	public static List<String> printMap(Map<String, Integer> map,int x,String s)
    {
		
		File f = null;
		if(s.toLowerCase().equals("twograms"))
			f = new File("Common2Grams.txt");
		else
			f = new File("CommonWords.txt");
		
		PrintWriter p = null;
        try {
			p = new PrintWriter(new BufferedWriter(new FileWriter(f)),true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int count = 0;
		List<String> words = new ArrayList<String>();
        for (Entry<String, Integer> entry : map.entrySet())
        {
        	if(count >= x)
        		break;
        	words.add(entry.getKey());
        	p.println(entry.getKey() + "\t" + entry.getValue());
			p.flush();
        	count++;
            //System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
        }
        
        p.close();
        return words;
    }
	
	
	
	
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
