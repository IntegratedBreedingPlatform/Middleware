package org.generationcp.middleware.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.exceptions.ConfigException;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HibernateUtilFactory {
    private final static Logger LOG = LoggerFactory.getLogger(HibernateUtilFactory.class);

    public static HibernateUtil getHibernateUtilForLocal() {
        Properties prop = new Properties();

        try {
            InputStream in = null;

            try {
                in = new FileInputStream(new File(ResourceFinder.locateFile("IBPDatasource.properties").toURI()));
            }
            catch (IllegalArgumentException ex) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("IBPDatasource.properties");
            }
            prop.load(in);

            String localHost = prop.getProperty("local.host");
            String localPort = prop.getProperty("local.port");
            String localDbname = prop.getProperty("local.dbname");
            String localUsername = prop.getProperty("local.username");
            String localPassword = prop.getProperty("local.password");

            in.close();

            return new HibernateUtil(localHost, localPort, localDbname, localUsername, localPassword);
        }
        catch (URISyntaxException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (HibernateException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (ConfigException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (IOException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }

        return null;
    }

    public static HibernateUtil getHibernateUtilForCentral() {
        Properties prop = new Properties();

        try {
            InputStream in = null;

            try {
                in = new FileInputStream(new File(ResourceFinder.locateFile("IBPDatasource.properties").toURI()));
            }
            catch (IllegalArgumentException ex) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("IBPDatasource.properties");
            }
            prop.load(in);

            String centralHost = prop.getProperty("central.host");
            String centralPort = prop.getProperty("central.port");
            String centralDbname = prop.getProperty("central.dbname");
            String centralUsername = prop.getProperty("central.username");
            String centralPassword = prop.getProperty("central.password");

            in.close();

            return new HibernateUtil(centralHost, centralPort, centralDbname, centralUsername, centralPassword);
        }
        catch (URISyntaxException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (HibernateException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (ConfigException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        catch (IOException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        
        return null;
    }
}
