package org.generationcp.middleware.hibernate;

import org.hibernate.Session;

/**
 * Implementations of this interface allows you to get or open a Hibernate
 * {@link Session}.
 * 
 * @author Glenn Marintes
 */
public interface HibernateSessionProvider {
    
    /**
     * Get a Hibernate {@link Session}.
     * 
     * @return
     */
    public Session getSession();
    
    /**
     * Close this {@link HibernateSessionProvider}.<br>
     * Implementations should clear resources used by this
     * {@link HibernateSessionProvider}.
     */
    public void close();
}
