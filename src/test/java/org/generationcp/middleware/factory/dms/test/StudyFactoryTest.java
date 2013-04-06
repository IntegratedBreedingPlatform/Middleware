package org.generationcp.middleware.factory.dms.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.middleware.factory.dms.StudyFactory;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.dms.CvTermId;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Test;


/**
 * Test Class for testing the StudyFactory
 * 
 * @author tippsgo
 *
 */
public class StudyFactoryTest {

	private static DmsProject p0 = createProject(1000L, "Root", "Description for Root Parent");
	private static DmsProject p1 = createProject(1L, "Project 1", "Description for Project 1");
	private static DmsProject p2 = createProject(2L, "Project 2", "Description for Project 2");
	private static DmsProject p3 = createProject(3L, "Project 3", "Description for Project 3");
//	private static DmsProject p4 = createProject(4L, "Project 4", "Description for Project 4");

	/**
	 * Test Case #1: Create a Study POJO with ALL fields populated.
	 * Expected: Returns a Study POJO that matches the ProperProperties.
	 */
	@Test
	public void testCase1() {
		Study s1 = new Study(1, "STUDY-P1", 10001, "TITLE-P1", "OBJECTIVE-P1", 10002, "TYPE-P1", 
							20130101, 20130201, 10004, 10003, p0.getDmsProjectId().intValue(), 20130401);
		List<ProjectProperty> properties = createProjectPropertiesFromStudy(s1);
		
		Study study = StudyFactory.getInstance().createStudy(p1, p0, properties);
		
		Assert.assertNotNull(study);
		assertResult(s1, study);
	}

	/**
	 * Test Case #2: If the ProjectProperty parameter is NULL.
	 * Expected: Returns NULL.
	 */
	@Test
	public void testCase2() {
		
		Study study = StudyFactory.getInstance().createStudy(null, null, null);
		
		Assert.assertNull(study);
	}

	/**
	 * Test Case #3: If the ProjectProperty parameter is NOT NULL, but its fields are NULL.
	 * Expected: Returns a Study POJO with NULL fields.
	 */
	@Test
	public void testCase3() {
		Study s2 = new Study(2, null, null, null, null, null, null, 
							null, null, null, null, null, null);
		List<ProjectProperty> properties = createProjectPropertiesFromStudy(s2);
		
		Study study = StudyFactory.getInstance().createStudy(p2, null, properties);
		
		Assert.assertNotNull(study);
		assertResult(s2, study);
	}
	
	/**
	 * Test Case #4: Create a Study POJO with an invalid number representing a numeric value.
	 * Expected: Throws a NumberFormatException
	 */
	@Test(expected = NumberFormatException.class)
	public void testCase4() {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		addPropertySetToList(properties, CvTermId.START_DATE, "ABCDEF", 7L);
		StudyFactory.getInstance().createStudy(p3, p2, properties);
	}
	
	//=========================  Test Data creation =====================================

	private List<ProjectProperty> createProjectPropertiesFromStudy(Study study) {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		
		addPropertySetToList(properties, CvTermId.STUDY_NAME, study.getName(), 111L);
		addPropertySetToList(properties, CvTermId.PM_KEY, getString(study.getProjectKey()), 2L);
		addPropertySetToList(properties, CvTermId.STUDY_TITLE, study.getTitle(), 3L);
		addPropertySetToList(properties, CvTermId.STUDY_OBJECTIVE, study.getObjective(), 4L);
		addPropertySetToList(properties, CvTermId.PI_ID, getString(study.getPrimaryInvestigator()), 5L);
		addPropertySetToList(properties, CvTermId.STUDY_TYPE, study.getType(), 6L);
		addPropertySetToList(properties, CvTermId.START_DATE, getString(study.getStartDate()), 7L);
		addPropertySetToList(properties, CvTermId.END_DATE, getString(study.getEndDate()), 8L);
		addPropertySetToList(properties, CvTermId.STUDY_UID, getString(study.getUser()), 9L);
		addPropertySetToList(properties, CvTermId.STUDY_IP, getString(study.getStatus()), 10L);
		addPropertySetToList(properties, CvTermId.RELEASE_DATE, getString(study.getCreationDate()), 11L);
		
		return properties;
	}
	
	private String getString(Integer value) {
		return value != null ? value.toString() : null;
	}
	
	private void addPropertySetToList(List<ProjectProperty> properties, CvTermId term, String value, Long rank) {
		long startId = properties.size() > 0 ? properties.get(properties.size()-1).getProjectPropertyId() + 1L : 1L;
		properties.add(createProjectProperty(startId, term, value, rank));
		properties.add(createProjectProperty(startId + 1, CvTermId.STUDY_INFORMATION, "study info", rank));
		properties.add(createProjectProperty(startId + 2, CvTermId.VARIABLE_DESCRIPTION, "some description", rank));
		properties.add(createProjectProperty(startId + 3, CvTermId.STANDARD_VARIABLE, term.getId().toString(), rank));
	}
	
	private ProjectProperty createProjectProperty(Long id, CvTermId type, String value, Long rank) {
		ProjectProperty property = new ProjectProperty();
		property.setProjectPropertyId(id);
		property.setTypeId(type.getId());
		property.setValue(value);
		property.setRank(rank);
		return property;
	}
	
	private static DmsProject createProject(Long id, String name, String description) {
		DmsProject project = new DmsProject();
		project.setDmsProjectId(id);
		project.setName(name);
		project.setDescription(description);
		return project;
	}

	//======================  Assertion Util =======================================
	
	private void assertResult(Study expectedStudy, Study actualStudy) {
		//Assert.assertEquals(expectedStudy.toString(), actualStudy.toString());
		Assert.assertEquals(expectedStudy.getId(), actualStudy.getId());
		Assert.assertEquals(expectedStudy.getName(), actualStudy.getName());
		Assert.assertEquals(expectedStudy.getProjectKey(), actualStudy.getProjectKey());
		Assert.assertEquals(expectedStudy.getTitle(), actualStudy.getTitle());
		Assert.assertEquals(expectedStudy.getObjective(), actualStudy.getObjective());
		Assert.assertEquals(expectedStudy.getPrimaryInvestigator(), actualStudy.getPrimaryInvestigator());
		Assert.assertEquals(expectedStudy.getType(), actualStudy.getType());
		Assert.assertEquals(expectedStudy.getStartDate(), actualStudy.getStartDate());
		Assert.assertEquals(expectedStudy.getEndDate(), actualStudy.getEndDate());
		Assert.assertEquals(expectedStudy.getUser(), actualStudy.getUser());
		Assert.assertEquals(expectedStudy.getStatus(), actualStudy.getStatus());
		Assert.assertEquals(expectedStudy.getHierarchy(), actualStudy.getHierarchy());
		Assert.assertEquals(expectedStudy.getCreationDate(), actualStudy.getCreationDate());
	}
	
}
