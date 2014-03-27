package com.cdrussell.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ImageConfiguration
{
    private static final String CONFIG_FILENAME = "config.properties"; 
    private static final String PROPERTY_INPUT_DIRECTORY = "inputDirectory";
    private static final String PROPERTY_OUTPUT_DIRECTORY = "outputDirectory";
    private static final String PROPERTY_CREATE_OUTPUT_AUTOMATICALLY = "createOutputDir";
    
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

    public boolean shouldCreateOutput()
    {
        String property = properties.getProperty(PROPERTY_CREATE_OUTPUT_AUTOMATICALLY, "false");
        return Boolean.parseBoolean(property);
    }
}
