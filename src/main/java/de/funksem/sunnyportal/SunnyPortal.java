package de.funksem.sunnyportal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

import de.funksem.sunnyportal.utils.ConverterUtils;

public class SunnyPortal
{
    private static Options options = new Options();

    //CHECKSTYLE:OFF
    static
    {
        options.addOption("h", "help", false, "Anzeige der Hilfe");
        options.addOption("s", "csvdir", true, "Quellverzeichnis mit den CSV-Dateien");
        options.addOption("e", "verguetung", true, "Vergütung in Cent");
    }

    // CHEKCSTYLE:ON

    /**
     * ++++++++++++++ M A I N ++++++++++++++
     */
    public static void main(String[] args)
    {
        try
        {
            CommandLine cli = parseCommandLine(args);
            callRightMethod(cli);
        }
        catch (Exception e)
        {
            System.err.println("Unbekannter Fehler - " + e);
            e.printStackTrace();
        }
    }

    private static void callRightMethod(CommandLine cli) throws FileNotFoundException, IOException
    {
        final String sourceDirectory = cli.getOptionValue('s');
        final String strVerguetungInEuro = cli.getOptionValue('e');
        Double verguetungInEuro = null;
        if (!StringUtils.isBlank(strVerguetungInEuro))
        {
            verguetungInEuro = ConverterUtils.toDouble(strVerguetungInEuro);
        }

        SunnyPortalExecutor executor = new SunnyPortalExecutor();
        executor.start(sourceDirectory, verguetungInEuro);
    }

    private static CommandLine parseCommandLine(String[] args)
    {
        CommandLine cli = null;
        CommandLineParser parser = new PosixParser();
        try
        {
            cli = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            System.err.println("Konnte Kommandozeile nicht parsen: " + e.getMessage());
            System.exit(1);
        }

        if ((cli == null) || cli.hasOption('h') || checkCommandLine(cli))
        {
            showHelpAndExit();
        }
        return cli;
    }

    private static boolean checkCommandLine(CommandLine cli)
    {
        boolean error = false;

        if (!cli.hasOption('s'))
        {
            System.out.println("Es ist kein Quellverzeichnis angegeben");
            error = true;
        }
        return error;
    }

    private static void showHelpAndExit()
    {
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("SunnyPortal <OPTIONS>" + Defines.LINE_SEPARATOR, options);
        System.out.println();
        System.exit(0);
    }
}
