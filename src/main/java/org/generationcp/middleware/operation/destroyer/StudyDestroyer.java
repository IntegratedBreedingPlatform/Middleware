package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudyDestroyer extends Destroyer {

	public StudyDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteStudy(int studyId) throws MiddlewareQueryException {
		if (studyId > 0) {
			throw new MiddlewareQueryException("Can not delete central study");
		}
		
		requireLocalDatabaseInstance();
		DmsProject study = getDmsProjectDao().getById(studyId);
		
		renameStudyAndDatasets(study);

		if (study.getProperties() != null && !study.getProperties().isEmpty()) {
			int maxRank = 0;
			boolean found = false;
			for (ProjectProperty property : study.getProperties()) {
				if (property.getTypeId().equals(TermId.STUDY_STATUS.getId())) {
					found = true;
					if (property.getValue() == null || !property.getValue().equals(String.valueOf(TermId.DELETED_STUDY.getId()))) {
						property.setValue(String.valueOf(TermId.DELETED_STUDY.getId()));
						getProjectPropertyDao().saveOrUpdate(property);
					}
					break;
				}
				if (property.getRank() != null && property.getRank() > maxRank) {
					maxRank = property.getRank();
				}
			}
			if (!found) {
				//create a study status using the maxRank
				VariableTypeList typeList = new VariableTypeList();
				StandardVariable statusDeletedTerm = new StandardVariable();
				statusDeletedTerm.setId(TermId.STUDY_STATUS.getId());
				statusDeletedTerm.setStoredIn(new Term(TermId.STUDY_INFO_STORAGE.getId(), null, null));
				VariableType type = new VariableType(TermId.STUDY_STATUS.name(), TermId.STUDY_STATUS.name(), statusDeletedTerm, maxRank + 1);
				typeList.add(type);
				VariableList varList = new VariableList();
				Variable var = new Variable(type, TermId.DELETED_STUDY.getId());
				varList.add(var);
				
				getProjectPropertySaver().saveProjectProperties(study, typeList);
				getProjectPropertySaver().saveProjectPropValues(study.getProjectId(), varList);
			}
		}
	}
	
	private void renameStudyAndDatasets(DmsProject study) throws MiddlewareQueryException {
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String tstamp = format.format(new Date());
		study.setName(study.getName() + "#" + tstamp);
		getDmsProjectDao().save(study);
		
		List<DmsProject> datasets = getProjectRelationshipDao().getSubjectsByObjectIdAndTypeId(study.getProjectId(), TermId.BELONGS_TO_STUDY.getId());
		if (datasets != null) {
			for (DmsProject dataset : datasets) {
				dataset.setName(dataset.getName() + "#" + tstamp);
				getDmsProjectDao().save(dataset);
			}
		}
	}
}
