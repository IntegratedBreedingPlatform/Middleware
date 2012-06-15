/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * This class has utility methods used by other classes.
 * 
 * @author Kevin Manansala
 * 
 */
public class ResourceFinder {
    /**
     * This method is written by Martin Senger. This is taken from the
     * org.generationcp.core.utils.Commons class from the PantheonConfig
     * project.
     * 
     * Return the location of the specified resource by searching the current
     * directory, user home directory, the current classpath and the system
     * classpath.
     * <p>
     * 
     * The code was inspired by Apache Commons class <tt>ConfigurationUtils</tt>
     * (authors Herve Quiroz, Oliver Heger, and Emmanuel Bourg).
     * <p>
     * 
     * @param name
     *            the name of the resource - usually a file name (absolute or
     *            relative)
     * 
     * @return a URL of the resource location
     * 
     * @throws FileNotFoundException
     *             if the resource cannot be found; or if the 'name' is null or
     *             otherwise invalid
     **/
    public static URL locateFile(String name) throws FileNotFoundException {

	if (name == null)
	    throw new FileNotFoundException("Null file name.");

	// attempt to create a URL directly
	try {
	    return new URL(name);
	} catch (IOException e) {
	    // let's make another attempt
	}

	// attempt to load from an absolute path, or from the current directory
	File file = new File(name);
	if (file.isAbsolute() || file.exists()) {
	    try {
		return file.toURI().toURL();
	    } catch (IOException e) {
		throw new FileNotFoundException("Malformed path: " + name);
	    }
	}

	// attempt to load from the user home directory
	try {
	    StringBuilder fName = new StringBuilder();
	    fName.append(System.getProperty("user.home"));
	    fName.append(File.separator);
	    fName.append(name);
	    file = new File(fName.toString());
	    if (file.exists())
		return file.toURI().toURL();
	} catch (IOException e) {
	    // let's make another attempt
	}

	// attempt to load from the context classpath
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	URL url = loader.getResource(name);
	if (url != null)
	    return url;

	// attempt to load from the system classpath
	url = ClassLoader.getSystemResource(name);
	if (url != null)
	    return url;

	// if all fails
	throw new FileNotFoundException(name);
    }

}
