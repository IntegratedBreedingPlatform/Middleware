
package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Util;

import java.util.List;

public class StudyDestroyer extends Destroyer {

	private DaoFactory daoFactory;

	public StudyDestroyer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public void deleteStudy(final int studyId) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		this.renameStudyAndDatasets(study);
		this.updateStudyStatusToDeleted(study);
	}

	private void updateStudyStatusToDeleted(final DmsProject study) {
		if (null != study) {
			study.setDeleted(true);
			this.daoFactory.getDmsProjectDAO().save(study);
		}
	}

	private void renameStudyAndDatasets(final DmsProject study) {
		final String tstamp = Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS");
		study.setName(study.getName() + "#" + tstamp);
		this.daoFactory.getDmsProjectDAO().save(study);

		final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO().getDatasetsByParent(study.getProjectId());
		if (datasets != null) {
			for (final DmsProject dataset : datasets) {
				dataset.setName(dataset.getName() + "#" + tstamp);
				dataset.setDeleted(true);
				this.daoFactory.getDmsProjectDAO().save(dataset);
			}
		}
	}
}
