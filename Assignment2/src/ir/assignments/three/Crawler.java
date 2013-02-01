package ir.assignments.three;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tartarus.martin.Stemmer;

import com.google.gson.JsonObject;



import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler{
	
	private static List<String> urls = new ArrayList<String>();
	private static int noOfUniqueUrls = 0;
	private static Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private final static Pattern subdomain = Pattern.compile(".*(\\.(ics))$");
	private static File qfive = new File("CommonWords.txt");
	

	/**
	* You should implement this function to specify whether
	* the given url should be crawled or not (based on your
	* crawling logic).
	*/
	@Override
	public boolean shouldVisit(WebURL url) {
		//return true;
		String href = url.getURL().toLowerCase();
		boolean a = !FILTERS.matcher(href).matches() && subdomain.matcher(url.getSubDomain()).matches();
		boolean b = alreadyVisited(url);
		return a && !b;
	}
	
	public synchronized boolean alreadyVisited(WebURL url)
	{
		if(ht.containsKey(url)) return true;
		else 
		{
				ht.put(url.getURL(),0);
				return false;
		}
	}
	
	/**
	* This function is called when a page is fetched and ready 
	* to be processed by your program.
	* Implement in this function, things to be done once the file contents have been got such as counting words, 2-grams etc.
	*/
	@Override
	public void visit(Page page) {  
		
		try
		{
			noOfUniqueUrls++;
			String url = page.getWebURL().getURL();
			System.out.println("URL: " + url);
			//crawl(url);
			urls.add(url);
			
			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String text = htmlParseData.getText();
				String html = htmlParseData.getHtml();
				List<WebURL> links = htmlParseData.getOutgoingUrls();
				
				parseHTML(html,url);
				//System.out.println("Text length: " + text.length());
				//System.out.println("Html length: " + html.length());
				//System.out.println("Number of outgoing links: " + links.size());
			}
		}
		catch(Exception e)
		{
			System.out.println("e : " + e.toString());
		}
	}
	
	
	public static void parseHTML(String html, String url) throws IOException
	{
		Document doc = Jsoup.parse(html);
		String title = doc.title();
        Elements content = doc.select("blockquote").select("p");
        String contentText =content.text();
      
		
		//System.out.println("Title : " + title);
		//System.out.println("Content : " + contentText);
		
		String processed = preprocess(contentText);
        
        Controller.fillToTextFile(title,url,processed);
	}
	
	
	
	public static String preprocess(String contentText)
	{
		//String swords = fillstopwords();
		String swords = "\\ba\\b|\\babout\\b|\\babove\\b|\\bafter\\b|\\bagain\\b|\\bagainst\\b|\\ball\\b|\\bam\\b|\\ban\\b|\\band\\b|\\bany\\b|\\bare\\b|\\baren't\\b|\\bas\\b|\\bat\\b|\\bbe\\b|\\bbecause\\b|\\bbeen\\b|\\bbefore\\b|\\bbeing\\b|\\bbelow\\b|\\bbetween\\b|\\bboth\\b|\\bbut\\b|\\bby\\b|\\bcan't\\b|\\bcannot\\b|\\bcould\\b|\\bcouldn't\\b|\\bdid\\b|\\bdidn't\\b|\\bdo\\b|\\bdoes\\b|\\bdoesn't\\b|\\bdoing\\b|\\bdon't\\b|\\bdown\\b|\\bduring\\b|\\beach\\b|\\bfew\\b|\\bfor\\b|\\bfrom\\b|\\bfurther\\b|\\bhad\\b|\\bhadn't\\b|\\bhas\\b|\\bhasn't\\b|\\bhave\\b|\\bhaven't\\b|\\bhaving\\b|\\bhe\\b|\\bhe'd\\b|\\bhe'll\\b|\\bhe's\\b|\\bher\\b|\\bhere\\b|\\bhere's\\b|\\bhers\\b|\\bherself\\b|\\bhim\\b|\\bhimself\\b|\\bhis\\b|\\bhow\\b|\\bhow's\\b|\\bi\\b|\\bi'd\\b|\\bi'll\\b|\\bi'm\\b|\\bi've\\b|\\bif\\b|\\bin\\b|\\binto\\b|\\bis\\b|\\bisn't\\b|\\bit\\b|\\bit's\\b|\\bits\\b|\\bitself\\b|\\blet's\\b|\\bme\\b|\\bmore\\b|\\bmost\\b|\\bmustn't\\b|\\bmy\\b|\\bmyself\\b|\\bno\\b|\\bnor\\b|\\bnot\\b|\\bof\\b|\\boff\\b|\\bon\\b|\\bonce\\b|\\bonly\\b|\\bor\\b|\\bother\\b|\\bought\\b|\\bour\\b|\\bours\\b|\\bourselves\\b|\\bout\\b|\\bover\\b|\\bown\\b|\\bsame\\b|\\bshan't\\b|\\bshe\\b|\\bshe'd\\b|\\bshe'll\\b|\\bshe's\\b|\\bshould\\b|\\bshouldn't\\b|\\bso\\b|\\bsome\\b|\\bsuch\\b|\\bthan\\b|\\bthat\\b|\\bthat's\\b|\\bthe\\b|\\btheir\\b|\\btheirs\\b|\\bthem\\b|\\bthemselves\\b|\\bthen\\b|\\bthere\\b|\\bthere's\\b|\\bthese\\b|\\bthey\\b|\\bthey'd\\b|\\bthey'll\\b|\\bthey're\\b|\\bthey've\\b|\\bthis\\b|\\bthose\\b|\\bthrough\\b|\\bto\\b|\\btoo\\b|\\bunder\\b|\\buntil\\b|\\bup\\b|\\bvery\\b|\\bwas\\b|\\bwasn't\\b|\\bwe\\b|\\bwe'd\\b|\\bwe'll\\b|\\bwe're\\b|\\bwe've\\b|\\bwere\\b|\\bweren't\\b|\\bwhat\\b|\\bwhat's\\b|\\bwhen\\b|\\bwhen's\\b|\\bwhere\\b|\\bwhere's\\b|\\bwhich\\b|\\bwhile\\b|\\bwho\\b|\\bwho's\\b|\\bwhom\\b|\\bwhy\\b|\\bwhy's\\b|\\bwith\\b|\\bwon't\\b|\\bwould\\b|\\bwouldn't\\b|\\byou\\b|\\byou'd\\b|\\byou'll\\b|\\byou're\\b|\\byou've\\b|\\byour\\b|\\byours\\b|\\byourself\\b|\\byourselves\\b|";
		//System.out.println(swords);
        //String stopwordsRemoved = removestopwords(contentText,swords);
		String stopwordsRemoved = contentText.toLowerCase().replaceAll(swords, "");
		String[] sw = stopwordsRemoved.split(" ");
		String stemmed = "";
		for(int i = 0; i < sw.length ; i++)
		{
			stemmed += " " + stem(sw[i]);
		}
		
        //String stemmed = stem(stopwordsRemoved);
		
        //System.out.println("Stemmed Content : " + stemmed);
        return stemmed;
	}
	
	/*public static String removestopwords(String contentText)
	{
		for(String sw : stopwords)
		{
			contentText.replaceAll(sw, "");
		}
		return contentText; 
	}
	*/
	
	
	// get a better stemmer maybe ?
	public static String stem(String contentToStem)
	{
		Stemmer stemmer = new Stemmer();
		stemmer.add(contentToStem.toCharArray(),contentToStem.length());
		stemmer.stem();
		return stemmer.toString();
	}
	
	public static String fillstopwords()
	{
		Scanner s;
		try {
			s = new Scanner(new File("stopwords.txt"));
			String stopwords = "";
			while (s.hasNext()){
				stopwords +=  "\\\\b" + s.next() + "\\\\b" + "|";
			}
			s.close();
			
			return stopwords;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
	public static Collection<String> crawl(String seedURL) {
		
		// TODO implement me
		
		
		
		return urls;
	}
	
	

	public static Collection<String> getUrls()
	{
		return urls;
	}
	
	public static int getnoOfURLS()
	{
		return noOfUniqueUrls;
	}

}