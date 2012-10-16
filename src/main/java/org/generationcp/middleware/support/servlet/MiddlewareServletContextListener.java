package org.generationcp.middleware.support.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * <p>
 * This class is intended for users that want to integrate the Middleware in a
 * Servlet-API environment.<br>
 * To integrate the Middleware, users need to add this
 * {@link ServletContextListener} implementation together with the
 * {@link MiddlewareServletRequestListener} class to the <code>web.xml</code>
 * file.
 * </p>
 * 
 * <pre>
 *      &lt;listener&gt;
 *          &lt;listener-class>org.generationcp.middleware.support.servlet.MiddlewareServletContextListener&lt;/listener-class&gt;
 *      &lt;/listener&gt;
 *      &lt;listener&gt;
 *          &lt;listener-class&gt;org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener&lt;/listener-class&gt;
 *      &lt;/listener&gt;
 * </pre>
 * 
 * <p>
 * This implementation of {@link ServletContextListener} takes care of creating
 * a {@link SessionFactory} instance when a {@link ServletContext} is
 * initialized and closing the {@link SessionFactory} when the
 * {@link ServletContext} is destroyed.
 * </p>
 * <p>
 * The Hibernate configuration filename and the database property files can be
 * set as context parameters in the <code>web.xml</code> file.
 * </p>
 * 
 * <pre>
 *      &lt;context-param&gt;
 *          &lt;param-name&gt;middleware_database_property_file&lt;/param-name&gt;
 *          &lt;param-value&gt;IBPDatasource.properties&lt;/param-value&gt;
 *      &lt;/context-param&gt;
 *      &lt;context-param&gt;
 *          &lt;param-name&gt;middleware_hibernate_config_file&lt;/param-name&gt;
 *          &lt;param-value&gt;ibpmidware_hib.cfg.xml&lt;/param-value&gt;
 *      &lt;/context-param&gt;
 * </pre>
 * 
 * @author Glenn Marintes
 */
public class MiddlewareServletContextListener implements ServletContextListener {
    public final static String ATTR_MIDDLEWARE_LOCAL_SESSION_FACTORY        = "MIDDLEWARE_LOCAL_SESSION_FACTORY";
    public final static String ATTR_MIDDLEWARE_CENTRAL_SESSION_FACTORY      = "MIDDLEWARE_CENTRAL_SESSION_FACTORY";
    
    private final static String PARAM_MIDDLEWARE_HIBERNATE_CONFIG           = "middleware_hibernate_config_file";
    private final static String PARAM_MIDDLEWARE_DATABASE_PROPERTY_FILE     = "middleware_database_property_file";
    
    private SessionFactory sessionFactoryForLocal;
    private SessionFactory sessionFactoryForCentral;
    
    public static SessionFactory getLocalSessionFactoryForRequestEvent(ServletRequestEvent event) {
        return (SessionFactory) event.getServletContext().getAttribute(ATTR_MIDDLEWARE_LOCAL_SESSION_FACTORY);
    }
    
    public static SessionFactory getCentralSessionFactoryForRequestEvent(ServletRequestEvent event) {
        return (SessionFactory) event.getServletContext().getAttribute(ATTR_MIDDLEWARE_CENTRAL_SESSION_FACTORY);
    }
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        
        String hibernateConfigurationFilename = context.getInitParameter(PARAM_MIDDLEWARE_HIBERNATE_CONFIG);
        String databasePropertyFilename = context.getInitParameter(PARAM_MIDDLEWARE_DATABASE_PROPERTY_FILE);
        
        if (databasePropertyFilename == null) {
            throw new RuntimeException("'middleware_database_property_file' context parameter not declared! It must be set to the filename of a middleware database property file.");
        }
        
        Exception exception = null;
        try {
            DatabaseConnectionParameters paramsForLocal = new DatabaseConnectionParameters(databasePropertyFilename, "local");
            DatabaseConnectionParameters paramsForCentral = new DatabaseConnectionParameters(databasePropertyFilename, "central");
            
            sessionFactoryForLocal = SessionFactoryUtil.openSessionFactory(hibernateConfigurationFilename, paramsForLocal);
            sessionFactoryForCentral = SessionFactoryUtil.openSessionFactory(hibernateConfigurationFilename, paramsForCentral);
            
            context.setAttribute(ATTR_MIDDLEWARE_LOCAL_SESSION_FACTORY, sessionFactoryForLocal);
            context.setAttribute(ATTR_MIDDLEWARE_CENTRAL_SESSION_FACTORY, sessionFactoryForCentral);
        }
        catch (FileNotFoundException e) {
            exception = e;
        }
        catch (ConfigException e) {
            exception = e;
        }
        catch (URISyntaxException e) {
            exception = e;
        }
        catch (IOException e) {
            exception = e;
        }
        
        if (exception != null) {
            throw new RuntimeException(MiddlewareServletContextListener.class.getName() + " is incorrectly configured.", exception);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (sessionFactoryForLocal != null) {
            try {
                sessionFactoryForLocal.close();
            }
            finally {
            }
        }
        
        if (sessionFactoryForCentral != null) {
            try {
                sessionFactoryForCentral.close();
            }
            finally {
            }
        }
    }
    
}
