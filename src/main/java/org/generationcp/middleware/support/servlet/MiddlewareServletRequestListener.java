package org.generationcp.middleware.support.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.hibernate.SessionFactory;

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
    
    /**
     * Get the ManagerFactory associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     */
    public static ManagerFactory getManagerFactoryForRequest(ServletRequest request) {
        return (ManagerFactory) request.getAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY);
    }
    
    /**
     * Get the {@link GermplasmDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     */
    public GermplasmDataManager getGermplasmDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getGermplasmDataManager();
    }
    
    /**
     * Get the {@link GermplasmListManager} instance associated with the
     * specified {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     */
    public GermplasmListManager getGermplasmListManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getGermplasmListManager();
    }

    /**
     * Get the {@link TraitDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     */
    public TraitDataManager getTraitDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getTraitDataManager();
    }

    /**
     * Get the {@link StudyDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     * @throws ConfigException
     */
    public StudyDataManager getStudyDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getStudyDataManager();
    }

    /**
     * Get the {@link InventoryDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     * @throws ConfigException
     */
    public InventoryDataManager getInventoryDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getInventoryDataManager();
    }
    
    /**
     * Get the {@link GenotypicDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     * @throws ConfigException
     */
    public GenotypicDataManager getGenotypicDataManagerForRequest(ServletRequest request) throws ConfigException {
        return getManagerFactoryForRequest(request).getGenotypicDataManager();
    }
    
    /**
     * Get the {@link UserDataManager} instance associated with the specified
     * {@link ServletRequest} instance.
     * 
     * @param request
     * @return
     * @throws ConfigException
     */
    public UserDataManager getUserDataManagerForRequest(ServletRequest request) {
        return getManagerFactoryForRequest(request).getUserDataManager();
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        
        SessionFactory sessionFactoryForLocal = MiddlewareServletContextListener.getLocalSessionFactoryForRequestEvent(event);
        SessionFactory sessionFactoryForCentral = MiddlewareServletContextListener.getCentralSessionFactoryForRequestEvent(event);
        
        if (sessionFactoryForLocal == null && sessionFactoryForCentral == null) {
            throw new RuntimeException("No SessionFactory found for request. See " + MiddlewareServletContextListener.class.getName() + " documentation for reference.");
        }
        
        HibernateSessionProvider sessionProviderForLocal = new HibernateSessionPerRequestProvider(sessionFactoryForLocal);
        HibernateSessionProvider sessionProviderForCentral = new HibernateSessionPerRequestProvider(sessionFactoryForCentral);
        
        ManagerFactory managerFactory = new ManagerFactory();
        managerFactory.setSessionProviderForLocal(sessionProviderForLocal);
        managerFactory.setSessionProviderForCentral(sessionProviderForCentral);
        
        request.setAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY, managerFactory);
    }
    
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        
        ManagerFactory managerFactory = (ManagerFactory) request.getAttribute(ATTR_MIDDLEWARE_SESSION_FACTORY);
        if (managerFactory != null) {
            managerFactory.close();
        }
    }
}
