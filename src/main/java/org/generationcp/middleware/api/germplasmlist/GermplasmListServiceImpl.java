package org.generationcp.middleware.api.germplasmlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchResponse;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchRequest;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchResponse;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchResponse;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Transactional
@Service
public class GermplasmListServiceImpl implements GermplasmListService {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
	public static final String LIST_NOT_FOUND = "list.not.found";

	private final DaoFactory daoFactory;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmSearchService germplasmSearchService;

	@Autowired
	private GermplasmService germplasmService;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private GermplasmListDataService germplasmListDataService;

	public GermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmListGeneratorDTO create(final GermplasmListGeneratorDTO request, final Integer loggedInUserId) {

		final List<Integer> gids = request.getEntries()
			.stream().map(GermplasmListGeneratorDTO.GermplasmEntryDTO::getGid).collect(Collectors.toList());
		final Map<Integer, String> preferredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);
		final Map<Integer, List<Name>> namesByGid = this.daoFactory.getNameDao().getNamesByGids(gids)
			.stream().collect(groupingBy(n -> n.getGermplasm().getGid()));

		// save list
		final GermplasmList germplasmList = this.createGermplasmList(request, loggedInUserId);

		// save variables
		final Set<Integer> variableIds = request.getEntries().stream().flatMap(e -> e.getData().keySet().stream()).collect(toSet());
		for (final Integer variableId : variableIds) {
			final GermplasmListDataView germplasmListDataView = new GermplasmListDataView.GermplasmListDataVariableViewBuilder(
				germplasmList,
				variableId,
				VariableType.ENTRY_DETAIL.getId()
			).build();
			this.daoFactory.getGermplasmListDataViewDAO().save(germplasmListDataView);
		}

