
package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

/**
 * Dao Factory for ontology
 */

public class OntologyDaoFactory {

	private HibernateSessionProvider sessionProvider;

	public OntologyDaoFactory(HibernateSessionProvider sessionProvider) {
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
}
