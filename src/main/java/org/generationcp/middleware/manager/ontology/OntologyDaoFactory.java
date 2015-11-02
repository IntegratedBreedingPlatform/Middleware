
package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.hibernate.Session;

/**
 * Dao Factory for ontology
 */

public class OntologyDaoFactory {

	private HibernateSessionProvider sessionProvider;

	public OntologyDaoFactory() {

	}

	public void setSessionProvider(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public CVTermDao getCvTermDao(){
		CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.sessionProvider.getSession());
		return cvTermDao;
	}

	public CvTermPropertyDao getCvTermPropertyDao(){
		CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.sessionProvider.getSession());
		return cvTermPropertyDao;
	}

	public CVTermRelationshipDao getCvTermRelationshipDao(){
		CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.sessionProvider.getSession());
		return cvTermRelationshipDao;
	}

	public CVDao getCvDao() {
		CVDao cvDao = new CVDao();
		cvDao.setSession(this.sessionProvider.getSession());
		return cvDao;
	}

	public Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}
}
