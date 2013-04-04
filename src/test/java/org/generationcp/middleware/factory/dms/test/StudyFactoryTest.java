package org.generationcp.middleware.factory.dms.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.middleware.factory.dms.StudyFactory;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.utils.test.TestDataUtil;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test Class for testing the StudyFactory
 * 
 * @author tippsgo
 *
 */
public class StudyFactoryTest {

	private static DmsProject p1 = TestDataUtil.createProject(1L, "Project 1", "Description for Project 1");
	private static DmsProject p2 = TestDataUtil.createProject(2L, "Project 2", "Description for Project 2");
	private static DmsProject p3 = TestDataUtil.createProject(3L, "Project 3", "Description for Project 3");
	private static DmsProject p4 = TestDataUtil.createProject(4L, "Project 4", "Description for Project 4");
	
	private static List<ProjectProperty> propertyData1 = new ArrayList<ProjectProperty>();
	private static List<ProjectProperty> propertyData2 = new ArrayList<ProjectProperty>();
	private static List<ProjectProperty> propertyData3 = new ArrayList<ProjectProperty>();
	private static List<ProjectProperty> propertyData4 = new ArrayList<ProjectProperty>();

	@BeforeClass
	public static void initialize() {
		createTestData();
	}
	
	/**
	 * Test Case #1: Create a Study POJO with ALL properties set.
	 * Expected: Returns a Study POJO that matches the ProperProperties.
	 */
	@Test
	public void testCase1() {
		Study study = StudyFactory.getInstance().createStudy(propertyData1);
		
		Assert.assertNotNull(study);
		assertResult(study, propertyData1);
	}

	/**
	 * Test Case #2: If the ProjectProperty parameter is NULL.
	 * Expected: Returns NULL.
	 */
	@Test
	public void testCase2() {
		Study study = StudyFactory.getInstance().createStudy(null);
		
		Assert.assertNull(study);
	}

	/**
	 * Test Case #3: If the ProjectProperty parameter is NOT NULL, but its fields are NULL.
	 * Expected: Returns a Study POJO with NULL fields.
	 */
	@Test
	public void testCase3() {
		Study study = StudyFactory.getInstance().createStudy(propertyData2);
		
		Assert.assertNotNull(study);
		assertResult(study, propertyData2);
	}
	
	/**
	 * Test Case #4: Create a Study POJO with an invalid number representing a numeric value.
	 * Expected: Throws a NumberFormatException
	 */
	@Test(expected = NumberFormatException.class)
	public void testCase4() {
		StudyFactory.getInstance().createStudy(propertyData4);
	}

	/**
	 * Test Case #5: Create a List of Study POJOs.
	 * Expected: Returns a List of Study POJOs that matches the ProjectProperties.
	 */
	@Test
	public void testCase5() {
		List<ProjectProperty> propertyDataList = new ArrayList<ProjectProperty>();
		propertyDataList.addAll(propertyData1);
		propertyDataList.addAll(propertyData3);
		
		List<Study> studies = StudyFactory.getInstance().createStudies(propertyDataList);
		
		Assert.assertNotNull(studies);
		Assert.assertEquals(2, studies.size());
		assertResult(studies.get(0), propertyData1);
		assertResult(studies.get(1), propertyData3);
	}
	
	/**
	 * Test Case #6: If the parameter is NULL.
	 * Expected: Returns an empty list.
	 */
	@Test
	public void testCase6() {
		List<Study> studies = StudyFactory.getInstance().createStudies(null);
		
		Assert.assertNotNull(studies);
		Assert.assertEquals(0, studies.size());
	}
	
	/**
	 * Test Case #7: If the parameter is an Empty List.
	 * Expected: Returns an empty list of Study POJOs.
	 */
	@Test
	public void testCase7() {
		List<ProjectProperty> propertyDataList = new ArrayList<ProjectProperty>();
		
		List<Study> studies = StudyFactory.getInstance().createStudies(propertyDataList);
		
		Assert.assertNotNull(studies);
		Assert.assertEquals(0, studies.size());
	}
	
	//=========================  Test Data creation =====================================
	
