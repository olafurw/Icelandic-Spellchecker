package models;

import java.util.Properties;
import java.io.FileInputStream;

public class SQLConfig
{
    private Properties prop;

    public SQLConfig()
    {
	try
	{
	    prop = new Properties();
	    prop.load(new FileInputStream("../config.ini"));
	}
	catch(Exception ex)
	{
	    System.out.println(ex.getMessage());
	}

	System.out.println(prop);
    }

    public String getDriver()
    {
	return prop.getProperty("driver");
    }

    public String getDatabase()
    {
	return prop.getProperty("database");
    }

    public String getUsername()
    {
	return prop.getProperty("user");
    }

    public String getPassword()
    {
	return prop.getProperty("password");
    }
}
