package models;

import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.language.RefinedSoundex;

public class SQLChecker
{
    private WordLists wordLists;
    private Connection conn;
    private SQLConfig config;

    public SQLChecker() throws SpellConnectionException
    {
	config = new SQLConfig();
	connect();
    }

    // Connects to the database
    private void connect() throws SpellConnectionException
    {
	try
	{
	    Class.forName(config.getDriver()).newInstance();
	    conn = DriverManager.getConnection(config.getDatabase(), config.getUsername(), config.getPassword());
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

    // Returns true of the word is correctly spelled
    public boolean isCorrect(String word)
    {
	// First we check if it is in the list of common words
	if(wordLists.commonWords.contains(word))
	{
	    return true;
	}

	// Then we check the hash map for the word
	// If there is a hash map for the word, return the boolean value stored there
	if(wordLists.wordMap.containsKey(word))
	{
	    return wordLists.wordMap.get(word);
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
    public void addWrongWordToDB(String word)
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
}
