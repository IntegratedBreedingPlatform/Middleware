/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.util;

import java.io.IOException;
import java.util.Properties;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The utility class for reading properties file (key=value pairs)
 * 
 * @author Joyce Avestro
 *
 */
public class PropertyReader{
    
    private static final Logger LOG = LoggerFactory.getLogger(PropertyReader.class);
    
    private static final String ERROR_MESSAGE = "Error accessing property file: ";

    private Properties configFile;
    
    public PropertyReader(String propertyFile) {
            setConfigFile(propertyFile);
    }

    public void setConfigFile(String propertyFile) { //throws MiddlewareException{
        configFile = new Properties();
        try {
            configFile.load(TermId.class.getClassLoader().getResourceAsStream(propertyFile));
        } catch (IOException e) {
            LOG.error(ERROR_MESSAGE + propertyFile, e); 
        }
    }

    public Properties getConfigFile(String propertyFile) throws MiddlewareException{
        return configFile;
    }
    
    public String getValue(String key){
        return configFile.getProperty(key);        
    }

    public Integer getIntegerValue(String key){
        String value = configFile.getProperty(key);
        Integer intValue = null;
        if (value != null && isInt(value)) {
            intValue = Integer.valueOf(value);
        }
        if (intValue == null){
            LOG.error("Value not found for key: " + key);
        }
        return intValue;
    }
    
    public boolean isInt(String key){
        String value = key.trim();
        if (value != null) {
            try { 
                Integer.valueOf(value);
            } catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }
    
}
