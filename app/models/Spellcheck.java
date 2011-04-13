// #TODO: The ability to spellcheck and show the result in some helpful way
// #TODO: Abstraction of some of the functions here (like the SQL stuff and the processing stuff)
// #TODO: Write test classes
// #TODO: Write performance classes
// #TODO: Write a build script to create a .war file
// #Wish: Propper substr check
// #Wish: O(1) performance
// #Wish: A pot of gold.
package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Properties;

import org.apache.commons.codec.language.RefinedSoundex;

public class Spellcheck
{
    private Properties prop;
    private Connection conn;
    private Map<String, Boolean> wordMap;
    private ArrayList<String> commonWords;
    private ArrayList<CheckedWord> checkedList;
    private int right = 0;
    private int wrong = 0;

    public Spellcheck() throws SpellConnectionException
    {
	// Init the arrays
	wordMap = new HashMap<String, Boolean>();
	commonWords = new ArrayList<String>();
	checkedList = new ArrayList<CheckedWord>();

	try
	{
	    prop = new Properties();
	    prop.load(new FileInputStream("config.ini"));
	}
	catch(Exception ex)
	{
	    System.out.println(ex.getMessage());
	}

	fillCommonWords();
	connect();
    }

    // Fills the commonWords array with most common words
    private void fillCommonWords()
    {
	commonWords.add("og");
	commonWords.add("að");
	commonWords.add("á");
	commonWords.add("í");
	commonWords.add("til");
	commonWords.add("það");
	commonWords.add("er");
	commonWords.add("þá");
	commonWords.add("við");
	commonWords.add("en");
	commonWords.add("var");
	commonWords.add("um");
	commonWords.add("ekki");
	commonWords.add("með");
	commonWords.add("því");
	commonWords.add("eru");
	commonWords.add("hann");
	commonWords.add("ég");
	commonWords.add("sem");
	commonWords.add("hafa");
	commonWords.add("eða");
    }

    // Takes in a string of words and classifies each word
    public ArrayList<CheckedWord> classify(String article)
    {
	// Create the vector
	ArrayList<CheckedWord> classified = new ArrayList<CheckedWord>();

	// Split by space
	StringTokenizer st = new StringTokenizer(article, "[ \t\n.,]", true);

	CheckedWord.Type type = CheckedWord.Type.UNKNOWN;
	String word;

	while(st.hasMoreTokens())
	{
	    // Get the next token
	    word = st.nextToken();

	    // Get the space
	    if(word.equals(" "))
	    {
		type = CheckedWord.Type.SPACE;
	    }
	    // Get the newline
	    else if(word.equals("\n"))
	    {
		type = CheckedWord.Type.NEWLINE;
	    }
	    // Dot or the comma
	    else if(word.equals(".") || word.equals(","))
	    {
		type = CheckedWord.Type.PUNCTUATION;
	    }
	    // Words with numbers in them
	    else if(hasNumber(word))
	    {
		type = CheckedWord.Type.NUMBER;
	    }
	    else
	    {
		// Now we might have a word
		type = CheckedWord.Type.WORD;
	    }

	    classified.add(new CheckedWord(word, type));
	}

	return classified;
    }

    // Returns true if the word has a number in it
    private boolean hasNumber(String word)
    {
	for(int i = 0; i < word.length(); i++)
	{
	    if(Character.isDigit(word.charAt(i)))
	    {
		return true;
	    }
	}

	return false;
    }

    // Spellchecks and returns a vector of the checked words
    public ArrayList<CheckedWord> check(ArrayList<CheckedWord> toCheck)
    {
	int index = 0;

	for(CheckedWord word : toCheck)
	{
	    // If it is classified as a word
	    if(word.getType() == CheckedWord.Type.WORD)
	    {
		// If the word is not correctly spelled, then we find similar words
		if(!isCorrect(word.getWord()))
		{
		    wrong++;

		    // If the word is not in the wordmap and not in the common words list
		    // Then add it as a false word so we dont need to do a sql check again
		    if(!wordMap.containsKey(word) && !commonWords.contains(word))
		    {
			wordMap.put(word.getWord(), false);
		    }

		    // Update the toCheck list
		    word.setCorrect(false);
		    //word.setSuggestions(getSimilarWords(word.getWord()));
		    toCheck.set(index, word);

		    // Add the word to the wrong db
		    addWrongWordToDB(word.getWord());
		}
		else
		{
		    right++;

		    // Add it to the checked list that we return
		    word.setCorrect(true);
		    word.setSuggestions(null);
		    toCheck.set(index, word);

		    // If the word is not in the wordmap and not in the common words list
		    // Then add it as a false word so we dont need to do a sql check again
		    if(!wordMap.containsKey(word) && !commonWords.contains(word))
		    {
			wordMap.put(word.getWord(), true);
		    }
		}
	    }
	    index++;
	}

	return toCheck;
    }

