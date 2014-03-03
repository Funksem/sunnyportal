package de.funksem.sunnyportal;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.funksem.sunnyportal.utils.DateUtils;

public final class ComputationUtils
{
    private ComputationUtils()
    {
    }

    public static Set<Integer> getYears(Map<Date, Double> data)
    {
        Set<Integer> result = new HashSet<Integer>();
        for (Map.Entry<Date, Double> entry : data.entrySet())
        {
            int year = DateUtils.getYear(entry.getKey());
            if (!result.contains(year))
            {
                result.add(year);
            }
        }
        return result;
    }

    public static Set<Integer> getMonth(Map<Date, Double> data)
    {
        Set<Integer> result = new HashSet<Integer>();
        for (Map.Entry<Date, Double> entry : data.entrySet())
        {
            int month = DateUtils.getMonth(entry.getKey());
            if (!result.contains(month))
            {
                result.add(month);
            }
        }
        return result;
    }

    public static Map<Date, Double> filterYear(Map<Date, Double> data, int year)
    {
        Map<Date, Double> result = new HashMap<>(data);

        for (Map.Entry<Date, Double> entry : data.entrySet())
        {
            if (DateUtils.getYear(entry.getKey()) != year)
            {
                result.remove(entry.getKey());
            }
        }
        return result;
    }

    public static Map<Date, Double> filterMonth(Map<Date, Double> data, int month)
    {
        Map<Date, Double> result = new HashMap<>(data);

        for (Map.Entry<Date, Double> entry : data.entrySet())
        {
            if (DateUtils.getMonth(entry.getKey()) != month)
            {
                result.remove(entry.getKey());
            }
        }
        return result;
    }

}
