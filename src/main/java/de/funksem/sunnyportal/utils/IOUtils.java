package de.funksem.sunnyportal.utils;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public final class IOUtils
{

    private IOUtils()
    {
    }

    public static Collection<File> getFiles(String path, String extension)
    {
        IOFileFilter filter = FileFilterUtils.suffixFileFilter(extension, IOCase.INSENSITIVE);
        return FileUtils.listFiles(new File(path), filter, TrueFileFilter.INSTANCE);
    }

}