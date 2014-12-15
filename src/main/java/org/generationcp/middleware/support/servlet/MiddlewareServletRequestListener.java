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
package org.generationcp.middleware.support.servlet;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.*;
import org.hibernate.SessionFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * <p>
 * This class is intended for users that want to integrate the Middleware in a
 * Servlet-API environment.<br>
 * To integrate the Middleware, users need to add this
 * {@link ServletRequestListener} implementation together with the
 * {@link MiddlewareServletContextListener} class to the <code>web.xml</code>
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
 * This implementation of {@link ServletRequestListener} takes care of creating
 * and associating an instance of {@link ManagerFactory} to a
 * {@link ServletRequest}. When the request is finished, this implementation
 * also takes care of closing the associated {@link ManagerFactory}
 * </p>
 * <p>
 * The {@link SessionFactory} is expected to be provided by the
 * {@link MiddlewareServletContextListener} and that is why users will always
 * need to use this class together with the
 * {@link MiddlewareServletContextListener}.
 * </p>
 * 
 * @author Glenn Marintes
 */
public class MiddlewareServletRequestListener implements ServletRequestListener {
    public final static String ATTR_MIDDLEWARE_SESSION_FACTORY = "MIDDLEWARE_SESSION_FACTORY";
    public static final String ATTR_WORKBENCH_DATAMANAGER = "WORKBENCH_MANAGER";
    public static final String ATTR_WORKBENCH_SESSION_PROVIDER = "WORKBENCH_SESSION_PROVIDER";
    
    /**
     * Get the ManagerFactory associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The ManagerFactory for the given Servlet Request
     */
    public static ManagerFactory getManagerFactoryForRequest(ServletRequest request) {
        return (ManagerFactory) request.getAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY);
    }

    public static WorkbenchDataManager getWorkbenchManagerForRequest(ServletRequest request) {
        return (WorkbenchDataManager) request.getAttribute(ATTR_WORKBENCH_DATAMANAGER);
    }
    
    /**
     * Get the {@link GermplasmDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The GermplasmDataManager for the given request
     */
    public static GermplasmDataManager getGermplasmDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getGermplasmDataManager();
    }
    
    /**
     * Get the {@link GermplasmListManager} instance associated with the
     * specified {@link ServletRequest} instance.
     * 
     * @param request
     * @return The GermplasmListManager for the given request
     */
    public static GermplasmListManager getGermplasmListManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getGermplasmListManager();
    }

    /**
     * Get the {@link OntologyDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The OntologyDataManager for the given request
     */
    public static OntologyDataManager getOntologyDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getOntologyDataManager();
    }

    /**
     * Get the {@link StudyDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The StudyDataManager for the given request
     * @throws ConfigException
     */
    public static StudyDataManager getStudyDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getStudyDataManager();
    }

    /**
     * Get the {@link InventoryDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The InventoryDataManager for the given request
     * @throws ConfigException
     */
    public static InventoryDataManager getInventoryDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getInventoryDataManager();
    }
    
    /**
     * Get the {@link GenotypicDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return The GenotypicDataManager for the given request
     * @throws ConfigException
     */
    public static GenotypicDataManager getGenotypicDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getGenotypicDataManager();
    }
    
    /**
     * Get the {@link UserDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return  The UserDataManager for the given request
     * @throws ConfigException
     */
    public static UserDataManager getUserDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getUserDataManager();
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        
        SessionFactory sessionFactory = MiddlewareServletContextListener.getSessionFactoryForRequestEvent(event);
        SessionFactory sessionFactoryForWorkbench = MiddlewareServletContextListener.getWorkbenchSessionFactoryForRequestEvent(event);
        
        if (sessionFactory == null) {
            throw new RuntimeException("No SessionFactory found for request. See " + MiddlewareServletContextListener.class.getName() + " documentation for reference.");
        }
        
        HibernateSessionProvider sessionProvider = new HibernateSessionPerRequestProvider(sessionFactory);
        HibernateSessionProvider sessionProviderForWorkbench = new HibernateSessionPerRequestProvider(sessionFactoryForWorkbench);
        
        ManagerFactory managerFactory = new ManagerFactory();
        managerFactory.setSessionProvider(sessionProvider);

        WorkbenchDataManager workbenchDataManager = new WorkbenchDataManagerImpl(sessionProviderForWorkbench);
        
        request.setAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY, managerFactory);
        request.setAttribute(ATTR_WORKBENCH_DATAMANAGER, workbenchDataManager);
        request.setAttribute(ATTR_WORKBENCH_SESSION_PROVIDER, sessionProviderForWorkbench);
    }
    
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        
        ManagerFactory managerFactory = (ManagerFactory) request.getAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY);
        if (managerFactory != null) {
            managerFactory.close();
        }

        HibernateSessionProvider workbenchSessionProvider = (HibernateSessionProvider) request.getAttribute(ATTR_WORKBENCH_SESSION_PROVIDER);
        workbenchSessionProvider.close();
    }
}
