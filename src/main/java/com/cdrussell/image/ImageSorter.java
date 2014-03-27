package com.cdrussell.image;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class ImageSorter
{
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");

    private static final String INVALID_PARAMETERS_ERROR_MESSAGE = " You must supply the input directory as parameter 1, and the output directory as parameter 2";

    private static ImageConfiguration configuration;

    private enum Error
    {
        NO_PARAMETERS(1), INVALID_INPUT_DIRECTORY(2), INVALID_OUTPUT_DIRECTORY(3);

        private int value;

        private Error(int value)
        {
            this.value = value;
        }
    }

    private static final String[] IMAGE_EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg", "jpeg", "mp4", "3gp"};
    private static FilenameFilter IMAGE_FILTER = new FilenameFilter()
    {
        @Override
        public boolean accept(File directory, String filename)
        {
            final String lowerCaseFilename = filename.toLowerCase();
            for (final String extension : IMAGE_EXTENSIONS)
            {
                if (lowerCaseFilename.endsWith("." + extension))
                {
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * @param args
     * @throws IOException
     * @throws ImageProcessingException
     */
    public static void main(String[] args) throws IOException
    {
        configuration = new ImageConfiguration();
        configuration.loadProperties();

        File inputImageDirectory = configuration.getInputDirectory();
        File outputImageDirectory = configuration.getOutputDirectory();
        boolean createOutputDirectoryAutomatically = configuration.shouldCreateOutput();

        ensureValidInputDirectory(inputImageDirectory);
        ensureValidOutputDirectory(outputImageDirectory, createOutputDirectoryAutomatically);

        System.out.println("Processing directory: " + inputImageDirectory.getAbsolutePath());

        List<File> imagesSuccessfullyProcessed = copyImagesToOutput(inputImageDirectory, outputImageDirectory);

        System.out.println("Moved all files; will remove originals");

        deleteOriginals(imagesSuccessfullyProcessed);
    }

    private static List<File> copyImagesToOutput(File inputImageDirectory, File outputImageDirectory)
    {
        List<File> imagesSuccessfullyProcessed = new ArrayList<File>();

        for (File image : inputImageDirectory.listFiles(IMAGE_FILTER))
        {
            try
            {
                String formattedDate = getDateTakenStringFromFile(image);
                File finalDirectory = new File(outputImageDirectory.getAbsoluteFile() + "/" + formattedDate);
                System.out.println("\nOutput directory is " + finalDirectory.getAbsolutePath());

                assert (isValidDirectory(finalDirectory, true));

                FileUtils.copyFile(image, new File(finalDirectory, image.getName()));

                imagesSuccessfullyProcessed.add(image);
            } catch (ImageProcessingException | IOException e)
            {
                System.err.println("\nFailed to process file " + image.getAbsolutePath());
            }
        }
        return imagesSuccessfullyProcessed;
    }

    private static void ensureValidOutputDirectory(File outputImageDirectory, boolean createOutputDirIfNeeded)
    {
        boolean needsWrite = true;
        if (!isValidDirectory(outputImageDirectory, needsWrite))
        {
            System.err.println("ERROR: " + Error.INVALID_OUTPUT_DIRECTORY + INVALID_PARAMETERS_ERROR_MESSAGE);
            System.exit(Error.INVALID_INPUT_DIRECTORY.ordinal());
            return;
        }
    }

    private static void ensureValidInputDirectory(File inputImageDirectory)
    {
        if (!isValidDirectory(inputImageDirectory, false))
        {
            System.err.println("ERROR: " + Error.INVALID_INPUT_DIRECTORY + INVALID_PARAMETERS_ERROR_MESSAGE);
            System.exit(Error.INVALID_INPUT_DIRECTORY.ordinal());
            return;
        }
    }

    private static void deleteOriginals(List<File> files)
    {
        System.out.println("There are " + files.size() + " images to be deleted");
        if (files.size() == 0)
            return;

        for (File file : files)
        {
            System.out.println("Deleting " + file.getAbsolutePath());
            FileUtils.deleteQuietly(file);
        }
    }

    private static boolean isValidDirectory(File directory, boolean needsWrite)
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

    private static String getDateTakenStringFromFile(File image) throws ImageProcessingException, IOException
    {
        System.out.println("\nProcessing file: " + image.getName());
        Date dateTaken = getDateTaken(image);
        return dateFormatter.format(dateTaken);
    }

    private static Date getDateTaken(File image)
    {
        Date dateTaken;
        if (image.getName().endsWith(".png"))
        {
            return new Date(image.lastModified());
        }
        
        Metadata meta;
        try
        {
            meta = ImageMetadataReader.readMetadata(image);
            ExifSubIFDDirectory directory = meta.getDirectory(ExifSubIFDDirectory.class);
            if (directory == null)
            {
                return new Date(image.lastModified());
            }
            dateTaken = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (dateTaken == null)
            {
                dateTaken = new Date(image.lastModified());
            }
            return dateTaken;
        } 
        catch (ImageProcessingException | IOException e)
        {
           return new Date(image.lastModified());
        }
    }
}
