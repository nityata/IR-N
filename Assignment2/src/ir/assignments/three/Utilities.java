package ir.assignments.three;

import ir.assignments.three.Frequency;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.*;
/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 * 
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case. 
	 * 
	 * Example:
	 * 
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 * 
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 * 
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 */
	public static ArrayList<String> tokenizeFile(File input) {
		try {
			/*  Sets up a file reader to read the file passed on the command
			line one character at a time */
			//FileReader input = new FileReader("file.txt");
			              
			/* Filter FileReader through a Buffered read to read a line at a
			time */
			BufferedReader bufRead = new BufferedReader( new FileReader(input));
			//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;    // String that holds current file line
			int count = 0;  // Line number of count 
			              
			// Read first line
			line = bufRead.readLine();
			count++;
			String regex = "( ^[a-zA-Z0-9]*$ ) | ( \\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b )";
			
			List <Pattern> patterns = new ArrayList <Pattern> ();
		    patterns.add (Pattern.compile ("(\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b)"));
		    patterns.add(Pattern.compile( "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})"  ));
		    patterns.add (Pattern.compile ("[a-zA-Z0-9]*"));
		    
			ArrayList<String> arrayToken = new ArrayList<String>();
			// Read through file one line at time. Print line # and line
			while (line != null){
				System.out.println(count+": "+line);
				for (Pattern p : patterns) 
	            {
	                Matcher m = p.matcher (line);
	                while (m.find ()){ 
	                    arrayToken.add (m.group ());
	                }
	              line= m.replaceFirst(" ");
	            }
				line = bufRead.readLine();
				count++;
			}
			bufRead.close();
			return arrayToken;
		}catch (ArrayIndexOutOfBoundsException e){
			/* If no file was passed on the command line, this exception is
	              generated. */
			System.out.println("Usage: java ReadFile filename\n");          
			  
		}catch (IOException e){
			// If another exception is generated, print a stack trace
			e.printStackTrace();
		}
		// TODO Write body!
		return null;
	}
	
	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 * 
	 * Example one:
	 * 
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total item count: 6
	 * Unique item count: 5
	 * 
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 * 
	 * 
	 * Example two:
	 * 
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 * 
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 * 
	 * @param frequencies A list of frequencies.
	 */
	public static void printFrequencies(List<Frequency> frequencies) {
		// TODO Write body!
		int i = 0;
		int total = 0;
		while ( i < frequencies.size() )
		{
			total = total + frequencies.get(i).getFrequency();
			i++;
		}
		
		System.out.println("Unique item count: " + frequencies.size());
		System.out.println("Total item count: " + total);
		i = 0;
		while ( i < frequencies.size() )
		{
			System.out.print(frequencies.get(i) +"\n");
			i++;
		}
	}
}
