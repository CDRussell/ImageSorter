package com.cdrussell.image;

import static com.cdrussell.image.ImageConfiguration.*;

import java.io.File;

import com.cdrussell.image.ImageSorter.Error;

public class DirectoryValidator
{
    public static void ensureValidOutputDirectory(File outputImageDirectory)
    {
        boolean needsWrite = true;
        if (!isValidDirectory(outputImageDirectory, needsWrite))
        {
            System.err.println("ERROR: " + Error.INVALID_OUTPUT_DIRECTORY + INVALID_PARAMETERS_ERROR_MESSAGE);
            System.exit(Error.INVALID_INPUT_DIRECTORY.ordinal());
            return;
        }
    }

    public static void ensureValidInputDirectory(File inputImageDirectory)
    {
        if (!isValidDirectory(inputImageDirectory, false))
        {
            System.err.println("ERROR: " + Error.INVALID_INPUT_DIRECTORY + INVALID_PARAMETERS_ERROR_MESSAGE);
            System.exit(Error.INVALID_INPUT_DIRECTORY.ordinal());
            return;
        }
    }
    
    public static boolean isValidDirectory(File directory, boolean needsWrite)
    {
        if (directory == null)
        {
            System.out.println("Null directories not permitted");
            return false;
        }

        if (!directory.exists())
        {
            System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            return false;
        }

        if (!directory.isDirectory())
        {
            System.out.println("Not a directory: " + directory.getAbsolutePath());
            return false;
        }

        if (!directory.canRead())
        {
            System.out.println("Not readable: " + directory.getAbsolutePath());
            return false;
        }

        if (needsWrite && !directory.canWrite())
        {
            System.out.println("Not writable: " + directory.getAbsolutePath());
            return false;
        }

        return true;
    }

}
