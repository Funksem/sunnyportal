package de.funksem.sunnyportal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
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

public class SunnyPortalExecutor
{
    Double verguetung = null;
    Double ueberweisungVomFinanzamt = null;

    public SunnyPortalExecutor()
    {
    }

    public void start(final String sourceDirectory, Double verguetungInEuro, Double guthabenProMonat)
    {
        if (!IOUtils.isDir(sourceDirectory))
        {
            throw new IllegalArgumentException("Fehler - Kein Verzeichnis: " + sourceDirectory);
        }

        System.out.println("Quellverzeichnis        = " + sourceDirectory);

        if (verguetungInEuro == null)
        {
            System.out.println("Keine Vergütung pro kWh angegeben.");
        }
        else
        {
            verguetung = verguetungInEuro;
            System.out.println("Vergütung pro kWh       = " + verguetung + " EUR");
        }
        if (guthabenProMonat == null)
        {
            System.out.println("Kein Guthaben pro Monat angegeben.");
        }
        else
        {
            ueberweisungVomFinanzamt = guthabenProMonat;
            System.out.println("Überweisungen (Finanzamt) pro Monat = " + ueberweisungVomFinanzamt + " EUR");
        }

        if ((ueberweisungVomFinanzamt != null) && (verguetung != null))
        {
            Double minKWhProMonat = getMinKWhProMonat();
            Double minKWhProJahr = getMinKWhProJahr();
            Double minKWhProTag = getMinKWhProTag();
            System.out.println("Ertrag pro Jahr um " + ueberweisungVomFinanzamt + " EUR zu erreichen  = "
                + ConverterUtils.runden(3, minKWhProJahr) + " kWh");
            System.out
                .println("Ertrag pro Monat um " + ueberweisungVomFinanzamt + " EUR zu erreichen = "
                    + ConverterUtils.runden(3, minKWhProMonat) + " kWh");
            System.out.println("Ertrag pro Tag um " + ueberweisungVomFinanzamt + " EUR zu erreichen   = "
                + ConverterUtils.runden(3, minKWhProTag) + " kWh");
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

                DecimalFormat formatterKWH = new java.text.DecimalFormat("0.000");
                String msg = MessageFormat
                    .format(
                        "{0}.{1} = {2} kWh (DSch/{3} Tage {4} kWh) -> {5} EUR (bis zum Finanzamtwert = {6} EUR)",
                        Integer.toString(year), StringUtils.leftPad(Integer.toString(month), 2, '0'),
                        formatterKWH.format(ConverterUtils.runden(3, power)), monatsTage,
                        formatterKWH.format(ConverterUtils.runden(3,
                            (power / monatsTage))), calcVerguetung(power), calcVerguetung(power)
                            - ueberweisungVomFinanzamt);
                System.out.println(msg);
            }
        }
    }

    private Double getMinKWhProTag()
    {
        if ((ueberweisungVomFinanzamt != null) && (verguetung != null))
        {
            return getMinKWhProJahr() / 365;
        }
        return null;
    }

    private Double getMinKWhProJahr()
    {
        if ((ueberweisungVomFinanzamt != null) && (verguetung != null))
        {
            return getMinKWhProMonat() * 12;
        }
        return null;
    }

    private Double getMinKWhProMonat()
    {
        if ((ueberweisungVomFinanzamt != null) && (verguetung != null))
        {
            return ueberweisungVomFinanzamt / verguetung;
        }
        return null;
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
                    if (!StringUtils.isBlank(line[Defines.COL_VALUE]))
                    {
                        Double power = ConverterUtils.toDouble(line[Defines.COL_VALUE]);
                        if (dataCsv.containsKey(date))
                        {
                            power += dataCsv.get(date);
                        }
                        dataCsv.put(date, power);
                    }
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

        System.out.println(MessageFormat.format(
            "Anzahl der Tage mit Messung = {0} (mit Ertrag={1}, ohne Ertrag={2})",
            computationGlobal.getGesamtTage(), computationGlobal.getTageMitErtrag(),
            computationGlobal.getTageOhneErtrag()));
        System.out.println("Gesamtertrag                = "
            + ConverterUtils.runden(3, computationGlobal.getGesamtertag()) + " kWh");

        Double unterschied = (computationGlobal.getGesamtertag() / computationGlobal.getGesamtTage())
            - getMinKWhProTag();

        System.out.println(MessageFormat.format(
            "Durchschnitt pro Tag        = {0} kWh, (bis zum Finanzamtwert = {1} kWh)",
            ConverterUtils.runden(3,
                (computationGlobal.getGesamtertag() / computationGlobal.getGesamtTage())), unterschied));

        System.out.println("Hoechster Ertrag            = "
            + computationGlobal.getHoechsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getHoechsterErtrag().getLeft()) + ")");
        System.out.println("Niedrigster Ertrag          = "
            + computationGlobal.getNiedrigsterErtrag().getRight()
            + " kWh (" + DateUtils.simpleFormat(computationGlobal.getNiedrigsterErtrag().getLeft()) + ")");
        if (verguetung != null)
        {
            System.out.println("Vergütung                   = "
                + calcVerguetung(computationGlobal.getGesamtertag()) + " EUR");
        }
    }

    Double calcVerguetung(Double power)
    {
        return ConverterUtils.runden(2, (power * verguetung));
    }
}
