package de.funksem.sunnyportal.utils;

import java.math.BigDecimal;

public final class ConverterUtils
{
    private ConverterUtils()
    {
    }

    public static Double toDouble(String value)
    {
        value = value.replace(',', '.');
        return new Double(value);
    }

    public static double runden(int s, double value)
    {
        BigDecimal dec = new BigDecimal(value);
        return dec.setScale(s, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
