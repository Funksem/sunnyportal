package de.funksem.sunnyportal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
        Map<Date, Double> dataCsv = new TreeMap<Date, Double>();

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

        // Zeitraum Von - Bis
        // Anzahl der Ertragstage
        // Anzahl der Tage ohne Ertrag
        // Gesamtertrag
        // Höchster Ertag mit Datum
        // Niedrigster Ertrag mit Datum
        // Gesamtdurchschnitt
        // Durchschnitt pro Jahr
        // Durchschnitt pro Monat

        Date firstDate;
        Date lastDate;
        int ertragstage = 0;
        int tageOhneErtrag = 0;
        Double gesamtertag = 0.0;

        java.util.Map.Entry<Date, Double> pair1 = new java.util.AbstractMap.SimpleEntry<>(null, 0.0);

        Double hoechsterErtrag = 0.0;
        Date hoechsterErtragDatum;
        Double niedrigsterErtrag = 0.0;
        Date niedrigsterErtragDatum;

        // key = Jahr, value = jahresgesamtertrag 
        Map<Integer, Double> jahrList = new TreeMap<>();
        // key = Jahr, value = Datum und höchster Ertrag 
        Map<Integer, Pair<Date, Double>> jahrHoechsterErtragList = new TreeMap<>();

        for (Map.Entry<Date, Double> entry : dataCsv.entrySet())
        {
            final Date date = entry.getKey();
            final Double power = entry.getValue();
            final int year = getYear(date);
            final int month = getMonth(date);

            System.out.println("MAP - " + date + " -> " + power + " kWh");

            if ((power == null) || (power <= 0.0))
            {
                tageOhneErtrag++;
                continue;
            }

            ertragstage++;
            gesamtertag += power;

            if (jahrList.containsKey(year))
            {
                jahrList.put(year, jahrList.get(year) + power);
            }
            else
            {
                jahrList.put(year, power);
            }

            if (jahrHoechsterErtragList.containsKey(year))
            {
                Pair<Date, Double> yearPair = jahrHoechsterErtragList.get(year);
                if (yearPair.getRight() < power)
                {
                    jahrHoechsterErtragList.put(year, Pair.of(date, power));
                }
            }
            else
            {
                jahrHoechsterErtragList.put(year, Pair.of(date, power));
            }

        }

        System.out.println("Alle Tage                   = " + (ertragstage + tageOhneErtrag));
        System.out.println("Anzahl der Tage mit Ertrag  = " + ertragstage);
        System.out.println("Anzahl der Tage ohne Ertrag = " + tageOhneErtrag);
        System.out.println("Gesamtertrag                = " + runden(2, gesamtertag) + " kWh");
        System.out.println("Gesamtdurchschnitt          = "
            + runden(2, (gesamtertag / (ertragstage + tageOhneErtrag))) + " kWh");

        for (Map.Entry<Integer, Double> entry : jahrList.entrySet())
        {
            final Integer year = entry.getKey();
            final Double power = entry.getValue();
            System.out.println("Ertrag " + year + "      = " + runden(2, power) + " kWh");

            Pair<Date, Double> yearPair = jahrHoechsterErtragList.get(year);
            System.out.println("Höchster Ertrag " + year + " = " + runden(2, yearPair.getRight()) + " kWh ("
                + yearPair.getLeft() + ")");

        }
    }

    private static int getMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    private static int getYear(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
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

    static double runden(int s, double value)
    {
        BigDecimal dec = new BigDecimal(value);
        return dec.setScale(s, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
