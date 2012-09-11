package org.generationcp.middleware.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A {@link HibernateSessionProvider} implementation that follows the
 * Session-Per-Thread model.<br>
 * <br>
 * {@link HibernateSessionProvider#getSession()} is implemented to open a new
 * session if no session has been previously created for the current thread yet.<br>
 * <br>
 * Users of this {@link HibernateSessionProvider} are tasked to close the
 * {@link Session} when the calling thread ends.<br>
 * 
 * @author Glenn Marintes
 */
public class HibernateSessionPerThreadProvider implements HibernateSessionProvider {
    private SessionFactory sessionFactory;
    
    private final static ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<Session>();
    
    public HibernateSessionPerThreadProvider() {
    }
    
    public HibernateSessionPerThreadProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Session getSession() {
        Session session = THREAD_SESSION.get();
        
        if (session == null) {
            session = sessionFactory.openSession();
            THREAD_SESSION.set(session);
        }
        
        return session;
    }
    
    /**
     * This implementation does nothing.<br>
     * One limitation of having a Session-Per-Thread model is that we do not
     * know when a Thread ends so we do not know which Session we need to close.
     */
    @Override
    public void close() {
    }
}
