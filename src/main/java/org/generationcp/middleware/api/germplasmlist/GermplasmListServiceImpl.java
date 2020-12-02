package org.generationcp.middleware.api.germplasmlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
@Service
public class GermplasmListServiceImpl implements GermplasmListService {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);
	private static final int MAX_CROSS_NAME_SIZE = 240;
	private static final String TRUNCATED = "(truncated)";
	private static final String GERMPLASM_PREFERRED_NAME = "";

	private final DaoFactory daoFactory;

	private enum GermplasmListDataPropertyName {

		PREFERRED_ID("PREFERRED ID"),
		GERMPLASM_LOCATION("LOCATIONS"),
		PREFERRED_NAME("PREFERRED NAME"),
		GERMPLASM_DATE("GERMPLASM DATE"),
		BREEDING_METHOD_NAME("METHOD NAME"),
		BREEDING_METHOD_ABBREVIATION("METHOD ABBREV"),
		BREEDING_METHOD_NUMBER("METHOD NUMBER"),
		BREEDING_METHOD_GROUP("METHOD GROUP"),
		FGID("FGID"),
		CROSS_FEMALE_PREFERRED_NAME("CROSS-FEMALE PREFERRED NAME"),
		MGID("MGID"),
		CROSS_MALE_PREFERRED_NAME("CROSS-MALE PREFERRED NAME"),
		GROUP_SOURCE_PREFERRED_NAME("GROUP SOURCE"),
		GROUP_SOURCE_GID("GROUP SOURCE GID"),
		IMMEDIATE_SOURCE_PREFERRED_NAME("IMMEDIATE SOURCE"),
		IMMEDIATE_SOURCE_GID("IMMEDIATE SOURCE GID");

		private final String name;

		GermplasmListDataPropertyName(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

	}

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
	private PedigreeDataManager pedigreeDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	public GermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmListGeneratorDTO create(final GermplasmListGeneratorDTO request, final int status, final String programUUID,
		final WorkbenchUser loggedInUser) {

		final List<Integer> gids = request.getEntries()
			.stream().map(GermplasmListGeneratorDTO.GermplasmEntryDTO::getGid).collect(Collectors.toList());
		final Map<Integer, String> preferredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);
		final Map<Integer, List<Name>> namesByGid = this.daoFactory.getNameDao().getNamesByGids(gids)
			.stream().collect(groupingBy(Name::getGermplasmId));

		final Integer currentUserId = loggedInUser.getUserid();
		final GermplasmList parent = request.getParentFolderId() != null ?
			this.daoFactory.getGermplasmListDAO().getById(Integer.valueOf(request.getParentFolderId()), false) : null;
		final String description = request.getDescription() != null ? request.getDescription() : StringUtils.EMPTY;

		// save list
		GermplasmList germplasmList = new GermplasmList(null, request.getName(), Long.valueOf(DATE_FORMAT.format(request.getDate())),
			request.getType(), currentUserId, description, parent, status, request.getNotes());
		germplasmList.setProgramUUID(programUUID);
		germplasmList = this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		request.setId(germplasmList.getId());

		// save germplasm list data
		for (final GermplasmListGeneratorDTO.GermplasmEntryDTO entry : request.getEntries()) {
			final Integer gid = entry.getGid();
			final String preferredName = preferredNamesMap.get(gid);
			final List<Name> names = namesByGid.get(gid);
			Preconditions.checkArgument(preferredName != null || names != null, "No name found for gid=" + gid);
			final String designation = preferredName != null ? preferredName : names.get(0).getNval();
			final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entry.getEntryNo(),
				entry.getEntryCode(), entry.getSeedSource(), designation, entry.getGroupName(),
				GermplasmListDataDAO.STATUS_ACTIVE, null);
			this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
		}

		return request;
	}

	@Override
	public List<GermplasmListData> addGermplasmListData(final List<GermplasmListData> data) {

		final List<GermplasmListData> idGermplasmListDataSaved = new ArrayList<>();
		try {
			final List<Integer> deletedListEntryIds = new ArrayList<>();
			data.forEach(germplasmListData -> {
				String groupName = germplasmListData.getGroupName();
				if (groupName.length() > MAX_CROSS_NAME_SIZE) {
					groupName = groupName.substring(0, MAX_CROSS_NAME_SIZE - 1);
					groupName = groupName + TRUNCATED;
					germplasmListData.setGroupName(groupName);
				}

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
				"Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(data="
					+ data + "): " + e.getMessage(),
				e);
		}

		return idGermplasmListDataSaved;
	}

	@Override
	public void addGermplasmEntriesToList(final Integer germplasmListId,
		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite, final String programUUID) {

		final GermplasmList germplasmList = this.getGermplasmListById(germplasmListId)
			.orElseThrow(() -> new MiddlewareRequestException("", "list.not.found"));

		//Get the germplasm entries to add
		final List<AddGermplasmEntryModel> addGermplasmEntriesModels = new ArrayList<>();
		if (CollectionUtils.isEmpty(searchComposite.getItemIds())) {
			final GermplasmSearchRequest searchRequest = searchComposite.getSearchRequest();
			if(Objects.isNull(searchRequest.getAddedColumnsPropertyIds())) {
				searchRequest.setAddedColumnsPropertyIds(new ArrayList<>());
			}
			if (!searchRequest.getAddedColumnsPropertyIds().contains(GERMPLASM_PREFERRED_NAME)) {
				searchRequest.getAddedColumnsPropertyIds().add(GERMPLASM_PREFERRED_NAME);
			}

			this.germplasmSearchService.searchGermplasm(searchRequest, null, programUUID)
				.forEach(germplasmSearchResponse -> addGermplasmEntriesModels.add(new AddGermplasmEntryModel(
					germplasmSearchResponse.getGid(),
					germplasmSearchResponse.getGermplasmPeferredName(),
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

		//Get the entryId max value
		final int maxEntryId = germplasmList.getListData()
			.stream()
			.mapToInt(GermplasmListData::getEntryId)
			.max()
			.orElse(1);
		final AtomicInteger lastEntryId = new AtomicInteger(maxEntryId);

		//Create germplasm lists data
		final List<GermplasmListData> germplasmListsData = addGermplasmEntriesModels.stream().map(model -> {
			final int entryId = lastEntryId.addAndGet(1);

			return new GermplasmListData(null,
				germplasmList,
				model.getGid(),
				entryId,
				String.valueOf(entryId),
				this.germplasmService.getPlotCodeValue(model.getGid()),
				model.getPreferredName(),
				this.pedigreeService.getCrossExpansion(model.getGid(), this.crossExpansionProperties),
				0,
				0,
				model.getGroupId());
		}).collect(Collectors.toList());
		final List<GermplasmListData> savedGermplasmListData = this.addGermplasmListData(germplasmListsData);

		if (CollectionUtils.isEmpty(savedGermplasmListData) ||
			CollectionUtils.isEmpty(germplasmList.getListData().get(0).getProperties())) {
			return;
		}

		final Set<String> propertyNames = germplasmList.getListData().get(0).getProperties()
			.stream()
			.map(ListDataProperty::getColumn)
			.collect(Collectors.toSet());
		this.addListDataProperties(savedGermplasmListData, propertyNames);
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
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getGermplasmListByParentAndName(germplasmListName, parentId, programUUID));
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
	public Integer updateGermplasmListFolder(final Integer userId, final String folderName, final Integer folderId,
		final String programUUID) {

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
			this.getGermplasmListById(newParentFolderId).orElseThrow(() -> new MiddlewareRequestException("", "list.parent.folder.not.found"));

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

	private void addListDataProperties(final List<GermplasmListData> savedGermplasmListData, final Set<String> propertyNames) {
		final Map<Integer, GermplasmListData> listDataIndexedByGid = savedGermplasmListData
			.stream()
			.collect(Collectors.toMap(GermplasmListData::getGid, germplasmListData -> germplasmListData));
		final List<Integer> gids = new ArrayList<>(listDataIndexedByGid.keySet());



		propertyNames
			.forEach(property -> {

				if (GermplasmListDataPropertyName.PREFERRED_ID.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.PREFERRED_ID.getName(),
						() -> this.germplasmDataManager.getPreferredIdsByGIDs(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.GERMPLASM_LOCATION.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.GERMPLASM_LOCATION.getName(),
						() -> this.germplasmDataManager.getLocationNamesByGids(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.PREFERRED_NAME.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.PREFERRED_NAME.getName(),
						() -> this.germplasmDataManager.getPreferredNamesByGids(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.GERMPLASM_DATE.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.GERMPLASM_DATE.getName(),
						() -> this.germplasmDataManager.getGermplasmDatesByGids(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName().equals(property)) {
					this.addBreedingMethodPropertyValue(GermplasmListDataPropertyName.BREEDING_METHOD_NAME, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.BREEDING_METHOD_ABBREVIATION.getName().equals(property) ) {
					this.addBreedingMethodPropertyValue(GermplasmListDataPropertyName.BREEDING_METHOD_ABBREVIATION, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.BREEDING_METHOD_NUMBER.getName().equals(property)) {
					this.addBreedingMethodPropertyValue(GermplasmListDataPropertyName.BREEDING_METHOD_NUMBER, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.BREEDING_METHOD_GROUP.getName().equals(property)) {
					this.addBreedingMethodPropertyValue(GermplasmListDataPropertyName.BREEDING_METHOD_GROUP, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.FGID.getName().equals(property)) {
					this.addCrossFemaleDataPropertyValues(GermplasmListDataPropertyName.FGID, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.CROSS_FEMALE_PREFERRED_NAME.getName().equals(property)) {
					this.addCrossFemaleDataPropertyValues(GermplasmListDataPropertyName.CROSS_FEMALE_PREFERRED_NAME, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.MGID.getName().equals(property)) {
					this.addCrossMaleDataPropertyValues(GermplasmListDataPropertyName.MGID, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.CROSS_MALE_PREFERRED_NAME.getName().equals(property)) {
					this.addCrossMaleDataPropertyValues(GermplasmListDataPropertyName.CROSS_MALE_PREFERRED_NAME, listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.GROUP_SOURCE_PREFERRED_NAME.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.GROUP_SOURCE_PREFERRED_NAME.getName(),
						() -> this.germplasmDataManager.getGroupSourcePreferredNamesByGids(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.GROUP_SOURCE_GID.getName().equals(property)) {
					final Supplier<Map<Integer, String>> valueSupplier = () -> this.germplasmService.getGermplasmByGIDs(gids)
						.stream()
						.collect(Collectors.toMap(Germplasm::getGid,
							germplasm -> germplasm.getGnpgs() == -1 && !Objects.isNull(germplasm.getGpid1()) && germplasm.getGpid1() != 0
								? germplasm.getGpid1().toString() : "-"));

					this.addListDataProperties(GermplasmListDataPropertyName.GROUP_SOURCE_GID.getName(),
						valueSupplier,
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.IMMEDIATE_SOURCE_PREFERRED_NAME.getName().equals(property)) {
					this.addListDataProperties(GermplasmListDataPropertyName.IMMEDIATE_SOURCE_PREFERRED_NAME.getName(),
						() -> this.germplasmDataManager.getGroupSourcePreferredNamesByGids(gids),
						listDataIndexedByGid);
					return;
				}

				if (GermplasmListDataPropertyName.IMMEDIATE_SOURCE_GID.getName().equals(property)) {
					final Supplier<Map<Integer, String>> valueSupplier = () -> this.germplasmService.getGermplasmByGIDs(gids)
						.stream()
						.collect(Collectors.toMap(Germplasm::getGid,
							germplasm -> germplasm.getGnpgs() == -1 && !Objects.isNull(germplasm.getGpid2()) && germplasm.getGpid2() != 0
								? germplasm.getGpid2().toString() : "-"));

					this.addListDataProperties(GermplasmListDataPropertyName.IMMEDIATE_SOURCE_GID.getName(),
						valueSupplier,
						listDataIndexedByGid);
					return;
				}

				// Check if any of the columns are attribute types
				final Map<String, Integer> attributeTypesMap = this.getAllAttributeTypesMap();
				final Integer attributeTypeId = attributeTypesMap.get(property);
				if (!Objects.isNull(attributeTypeId)) {
					this.addListDataProperties(property,
						() -> this.germplasmDataManager.getAttributeValuesByTypeAndGIDList(attributeTypeId, gids),
						listDataIndexedByGid);
					return;
				}

				// Check if any of the columns are name types
				final Map<String, Integer> nameTypesMap = this.getAllNameTypesMap();
				final Integer nameTypeId = nameTypesMap.get(property);
				if (!Objects.isNull(nameTypeId)) {
					this.addListDataProperties(property,
						() -> this.germplasmDataManager.getNamesByTypeAndGIDList(nameTypeId, gids),
						listDataIndexedByGid);
					return;
				}
			});
	}

	private void addBreedingMethodPropertyValue(final GermplasmListDataPropertyName propertyName, final Map<Integer, GermplasmListData> listDataIndexedByGid) {
		final Supplier<Map<Integer, String>> valueSupplier = () -> this.germplasmDataManager.getMethodsByGids(new ArrayList<>(listDataIndexedByGid.keySet()))
			.entrySet()
			.stream()
			.collect(Collectors.toMap(entrySet -> entrySet.getKey(), entrySet -> this.getBreedingMethodValue(propertyName.getName(), entrySet)));

		this.addListDataProperties(propertyName.getName(),
			valueSupplier,
			listDataIndexedByGid);
	}

	private <T> void addListDataProperties(final String propertyName, final Supplier<Map<Integer, T>> valuesSupplier, final Map<Integer, GermplasmListData> listDataIndexedByGid) {
		valuesSupplier.get()
			.entrySet()
			.forEach(entrySet -> this.addListDataProperty(listDataIndexedByGid.get(entrySet.getKey()),
				propertyName,
				Objects.isNull(entrySet.getValue()) ? null : entrySet.getValue().toString()));
	}

	private <T> void addListDataProperty(final GermplasmListData listData, final String propertyName, final String value) {
		final ListDataProperty listDataProperty = new ListDataProperty(
			listData,
			propertyName,
			value
		);
		this.daoFactory.getListDataPropertyDAO().save(listDataProperty);
	}

	private void addCrossFemaleDataPropertyValues(final GermplasmListDataPropertyName propertyName, final Map<Integer, GermplasmListData> listDataIndexedByGid) {
		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final Table<Integer, String, Optional<Germplasm>>
			table = this.pedigreeDataManager.generatePedigreeTable(listDataIndexedByGid.keySet(), level, false);

		listDataIndexedByGid.keySet().forEach(gid -> {
			final Optional<Germplasm> femaleParent = table.get(gid, GermplasmListDataPropertyName.FGID.getName());
			final String value;
			if (propertyName == GermplasmListDataPropertyName.FGID) {
				value = this.getGermplasmGid(femaleParent);
			} else if (propertyName == GermplasmListDataPropertyName.CROSS_FEMALE_PREFERRED_NAME) {
				value = this.getGermplasmPreferredName(femaleParent);
			} else {
				value = "-";
			}

			this.addListDataProperty(listDataIndexedByGid.get(gid), propertyName.getName(), value);
		});
	}

	private void addCrossMaleDataPropertyValues(final GermplasmListDataPropertyName propertyName, final Map<Integer, GermplasmListData> listDataIndexedByGid) {

		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final Table<Integer, String, Optional<Germplasm>> table = this.pedigreeDataManager.generatePedigreeTable(listDataIndexedByGid.keySet(), level, false);

		listDataIndexedByGid.keySet().forEach(gid -> {
			final Optional<Germplasm> maleParent = table.get(gid, GermplasmListDataPropertyName.MGID.getName());
			final String value;
			if (propertyName == GermplasmListDataPropertyName.MGID) {
				value = this.getGermplasmGid(maleParent);
			} else if (propertyName == GermplasmListDataPropertyName.CROSS_MALE_PREFERRED_NAME) {
				value = this.getGermplasmPreferredName(maleParent);
			} else {
				value = "-";
			}
			this.addListDataProperty(listDataIndexedByGid.get(gid), propertyName.getName(), value);
		});
	}

	private String getGermplasmGid(final Optional<Germplasm> germplasm) {
		if (germplasm.isPresent()) {
			if(germplasm.get().getGid()!=0) {
				return String.valueOf(germplasm.get().getGid());
			} else {
				return Name.UNKNOWN;
			}
		} else {
			return "-";
		}
	}

	private String getGermplasmPreferredName(final Optional<Germplasm> germplasm) {
		if (germplasm.isPresent()) {
			return germplasm.get().getPreferredName().getNval();
		} else {
			return "-";
		}
	}

	private String getBreedingMethodValue(final String property, final Map.Entry<Integer, Object> entrySet) {
		if (GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName().equals(property)) {
			return ((Method) entrySet.getValue()).getMname();
		}

		if (GermplasmListDataPropertyName.BREEDING_METHOD_ABBREVIATION.getName().equals(property)) {
			return ((Method) entrySet.getValue()).getMcode();
		}

		if (GermplasmListDataPropertyName.BREEDING_METHOD_NUMBER.getName().equals(property)) {
			return ((Method) entrySet.getValue()).getMid().toString();
		}

		if (GermplasmListDataPropertyName.BREEDING_METHOD_GROUP.getName().equals(property)) {
			return ((Method) entrySet.getValue()).getMgrp();
		}

		return "";
	}

	private Map<String, Integer> getAllAttributeTypesMap() {
		return this.germplasmDataManager.getAllAttributesTypes()
			.stream()
			.collect(Collectors.toMap(userDefinedField -> userDefinedField.getFcode().toUpperCase(), userDefinedField -> userDefinedField.getFldno()));
	}

	private Map<String, Integer> getAllNameTypesMap() {
		return this.germplasmListManager.getGermplasmNameTypes()
			.stream()
			.collect(Collectors.toMap(userDefinedField -> userDefinedField.getFname().toUpperCase(), userDefinedField -> userDefinedField.getFldno()));
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
			return gid;
		}

		public String getPreferredName() {
			return preferredName;
		}

		public Integer getGroupId() {
			return groupId;
		}
	}

}
