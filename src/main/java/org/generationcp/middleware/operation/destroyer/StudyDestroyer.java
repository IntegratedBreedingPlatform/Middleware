
package org.generationcp.middleware.operation.destroyer;

import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.util.Util;

public class StudyDestroyer extends Destroyer {

	public StudyDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteStudy(int studyId) throws MiddlewareQueryException {
		DmsProject study = this.getDmsProjectDao().getById(studyId);
		this.renameStudyAndDatasets(study);
		this.updateStudyStatusToDeleted(study);
		this.deleteRelationshipsIfNotAStudy(study);
	}

	private void updateStudyStatusToDeleted(DmsProject study) throws MiddlewareQueryException {
		if (null != study) {
			study.setDeleted(true);
			this.getDmsProjectDao().save(study);
		}
	}

	protected void deleteRelationshipsIfNotAStudy(DmsProject study) throws MiddlewareQueryException {
		boolean isAStudy = this.getProjectRelationshipDao().isSubjectTypeExisting(study.getProjectId(), TermId.IS_STUDY.getId());
		if (!isAStudy) {
			this.getProjectRelationshipDao().deleteByProjectId(study.getProjectId());
		}
	}

	private void renameStudyAndDatasets(DmsProject study) throws MiddlewareQueryException {
		String tstamp = Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS");
		study.setName(study.getName() + "#" + tstamp);
		this.getDmsProjectDao().save(study);

		List<DmsProject> datasets =
				this.getProjectRelationshipDao().getSubjectsByObjectIdAndTypeId(study.getProjectId(), TermId.BELONGS_TO_STUDY.getId());
		if (datasets != null) {
			for (DmsProject dataset : datasets) {
				dataset.setName(dataset.getName() + "#" + tstamp);
				dataset.setDeleted(true);
				this.getDmsProjectDao().save(dataset);
			}
		}
	}
}
