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
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
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
import org.generationcp.middleware.pojos.Attribute;
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
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FieldbookServiceImpl extends Service implements FieldbookService {

	@Resource
	private GermplasmGroupingService germplasmGroupingService;

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

	public FieldbookServiceImpl() {
		super();
	}

	public FieldbookServiceImpl(final HibernateSessionProvider sessionProvider, final String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfTrial(final List<Integer> trialIdList,
			final CrossExpansionProperties crossExpansionProperties, boolean pedigreeRequired) {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(trialIdList, StudyType.T, crossExpansionProperties, pedigreeRequired);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfNursery(final List<Integer> nurseryIdList,
			final CrossExpansionProperties crossExpansionProperties, boolean pedigreeRequired) {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(nurseryIdList, StudyType.N, crossExpansionProperties, pedigreeRequired);
	}

	@Override
	public List<Location> getAllLocations() {
		final Integer fieldLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		final Integer blockLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());

		final List<Location> locList = this.getLocationDataManager().getAllLocations();
		final List<Location> newLocation = new ArrayList<Location>();

		for (final Location loc : locList) {
			if (fieldLtypeFldId != null && fieldLtypeFldId.intValue() == loc.getLtype().intValue() || blockLtypeFldId != null
					&& blockLtypeFldId.intValue() == loc.getLtype().intValue()) {
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
		final Integer seedLType =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.getLocationDataManager().getLocationsByType(seedLType);
	}

	@Override
	public void saveOrUpdateFieldmapProperties(final List<FieldMapInfo> info, final int userId, final boolean isNew) {
		this.getStudyDataManager().saveOrUpdateFieldmapProperties(info, userId, isNew);
	}

	@Override
	public Study getStudy(final int studyId) {
		// not using the variable type
		return this.getStudyDataManager().getStudy(studyId, false);
	}

	@Override
	public List<Location> getFavoriteLocationByLocationIDs(final List<Integer> locationIds, final Boolean isBreedingLocation) {
		if (isBreedingLocation == null) {
			return this.getFavoriteLocationByLocationIDs(locationIds);
		}

		if (isBreedingLocation) {
			return this.getLocationDataManager().getAllBreedingLocations(locationIds);
		}

		return this.getLocationDataManager().getAllSeedingLocations(locationIds);

	}

	@Override
	public List<Location> getFavoriteLocationByLocationIDs(final List<Integer> locationIds) {
		return this.getLocationDataManager().getLocationsByIDs(locationIds);
	}
	
	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(final int datasetId, final int geolocationId,
			final CrossExpansionProperties crossExpansionProperties) {
		return this.getStudyDataManager().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, crossExpansionProperties);
	}

	@Override
	public List<DatasetReference> getDatasetReferences(final int studyId) {
		return this.getStudyDataManager().getDatasetReferences(studyId);
	}

	@Override
	public Integer getGermplasmIdByName(final String name) {

		final List<Germplasm> germplasmList = this.getGermplasmDataManager().getGermplasmByName(name, 0, 1, Operation.EQUAL);
		Integer gid = null;
		if (germplasmList != null && !germplasmList.isEmpty()) {
			gid = germplasmList.get(0).getGid();
		}
		return gid;
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethodRole(final String property, final String scale, final String method,
			final PhenotypicType role) {
		return this.getOntologyDataManager().getStandardVariableIdByPropertyScaleMethod(property, scale, method);
	}

	@Override
	public Workbook getNurseryDataSet(final int id) {
		final Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.N);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@Override
	public Workbook getTrialDataSet(final int id) {
		final Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.T);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveMeasurementRows(final Workbook workbook, final String programUUID) {

		final long startTime = System.currentTimeMillis();

		try {

			final List<MeasurementVariable> variates = workbook.getVariates();
			final List<MeasurementVariable> factors = workbook.getFactors();
			final List<MeasurementRow> observations = workbook.getObservations();

			this.getWorkbookSaver().saveWorkbookVariables(workbook);
			this.getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);

			final Map<String, ?> variableMap = this.getWorkbookSaver().saveVariables(workbook, programUUID);

			// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
			final Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
			final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");

			// unpack maps
			// Strings
			final List<String> trialHeaders = headerMap.get("trialHeaders");

			// VariableTypeLists
			final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");

			// save trial observations
			this.getWorkbookSaver().updateTrialObservations(workbook, programUUID);

			Integer measurementDatasetId = workbook.getMeasurementDatesetId();
			if (measurementDatasetId == null) {
				measurementDatasetId =
						this.getWorkbookBuilder().getMeasurementDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
			}

			// save factors
			// TODO: Possible improvement
			this.getWorkbookSaver().createStocksIfNecessary(measurementDatasetId, workbook, effectVariables, trialHeaders);

			if (factors != null) {
				for (final MeasurementVariable factor : factors) {
					if (NonEditableFactors.find(factor.getTermId()) == null) {
						for (final MeasurementRow row : observations) {
							for (final MeasurementData field : row.getDataList()) {
								if (factor.getName().equals(field.getLabel()) && factor.getRole() == PhenotypicType.TRIAL_DESIGN) {
									this.getExperimentPropertySaver().saveOrUpdateProperty(
											this.getExperimentDao().getById(row.getExperimentId()), factor.getTermId(), field.getValue());
								}
							}
						}
					}
				}
			}

			if (variates != null && !variates.isEmpty()) {
				final Measurements measurements =
						new Measurements(this.getActiveSession(), this.getPhenotypeSaver(), this.getPhenotypeOutlierSaver());
				measurements.saveMeasurements(observations);
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		} finally {
			this.getActiveSession().setFlushMode(FlushMode.AUTO);
		}

		FieldbookServiceImpl.LOG.debug("========== saveMeasurementRows Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

	}

	@Override
	public List<Method> getAllBreedingMethods(final boolean filterOutGenerative) {
		final List<Method> methodList =
				filterOutGenerative ? this.getGermplasmDataManager().getAllMethodsNotGenerative() : this.getGermplasmDataManager()
						.getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}

	@Override
	public List<Method> getAllGenerativeMethods(final String programUUID) {
		final List<Method> methodList = this.getGermplasmDataManager().getMethodsByType("GEN", programUUID);
		return methodList;
	}

	@Override
	public List<Method> getFavoriteBreedingMethods(final List<Integer> methodIds, final boolean filterOutGenerative) {
		final List<Method> methodList;
		if (filterOutGenerative) {
			methodList = this.getGermplasmDataManager().getNonGenerativeMethodsByID(methodIds);
		} else {
			methodList = this.getGermplasmDataManager().getMethodsByIDs(methodIds);
		}

		return methodList;
	}
	
	@Override
	public List<Method> getFavoriteMethods(final List<Integer> methodIds, final Boolean filterOutGenerative) {

		if (filterOutGenerative == null) {
			return this.getGermplasmDataManager().getMethodsByIDs(methodIds);
		}

		if (filterOutGenerative) {
			return this.getGermplasmDataManager().getNonGenerativeMethodsByID(methodIds);
		}

		return this.getGermplasmDataManager().getDerivativeAndMaintenanceMethods(methodIds);
	}

	@Override
	public Integer saveNurseryAdvanceGermplasmList(final List<Pair<Germplasm, List<Name>>> germplasms,
			final List<Pair<Germplasm, GermplasmListData>> listDataItems, final GermplasmList germplasmList,
			List<Pair<Germplasm, List<Attribute>>> germplasmAttributes) throws MiddlewareQueryException {

		final GermplasmDAO germplasmDao = this.getGermplasmDao();
		final GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		final long startTime = System.currentTimeMillis();

		try {
			germplasmListDao.save(germplasmList);
			int counter = 0;
			// Save germplasms, names, list data
			for (final Pair<Germplasm, List<Name>> pair : germplasms) {
				Germplasm germplasm = pair.getLeft();
				final GermplasmListData germplasmListData = listDataItems.get(counter).getRight();

				Germplasm germplasmFound = null;
				// Check if germplasm exists
				if (germplasm.getGid() != null) {
					// Check if the given gid exists
					germplasmFound = this.getGermplasmDataManager().getGermplasmByGID(germplasm.getGid());

					// Check if the given germplasm name exists
					if (germplasmFound == null) {
						final List<Germplasm> germplasmsFound =
								this.getGermplasmDataManager().getGermplasmByName(germplasm.getPreferredName().getNval(), 0, 1,
										Operation.EQUAL);

						if (!germplasmsFound.isEmpty()) {
							germplasmFound = germplasmsFound.get(0);
						}
					}
				}

				// This is where the new germplasm saves on advancing.
				// Save germplasm and name entries if non-existing
				if (germplasmFound == null || germplasmFound.getGid() == null) {
					final List<Name> nameList = germplasms.get(counter).getRight();
					// Lgid could not be null in the DB, so we are saving a value before saving it to the DB
					if (germplasm.getLgid() == null) {
						germplasm.setLgid(germplasm.getGid() != null ? germplasm.getGid() : Integer.valueOf(0));
					}

					germplasm = germplasmDao.save(germplasm);

					for (final Name name : nameList) {
						// Germplasm and Name entities are currently only mapped as uni-directional OneToMany so we need to manage the Name
						// side of the relationship link (Name.germplasmId) manually.
						name.setGermplasmId(germplasm.getGid());
						germplasm.getNames().add(name);
					}

					// inherit 'selection history at fixation' names of parent if parent is part of a group (= has mgid)
					if (germplasm.getMgid() > 0) {
						this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(germplasm);
					}

					// set Lgid to GID if it's value was not set previously
					if (germplasm.getLgid().equals(Integer.valueOf(0))) {
						germplasm.setLgid(germplasm.getGid());
					}

					// Save Germplasm attributes
					List<Attribute> attributesList = germplasmAttributes.get(counter).getRight();
					AttributeDAO attributeDAO = this.getAttributeDao();
					for (Attribute attribute : attributesList) {
						attribute.setGermplasmId(germplasm.getGid());
						attributeDAO.save(attribute);
					}
				}

				// Save germplasmListData
				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);
				counter++;
			}

		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms=" + germplasms
					+ ", germplasmList=" + germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveNurseryAdvanceGermplasmList Duration (ms): "
				+ (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	@Override
	public Integer saveGermplasmList(final List<Pair<Germplasm, GermplasmListData>> listDataItems, final GermplasmList germplasmList) {

		final GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		final long startTime = System.currentTimeMillis();

		try {

			germplasmListDao.save(germplasmList);

			// Save germplasms, names, list data
			for (final Pair<Germplasm, GermplasmListData> pair : listDataItems) {

				final Germplasm germplasm = pair.getLeft();
				final GermplasmListData germplasmListData = pair.getRight();

				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasmList="
					+ germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();

	}

	@Override
	public String getCimmytWheatGermplasmNameByGid(final int gid) {
		List<Name> names = this.getByGidAndNtype(gid, GermplasmNameType.CIMMYT_SELECTION_HISTORY);
		if (names == null || names.isEmpty()) {
			names = this.getByGidAndNtype(gid, GermplasmNameType.UNRESOLVED_NAME);
		}
		return names != null && !names.isEmpty() ? names.get(0).getNval() : null;
	}

	private List<Name> getByGidAndNtype(final int gid, final GermplasmNameType nType) {
		return this.getNameDao().getByGIDWithFilters(gid, null, nType);
	}

	@Override
	public GermplasmList getGermplasmListByName(final String name, final String programUUID) {
		final List<GermplasmList> germplasmLists =
				this.getGermplasmListManager().getGermplasmListByName(name, programUUID, 0, 1, Operation.EQUAL);
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
	public Germplasm getGermplasmByGID(final int gid) {
		return this.getGermplasmDataManager().getGermplasmByGID(gid);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(final int stdVarId) {
		return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(final String property, final String scale, final String method,
			final PhenotypicType role) {

		final Integer stdVarId = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (stdVarId != null) {
			return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
		}
		return new ArrayList<ValueReference>();
	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(final String programUUID) {
		return this.getOntologyDataManager().getAllStandardVariables(programUUID);
	}

	@Override
	public StandardVariable getStandardVariable(final int id, final String programUUID) {
		return this.getOntologyDataManager().getStandardVariable(id, programUUID);
	}

	@Override
	public List<ValueReference> getAllNurseryTypes(final String programUUID) {

		final List<ValueReference> nurseryTypes = new ArrayList<ValueReference>();

		final StandardVariable stdVar = this.getOntologyDataManager().getStandardVariable(TermId.NURSERY_TYPE.getId(), programUUID);
		final List<Enumeration> validValues = stdVar.getEnumerations();

		if (validValues != null) {
			for (final Enumeration value : validValues) {
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
	public int countPlotsWithRecordedVariatesInDataset(final int datasetId, final List<Integer> variateIds) {

		return this.getStudyDataManager().countPlotsWithRecordedVariatesInDataset(datasetId, variateIds);
	}

	@Override
	public List<StandardVariableReference> filterStandardVariablesByMode(final List<Integer> storedInIds, final List<Integer> propertyIds,
			final boolean isRemoveProperties) {
		final List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		final List<CVTerm> variables = new ArrayList<CVTerm>();
		final Set<Integer> variableIds = new HashSet<Integer>();

		this.addAllVariableIdsInMode(variableIds, storedInIds);
		if (propertyIds != null && !propertyIds.isEmpty()) {
			final Set<Integer> propertyVariableList = new HashSet<Integer>();
			this.createPropertyList(propertyVariableList, propertyIds);
			this.filterByProperty(variableIds, propertyVariableList, isRemoveProperties);
		}

		final List<Integer> variableIdList = new ArrayList<Integer>(variableIds);
		variables.addAll(this.getCvTermDao().getValidCvTermsByIds(variableIdList, TermId.CATEGORICAL_VARIATE.getId(),
				TermId.CATEGORICAL_VARIABLE.getId()));
		for (final CVTerm variable : variables) {
			list.add(new StandardVariableReference(variable.getCvTermId(), variable.getName(), variable.getDefinition()));
		}
		return list;
	}

	@Override
	public List<StandardVariableReference> filterStandardVariablesByIsAIds(final List<StandardVariableReference> standardReferences,
			final List<Integer> isAIds) {
		List<StandardVariableReference> newRefs = new ArrayList<StandardVariableReference>();
		try {
			final List<StandardVariableSummary> variableSummaries =
					this.getStandardVariableDao().getStandardVariableSummaryWithIsAId(isAIds);
			for (final StandardVariableReference ref : standardReferences) {
				boolean isFound = false;
				for (final StandardVariableSummary summary : variableSummaries) {
					if (ref.getId().intValue() == summary.getId().intValue()) {
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					newRefs.add(ref);
				}
			}
		} catch (final MiddlewareQueryException e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			newRefs = standardReferences;
		}
		return newRefs;
	}

	private void addAllVariableIdsInMode(final Set<Integer> variableIds, final List<Integer> storedInIds) {
		for (final Integer storedInId : storedInIds) {
			variableIds.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.STORED_IN.getId(), storedInId));
		}
	}

	private void createPropertyList(final Set<Integer> propertyVariableList, final List<Integer> propertyIds) {
		for (final Integer propertyId : propertyIds) {
			propertyVariableList.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(),
					propertyId));
		}
	}

	private void filterByProperty(final Set<Integer> variableIds, final Set<Integer> variableListByProperty,
			final boolean isRemoveProperties) {
		// delete variables not in the list of filtered variables by property
		final Iterator<Integer> iter = variableIds.iterator();
		boolean inList = false;

		if (isRemoveProperties) {
			// remove variables having the specified properties from the list
			while (iter.hasNext()) {
				final Integer id = iter.next();
				for (final Integer variable : variableListByProperty) {
					if (id.equals(variable)) {
						iter.remove();
					}
				}
			}
		} else {
			// remove variables not in the property list
			while (iter.hasNext()) {
				inList = false;
				final Integer id = iter.next();
				for (final Integer variable : variableListByProperty) {
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
	public Workbook getStudyVariableSettings(final int id, final boolean isNursery) {
		return this.getWorkbookBuilder().createStudyVariableSettings(id, isNursery);
	}

	@Override
	public List<Germplasm> getGermplasms(final List<Integer> gids) {
		return this.getGermplasmDataManager().getGermplasms(gids);
	}

	@Override
	public List<Location> getAllFieldLocations(final int locationId) {
		return this.getLocationDataManager().getAllFieldLocations(locationId);
	}

	@Override
	public List<Location> getAllBlockLocations(final int fieldId) {
		return this.getLocationDataManager().getAllBlockLocations(fieldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(final int blockId) {
		return this.getLocationDataManager().getBlockInformation(blockId);
	}

	@Override
	public List<Location> getAllFields() {
		return this.getLocationDataManager().getAllFields();
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
		final LocationDataManager manager = this.getLocationDataManager();

		final Integer lType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, locCode);
		final Location location = new Location(null, lType, 0, locationName, "-", 0, 0, 0, 0, 0);

		final Integer dType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, parentCode);
		final Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentId), 0, 0);

		return manager.addLocationAndLocdes(location, locdes);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(final int blockId) {
		return this.getStudyDataManager().getAllFieldMapsInBlockByBlockId(blockId);
	}

	@Override
	public List<StandardVariable> getPossibleTreatmentPairs(final int cvTermId, final int propertyId, final List<Integer> hiddenFields) {
		final List<StandardVariable> treatmentPairs = new ArrayList<StandardVariable>();
		treatmentPairs.addAll(this.getCvTermDao().getAllPossibleTreatmentPairs(cvTermId, propertyId, hiddenFields));

		final List<Integer> termIds = new ArrayList<Integer>();
		final Map<Integer, CVTerm> termMap = new HashMap<Integer, CVTerm>();

		for (final StandardVariable pair : treatmentPairs) {
			termIds.add(pair.getProperty().getId());
			termIds.add(pair.getScale().getId());
			termIds.add(pair.getMethod().getId());
		}

		final List<CVTerm> terms = new ArrayList<CVTerm>();
		terms.addAll(this.getCvTermDao().getByIds(termIds));

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
		return this.getLocationDataManager().getLocationByID(id);
	}

	@Override
	public Location getLocationByName(final String locationName, final Operation op) {
		final List<Location> locations = this.getLocationDataManager().getLocationsByName(locationName, 0, 1, op);
		if (locations != null && !locations.isEmpty()) {
			return locations.get(0);
		}
		return null;
	}

	@Override
	public Integer updateGermplasmList(List<Pair<Germplasm, GermplasmListData>> listDataItems, GermplasmList germplasmList) {
		final GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		final long startTime = System.currentTimeMillis();

		try {

			germplasmListDao.update(germplasmList);

			// Save germplasms, names, list data
			for (final Pair<Germplasm, GermplasmListData> pair : listDataItems) {

				final Germplasm germplasm = pair.getLeft();
				final GermplasmListData germplasmListData = pair.getRight();

				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().update(germplasmListData);
			}

		} catch (final MiddlewareQueryException e) {
			FieldbookServiceImpl.LOG.error("Error encountered with FieldbookService.updateNurseryCrossesGermplasmList(germplasmList="
					+ germplasmList + "): " + e.getMessage());
			throw e;
		}

		FieldbookServiceImpl.LOG.debug("========== updateGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return germplasmList.getId();
	}

	@Override
	public Person getPersonById(final int id) {
		return this.getUserDataManager().getPersonById(id);
	}

	@Override
	public int getMeasurementDatasetId(final int studyId, final String studyName) {
		return this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
	}

	@Override
	public long countObservations(final int datasetId) {
		return this.getExperimentBuilder().count(datasetId);
	}

	@Override
	public long countStocks(final int datasetId) {
		return this.getStockBuilder().countStocks(datasetId);
	}

	@Override
	public boolean hasFieldMap(final int datasetId) {
		return this.getExperimentBuilder().hasFieldmap(datasetId);
	}

	@Override
	public GermplasmList getGermplasmListById(final Integer listId) {
		return this.getGermplasmListManager().getGermplasmListById(listId);
	}

	@Override
	public String getOwnerListName(final Integer userId) {

		final User user = this.getUserDataManager().getUserById(userId);
		if (user != null) {
			final int personId = user.getPersonid();
			final Person p = this.getUserDataManager().getPersonById(personId);

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
	public StudyDetails getStudyDetails(final StudyType studyType, final int studyId) {
		return this.getStudyDataManager().getStudyDetails(studyType, studyId);
	}

	@Override
	public String getBlockId(final int datasetId, final String trialInstance) {
		return this.getGeolocationPropertyDao().getValueOfTrialInstance(datasetId, TermId.BLOCK_ID.getId(), trialInstance);
	}

	@Override
	public String getFolderNameById(final Integer folderId) {
		return this.getStudyDataManager().getFolderNameById(folderId);
	}

	@Override
	public boolean checkIfStudyHasFieldmap(final int studyId) {
		return this.getExperimentBuilder().checkIfStudyHasFieldmap(studyId);
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(final int datasetId, final List<Integer> variateIds) {
		return this.getStudyDataManager().checkIfStudyHasMeasurementData(datasetId, variateIds);
	}

	@Override
	public int countVariatesWithData(final int datasetId, final List<Integer> variateIds) {
		return this.getStudyDataManager().countVariatesWithData(datasetId, variateIds);
	}

	@Override
	public void deleteObservationsOfStudy(final int datasetId) {
		try {
			this.getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
		} catch (final Exception e) {

			this.logAndThrowException("Error encountered with deleteObservationsOfStudy(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<MeasurementRow> buildTrialObservations(final int trialDatasetId, final List<MeasurementVariable> factorList,
			final List<MeasurementVariable> variateList) {
		return this.getWorkbookBuilder().buildTrialObservations(trialDatasetId, factorList, variateList);
	}

	@Override
	public List<Integer> getGermplasmIdsByName(final String name) {
		return this.getNameDao().getGidsByName(name);
	}

	@Override
	public Integer addGermplasmName(final String nameValue, final int gid, final int userId, final int nameTypeId, final int locationId,
			final Integer date) {
		final Name name = new Name(null, gid, nameTypeId, 0, userId, nameValue, locationId, date, 0);
		return this.getGermplasmDataManager().addGermplasmName(name);
	}

	@Override
	public List<Integer> addGermplasmNames(final List<Name> names) {
		return this.getGermplasmDataManager().addGermplasmName(names);
	}

	@Override
	public Integer addGermplasm(final String nameValue, final int userId) {
		final Name name = new Name(null, null, 1, 1, userId, nameValue, 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, userId, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public Integer addGermplasm(final Germplasm germplasm, final Name name) {
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public List<Integer> addGermplasm(final List<Pair<Germplasm, Name>> germplasmPairs) {
		return this.getGermplasmDataManager().addGermplasm(germplasmPairs);
	}

	@Override
	public Integer getProjectIdByNameAndProgramUUID(final String name, final String programUUID) {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID);
	}

	@Override
	public MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(final String property, final String scale,
			final String method, final PhenotypicType role, final String programUUID) {
		final MeasurementVariable variable = null;
		StandardVariable standardVariable = null;
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
		this.getWorkbookBuilder().setTreatmentFactorValues(treatmentFactors, measurementDatasetID);
	}

	@Override
	public Workbook getCompleteDataset(final int datasetId, final boolean isTrial) {
		final Workbook workbook = this.getDataSetBuilder().buildCompleteDataset(datasetId, isTrial);
		this.setOrderVariableByRank(workbook, datasetId);
		return workbook;
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes() {
		return this.getGermplasmListManager().getGermplasmNameTypes();
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGids(final List<Integer> gids) {
		return this.getNameDao().getNamesByGidsInMap(gids);
	}

	@Override
	public int countGermplasmListDataByListId(final Integer listId) {
		return (int) this.getGermplasmListManager().countGermplasmListDataByListId(listId);
	}

	@Override
	public int countListDataProjectGermplasmListDataByListId(final Integer listId) {
		return (int) this.getGermplasmListManager().countListDataProjectGermplasmListDataByListId(listId);
	}

	@Override
	public Method getMethodById(final int id) {
		return this.getGermplasmDataManager().getMethodByID(id);
	}

	@Override
	public Method getMethodByCode(final String code, final String programUUID) {
		return this.getGermplasmDataManager().getMethodByCode(code, programUUID);
	}

	@Override
	public Method getMethodByName(final String name) {
		return this.getGermplasmDataManager().getMethodByName(name);
	}

	@Override
	public void deleteStudy(final int studyId, final Integer currentUserId) throws UnpermittedDeletionException {
		final Integer studyUserId = this.getStudy(studyId).getUser();
		if (studyUserId != null && !studyUserId.equals(currentUserId)) {
			throw new UnpermittedDeletionException(
					"You are not able to delete this nursery or trial as you are not the owner. The owner is "
							+ this.getOwnerListName(studyUserId));
		}

		try {
			this.getStudyDestroyer().deleteStudy(studyId);

		} catch (final Exception e) {
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<Integer> getFavoriteProjectLocationIds(final String programUUID) {
		final List<ProgramFavorite> favList =
				this.getGermplasmDataManager().getProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, Integer.MAX_VALUE, programUUID);
		final List<Integer> favoriteList = new ArrayList<>();
		if (favList != null && !favList.isEmpty()) {
			for (final ProgramFavorite fav : favList) {
				favoriteList.add(fav.getEntityId());

			}
		}
		return favoriteList;
	}

	@Override
	public List<Integer> getFavoriteProjectMethods(final String programUUID) {
		final List<ProgramFavorite> favList =
				this.getGermplasmDataManager().getProgramFavorites(ProgramFavorite.FavoriteType.METHOD, Integer.MAX_VALUE, programUUID);
		final List<Integer> ids = new ArrayList<Integer>();
		if (favList != null && !favList.isEmpty()) {
			for (final ProgramFavorite fav : favList) {
				ids.add(fav.getEntityId());
			}
		}
		return ids;
	}

	@Override
	public List<GermplasmList> getGermplasmListsByProjectId(final int projectId, final GermplasmListType type) {
		return this.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
	}

	@Override
	public List<ListDataProject> getListDataProject(final int listId) {
		return this.getListDataProjectDAO().getByListId(listId);
	}

	@Override
	public ListDataProject getListDataProjectByStudy(final int projectId, final GermplasmListType type, final int plotId) {
		return this.getListDataProjectDAO().getByStudy(projectId, type, plotId);
	}

	@Override
	public ListDataProject getListDataProjectByListIdAndEntryNo(final int listId, final int entryNo) {
		return this.getListDataProjectDAO().getByListIdAndEntryNo(listId, entryNo);
	}

	@Override
	public void deleteListDataProjects(final int projectId, final GermplasmListType type) {
		// when used in advanced, it will delete all the advance lists (list data projects)
		final List<GermplasmList> lists = this.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		if (lists != null && !lists.isEmpty()) {
			for (final GermplasmList list : lists) {
				this.getListDataProjectDAO().deleteByListIdWithList(list.getId());
			}
		}
	}

	@Override
	public int saveOrUpdateListDataProject(final int projectId, final GermplasmListType type, final Integer originalListId,
			final List<ListDataProject> listDatas, final int userId) {

		int listId = 0;
		try {

			listId = this.getListDataProjectSaver().saveOrUpdateListDataProject(projectId, type, originalListId, listDatas, userId);

		} catch (final Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with saveOrUpdateListDataProject(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}
		return listId;
	}

	@Override
	public void updateGermlasmListInfoStudy(final int crossesListId, final int studyId) {

		try {

			this.getListDataProjectSaver().updateGermlasmListInfoStudy(crossesListId, studyId);

		} catch (final Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with updateGermlasmListInfoStudy(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}

	}

	@Override
	public void saveStudyColumnOrdering(final Integer studyId, final String studyName, final List<Integer> orderedTermIds) {
		final Integer plotDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
		this.getStudyDataManager().updateVariableOrdering(plotDatasetId, orderedTermIds);
	}

	@Override
	public boolean setOrderVariableByRank(final Workbook workbook) {
		if (workbook != null) {
			final Integer studyId = workbook.getStudyDetails().getId();
			final String studyName = workbook.getStudyDetails().getStudyName();
			if (studyId != null) {
				final Integer plotDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
				this.setOrderVariableByRank(workbook, plotDatasetId);
			}
			return true;
		}
		return false;
	}

	public boolean setOrderVariableByRank(final Workbook workbook, final Integer plotDatasetId) {
		if (workbook != null) {
			final List<Integer> storedInIds = new ArrayList<Integer>();
			storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
			storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());
			storedInIds.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
			workbook.setColumnOrderedLists(this.getProjectPropertyDao().getDatasetVariableIdsForGivenStoredInIds(plotDatasetId,
					storedInIds, null));
			return true;
		}
		return false;
	}

	@Override
	public void addListDataProjectList(final List<ListDataProject> listDataProjectList) {

		try {
			for (final ListDataProject listDataProject : listDataProjectList) {
				listDataProject.setList(this.getGermplasmListById(listDataProject.getList().getId()));
				this.getListDataProjectDAO().save(listDataProject);
			}
		} catch (final Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.logAndThrowException("Error encountered with addListDataProjectList(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public StandardVariable getStandardVariableByName(final String name, final String programUUID) {
		return this.getStandardVariableBuilder().getByName(name, programUUID);
	}

	GermplasmGroupingService getGermplasmGroupingService() {
		return this.germplasmGroupingService;
	}

	void setGermplasmGroupingService(GermplasmGroupingService germplasmGroupingService) {
		this.germplasmGroupingService = germplasmGroupingService;
	}

}