    // Connects to the database
    private void connect() throws SpellConnectionException
    {
	try
	{
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    conn = DriverManager.getConnection(prop.getProperty("database"), prop.getProperty("user"), prop.getProperty("password"));
	}
	catch(SQLException e)
	{
	    throw new SpellConnectionException("SQL Exception: " + e.getMessage());
	}
	catch(InstantiationException e)
	{
	    throw new SpellConnectionException("Instance Exception: " + e.getMessage());
	}
	catch(IllegalAccessException e)
	{
	    throw new SpellConnectionException("Illegal access Exception: " + e.getMessage());
	}
	catch(ClassNotFoundException e)
	{
	    throw new SpellConnectionException("Class not found Exception: " + e.getMessage());
	}
    }

    // Returns a list of similar words
    private ArrayList<String> getSimilarWords(String word)
    {
	// Create the vector to return
	ArrayList<String> ret = new ArrayList<String>();

	// Create the sorted list
	HashMap<String, Integer> vec = getSimilar(word);
	LevenshteinCompare dist = new LevenshteinCompare(vec);
	TreeMap<String, Integer> sorted_vec = new TreeMap<String, Integer>(dist);
	sorted_vec.putAll(vec);

	int i = 0;

	// Add the sorted list to the vector
	// Limit of 10, this will change
	for(String words : sorted_vec.keySet())
	{
	    i++;
	    ret.add(words);

	    if(i == 10)
	    {
		break;
	    }
	}

	return ret;
    }

