/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.support.servlet;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.hibernate.SessionFactory;

/**
 * <p>
 * This class is intended for users that want to integrate the Middleware in a Servlet-API environment.<br>
 * To integrate the Middleware, users need to add this {@link ServletContextListener} implementation together with the
 * {@link MiddlewareServletRequestListener} class to the <code>web.xml</code> file.
 * </p>
 * <p/>
 * 
 * <pre>
 *      &lt;listener&gt;
 *          &lt;listener-class>org.generationcp.middleware.support.servlet.MiddlewareServletContextListener&lt;/listener-class&gt;
 *      &lt;/listener&gt;
 *      &lt;listener&gt;
 *          &lt;listener-class&gt;org.generationcp.middleware.support.servlet.MiddlewareServletRequestListener&lt;/listener-class&gt;
 *      &lt;/listener&gt;
 * </pre>
 * <p/>
 * <p>
 * This implementation of {@link ServletContextListener} takes care of creating a {@link SessionFactory} instance when a
 * {@link ServletContext} is initialized and closing the {@link SessionFactory} when the {@link ServletContext} is destroyed.
 * </p>
 * <p>
 * The Hibernate configuration filename and the database property files can be set as context parameters in the <code>web.xml</code> file.
 * </p>
 * <p/>
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

	public final static String ATTR_WORKBENCH_SESSION_FACTORY = "WORKBENCH_SESSION_FACTORY";

	public final static String PARAM_DATABASE_PROPERTY_FILE = "database_property_file";

	private SessionFactory sessionFactoryForWorkbench;

	public static SessionFactory getWorkbenchSessionFactoryForRequestEvent(ServletRequestEvent event) {
		return (SessionFactory) event.getServletContext().getAttribute(MiddlewareServletContextListener.ATTR_WORKBENCH_SESSION_FACTORY);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();

		String workbenchPropertyFilename = context.getInitParameter(MiddlewareServletContextListener.PARAM_DATABASE_PROPERTY_FILE);

		if (workbenchPropertyFilename == null) {
			throw new RuntimeException(
					"'workbench_database_property_file' context parameter not declared! It must be set to the filename of a workbench database property file.");
		}

		Exception exception = null;
		try {
			DatabaseConnectionParameters paramsForWorkbench = new DatabaseConnectionParameters(workbenchPropertyFilename, "workbench");

			this.sessionFactoryForWorkbench = SessionFactoryUtil.openSessionFactory(paramsForWorkbench);

			context.setAttribute(MiddlewareServletContextListener.ATTR_WORKBENCH_SESSION_FACTORY, this.sessionFactoryForWorkbench);
		} catch (ConfigException | URISyntaxException | IOException e) {
			exception = e;
		}

		if (exception != null) {
			throw new RuntimeException(MiddlewareServletContextListener.class.getName() + " is incorrectly configured.", exception);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (this.sessionFactoryForWorkbench != null) {
			try {
				this.sessionFactoryForWorkbench.close();
				this.sessionFactoryForWorkbench = null;
			} finally {
			}
		}
	}

}
