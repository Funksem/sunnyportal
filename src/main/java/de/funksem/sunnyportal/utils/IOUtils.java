package de.funksem.sunnyportal.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

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

    public static boolean isDir(String directory)
    {
        if (StringUtils.isBlank(directory))
        {
            return false;
        }

        Path sourcePath = Paths.get(directory);

        if (Files.isDirectory(sourcePath))
        {
            return true;
        }
        return false;
    }

}