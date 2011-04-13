package models;

import java.util.Comparator;
import java.util.Map;

class LevenshteinCompare implements Comparator<Object>
{
    Map<String, Integer> base;

    public LevenshteinCompare(Map<String, Integer> base)
    {
	this.base = base;
    }

    // Used to sort the map
    public int compare(Object a, Object b)
    {
	if((Integer) base.get(a) < (Integer) base.get(b))
	{
	    return -1;
	}
	else if((Integer) base.get(a) == (Integer) base.get(b))
	{
	    return 0;
	}
	else
	{
	    return 1;
	}
    }
}
