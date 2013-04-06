package org.generationcp.middleware.factory.dms;

import java.util.List;

import org.generationcp.middleware.helper.dms.ProjectPropertiesHelper;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.dms.CvTermId;
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
		
		study.setName(helper.getString(CvTermId.STUDY_NAME));
		study.setProjectKey(helper.getInteger(CvTermId.PM_KEY));
		study.setTitle(helper.getString(CvTermId.STUDY_TITLE));
		study.setObjective(helper.getString(CvTermId.STUDY_OBJECTIVE));
		study.setPrimaryInvestigator(helper.getInteger(CvTermId.PI_ID));
		study.setType(helper.getString(CvTermId.STUDY_TYPE));
		study.setStartDate(helper.getInteger(CvTermId.START_DATE));
		study.setEndDate(helper.getInteger(CvTermId.END_DATE));
		study.setUser(helper.getInteger(CvTermId.STUDY_UID));
		study.setStatus(helper.getInteger(CvTermId.STUDY_IP));
		study.setCreationDate(helper.getInteger(CvTermId.RELEASE_DATE));
		
	}
	
}
