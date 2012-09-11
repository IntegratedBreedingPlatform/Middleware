package org.generationcp.middleware.util;

import java.io.FileNotFoundException;
import java.net.URL;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SessionFactoryUtil {
    private final static Logger LOG = LoggerFactory.getLogger(SessionFactoryUtil.class);
    
    private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";
    
    public static SessionFactory openSessionFactory(DatabaseConnectionParameters params) throws FileNotFoundException {
        String connectionUrl = String.format("jdbc:mysql://%s:%s/%s", params.getHost()
                                             , params.getPort()
                                             , params.getDbName()
                                             );

        URL urlOfCfgFile = ResourceFinder.locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

        AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
        cfg.setProperty("hibernate.connection.url", connectionUrl);
        cfg.setProperty("hibernate.connection.username", params.getUsername());
        cfg.setProperty("hibernate.connection.password", params.getPassword());

        LOG.info("Opening SessionFactory for local database...");
        return cfg.buildSessionFactory();
    }
}
