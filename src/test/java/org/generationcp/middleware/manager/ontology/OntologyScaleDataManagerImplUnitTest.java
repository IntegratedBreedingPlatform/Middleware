
package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import org.generationcp.middleware.UnitTestBase;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.UnitTestDaoIDGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class OntologyScaleDataManagerImplUnitTest extends UnitTestBase {
	private OntologyScaleDataManagerImpl newOsdmi;
	private Scale scale;
	private CVTermProperty property;
	private CVTermRelationshipDao relationshipDao;

	@InjectMocks
	private OntologyScaleDataManagerImpl scaleDataManager = new OntologyScaleDataManagerImpl();

	@Mock
	private OntologyDaoFactory daoFactory;

	@Mock
	private CVDao cvDao;

	@Mock
	private CVTermDao cvTermDao;

	@Mock
	private CvTermPropertyDao cvTermPropertyDao;

	@Mock
	private CVTermRelationshipDao cvTermRelationshipDao;

	@Mock
	private OntologyCommonDAO ontologyCommonDAO;

	@Before
	public void before() {
		this.newOsdmi = new OntologyScaleDataManagerImpl();
		this.cvTermPropertyDao = Mockito.mock(CvTermPropertyDao.class);
		this.scale = new Scale();
		this.scale.setId(10);
		this.property = new CVTermProperty();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.daoFactory.getCvDao()).thenReturn(cvDao);
		Mockito.when(this.daoFactory.getCvTermDao()).thenReturn(cvTermDao);
		Mockito.when(this.daoFactory.getCvTermPropertyDao()).thenReturn(cvTermPropertyDao);
		Mockito.when(this.daoFactory.getCvTermRelationshipDao()).thenReturn(cvTermRelationshipDao);
	}

	@Test
	public void updatingValuesIfStringIsNotNullOrEmptyTest() throws Exception {
		this.scale.setMaxValue("100");

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "100", TermId.MAX_VALUE.getId());

		Mockito.verify(this.cvTermPropertyDao).save(this.scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(this.scale.getMaxValue()),
				0);
	}

	@Test
	public void updatingValuesIfStringIsNullOrEmptyTest() throws Exception {
		this.scale.setMaxValue("");

		when(this.cvTermPropertyDao.getOneByCvTermAndType(this.scale.getId(), TermId.MAX_VALUE.getId())).thenReturn(this.property);

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "", TermId.MAX_VALUE.getId());
		Mockito.verify(this.cvTermPropertyDao).makeTransient(this.property);

	}

	@Test
	public void updatingValuesIfStringIsNullOrEmptyAndPropertyIsNullTest() throws Exception {
		this.scale.setMaxValue("");
		this.property = null;

		when(this.cvTermPropertyDao.getOneByCvTermAndType(this.scale.getId(), TermId.MAX_VALUE.getId())).thenReturn(this.property);

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "", TermId.MAX_VALUE.getId());
		Mockito.verify(this.cvTermPropertyDao, never()).makeTransient(this.property);

	}

	@Test
	public void testGetAllScalesShouldFetchAndVerify() throws Exception {
		Map<Integer, Scale> scaleMap = new HashMap<>();
		TestDataHelper.fillTestScales(scaleMap, 3);

		// Fill Test Created Date Property
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyForScale(new ArrayList<>(scaleMap.values()), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = this.constructDate(2015, Calendar.JUNE, 30);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDatePropertyForScale(new ArrayList<>(scaleMap.values()), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		List<CVTerm> cvTermList = new ArrayList<>();
		CVTerm cvTerm = new CVTerm();
		cvTerm.setCv(CvId.SCALES.getId());
		cvTerm.setCvTermId(1);
		cvTerm.setName("Name");
		cvTerm.setDefinition("Definition");
		cvTerm.setIsObsolete(false);
		cvTermList.add(cvTerm);

		Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);
		Mockito.when(this.ontologyCommonDAO.getScalesWithDataTypeAndProperties(Mockito.anyList(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(scaleMap);

		List<Scale> scales = this.scaleDataManager.getAllScales();

		List<String> scaleNames = Util.convertAll(scales, new Function<Scale, String>() {

			@Override
			public String apply(Scale x) {
				return x.getName();
			}
		});

		final boolean scaleNameIsOrdered = Ordering.natural().isOrdered(scaleNames);

		Assert.assertEquals(scaleMap.size(), scales.size());
		Assert.assertTrue(scaleNameIsOrdered);

		Mockito.verify(this.ontologyCommonDAO).getScalesWithDataTypeAndProperties(Mockito.anyList(), Mockito.anyMap(), Mockito.anyBoolean());
	}


	@Test
	public void testGetScaleByIdShouldFetchAndVerify() throws Exception {
		Map<Integer, Scale> scaleMap = new HashMap<>();
		TestDataHelper.fillTestScales(scaleMap, 1);

		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyForScale(new ArrayList<>(scaleMap.values()), createdDateProperties, testCreatedDate);

		Date testUpdatedDate = this.constructDate(2015, Calendar.JUNE, 30);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDatePropertyForScale(new ArrayList<>(scaleMap.values()), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Scale existingScale = scaleMap.get(scaleMap.keySet().iterator().next());

		List<CVTerm> cvTermList = new ArrayList<>();
		CVTerm cvTerm = new CVTerm();
		cvTerm.setCv(CvId.SCALES.getId());
		cvTerm.setCvTermId(1);
		cvTerm.setName("Name");
		cvTerm.setDefinition("Definition");
		cvTerm.setIsObsolete(false);
		cvTermList.add(cvTerm);

		Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);
		Mockito.when(this.ontologyCommonDAO.getScalesWithDataTypeAndProperties(Mockito.anyList(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(scaleMap);

		final Scale scale = this.scaleDataManager.getScale(existingScale.getId(), true);

		// Make sure our scale exists and is inserted properly and display proper message if it is not inserted properly
		String message = "The %s for scale '" + existingScale.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), existingScale.getName(), scale.getName());
		Assert.assertEquals(String.format(message, "Definition"), existingScale.getDefinition(), scale.getDefinition());
		Assert.assertEquals(String.format(message, "DataType"), existingScale.getDataType(), scale.getDataType());
		Assert.assertEquals(String.format(message, "Min Value"), existingScale.getMinValue(), scale.getMinValue());
		Assert.assertEquals(String.format(message, "Max Value"), existingScale.getMaxValue(), scale.getMaxValue());
		Assert.assertEquals(String.format(message, "CreatedDate"),existingScale.getDateCreated(), scale.getDateCreated());
		Assert.assertEquals(String.format(message, "UpdatedDate"), existingScale.getDateLastModified(), scale.getDateLastModified());

		Mockito.verify(this.ontologyCommonDAO).getScalesWithDataTypeAndProperties(Mockito.anyList(), Mockito.anyMap(), Mockito.anyBoolean());

	}

	@Test
	public void testAddScaleShouldVerifyDaoCallsAndSetCreatedDate() throws Exception {
		Scale scale = new Scale();
		scale.setName(TestDataHelper.getNewRandomName("Scale"));
		scale.setDefinition("Test Scale Name");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		TermSummary termSummary = new TermSummary(1, "Category1", "Definition");
		scale.addCategory(termSummary);

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.SCALES.getId());
		cvTerm.setName(scale.getName());
		cvTerm.setDefinition(scale.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Date date = this.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		Mockito.when(this.cvTermDao.getByNameAndCvId(scale.getName(), CvId.SCALES.getId())).thenReturn(null);
		Mockito.when(this.cvTermDao.save(scale.getName(), scale.getDefinition(), CvId.SCALES)).thenReturn(cvTerm);

		this.scaleDataManager.addScale(scale);

		// Make sure each scale data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for scale '" + scale.getId() + "' was not added correctly.";
		Assert.assertNotNull(String.format(message, "Id"), scale.getId());
		Assert.assertNotNull(String.format(message, "CvId"), scale.getVocabularyId());
		Assert.assertEquals(String.format(message, "CreatedDate"), date, scale.getDateCreated());

		Mockito.verify(this.cvTermDao).getByNameAndCvId(scale.getName(), CvId.SCALES.getId());
		Mockito.verify(this.cvTermDao).save(scale.getName(), scale.getDefinition(), CvId.SCALES);
	}

	@Test(expected = MiddlewareException.class)
	public void testAddScaleShouldNotAllowNewScaleWithSameName() {
		Scale scale = TestDataHelper.generateScale();

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.SCALES.getId());
		cvTerm.setName(scale.getName());
		cvTerm.setDefinition(scale.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Mockito.when(this.cvTermDao.getByNameAndCvId(scale.getName(), CvId.SCALES.getId())).thenReturn(cvTerm);

		this.scaleDataManager.addScale(scale);
	}

	@Test(expected = MiddlewareException.class)
	public void testAddScaleWithEmptyDataType() {
		Scale scale = new Scale();
		scale.setName("Name");
		scale.setDefinition("Description");

		Mockito.when(this.cvTermDao.getByNameAndCvId(scale.getName(), CvId.SCALES.getId())).thenReturn(null);
		this.scaleDataManager.addScale(scale);
	}

	@Test
	public void testUpdateScaleShouldVerifyDaoCallsAndSetUpdatedDate() throws Exception {
		CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

		//Updating scale via manager
		Scale scale = new Scale();
		scale.setId(scaleTerm.getCvTermId());
		scale.setName("New Scale Name");
		scale.setDefinition("New Scale Definition");
		scale.setDataType(DataType.NUMERIC_VARIABLE);
		scale.setMinValue("5");
		scale.setMaxValue("50");

		Date updatedDate = this.constructDate(2015, Calendar.MAY, 20);
		this.stubCurrentDate(updatedDate);

		CVTermRelationship cvTermRelationship1 = new CVTermRelationship();
		cvTermRelationship1.setCvTermRelationshipId(UnitTestDaoIDGenerator.generateId(CVTermRelationship.class));
		cvTermRelationship1.setSubjectId(scale.getId());
		cvTermRelationship1.setTypeId(TermId.HAS_TYPE.getId());
		cvTermRelationship1.setObjectId(DataType.CATEGORICAL_VARIABLE.getId());

		CV cv = new CV();
		cv.setCvId(101);
		cv.setName("Cv Name");
		cv.setDefinition("Cv Definition");

		CVTermRelationship cvTermRelationship2 = new CVTermRelationship();
		cvTermRelationship2.setCvTermRelationshipId(UnitTestDaoIDGenerator.generateId(CVTermRelationship.class));
		cvTermRelationship2.setSubjectId(scale.getId());
		cvTermRelationship2.setTypeId(TermId.HAS_VALUE.getId());
		cvTermRelationship2.setObjectId(cv.getCvId());

		List<CVTermRelationship> cvTermRelationshipList = new ArrayList<>();
		cvTermRelationshipList.add(cvTermRelationship1);
		cvTermRelationshipList.add(cvTermRelationship2);

		Mockito.when(this.cvTermDao.getById(scale.getId())).thenReturn(scaleTerm);
		Mockito.when(this.cvTermRelationshipDao.getBySubject(scale.getId())).thenReturn(cvTermRelationshipList);
		Mockito.when(this.cvTermDao.merge(scaleTerm)).thenReturn(scaleTerm);

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.SCALES.getId());
		cvTerm.setName(scale.getName());
		cvTerm.setDefinition(scale.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		List<CVTerm> cvTermList = new ArrayList<>();
		cvTermList.add(cvTerm);

		Mockito.when(this.cvTermDao.getByIds(Mockito.anyList())).thenReturn(cvTermList);

		this.scaleDataManager.updateScale(scale);

		// Make sure each scale data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for scale '" + scaleTerm.getCvTermId() + "' was not updated correctly.";
		Assert.assertNotNull(String.format(message, "Id"), scale.getId());

		Mockito.verify(this.cvTermDao).getById(scale.getId());
		Mockito.verify(this.cvTermRelationshipDao).getBySubject(scale.getId());
		Mockito.verify(this.cvTermDao).merge(scaleTerm);
		Mockito.verify(this.cvTermDao).getByIds(Mockito.anyList());
	}

	@After
	public void after() {
		this.newOsdmi = null;
		this.cvTermPropertyDao = null;
		this.scale = null;
		this.property = null;
	}

}
