
package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.util.Util;

import java.util.List;

public class StudyDestroyer extends Destroyer {

	public StudyDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteStudy(int studyId) throws MiddlewareQueryException {
		DmsProject study = this.getDmsProjectDao().getById(studyId);
		this.renameStudyAndDatasets(study);

		if (study.getProperties() != null && !study.getProperties().isEmpty()) {
			this.updateStudyStatusToDeleted(study);
		}

		this.deleteRelationshipsIfNotAStudy(study);
	}

	private void updateStudyStatusToDeleted(DmsProject study) throws MiddlewareQueryException {
		int maxRank = 0;
		boolean found = false;
		for (ProjectProperty property : study.getProperties()) {
			if (property.getTypeId().equals(TermId.STUDY_STATUS.getId())) {
				found = true;
				if (property.getValue() == null || !property.getValue().equals(String.valueOf(TermId.DELETED_STUDY.getId()))) {
					property.setValue(String.valueOf(TermId.DELETED_STUDY.getId()));
					this.getProjectPropertyDao().saveOrUpdate(property);
				}
				break;
			}
			if (property.getRank() != null && property.getRank() > maxRank) {
				maxRank = property.getRank();
			}
		}
		if (!found) {
			// create a study status using the maxRank
			VariableTypeList typeList = new VariableTypeList();
			StandardVariable statusDeletedTerm = new StandardVariable();
			statusDeletedTerm.setId(TermId.STUDY_STATUS.getId());
			statusDeletedTerm.setPhenotypicType(PhenotypicType.STUDY);
			DMSVariableType type =
					new DMSVariableType(TermId.STUDY_STATUS.name(), TermId.STUDY_STATUS.name(), statusDeletedTerm, maxRank + 1);
			typeList.add(type);
			VariableList varList = new VariableList();
			Variable var = new Variable(type, TermId.DELETED_STUDY.getId());
			varList.add(var);

			this.getProjectPropertySaver().saveProjectProperties(study, typeList);
			this.getProjectPropertySaver().saveProjectPropValues(study.getProjectId(), varList);
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
				this.getDmsProjectDao().save(dataset);
			}
		}
	}
}
