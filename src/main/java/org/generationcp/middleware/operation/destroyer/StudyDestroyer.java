
package org.generationcp.middleware.operation.destroyer;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Util;

public class StudyDestroyer extends Destroyer {

	public StudyDestroyer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteStudy(final int studyId) {
		final DmsProject study = this.getDmsProjectDao().getById(studyId);
		this.renameStudyAndDatasets(study);
		this.updateStudyStatusToDeleted(study);
	}

	private void updateStudyStatusToDeleted(final DmsProject study) {
		if (null != study) {
			study.setDeleted(true);
			this.getDmsProjectDao().save(study);
		}
	}

	private void renameStudyAndDatasets(final DmsProject study) {
		final String tstamp = Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS");
		study.setName(study.getName() + "#" + tstamp);
		this.getDmsProjectDao().save(study);

		final List<DmsProject> datasets = this.getDmsProjectDao().getDatasetsByParent(study.getProjectId());
		if (datasets != null) {
			for (final DmsProject dataset : datasets) {
				dataset.setName(dataset.getName() + "#" + tstamp);
				dataset.setDeleted(true);
				this.getDmsProjectDao().save(dataset);
			}
		}
	}
}
