package de.funksem.sunnyportal;

import java.io.File;
import java.util.Collection;

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

        Collection<File> csvFiles = CsvUtils.getCsvFiles(sourceDirectory);
        for (File file : csvFiles)
        {
            System.out.println("> " + file);
        }
    }
}
