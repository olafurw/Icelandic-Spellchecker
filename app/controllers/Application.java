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
            Spellcheck spell = new Spellcheck();

            // Get the argument
            String toCheck = params.get("spelling");

            // Make the checker do it's work
            ArrayList<CheckedWord> checkedWords = spell.check(spell.classify(toCheck));

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