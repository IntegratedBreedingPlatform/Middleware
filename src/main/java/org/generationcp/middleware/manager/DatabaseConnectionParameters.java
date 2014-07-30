/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.util.ResourceFinder;

/**
 * Database connection parameters: host, port, dbName, url, driverName, userName and password.
 *
 */
public class DatabaseConnectionParameters{

    private String host;
    private String port;
    private String dbName;
    private String url;
    private String driverName;
    private String username;
    private String password;

    public DatabaseConnectionParameters(String driverName, String url, String username, String password) {

        this.driverName = driverName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public DatabaseConnectionParameters(String host, String port, String dbName, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }

    public DatabaseConnectionParameters(String propertyFile, String key) throws FileNotFoundException, URISyntaxException, IOException,
            ConfigException {
        super();
        InputStream in = new FileInputStream(new File(ResourceFinder.locateFile(propertyFile).toURI()));
        Properties prop = new Properties();
        prop.load(in);

        String host = prop.getProperty(key + ".host", null);
        if (host == null) {
            throw new ConfigException("Missing property: " + key + ".host from file: " + propertyFile);
        }

        String port = prop.getProperty(key + ".port", null);
        if (port == null) {
            throw new ConfigException("Missing property: " + key + ".port from file: " + propertyFile);
        }

        String dbname = prop.getProperty(key + ".dbname", null);
        if (dbname == null) {
            throw new ConfigException("Missing property: " + key + ".dbname from file: " + propertyFile);
        }

        String username = prop.getProperty(key + ".username", null);
        if (username == null) {
            throw new ConfigException("Missing property: " + key + ".username from file: " + propertyFile);
        }

        String password = prop.getProperty(key + ".password", null);
        if (password == null) {
            throw new ConfigException("Missing property: " + key + ".password from file: " + propertyFile);
        }

        String driverName = prop.getProperty(key + ".driverName", null);
        if (driverName == null) {
            driverName = "com.mysql.jdbc.Driver";
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;

        this.host = host;
        this.port = port;
        this.dbName = dbname;
        this.username = username;
        this.password = password;
        this.driverName = driverName;
        this.url = url;

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}
