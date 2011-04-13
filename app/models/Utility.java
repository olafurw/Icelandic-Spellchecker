package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utility
{
    private Utility()
    {

    }

    // Returns true if the word has a number in it
    public static boolean hasNumber(String word)
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

    // Converts a word from the Icelandic characters so a soundex check can be done
    public static String convertWord(String word)
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

    // Converts a file to a string
    public static String fileToString(String filename) throws IOException
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