    // Returns true of the word is correctly spelled
    private boolean isCorrect(String word)
    {
	// First we check if it is in the list of common words
	if(commonWords.contains(word))
	{
	    return true;
	}

	// Then we check the hash map for the word
	// If there is a hash map for the word, return the boolean value stored there
	if(wordMap.containsKey(word))
	{
	    return wordMap.get(word);
	}

	try
	{
	    // A quick SQL query to find if a word is within the database
	    PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS(SELECT `ord` FROM `ordmyndir` WHERE `ord` = ?)");
	    stmt.setString(1, word);

	    ResultSet rs = stmt.executeQuery();

	    // If there is one value, return it
	    if(rs.next())
	    {
		return rs.getBoolean(1);
	    }
	}
	catch(SQLException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return false;
    }

    // Adds a word to the wrong db
    private void addWrongWordToDB(String word)
    {
	try
	{
	    PreparedStatement stmt = conn.prepareStatement("INSERT INTO `wrong` (`ord`) VALUES (?)");
	    stmt.setString(1, word);
	    stmt.execute();
	}
	catch(SQLException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    // Returns a vector of similar words based on soundex
    private HashMap<String, Integer> getSimilar(String word)
    {
	HashMap<String, Integer> vec = new HashMap<String, Integer>();

	try
	{
	    RefinedSoundex rex = new RefinedSoundex();

	    // Create a statement that finds every word with a substr of the soundex value of the search word
	    PreparedStatement stmt = conn.prepareStatement("SELECT `ord` FROM `ordmyndir` WHERE `soundex` LIKE ?");
	    stmt.setString(1, getSearchSoundex(rex.soundex(convertWord(word))).concat("%"));

	    ResultSet rs = stmt.executeQuery();

	    while(rs.next())
	    {
		String ord = rs.getString("ord");
		vec.put(rs.getString("ord"), LevenshteinDistance.distance(word, ord));
	    }
	}
	catch(SQLException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return vec;
    }

    // Returns true if it can find that every sub section of the word is spelled correctly
    // It splits the word by vowels.
    public boolean isSubstrCorrect(String word)
    {
	// Used to note how far into the splitting we are
	int progress = 0;

	while(progress < word.length())
	{
	    int longestMatch = getLongestMatch2(word.substring(progress));

	    if(longestMatch < 1)
	    {
		return false;
	    }

	    progress += longestMatch;
	}

	return true;
    }

    // Returns the length of the longest match from the beginning of the word
    private int old_getLongestMatch(String word)
    {
	// Get the start and end indexes
	int vowelIndex = getFirstVowel(word, 0);
	int startIndex = 0;

	// Used to store the longest correct segment of the word
	int longestCorrect = 0;

	while(vowelIndex > -1)
	{
	    String syllable = word.substring(startIndex, vowelIndex + 1);

	    // If the syllable is correct
	    if(isCorrect(syllable))
	    {
		// And the length of it is longer than the last one, then we add that length
		if((vowelIndex + 1) > longestCorrect)
		{
		    longestCorrect = vowelIndex + 1;
		}
	    }

	    vowelIndex = getFirstVowel(word, vowelIndex);
	}

	return longestCorrect;
    }

    // This runs through all iterations of the word and returns the index of the longest match
    private int getLongestMatch2(String word)
    {
	int longestMatch = 0;

	for(int i = 2; i <= word.length(); i++)
	{
	    if(isCorrect(word.substring(0, i)))
	    {
		longestMatch = i;
	    }
	}

	return longestMatch;
    }

    // Returns the index of the first vowel of the word from the offset
    // Returns -1 if no vowel was found
    private int getFirstVowel(String word, int offset)
    {
	String vowels = "a�ei�o�u�y���";

	int shortestIndex = -1;
	int currentIndex = 0;

	// For every vowel
	for(int i = 0; i < vowels.length(); i++)
	{
	    // Find it within the word
	    currentIndex = word.indexOf(vowels.charAt(i), offset + 1);

	    // Find the shortest index
	    if((shortestIndex == -1 && currentIndex > -1) || (currentIndex > -1 && currentIndex < shortestIndex))
	    {
		shortestIndex = currentIndex;
	    }
	}

	return shortestIndex;
    }

    // Returns the substr of the soundex value
    private String getSearchSoundex(String sx)
    {
	// If we have a short soundex, then we'll use it
	if(sx.length() < 8)
	{
	    return sx;
	}

	return sx.substring(0, 6);
    }

    private String convertWord(String word)
    {
	StringBuilder sb = new StringBuilder();

	char[] charArr = word.toLowerCase().toCharArray();

	for(int i = 0; i < charArr.length; i++)
	{
	    if(charArr[i] == 'á')
	    {
		sb.append('a');
	    }
	    else if(charArr[i] == 'é')
	    {
		sb.append('e');
	    }
	    else if(charArr[i] == 'í')
	    {
		sb.append('i');
	    }
	    else if(charArr[i] == 'ú')
	    {
		sb.append('u');
	    }
	    else if(charArr[i] == 'ý')
	    {
		sb.append('y');
	    }
	    else if(charArr[i] == 'ð')
	    {
		sb.append('d');
	    }
	    else if(charArr[i] == 'ó')
	    {
		sb.append('o');
	    }
	    else if(charArr[i] == 'ö')
	    {
		sb.append('o');
	    }
	    else if(charArr[i] == 'æ')
	    {
		sb.append("ae");
	    }
	    else if(charArr[i] == 'þ')
	    {
		sb.append("th");
	    }
	    else if(charArr[i] == '.')
	    {
	    }
	    else if(charArr[i] == '/')
	    {
	    }
	    else if(charArr[i] == '-')
	    {
	    }
	    else
	    {
		sb.append(word.charAt(i));
	    }
	}

	return sb.toString();
    }

    private void updateSoundex()
    {
	try
	{
	    PreparedStatement stmt = conn.prepareStatement("SELECT `ord` FROM `ordmyndir` WHERE `soundex` = ''");
	    ResultSet rs = stmt.executeQuery();

	    RefinedSoundex sex = new RefinedSoundex();

	    int count = 0;

	    HashMap<String, String> vec = new HashMap<String, String>();

	    System.out.println("Staring vec");

	    while(rs.next())
	    {
		String ord = rs.getString("ord");

		vec.put(ord, convertWord(ord));
	    }

	    rs.close();
	    stmt.close();

	    System.out.println("Vec Done");

	    Statement update;

	    for(String orginal : vec.keySet())
	    {
		count++;
		try
		{
		    String ssx = sex.soundex(vec.get(orginal));

		    update = conn.createStatement();

		    update.execute("UPDATE `ordmyndir` SET `soundex` = '" + ssx + "'  WHERE `ord` = '" + orginal + "'");
		    update.close();

		    if(count % 10000 == 0)
		    {
			System.out.println(count);
		    }
		}
		catch(ArrayIndexOutOfBoundsException aioobex)
		{
		    System.out.println(aioobex.getMessage());
		    System.out.println(aioobex.getCause());
		}
	    }

	    System.out.println("Done");

	    stmt.close();
	}
	catch(SQLException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void printWordMap()
    {
	Iterator<?> it = wordMap.entrySet().iterator();

	while(it.hasNext())
	{
	    Map.Entry<String, Boolean> pair = (Map.Entry<String, Boolean>) it.next();

	    System.out.println(pair.getKey() + " " + pair.getValue());
	}
    }

    // Converts a file to a string
    private String fileToString(String filename) throws IOException
    {
	BufferedReader reader = new BufferedReader(new FileReader(filename));
	StringBuilder builder = new StringBuilder();
	String line;

	// For every line in the file, append it to the string builder
	while((line = reader.readLine()) != null)
	{
	    builder.append(line + "\n");
	}

	return builder.toString();
    }
}
