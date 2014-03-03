package de.funksem.sunnyportal.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class ConverterUtils
{
    private ConverterUtils()
    {
    }

    public static Double toDouble(String value) throws ParseException
    {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        return format.parse(value).doubleValue();
    }

    public static double runden(int s, double value)
    {
        BigDecimal dec = new BigDecimal(value);
        return dec.setScale(s, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