	private static void createTestData() {
		//Create Project Property data (PropertyId, ProjectId, TypeId, Value, Rank)	
		
		//PROJECT 1 - all fields have values
		propertyData1.add(TestDataUtil.createProjectProperty(4L, p1, "STUDY - CONDUCTED (DBCV)", "STUDY-P1", 1L)); //name
		propertyData1.add(TestDataUtil.createProjectProperty(12L, p1, "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)", "10001", 3L)); //project key
		propertyData1.add(TestDataUtil.createProjectProperty(8L, p1, "TITLE - ASSIGNED (TEXT)", "TITLE-P1", 2L)); //title
		propertyData1.add(TestDataUtil.createProjectProperty(16L, p1, "OBJECTIVE - DESCRIBED (TEXT)", "OBJECTIVE-P1", 4L)); //objective
		propertyData1.add(TestDataUtil.createProjectProperty(20L, p1, "PRINCIPAL INVESTIGATOR - ASSIGNED (DBID)", "10002", 5L)); //primary investigator id
		propertyData1.add(TestDataUtil.createProjectProperty(24L, p1, "STUDY - ASSIGNED (TYPE)", "TYPE-P1", 6L)); //type id
		propertyData1.add(TestDataUtil.createProjectProperty(28L, p1, "START DATE - ASSIGNED (DATE)", "20130101", 7L)); //start date
		propertyData1.add(TestDataUtil.createProjectProperty(32L, p1, "END DATE - ASSIGNED (DATE)", "20130201", 8L)); //end date
		propertyData1.add(TestDataUtil.createProjectProperty(36L, p1, "STUDY IP STATUS - ASSIGNED (TYPE)", "10003", 9L)); //status
		
		//PROJECT 2 - all fields have null values
		propertyData2.add(TestDataUtil.createProjectProperty(4L, p2, "STUDY - CONDUCTED (DBCV)", null, 1L)); //name
		propertyData2.add(TestDataUtil.createProjectProperty(12L, p2, "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)", null, 3L)); //project key
		propertyData2.add(TestDataUtil.createProjectProperty(8L, p2, "TITLE - ASSIGNED (TEXT)", null, 2L)); //title
		propertyData2.add(TestDataUtil.createProjectProperty(16L, p2, "OBJECTIVE - DESCRIBED (TEXT)", null, 4L)); //objective
		propertyData2.add(TestDataUtil.createProjectProperty(20L, p2, "PRINCIPAL INVESTIGATOR - ASSIGNED (DBID)", null, 5L)); //primary investigator id
		propertyData2.add(TestDataUtil.createProjectProperty(24L, p2, "STUDY - ASSIGNED (TYPE)", null, 6L)); //type id
		propertyData2.add(TestDataUtil.createProjectProperty(28L, p2, "START DATE - ASSIGNED (DATE)", null, 7L)); //start date
		propertyData2.add(TestDataUtil.createProjectProperty(32L, p2, "END DATE - ASSIGNED (DATE)", null, 8L)); //end date
		propertyData2.add(TestDataUtil.createProjectProperty(36L, p2, "STUDY IP STATUS - ASSIGNED (TYPE)", null, 9L)); //status
		
		//PROJECT 3 - correct test data, not all fields are populated
		propertyData3.add(TestDataUtil.createProjectProperty(4L, p3, "STUDY - CONDUCTED (DBCV)", "NAME-P3", 1L)); //name
		propertyData3.add(TestDataUtil.createProjectProperty(12L, p3, "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)", "30001", 3L)); //project key
		propertyData3.add(TestDataUtil.createProjectProperty(8L, p3, "TITLE - ASSIGNED (TEXT)", "TITLE-P3", 2L)); //title
		propertyData3.add(TestDataUtil.createProjectProperty(28L, p3, "START DATE - ASSIGNED (DATE)", "20130301", 7L)); //start date
		
		//PROJECT 5 - invalid test data, will throw NumberFormatException
		propertyData4.add(TestDataUtil.createProjectProperty(28L, p4, "START DATE - ASSIGNED (DATE)", "ABCDEF", 7L)); //start date
		
	}
	
	//======================  Assertion Util =======================================
	
	private void assertResult(Study study, List<ProjectProperty> properties) {
		if (properties != null && properties.size() > 0) {
			Assert.assertNotNull(properties.get(0).getProject().getDmsProjectId());
			Assert.assertEquals(study.getId(), Integer.valueOf(properties.get(0).getProject().getDmsProjectId().toString()));
			Assert.assertEquals(study.getName(), getProperty(properties, "STUDY - CONDUCTED (DBCV)"));
			Assert.assertEquals(study.getProjectKey(), getIntegerProperty(properties, "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)"));
			Assert.assertEquals(study.getTitle(), getProperty(properties, "TITLE - ASSIGNED (TEXT)"));
			Assert.assertEquals(study.getObjective(), getProperty(properties, "OBJECTIVE - DESCRIBED (TEXT)"));
			Assert.assertEquals(study.getPrimaryInvestigator(), getIntegerProperty(properties, "PRINCIPAL INVESTIGATOR - ASSIGNED (DBID)"));
			Assert.assertEquals(study.getType(), getProperty(properties, "STUDY - ASSIGNED (TYPE)"));
			Assert.assertEquals(study.getStartDate(), getIntegerProperty(properties, "START DATE - ASSIGNED (DATE)"));
			Assert.assertEquals(study.getEndDate(), getIntegerProperty(properties, "END DATE - ASSIGNED (DATE)"));
			Assert.assertEquals(study.getStatus(), getIntegerProperty(properties, "STUDY IP STATUS - ASSIGNED (TYPE)"));
		}
	}
	
	private String getProperty(List<ProjectProperty> properties, String type) {
		if (properties != null) {
			for (ProjectProperty property : properties) {
				if (property.getType() != null && type.equals(property.getType().getName())) {
					return property.getValue();
				}
			}
		}
		return null;
	}

	private Integer getIntegerProperty(List<ProjectProperty> properties, String type) {
		if (properties != null) {
			String value;
			for (ProjectProperty property : properties) {
				if (property.getType() != null && type.equals(property.getType().getName())) {
					value = property.getValue();
					if (value != null) {
						return Integer.valueOf(property.getValue());
					}
				}
			}
		}
		return null;
	}

}
