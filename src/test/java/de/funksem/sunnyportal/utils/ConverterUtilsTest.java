package de.funksem.sunnyportal.utils;

import org.junit.Assert;
import org.testng.annotations.Test;

@Test
public class ConverterUtilsTest
{
    public ConverterUtilsTest()
    {
    }

    @Test
    void testToDouble()
    {
        String value = "0.14";
        Double x = 0.14;

        Double result = ConverterUtils.toDouble(value);
        Assert.assertEquals(x, result);
    }

    @Test
    void testToDoubleWithComma()
    {
        String value = "0,14";
        Double x = 0.14;

        Double result = ConverterUtils.toDouble(value);
        Assert.assertEquals(x, result);
    }
}
