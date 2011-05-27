package models;

import java.util.ArrayList;

public class Spellcheck
{
    private SQLChecker checker;
    private ArrayList<String> wrongList;
    private int right = 0;
    private int wrong = 0;

    public Spellcheck() throws SpellConnectionException
    {
	// Init the SQL Checker
	checker = new SQLChecker();

	// Setup the common words list
	WordLists.fillCommonWords();
    }

    // Spellchecks and returns a vector of the checked words
    public ArrayList<String> check(ArrayList<CheckedWord> toCheck)
    {
	// Init map of all wrong words
	wrongList = new ArrayList<String>();

	for(CheckedWord word : toCheck)
	{
	    // If it is classified as a word
	    if(word.getType() == CheckedWord.Type.WORD)
	    {
		// If the word is not correctly spelled, then we find similar words
		if(!checker.isCorrect(word.getWord()))
		{
		    wrong++;

		    // If the word is not in the wordmap and not in the common words list
		    // Then add it as a false word so we dont need to do a sql check again
		    if(!WordLists.wordMap.containsKey(word.getWord()) && !WordLists.commonWords.contains(word.getWord()))
		    {
			WordLists.wordMap.put(word.getWord(), false);
		    }

		    // Update the toCheck list
		    word.setCorrect(false);
		    word.setSuggestions(null);

		    if(!wrongList.contains(word.getWord()))
		    {
			wrongList.add(word.getWord());
		    }

		    // Add the word to the wrong db
		    checker.addWrongWordToDB(word.getWord());
		}
		else
		{
		    right++;

		    // Add it to the checked list that we return
		    word.setCorrect(true);
		    word.setSuggestions(null);

		    // If the word is not in the wordmap and not in the common words list
		    // Then add it as a false word so we dont need to do a sql check again
		    if(!WordLists.wordMap.containsKey(word.getWord()) && !WordLists.commonWords.contains(word.getWord()))
		    {
			WordLists.wordMap.put(word.getWord(), true);
		    }
		}
	    }
	}

	return wrongList;
    }
}