		// save germplasm list data
		for (final GermplasmListGeneratorDTO.GermplasmEntryDTO entry : request.getEntries()) {
			final Integer gid = entry.getGid();
			final String preferredName = preferredNamesMap.get(gid);
			final List<Name> names = namesByGid.get(gid);
			Preconditions.checkArgument(preferredName != null || names != null, "No name found for gid=" + gid);
			final String designation = preferredName != null ? preferredName : names.get(0).getNval();
			GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entry.getEntryNo(),
				entry.getEntryCode(), entry.getSeedSource(), designation, entry.getGroupName(),
				GermplasmListDataDAO.STATUS_ACTIVE, null);
			germplasmListData = this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);

			// save entry details
			for (final Map.Entry<Integer, GermplasmListObservationDto> entryDetailSet : entry.getData().entrySet()) {
				final GermplasmListObservationDto entryDetail = entryDetailSet.getValue();

				// save data
				final GermplasmListDataDetail germplasmListDataDetail = new GermplasmListDataDetail(
					germplasmListData,
					entryDetailSet.getKey(),
					entryDetail.getValue(),
					entryDetail.getcValueId()
				);
				this.daoFactory.getGermplasmListDataDetailDAO().save(germplasmListDataDetail);
			}
		}

		return request;
	}

	private GermplasmList createGermplasmList(final GermplasmListBasicInfoDTO request, final Integer currentUserId) {
		final GermplasmList parent = request.getParentFolderId() != null ?
			this.daoFactory.getGermplasmListDAO().getById(Integer.valueOf(request.getParentFolderId()), false) : null;
		final String description = request.getDescription() != null ? request.getDescription() : StringUtils.EMPTY;

		GermplasmList germplasmList =
			new GermplasmList(null, request.getListName(), Long.valueOf(this.dateFormat.format(request.getCreationDate())),
				request.getListType(), currentUserId, description, parent, request.getStatus(), request.getNotes(), null);
		germplasmList.setProgramUUID(request.getProgramUUID());
		germplasmList = this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		request.setListId(germplasmList.getId());
		return germplasmList;
	}

	@Override
	public void importUpdates(final GermplasmListGeneratorDTO request) {
		final Integer listId = request.getListId();
		// TODO validate deleted
		final GermplasmList germplasmList = this.getGermplasmListById(listId)
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));

		// if variables not exist in list, add them

		final Set<Integer> existingVariableIds = this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId)
			.stream().map(GermplasmListDataView::getCvtermId).collect(toSet());

		final Set<Integer> variableIds = request.getEntries().stream().flatMap(e -> e.getData().keySet().stream())
			.filter(variableId -> !existingVariableIds.contains(variableId))
			.collect(toSet());

		for (final Integer variableId : variableIds) {
			final GermplasmListDataView germplasmListDataView = new GermplasmListDataView.GermplasmListDataVariableViewBuilder(
				germplasmList,
				variableId,
				VariableType.ENTRY_DETAIL.getId()
			).build();
			this.daoFactory.getGermplasmListDataViewDAO().save(germplasmListDataView);
		}

		// if entry details not exist, create them, otherwise update them

		final Table<Integer, Integer, GermplasmListDataDetail> table = this.daoFactory.getGermplasmListDataDetailDAO()
			.getTableEntryIdToVariableId(listId);

		final Map<Integer, GermplasmListData> germplasmListDataByEntryId = this.daoFactory.getGermplasmListDataDAO()
			.getMapByEntryId(listId);

		for (final GermplasmListGeneratorDTO.GermplasmEntryDTO entry : request.getEntries()) {
			final GermplasmListData germplasmListData = germplasmListDataByEntryId.get(entry.getEntryNo());

			// Temporary workaround to allow users to edit ENTRY_CODE
			final String entryCode = entry.getEntryCode();
			if (!isBlank(entryCode)) {
				germplasmListData.setEntryCode(entryCode);
				this.daoFactory.getGermplasmListDataDAO().update(germplasmListData);
			}

			for (final Map.Entry<Integer, GermplasmListObservationDto> entryDetailSet : entry.getData().entrySet()) {
				final GermplasmListObservationDto entryDetail = entryDetailSet.getValue();

				GermplasmListDataDetail germplasmListDataDetail = table.get(entry.getEntryNo(), entryDetail.getVariableId());
				if (germplasmListDataDetail != null) {
					germplasmListDataDetail.setValue(entryDetail.getValue());
				} else {
					germplasmListDataDetail = new GermplasmListDataDetail(
						germplasmListData,
						entryDetailSet.getKey(),
						entryDetail.getValue(),
						entryDetail.getcValueId()
					);
				}

				this.daoFactory.getGermplasmListDataDetailDAO().saveOrUpdate(germplasmListDataDetail);
			}
		}
	}

	/**
	 * Inserts a list of multiple {@code GermplasmListData} objects into the database.
	 *
	 * @param data - A list of {@code GermplasmListData} objects to be persisted to the database. {@code GermplasmListData}
	 *             objects must be valid.
	 * @return Returns the ids of the {@code GermplasmListData} records inserted in the database.
	 */
	private List<GermplasmListData> addGermplasmListData(final List<GermplasmListData> data) {

		final List<GermplasmListData> idGermplasmListDataSaved = new ArrayList<>();
		try {
			final List<Integer> deletedListEntryIds = new ArrayList<>();
			data.forEach(germplasmListData -> {
				germplasmListData.truncateGroupNameIfNeeded();
				final GermplasmListData recordSaved = this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
				idGermplasmListDataSaved.add(recordSaved);
				if (!Objects.isNull(germplasmListData.getStatus()) && germplasmListData.getStatus() == 9) {
					deletedListEntryIds.add(germplasmListData.getId());
				}
			});

			if (!deletedListEntryIds.isEmpty()) {
				this.daoFactory.getTransactionDAO().cancelUnconfirmedTransactionsForListEntries(deletedListEntryIds);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm List Data: GermplasmListServiceImpl.addGermplasmListData(data="
					+ data + "): " + e.getMessage(),
				e);
		}

		return idGermplasmListDataSaved;
	}

	@Override
	public void addGermplasmEntriesToList(final Integer germplasmListId,
		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite, final String programUUID) {

		final GermplasmList germplasmList = this.getGermplasmListById(germplasmListId)
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));

		//Get the germplasm entries to add
		final List<AddGermplasmEntryModel> addGermplasmEntriesModels = new ArrayList<>();
		if (CollectionUtils.isEmpty(searchComposite.getItemIds())) {
			final GermplasmSearchRequest searchRequest = searchComposite.getSearchRequest();
			if (!searchRequest.getAddedColumnsPropertyIds().contains(ColumnLabels.PREFERRED_NAME.getName())) {
				searchRequest.getAddedColumnsPropertyIds().add(ColumnLabels.PREFERRED_NAME.getName());
			}

			this.germplasmSearchService.searchGermplasm(searchRequest, null, programUUID)
				.forEach(germplasmSearchResponse -> addGermplasmEntriesModels.add(new AddGermplasmEntryModel(
					germplasmSearchResponse.getGid(),
					germplasmSearchResponse.getGermplasmPreferredName(),
					germplasmSearchResponse.getGroupId()
				)));

		} else {

			this.germplasmService.getGermplasmByGIDs(new ArrayList<>(searchComposite.getItemIds()))
				.forEach(germplasm -> addGermplasmEntriesModels.add(new AddGermplasmEntryModel(
						germplasm.getGid(),
						germplasm.getPreferredName().getNval(),
						germplasm.getMgid())
					)
				);
		}

		this.addGermplasmEntriesModelsToList(germplasmList, addGermplasmEntriesModels);
	}

	private void addGermplasmEntriesModelsToList(final GermplasmList germplasmList,
		final List<AddGermplasmEntryModel> addGermplasmEntriesModels) {

		//Get the entryId max value
		final int maxEntryNo = germplasmList.getListData()
			.stream()
			.mapToInt(GermplasmListData::getEntryId)
			.max()
			.orElse(0);
		final AtomicInteger lastEntryNo = new AtomicInteger(maxEntryNo);

		final Set<Integer> gids = addGermplasmEntriesModels
			.stream()
			.map(AddGermplasmEntryModel::getGid)
			.collect(toSet());

		final Integer level = germplasmList.getGenerationLevel();
		final Map<Integer, String> crossExpansionsBulk =
			this.pedigreeService.getCrossExpansionsBulk(gids, level, this.crossExpansionProperties);

		final Map<Integer, String> plotCodeValuesIndexedByGids = this.germplasmService.getPlotCodeValues(gids);

		//Create germplasm lists data
		final List<GermplasmListData> germplasmListsData = addGermplasmEntriesModels.stream().map(model -> {
			final int entryNo = lastEntryNo.addAndGet(1);

			return new GermplasmListData(null,
				germplasmList,
				model.getGid(),
				entryNo,
				String.valueOf(entryNo),
				plotCodeValuesIndexedByGids.get(model.getGid()),
				model.getPreferredName(),
				crossExpansionsBulk.get(model.getGid()),
				0,
				0,
				model.getGroupId());
		}).collect(Collectors.toList());
		this.addGermplasmListData(germplasmListsData);
	}

	@Override
	public void addGermplasmListEntriesToAnotherList(final Integer destinationListId, final Integer sourceListId, final String programUUID,
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite) {
		final GermplasmList destinationGermplasmList = this.getGermplasmListById(destinationListId)
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));

		this.getGermplasmListById(sourceListId)
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));

		//Get the germplasm entries to add
		PageRequest pageRequest = null;
		if (searchComposite.getSearchRequest() != null
			&& !CollectionUtils.isEmpty(searchComposite.getSearchRequest().getEntryNumbers())) {
			pageRequest = new PageRequest(0, searchComposite.getSearchRequest().getEntryNumbers().size(),
				new Sort(Sort.Direction.ASC, GermplasmListStaticColumns.ENTRY_NO.name()));
		}

		final List<GermplasmListDataSearchResponse> responseList =
			this.germplasmListDataService.searchGermplasmListData(sourceListId, searchComposite.getSearchRequest(), pageRequest);
		final GermplasmSearchRequest germplasmSearchRequest = new GermplasmSearchRequest();
		germplasmSearchRequest.setGids(
			responseList
				.stream().map(response -> Integer.valueOf(response.getData().get(GermplasmListStaticColumns.GID.name()).toString()))
				.collect(Collectors.toList()));
		final Map<Integer, GermplasmSearchResponse> germplasmResponseMap =
			this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, null, programUUID)
				.stream().collect(Collectors.toMap(GermplasmSearchResponse::getGid, Function.identity()));
		final List<AddGermplasmEntryModel> addGermplasmEntriesModels = responseList
			.stream().map(response -> this.constructGermplasmEntryModel(germplasmResponseMap,
				Integer.valueOf(response.getData().get(GermplasmListStaticColumns.GID.name()).toString())))
			.collect(Collectors.toList());

		this.addGermplasmEntriesModelsToList(destinationGermplasmList, addGermplasmEntriesModels);
	}

	private AddGermplasmEntryModel constructGermplasmEntryModel(final Map<Integer, GermplasmSearchResponse> germplasmResponseMap, final Integer gid) {
		return new AddGermplasmEntryModel(
			gid, germplasmResponseMap.get(gid).getGermplasmPreferredName(), germplasmResponseMap.get(gid).getGroupId());
	}

	@Override
	public GermplasmListDto cloneGermplasmList(final Integer listId, final GermplasmListDto listDto, final Integer loggedInUserId) {

		// copy info from request
		final GermplasmList destinationList = this.createGermplasmList(listDto, loggedInUserId);
		final Integer destinationListId = destinationList.getId();

		// copy other fields not coming in request
		final GermplasmList originList = this.daoFactory.getGermplasmListDAO().getById(listId);
		destinationList.setGenerationLevel(originList.getGenerationLevel());
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(destinationList);

		// copy data
		this.daoFactory.getGermplasmListDataDAO().copyEntries(listId, destinationListId);
		this.daoFactory.getGermplasmListDataViewDAO().copyEntries(listId, destinationListId);
		this.daoFactory.getGermplasmListDataDetailDAO().copyEntries(listId, destinationListId, loggedInUserId);

		return listDto;
	}

	@Override
	public Optional<GermplasmList> getGermplasmListById(final Integer id) {
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getById(id));
	}

	@Override
	public Optional<GermplasmList> getGermplasmListByIdAndProgramUUID(final Integer id, final String programUUID) {
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getByIdAndProgramUUID(id, programUUID));
	}

	@Override
	public Optional<GermplasmList> getGermplasmListByParentAndName(final String germplasmListName, final Integer parentId,
		final String programUUID) {
		return Optional
			.ofNullable(this.daoFactory.getGermplasmListDAO().getGermplasmListByParentAndName(germplasmListName, parentId, programUUID));
	}

	@Override
	public long countMyLists(final String programUUID, final Integer userId) {
		return this.daoFactory.getGermplasmListDAO().countMyLists(programUUID, userId);
	}

	@Override
	public List<MyListsDTO> getMyLists(final String programUUID, final Pageable pageable, final Integer userId) {
		return this.daoFactory.getGermplasmListDAO().getMyLists(programUUID, pageable, userId);
	}

	@Override
	public Integer createGermplasmListFolder(final Integer userId, final String folderName, final Integer parentId,
		final String programUUID) {

		final GermplasmList parentFolder = (Objects.isNull(parentId)) ? null :
			this.getGermplasmListById(parentId).orElseThrow(() -> new MiddlewareException("Parent Folder does not exist"));

		final GermplasmList folder = new GermplasmList();
		folder.setDate(Util.getCurrentDateAsLongValue());
		folder.setUserId(userId);
		folder.setDescription(folderName);
		folder.setName(folderName);
		folder.setNotes(null);
		folder.setParent(parentFolder);
		folder.setType(GermplasmList.FOLDER_TYPE);
		folder.setProgramUUID(programUUID);
		folder.setStatus(GermplasmList.Status.FOLDER.getCode());
		return this.daoFactory.getGermplasmListDAO().save(folder).getId();
	}

	@Override
	public Integer updateGermplasmListFolder(final String folderName, final Integer folderId) {

		final GermplasmList folder =
			this.getGermplasmListById(folderId).orElseThrow(() -> new MiddlewareException("Folder does not exist"));

		folder.setName(folderName);
		folder.setDescription(folderName);

		return this.daoFactory.getGermplasmListDAO().save(folder).getId();
	}

	@Override
	public Integer moveGermplasmListFolder(final Integer germplasmListId, final Integer newParentFolderId,
		final String programUUID) {

		final GermplasmList listToMove = this.getGermplasmListById(germplasmListId)
			.orElseThrow(() -> new MiddlewareRequestException("", "list.folder.not.found"));

		final GermplasmList newParentFolder = (Objects.isNull(newParentFolderId)) ? null :
			this.getGermplasmListById(newParentFolderId)
				.orElseThrow(() -> new MiddlewareRequestException("", "list.parent.folder.not.found"));

		//Locking list when moving a from program to any crop folder
		if (!StringUtils.isEmpty(listToMove.getProgramUUID()) && programUUID == null && !listToMove.getType().equals("FOLDER")) {
			listToMove.setStatus(GermplasmList.Status.LOCKED_LIST.getCode());
		}

		listToMove.setProgramUUID(programUUID);
		listToMove.setParent(newParentFolder);

		try {
			return this.daoFactory.getGermplasmListDAO().saveOrUpdate(listToMove).getId();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in moveGermplasmList in GermplasmListServiceImpl: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteGermplasmListFolder(final Integer folderId) {
		final GermplasmList folder = this.getGermplasmListById(folderId)
			.orElseThrow(() -> new MiddlewareRequestException("", "list.folder.not.found"));

		this.daoFactory.getGermplasmListDAO().makeTransient(folder);
	}

	@Override
	public List<GermplasmListDto> getGermplasmLists(final Integer gid) {
		return this.daoFactory.getGermplasmListDAO().getGermplasmListDtos(gid);
	}

	@Override
	public void performGermplasmListEntriesDeletion(final List<Integer> gids) {
		final List<Integer> germplasmListIds = this.daoFactory.getGermplasmListDAO().getListIdsByGIDs(gids);
		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(germplasmListIds)) {
			final Map<Integer, List<GermplasmListData>> germplasmListDataMap = this.daoFactory.getGermplasmListDataDAO()
				.getGermplasmDataListMapByListIds(germplasmListIds);
			final List<GermplasmListData> germplasmListDataToBeDeleted = new ArrayList<>();
			final List<GermplasmListData> germplasmListDataToBeUpdated = new ArrayList<>();
			for (final Integer listId : germplasmListIds) {
				final Iterator<GermplasmListData> iterator = germplasmListDataMap.get(listId).iterator();
				while (iterator.hasNext()) {
					final GermplasmListData germplasmListData = iterator.next();
					if (germplasmListData.getGermplasm() != null && gids.contains(germplasmListData.getGermplasm().getGid())) {
						iterator.remove();
						germplasmListDataToBeDeleted.add(germplasmListData);
					}
				}

				// Change entry IDs on listData
				final List<GermplasmListData> listData = germplasmListDataMap.get(listId);
				Integer entryId = 1;
				for (final GermplasmListData germplasmListData : listData) {
					germplasmListData.setEntryId(entryId);
					entryId++;
				}
				germplasmListDataToBeUpdated.addAll(listData);
			}

			this.deleteGermplasmListData(germplasmListDataToBeDeleted);
			this.updateGermplasmListData(germplasmListDataToBeUpdated);
		}
	}

	@Override
	public void deleteProgramGermplasmLists(final String programUUID) {
		this.daoFactory.getGermplasmListDAO().markProgramGermplasmListsAsDeleted(programUUID);
	}

	@Override
	public long countGermplasmLists(final List<Integer> gids) {
		return this.daoFactory.getGermplasmListDAO().countByGIDs(gids);
	}

	@Override
	public List<GermplasmListSearchResponse> searchGermplasmList(final GermplasmListSearchRequest request,
		final Pageable pageable, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().searchGermplasmList(request, pageable, programUUID);
	}

	@Override
	public long countSearchGermplasmList(final GermplasmListSearchRequest request, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().countSearchGermplasmList(request, programUUID);
	}

	@Override
	public boolean toggleGermplasmListStatus(final Integer listId) {
		final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
		if (germplasmList.isLockedList()) {
			germplasmList.unlock();
		} else {
			germplasmList.lockList();
		}
		this.daoFactory.getGermplasmListDAO().save(germplasmList);
		return germplasmList.isLockedList();
	}

	@Override
	public List<Integer> getListOntologyVariables(final Integer listId, final List<Integer> types) {
		final List<GermplasmListDataView> columns =
			this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		return columns.stream()
			.filter(c -> c.getCvtermId() != null && (types == null || types.contains(c.getTypeId())))
			.map(GermplasmListDataView::getCvtermId)
			.collect(Collectors.toList());
	}

	@Override
	public void addVariableToList(final Integer listId, final GermplasmListVariableRequestDto germplasmListVariableRequestDto) {
		final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
		final GermplasmListDataView germplasmListDataView =
			new GermplasmListDataView.GermplasmListDataVariableViewBuilder(germplasmList, germplasmListVariableRequestDto.getVariableId(),
				germplasmListVariableRequestDto.getVariableTypeId()).build();
		this.daoFactory.getGermplasmListDataViewDAO().save(germplasmListDataView);
	}

	@Override
	public void removeListVariables(final Integer listId, final Set<Integer> variableIds) {
		this.daoFactory.getGermplasmListDataDetailDAO().deleteByListIdAndVariableIds(listId, variableIds);
		this.daoFactory.getGermplasmListDataViewDAO().deleteByListIdAndVariableIds(listId, variableIds);
	}

	@Override
	public List<Variable> getGermplasmListVariables(final String programUUID, final Integer listId,
		final Integer variableTypeId) {
		final List<GermplasmListDataView> columns =
			this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		final List<Integer> variableIds = columns.stream().filter(
				c -> c.getCvtermId() != null && (c.getTypeId().equals(variableTypeId)
					|| variableTypeId == null)).map(GermplasmListDataView::getCvtermId)
			.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(variableIds)) {
			final VariableFilter variableFilter = new VariableFilter();
			if (StringUtils.isNotEmpty(programUUID)) {
				variableFilter.setProgramUuid(programUUID);
			}
			variableIds
				.forEach(variableFilter::addVariableId);
			return this.ontologyVariableDataManager.getWithFilter(variableFilter);
		}
		return Collections.emptyList();
	}

	@Override
	public Optional<GermplasmListDataDto> getGermplasmListData(final Integer listDataId) {
		final GermplasmListData germplasmListData = this.daoFactory.getGermplasmListDataDAO().getById(listDataId);
		if (germplasmListData != null) {
			final GermplasmListDataDto germplasmListDataDto =
				new GermplasmListDataDto(germplasmListData.getListDataId(), germplasmListData.getList().getId(),
					germplasmListData.getEntryId(), germplasmListData.getGid());
			return Optional.of(germplasmListDataDto);
		}
		return Optional.empty();
	}

	@Override
	public Optional<GermplasmListObservationDto> getListDataObservation(final Integer observationId) {
		final GermplasmListDataDetail germplasmListDataDetail = this.daoFactory.getGermplasmListDataDetailDAO().getById(observationId);
		if (germplasmListDataDetail != null) {
			final GermplasmListObservationDto germplasmListObservationDto =
				new GermplasmListObservationDto(observationId, germplasmListDataDetail.getListData().getListDataId(),
					germplasmListDataDetail.getVariableId(), germplasmListDataDetail.getValue(),
					germplasmListDataDetail.getCategoricalValueId());
			return Optional.of(germplasmListObservationDto);
		}
		return Optional.empty();
	}

	@Override
	public Integer saveListDataObservation(final Integer listId, final GermplasmListObservationRequestDto observationRequestDto) {
		final Optional<GermplasmListDataDetail> observationOptional = this.daoFactory.getGermplasmListDataDetailDAO()
			.getByListDataIdAndVariableId(observationRequestDto.getListDataId(), observationRequestDto.getVariableId());
		if (observationOptional.isPresent()) {
			throw new MiddlewareRequestException("", "germplasm.list.data.details.exists", "");
		}
		final GermplasmListData germplasmListData =
			this.daoFactory.getGermplasmListDataDAO().getById(observationRequestDto.getListDataId());
		final GermplasmListDataDetail germplasmListDataDetail =
			new GermplasmListDataDetail(germplasmListData, observationRequestDto.getVariableId(), observationRequestDto.getValue(),
				observationRequestDto.getcValueId());
		this.daoFactory.getGermplasmListDataDetailDAO().save(germplasmListDataDetail);
		return germplasmListDataDetail.getId();
	}

	@Override
	public void updateListDataObservation(final Integer observationId, final String value, final Integer cValueId) {
		final GermplasmListDataDetail germplasmListDataDetail = this.daoFactory.getGermplasmListDataDetailDAO().getById(observationId);
		germplasmListDataDetail.setValue(value);
		germplasmListDataDetail.setCategoricalValueId(cValueId);
		this.daoFactory.getGermplasmListDataDetailDAO().update(germplasmListDataDetail);
	}

	@Override
	public void deleteListDataObservation(final Integer observationId) {
		final GermplasmListDataDetail germplasmListDataDetail = this.daoFactory.getGermplasmListDataDetailDAO().getById(observationId);
		this.daoFactory.getGermplasmListDataDetailDAO().makeTransient(germplasmListDataDetail);
	}

	@Override
	public long countObservationsByVariables(final Integer listId, final List<Integer> variableIds) {
		return this.daoFactory.getGermplasmListDataDetailDAO().countObservationsByListAndVariables(listId, variableIds);
	}

	@Override
	public void editListMetadata(final GermplasmListDto germplasmListDto) {
		final GermplasmList germplasmList = this.getGermplasmListById(germplasmListDto.getListId())
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));
		germplasmList.setName(germplasmListDto.getListName());
		germplasmList.setDescription(germplasmListDto.getDescription());
		germplasmList.setType(germplasmListDto.getListType());
		germplasmList.setDate(Long.valueOf(this.dateFormat.format(germplasmListDto.getCreationDate())));
		germplasmList.setNotes(germplasmListDto.getNotes());
		this.daoFactory.getGermplasmListDAO().update(germplasmList);
	}

	@Override
	public long countEntryDetailsNamesAndAttributesAdded(final Integer listId) {
		return this.daoFactory.getGermplasmListDataViewDAO().countEntryDetailsNamesAndAttributesAdded(listId);
	}

	@Override
	public void deleteGermplasmList(final Integer listId) {
		final GermplasmList germplasmList = this.getGermplasmListById(listId)
			.orElseThrow(() -> new MiddlewareRequestException("", LIST_NOT_FOUND));
		germplasmList.setStatus(GermplasmList.Status.DELETED.getCode());
		this.daoFactory.getGermplasmListDAO().update(germplasmList);
	}

	@Override
	public void removeGermplasmEntriesFromList(final Integer germplasmListId,
		final SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite) {
		final Set<Integer> listDataIds =
			!CollectionUtils.isEmpty(searchComposite.getItemIds()) ? searchComposite.getItemIds() :
				this.germplasmListDataService.searchGermplasmListData(germplasmListId, searchComposite.getSearchRequest(), null).stream()
					.map(GermplasmListDataSearchResponse::getListDataId).collect(Collectors.toSet());
		this.daoFactory.getGermplasmListDataDetailDAO().deleteByListDataIds(listDataIds);
		this.daoFactory.getGermplasmListDataDAO().deleteByListDataIds(listDataIds);
		this.daoFactory.getGermplasmListDataDAO().reOrderEntries(germplasmListId);
	}

	private void updateGermplasmListData(final List<GermplasmListData> germplasmListData) {
		try {
			for (final GermplasmListData data : germplasmListData) {
				this.daoFactory.getGermplasmListDataDAO().update(data);
			}
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm List Data: GermplasmListServiceImpl.updateGermplasmListData(germplasmListData="
					+ germplasmListData + "): " + e.getMessage(),
				e);
		}
	}

	private void deleteGermplasmListData(final List<GermplasmListData> germplasmListData) {
		try {
			for (final GermplasmListData data : germplasmListData) {
				this.daoFactory.getGermplasmListDataDAO().makeTransient(data);
			}
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while deleting Germplasm List Data: GermplasmListServiceImpl.deleteGermplasmListData(germplasmListData="
					+ germplasmListData + "): " + e.getMessage(),
				e);
		}
	}

	private static class AddGermplasmEntryModel {

		private final Integer gid;
		private final String preferredName;
		private final Integer groupId;

		public AddGermplasmEntryModel(final Integer gid, final String preferredName, final Integer groupId) {
			this.gid = gid;
			this.preferredName = preferredName;
			this.groupId = groupId;
		}

		public Integer getGid() {
			return this.gid;
		}

		public String getPreferredName() {
			return this.preferredName;
		}

		public Integer getGroupId() {
			return this.groupId;
		}
	}

}
