package models;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SentenceParser
{
    private String sentence;
    
    public SentenceParser(String sentence)
    {
	this.sentence = sentence;
    }

    // Takes in a string of words and classifies each word
    public ArrayList<CheckedWord> parse()
    {
	// Create the vector
	ArrayList<CheckedWord> classified = new ArrayList<CheckedWord>();

	// Split by space
	StringTokenizer st = new StringTokenizer(sentence, "[ \t\n.,]", true);

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
	    else if(Utility.hasNumber(word))
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
}
