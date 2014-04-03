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
    private static SimpleDateFormat dateFormatter;
    private static ImageConfiguration configuration;

    public enum Error
    {
        NO_PARAMETERS(1), INVALID_INPUT_DIRECTORY(2), INVALID_OUTPUT_DIRECTORY(3);

        private int value;

        private Error(int value)
        {
            this.value = value;
        }
    }

    private static final String[] IMAGE_EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg", "jpeg", "mp4", "3gp" };
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
     * Copies images and videos from the input directory to output directory. 
     * The output directory is split into sub-directories based on the date taken of the images/videos.
     * If EXIF data exists, that will be used. Otherwise the last modified timestamp will be used.
     * 
     * Loads the configuration from the config.properties which should be in the root of the project.
     * @param args Not used
     * @throws IOException
     * @throws ImageProcessingException
     */
    public static void main(String[] args) throws IOException
    {
        configuration = new ImageConfiguration();
        configuration.loadProperties();

        dateFormatter = new SimpleDateFormat(configuration.getDateFormat());
        
        File inputImageDirectory = configuration.getInputDirectory();
        File outputImageDirectory = configuration.getOutputDirectory();

        DirectoryValidator.ensureValidInputDirectory(inputImageDirectory);
        DirectoryValidator.ensureValidOutputDirectory(outputImageDirectory);

        System.out.println("Processing directory: " + inputImageDirectory.getAbsolutePath());

        List<File> imagesSuccessfullyProcessed = copyImagesToOutput(inputImageDirectory, outputImageDirectory);

        System.out.println("Copied all files");

        if (configuration.shouldDeleteOriginals())
        {
            System.out.println("Will delete originals");
            deleteFiles(imagesSuccessfullyProcessed);
        }
        
        System.out.println("Copied " + imagesSuccessfullyProcessed.size() + " files");
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

                assert (DirectoryValidator.isValidDirectory(finalDirectory, true));

                FileUtils.copyFile(image, new File(finalDirectory, image.getName()));

                imagesSuccessfullyProcessed.add(image);
            }
            catch (ImageProcessingException | IOException e)
            {
                System.err.println("\nFailed to process file " + image.getAbsolutePath());
            }
        }
        return imagesSuccessfullyProcessed;
    }

    /**
     * Deletes the given list of Files
     * @param files The list of files to be deleted.
     */
    private static void deleteFiles(List<File> files)
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
