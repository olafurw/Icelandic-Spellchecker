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
            ArrayList<CheckedWord> checkedWords = spell.check(parser.parse());

            // Send the results to the view
            renderArgs.put("checkedWords", checkedWords);
            render();
        }

        catch(SpellConnectionException scex)
        {
            scex.getStackTrace();
        }
    }
}