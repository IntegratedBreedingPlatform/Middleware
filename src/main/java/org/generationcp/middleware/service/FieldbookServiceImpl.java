/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.fieldbook.NonEditableFactors;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.UnpermittedDeletionException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FieldbookServiceImpl extends Service implements FieldbookService {

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

	public FieldbookServiceImpl() {
		super();
	}

	public FieldbookServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public List<StudyDetails> getAllLocalNurseryDetails(String programUUID) {
		List<StudyDetails> studyDetailList = this.getStudyDataManager().getAllStudyDetails(StudyType.N, programUUID);
		return FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailList);
	}

	@Override
	public List<StudyDetails> getAllLocalTrialStudyDetails(String programUUID) {
		List<StudyDetails> studyDetailList = this.getStudyDataManager().getAllStudyDetails(StudyType.T, programUUID);
		return FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailList);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList, CrossExpansionProperties crossExpansionProperties) {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(trialIdList, StudyType.T, crossExpansionProperties);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList, CrossExpansionProperties crossExpansionProperties) {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(nurseryIdList, StudyType.N, crossExpansionProperties);
	}

	@Override
	public List<Location> getAllLocations() {
		Integer fieldLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		Integer blockLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());

		List<Location> locList = this.getLocationDataManager().getAllLocations();
		List<Location> newLocation = new ArrayList<Location>();

		for (Location loc : locList) {
			if (fieldLtypeFldId != null && fieldLtypeFldId.intValue() == loc.getLtype().intValue()
					|| blockLtypeFldId != null && blockLtypeFldId.intValue() == loc.getLtype().intValue()) {
				continue;
			}
			newLocation.add(loc);
		}

		return newLocation;
	}

	@Override
	public List<Location> getAllBreedingLocations() {
		return this.getLocationDataManager().getAllBreedingLocations();
	}

	@Override
	public List<Location> getAllSeedLocations() {
		Integer seedLType =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.getLocationDataManager().getLocationsByType(seedLType);
	}

	@Override
	public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) {
		this.getStudyDataManager().saveOrUpdateFieldmapProperties(info, userId, isNew);
	}

	@Override
	public Study getStudy(int studyId) {
		// not using the variable type
		return this.getStudyDataManager().getStudy(studyId, false);
	}

	@Override
	public List<Location> getFavoriteLocationByLocationIDs(List<Integer> locationIds) {
		return this.getLocationDataManager().getLocationsByIDs(locationIds);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId,
			CrossExpansionProperties crossExpansionProperties) {
		return this.getStudyDataManager().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, crossExpansionProperties);
	}

	@Override
	public List<DatasetReference> getDatasetReferences(int studyId) {
		return this.getStudyDataManager().getDatasetReferences(studyId);
	}

	@Override
	public Integer getGermplasmIdByName(String name) {

		List<Germplasm> germplasmList = this.getGermplasmDataManager().getGermplasmByName(name, 0, 1, Operation.EQUAL);
		Integer gid = null;
		if (germplasmList != null && !germplasmList.isEmpty()) {
			gid = germplasmList.get(0).getGid();
		}
		return gid;
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role) {
		return this.getOntologyDataManager().getStandardVariableIdByPropertyScaleMethod(property, scale, method);
	}

	@Override
	public Workbook getNurseryDataSet(int id) {
		Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.N);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@Override
	public Workbook getTrialDataSet(int id) {
		Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.T);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveMeasurementRows(Workbook workbook, String programUUID) {

		long startTime = System.currentTimeMillis();

		try {

			List<Integer> deletedVariateIds = this.getDeletedVariateIds(workbook.getVariates());

			List<MeasurementVariable> variates = workbook.getVariates();
			List<MeasurementVariable> factors = workbook.getFactors();
			List<MeasurementRow> observations = workbook.getObservations();

			this.getWorkbookSaver().saveWorkbookVariables(workbook);
			this.getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);

			final Map<String, ?> variableMap = this.getWorkbookSaver().saveVariables(workbook, programUUID);

			// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
			Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
			Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");

			// unpack maps
			// Strings
			List<String> trialHeaders = headerMap.get("trialHeaders");

			// VariableTypeLists
			VariableTypeList effectVariables = variableTypeMap.get("effectVariables");

			// get the trial dataset id
			Integer trialDatasetId = workbook.getTrialDatasetId();
			if (trialDatasetId == null) {
				trialDatasetId = this.getWorkbookBuilder().getTrialDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
			}

			// save trial observations
			this.getWorkbookSaver().saveTrialObservations(workbook, programUUID);

			Integer measurementDatasetId = workbook.getMeasurementDatesetId();
			if (measurementDatasetId == null) {
				measurementDatasetId =
						this.getWorkbookBuilder().getMeasurementDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
			}

			// save factors
			this.getWorkbookSaver().createStocksIfNecessary(measurementDatasetId, workbook, effectVariables, trialHeaders);

			if (factors != null) {
				for (MeasurementVariable factor : factors) {
					if (NonEditableFactors.find(factor.getTermId()) == null) {
						for (MeasurementRow row : observations) {
							for (MeasurementData field : row.getDataList()) {
								if (factor.getName().equals(field.getLabel()) && factor.getRole() == PhenotypicType.TRIAL_DESIGN) {
									this.getExperimentPropertySaver().saveOrUpdateProperty(
											this.getExperimentDao().getById(row.getExperimentId()), factor.getTermId(), field.getValue());
								}
							}
						}
					}
				}
			}

			// save variates
			if (variates != null) {
				for (MeasurementVariable variate : variates) {
					if (deletedVariateIds != null && !deletedVariateIds.isEmpty() && deletedVariateIds.contains(variate.getTermId())) {
						// skip this was already deleted.
					} else {
						for (MeasurementRow row : observations) {
							for (MeasurementData field : row.getDataList()) {
								if (variate.getName().equals(field.getLabel())) {
									Phenotype phenotype = this.getPhenotypeDao().getPhenotypeByProjectExperimentAndType(
											measurementDatasetId, row.getExperimentId(), variate.getTermId());
									if (field.getValue() != null) {
										field.setValue(field.getValue().trim());
									}
									if (field.getPhenotypeId() != null) {
										phenotype = this.getPhenotypeDao().getById(field.getPhenotypeId());
									}
									if (phenotype == null && field.getValue() != null && !"".equals(field.getValue().trim())) {
										phenotype = new Phenotype();
									}
									if (phenotype != null) {

										this.getPhenotypeSaver().saveOrUpdate(
												row.getExperimentId(),
												variate.getTermId(),
												field.getcValueId() != null && !"".equals(field.getcValueId()) ? field.getcValueId()
														: field.getValue(), phenotype, field.isCustomCategoricalValue(),
														variate.getDataTypeId());
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveMeasurementRows Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

	}

	@Override
	public List<Method> getAllBreedingMethods(boolean filterOutGenerative) {
		List<Method> methodList = filterOutGenerative ? this.getGermplasmDataManager().getAllMethodsNotGenerative()
				: this.getGermplasmDataManager().getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}

	@Override
	public List<Method> getFavoriteBreedingMethods(List<Integer> methodIds, boolean filterOutGenerative) {
		List<Method> methodList;
		if (filterOutGenerative) {
			methodList = getGermplasmDataManager().getNonGenerativeMethodsByID(methodIds);
		} else {
			methodList = getGermplasmDataManager().getMethodsByIDs(methodIds);
		}

		return methodList;
	}

	// TODO : optimize / remove loop
	@Override
	public Integer saveNurseryAdvanceGermplasmList(Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listDataItems,
			GermplasmList germplasmList) {

		GermplasmDAO germplasmDao = this.getGermplasmDao();
		NameDAO nameDao = this.getNameDao();
		GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		long startTime = System.currentTimeMillis();

		try {
			germplasmListDao.save(germplasmList);

			// Save germplasms, names, list data
			for (Germplasm germplasm : germplasms.keySet()) {

				GermplasmListData germplasmListData = listDataItems.get(germplasm);

				Germplasm germplasmFound = null;

				// Check if germplasm exists
				if (germplasm.getGid() != null) {

					// Check if the given gid exists
					germplasmFound = this.getGermplasmDataManager().getGermplasmByGID(germplasm.getGid());

					// Check if the given germplasm name exists
					if (germplasmFound == null) {
						List<Germplasm> germplasmsFound = this.getGermplasmDataManager()
								.getGermplasmByName(germplasm.getPreferredName().getNval(), 0, 1, Operation.EQUAL);

						if (!germplasmsFound.isEmpty()) {
							germplasmFound = germplasmsFound.get(0);
						}
					}
				}

				// Save germplasm and name entries if non-existing
				if (germplasmFound == null || germplasmFound.getGid() == null) {
					List<Name> nameList = germplasms.get(germplasm);
					// Lgid could not be null in the DB, so we are saving a value before saving it to the DB
					if (germplasm.getLgid() == null) {
						germplasm.setLgid(germplasm.getGid() != null ? germplasm.getGid() : Integer.valueOf(0));
					}
					germplasm = germplasmDao.save(germplasm);
					// set Lgid to GID if it's value was not set previously
					if (germplasm.getLgid().equals(Integer.valueOf(0))) {
						germplasm.setLgid(germplasm.getGid());
					}

					// Save name entries
					for (Name name : nameList) {
						name.setGermplasmId(germplasm.getGid());
						nameDao.save(name);
					}
				}

				// Save germplasmListData
				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);
			}

		} catch (Exception e) {

			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms=" + germplasms
					+ ", germplasmList=" + germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG
		.debug("========== saveNurseryAdvanceGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	@Override
	public Integer saveGermplasmList(Map<Germplasm, GermplasmListData> listDataItems, GermplasmList germplasmList) {



		GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		long startTime = System.currentTimeMillis();

		try {

			germplasmListDao.save(germplasmList);

			// Save germplasms, names, list data
			for (Entry<Germplasm, GermplasmListData> entry : listDataItems.entrySet()) {

				Germplasm germplasm = entry.getKey();
				GermplasmListData germplasmListData = entry.getValue();

				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);
			}

		} catch (Exception e) {
			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasmList="
					+ germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	@Override
	public String getCimmytWheatGermplasmNameByGid(int gid) {
		List<Name> names = this.getByGidAndNtype(gid, GermplasmNameType.CIMMYT_SELECTION_HISTORY);
		if (names == null || names.isEmpty()) {
			names = this.getByGidAndNtype(gid, GermplasmNameType.UNRESOLVED_NAME);
		}
		return names != null && !names.isEmpty() ? names.get(0).getNval() : null;
	}

	private List<Name> getByGidAndNtype(int gid, GermplasmNameType nType) {
		return this.getNameDao().getByGIDWithFilters(gid, null, nType);
	}

	@Override
	public GermplasmList getGermplasmListByName(String name, String programUUID) {
		List<GermplasmList> germplasmLists =
				this.getGermplasmListManager().getGermplasmListByName(name, programUUID, 0, 1, Operation.EQUAL);
		if (!germplasmLists.isEmpty()) {
			return germplasmLists.get(0);
		}
		return null;
	}

	@Override
	public Method getBreedingMethodById(int mid) {
		return this.getGermplasmDataManager().getMethodByID(mid);
	}

	@Override
	public Germplasm getGermplasmByGID(int gid) {
		return this.getGermplasmDataManager().getGermplasmByGID(gid);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) {
		return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(String property, String scale, String method, PhenotypicType role) {

		Integer stdVarId = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (stdVarId != null) {
			return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
		}
		return new ArrayList<ValueReference>();
	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(String programUUID) {
		return this.getOntologyDataManager().getAllStandardVariables(programUUID);
	}

	@Override
	public StandardVariable getStandardVariable(int id, String programUUID) {
		return this.getOntologyDataManager().getStandardVariable(id, programUUID);
	}

	@Override
	public List<ValueReference> getAllNurseryTypes(String programUUID) {

		List<ValueReference> nurseryTypes = new ArrayList<ValueReference>();

		StandardVariable stdVar = this.getOntologyDataManager().getStandardVariable(TermId.NURSERY_TYPE.getId(), programUUID);
		List<Enumeration> validValues = stdVar.getEnumerations();

		if (validValues != null) {
			for (Enumeration value : validValues) {
				if (value != null) {
					nurseryTypes.add(new ValueReference(value.getId(), value.getName(), value.getDescription()));
				}
			}
		}

		return nurseryTypes;
	}

	@Override
	public List<Person> getAllPersons() {
		return this.getUserDataManager().getAllPersons();
	}

	@Override
	public List<Person> getAllPersonsOrderedByLocalCentral() {
		return this.getUserDataManager().getAllPersonsOrderedByLocalCentral();
	}

	@Override
	public int countPlotsWithRecordedVariatesInDataset(int datasetId, List<Integer> variateIds) {

		return this.getStudyDataManager().countPlotsWithRecordedVariatesInDataset(datasetId, variateIds);
	}

	@Override
	public List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds, List<Integer> propertyIds,
			boolean isRemoveProperties) {
		List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		List<CVTerm> variables = new ArrayList<CVTerm>();
		Set<Integer> variableIds = new HashSet<Integer>();

		this.addAllVariableIdsInMode(variableIds, storedInIds);
		if (propertyIds != null && !propertyIds.isEmpty()) {
			Set<Integer> propertyVariableList = new HashSet<Integer>();
			this.createPropertyList(propertyVariableList, propertyIds);
			this.filterByProperty(variableIds, propertyVariableList, isRemoveProperties);
		}

		List<Integer> variableIdList = new ArrayList<Integer>(variableIds);
		variables.addAll(this.getCvTermDao().getValidCvTermsByIds(variableIdList, TermId.CATEGORICAL_VARIATE.getId(),
				TermId.CATEGORICAL_VARIABLE.getId()));
		for (CVTerm variable : variables) {
			list.add(new StandardVariableReference(variable.getCvTermId(), variable.getName(), variable.getDefinition()));
		}
		return list;
	}

	@Override
	public List<StandardVariableReference> filterStandardVariablesByIsAIds(List<StandardVariableReference> standardReferences,
			List<Integer> isAIds) {
		List<StandardVariableReference> newRefs = new ArrayList<StandardVariableReference>();
		try {
			List<StandardVariableSummary> variableSummaries = this.getStandardVariableDao().getStandardVariableSummaryWithIsAId(isAIds);
			for (StandardVariableReference ref : standardReferences) {
				boolean isFound = false;
				for (StandardVariableSummary summary : variableSummaries) {
					if (ref.getId().intValue() == summary.getId().intValue()) {
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					newRefs.add(ref);
				}
			}
		} catch (MiddlewareQueryException e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			newRefs = standardReferences;
		}
		return newRefs;
	}

	private void addAllVariableIdsInMode(Set<Integer> variableIds, List<Integer> storedInIds) {
		for (Integer storedInId : storedInIds) {
			variableIds.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.STORED_IN.getId(), storedInId));
		}
	}

	private void createPropertyList(Set<Integer> propertyVariableList, List<Integer> propertyIds) {
		for (Integer propertyId : propertyIds) {
			propertyVariableList
			.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), propertyId));
		}
	}

	private void filterByProperty(Set<Integer> variableIds, Set<Integer> variableListByProperty, boolean isRemoveProperties) {
		// delete variables not in the list of filtered variables by property
		Iterator<Integer> iter = variableIds.iterator();
		boolean inList = false;

		if (isRemoveProperties) {
			// remove variables having the specified properties from the list
			while (iter.hasNext()) {
				Integer id = iter.next();
				for (Integer variable : variableListByProperty) {
					if (id.equals(variable)) {
						iter.remove();
					}
				}
			}
		} else {
			// remove variables not in the property list
			while (iter.hasNext()) {
				inList = false;
				Integer id = iter.next();
				for (Integer variable : variableListByProperty) {
					if (id.equals(variable)) {
						inList = true;
					}
				}
				if (!inList) {
					iter.remove();
				}
			}
		}
	}

	@Override
	public Workbook getStudyVariableSettings(int id, boolean isNursery) {
		return this.getWorkbookBuilder().createStudyVariableSettings(id, isNursery);
	}

	@Override
	public List<Germplasm> getGermplasms(List<Integer> gids) {
		return this.getGermplasmDataManager().getGermplasms(gids);
	}

	@Override
	public List<Location> getAllFieldLocations(int locationId) {
		return this.getLocationDataManager().getAllFieldLocations(locationId);
	}

	@Override
	public List<Location> getAllBlockLocations(int fieldId) {
		return this.getLocationDataManager().getAllBlockLocations(fieldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(int blockId) {
		return this.getLocationDataManager().getBlockInformation(blockId);
	}

	@Override
	public List<Location> getAllFields() {
		return this.getLocationDataManager().getAllFields();
	}

	@Override
	public int addFieldLocation(String fieldName, Integer parentLocationId, Integer currentUserId) {
		return this.addLocation(fieldName, parentLocationId, currentUserId, LocationType.FIELD.getCode(),
				LocdesType.FIELD_PARENT.getCode());
	}

	@Override
	public int addBlockLocation(String blockName, Integer parentFieldId, Integer currentUserId) {
		return this.addLocation(blockName, parentFieldId, currentUserId, LocationType.BLOCK.getCode(), LocdesType.BLOCK_PARENT.getCode());
	}

	public int addLocation(String locationName, Integer parentId, Integer currentUserId, String locCode, String parentCode) {
		LocationDataManager manager = this.getLocationDataManager();

		Integer lType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, locCode);
		Location location = new Location(null, lType, 0, locationName, "-", 0, 0, 0, 0, 0);

		Integer dType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, parentCode);
		Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentId), 0, 0);

		return manager.addLocationAndLocdes(location, locdes);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId) {
		return this.getStudyDataManager().getAllFieldMapsInBlockByBlockId(blockId);
	}

	@Override
	public List<StandardVariable> getPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields) {
		List<StandardVariable> treatmentPairs = new ArrayList<StandardVariable>();
		treatmentPairs.addAll(this.getCvTermDao().getAllPossibleTreatmentPairs(cvTermId, propertyId, hiddenFields));

		List<Integer> termIds = new ArrayList<Integer>();
		Map<Integer, CVTerm> termMap = new HashMap<Integer, CVTerm>();

		for (StandardVariable pair : treatmentPairs) {
			termIds.add(pair.getProperty().getId());
			termIds.add(pair.getScale().getId());
			termIds.add(pair.getMethod().getId());
		}

		List<CVTerm> terms = new ArrayList<CVTerm>();
		terms.addAll(this.getCvTermDao().getByIds(termIds));

		for (CVTerm term : terms) {
			termMap.put(term.getCvTermId(), term);
		}

		for (StandardVariable pair : treatmentPairs) {
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
	public TermId getStudyType(int studyId) {
		String value = this.getProjectPropertyDao().getValueByProjectIdAndTypeId(studyId, TermId.STUDY_TYPE.getId());
		if (value != null && NumberUtils.isNumber(value)) {
			return TermId.getById(Integer.valueOf(value));
		}
		return null;
	}

	@Override
	public List<FolderReference> getRootFolders(String programUUID) {
		return this.getStudyDataManager().getRootFolders(programUUID);
	}

	@Override
	public List<Reference> getChildrenOfFolder(int folderId, String programUUID) {
		return this.getStudyDataManager().getChildrenOfFolder(folderId, programUUID);
	}

	@Override
	public boolean isStudy(int id) {
		return this.getStudyDataManager().isStudy(id);
	}

	@Override
	public Location getLocationById(int id) {
		return this.getLocationDataManager().getLocationByID(id);
	}

	@Override
	public Location getLocationByName(String locationName, Operation op) {
		List<Location> locations = this.getLocationDataManager().getLocationsByName(locationName, 0, 1, op);
		if (locations != null && !locations.isEmpty()) {
			return locations.get(0);
		}
		return null;
	}

	@Override
	public Person getPersonById(int id) {
		return this.getUserDataManager().getPersonById(id);
	}

	@Override
	public int getMeasurementDatasetId(int studyId, String studyName) {
		return this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
	}

	@Override
	public long countObservations(int datasetId) {
		return this.getExperimentBuilder().count(datasetId);
	}

	@Override
	public long countStocks(int datasetId) {
		return this.getStockBuilder().countStocks(datasetId);
	}

	@Override
	public boolean hasFieldMap(int datasetId) {
		return this.getExperimentBuilder().hasFieldmap(datasetId);
	}

	@Override
	public GermplasmList getGermplasmListById(Integer listId) {
		return this.getGermplasmListManager().getGermplasmListById(listId);
	}

	@Override
	public String getOwnerListName(Integer userId) {

		User user = this.getUserDataManager().getUserById(userId);
		if (user != null) {
			int personId = user.getPersonid();
			Person p = this.getUserDataManager().getPersonById(personId);

			if (p != null) {
				return p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName();
			} else {
				return user.getName();
			}
		} else {
			return "";
		}
	}

	@Override
	public StudyDetails getStudyDetails(StudyType studyType, int studyId) {
		return this.getStudyDataManager().getStudyDetails(studyType, studyId);
	}

	@Override
	public String getBlockId(int datasetId, String trialInstance) {
		return this.getGeolocationPropertyDao().getValueOfTrialInstance(datasetId, TermId.BLOCK_ID.getId(), trialInstance);
	}

	@Override
	public String getFolderNameById(Integer folderId) {
		return this.getStudyDataManager().getFolderNameById(folderId);
	}

	private List<Integer> getDeletedVariateIds(List<MeasurementVariable> variables) {
		List<Integer> ids = new ArrayList<Integer>();
		if (variables != null) {
			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.DELETE) {
					ids.add(variable.getTermId());
				}
			}
		}
		return ids;
	}

	@Override
	public boolean checkIfStudyHasFieldmap(int studyId) {
		return this.getExperimentBuilder().checkIfStudyHasFieldmap(studyId);
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) {
		return this.getStudyDataManager().checkIfStudyHasMeasurementData(datasetId, variateIds);
	}

	@Override
	public int countVariatesWithData(int datasetId, List<Integer> variateIds) {
		return this.getStudyDataManager().countVariatesWithData(datasetId, variateIds);
	}

	@Override
	public void deleteObservationsOfStudy(int datasetId) {
		try {
			this.getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
		} catch (Exception e) {

			this.logAndThrowException("Error encountered with deleteObservationsOfStudy(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList,
			List<MeasurementVariable> variateList) {
		return this.getWorkbookBuilder().buildTrialObservations(trialDatasetId, factorList, variateList);
	}

	@Override
	public List<Integer> getGermplasmIdsByName(String name) {
		return this.getNameDao().getGidsByName(name);
	}

	@Override
	public Integer addGermplasmName(String nameValue, int gid, int userId, int nameTypeId, int locationId, Integer date) {
		Name name = new Name(null, gid, nameTypeId, 0, userId, nameValue, locationId, date, 0);
		return this.getGermplasmDataManager().addGermplasmName(name);
	}

	@Override
	public Integer addGermplasm(String nameValue, int userId) {
		Name name = new Name(null, null, 1, 1, userId, nameValue, 0, 0, 0);
		Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, userId, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public Integer addGermplasm(Germplasm germplasm, Name name) {
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public Integer getProjectIdByNameAndProgramUUID(String name, String programUUID) {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID);
	}

	@Override
	public MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(String property, String scale, String method,
			PhenotypicType role, String programUUID) {
		MeasurementVariable variable = null;
		StandardVariable standardVariable = null;
		Integer id = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (id != null) {
			standardVariable = this.getStandardVariableBuilder().create(id, programUUID);
			standardVariable.setPhenotypicType(role);
			return this.getMeasurementVariableTransformer().transform(standardVariable, false);
		}
		return variable;
	}

	@Override
	public void setTreatmentFactorValues(List<TreatmentVariable> treatmentFactors, int measurementDatasetID) {
		this.getWorkbookBuilder().setTreatmentFactorValues(treatmentFactors, measurementDatasetID);
	}

	@Override
	public Workbook getCompleteDataset(int datasetId, boolean isTrial) {
		Workbook workbook = this.getDataSetBuilder().buildCompleteDataset(datasetId, isTrial);
		this.setOrderVariableByRank(workbook, datasetId);
		return workbook;
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes() {
		return this.getGermplasmListManager().getGermplasmNameTypes();
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGids(List<Integer> gids) {
		return this.getNameDao().getNamesByGidsInMap(gids);
	}

	@Override
	public int countGermplasmListDataByListId(Integer listId) {
		return (int) this.getGermplasmListManager().countGermplasmListDataByListId(listId);
	}

	@Override
	public int countListDataProjectGermplasmListDataByListId(Integer listId) {
		return (int) this.getGermplasmListManager().countListDataProjectGermplasmListDataByListId(listId);
	}

	@Override
	public Method getMethodById(int id) {
		return this.getGermplasmDataManager().getMethodByID(id);
	}

	@Override
	public Method getMethodByCode(String code, String programUUID) {
		return this.getGermplasmDataManager().getMethodByCode(code, programUUID);
	}

	@Override
	public Method getMethodByName(String name) {
		return this.getGermplasmDataManager().getMethodByName(name);
	}

	@Override
	public void deleteStudy(int studyId, Integer currentUserId) throws UnpermittedDeletionException {
		Integer studyUserId = this.getStudy(studyId).getUser();
		if (studyUserId != null && !studyUserId.equals(currentUserId)) {
			throw new UnpermittedDeletionException(
					"You are not able to delete this nursery or trial as you are not the owner. The owner is "
							+ this.getOwnerListName(studyUserId));
		}

		try {
			this.getStudyDestroyer().deleteStudy(studyId);

		} catch (Exception e) {
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<Integer> getFavoriteProjectLocationIds(String programUUID) {
		List<ProgramFavorite> favList =
				this.getGermplasmDataManager().getProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, Integer.MAX_VALUE, programUUID);
		List<Integer> favoriteList = new ArrayList<>();
		if (favList != null && !favList.isEmpty()) {
			for (ProgramFavorite fav : favList) {
				favoriteList.add(fav.getEntityId());

			}
		}
		return favoriteList;
	}

	@Override
	public List<Integer> getFavoriteProjectMethods(String programUUID) {
		List<ProgramFavorite> favList =
				this.getGermplasmDataManager().getProgramFavorites(ProgramFavorite.FavoriteType.METHOD, Integer.MAX_VALUE, programUUID);
		List<Integer> ids = new ArrayList<Integer>();
		if (favList != null && !favList.isEmpty()) {
			for (ProgramFavorite fav : favList) {
				ids.add(fav.getEntityId());
			}
		}
		return ids;
	}

	@Override
	public List<GermplasmList> getGermplasmListsByProjectId(int projectId, GermplasmListType type) {
		return this.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
	}

	@Override
	public List<ListDataProject> getListDataProject(int listId) {
		return this.getListDataProjectDAO().getByListId(listId);
	}

	@Override
	public ListDataProject getListDataProjectByStudy(int projectId, GermplasmListType type, int plotId) {
		return this.getListDataProjectDAO().getByStudy(projectId, type, plotId);
	}

	@Override
	public ListDataProject getListDataProjectByListIdAndEntryNo(int listId, int entryNo) {
		return this.getListDataProjectDAO().getByListIdAndEntryNo(listId, entryNo);
	}

	@Override
	public void deleteListDataProjects(int projectId, GermplasmListType type) {
		// when used in advanced, it will delete all the advance lists (list data projects)
		List<GermplasmList> lists = this.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		if (lists != null && !lists.isEmpty()) {
			for (GermplasmList list : lists) {
				this.getListDataProjectDAO().deleteByListIdWithList(list.getId());
			}
		}
	}

	@Override
	public int saveOrUpdateListDataProject(int projectId, GermplasmListType type, Integer originalListId, List<ListDataProject> listDatas,
			int userId) {

		int listId = 0;
		try {

			listId = this.getListDataProjectSaver().saveOrUpdateListDataProject(projectId, type, originalListId, listDatas, userId);

		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with saveOrUpdateListDataProject(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}
		return listId;
	}

	@Override
	public void updateGermlasmListInfoStudy(int crossesListId, int studyId) {

		try {

			this.getListDataProjectSaver().updateGermlasmListInfoStudy(crossesListId, studyId);

		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with updateGermlasmListInfoStudy(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}

	}

	@Override
	public void saveStudyColumnOrdering(Integer studyId, String studyName, List<Integer> orderedTermIds) {
		Integer plotDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
		this.getStudyDataManager().updateVariableOrdering(plotDatasetId, orderedTermIds);
	}

	@Override
	public boolean setOrderVariableByRank(Workbook workbook) {
		if (workbook != null) {
			Integer studyId = workbook.getStudyDetails().getId();
			String studyName = workbook.getStudyDetails().getStudyName();
			if (studyId != null) {
				Integer plotDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
				this.setOrderVariableByRank(workbook, plotDatasetId);
			}
			return true;
		}
		return false;
	}

	public boolean setOrderVariableByRank(Workbook workbook, Integer plotDatasetId) {
		if (workbook != null) {
			List<Integer> storedInIds = new ArrayList<Integer>();
			storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
			storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
			workbook.setColumnOrderedLists(
					this.getProjectPropertyDao().getDatasetVariableIdsForGivenStoredInIds(plotDatasetId, storedInIds, null));
			return true;
		}
		return false;
	}

	@Override
	public void addListDataProjectList(List<ListDataProject> listDataProjectList) {

		try {
			for (ListDataProject listDataProject : listDataProjectList) {
				listDataProject.setList(this.getGermplasmListById(listDataProject.getList().getId()));
				this.getListDataProjectDAO().save(listDataProject);
			}
		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with addListDataProjectList(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public StandardVariable getStandardVariableByName(String name, String programUUID) {
		return this.getStandardVariableBuilder().getByName(name, programUUID);
	}

}
