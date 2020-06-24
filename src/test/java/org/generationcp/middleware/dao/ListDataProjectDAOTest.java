package org.generationcp.middleware.dao;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ListDataProjectDAOTest extends IntegrationTestBase {

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private static final int NO_OF_TEST_ENTRIES = 1;

	private ListDataProjectDAO listDataProjectDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private DaoFactory daoFactory;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	private Germplasm parentGermplasm;
	private GermplasmList germplasmList;
	private List<ListDataProject> listDataProjects;
	private final String cropPrefix = "ABCD";

	@Before
	public void beforeTest() {

		this.daoFactory = new DaoFactory(this.sessionProvder);
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}

		this.germplasmList = this.createGermplasmList();
		this.listDataProjects = this.createTestListDataProjects(germplasmList, NO_OF_TEST_ENTRIES);
		this.saveListDataProjects(this.listDataProjects);

	}

	@Test
	public void testGetByListId() {

		// get result of method being tested
		final List<ListDataProject> savedListDataProjects = this.daoFactory.getListDataProjectDAO().getByListId(this.germplasmList.getId());

		// Verify # or record received and the returned result again
		Assert.assertNotNull("The list data project should not be null", savedListDataProjects);
		Assert.assertEquals("Expecting # of list data project records to be " + NO_OF_TEST_ENTRIES,
			NO_OF_TEST_ENTRIES, this.listDataProjects.size());

		final ListDataProject testListDataProject = this.listDataProjects.get(0);
		final ListDataProject firstListDataProject = savedListDataProjects.get(0);

		Assert.assertEquals("The designation must be " + testListDataProject.getDesignation(),
			testListDataProject.getDesignation(), firstListDataProject.getDesignation());
		Assert.assertEquals("The groupName must be " + testListDataProject.getGroupName(), testListDataProject.getGroupName(),
			firstListDataProject.getGroupName());
		Assert.assertEquals("The GID must be " + testListDataProject.getGermplasmId(), testListDataProject.getGermplasmId(),
			firstListDataProject.getGermplasmId());
		Assert.assertEquals("The seedSource must be " + testListDataProject.getSeedSource(), testListDataProject.getSeedSource(),
			firstListDataProject.getSeedSource());
		Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(), testListDataProject.getDuplicate(),
			firstListDataProject.getDuplicate());
		Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(), testListDataProject.getDuplicate(),
			firstListDataProject.getDuplicate());
		Assert.assertEquals("The checkType must be " + testListDataProject.getCheckType(), testListDataProject.getCheckType(),
			firstListDataProject.getCheckType());
		Assert.assertEquals("The entryCode must be " + testListDataProject.getEntryCode(), testListDataProject.getEntryCode(),
			firstListDataProject.getEntryCode());
	}

	@Test
	public void testGetListDataProjectWithParents() {

		// get result of method being tested
		final List<ListDataProject> savedListDataProjects =
			this.daoFactory.getListDataProjectDAO().getListDataProjectWithParents(this.germplasmList.getId());

		// Verify returned result
		Assert.assertNotNull("The list data project should not be null", savedListDataProjects);

		final ListDataProject testListDataProject = this.listDataProjects.get(0);
		final ListDataProject firstListDataProject = savedListDataProjects.get(0);

		for (final ListDataProject listDataProject : savedListDataProjects) {
			final String expectedPreferredName = DataSetupTest.GERMPLSM_PREFIX + listDataProject.getEntryId();
			Assert.assertEquals("The preferred name must be " + expectedPreferredName, expectedPreferredName,
				listDataProject.getDesignation());

			// test at least one of the records for the other fields
			if (testListDataProject.getListDataProjectId().equals(listDataProject.getListDataProjectId())) {
				Assert.assertEquals("The entryId must be " + testListDataProject.getEntryId(), testListDataProject.getEntryId(),
					listDataProject.getEntryId());
				Assert.assertEquals("The designation must be " + testListDataProject.getDesignation(),
					testListDataProject.getDesignation(), listDataProject.getDesignation());
				Assert.assertEquals("The groupName must be " + testListDataProject.getGroupName(),
					testListDataProject.getGroupName(), listDataProject.getGroupName());
				Assert.assertEquals("The groupId must be " + testListDataProject.getGroupId(), testListDataProject.getGroupId(),
					listDataProject.getGroupId());
				Assert.assertEquals("The germplasmId must be " + testListDataProject.getGermplasmId(),
					testListDataProject.getGermplasmId(), listDataProject.getGermplasmId());
				Assert.assertEquals("The seedSource must be " + testListDataProject.getSeedSource(),
					testListDataProject.getSeedSource(), listDataProject.getSeedSource());
				Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(),
					testListDataProject.getDuplicate(), listDataProject.getDuplicate());
				Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(),
					testListDataProject.getDuplicate(), listDataProject.getDuplicate());
				Assert.assertEquals("The checkType must be " + testListDataProject.getCheckType(),
					testListDataProject.getCheckType(), listDataProject.getCheckType());
				Assert.assertEquals("The entryCode must be " + testListDataProject.getEntryCode(),
					testListDataProject.getEntryCode(), listDataProject.getEntryCode());

				// check parent germplasm values
				final String parentPreferredName = this.parentGermplasm.getPreferredName().getNval();
				Assert.assertEquals("The Female Parent Designation must be " + parentPreferredName, parentPreferredName,
					listDataProject.getFemaleParentDesignation());
				Assert.assertEquals("The Female Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
					listDataProject.getFemaleGid());
				Assert.assertEquals("The Male Parent Designation must be " + parentPreferredName, parentPreferredName,
					listDataProject.getMaleParentDesignation());
				Assert.assertEquals("The Male Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
					listDataProject.getMaleGid());
			}
		}

	}

	@Test
	public void testGetByListIdAndGidNotNull() {

		// get result of method being tested
		final List<ListDataProject> savedListDataProjects =
			this.daoFactory.getListDataProjectDAO().getListDataProjectWithParents(this.germplasmList.getId());
		for (final ListDataProject listDataProject : savedListDataProjects) {
			Assert.assertNotNull(
				this.daoFactory.getListDataProjectDAO().getByListIdAndGid(this.germplasmList.getId(), listDataProject.getGermplasmId()));
		}
	}

	@Test
	public void testGetByListIdAndGidNull() {
		Assert.assertNull(
			this.daoFactory.getListDataProjectDAO().getByListIdAndGid(this.germplasmList.getId(), this.parentGermplasm.getGid()));
	}

	private ListDataProject createListDataProject(final GermplasmList germplasmList, final int gid,
		final SystemDefinedEntryType systemDefinedEntryType) {
		final ListDataProject listDataProject = new ListDataProject();
		listDataProject.setCheckType(systemDefinedEntryType.getEntryTypeCategoricalId());
		listDataProject.setSeedSource("");
		listDataProject.setList(germplasmList);
		listDataProject.setGermplasmId(gid);
		listDataProject.setDesignation(DataSetupTest.GERMPLSM_PREFIX + 1);
		listDataProject.setEntryCode("-");
		listDataProject.setEntryId(1);
		listDataProject.setGroupName("-");
		listDataProject.setGroupId(0);
		return listDataProject;
	}

	private List<ListDataProject> createTestListDataProjects(final GermplasmList germplasmList, final int noOfEntries) {

		final List<ListDataProject> listDataProjects = new ArrayList<>();

		this.parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(noOfEntries, ListDataProjectDAOTest.GERMPLASM_PREFERRED_NAME_PREFIX,
				this.parentGermplasm);

		final List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();
		for (int i = 0; i < noOfEntries; i++) {
			listDataProjects.add(this.createListDataProject(germplasmList, gids[i], SystemDefinedEntryType.TEST_ENTRY));
		}

		return listDataProjects;
	}

	private GermplasmList createGermplasmList() {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List ",
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		germplasmList.setProgramUUID(programUUID);
		this.germplasmListManager.addGermplasmList(germplasmList);
		return germplasmList;
	}

	private void saveListDataProjects(final List<ListDataProject> listDataProjects) {
		for (final ListDataProject listDataProject : listDataProjects) {
			this.daoFactory.getListDataProjectDAO().save(listDataProject);
		}
	}

}
