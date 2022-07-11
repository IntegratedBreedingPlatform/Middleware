package org.generationcp.middleware.api.germplasmlist;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchRequest;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchResponse;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GermplasmListServiceIntegrationTest extends IntegrationTestBase {

	private static final String SINGLE_CROSS_METHOD = "C2W";
	private static final String DOUBLE_CROSS_METHOD = "CDC";
	private static final String TEST_LIST_1_PARENT = "Test List #1 Parent";
	private static final Integer USER_ID = new Random().nextInt();
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Autowired
	private GermplasmListService germplasmListService;

	@Autowired
	private GermplasmListDataService germplasmListDataService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	private Integer parentFolderId;

	private DaoFactory daoFactory;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		//Create parent folder
		final GermplasmListTestDataInitializer germplasmListTDI = new GermplasmListTestDataInitializer();
		final GermplasmList germplasmListParent = germplasmListTDI
			.createGermplasmList(TEST_LIST_1_PARENT, USER_ID, "Test Parent List #1", null, 1,
				PROGRAM_UUID);
		this.parentFolderId = this.germplasmListManager.addGermplasmList(germplasmListParent);
	}

	@Test
	public void shouldCreateAndUpdateAndGetAndDeleteGermplasmListFolder_OK() {

		final String folderName = "NewFolder";

		assertFalse(this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID).isPresent());

		//Create germplasm folder
		final Integer germplasmListNewFolderId =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListNewFolderId);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListNewFolderId);
		assertTrue(newGermplasmListById.isPresent());
		final GermplasmList newGermplasmList = newGermplasmListById.get();
		this.assertGermplasmList(newGermplasmList, germplasmListNewFolderId, folderName);

		//Get the created germplasm folder by folder name and parent id
		final Optional<GermplasmList> germplasmListByParentAndName =
			this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByParentAndName.isPresent());
		assertThat(germplasmListByParentAndName.get().getId(), is(germplasmListNewFolderId));

		//Update germplasm folder
		final String updatedFolderName = "updatedFolderName";
		final Integer germplasmListUpdatedFolderId =
			this.germplasmListService.updateGermplasmListFolder(updatedFolderName, germplasmListNewFolderId);
		assertNotNull(germplasmListUpdatedFolderId);
		assertThat(germplasmListUpdatedFolderId, is(germplasmListNewFolderId));

		//Get the updated germplasm folder by id
		final Optional<GermplasmList> updatedGermplasmListById =
			this.germplasmListService.getGermplasmListById(germplasmListUpdatedFolderId);
		assertTrue(updatedGermplasmListById.isPresent());
		final GermplasmList updatedGermplasmList = updatedGermplasmListById.get();
		this.assertGermplasmList(updatedGermplasmList, germplasmListUpdatedFolderId, updatedFolderName);

		//Delete the germplasm folder
		this.germplasmListService.deleteGermplasmListFolder(germplasmListUpdatedFolderId);

		//Should not get the deleted germplasm folder
		final Optional<GermplasmList> deletedGermplasmListById =
			this.germplasmListService.getGermplasmListById(germplasmListUpdatedFolderId);
		assertFalse(deletedGermplasmListById.isPresent());
	}

	@Test
	public void shouldMoveGermplasmListFolder_OK() {

		//Create germplasm folder 1
		final String folderName1 = "folderName1";
		final Integer newFolderId1 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName1, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(newFolderId1);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById1 = this.germplasmListService.getGermplasmListById(newFolderId1);
		assertTrue(newGermplasmListById1.isPresent());
		final GermplasmList newGermplasmList1 = newGermplasmListById1.get();
		this.assertGermplasmList(newGermplasmList1, newFolderId1, folderName1);
		assertThat(newGermplasmList1.getParentId(), is(this.parentFolderId));

		//Create germplasm folder 2
		final String folderName2 = "folderName2";
		final Integer newFolderId2 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName2, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(newFolderId2);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById2 = this.germplasmListService.getGermplasmListById(newFolderId2);
		assertTrue(newGermplasmListById2.isPresent());
		final GermplasmList newGermplasmList2 = newGermplasmListById2.get();
		this.assertGermplasmList(newGermplasmList2, newFolderId2, folderName2);
		assertThat(newGermplasmList2.getParentId(), is(this.parentFolderId));

		//Move folder 1 to folder 2
		final GermplasmList germplasmList = this.germplasmListService.moveGermplasmListFolder(newFolderId1, newFolderId2, PROGRAM_UUID);
		assertNotNull(germplasmList.getId());
		assertThat(germplasmList.getId(), is(newFolderId1));

		//Get the moved folder
		final Optional<GermplasmList> movedFolderById = this.germplasmListService.getGermplasmListById(newFolderId1);
		assertTrue(movedFolderById.isPresent());
		final GermplasmList movedFolder = movedFolderById.get();
		assertThat(movedFolder.getParentId(), is(newFolderId2));
	}

	@Test
	public void shouldGetGermplasmListByIdAndProgramUUID_OK() {
		final Optional<GermplasmList> germplasmListByIdAndProgramUUID =
			this.germplasmListService.getGermplasmListByIdAndProgramUUID(this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByIdAndProgramUUID.isPresent());
		final GermplasmList parentGermplasmList = germplasmListByIdAndProgramUUID.get();
		assertThat(parentGermplasmList.getId(), is(this.parentFolderId));
		assertThat(parentGermplasmList.getProgramUUID(), is(PROGRAM_UUID));
	}

	@Test
	public void shouldGetGermplasmListByIdAndNullProgramUUID_OK() {
		final String folderName1 = "folderName1";
		final Integer newFolderId1 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName1, this.parentFolderId, null);
		assertNotNull(newFolderId1);

		final Optional<GermplasmList> germplasmListByIdAndProgramUUID =
			this.germplasmListService.getGermplasmListByIdAndProgramUUID(newFolderId1, null);
		assertTrue(germplasmListByIdAndProgramUUID.isPresent());
		final GermplasmList newGermplasmList = germplasmListByIdAndProgramUUID.get();
		assertThat(newGermplasmList.getId(), is(newFolderId1));
		assertNull(newGermplasmList.getProgramUUID());
	}

	@Test
	public void testAddGermplasmEntriesToList_WithSelectedItems_OK() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm existingGermplasmEntry = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, existingGermplasmEntry.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		//Create entry to add into existing list
		final Method doubleCrossMethod = this.daoFactory.getMethodDAO().getByCode(DOUBLE_CROSS_METHOD);
		final Germplasm addGermplasmEntry = this.createGermplasm(doubleCrossMethod);

		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite = new SearchCompositeDto<>();
		searchComposite.setItemIds(Sets.newHashSet(addGermplasmEntry.getGid()));
		this.germplasmListService.addGermplasmEntriesToList(germplasmList.getId(), searchComposite, PROGRAM_UUID);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(germplasmListById.isPresent());

		final GermplasmList actualGermplasmList = germplasmListById.get();
		assertThat(actualGermplasmList.getListData(), hasSize(2));

		final GermplasmListData actualEntry1 = actualGermplasmList.getListData().get(0);
		assertNotNull(actualEntry1);
		assertNotNull(actualEntry1.getListDataId());

		final GermplasmListData actualEntry2 = actualGermplasmList.getListData().get(1);
		assertNotNull(actualEntry2);
		assertNotNull(actualEntry2.getListDataId());
	}

	@Test
	public void testAddGermplasmEntriesToList_WithoutSelectedItems_OK() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm existingGermplasmEntry = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, existingGermplasmEntry.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		//Create entry to add into existing list
		final Method doubleCrossMethod = this.daoFactory.getMethodDAO().getByCode(DOUBLE_CROSS_METHOD);
		final Germplasm addGermplasmEntry = this.createGermplasm(doubleCrossMethod);

		final GermplasmSearchRequest germplasmSearchRequest = new GermplasmSearchRequest();
		germplasmSearchRequest.setGids(Arrays.asList(addGermplasmEntry.getGid()));
		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite = new SearchCompositeDto<>();
		searchComposite.setSearchRequest(germplasmSearchRequest);
		this.germplasmListService.addGermplasmEntriesToList(germplasmList.getId(), searchComposite, PROGRAM_UUID);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(germplasmListById.isPresent());

		final GermplasmList actualGermplasmList = germplasmListById.get();
		assertThat(actualGermplasmList.getListData(), hasSize(2));

		final GermplasmListData actualEntry1 = actualGermplasmList.getListData().get(0);
		assertNotNull(actualEntry1);
		assertNotNull(actualEntry1.getListDataId());

		final GermplasmListData actualEntry2 = actualGermplasmList.getListData().get(1);
		assertNotNull(actualEntry2);
		assertNotNull(actualEntry2.getListDataId());
	}

	@Test
	public void testGetGermplasmLists_OK() {
		//create germplasm
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);

		final List<GermplasmListDto> germplasmListDtos = this.germplasmListService.getGermplasmLists(germplasm.getGid());

		//create germplasm list
		final int randomInt = new Random().nextInt(100);
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, germplasm.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final List<GermplasmListDto> updatedGermplasmListDtos = this.germplasmListService.getGermplasmLists(germplasm.getGid());

		Assert.assertEquals(germplasmListDtos.size() + 1, updatedGermplasmListDtos.size());
	}

	@Test
	public void testToggleGermplasmListStatus_OK() {
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List ",
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		assertTrue(this.germplasmListService.toggleGermplasmListStatus(germplasmList.getId()));
		assertFalse(this.germplasmListService.toggleGermplasmListStatus(germplasmList.getId()));
	}

	@Test
	public void shouldCreateAndGetAndRemoveListVariables_Ok() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Variable
		final String variableName = RandomStringUtils.randomAlphabetic(20);
		final StandardVariable variable = this.createEntryDetailVariable(variableName);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);

		final List<Variable> variables =
			this.germplasmListService.getGermplasmListVariables(PROGRAM_UUID, germplasmList.getId(), VariableType.ENTRY_DETAIL.getId());
		final List<Integer> ontologyVariableIds = this.germplasmListService.getListOntologyVariables(germplasmList.getId(),
			Lists.newArrayList(VariableType.ENTRY_DETAIL.getId()));

		assertThat(variables, hasSize(1));
		assertEquals(ontologyVariableIds.size(), variables.size());
		assertEquals(variables.get(0).getName(), variableName);
		assertEquals(ontologyVariableIds.get(0), (Integer) variable.getId());

		this.germplasmListService.removeListVariables(germplasmList.getId(), Sets.newHashSet(variable.getId()));
		assertTrue(this.germplasmListService.getListOntologyVariables(germplasmList.getId(),
			Lists.newArrayList(VariableType.ENTRY_DETAIL.getId())).isEmpty());
	}

	@Test
	public void testGetGermplasmListData_Ok() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final Integer entryNumber = 1;
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, germplasm.getGid(), entryNumber);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final Optional<GermplasmListDataDto> germplasmListDataDtoOptional =
			this.germplasmListService.getGermplasmListData(germplasmListData.getListDataId());
		assertTrue(germplasmListDataDtoOptional.isPresent());

		final GermplasmListDataDto germplasmListDataDto = germplasmListDataDtoOptional.get();
		assertEquals(germplasmListDataDto.getListId(), germplasmList.getId());
		assertEquals(germplasmListDataDto.getListDataId(), germplasmListData.getListDataId());
		assertEquals(germplasmListDataDto.getEntryNumber(), entryNumber);
		assertEquals(germplasmListDataDto.getGid(), germplasm.getGid());
	}

	@Test
	public void shouldSaveAndUpdateAndDeleteListDataObservation_Ok() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final int entryNumber = 1;
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, germplasm.getGid(), entryNumber);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		// Variable
		final String variableName = RandomStringUtils.randomAlphabetic(20);
		final StandardVariable variable = this.createEntryDetailVariable(variableName);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);

		final String value = "1";
		final GermplasmListObservationRequestDto germplasmListObservationRequestDto =
			new GermplasmListObservationRequestDto(germplasmListData.getListDataId(), variable.getId(), value, null);

		final Integer listDataObservationId =
			this.germplasmListService.saveListDataObservation(germplasmList.getId(), germplasmListObservationRequestDto);
		this.assertGermplasmListDataObservationOptionalPresent(listDataObservationId, value, variable.getId(),
			germplasmListData.getListDataId());

		final String newValue = "2";
		this.germplasmListService.updateListDataObservation(listDataObservationId, newValue, null);
		this.assertGermplasmListDataObservationOptionalPresent(listDataObservationId, newValue, variable.getId(),
			germplasmListData.getListDataId());

		this.germplasmListService.deleteListDataObservation(listDataObservationId);
		assertFalse(this.germplasmListService.getListDataObservation(listDataObservationId).isPresent());
	}

	@Test
	public void testCountObservationsByVariables_Ok() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData1 = this.createGermplasmListData(germplasmList, germplasm.getGid(), 1);
		final GermplasmListData germplasmListData2 = this.createGermplasmListData(germplasmList, germplasm.getGid(), 2);

		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData2);

		// Variable
		final String variableName = RandomStringUtils.randomAlphabetic(20);
		final StandardVariable variable = this.createEntryDetailVariable(variableName);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);

		final String value = "1";
		final GermplasmListObservationRequestDto germplasmListObservationRequestDto1 =
			new GermplasmListObservationRequestDto(germplasmListData1.getListDataId(), variable.getId(), value, null);

		final GermplasmListObservationRequestDto germplasmListObservationRequestDto2 =
			new GermplasmListObservationRequestDto(germplasmListData2.getListDataId(), variable.getId(), value, null);

		this.germplasmListService.saveListDataObservation(germplasmList.getId(), germplasmListObservationRequestDto1);
		this.germplasmListService.saveListDataObservation(germplasmList.getId(), germplasmListObservationRequestDto2);

		final long count =
			this.germplasmListService.countObservationsByVariables(germplasmList.getId(), Lists.newArrayList((Integer) variable.getId()));
		assertEquals(2l, count);
	}

	@Test
	public void testDeleteGermplasmList_OK() {
		final int randomInt = new Random().nextInt(100);
		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		this.sessionProvder.getSession().flush();
		this.germplasmListService.deleteGermplasmList(germplasmList.getId());

		final Optional<GermplasmList> deletedGermplasmList = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(deletedGermplasmList.isPresent());
		Assert.assertEquals(GermplasmList.Status.DELETED.getCode(), deletedGermplasmList.get().getStatus().intValue());
	}

	@Test
	public void testAddGermplasmListEntriesToAnotherList_OK() {
		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + new Random().nextInt(100),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Source Germplasm list
		final GermplasmList sourceGermplasmList = new GermplasmList(null, "Test Germplasm List " + new Random().nextInt(100),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(sourceGermplasmList);
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm1 = this.createGermplasm(singleCrossMethod);
		final Germplasm germplasm2 = this.createGermplasm(singleCrossMethod);

		// Add ENTRY_NO Entry Detail Variable.
		final GermplasmListDataView sourceGermplasmListDataView = new GermplasmListDataView.GermplasmListDataVariableViewBuilder(
			sourceGermplasmList,
			TermId.ENTRY_NO.getId(),
			VariableType.ENTRY_DETAIL.getId()
		).build();
		this.daoFactory.getGermplasmListDataViewDAO().save(sourceGermplasmListDataView);

		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(this.createGermplasmListData(sourceGermplasmList, germplasm1.getGid(), 1));
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(this.createGermplasmListData(sourceGermplasmList, germplasm2.getGid(), 2));

		this.sessionProvder.getSession().flush();

		assertTrue(CollectionUtils.isEmpty(this.daoFactory.getGermplasmListDataDAO().getByListId(germplasmList.getId())));
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchCompositeDto = new SearchCompositeDto<>();
		final GermplasmListDataSearchRequest germplasmListDataSearchRequest = new GermplasmListDataSearchRequest();
		germplasmListDataSearchRequest.setEntryNumbers(Lists.newArrayList(1, 2));
		searchCompositeDto.setSearchRequest(germplasmListDataSearchRequest);
		this.germplasmListService.addGermplasmListEntriesToAnotherList(germplasmList.getId(), sourceGermplasmList.getId(), null,
			searchCompositeDto);
		Assert.assertEquals(2, this.daoFactory.getGermplasmListDataDAO().getByListId(germplasmList.getId()).size());
	}

	@Test
	public void testAddGermplasmListEntriesToAnotherList_WithDuplicateGermplasmEntries() {
		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + new Random().nextInt(100),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		// Source Germplasm list
		final GermplasmList sourceGermplasmList = new GermplasmList(null, "Test Germplasm List " + new Random().nextInt(100),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(sourceGermplasmList);

		// Add ENTRY_NO Entry Detail Variable.
		final GermplasmListDataView sourceGermplasmListDataView = new GermplasmListDataView.GermplasmListDataVariableViewBuilder(
			sourceGermplasmList,
			TermId.ENTRY_NO.getId(),
			VariableType.ENTRY_DETAIL.getId()
		).build();

		this.daoFactory.getGermplasmListDataViewDAO().save(sourceGermplasmListDataView);

		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm1 = this.createGermplasm(singleCrossMethod);
		final Germplasm germplasm2 = this.createGermplasm(singleCrossMethod);

		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(this.createGermplasmListData(sourceGermplasmList, germplasm1.getGid(), 1));
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(this.createGermplasmListData(sourceGermplasmList, germplasm1.getGid(), 2));
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(this.createGermplasmListData(sourceGermplasmList, germplasm2.getGid(), 3));

		this.sessionProvder.getSession().flush();

		assertTrue(CollectionUtils.isEmpty(this.daoFactory.getGermplasmListDataDAO().getByListId(germplasmList.getId())));
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchCompositeDto = new SearchCompositeDto<>();
		final GermplasmListDataSearchRequest germplasmListDataSearchRequest = new GermplasmListDataSearchRequest();
		germplasmListDataSearchRequest.setEntryNumbers(Lists.newArrayList(1, 2, 3));
		searchCompositeDto.setSearchRequest(germplasmListDataSearchRequest);
		this.germplasmListService.addGermplasmListEntriesToAnotherList(germplasmList.getId(), sourceGermplasmList.getId(), null,
			searchCompositeDto);
		Assert.assertEquals(3, this.daoFactory.getGermplasmListDataDAO().getByListId(germplasmList.getId()).size());
	}

	@Test
	public void testCloneGermplasmListEntries_OK() {
		// Create source germplasm list
		final int randNameSuffix = new Random().nextInt(100);
		final GermplasmList sourceGermplasmList = new GermplasmList(null, "Test Germplasm List " + randNameSuffix,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(sourceGermplasmList);

		final StandardVariable variable = this.setupSourceListEntries(sourceGermplasmList);

		// Germplasm List Generator (from request)
		final GermplasmListDto request = new GermplasmListDto();
		request.setListName("Test Cloned Germplasm List " + randNameSuffix);
		request.setCreationDate(new Date());
		request.setListType("LST");
		request.setDescription("Test Cloned Germplasm List");

		// Clone source list to new list
		final GermplasmListDto clonedList =
			this.germplasmListService.cloneGermplasmList(sourceGermplasmList.getId(), request, USER_ID);
		this.assertGermplasmListCloned(sourceGermplasmList, variable, clonedList);
	}

	private StandardVariable setupSourceListEntries(final GermplasmList sourceGermplasmList) {
		// add entries to source list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm1 = this.createGermplasm(singleCrossMethod);
		final Germplasm germplasm2 = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData1 = this.createGermplasmListData(sourceGermplasmList, germplasm1.getGid(), 1);
		final GermplasmListData germplasmListData2 = this.createGermplasmListData(sourceGermplasmList, germplasm2.getGid(), 2);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData2);

		// Add View and Entry Details to source
		final String variableName = RandomStringUtils.randomAlphabetic(20);
		final StandardVariable variable = this.createEntryDetailVariable(variableName);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(sourceGermplasmList.getId(), germplasmListVariableRequestDto);

		final Integer listDataObservation1 = this.germplasmListService.saveListDataObservation(sourceGermplasmList.getId(),
			new GermplasmListObservationRequestDto(germplasmListData1.getListDataId(), variable.getId(), "1", null));
		final Integer listDataObservation2 = this.germplasmListService.saveListDataObservation(sourceGermplasmList.getId(),
			new GermplasmListObservationRequestDto(germplasmListData2.getListDataId(), variable.getId(), "2", null));
		return variable;
	}

	private void assertGermplasmListCloned(final GermplasmList sourceGermplasmList, final StandardVariable variable,
		final GermplasmListDto clonedList) {
		final Integer clonedListId = clonedList.getListId();
		final List<GermplasmListData> clonedListEntries = this.daoFactory.getGermplasmListDataDAO().getByListId(clonedListId);

		// verify if entries from source are cloned in the target and are in the proper order
		Assert.assertEquals(2, clonedListEntries.size());

		final Map<Integer, GermplasmListData> sourceListMap =
			this.daoFactory.getGermplasmListDataDAO().getMapByEntryId(sourceGermplasmList.getId());
		clonedListEntries.forEach(entry -> {
			assertEquals(sourceListMap.get(entry.getEntryId()).getGid(), entry.getGid());

			// verify if corresponding germplasm list data details were copied
			final Optional<GermplasmListDataDetail> observationOptional = this.daoFactory.getGermplasmListDataDetailDAO()
				.getByListDataIdAndVariableId(entry.getId(), variable.getId());
			Assert.assertTrue(observationOptional.isPresent());
			// as set above, variable values are equal to its entry no for simplicity
			Assert.assertEquals(entry.getEntryId().toString(), observationOptional.get().getValue());
		});

		// verify if germplasm list data view were copied
		final List<GermplasmListDataView> view = this.daoFactory.getGermplasmListDataViewDAO().getByListId(clonedListId);
		Assert.assertEquals(1, view.size());
		Assert.assertNotNull(view.get(0).getCvtermId());
		Assert.assertEquals(variable.getId(), view.get(0).getCvtermId().intValue());
	}

	public void testShouldRemoveEntriesFromList() {
		final int randomInt = new Random().nextInt(100);
		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData1 = this.createGermplasmListData(germplasmList, germplasm.getGid(), 1);
		final GermplasmListData germplasmListData2 = this.createGermplasmListData(germplasmList, germplasm.getGid(), 2);
		final GermplasmListData germplasmListData3 = this.createGermplasmListData(germplasmList, germplasm.getGid(), 3);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData2);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData3);

		// Entry Detail
		final String variableName = RandomStringUtils.randomAlphabetic(20);
		final StandardVariable variable = this.createEntryDetailVariable(variableName);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);

		this.germplasmListService.saveListDataObservation(germplasmList.getId(),
			new GermplasmListObservationRequestDto(germplasmListData1.getListDataId(), variable.getId(), "1", null));
		this.germplasmListService.saveListDataObservation(germplasmList.getId(),
			new GermplasmListObservationRequestDto(germplasmListData2.getListDataId(), variable.getId(), "2", null));
		this.germplasmListService.saveListDataObservation(germplasmList.getId(),
			new GermplasmListObservationRequestDto(germplasmListData3.getListDataId(), variable.getId(), "3", null));

		// Remove by searchComposite.itemIds
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite1 = new SearchCompositeDto<>();
		searchComposite1.setItemIds(Sets.newHashSet(germplasmListData2.getListDataId()));
		this.germplasmListService.removeGermplasmEntriesFromList(germplasmList.getId(), searchComposite1);

		final List<GermplasmListDataSearchResponse> germplasmListDataResponseList =
			this.germplasmListDataService.searchGermplasmListData(germplasmList.getId(), new GermplasmListDataSearchRequest(), null);
		final List<GermplasmListDataDetail> germplasmListDataDetailList =
			this.germplasmListDataService.getGermplasmListDataDetailList(germplasmList.getId());
		Assert.assertEquals(2, germplasmListDataResponseList.size());
		Assert.assertEquals(2, germplasmListDataDetailList.size());
		Assert.assertEquals(1, germplasmListDataResponseList.get(0).getData().get(TermId.ENTRY_NO.name()));
		Assert.assertEquals(2, germplasmListDataResponseList.get(1).getData().get(TermId.ENTRY_NO.name()));

		// Remove by searchComposite.searchRequest.entryNumbers
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite2 = new SearchCompositeDto<>();
		final GermplasmListDataSearchRequest germplasmListDataSearchRequest = new GermplasmListDataSearchRequest();
		germplasmListDataSearchRequest.setEntryNumbers(Arrays.asList(2));
		searchComposite2.setSearchRequest(germplasmListDataSearchRequest);
		this.germplasmListService.removeGermplasmEntriesFromList(germplasmList.getId(), searchComposite2);

		final List<GermplasmListDataSearchResponse> germplasmListDataResponseList2 =
			this.germplasmListDataService.searchGermplasmListData(germplasmList.getId(), new GermplasmListDataSearchRequest(), null);
		final List<GermplasmListDataDetail> germplasmListDataDetailList2 =
			this.germplasmListDataService.getGermplasmListDataDetailList(germplasmList.getId());

		Assert.assertEquals(1, germplasmListDataResponseList2.size());
		Assert.assertEquals(1, germplasmListDataDetailList2.size());
		Assert.assertEquals(1, germplasmListDataResponseList2.get(0).getData().get(TermId.ENTRY_NO.name()));

	}

	@Test
	public void testEditListMetadata() {
		final int randomInt = new Random().nextInt(100);
		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final GermplasmListDto germplasmListDto = new GermplasmListDto();
		germplasmListDto.setListId(germplasmList.getId());
		germplasmListDto.setListName(RandomStringUtils.randomAlphabetic(20));
		germplasmListDto.setDescription(RandomStringUtils.randomAlphabetic(20));
		germplasmListDto.setNotes(RandomStringUtils.randomAlphabetic(55));
		germplasmListDto.setCreationDate(new Date());
		germplasmListDto.setListType(GermplasmListType.F1.name());
		this.germplasmListService.editListMetadata(germplasmListDto);

		final Optional<GermplasmList> updatedList = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		Assert.assertTrue(updatedList.isPresent());
		final GermplasmList list = updatedList.get();
		Assert.assertEquals(germplasmListDto.getListName(), list.getName());
		Assert.assertEquals(germplasmListDto.getDescription(), list.getDescription());
		Assert.assertEquals(germplasmListDto.getNotes(), list.getNotes());
		Assert.assertEquals(germplasmListDto.getListType(), list.getType());
		Assert.assertEquals(Long.valueOf(Util.convertDateToIntegerValue(germplasmListDto.getCreationDate())), list.getDate());
	}

	@Test
	public void testPerformGermplasmListEntriesDeletion() {

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + new Random().nextInt(100),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add 2 germplasm entries to list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasmEntry1 = this.createGermplasm(singleCrossMethod);
		final Germplasm germplasmEntry2 = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData1 = this.createGermplasmListData(germplasmList, germplasmEntry1.getGid(), 1);
		final GermplasmListData germplasmListData2 = this.createGermplasmListData(germplasmList, germplasmEntry2.getGid(), 2);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData2);

		// Add ListDataDetail
		final CVTerm entrySourceVariable = this.daoFactory.getCvTermDao().getByName("ENTRY_SOURCE");
		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(entrySourceVariable.getCvTermId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());
		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);
		final GermplasmListObservationRequestDto germplasmListObservationRequestDto = new GermplasmListObservationRequestDto();
		germplasmListObservationRequestDto.setVariableId(entrySourceVariable.getCvTermId());
		germplasmListObservationRequestDto.setValue(RandomStringUtils.randomAlphabetic(10));
		germplasmListObservationRequestDto.setListDataId(germplasmListData1.getListDataId());
		this.germplasmListService.saveListDataObservation(germplasmList.getId(), germplasmListObservationRequestDto);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		this.germplasmListService.performGermplasmListEntriesDeletion(Arrays.asList(germplasmEntry1.getGid()));

		Assert.assertNull(
			this.daoFactory.getGermplasmListDataDAO().getByListIdAndLrecId(germplasmList.getId(), germplasmListData1.getListDataId()));
		Assert.assertFalse(this.daoFactory.getGermplasmListDataDetailDAO()
			.getByListDataIdAndVariableId(germplasmListData1.getListDataId(), entrySourceVariable.getCvTermId()).isPresent());

		final GermplasmListData updateGermplasmListData2 =
			this.daoFactory.getGermplasmListDataDAO().getByListIdAndLrecId(germplasmList.getId(), germplasmListData2.getListDataId());
		Assert.assertNotNull(updateGermplasmListData2);
		// The second germplasm listdata's entryId should be updated to 1
		Assert.assertEquals(1, updateGermplasmListData2.getEntryId().intValue());

	}

	private void assertGermplasmListDataObservationOptionalPresent(final Integer listDataObservationId, final String value,
		final Integer variableId, final Integer germplasmListDataId) {
		final Optional<GermplasmListObservationDto> germplasmListObservationDtoOptional =
			this.germplasmListService.getListDataObservation(listDataObservationId);
		assertTrue(germplasmListObservationDtoOptional.isPresent());
		final GermplasmListObservationDto germplasmListObservationDto = germplasmListObservationDtoOptional.get();
		assertEquals(germplasmListObservationDto.getObservationId(), listDataObservationId);
		assertEquals(germplasmListObservationDto.getValue(), value);
		assertEquals(germplasmListObservationDto.getVariableId(), variableId);
		assertEquals(germplasmListObservationDto.getListDataId(), germplasmListDataId);
	}

	private Germplasm createGermplasm(final Method method) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), 0, 0, 0,
			0, 0, 0, 0,
			0, 0, null, null, method);

		final Germplasm savedGermplasm = this.daoFactory.getGermplasmDao().save(germplasm);

		final Name name = new Name(null, savedGermplasm, 1, 1, "Name", 0, 0, 0);
		this.daoFactory.getNameDao().save(name);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		return savedGermplasm;
	}

	private GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final int gid, final int entryNo) {
		return new GermplasmListData(null, germplasmList, gid, entryNo,
			DataSetupTest.GERMPLSM_PREFIX + entryNo + " Source", DataSetupTest.GERMPLSM_PREFIX + "Group A",
			0, 0);
	}

	private void assertGermplasmList(final GermplasmList germplasmList, final Integer id, final String name) {
		assertNotNull(germplasmList);
		assertThat(germplasmList.getId(), is(id));
		assertNotNull(germplasmList.getDate());
		assertThat(germplasmList.getUserId(), is(USER_ID));
		assertThat(germplasmList.getDescription(), is(name));
		assertThat(germplasmList.getName(), is(name));
		assertNull(germplasmList.getNotes());
		assertNotNull(germplasmList.getParent());
		assertThat(germplasmList.getParent().getId(), is(this.parentFolderId));
		assertThat(germplasmList.getType(), is(GermplasmList.FOLDER_TYPE));
		assertThat(germplasmList.getProgramUUID(), is(PROGRAM_UUID));
		assertThat(germplasmList.getStatus(), is(GermplasmList.Status.FOLDER.getCode()));
	}

	private StandardVariable createEntryDetailVariable(final String name) {

		final CVTerm property = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		final CVTerm method = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setProperty(new Term(property.getCvTermId(), property.getName(), property.getDefinition()));
		standardVariable.setScale(new Term(scale.getCvTermId(), scale.getName(), scale.getDefinition()));
		standardVariable.setMethod(new Term(method.getCvTermId(), method.getName(), method.getDefinition()));
		standardVariable.setDataType(new Term(numericDataType.getCvTermId(), numericDataType.getName(), numericDataType.getDefinition()));
		standardVariable.setVariableTypes(Sets.newHashSet(VariableType.ENTRY_DETAIL));
		this.ontologyDataManager.addStandardVariable(standardVariable, PROGRAM_UUID);

		return standardVariable;
	}

}
