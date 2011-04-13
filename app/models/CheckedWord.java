package models;

import java.util.ArrayList;

public class CheckedWord
{
    public static enum Type
    {
	WORD, NUMBER, SPACE, NEWLINE, UNKNOWN
    };
    private String word;
    private boolean correct;
    private ArrayList<String> suggestions;
    private Type type;

    public CheckedWord(String word, Type type)
    {
	this.word = word;
	this.type = type;
    }

    public String getWord()
    {
	return word;
    }

    public Type getType()
    {
	return type;
    }

    public boolean isCorrect()
    {
	return correct;
    }

    public void setCorrect(boolean correct)
    {
	this.correct = correct;
    }

    public ArrayList<String> getSuggestions()
    {
	return suggestions;
    }

    public void setSuggestions(ArrayList<String> suggestions)
    {
	this.suggestions = suggestions;
    }
}
