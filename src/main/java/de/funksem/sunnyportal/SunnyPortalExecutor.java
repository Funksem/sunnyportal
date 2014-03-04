package de.funksem.sunnyportal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVReader;
import de.funksem.sunnyportal.utils.ConverterUtils;
import de.funksem.sunnyportal.utils.DateUtils;
import de.funksem.sunnyportal.utils.IOUtils;

public class SunnyPortalExecutor
{
    Double verguetung = null;

    public SunnyPortalExecutor()
    {
    }

    public void start(final String sourceDirectory, Double verguetungInEuro)
    {
        if (!IOUtils.isDir(sourceDirectory))
        {
            throw new IllegalArgumentException("Fehler - Kein Verzeichnis: " + sourceDirectory);
        }

        System.out.println("Quellverzeichnis  = " + sourceDirectory);

        if (verguetungInEuro == null)
        {
            System.out.println("Keine Vergütung pro kWh angegeben.");
        }
        else
        {
            verguetung = verguetungInEuro;
            System.out.println("Vergütung pro kWh = " + verguetung + " EUR");
        }

        Collection<File> csvFiles = IOUtils.getFiles(sourceDirectory, Defines.EXTENSION_CSV);
        System.out.println(Defines.LINE_SEPARATOR + "Verarbeite CSV-Dateien (Anzahl=" + csvFiles.size()
            + "): ");

        final Map<Date, Double> csvData = readCsvFiles(csvFiles);

        ComputationGlobal computationGlobal = new ComputationGlobal(csvData);
        computationGlobal.startComputation();

        printBasicResults(computationGlobal, "GESAMT");

        for (Integer year : ComputationUtils.getYears(csvData))
        {
            Map<Date, Double> filteredData = ComputationUtils.filterYear(csvData, year);
            ComputationYear computationYear = new ComputationYear(filteredData);
            computationYear.startComputation();

            printBasicResults(computationYear, Integer.toString(year));
            System.out.println(Defines.LINE_SEPARATOR);

            Map<Integer, Double> monatsErtrag = computationYear.getMonatsErtraege();
            for (Map.Entry<Integer, Double> entry : monatsErtrag.entrySet())
            {
                final Integer month = entry.getKey();
                final Double power = entry.getValue();

                int monatsTage = DateUtils.monatsTagzahl(month, year);
                String strMonth = Integer.toString(month);
                if (month < 10)
                {
                    strMonth = "0" + month;
                }
                System.out.println(year
                    + "."
                    + strMonth
                    + " -> "
                    + ConverterUtils.runden(3, power)
                    + " kWh (DSch/" + monatsTage + " Tage "
                    + ConverterUtils.runden(3,
                        (power / monatsTage)) + " kWh) -> " + calcVerguetung(power) + " EUR");
            }
        }
    }

    private Map<Date, Double> readCsvFiles(Collection<File> csvFiles)
    {
        Map<Date, Double> dataCsv = new TreeMap<Date, Double>();
        for (File file : csvFiles)
        {
            System.out.println(" + " + file.getName());
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
        return dataCsv;
    }

    private void printBasicResults(ComputationGlobal computationGlobal, String title)
    {
        System.out.println("\n++++++++++++++++++++++++++  " + title + "  ++++++++++++++++++++++++++\n");
        System.out.println("Zeitraum                    = "
            + DateUtils.simpleFormat(computationGlobal.getAnfangsDatum()) + " bis "
            + DateUtils.simpleFormat(computationGlobal.getEndeDatum()));
        System.out.println("Anzahl der Tage mit Messung = " + computationGlobal.getGesamtTage());
        System.out.println("Anzahl der Tage mit Ertrag  = " + computationGlobal.getTageMitErtrag());
        System.out.println("Anzahl der Tage ohne Ertrag = " + computationGlobal.getTageOhneErtrag());
        System.out.println("Gesamtertrag                = "
            + ConverterUtils.runden(3, computationGlobal.getGesamtertag()) + " kWh");
        if (verguetung != null)
        {
            System.out.println("Vergütung                   = "
                + calcVerguetung(computationGlobal.getGesamtertag()) + " EUR");
        }
        System.out.println("Durchschnitt pro Tag        = "
            + ConverterUtils.runden(3,
                (computationGlobal.getGesamtertag() / computationGlobal.getGesamtTage())) + " kWh");
        System.out.println("Hoechster Ertrag            = "
            + computationGlobal.getHoechsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getHoechsterErtrag().getLeft()) + ")");
        System.out.println("Niedrigster Ertrag          = "
            + computationGlobal.getNiedrigsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getNiedrigsterErtrag().getLeft()) + ")");
    }

    Double calcVerguetung(Double power)
    {
        return ConverterUtils.runden(2, (power * verguetung));

    }
}
