
package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
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

	public Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}

	public CVDao getCvDao() {
		CVDao cvDao = new CVDao();
		cvDao.setSession(this.getActiveSession());
		return cvDao;
	}

	public CVTermDao getCvTermDao(){
		CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.getActiveSession());
		return cvTermDao;
	}

	public CvTermPropertyDao getCvTermPropertyDao(){
		CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.getActiveSession());
		return cvTermPropertyDao;
	}

	public CVTermRelationshipDao getCvTermRelationshipDao(){
		CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.getActiveSession());
		return cvTermRelationshipDao;
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.getActiveSession());
		return cvTermSynonymDao;
	}

	public VariableOverridesDao getVariableProgramOverridesDao() {
		VariableOverridesDao variableOverridesDao = new VariableOverridesDao();
		variableOverridesDao.setSession(this.getActiveSession());
		return variableOverridesDao;
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		ProgramFavoriteDAO programFavoriteDAO = new ProgramFavoriteDAO();
		programFavoriteDAO.setSession(this.getActiveSession());
		return programFavoriteDAO;
	}

	public DmsProjectDao getDmsProjectDao() {
		DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.getActiveSession());
		return dmsProjectDao;
	}

	public ExperimentDao getExperimentDao() {
		ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(this.getActiveSession());
		return experimentDao;
	}
}
