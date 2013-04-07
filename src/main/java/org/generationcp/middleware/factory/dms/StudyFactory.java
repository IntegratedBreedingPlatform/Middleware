package org.generationcp.middleware.factory.dms;

import java.util.List;

import org.generationcp.middleware.helper.dms.ProjectPropertiesHelper;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.dms.CVTermId;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

/**
 * Factory class used for creating the Study POJOs.
 * This will be used for converting POJOs from the new Chado Schema into the schema used by our applications.
 * 
 * The Parent is retrieved using the ProjectRelationship table where the subject = project_id.
 * The collection of ProjectProperty is retrieved by project_id and the types are in StudyField.values().
 * 
 * @author tippsgo
 *
 */
public class StudyFactory {
	
	private static final StudyFactory instance = new StudyFactory();
	
	public static StudyFactory getInstance() {
		return instance;
	}
	
	public Study createStudy(DmsProject project, DmsProject parent, List<ProjectProperty> properties) { 
		Study study = null;

		if (project != null) {
			study = new Study();
			mapProjectToStudy(project, study);
			mapParentToStudy(parent, study);
			mapPropertiesToStudy(properties, study);
		}
		
		return study;
	}
	
	private void mapProjectToStudy(DmsProject project, Study study) {
		study.setId(Integer.valueOf(project.getDmsProjectId().intValue()));
	}
	
	private void mapParentToStudy(DmsProject parent, Study study) {
		if (parent != null) {
			study.setHierarchy(Integer.valueOf(parent.getDmsProjectId().toString()));
		}
	}
	
	private void mapPropertiesToStudy(List<ProjectProperty> properties, Study study) {
		
		ProjectPropertiesHelper helper = new ProjectPropertiesHelper(properties);
		
		study.setName(helper.getString(CVTermId.STUDY_NAME));
		study.setProjectKey(helper.getInteger(CVTermId.PM_KEY));
		study.setTitle(helper.getString(CVTermId.STUDY_TITLE));
		study.setObjective(helper.getString(CVTermId.STUDY_OBJECTIVE));
		study.setPrimaryInvestigator(helper.getInteger(CVTermId.PI_ID));
		study.setType(helper.getString(CVTermId.STUDY_TYPE));
		study.setStartDate(helper.getInteger(CVTermId.START_DATE));
		study.setEndDate(helper.getInteger(CVTermId.END_DATE));
		study.setUser(helper.getInteger(CVTermId.STUDY_UID));
		study.setStatus(helper.getInteger(CVTermId.STUDY_IP));
		study.setCreationDate(helper.getInteger(CVTermId.RELEASE_DATE));
		
	}
	
}
