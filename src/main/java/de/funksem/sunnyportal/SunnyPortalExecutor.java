package de.funksem.sunnyportal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import de.funksem.sunnyportal.utils.ConverterUtils;
import de.funksem.sunnyportal.utils.DateUtils;
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
            System.out.println("Fehler - Kein Verzeichnis: " + sourceDirectory);
            return;
        }

        Collection<File> csvFiles = IOUtils.getFiles(sourceDirectory, Defines.EXTENSION_CSV);
        Map<Date, Double> dataCsv = new TreeMap<Date, Double>();

        System.out.println("Verarbeite CSV-Dateien (" + csvFiles.size() + "): ");

        for (File file : csvFiles)
        {
            System.out.println("++ " + file.getName());
            try (CSVReader reader = new CSVReader(new FileReader(file), Defines.DEFAULT_SEPARATOR))
            {
                reader.readNext(); // Header überspringen(!)
                String[] line;
                while ((line = reader.readNext()) != null)
                {
                    final Date date = DateUtils.toDate(line[Defines.COL_DATE]);
                    Double power = ConverterUtils.toDouble(line[Defines.COL_VALUE]);
                    if (dataCsv.containsKey(date))
                    {
                        power += dataCsv.get(date);
                    }
                    dataCsv.put(date, power);
                }
            }
            catch (IOException | ParseException e)
            {
                e.printStackTrace();
            }
        }

        System.out.println("\n++++++++++++++++  GESAMT  ++++++++++++++++\n");
        ComputationGlobal computationGlobal = new ComputationGlobal(dataCsv);
        computationGlobal.startComputation();

        System.out.println("Zeitraum                    = "
            + DateUtils.simpleFormat(computationGlobal.getAnfangsDatum()) + " bis "
            + DateUtils.simpleFormat(computationGlobal.getEndeDatum()));
        System.out.println("Alle Tage mit Messung       = " + computationGlobal.getGesamtTage());
        System.out.println("Anzahl der Tage mit Ertrag  = " + computationGlobal.getTageMitErtrag());
        System.out.println("Anzahl der Tage ohne Ertrag = " + computationGlobal.getTageOhneErtrag());
        System.out.println("Gesamtertrag                = "
            + ConverterUtils.runden(3, computationGlobal.getGesamtertag()) + " kWh");
        System.out.println("Durchschnitt / Ertragstag   = "
            + ConverterUtils.runden(3,
                (computationGlobal.getGesamtertag() / computationGlobal.getGesamtTage())) + " kWh");
        System.out.println("Hoechster Ertrag            = "
            + computationGlobal.getHoechsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getHoechsterErtrag().getLeft()) + ")");
        System.out.println("Niedrigster Ertrag          = "
            + computationGlobal.getNiedrigsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getNiedrigsterErtrag().getLeft()) + ")");

        for (Integer year : ComputationUtils.getYears(dataCsv))
        {
            System.out.println("\n++++++++++++++++  " + year + "  ++++++++++++++++\n");
            Map<Date, Double> filteredData = ComputationUtils.filterYear(dataCsv, year);

            ComputationYear computationYear = new ComputationYear(filteredData);
            computationYear.startComputation();
            System.out.println("Alle Tage mit Messung       = " + computationYear.getGesamtTage());
            System.out.println("Anzahl der Tage mit Ertrag  = " + computationYear.getTageMitErtrag());
            System.out.println("Anzahl der Tage ohne Ertrag = " + computationYear.getTageOhneErtrag());

            System.out.println("Gesamtertrag                = "
                + ConverterUtils.runden(3, computationYear.getGesamtertag()) + " kWh");
            System.out.println("Gesamtdurchschnitt / Tag    = "
                + ConverterUtils.runden(3,
                    (computationYear.getGesamtertag() / computationYear.getGesamtTage())) + " kWh");
            System.out.println("Hoechster Ertrag            = "
                + computationYear.getHoechsterErtrag().getRight()
                + " kWh (" + DateUtils.simpleFormat(computationYear.getHoechsterErtrag().getLeft()) + ")");
            System.out
                .println("Niedrigster Ertrag          = "
                    + computationYear.getNiedrigsterErtrag().getRight()
                    + " kWh (" + DateUtils.simpleFormat(computationYear.getNiedrigsterErtrag().getLeft())
                    + ")");

            Map<Integer, Double> monatsErtrag = computationYear.getMonatsErtraege();
            for (Map.Entry<Integer, Double> entry : monatsErtrag.entrySet())
            {
                final Integer month = entry.getKey();
                final Double power = entry.getValue();

                int monatsTage = DateUtils.monatsTagzahl(month, DateUtils.istSchaltjahr(year));
                System.out.println(year
                    + "."
                    + month
                    + " -> "
                    + ConverterUtils.runden(3, power)
                    + " kWh (Durchschnitt/" + monatsTage + " Tage "
                    + ConverterUtils.runden(3,
                        (power / monatsTage)) + " kWh)");
            }
        }
    }
}
