package com.cdrussell.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ImageConfiguration
{
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    private static final String CONFIG_FILENAME = "config.properties"; 
    
    private static final String PROPERTY_INPUT_DIRECTORY = "inputDirectory";
    private static final String PROPERTY_OUTPUT_DIRECTORY = "outputDirectory";
    private static final String PROPERTY_DELETE_ORIGINALS = "deleteOriginals";
    private static final String PROPERTY_DATE_FORMAT = "dateFormat";
    
    public static final String INVALID_PARAMETERS_ERROR_MESSAGE = " You must supply the input directory as parameter 1, and the output directory as parameter 2";
    
    private final Properties properties = new Properties();
    
    public void loadProperties() throws IOException
    {
        InputStream propertiesInputStream = new FileInputStream(CONFIG_FILENAME);
        properties.load(propertiesInputStream);
    }

    public File getInputDirectory()
    {
        String property = properties.getProperty(PROPERTY_INPUT_DIRECTORY);
        return new File(property);
    }
    
    public File getOutputDirectory()
    {
        String property = properties.getProperty(PROPERTY_OUTPUT_DIRECTORY);
        return new File(property);
    }
    
    public boolean shouldDeleteOriginals()
    {
        String property = properties.getProperty(PROPERTY_DELETE_ORIGINALS, "false");
        return Boolean.parseBoolean(property);
    }
    
    public String getDateFormat()
    {
        return properties.getProperty(PROPERTY_DATE_FORMAT, DEFAULT_DATE_FORMAT);
    }
}
