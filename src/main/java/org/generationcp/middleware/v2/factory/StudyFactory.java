package org.generationcp.middleware.v2.factory;

import java.util.List;

import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.StudyDetails;
import org.generationcp.middleware.v2.helper.ProjectPropertiesHelper;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

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

	private StudyFactory() {
	}
	
	public static StudyFactory getInstance() {
		return instance;
	}
	
	public Study createStudy(DmsProject project, Integer parentId, List<ProjectProperty> properties) { 
		Study study = null;

		if (project != null) {
			study = new Study();
			mapProjectToStudy(project, study);
			mapParentToStudy(parentId, study);
			mapPropertiesToStudy(properties, study);
		}
		
		return study;
	}
	
	private void mapProjectToStudy(DmsProject project, Study study) {
		study.setId(Integer.valueOf(project.getProjectId().intValue()));
		study.setName(project.getName());
		study.setTitle(project.getDescription());
	}
	
	private void mapParentToStudy(Integer parentId, Study study) {
		if (parentId != null) {
			study.setHierarchy(parentId.intValue());
		}
	}
	
	private void mapPropertiesToStudy(List<ProjectProperty> properties, Study study) {
		
		ProjectPropertiesHelper helper = new ProjectPropertiesHelper(properties);
		
		study.setProjectKey(helper.getInteger(TermId.PM_KEY));
		study.setObjective(helper.getString(TermId.STUDY_OBJECTIVE));
		study.setPrimaryInvestigator(helper.getInteger(TermId.PI_ID));
		study.setType(helper.getString(TermId.STUDY_TYPE));
		study.setStartDate(helper.getInteger(TermId.START_DATE));
		study.setEndDate(helper.getInteger(TermId.END_DATE));
		study.setUser(helper.getInteger(TermId.STUDY_UID));
		study.setStatus(helper.getInteger(TermId.STUDY_IP));
		study.setCreationDate(helper.getInteger(TermId.CREATION_DATE));
		
	}

	public StudyDetails createStudyDetails(DmsProject project) {
        ProjectPropertiesHelper helper = new ProjectPropertiesHelper(project.getProperties());
		
        StudyDetails studyDetails = new StudyDetails();
        studyDetails.setId(project.getProjectId());
        studyDetails.setName(project.getName());
        studyDetails.setTitle(project.getDescription());
        studyDetails.setObjective(helper.getString(TermId.STUDY_OBJECTIVE));
        studyDetails.setPrimaryInvestigator(helper.getInteger(TermId.PI_ID));
        studyDetails.setType(helper.getString(TermId.STUDY_TYPE));
        studyDetails.setStartDate(helper.getInteger(TermId.START_DATE));
        studyDetails.setEndDate(helper.getInteger(TermId.END_DATE));
        studyDetails.setUser(helper.getInteger(TermId.STUDY_UID));
        studyDetails.setStatus(helper.getInteger(TermId.STUDY_IP));
        studyDetails.setCreationDate(helper.getInteger(TermId.CREATION_DATE));
		
		return studyDetails;
	}
	
}
