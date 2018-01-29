package org.generationcp.middleware.dao;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.operation.saver.ListDataProjectSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListDataProjectDAOTest extends IntegrationTestBase {

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";

	private ListDataProjectSaver listDataProjectSaver;
	private ListDataProjectDAO listDataProjectDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private DataSetupTest dataSetupTest;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	private Germplasm parentGermplasm;
	private ListDataProject testListDataProject;
	private Integer studyId;
	private final String cropPrefix = "ABCD";

	@Before
	public void beforeTest() {
		
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.listDataProjectSaver = new ListDataProjectSaver(this.sessionProvder);
		this.listDataProjectDAO.setSession(this.sessionProvder.getSession());
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);

		// setup test data
		studyId = this.createNurseryTestData();
		this.testListDataProject = this.listDataProjectDAO.getByStudy(studyId, GermplasmListType.NURSERY, 0);
	}

	@Test
	public void testGetByListId() throws Exception {
		// get result of method being tested
		final Integer listId = this.testListDataProject.getList().getId();
		final List<ListDataProject> listDataProjects = this.listDataProjectDAO.getByListId(listId);

		// Verify # or record received and the returned result again
		// testListDataProject
		Assert.assertNotNull("The list data project should not be null", listDataProjects);
		Assert.assertEquals("Expecting # of list data project records to be " + DataSetupTest.NUMBER_OF_GERMPLASM,
				DataSetupTest.NUMBER_OF_GERMPLASM, listDataProjects.size());
		final ListDataProject firstListDataProject = listDataProjects.get(0);
		Assert.assertEquals("The designation must be " + this.testListDataProject.getDesignation(),
				this.testListDataProject.getDesignation(), firstListDataProject.getDesignation());
		Assert.assertEquals("The groupName must be " + this.testListDataProject.getGroupName(), this.testListDataProject.getGroupName(),
				firstListDataProject.getGroupName());
		Assert.assertEquals("The GID must be " + this.testListDataProject.getGermplasmId(), this.testListDataProject.getGermplasmId(),
				firstListDataProject.getGermplasmId());
		Assert.assertEquals("The seedSource must be " + this.testListDataProject.getSeedSource(), this.testListDataProject.getSeedSource(),
				firstListDataProject.getSeedSource());
		Assert.assertEquals("The duplicate must be " + this.testListDataProject.getDuplicate(), this.testListDataProject.getDuplicate(),
				firstListDataProject.getDuplicate());
		Assert.assertEquals("The duplicate must be " + this.testListDataProject.getDuplicate(), this.testListDataProject.getDuplicate(),
				firstListDataProject.getDuplicate());
		Assert.assertEquals("The checkType must be " + this.testListDataProject.getCheckType(), this.testListDataProject.getCheckType(),
				firstListDataProject.getCheckType());
		Assert.assertEquals("The entryCode must be " + this.testListDataProject.getEntryCode(), this.testListDataProject.getEntryCode(),
				firstListDataProject.getEntryCode());
	}

	@Test
	public void testGetListDataProjectWithParents() {
		// get result of method being tested
		final Integer listId = this.testListDataProject.getList().getId();
		final List<ListDataProject> listDataProjects = this.listDataProjectDAO.getListDataProjectWithParents(listId);

		// Verify returned result
		Assert.assertNotNull("The list data project should not be null", listDataProjects);
		for (final ListDataProject listDataProject : listDataProjects) {
			final String expectedPreferredName = DataSetupTest.GERMPLSM_PREFIX + listDataProject.getEntryId();
			Assert.assertEquals("The preferred name must be " + expectedPreferredName, expectedPreferredName,
					listDataProject.getDesignation());

			// test at least one of the records for the other fields
			if (this.testListDataProject.getListDataProjectId().equals(listDataProject.getListDataProjectId())) {
				Assert.assertEquals("The entryId must be " + this.testListDataProject.getEntryId(), this.testListDataProject.getEntryId(),
						listDataProject.getEntryId());
				Assert.assertEquals("The designation must be " + this.testListDataProject.getDesignation(),
						this.testListDataProject.getDesignation(), listDataProject.getDesignation());
				Assert.assertEquals("The groupName must be " + this.testListDataProject.getGroupName(),
						this.testListDataProject.getGroupName(), listDataProject.getGroupName());
				Assert.assertEquals("The groupId must be " + this.testListDataProject.getGroupId(), this.testListDataProject.getGroupId(),
						listDataProject.getGroupId());
				Assert.assertEquals("The germplasmId must be " + this.testListDataProject.getGermplasmId(),
						this.testListDataProject.getGermplasmId(), listDataProject.getGermplasmId());
				Assert.assertEquals("The seedSource must be " + this.testListDataProject.getSeedSource(),
						this.testListDataProject.getSeedSource(), listDataProject.getSeedSource());
				Assert.assertEquals("The duplicate must be " + this.testListDataProject.getDuplicate(),
						this.testListDataProject.getDuplicate(), listDataProject.getDuplicate());
				Assert.assertEquals("The duplicate must be " + this.testListDataProject.getDuplicate(),
						this.testListDataProject.getDuplicate(), listDataProject.getDuplicate());
				Assert.assertEquals("The checkType must be " + this.testListDataProject.getCheckType(),
						this.testListDataProject.getCheckType(), listDataProject.getCheckType());
				Assert.assertEquals("The entryCode must be " + this.testListDataProject.getEntryCode(),
						this.testListDataProject.getEntryCode(), listDataProject.getEntryCode());

				// check parent germplasm values
				final String parentPreferredName = this.parentGermplasm.getPreferredName().getNval();
				Assert.assertEquals("The Female Parent Designation must be " + parentPreferredName, parentPreferredName,
						listDataProject.getFemaleParent());
				Assert.assertEquals("The Female Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
						listDataProject.getFgid());
				Assert.assertEquals("The Male Parent Designation must be " + parentPreferredName, parentPreferredName,
						listDataProject.getMaleParent());
				Assert.assertEquals("The Male Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
						listDataProject.getMgid());
			}
		}

	}

	@Test
	public void testCountByListIdAndEntryType() {

		final int userId = 2;
		final long noOfTestEntries = 3;
		final long noOfCheckEntries = 4;
		final int listId = this.testListDataProject.getList().getId();

		final List<ListDataProject> listDataProjects =
				this.createListDataProject(this.testListDataProject.getList(), noOfTestEntries, noOfCheckEntries);
		listDataProjectSaver.saveOrUpdateListDataProject(studyId, GermplasmListType.NURSERY, listId, listDataProjects, userId,
				this.testListDataProject.getList().getProgramUUID(), this.testListDataProject.getList().getStatus());

		Assert.assertEquals(String.format("There are only {0} check entries in the list", noOfCheckEntries), noOfCheckEntries,
				this.listDataProjectDAO.countByListIdAndEntryType(listId, SystemDefinedEntryType.CHECK_ENTRY));
		Assert.assertEquals(String.format("There are only {0} test entries in the list", noOfTestEntries), noOfTestEntries,
				this.listDataProjectDAO.countByListIdAndEntryType(listId, SystemDefinedEntryType.TEST_ENTRY));

	}

	@Test
	public void testGetGermplasmUsedInEntryListTrue() {
		final Integer listId = this.testListDataProject.getList().getId();
		final List<ListDataProject> listDataProjects = this.middlewareFieldbookService.getListDataProject(listId);

		List<Integer> gids = Lists.transform(listDataProjects, new Function<ListDataProject, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable ListDataProject listDataProject) {
				return listDataProject.getGermplasmId();
			}
		});

		Assert.assertTrue(this.listDataProjectDAO.getGermplasmUsedInEntryList(gids).size() > 0);
	}

	@Test
	public void testGetGermplasmUsedInEntryListFalse() {
		Assert.assertFalse(this.listDataProjectDAO.getGermplasmUsedInEntryList(Arrays.asList(this.parentGermplasm.getGid())).size() > 0);
	}

	@Test
	public void testGetByListIdAndGidNotNull() {
		final Integer listId = this.testListDataProject.getList().getId();
		final List<ListDataProject> listDataProjects = this.middlewareFieldbookService.getListDataProject(listId);

		for (ListDataProject listDataProject : listDataProjects) {
			Assert.assertNotNull(this.listDataProjectDAO.getByListIdAndGid(listId, listDataProject.getGermplasmId()));
		}
	}

	@Test
	public void testGetByListIdAndGidNull() {
		final Integer listId = this.testListDataProject.getList().getId();
		Assert.assertNull(this.listDataProjectDAO.getByListIdAndGid(listId, this.parentGermplasm.getGid()));
	}

	private List<ListDataProject> createListDataProject(final GermplasmList germplasmList, final long noOfTestEntries, final long noOfCheckEntries) {

		final List<ListDataProject> listDataProjects = new ArrayList<>();
		for (int i = 0; i < noOfCheckEntries; i++) {
			listDataProjects.add(createListDataProject(germplasmList, SystemDefinedEntryType.CHECK_ENTRY));
		}
		for (int i = 0; i < noOfTestEntries; i++) {
			listDataProjects.add(createListDataProject(germplasmList, SystemDefinedEntryType.TEST_ENTRY));
		}

		return listDataProjects;

	}

	private ListDataProject createListDataProject(final GermplasmList germplasmList, final SystemDefinedEntryType systemDefinedEntryType) {

		final ListDataProject listDataProject = new ListDataProject();
		listDataProject.setCheckType(systemDefinedEntryType.getEntryTypeCategoricalId());
		listDataProject.setSeedSource("");
		listDataProject.setList(germplasmList);
		listDataProject.setGermplasmId(1);

		return listDataProject;

	}

	/*
	 * Create nursery to create proper listdataproject records. Would be needing
	 * nursery as well for refactoring on ListDataProject.getByStudy method
	 * later on
	 */
	private int createNurseryTestData() {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		this.parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final Integer[] gids = this.germplasmTestDataGenerator
				.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, ListDataProjectDAOTest.GERMPLASM_PREFERRED_NAME_PREFIX,
						this.parentGermplasm);

		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(programUUID, gids, cropPrefix);

		return nurseryId;
	}

}
