package org.generationcp.middleware.v2.manager;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.hibernate.Session;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private DmsProjectDao dmsProjectDao;

	public StudyDataManagerImpl() { }

	public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	private DmsProjectDao getDmsProjectDao() {
		if (dmsProjectDao == null) {
			dmsProjectDao = new DmsProjectDao();
		}
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}
	
	private StudyFactory getStudyFactory() {
		return StudyFactory.getInstance();
	}

	@Override
	public StudyDetails getStudyDetails(int studyId) throws MiddlewareQueryException {
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				return getStudyFactory().createStudyDetails(project);
			}
		}
		return null;
	}
}
