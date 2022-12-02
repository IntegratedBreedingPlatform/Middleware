/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.service;

import com.google.common.base.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.generationcp.middleware.util.TimerWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
public class FieldbookServiceImpl extends Service implements FieldbookService {

	@Resource
	private GermplasmGroupingService germplasmGroupingService;

	@Resource
	private GermplasmListManager germplasmListManager;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Resource
	private UserService userService;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private WorkbookBuilder workbookBuilder;

	@Resource
	private DataSetBuilder dataSetBuilder;

	@Resource
	private WorkbookSaver workbookSaver;

	@Resource
	private StockBuilder stockBuilder;

	@Resource
	private LocationService locationService;

	@Resource
	private GermplasmListDataService germplasmListDataService;

	private DaoFactory daoFactory;

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);
	private static final List<Integer> FIELDMAP_TERM_IDS =
		Arrays.asList(TermId.FIELDMAP_COLUMN.getId(), TermId.FIELDMAP_RANGE.getId());

	public FieldbookServiceImpl() {
		super();
	}

	public FieldbookServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfTrial(final List<Integer> trialIdList,
			final CrossExpansionProperties crossExpansionProperties) {
		return this.studyDataManager.getFieldMapInfoOfStudy(trialIdList, crossExpansionProperties);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfNursery(final List<Integer> nurseryIdList,
			final CrossExpansionProperties crossExpansionProperties) {
		return this.studyDataManager.getFieldMapInfoOfStudy(nurseryIdList, crossExpansionProperties);
	}

	@Override
	public List<Location> getAllLocations() {
		return this.locationService.getAllLocations();
	}

	@Override
	public List<Location> getAllBreedingLocations() {
		return this.locationService.getAllBreedingLocations();
	}

	@Override
	public void saveOrUpdateFieldmapProperties(final List<FieldMapInfo> info, final int userId, final boolean isNew) {
		this.studyDataManager.saveOrUpdateFieldmapProperties(info, userId, isNew);
	}

	@Override
	public Study getStudy(final int studyId) {
		// not using the variable type
		return this.studyDataManager.getStudy(studyId, false);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(final int datasetId, final int geolocationId,
			final CrossExpansionProperties crossExpansionProperties) {
		return this.studyDataManager.getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, crossExpansionProperties);
	}

	@Override
	public List<DatasetReference> getDatasetReferences(final int studyId) {
		return this.studyDataManager.getDatasetReferences(studyId);
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethodRole(final String property, final String scale, final String method,
			final PhenotypicType role) {
		return this.getOntologyDataManager().getStandardVariableIdByPropertyScaleMethod(property, scale, method);
	}

	@Override
	public Workbook getStudyDataSet(final int id) {
		final Workbook workbook = this.workbookBuilder.create(id);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@Override
	public Workbook getStudyByNameAndProgramUUID(final String studyName, final String programUUID) {
		final int id = this.studyDataManager.getStudyIdByNameAndProgramUUID(studyName, programUUID);
		return this.getStudyDataSet(id);
	}

	@Override
	public boolean loadObservations(final Workbook workbook, final List<Integer> instanceNumbers, final List<Integer> repNumbers) {
		if (workbook.getObservations() == null || workbook.getObservations().isEmpty() && workbook.getStudyDetails() != null
				&& workbook.getStudyDetails().getId() != null) {
			this.workbookBuilder.loadObservations(workbook, instanceNumbers, repNumbers);
			return true;
		}
		return false;
	}

	public void saveWorkbookVariablesAndObservations(final Workbook workbook) {
		try {
			this.workbookSaver.saveWorkbookVariables(workbook);
			this.workbookSaver.removeDeletedVariablesAndObservations(workbook);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		}
	}

	@Override
	public void saveExperimentalDesign(
		final Workbook workbook, final String programUUID, final CropType crop) {
		final TimerWatch timerWatch = new TimerWatch("saveExperimentalDesign (grand total)");
		try {
			this.workbookSaver.saveProjectProperties(workbook);
			this.workbookSaver.removeDeletedVariablesAndObservations(workbook);
			final Map<String, ?> variableMap = this.workbookSaver.saveVariables(workbook, programUUID);
			this.workbookSaver.savePlotDataset(workbook, variableMap, programUUID, crop);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving to database: ", e);
		} finally {
			timerWatch.stop();
		}
	}

	@Override
	public List<Method> getAllBreedingMethods(final boolean filterOutGenerative) {
		final List<Method> methodList = filterOutGenerative ?
				this.getGermplasmDataManager().getAllMethodsNotGenerative() :
				this.getGermplasmDataManager().getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}

	@Override
	public Integer saveNurseryAdvanceGermplasmList(final List<Pair<Germplasm, List<Name>>> germplasms,
			final List<Pair<Germplasm, GermplasmListData>> listDataItems, final GermplasmList germplasmList,
			final List<Pair<Germplasm, List<Attribute>>> germplasmAttributes, final CropType cropType) {

		final GermplasmDAO germplasmDao = this.daoFactory.getGermplasmDao();
		final GermplasmListDAO germplasmListDao = this.daoFactory.getGermplasmListDAO();

		final long startTime = System.currentTimeMillis();

		try {
			germplasmListDao.save(germplasmList);
			int counter = 0;

			final Set<Integer> existingGids = new HashSet<>();
			final Set<Integer> parentGids = new HashSet<>();
			germplasms.stream()
				.map(Pair::getKey)
				.forEach(germplasm -> {
					if (germplasm.getGid() != null) {
						existingGids.add(germplasm.getGid());
					}
					if (germplasm.getMgid() > 0 && germplasm.getGpid2() > 0) {
						parentGids.add(germplasm.getGpid2());
					}
				});

			final Map<Integer, Germplasm> existingGermplasmsByGids =
				CollectionUtils.isEmpty(existingGids) ? new HashMap<>() : this.getGermplasmByGids(existingGids);
			final Map<Integer, List<BasicNameDTO>> parentGermplasmNamesByGids =
				CollectionUtils.isEmpty(parentGids) ? new HashMap<>() : this.getNamesByGids(parentGids);

			// Save germplasms, names, list data
			for (final Pair<Germplasm, List<Name>> pair : germplasms) {
				Germplasm germplasm = pair.getLeft();
				final GermplasmListData germplasmListData = listDataItems.get(counter).getRight();

				Germplasm germplasmFound = null;
				// Check if germplasm exists
				if (germplasm.getGid() != null) {
					// Check if the given gid exists
					germplasmFound = existingGermplasmsByGids.get(germplasm.getGid());

					// Check if the given germplasm name exists
					if (germplasmFound == null) {
						final List<Germplasm> germplasmsFound = this.getGermplasmDataManager()
								.getGermplasmByName(germplasm.getPreferredName().getNval(), 0, 1, Operation.EQUAL);

						if (!germplasmsFound.isEmpty()) {
							germplasmFound = germplasmsFound.get(0);
						}
					}
				}

				// This is where the new germplasm saves on advancing.
				// Save germplasm and name entries if non-existing
				if (germplasmFound == null || germplasmFound.getGid() == null) {
					final List<Name> nameList = germplasms.get(counter).getRight();

					GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(germplasm));
					germplasm = germplasmDao.save(germplasm);

					for (final Name name : nameList) {
						// Germplasm and Name entities are currently only mapped
						// as uni-directional OneToMany so we need to manage the
						// Name
						// side of the relationship link (Name.germplasmId)
						// manually.
						name.setGermplasm(germplasm);
						germplasm.getNames().add(name);
					}

					// inherit 'selection history at fixation' and code names of
					// parent
					// if parent is part of a group (= has mgid)
					if (germplasm.getMgid() > 0) {
						final List<BasicNameDTO> parentNames = parentGermplasmNamesByGids.get(germplasm.getGpid2());
						this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(germplasm, germplasm.getGpid2(), parentNames);
						this.germplasmGroupingService.copyCodedNames(germplasm, parentNames);
					}

					// Save Germplasm attributes
					final List<Attribute> attributesList = germplasmAttributes.get(counter).getRight();
					final AttributeDAO attributeDAO = this.daoFactory.getAttributeDAO();
					for (final Attribute attribute : attributesList) {
						attribute.setGermplasmId(germplasm.getGid());
						attributeDAO.save(attribute);
					}
				}

				// Save germplasmListData
				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
				counter++;
			}

			// Add default columns
			this.germplasmListDataService.saveDefaultView(germplasmList);

		} catch (final Exception e) {

			this.logAndThrowException(
					"Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms=" + germplasms + ", germplasmList="
							+ germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG
				.debug("========== saveNurseryAdvanceGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	/**
	 * Saves germplasm list crosses types. ListData items are always added to
	 * the database, before saving the germplasm list.
	 *
	 * @param listDataItems the list data to add - the key of the Map is the germplasm
	 *                      associated to the germplasm list data value
	 * @param germplasmList the germplasm list to add
	 * @return The id of the newly-created germplasm list
	 */
	@Override
	public Integer saveGermplasmList(final String cropName, final List<Pair<Germplasm, GermplasmListData>> listDataItems, final GermplasmList germplasmList,
			final boolean isApplyNewGroupToPreviousCrosses) {

		final GermplasmListDAO germplasmListDao = this.daoFactory.getGermplasmListDAO();

		final long startTime = System.currentTimeMillis();

		try {

			germplasmListDao.save(germplasmList);

			final Map<Integer, Integer> germplasmIdMethodIdMap = new HashMap<>();
			// Save germplasms, names, list data
			final GermplasmListDataDAO germplasmListDataDAO = this.daoFactory.getGermplasmListDataDAO();
			for (final Pair<Germplasm, GermplasmListData> pair : listDataItems) {

				final Germplasm germplasm = pair.getLeft();
				germplasmIdMethodIdMap.put(germplasm.getGid(), germplasm.getMethod().getMid());
				final GermplasmListData germplasmListData = pair.getRight();

				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				germplasmListDataDAO.save(germplasmListData);
			}

			this.germplasmListDataService.saveDefaultView(germplasmList);

			// For Management Group Settings Processing
			this.germplasmGroupingService.processGroupInheritanceForCrosses(cropName, germplasmIdMethodIdMap, isApplyNewGroupToPreviousCrosses,
					this.crossExpansionProperties.getHybridBreedingMethods());

		} catch (final Exception e) {
			this.logAndThrowException(
					"Error encountered with FieldbookService.saveGermplasmList(germplasmList=" + germplasmList + "): " + e
							.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	@Override
	public GermplasmList getGermplasmListByName(final String name, final String programUUID) {
		final List<GermplasmList> germplasmLists = this.germplasmListManager.getGermplasmListByName(name, programUUID, 0, 1, Operation.EQUAL);
		if (!germplasmLists.isEmpty()) {
			return germplasmLists.get(0);
		}
		return null;
	}

	@Override
	public Method getBreedingMethodById(final int mid) {
		return this.getGermplasmDataManager().getMethodByID(mid);
	}

	@Override
	public StandardVariable getStandardVariable(final int id, final String programUUID) {
		return this.getOntologyDataManager().getStandardVariable(id, programUUID);
	}

	@Override
	public int countPlotsWithRecordedVariatesInDataset(final int datasetId, final List<Integer> variateIds) {
		return this.studyDataManager.countPlotsWithRecordedVariatesInDataset(datasetId, variateIds);
	}

	@Override
	public Workbook getStudyVariableSettings(final int id) {
		return this.workbookBuilder.createStudyVariableSettings(id);
	}

	@Override
	public List<Germplasm> getGermplasms(final List<Integer> gids) {
		return this.getGermplasmDataManager().getGermplasms(gids);
	}

	@Override
	public List<Location> getAllFieldLocations(final int locationId) {
		return this.locationService.getAllFieldLocations(locationId);
	}

	@Override
	public List<Location> getAllBlockLocations(final int fieldId) {
		return this.locationService.getAllBlockLocations(fieldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(final int blockId) {
		return this.locationService.getBlockInformation(blockId);
	}

	@Override
	public List<Location> getAllFields() {
		return this.locationService.getAllFields();
	}

	@Override
	public int addFieldLocation(final String fieldName, final Integer parentLocationId, final Integer currentUserId) {
		return this
				.addLocation(fieldName, parentLocationId, currentUserId, LocationType.FIELD.getCode(), LocdesType.FIELD_PARENT.getCode());
	}

	@Override
	public int addBlockLocation(final String blockName, final Integer parentFieldId, final Integer currentUserId) {
		return this.addLocation(blockName, parentFieldId, currentUserId, LocationType.BLOCK.getCode(), LocdesType.BLOCK_PARENT.getCode());
	}

	public int addLocation(final String locationName, final Integer parentId, final Integer currentUserId, final String locCode,
			final String parentCode) {

		final Integer lType = this.locationService.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, locCode);
		final Location location = new Location(null, lType, 0, locationName, null, 0, 0, null, null, 0);

		final Integer dType = this.locationService.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, parentCode);
		final Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentId), 0, 0);
		return this.locationService.addLocationAndLocdes(location, locdes);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(final int blockId) {
		return this.studyDataManager.getAllFieldMapsInBlockByBlockId(blockId);
	}

	@Override
	public List<StandardVariable> getPossibleTreatmentPairs(final int cvTermId, final int propertyId, final List<Integer> hiddenFields) {
		final List<StandardVariable> treatmentPairs = new ArrayList<>();
		treatmentPairs.addAll(this.daoFactory.getCvTermDao().getAllPossibleTreatmentPairs(cvTermId, propertyId, hiddenFields));

		final List<Integer> termIds = new ArrayList<>();
		final Map<Integer, CVTerm> termMap = new HashMap<>();

		for (final StandardVariable pair : treatmentPairs) {
			termIds.add(pair.getProperty().getId());
			termIds.add(pair.getScale().getId());
			termIds.add(pair.getMethod().getId());
		}

		final List<CVTerm> terms = new ArrayList<>();
		terms.addAll(this.daoFactory.getCvTermDao().getByIds(termIds));

		for (final CVTerm term : terms) {
			termMap.put(term.getCvTermId(), term);
		}

		for (final StandardVariable pair : treatmentPairs) {
			pair.getProperty().setName(termMap.get(pair.getProperty().getId()).getName());
			pair.getProperty().setDefinition(termMap.get(pair.getProperty().getId()).getDefinition());
			pair.getScale().setName(termMap.get(pair.getScale().getId()).getName());
			pair.getScale().setDefinition(termMap.get(pair.getScale().getId()).getDefinition());
			pair.getMethod().setName(termMap.get(pair.getMethod().getId()).getName());
			pair.getMethod().setDefinition(termMap.get(pair.getMethod().getId()).getDefinition());
		}

		return treatmentPairs;
	}

	@Override
	public Location getLocationById(final int id) {
		return this.locationService.getLocationByID(id);
	}

	@Override
	public Location getLocationByName(final String locationName, final Operation op) {
		final List<Location> locations = this.locationService.getLocationsByName(locationName, 0, 1, op);
		if (locations != null && !locations.isEmpty()) {
			return locations.get(0);
		}
		return null;
	}

	@Override
	public int getMeasurementDatasetId(final int studyId) {
		return this.workbookBuilder.getMeasurementDataSetId(studyId);
	}

	@Override
	public long countObservations(final int datasetId) {
		return this.getExperimentBuilder().count(datasetId);
	}

	@Override
	public long countStocks(final int datasetId) {
		return this.stockBuilder.countStocks(datasetId);
	}

	@Override
	public boolean hasFieldLayout(final int datasetId) {
		return this.getExperimentBuilder().hasFieldLayout(datasetId);
	}

	@Override
	public GermplasmList getGermplasmListById(final Integer listId) {
		return this.germplasmListManager.getGermplasmListById(listId);
	}

	@Override
	public String getOwnerListName(final Integer userId) {
		final WorkbenchUser workbenchUser = this.userService.getUserById(userId);
		if (workbenchUser != null) {
				return workbenchUser.getPerson().getDisplayName();
		} else {
			return "";
		}
	}

	@Override
	public String getFolderNameById(final Integer folderId) {
		return this.studyDataManager.getFolderNameById(folderId);
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(final int datasetId, final List<Integer> variateIds) {
		return this.studyDataManager.checkIfStudyHasMeasurementData(datasetId, variateIds);
	}

	@Override
	public int countVariatesWithData(final int datasetId, final List<Integer> variateIds) {
		return this.studyDataManager.countVariatesWithData(datasetId, variateIds);
	}

	@Override
	public List<Integer> getGermplasmIdsByName(final String name) {
		return this.daoFactory.getNameDao().getGidsByName(name);
	}

	@Override
	public Integer getProjectIdByNameAndProgramUUID(final String name, final String programUUID) {
		return this.daoFactory.getDmsProjectDAO().getProjectIdByNameAndProgramUUID(name, programUUID);
	}

	@Override
	public MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(final String property, final String scale,
			final String method, final PhenotypicType role, final String programUUID) {
		final MeasurementVariable variable = null;
		final StandardVariable standardVariable;
		final Integer id = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (id != null) {
			standardVariable = this.getStandardVariableBuilder().create(id, programUUID);
			standardVariable.setPhenotypicType(role);
			return this.getMeasurementVariableTransformer().transform(standardVariable, false);
		}
		return variable;
	}

	@Override
	public void setTreatmentFactorValues(final List<TreatmentVariable> treatmentFactors, final int measurementDatasetID) {
		this.workbookBuilder.setTreatmentFactorValues(treatmentFactors, measurementDatasetID);
	}

	@Override
	public Workbook getCompleteDataset(final int datasetId) {
		final Workbook workbook = this.dataSetBuilder.buildCompleteDataset(datasetId);
		this.setOrderVariableByRank(workbook, datasetId);
		return workbook;
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes() {
		return this.germplasmListManager.getGermplasmNameTypes();
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGids(final List<Integer> gids) {
		return this.daoFactory.getNameDao().getNamesByGidsInMap(gids);
	}

	@Override
	public Map<Integer, List<BasicNameDTO>> getNamesByGids(final Set<Integer> gids) {
		return this.daoFactory.getNameDao().getBasicNamesByGids(new HashSet<>(gids))
			.stream()
			.collect(Collectors.groupingBy(BasicNameDTO::getGid, Collectors.toList()));
	}

	@Override
	public int countGermplasmListDataByListId(final Integer listId) {
		return (int) this.germplasmListManager.countGermplasmListDataByListId(listId);
	}

	@Override
	public Method getMethodById(final int id) {
		return this.getGermplasmDataManager().getMethodByID(id);
	}

	@Override
	public Method getMethodByCode(final String code) {
		return this.getGermplasmDataManager().getMethodByCode(code);
	}

	@Override
	public Method getMethodByName(final String name) {
		return this.getGermplasmDataManager().getMethodByName(name);
	}

	@Override
	public void saveStudyColumnOrdering(final Integer studyId, final List<Integer> orderedTermIds) {
		final int plotDatasetId = this.workbookBuilder.getMeasurementDataSetId(studyId);
		this.studyDataManager.updateVariableOrdering(plotDatasetId, orderedTermIds);
	}

	@Override
	public boolean setOrderVariableByRank(final Workbook workbook) {
		if (workbook != null) {
			final Integer studyId = workbook.getStudyDetails().getId();
			if (studyId != null) {
				final Integer plotDatasetId = this.workbookBuilder.getMeasurementDataSetId(studyId);
				this.setOrderVariableByRank(workbook, plotDatasetId);
			}
			return true;
		}
		return false;
	}

	public boolean setOrderVariableByRank(final Workbook workbook, final Integer plotDatasetId) {
		if (workbook != null) {
			final List<Integer> storedInIds = new ArrayList<>();
			storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
			storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
			workbook.setColumnOrderedLists(
				this.daoFactory.getProjectPropertyDAO().getDatasetVariableIdsForVariableTypeIds(plotDatasetId, storedInIds, null));
			return true;
		}
		return false;
	}

	@Override
	public StandardVariable getStandardVariableByName(final String name, final String programUUID) {
		return this.getStandardVariableBuilder().getByName(name, programUUID);
	}

	void setGermplasmGroupingService(final GermplasmGroupingService germplasmGroupingService) {
		this.germplasmGroupingService = germplasmGroupingService;
	}

	void setCrossExpansionProperties(final CrossExpansionProperties crossExpansionProperties) {
		this.crossExpansionProperties = crossExpansionProperties;
	}

	void setGermplasmListManager(final GermplasmListManager germplasmListManager) {
		this.germplasmListManager = germplasmListManager;
	}

	protected void setLocationService(final LocationService locationService) {
		this.locationService = locationService;
	}

	@Override
	public Optional<StudyReference> getStudyReferenceByNameAndProgramUUID(final String name, final String programUUID) {
		final Integer studyId = this.studyDataManager.getStudyIdByNameAndProgramUUID(name, programUUID);
		if (studyId != null) {
			return Optional.of(this.studyDataManager.getStudyReference(studyId));
		}
		return Optional.absent();
	}

	void setWorkbookBuilder(final WorkbookBuilder workbookBuilder) {
		this.workbookBuilder = workbookBuilder;
	}

	void setDataSetBuilder(final DataSetBuilder dataSetBuilder) {
		this.dataSetBuilder = dataSetBuilder;
	}

	void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	void setWorkbookSaver(final WorkbookSaver workbookSaver) {
		this.workbookSaver = workbookSaver;
	}

	public void setGermplasmListDataService(final GermplasmListDataService germplasmListDataService) {
		this.germplasmListDataService = germplasmListDataService;
	}

	@Override
	public List<String> deleteAllFieldMapsByTrialInstanceIds(final List<Integer> geolocationIds, final Integer projectId,
		final boolean deleteProjectProp, final boolean deleteFieldAndBlock) {
		this.daoFactory.getExperimentPropertyDao().deleteExperimentPropByLocationIds(geolocationIds, FIELDMAP_TERM_IDS);

		final Map<Integer, Integer> blocksToDeleteMap = this.daoFactory.getGeolocationPropertyDao()
			.getSafeToDeleteLocationBlockIdMap(geolocationIds);
		this.daoFactory.getGeolocationPropertyDao().deleteBlockPropertiesByGeolocationId(geolocationIds);

		final List<Integer> blockIdsToDelete = new ArrayList<>(blocksToDeleteMap.values());
		final List<Integer> instancesWithSharedBlock = geolocationIds.stream().filter(
			id -> !blocksToDeleteMap.containsKey(id)).collect(Collectors.toList());

		if (deleteFieldAndBlock && CollectionUtils.isNotEmpty(blockIdsToDelete)) {
			this.locationService.deleteBlockFieldLocationByBlockId(blockIdsToDelete);
		}

		if (deleteProjectProp) {
			this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(
				projectId, Arrays.asList(TermId.FIELDMAP_COLUMN.getId(), TermId.FIELDMAP_RANGE.getId()));
		}

		if (deleteFieldAndBlock && CollectionUtils.isNotEmpty(instancesWithSharedBlock)) {
			final List<Geolocation> geolocationList = this.daoFactory.getGeolocationDao().getByIds(instancesWithSharedBlock);
			return geolocationList.stream().map(Geolocation::getDescription).collect(Collectors.toList());
		}

		return Collections.EMPTY_LIST;
	}

	private Map<Integer, Germplasm> getGermplasmByGids(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getByGIDList(new ArrayList<>(gids))
			.stream()
			.collect(Collectors.toMap(Germplasm::getGid, Function.identity()));
	}

}
