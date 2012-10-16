package org.generationcp.middleware.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * <p>
 * A {@link HibernateSessionProvider} implementation that follows the
 * Session-Per-Thread model.
 * </p>
 * <p>
 * {@link HibernateSessionProvider#getSession()} is implemented to open a new
 * session if no session has been previously created for the current thread.
 * </p>
 * <p>
 * Users of this {@link HibernateSessionProvider} must call the
 * {@link HibernateSessionPerThreadProvider#close()} to close the
 * {@link Session} before the calling {@link Thread} ends.
 * </p>
 * 
 * @author Glenn Marintes
 */
public class HibernateSessionPerThreadProvider implements HibernateSessionProvider {
    private SessionFactory sessionFactory;
    
    private final ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<Session>();
    
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
     * <p>
     * This implementation will close the {@link Session} for the calling
     * {@link Thread} only.
     * </p>
     * <p>
     * Users of {@link HibernateSessionPerThreadProvider} must be careful that
     * they are calling {@link HibernateSessionPerThreadProvider#close()} from
     * the right thread.
     * </p>
     * <p>
     * IMPORTANT: Calling this method WILL NOT CLOSE all open sessions. To avoid
     * leaking Sessions, always call this method for each thread that has called
     * {@link HibernateSessionPerThreadProvider#getSession()}.
     * </p>
     */
    @Override
    public void close() {
        Session session = THREAD_SESSION.get();
        if (session != null) {
            try {
                session.close();
            }
            finally {
                THREAD_SESSION.remove();
            }
        }
    }
}
