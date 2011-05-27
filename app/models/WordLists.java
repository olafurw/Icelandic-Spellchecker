package models;

import java.util.ArrayList;
import java.util.HashMap;

public class WordLists
{
    public static ArrayList<String> commonWords = new ArrayList<String>();
    public static HashMap<String, Boolean> wordMap = new HashMap<String, Boolean>();

    private WordLists()
    {

    }

    // Fills the commonWords array with most common words
    public static void fillCommonWords()
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
	commonWords.add("yfir");
    }
}
