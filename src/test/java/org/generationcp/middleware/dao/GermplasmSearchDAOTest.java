/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchResponse;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GermplasmSearchDAOTest extends IntegrationTestBase {

	private static final Integer GROUP_ID = 10;
	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";
	private static final String DERIVATIVE_NAME_CODE = "DRVNM";
	private static final String DERIVATIVE_NAME = "DERIVATIVE NAME";

	private GermplasmSearchDAO dao;
	private UserDefinedFieldDAO userDefinedFieldDao;
	private GermplasmDAO germplasmDao;
	private NameDAO nameDAO;
	private GermplasmExternalReferenceDAO germplasmExternalReferenceDAO;

	private Integer germplasmGID;
	private Integer femaleParentGID;
	private Integer maleParentGID;
	private Name preferredName;
	private Name preferredId;
	private Name maleParentPreferredName;
	private Name femaleParentPreferredName;
	private final int germplasmDate = 20150101;
	private String attributeValue;
	private String code1NameTypeValue;
	private static final int UNKNOWN_GENERATIVE_METHOD_ID = 1;
	private CropType cropType;
	private Pageable pageable;
	private String programUUID;
	private DaoFactory daoFactory;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	// pedigree tests
	private Germplasm greatGrandParentGermplasm;
	private Germplasm grandParentGermplasm;
	private Germplasm groupSource;
	private Germplasm descendant;

	private String nameType;

	@Before
	public void setUp() throws Exception {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}
		if (this.dao == null) {
			this.dao = new GermplasmSearchDAO(this.sessionProvder.getSession());

		}
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());

		}
		if (this.userDefinedFieldDao == null) {
			this.userDefinedFieldDao = new UserDefinedFieldDAO(this.sessionProvder.getSession());
		}
		if (this.nameDAO == null) {
			this.nameDAO = new NameDAO(this.sessionProvder.getSession());
		}
		if (this.germplasmExternalReferenceDAO == null) {
			this.germplasmExternalReferenceDAO = new GermplasmExternalReferenceDAO(this.sessionProvder.getSession());
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, this.daoFactory);
		}

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);

		this.initializeGermplasms();
		this.createTestGermplasmForSorting();

		this.pageable = mock(Pageable.class);
		when(this.pageable.getPageSize()).thenReturn(25);
		when(this.pageable.getPageNumber()).thenReturn(0);
		this.mockSortState(null, null);

		this.programUUID = ContextHolder.getCurrentProgram();

		// Get Name type ID = 1
		this.nameType = this.userDefinedFieldDao.getById(1).getFcode();
	}

	@Test
	public void testSearchGermplasm_ExactMatchGID() {
		final GermplasmSearchRequest request = this.createSearchRequest(this.germplasmGID);
		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchGermplasm_ExactMatchGermplasmName() {
		final GermplasmSearchRequest request =
			this.createSearchRequest(this.preferredName.getNval(), SqlTextFilter.Type.EXACTMATCH);
		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);
		Assert.assertEquals(
			"The results should contain one germplasm since there's only one test data with '" + this.preferredName.getNval()
				+ "' name", 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchGermplasm_StartsWithGermplasmName() {

		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(this.germplasmDate, 12, 13, 1, 0, 1, 1, 0, 1, "LocationName");
		germplasm.getPreferredName().setNval("GermplasmName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final GermplasmSearchRequest request =
			this.createSearchRequest(germplasm.getPreferredName().getNval(), SqlTextFilter.Type.STARTSWITH);
		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);
		Assert.assertEquals(
			"The results should contain one germplasm since there's only one test data with name that starts with " + germplasm
				.getPreferredName().getNval(), 1, results.size());
		Assert.assertTrue(germplasm.getPreferredName().getNval().contains("GermplasmName"));
	}

	@Test
	public void testSearchGermplasm_ContainsGermplasmName() {
		final GermplasmSearchRequest request =
			this.createSearchRequest(this.preferredName.getNval(), SqlTextFilter.Type.CONTAINS);
		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with name that contains "
			+ this.preferredName.getNval(), 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchGermplasm_IncludeParents() {

		final Germplasm parentGermplasm =
			GermplasmTestDataInitializer.createGermplasm(this.germplasmDate, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer parentGermplasmId =
			this.germplasmTestDataGenerator.addGermplasm(parentGermplasm, parentGermplasm.getPreferredName(), this.cropType);

		final Germplasm childGermplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, parentGermplasm.getGid(), 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Integer childGermplasmId =
			this.germplasmTestDataGenerator.addGermplasm(childGermplasm, childGermplasm.getPreferredName(), this.cropType);

		final GermplasmSearchRequest request = this.createSearchRequest(childGermplasm.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(1);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.GENERATIVE);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		Assert.assertTrue("Result should include both child and parent germplasms", results.size() >= 2);
		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertTrue("Parent germplasm should be included in search result", resultGIDs.contains(parentGermplasmId));
		Assert.assertTrue("Child germplasm should be included in search result", resultGIDs.contains(childGermplasmId));
	}

	@Test
	public void testSearchGermplasm_IncludePedigreeGenerative() {
		this.createPedigree();

		final GermplasmSearchRequest request = this.createSearchRequest(this.descendant.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(1);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.GENERATIVE);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertFalse(resultGIDs.contains(this.greatGrandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.grandParentGermplasm.getGid()));
		Assert.assertFalse(resultGIDs.contains(this.groupSource.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.descendant.getGid()));
	}

	@Test
	public void testSearchGermplasm_IncludePedigreeGenerativeLevelTwo() {
		this.createPedigree();

		final GermplasmSearchRequest request = this.createSearchRequest(this.descendant.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(2);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.GENERATIVE);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertTrue(resultGIDs.contains(this.greatGrandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.grandParentGermplasm.getGid()));
		Assert.assertFalse(resultGIDs.contains(this.groupSource.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.descendant.getGid()));
	}

	@Test
	public void testSearchGermplasm_IncludePedigreeDerivative() {
		this.createPedigree();

		final GermplasmSearchRequest request = this.createSearchRequest(this.descendant.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(1);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.DERIVATIVE);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertFalse(resultGIDs.contains(this.greatGrandParentGermplasm.getGid()));
		Assert.assertFalse(resultGIDs.contains(this.grandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.groupSource.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.descendant.getGid()));
	}

	@Test
	public void testSearchGermplasm_IncludePedigreeBoth() {
		this.createPedigree();

		final GermplasmSearchRequest request = this.createSearchRequest(this.descendant.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(2);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.BOTH);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertFalse(resultGIDs.contains(this.greatGrandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.grandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.groupSource.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.descendant.getGid()));
	}

	@Test
	public void testSearchGermplasm_IncludePedigreeBothLevelThree() {
		this.createPedigree();

		final GermplasmSearchRequest request = this.createSearchRequest(this.descendant.getGid());
		final GermplasmSearchRequest.IncludePedigree includePedigree = new GermplasmSearchRequest.IncludePedigree();
		includePedigree.setGenerationLevel(3);
		includePedigree.setType(GermplasmSearchRequest.IncludePedigree.Type.BOTH);
		request.setIncludePedigree(includePedigree);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final GermplasmSearchResponse germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertTrue(resultGIDs.contains(this.greatGrandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.grandParentGermplasm.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.groupSource.getGid()));
		Assert.assertTrue(resultGIDs.contains(this.descendant.getGid()));
	}

	@Test
	public void testSearchGermplasm_IncludeMGMembers() {
		final GermplasmSearchRequest request = this.createSearchRequest(this.germplasmGID);
		request.setIncludeGroupMembers(true);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);
		Assert.assertEquals("The result should contain 2 germplasms (one is the actual result and the other is the MG member)", 2,
			results.size());
		this.assertPossibleGermplasmFields(results);

		final GermplasmSearchResponse actualResult = results.get(0);
		final GermplasmSearchResponse mgMember = results.get(1);
		Assert.assertEquals("Lot count should be 0", Integer.valueOf(1), actualResult.getLotCount());
		Assert.assertEquals("Total Available Balance should be 100.0",
			Double.valueOf(100.0), Double.valueOf(actualResult.getAvailableBalance()));
		Assert.assertEquals("Lot count for mgMember should be 0", Integer.valueOf(0), mgMember.getLotCount());
		Assert.assertEquals("Total Available Balance for mgMember should be 0.0",
			Double.valueOf(0.0), Double.valueOf(mgMember.getAvailableBalance()));
	}

	@Test
	public void testSearchGermplasm_WithAllAddedColumns() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		// Create propertyId list with all addable columns.
		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);
		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);
		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);
		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);
		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		// Add Attributes column (NOTE attribute) and "DERIVATIVE NAME" name type
		propertyIds.add(NOTE_ATTRIBUTE);
		propertyIds.add(DERIVATIVE_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithImmediateSourceGIDAndName() {
		//Create a new germplasm with -1 gnpgs
		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.femaleParentGID, this.maleParentGID, -1, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID,
				1,
				"LocationName");

		final Integer gid = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		final GermplasmSearchRequest searchParameter = this.createSearchRequest(gid);

		final List<String> propertyIds = new LinkedList<>();

		// Create propertyId list with all addable columns.
		propertyIds.add(GermplasmSearchDAO.IMMEDIATE_SOURCE_GID);
		propertyIds.add(GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithGermplasmExternalReferenceSource() {
		//Create a new germplasm with -1 gnpgs
		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.femaleParentGID, this.maleParentGID, -1, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID,
				1, "LocationName");

		final Integer gid = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		final GermplasmSearchRequest searchParameter = this.createSearchRequest(gid);

		final GermplasmExternalReference germplasmExternalReference = this.createGermplasmExternalReference(germplasm);

		final SqlTextFilter externalReferenceSourceFilter = new SqlTextFilter();
		externalReferenceSourceFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		externalReferenceSourceFilter.setValue(germplasmExternalReference.getSource());
		searchParameter.setExternalReferenceSource(externalReferenceSourceFilter);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
	}

	@Test
	public void testSearchGermplasm_WithGermplasmExternalReferenceId() {
		//Create a new germplasm with -1 gnpgs
		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.femaleParentGID, this.maleParentGID, -1, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID,
				1, "LocationName");

		final Integer gid = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		final GermplasmSearchRequest searchParameter = this.createSearchRequest(gid);

		final GermplasmExternalReference germplasmExternalReference = this.createGermplasmExternalReference(germplasm);

		final SqlTextFilter externalReferenceIdFilter = new SqlTextFilter();
		externalReferenceIdFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		externalReferenceIdFilter.setValue(germplasmExternalReference.getReferenceId());
		searchParameter.setExternalReferenceId(externalReferenceIdFilter);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
	}

	private GermplasmExternalReference createGermplasmExternalReference(final Germplasm germplasm) {
		final GermplasmExternalReference germplasmExternalReference = new GermplasmExternalReference();
		germplasmExternalReference.setGermplasm(germplasm);
		germplasmExternalReference.setSource(RandomStringUtils.randomAlphabetic(200));
		germplasmExternalReference.setReferenceId(RandomStringUtils.randomAlphabetic(500));
		this.germplasmExternalReferenceDAO.save(germplasmExternalReference);
		return germplasmExternalReference;
	}

	@Test
	public void testSearchGermplasm_WithGermplasmDetailsColumnsOnly() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);
		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithMethodDetailsColumnsOnly() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);
		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);
		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithParentDetailsColumnsOnly() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithAttributeTypeAddedColumnOnly() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);
		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasm_WithGermplasmNameTypeAddedColumnOnly() {

		final GermplasmSearchRequest searchParameter = this.createSearchRequest(this.germplasmGID);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(DERIVATIVE_NAME);
		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchGermplasmPreferredIdSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.PREFERRED_ID}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmPreferredId());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));
	}

	@Test
	public void testSearchGermplasmPreferredIdSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.PREFERRED_ID}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmPreferredId());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));
	}

	@Test
	public void testSearchGermplasmPreferredNameSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.PREFERRED_NAME}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmPreferredName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmPreferredNameSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.PREFERRED_NAME}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmPreferredName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmGermplasmDateSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.GERMPLASM_DATE}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmDate());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmGermplasmDateSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.GERMPLASM_DATE}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGermplasmDate());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmGermplasmLocationSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.LOCATION_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.LOCATION_NAME}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getLocationName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmGermplasmLocationSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.LOCATION_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.LOCATION_NAME}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getLocationName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodNameSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_NAME}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodNameSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_NAME}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodAbbreviationSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_ABBREVIATION}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodCode());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodAbbreviationSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_ABBREVIATION}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodCode());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodNumberSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_NUMBER}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodNumber());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodNumberSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_NUMBER}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodNumber());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodGroupSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_GROUP}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodGroup());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmMethodGroupSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {GermplasmSearchDAO.METHOD_GROUP}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getMethodGroup());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_AttributeTypeSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {NOTE_ATTRIBUTE.toUpperCase()}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE.toUpperCase()));
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_AttributeTypeSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {NOTE_ATTRIBUTE.toUpperCase()}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE.toUpperCase()));
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_NameTypeSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(DERIVATIVE_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {DERIVATIVE_NAME}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getNameTypesValueMap().get(DERIVATIVE_NAME));
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_NameTypeSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(DERIVATIVE_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		this.mockSortState(new String[] {DERIVATIVE_NAME}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getNameTypesValueMap().get(DERIVATIVE_NAME));
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasmNamesSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.NAMES}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getNames());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_NamesSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.NAMES}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getNames());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_GIDSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.GID}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGid());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_GIDSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.GID}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGid());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_GroupIdSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.GROUP_ID}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGroupId());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_GroupIdSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.GROUP_ID}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getGroupId());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_LotSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.AVAIL_LOTS}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getLotCount());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_LotSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.AVAIL_LOTS}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<Integer> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getLotCount());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_BalanceSortAscending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.AVAIL_BALANCE}, new boolean[] {true});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getAvailableBalance());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_BalanceSortDescending() {

		final GermplasmSearchRequest searchParameter =
			this.createSearchRequest("GermplasmForSorting", SqlTextFilter.Type.STARTSWITH);

		this.mockSortState(new String[] {GermplasmSearchDAO.AVAIL_BALANCE}, new boolean[] {false});

		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(searchParameter, this.pageable, this.programUUID);

		final List<String> list = new ArrayList<>();
		for (final GermplasmSearchResponse g : results) {
			list.add(g.getAvailableBalance());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchGermplasm_FilterByList() {

		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null, false);
		final int germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);

		final GermplasmListData germplasmListData =
			GermplasmListDataTestDataInitializer.createGermplasmListData(germplasmList, this.germplasmGID, 2);
		this.germplasmListManager.addGermplasmListData(germplasmListData);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setGermplasmListIds(Collections.singletonList(germplasmListId));
		final List<GermplasmSearchResponse> results = this.dao.searchGermplasm(request, this.pageable, this.programUUID);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testCountSearchGermplasm_AllGidsExist() {

		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");

		this.germplasmDao.save(germplasm1);
		this.germplasmDao.save(germplasm2);

		this.sessionProvder.getSession().flush();

		final Set<Integer> gids = new HashSet<>();
		gids.add(germplasm1.getGid());
		gids.add(germplasm2.getGid());

		final long result = this.dao.countSearchGermplasm(this.getGermplasmSearchRequest(gids), this.programUUID);

		Assert.assertEquals("The number of gids in list should match the count of records matched in the database.", gids.size(),
			(int) result);

	}

	@Test
	public void testCountSearchGermplasm_OnlyOneGidExists() {

		final Set<Integer> gids = new HashSet<>();
		final Integer dummyGid = Integer.MIN_VALUE + 1;

		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		this.germplasmDao.save(germplasm1);

		final Germplasm germplasm = this.germplasmDao.getById(dummyGid);
		Assert.assertNull("Make sure that gid " + dummyGid + " doesn't exist.", germplasm);

		gids.add(germplasm1.getGid());
		gids.add(dummyGid);

		final long result = this.dao.countSearchGermplasm(this.getGermplasmSearchRequest(gids), this.programUUID);

		Assert.assertEquals("Only one gid has a match in the database.", 1, (int) result);

	}

	@Test
	public void testCountSearchGermplasm_WithDeletedGid() {

		final Set<Integer> gids = new HashSet<>();

		final Germplasm deletedGermplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		deletedGermplasm.setDeleted(true);
		this.germplasmDao.save(deletedGermplasm);

		gids.add(deletedGermplasm.getGid());

		final long result = this.dao.countSearchGermplasm(this.getGermplasmSearchRequest(gids), this.programUUID);

		Assert.assertEquals("The count should be zero because the gid in the list is already deleted.", 0, (int) result);

	}

	@Test
	public void testCountSearchGermplasm_NoGidExists() {

		final Integer dummyGid = Integer.MIN_VALUE + 1;

		final Set<Integer> gids = new HashSet<>();

		final Germplasm germplasm = this.germplasmDao.getById(dummyGid);

		Assert.assertNull("We're testing a gid that doesnt exist, so the germplasm should be null.", germplasm);

		// Add dummy gid that do not exist in the database
		gids.add(dummyGid);

		final long result = this.dao.countSearchGermplasm(this.getGermplasmSearchRequest(gids), this.programUUID);

		Assert.assertEquals("The count should be zero because the gid in the list doesn't exist.", 0, (int) result);

	}

	@Test
	public void testAddPreFilteredGids_NoMatchFemaleParentName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(RandomStringUtils.randomAlphabetic(20));
		request.setNameFilter(nameFilter);
		request.setFemaleParentName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertTrue(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertTrue(preFilteredGids.isEmpty());
	}

	@Test
	public void testAddPreFilteredGids_MatchByFemaleParentName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(this.femaleParentPreferredName.getNval());
		request.setNameFilter(nameFilter);
		request.setFemaleParentName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertFalse(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertEquals(2, preFilteredGids.size());
		Assert.assertTrue(preFilteredGids.contains(this.germplasmGID));
	}

	@Test
	public void testAddPreFilteredGids_NoMatchMaleParentName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(RandomStringUtils.randomAlphabetic(20));
		request.setNameFilter(nameFilter);
		request.setMaleParentName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertTrue(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertTrue(preFilteredGids.isEmpty());
	}

	@Test
	public void testAddPreFilteredGids_MatchByMaleParentName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(this.maleParentPreferredName.getNval());
		request.setNameFilter(nameFilter);
		request.setMaleParentName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertFalse(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertEquals(2, preFilteredGids.size());
		Assert.assertTrue(preFilteredGids.contains(this.germplasmGID));
	}

	@Test
	public void testAddPreFilteredGids_NoMatchGroupSourceName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(RandomStringUtils.randomAlphabetic(20));
		request.setNameFilter(nameFilter);
		request.setGroupSourceName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertTrue(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertTrue(preFilteredGids.isEmpty());
	}

	@Test
	public void testAddPreFilteredGids_NoMatchImmediateSourceName() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(RandomStringUtils.randomAlphabetic(20));
		request.setNameFilter(nameFilter);
		request.setImmediateSourceName(nameFilter);
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertTrue(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertTrue(preFilteredGids.isEmpty());
	}

	@Test
	public void testAddPreFilteredGids_MatchByNameType() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setNameTypes(Collections.singletonMap(this.nameType, this.preferredName.getNval()));
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertFalse(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertEquals(1, preFilteredGids.size());
		Assert.assertEquals(this.germplasmGID, preFilteredGids.get(0));
	}

	@Test
	public void testAddPreFilteredGids_MultipleNameFiltersWithIntersection() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		// Filter 1 - male parent preferred name
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(this.maleParentPreferredName.getNval());
		request.setNameFilter(nameFilter);
		request.setMaleParentName(nameFilter);

		// Filter 2 - filter by name type which will yield 1 common GID with matches from Filter 1
		request.setNameTypes(Collections.singletonMap(this.nameType, this.preferredName.getNval()));
		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertFalse(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertEquals(1, preFilteredGids.size());
		Assert.assertEquals(this.germplasmGID, preFilteredGids.get(0));
	}

	@Test
	public void testAddPreFilteredGids_MultipleNameFiltersNoIntersection() {
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		// Filter 1 - male parent preferred name
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		nameFilter.setValue(this.maleParentPreferredName.getNval());
		request.setNameFilter(nameFilter);
		request.setMaleParentName(nameFilter);

		// Filter 2 - get by name type but the match will yield no intersection with matches from Filter 1
		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, 1, 2, 2, 0, 1, GermplasmSearchDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1,
				"LocationName");
		final Integer tempFemaleParentGID = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		request.setNameTypes(Collections.singletonMap(this.nameType, germplasm.getPreferredName().getNval()));

		final List<Integer> preFilteredGids = new ArrayList<>();
		Assert.assertTrue(this.dao.addPreFilteredGids(request, preFilteredGids, this.programUUID));
		Assert.assertTrue(preFilteredGids.isEmpty());
	}

	private void initializeGermplasms() {
		final Germplasm fParent =
			GermplasmTestDataInitializer.createGermplasm(this.germplasmDate, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		this.femaleParentGID = this.germplasmTestDataGenerator.addGermplasm(fParent, fParent.getPreferredName(), this.cropType);
		this.femaleParentPreferredName = fParent.getPreferredName();

		final Germplasm mParent =
			GermplasmTestDataInitializer.createGermplasm(this.germplasmDate, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		this.maleParentGID = this.germplasmTestDataGenerator.addGermplasm(mParent, mParent.getPreferredName(), this.cropType);
		this.maleParentPreferredName = mParent.getPreferredName();

		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.femaleParentGID, this.maleParentGID, 2, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID,
				1,
				"LocationName");

		// Create Germplasm and add Preferred Name
		this.germplasmGID = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		this.preferredName = germplasm.getPreferredName();

		// Add Preferred Id, nstat = 8 means the name is preferred Id
		this.preferredId = GermplasmTestDataInitializer.createGermplasmName(this.germplasmGID, "Preferred Id of " + this.germplasmGID, GermplasmNameType.LINE_NAME.getUserDefinedFieldID());
		this.preferredId.setNstat(8);
		this.nameDAO.save(this.preferredId);

		// Add name of CODE1 type
		this.code1NameTypeValue = "Code1 Name of " + this.germplasmGID;
		final UserDefinedField nameType = this.userDefinedFieldDao.getByTableTypeAndCode("NAMES", "NAME", DERIVATIVE_NAME_CODE);
		final Name code1Name =
			GermplasmTestDataInitializer.createGermplasmName(this.germplasmGID, this.code1NameTypeValue, nameType.getFldno());
		code1Name.setNstat(0);
		this.nameDAO.save(code1Name);

		// Add NOTE attribute
		final VariableFilter attributeVariableTypeFilter = new VariableFilter();
		attributeVariableTypeFilter.addName(NOTE_ATTRIBUTE);
		attributeVariableTypeFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
		final List<Variable> attributeDTOs = this.ontologyVariableDataManager.getWithFilter(attributeVariableTypeFilter);

		this.attributeValue = "Attribute of " + this.germplasmGID;
		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(this.germplasmGID);
		attribute.setTypeId(attributeDTOs.get(0).getId());
		attribute.setAval(this.attributeValue);
		attribute.setAdate(this.germplasmDate);

		this.daoFactory.getAttributeDAO().saveOrUpdate(attribute);

		final Lot lot = new Lot();
		lot.setEntityType("GERMPLSM");
		lot.setLocationId(0);
		lot.setScaleId(TermId.SEED_AMOUNT_G.getId());
		lot.setEntityId(this.germplasmGID);
		lot.setStatus(0);
		lot.setStockId(RandomStringUtils.randomAlphabetic(35));
		this.daoFactory.getLotDao().save(lot);

		final Transaction transaction = new Transaction();
		transaction.setQuantity(100.0);
		transaction.setStatus(TransactionStatus.CONFIRMED.getIntValue());
		transaction.setLot(lot);
		transaction.setType(TransactionType.DEPOSIT.getId());
		this.daoFactory.getTransactionDAO().save(transaction);

		final Germplasm mgMember = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.femaleParentGID, this.maleParentGID, 2, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID,
				1,
				"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(mgMember, mgMember.getPreferredName(), this.cropType);

	}

	private List<Integer> createTestGermplasmForSorting() {

		final List<Integer> testGermplasmGIDs = new ArrayList<>();
		final Integer tempGermplasmDate = 20150101;

		for (int i = 1; i <= 5; i++) {
			final int random = RandomUtils.nextInt();

			final Germplasm fParent = GermplasmTestDataInitializer
				.createGermplasm(tempGermplasmDate, 1, 2, 2, 0, 1, GermplasmSearchDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1,
					"LocationName" + random);
			final Integer tempFemaleParentGID = this.germplasmTestDataGenerator.addGermplasm(fParent, fParent.getPreferredName(), this.cropType);

			final Germplasm mParent = GermplasmTestDataInitializer
				.createGermplasm(tempGermplasmDate, 1, 2, 2, 0, 1, GermplasmSearchDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1,
					"LocationName" + random);
			final Integer tempMaleParentGID = this.germplasmTestDataGenerator.addGermplasm(mParent, mParent.getPreferredName(), this.cropType);

			final Germplasm germplasm = GermplasmTestDataInitializer
				.createGermplasm(tempGermplasmDate, tempFemaleParentGID, tempMaleParentGID, 2, 0, 1,
					GermplasmSearchDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, random, 1,
					"LocationName");

			// Create Germplasm and add Preferred Name
			germplasm.getPreferredName().setNval("GermplasmForSorting" + random);
			final Integer tempGermplasmGid = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
			testGermplasmGIDs.add(tempGermplasmGid);

			// Add Preferred Id, nstat = 8 means the name is preferred Id
			final Name tempPreferredId =
				GermplasmTestDataInitializer.createGermplasmName(tempGermplasmGid, "Preferred Id of " + tempGermplasmGid, GermplasmNameType.LINE_NAME.getUserDefinedFieldID());
			tempPreferredId.setNstat(8);
			this.nameDAO.save(tempPreferredId);

			// Add name of CODE1 type
			final Name code1Name =
				GermplasmTestDataInitializer.createGermplasmName(tempGermplasmGid, "Code1 Name of " + tempGermplasmGid, GermplasmNameType.CULTIVAR_NAME.getUserDefinedFieldID());
			code1Name.setNstat(0);
			this.nameDAO.save(code1Name);

			// Add NOTE attribute
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.addName(NOTE_ATTRIBUTE);
			variableFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
			final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);

			final Attribute attribute = new Attribute();
			attribute.setGermplasmId(tempGermplasmGid);
			attribute.setTypeId(variables.get(0).getId());
			attribute.setAval("Attribute of " + tempGermplasmGid);
			attribute.setAdate(tempGermplasmDate);

			this.daoFactory.getAttributeDAO().saveOrUpdate(attribute);

		}

		return testGermplasmGIDs;

	}

	private GermplasmSearchRequest createSearchRequest(final String searchKeyword, final SqlTextFilter.Type type) {

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		final SqlTextFilter nameFilter = new SqlTextFilter();
		nameFilter.setType(type);
		nameFilter.setValue(searchKeyword);
		request.setNameFilter(nameFilter);
		return request;
	}

	private GermplasmSearchRequest createSearchRequest(final Integer germplasmGID) {

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setGids(Collections.singletonList(germplasmGID));
		return request;
	}

	// TODO remove sortState dependency
	private void mockSortState(final String[] columns, final boolean[] orders) {
		if (columns != null && orders != null) {
			final Sort.Order order = new Sort.Order(orders[0] ? Sort.Direction.ASC : Sort.Direction.DESC, columns[0]);
			when(this.pageable.getSort()).thenReturn(new Sort(order));
		}
	}

	/**
	 * Method to assert fields contained by germplasm search results.
	 */
	private void assertPossibleGermplasmFields(final List<GermplasmSearchResponse> results) {
		for (final GermplasmSearchResponse response : results) {
			Assert.assertEquals("Result should contain Method Name", "Unknown generative method", response.getMethodName());
			Assert.assertEquals("Result should contain Location Name", "Afghanistan", response.getLocationName());
		}
	}

	private void assertInventoryFields(final List<GermplasmSearchResponse> results) {
		for (final GermplasmSearchResponse response : results) {
			Assert.assertEquals("Lot count should be 1", Integer.valueOf(1), response.getLotCount());
			Assert.assertEquals("Total Available Balance should be 100.0",
				Double.valueOf(100.0), Double.valueOf(response.getAvailableBalance()));
		}
	}

	private void assertAddedGermplasmFields(final GermplasmSearchResponse germplasm, final List<String> propertyIds) {

		if (propertyIds.contains(GermplasmSearchDAO.PREFERRED_ID)) {
			Assert.assertEquals("Result germplasm should contain Preferred ID", this.preferredId.getNval(),
				germplasm.getGermplasmPreferredId());
		} else {
			Assert.assertTrue("Result germplasm should not contain Preferred ID", StringUtils.isEmpty(germplasm.getGermplasmPreferredId()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.PREFERRED_NAME)) {
			Assert.assertEquals("Result germplasm should contain Preferred Name", this.preferredName.getNval(),
				germplasm.getGermplasmPreferredName());
		} else {
			Assert.assertTrue("Result germplasm should not contain Preferred Name",
				StringUtils.isEmpty(germplasm.getGermplasmPreferredName()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.GERMPLASM_DATE)) {
			Assert.assertEquals("Result germplasm should contain Germplasm Date", String.valueOf(this.germplasmDate),
				germplasm.getGermplasmDate());
		} else {
			Assert.assertTrue("Result germplasm should not contain Germplasm Date", StringUtils.isEmpty(germplasm.getGermplasmDate()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_ABBREVIATION)) {
			Assert.assertEquals("Result germplasm should contain Method Abbreviation", "UGM", germplasm.getMethodCode());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Abbreviation", StringUtils.isEmpty(germplasm.getMethodCode()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_NUMBER)) {
			Assert.assertEquals("Result germplasm should contain Method Number", String.valueOf(1), germplasm.getMethodNumber());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Number", StringUtils.isEmpty(germplasm.getMethodCode()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_GROUP)) {
			Assert.assertEquals("Result germplasm should contain Method Group", "G", germplasm.getMethodGroup());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Group", StringUtils.isEmpty(germplasm.getMethodGroup()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.GERMPLASM_DATE)) {
			Assert.assertEquals(String.valueOf(this.germplasmDate), germplasm.getGermplasmDate());
		}

		if (propertyIds.contains(GermplasmSearchDAO.IMMEDIATE_SOURCE_GID)) {
			Assert.assertEquals("Result germplasm should contain Immediate Source GID", String.valueOf(this.maleParentGID),
				germplasm.getImmediateSourceGID());
		} else {
			Assert.assertTrue("Result germplasm should not contain Immediate Source GID",
				StringUtils.isEmpty(germplasm.getImmediateSourceGID()));
		}

		if (propertyIds.contains(GermplasmSearchDAO.IMMEDIATE_SOURCE_NAME)) {
			Assert.assertEquals("Result germplasm should contain Immediate Source Preferred Name", this.maleParentPreferredName.getNval(),
				germplasm.getImmediateSourceName());
		} else {
			Assert.assertTrue("Result germplasm should not contain Immediate Source Preferred Name",
				StringUtils.isEmpty(germplasm.getImmediateSourceName()));
		}

		if (propertyIds.contains(NOTE_ATTRIBUTE)) {
			Assert.assertEquals("Result germplasm should contain Note", this.attributeValue,
				germplasm.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE));
		} else {
			Assert.assertFalse("Result germplasm should not contain Note attribute",
				germplasm.getAttributeTypesValueMap().containsKey(NOTE_ATTRIBUTE));
		}

		if (propertyIds.contains(DERIVATIVE_NAME)) {
			Assert.assertEquals("Result germplasm should contain CODE1 Name", this.code1NameTypeValue,
				germplasm.getNameTypesValueMap().get(DERIVATIVE_NAME));
		} else {
			Assert.assertFalse("Result germplasm should not contain CODE1 Name",
				germplasm.getNameTypesValueMap().containsKey(DERIVATIVE_NAME));
		}
	}

	private void createPedigree() {
		this.greatGrandParentGermplasm =
			GermplasmTestDataInitializer.createGermplasm(this.germplasmDate, 1, 2, 2, 0, 1, 1, 0, 1, "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(this.greatGrandParentGermplasm, this.greatGrandParentGermplasm.getPreferredName(), this.cropType);

		this.grandParentGermplasm = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.greatGrandParentGermplasm.getGid(), 2, 2, 0, 1, 1, 0, 1,
				"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(this.grandParentGermplasm, this.grandParentGermplasm.getPreferredName(), this.cropType);

		this.groupSource = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.grandParentGermplasm.getGid(), 2, 2, 0, 1, 1, 0, 1,
				"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(this.groupSource, this.groupSource.getPreferredName(), this.cropType);

		this.descendant = GermplasmTestDataInitializer
			.createGermplasm(this.germplasmDate, this.groupSource.getGid(), this.groupSource.getGid(), -1, 0, 1, 1, 0, 1,
				"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(this.descendant, this.descendant.getPreferredName(), this.cropType);
	}

	private GermplasmSearchRequest getGermplasmSearchRequest(final Set<Integer> gids) {
		final GermplasmSearchRequest searchRequest = new GermplasmSearchRequest();
		searchRequest.setGids(new ArrayList<>(gids));

		return searchRequest;
	}
}
