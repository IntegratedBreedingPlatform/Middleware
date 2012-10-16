package org.generationcp.middleware.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * <p>
 * A {@link HibernateSessionProvider} implementation that is aimed to achieve
 * the Session-Per-Request model.
 * </p>
 * <p>
 * {@link HibernateSessionProvider#getSession()} is implemented to open a new
 * session if no session has been previously created.
 * </p>
 * <p>
 * When this {@link HibernateSessionProvider} is closed, the associated
 * {@link Session} is also closed.
 * </p>
 * 
 * @author Glenn Marintes
 */
public class HibernateSessionPerRequestProvider implements HibernateSessionProvider {
    private SessionFactory sessionFactory;
    
    private Session session;
    
    public HibernateSessionPerRequestProvider() {
    }
    
    public HibernateSessionPerRequestProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public synchronized void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public synchronized Session getSession() {
        if (session != null) {
            return session;
            
        }
        
        session = sessionFactory == null ? null : sessionFactory.openSession();
        return session;
    }
    
    @Override
    public void close() {
        if (session != null) {
            session.close();
            session = null;
        }
    }
}
