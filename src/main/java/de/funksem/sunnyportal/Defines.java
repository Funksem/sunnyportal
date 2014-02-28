package de.funksem.sunnyportal;

public final class Defines
{
    private Defines()
    {
    }

    public static final String EXTENSION_CSV = "csv";

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String SYSTEM_TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static final char DEFAULT_SEPARATOR = ';';

    public static final int COL_DATE = 0;
    public static final int COL_VALUE = 1;
}
