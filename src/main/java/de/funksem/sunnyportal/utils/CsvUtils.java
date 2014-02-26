package de.funksem.sunnyportal.utils;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.funksem.sunnyportal.Defines;

public final class CsvUtils
{
    private CsvUtils()
    {
    }

    public static Collection<File> getCsvFiles(String path)
    {
        IOFileFilter filter = FileFilterUtils.suffixFileFilter(Defines.EXTENSION_CSV,
            IOCase.INSENSITIVE);
        return FileUtils.listFiles(new File(path), filter, TrueFileFilter.INSTANCE);
    }
}
