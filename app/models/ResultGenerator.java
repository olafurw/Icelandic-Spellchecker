package models;

import java.util.ArrayList;

public class ResultGenerator
{
    private ArrayList<String> wrongWords;
    private String input;

    public ResultGenerator(String input, ArrayList<String> wrongWords)
    {
	this.wrongWords = wrongWords;
	this.input = input;
    }

    public String createHTML()
    {
	String result = this.input.replace("\n", "<br />");

	for(String word: wrongWords)
	{
	    result = result.replace(word, "<b style='color: RED'>" + word + "</b>");
	}

	return result;
    }
}
