package de.funksem.sunnyportal.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO hartmann_t Klassenbeschreibung fuer DateUtils einfuegen
 *
 * @author hartmann_t
 * @date 07.03.2014
 */
public final class DateUtils
{
    private static final String PATTERN_DD_MM_YY = "dd.MM.yy";
    private final static DateFormat DATE_ONLY = new SimpleDateFormat(PATTERN_DD_MM_YY);
    private final static DateFormat SIMPLE_DATE = new SimpleDateFormat(PATTERN_DD_MM_YY);

    private DateUtils()
    {
    }

    public static int getMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getYear(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static Date toDate(String strDate) throws ParseException
    {
        return DATE_ONLY.parse(strDate);
    }

    public static String simpleFormat(Date date)
    {
        return SIMPLE_DATE.format(date);
    }

    // Ist das Jahr ein Schaltjahr?
    public static boolean istSchaltjahr(int year)
    {
        return ((year % 400) == 0) || (((year % 4) == 0) && ((year % 100) != 0));
    }

    public static int monatsTagzahl(int m, int year)
    {
        return monatsTagzahl(m, istSchaltjahr(year));
    }

    public static int monatsTagzahl(int m, boolean sj)
    {
        switch (m)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default: // Februar
                if (sj)
                {
                    return 29;
                }
                return 28;
        }
    }

    /**
     * TODO Ergebnisse zu ungenau
     */
    public static int diffInMonaten(Date d1, Date d2)
    {
        long millisBetween = 0;
        if (d1.getTime() > d2.getTime())
        {
            millisBetween = d1.getTime() - d2.getTime();
        }
        else
        {
            millisBetween = d2.getTime() - d1.getTime();
        }
        long daysBetween = millisBetween / (1000 * 60 * 60 * 24);

        return (int) (daysBetween / 30);
    }
}
