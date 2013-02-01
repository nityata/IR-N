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

	private static TreeMap<String, Integer> ht;
	private static File file;
	private static PrintWriter pw;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		try {
			
			ht = new TreeMap<String, Integer>();
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
	        List<String> words = getFirstFiveHundred();
	        stopTime = System.currentTimeMillis();
	        System.out.println("words : " + words);
	        elapsedTime = stopTime - startTime;
	        System.out.println("Elapsed time to create tree map: " + elapsedTime/1000/60 + " minutes");
	        System.out.println("No of unique urls : " + Crawler.getnoOfURLS());
	        
		}
		finally {
			 //out.close();
			pw.close();
		}
	}
	
	
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
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

	
	public static void fillToTextFile(String title, String url, String content)
	{
		//out.write(title + "$$" + url + "$$" + content + "##");
		//out.flush();
		pw.println("title:"+title.trim()+"$$"+"url:"+url.trim()+"$$"+"content:"+content.trim() );
		pw.flush();
		//out.close();
       
	}
	
	public static List<String> printMap(Map<String, Integer> map)
    {
		File f = new File("CommonWords.txt");
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
        	if(count >= 500)
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
	
	public static void createTreeMapFromFile()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File("trial.txt")));
			//PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File("content.txt"))),true);
			String S= " ";
			do{
			
				S=br.readLine();
				Matcher m=Pattern.compile("(.*)(\\bcontent:\\b)(.*)").matcher(S);
				while(m.find())
				{
					String S1=m.group(3).toString();
					//pw1.print(S1);
					//System.out.println("S1 : " + S1);
					if(!S1.trim().equals(""))
					{
						List<String> tokens = tokenizeString(S1);
						//System.out.println("Tokens : " + tokens);
						insertToHashmap(tokens);
					}
					//
					//
				}
			}while(S.length()!=0);
			
			//System.out.println("HT : " + ht);
			
		}
		catch(Exception ex)
		{
			ex.getMessage();
		}
	}
	
	
	public static ArrayList<String> tokenizeString(String toTokenize) {
		try {
			
			List <Pattern> patterns = new ArrayList <Pattern> ();
		    patterns.add (Pattern.compile ("[a-zA-Z]*"));
		    
			ArrayList<String> arrayToken = new ArrayList<String>();
			// Read through file one line at time. Print line # and line
			
				for (Pattern p : patterns) 
	            {
	                Matcher m = p.matcher (toTokenize);
	                while (m.find ()){ 
	                    arrayToken.add (m.group());
	                }
	                //toTokenize= m.replaceFirst(" ");
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
	

	public static List<String> getFirstFiveHundred()
	{
//		Map<String,Integer> map = ht.descendingMap();
//		Set<String> keys = map.keySet();
//		Iterator<String> it = keys.iterator();
		Map<String, Integer> sortedMapDesc = sortByComparator(ht, false);
		List<String> words = printMap(sortedMapDesc);
//		int count = 0;
//		while(it.hasNext() && count < 500)
//		{
//			words.add(it.next());count++;
//		}
//		return words;
		/*for (Entry<String, Integer> entry  : entriesSortedByValues(ht)) {
		    //System.out.println(entry.getKey()+":"+entry.getValue());
		    words.add(entry.getKey());
		}*/
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
}
