package de.funksem.sunnyportal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.funksem.sunnyportal.utils.CsvUtils;

public final class SunnyPortalExecutor
{
    private SunnyPortalExecutor()
    {
    }

    public static void start(final String sourceDirectory)
    {
        if (StringUtils.isBlank(sourceDirectory))
        {
            throw new IllegalArgumentException();
        }

        System.out.println(new File(".").getAbsolutePath());

        List<Date> dateList = new ArrayList<>();
        List<Double> powerList = new ArrayList<>();

        Collection<File> csvFiles = CsvUtils.getCsvFiles(sourceDirectory);
        for (File file : csvFiles)
        {
            System.out.println("Processing " + file);

        }
    }
}
