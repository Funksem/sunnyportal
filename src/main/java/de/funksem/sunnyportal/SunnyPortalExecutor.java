package de.funksem.sunnyportal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import de.funksem.sunnyportal.utils.IOUtils;

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

        Path sourcePath = Paths.get(sourceDirectory);

        if (!Files.isDirectory(sourcePath))
        {
            System.out.println("Kein Verzeichnis " + sourceDirectory);
            return;
        }

        List<Date> dateList = new ArrayList<>();
        List<Double> powerList = new ArrayList<>();
        Map<Date, Double> dataCsv = new HashMap<>();

        Collection<File> csvFiles = IOUtils.getFiles(sourceDirectory, Defines.EXTENSION_CSV);

        System.out.println("Anzahl der CSV-Dateien = " + csvFiles.size());

        for (File file : csvFiles)
        {
            System.out.println("Processing " + file);
            try
            {
                CSVReader reader = new CSVReader(new FileReader(file), Defines.DEFAULT_SEPARATOR);
                reader.readNext(); // Header überspringen(!)
                String[] line;
                while ((line = reader.readNext()) != null)
                {
                    System.out.println(file.getName() + ": " + line[Defines.COL_DATE] + " -> "
                        + line[Defines.COL_VALUE] + " kWh");
                    final Date date = str2Date(line[Defines.COL_DATE]);
                    Double power = str2Double(line[Defines.COL_VALUE]);
                    System.out.println(date + " -> " + power + " kWh");

                    if (dataCsv.containsKey(date))
                    {
                        dataCsv.put(date, dataCsv.get(date) + power);
                    }
                    else
                    {
                        dataCsv.put(date, power);
                    }

                    //                    mapToWrite.put(line[COL_NAME], new DefaultProperty(line[COL_NAME], line[COL_DEFVALUE], line[COL_VALUE],
                    //                        line[COL_REGEX], Boolean.TRUE, PropertyLevel.valueOf(line[COL_LEVEL]), line[COL_DESC]));
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    static Date str2Date(String strDate) throws ParseException
    {
        try
        {
            SimpleDateFormat sdfToDate = new SimpleDateFormat("dd.MM.yy");
            return sdfToDate.parse(strDate);
        }
        catch (ParseException e)
        {
            throw e;
        }
    }

    static Double str2Double(String strDouble) throws ParseException
    {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        Number number = format.parse(strDouble);
        return number.doubleValue();
    }
}
