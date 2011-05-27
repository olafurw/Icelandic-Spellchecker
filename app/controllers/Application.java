package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller
{
    public static void index()
    {
        // Render the default view
        render();
    }

    public static void check()
    {
        try
        {
            // Get the spellchecker
	    SentenceParser parser = new SentenceParser(params.get("spelling"));
            Spellcheck spell = new Spellcheck();

            // Make the checker do it's work
            ArrayList<String> wrongWords = spell.check(parser.parse());

	    // Use the wrong words list to generate the html
	    ResultGenerator generator = new ResultGenerator(params.get("spelling"), wrongWords);
	    String resultHTML = generator.createHTML();

            // Send the results to the view
            renderArgs.put("resultHTML", resultHTML);
            render();
        }

        catch(SpellConnectionException scex)
        {
            scex.getStackTrace();
        }
    }
}