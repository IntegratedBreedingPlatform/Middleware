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
import java.util.Collections;
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
import org.generationcp.middleware.util.DatabaseBroker;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldbookServiceImpl extends Service implements FieldbookService {

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

	public FieldbookServiceImpl() {
		super();
	}

	public FieldbookServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public List<StudyDetails> getAllLocalNurseryDetails(String programUUID) throws MiddlewareQueryException {
		List<StudyDetails> studyDetailList = this.getStudyDataManager().getAllStudyDetails(StudyType.N, programUUID);
		return FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailList);
	}

	@Override
	public List<StudyDetails> getAllLocalTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
		List<StudyDetails> studyDetailList = this.getStudyDataManager().getAllStudyDetails(StudyType.T, programUUID);
		return FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailList);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList, CrossExpansionProperties crossExpansionProperties)
			throws MiddlewareQueryException {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(trialIdList, StudyType.T, crossExpansionProperties);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList, CrossExpansionProperties crossExpansionProperties)
			throws MiddlewareQueryException {
		return this.getStudyDataManager().getFieldMapInfoOfStudy(nurseryIdList, StudyType.N, crossExpansionProperties);
	}

	@Override
	public List<Location> getAllLocations() throws MiddlewareQueryException {
		Integer fieldLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.FIELD.getCode());
		Integer blockLtypeFldId =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());

		List<Location> locList = this.getLocationDataManager().getAllLocations();
		List<Location> newLocation = new ArrayList<Location>();

		for (Location loc : locList) {
			if (fieldLtypeFldId != null && fieldLtypeFldId.intValue() == loc.getLtype().intValue() || blockLtypeFldId != null
					&& blockLtypeFldId.intValue() == loc.getLtype().intValue()) {
				continue;
			}
			newLocation.add(loc);
		}

		return newLocation;
	}

	@Override
	public List<Location> getAllBreedingLocations() throws MiddlewareQueryException {
		return this.getLocationDataManager().getAllBreedingLocations();
	}

	@Override
	public List<Location> getAllSeedLocations() throws MiddlewareQueryException {
		Integer seedLType =
				this.getLocationDataManager().getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.getCode());
		return this.getLocationDataManager().getLocationsByType(seedLType);
	}

	@Override
	public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) throws MiddlewareQueryException {
		this.getStudyDataManager().saveOrUpdateFieldmapProperties(info, userId, isNew);
	}

	@Override
	public Study getStudy(int studyId) throws MiddlewareQueryException {
		// not using the variable type
		return this.getStudyDataManager().getStudy(studyId, false);
	}

	@Override
	public List<Location> getFavoriteLocationByProjectId(List<Long> locationIds) throws MiddlewareQueryException {
		List<Location> locationList = new ArrayList<Location>();

		for (int i = 0; i < locationIds.size(); i++) {
			Integer locationId = Integer.valueOf(locationIds.get(i).toString());
			Location location = this.getLocationDataManager().getLocationByID(locationId);

			locationList.add(location);
		}
		return locationList;
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId,
			CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException {
		return this.getStudyDataManager().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, crossExpansionProperties);
	}

	@Override
	public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
		return this.getStudyDataManager().getDatasetReferences(studyId);
	}

	@Override
	public Integer getGermplasmIdByName(String name) throws MiddlewareQueryException {

		List<Germplasm> germplasmList = this.getGermplasmDataManager().getGermplasmByName(name, 0, 1, Operation.EQUAL);
		Integer gid = null;
		if (germplasmList != null && !germplasmList.isEmpty()) {
			gid = germplasmList.get(0).getGid();
		}
		return gid;
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role)
			throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
	}

	@Override
	public Workbook getNurseryDataSet(int id) throws MiddlewareQueryException {
		Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.N);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@Override
	public Workbook getTrialDataSet(int id) throws MiddlewareQueryException {
		Workbook workbook = this.getWorkbookBuilder().create(id, StudyType.T);
		this.setOrderVariableByRank(workbook);
		return workbook;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		long startTime = System.currentTimeMillis();

		try {
			trans = session.beginTransaction();

			List<Integer> deletedVariateIds = this.getDeletedVariateIds(workbook.getVariates());

			List<MeasurementVariable> variates = workbook.getVariates();
			List<MeasurementVariable> factors = workbook.getFactors();
			List<MeasurementRow> observations = workbook.getObservations();

			int i = 0;
			this.getWorkbookSaver().saveWorkbookVariables(workbook);
			this.getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);

			final Map<String, ?> variableMap = this.getWorkbookSaver().saveVariables(workbook);

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
			this.getWorkbookSaver().saveTrialObservations(workbook);

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
								if (factor.getName().equals(field.getLabel())
										&& factor.getStoredIn() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
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
									Phenotype phenotype =
											this.getPhenotypeDao().getPhenotypeByProjectExperimentAndType(measurementDatasetId,
													row.getExperimentId(), variate.getTermId());
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
												variate.getStoredIn(),
												field.getcValueId() != null && !"".equals(field.getcValueId()) ? field.getcValueId()
														: field.getValue(), phenotype, field.isCustomCategoricalValue());

										i++;
										if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
											// flush a batch of inserts and release memory
											session.flush();
											session.clear();
										}
									}
								}
							}
						}
					}
				}
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}

		FieldbookServiceImpl.LOG.debug("========== saveMeasurementRows Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

	}

	@Override
	public List<Method> getAllBreedingMethods(boolean filterOutGenerative) throws MiddlewareQueryException {
		List<Method> methodList =
				filterOutGenerative ? this.getGermplasmDataManager().getAllMethodsNotGenerative() : this.getGermplasmDataManager()
						.getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}

	@Override
	public List<Method> getFavoriteBreedingMethods(List<Integer> methodIds, boolean filterOutGenerative) throws MiddlewareQueryException {
		List<Method> methodList = new ArrayList<Method>();
		List<Integer> validMethodClasses = new ArrayList<Integer>();
		validMethodClasses.addAll(Method.BULKED_CLASSES);
		validMethodClasses.addAll(Method.NON_BULKED_CLASSES);
		for (int i = 0; i < methodIds.size(); i++) {
			Integer methodId = methodIds.get(i);
			Method method = this.getGermplasmDataManager().getMethodByID(methodId);
			// filter out generative method types

			if (method == null) {
				continue;
			}
			if (!filterOutGenerative) {
				methodList.add(method);
			} else if ((method.getMtype() == null || !"GEN".equals(method.getMtype())) && method.getGeneq() != null
					&& validMethodClasses.contains(method.getGeneq())) {
				methodList.add(method);
			}
		}

		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}

	@Override
	public Integer saveNurseryAdvanceGermplasmList(Map<Germplasm, List<Name>> germplasms, Map<Germplasm, GermplasmListData> listDataItems,
			GermplasmList germplasmList) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		Integer listId = null;

		GermplasmDAO germplasmDao = this.getGermplasmDao();
		NameDAO nameDao = this.getNameDao();
		GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		long startTime = System.currentTimeMillis();

		try {
			trans = session.beginTransaction();

			germplasmListDao.save(germplasmList);

			int i = 0;

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
						List<Germplasm> germplasmsFound =
								this.getGermplasmDataManager().getGermplasmByName(germplasm.getPreferredName().getNval(), 0, 1,
										Operation.EQUAL);

						if (!germplasmsFound.isEmpty()) {
							germplasmFound = germplasmsFound.get(0);
						}
					}
				}

				// Save germplasm and name entries if non-existing
				if (germplasmFound == null || germplasmFound.getGid() == null) {
					germplasm = germplasmDao.save(germplasm);
					germplasm.setLgid(germplasm.getGid());

					// Save name entries
					for (Name name : germplasms.get(germplasm)) {
						name.setGermplasmId(germplasm.getGid());
						nameDao.save(name);
					}
				}

				// Save germplasmListData
				Integer germplasmListDataId = this.getGermplasmListDataDAO().getNextId("id");
				germplasmListData.setId(germplasmListDataId);
				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);

				i++;
				if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					// flush a batch of inserts and release memory
					session.flush();
					session.clear();
				}

			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasms=" + germplasms
					+ ", germplasmList=" + germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		} finally {
			session.flush();
		}

		FieldbookServiceImpl.LOG.debug("========== saveNurseryAdvanceGermplasmList Duration (ms): "
				+ (System.currentTimeMillis() - startTime) / 60);

		return listId;

	}

	@Override
	public Integer saveGermplasmList(Map<Germplasm, GermplasmListData> listDataItems, GermplasmList germplasmList)
			throws MiddlewareQueryException {

		Session session = this.getCurrentSession();
		Transaction trans = null;

		Integer listId = null;

		GermplasmListDAO germplasmListDao = this.getGermplasmListDAO();

		long startTime = System.currentTimeMillis();

		try {
			trans = session.beginTransaction();

			germplasmListDao.save(germplasmList);

			int i = 0;

			// Save germplasms, names, list data
			for (Entry<Germplasm, GermplasmListData> entry : listDataItems.entrySet()) {

				// Save germplasmListData
				Integer germplasmListDataId = this.getGermplasmListDataDAO().getNextId("id");

				Germplasm germplasm = entry.getKey();
				GermplasmListData germplasmListData = entry.getValue();

				germplasmListData.setId(germplasmListDataId);
				germplasmListData.setGid(germplasm.getGid());
				germplasmListData.setList(germplasmList);
				this.getGermplasmListDataDAO().save(germplasmListData);

				i++;
				// flush a batch of inserts and release memory
				if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					session.flush();
					session.clear();
				}

			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with FieldbookService.saveNurseryAdvanceGermplasmList(germplasmList="
					+ germplasmList + "): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		} finally {
			session.flush();
		}

		FieldbookServiceImpl.LOG.debug("========== saveGermplasmList Duration (ms): " + (System.currentTimeMillis() - startTime) / 60);

		return listId;

	}

	@Override
	public String getCimmytWheatGermplasmNameByGid(int gid) throws MiddlewareQueryException {
		List<Name> names = this.getByGidAndNtype(gid, GermplasmNameType.CIMMYT_SELECTION_HISTORY);
		if (names == null || names.isEmpty()) {
			names = this.getByGidAndNtype(gid, GermplasmNameType.UNRESOLVED_NAME);
		}
		return names != null && !names.isEmpty() ? names.get(0).getNval() : null;
	}

	private List<Name> getByGidAndNtype(int gid, GermplasmNameType nType) throws MiddlewareQueryException {
		return this.getNameDao().getByGIDWithFilters(gid, null, nType);
	}

	@Override
	public GermplasmList getGermplasmListByName(String name) throws MiddlewareQueryException {
		List<GermplasmList> germplasmLists = this.getGermplasmListManager().getGermplasmListByName(name, 0, 1, Operation.EQUAL);
		if (!germplasmLists.isEmpty()) {
			return germplasmLists.get(0);
		}
		return null;
	}

	@Override
	public Method getBreedingMethodById(int mid) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getMethodByID(mid);
	}

	@Override
	public Germplasm getGermplasmByGID(int gid) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getGermplasmByGID(gid);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException {
		return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
	}

	@Override
	public List<ValueReference> getDistinctStandardVariableValues(String property, String scale, String method, PhenotypicType role)
			throws MiddlewareQueryException {

		Integer stdVarId = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (stdVarId != null) {
			return this.getValueReferenceBuilder().getDistinctStandardVariableValues(stdVarId);
		}
		return new ArrayList<ValueReference>();
	}

	@Override
	public Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException {
		return this.getOntologyDataManager().getAllStandardVariables();
	}

	@Override
	public StandardVariable getStandardVariable(int id) throws MiddlewareQueryException {
		return this.getOntologyDataManager().getStandardVariable(id);
	}

	@Override
	public List<ValueReference> getAllNurseryTypes() throws MiddlewareQueryException {

		List<ValueReference> nurseryTypes = new ArrayList<ValueReference>();

		StandardVariable stdVar = this.getOntologyDataManager().getStandardVariable(TermId.NURSERY_TYPE.getId());
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
	public List<Person> getAllPersons() throws MiddlewareQueryException {
		return this.getUserDataManager().getAllPersons();
	}

	@Override
	public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
		return this.getUserDataManager().getAllPersonsOrderedByLocalCentral();
	}

	@Override
	public int countPlotsWithRecordedVariatesInDataset(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {

		return this.getStudyDataManager().countPlotsWithRecordedVariatesInDataset(datasetId, variateIds);
	}

	@Override
	public List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds, List<Integer> propertyIds,
			boolean isRemoveProperties) throws MiddlewareQueryException {
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

	private void addAllVariableIdsInMode(Set<Integer> variableIds, List<Integer> storedInIds) throws MiddlewareQueryException {
		for (Integer storedInId : storedInIds) {
			variableIds.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.STORED_IN.getId(), storedInId));
		}
	}

	private void createPropertyList(Set<Integer> propertyVariableList, List<Integer> propertyIds) throws MiddlewareQueryException {
		for (Integer propertyId : propertyIds) {
			propertyVariableList.addAll(this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(),
					propertyId));
		}
	}

	private void filterByProperty(Set<Integer> variableIds, Set<Integer> variableListByProperty, boolean isRemoveProperties)
			throws MiddlewareQueryException {
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
	public Workbook getStudyVariableSettings(int id, boolean isNursery) throws MiddlewareQueryException {
		return this.getWorkbookBuilder().createStudyVariableSettings(id, isNursery);
	}

	@Override
	public List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getGermplasms(gids);
	}

	@Override
	public List<Location> getAllFieldLocations(int locationId) throws MiddlewareQueryException {
		return this.getLocationDataManager().getAllFieldLocations(locationId);
	}

	@Override
	public List<Location> getAllBlockLocations(int fieldId) throws MiddlewareQueryException {
		return this.getLocationDataManager().getAllBlockLocations(fieldId);
	}

	@Override
	public FieldmapBlockInfo getBlockInformation(int blockId) throws MiddlewareQueryException {
		return this.getLocationDataManager().getBlockInformation(blockId);
	}

	@Override
	public List<Location> getAllFields() throws MiddlewareQueryException {
		return this.getLocationDataManager().getAllFields();
	}

	@Override
	public int addFieldLocation(String fieldName, Integer parentLocationId, Integer currentUserId) throws MiddlewareQueryException {
		return this
				.addLocation(fieldName, parentLocationId, currentUserId, LocationType.FIELD.getCode(), LocdesType.FIELD_PARENT.getCode());
	}

	@Override
	public int addBlockLocation(String blockName, Integer parentFieldId, Integer currentUserId) throws MiddlewareQueryException {
		return this.addLocation(blockName, parentFieldId, currentUserId, LocationType.BLOCK.getCode(), LocdesType.BLOCK_PARENT.getCode());
	}

	public int addLocation(String locationName, Integer parentId, Integer currentUserId, String locCode, String parentCode)
			throws MiddlewareQueryException {
		LocationDataManager manager = this.getLocationDataManager();

		Integer lType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, locCode);
		Location location = new Location(null, lType, 0, locationName, "-", 0, 0, 0, 0, 0);

		Integer dType = manager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, parentCode);
		Locdes locdes = new Locdes(null, null, dType, currentUserId, String.valueOf(parentId), 0, 0);

		return manager.addLocationAndLocdes(location, locdes);
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId) throws MiddlewareQueryException {
		return this.getStudyDataManager().getAllFieldMapsInBlockByBlockId(blockId);
	}

	@Override
	public List<StandardVariableReference> getAllTreatmentLevels(List<Integer> hiddenFields) throws MiddlewareQueryException {
		List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		list.addAll(this.getCvTermDao().getAllTreatmentFactors(hiddenFields, true));
		list.addAll(this.getCvTermDao().getAllTreatmentFactors(hiddenFields, false));
		Collections.sort(list);
		return list;
	}

	@Override
	public List<StandardVariable> getPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields)
			throws MiddlewareQueryException {
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
	public TermId getStudyType(int studyId) throws MiddlewareQueryException {
		String value = this.getProjectPropertyDao().getValueByProjectIdAndTypeId(studyId, TermId.STUDY_TYPE.getId());
		if (value != null && NumberUtils.isNumber(value)) {
			return TermId.getById(Integer.valueOf(value));
		}
		return null;
	}

	@Override
	public List<FolderReference> getRootFolders(String programUUID) throws MiddlewareQueryException {
		return this.getStudyDataManager().getRootFolders(programUUID);
	}

	@Override
	public List<Reference> getChildrenOfFolder(int folderId, String programUUID) throws MiddlewareQueryException {
		return this.getStudyDataManager().getChildrenOfFolder(folderId, programUUID);
	}

	@Override
	public boolean isStudy(int id) throws MiddlewareQueryException {
		return this.getStudyDataManager().isStudy(id);
	}

	@Override
	public Location getLocationById(int id) throws MiddlewareQueryException {
		return this.getLocationDataManager().getLocationByID(id);
	}

	@Override
	public Location getLocationByName(String locationName, Operation op) throws MiddlewareQueryException {
		List<Location> locations = this.getLocationDataManager().getLocationsByName(locationName, 0, 1, op);
		if (locations != null && !locations.isEmpty()) {
			return locations.get(0);
		}
		return null;
	}

	@Override
	public Person getPersonById(int id) throws MiddlewareQueryException {
		return this.getUserDataManager().getPersonById(id);
	}

	@Override
	public int getMeasurementDatasetId(int studyId, String studyName) throws MiddlewareQueryException {
		return this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
	}

	@Override
	public long countObservations(int datasetId) throws MiddlewareQueryException {
		return this.getExperimentBuilder().count(datasetId);
	}

	@Override
	public long countStocks(int datasetId) throws MiddlewareQueryException {
		return this.getStockBuilder().countStocks(datasetId);
	}

	@Override
	public boolean hasFieldMap(int datasetId) throws MiddlewareQueryException {
		return this.getExperimentBuilder().hasFieldmap(datasetId);
	}

	@Override
	public GermplasmList getGermplasmListById(Integer listId) throws MiddlewareQueryException {
		return this.getGermplasmListManager().getGermplasmListById(listId);
	}

	@Override
	public String getOwnerListName(Integer userId) throws MiddlewareQueryException {

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
	public StudyDetails getStudyDetails(StudyType studyType, int studyId) throws MiddlewareQueryException {
		return this.getStudyDataManager().getStudyDetails(studyType, studyId);
	}

	@Override
	public String getBlockId(int datasetId, String trialInstance) throws MiddlewareQueryException {
		return this.getGeolocationPropertyDao().getValueOfTrialInstance(datasetId, TermId.BLOCK_ID.getId(), trialInstance);
	}

	@Override
	public String getFolderNameById(Integer folderId) throws MiddlewareQueryException {
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
	public boolean checkIfStudyHasFieldmap(int studyId) throws MiddlewareQueryException {
		return this.getExperimentBuilder().checkIfStudyHasFieldmap(studyId);
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
		return this.getStudyDataManager().checkIfStudyHasMeasurementData(datasetId, variateIds);
	}

	@Override
	public int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
		return this.getStudyDataManager().countVariatesWithData(datasetId, variateIds);
	}

	@Override
	public void deleteObservationsOfStudy(int datasetId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();

			this.getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with deleteObservationsOfStudy(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList,
			List<MeasurementVariable> variateList) throws MiddlewareQueryException {
		return this.getWorkbookBuilder().buildTrialObservations(trialDatasetId, factorList, variateList);
	}

	@Override
	public List<Integer> getGermplasmIdsByName(String name) throws MiddlewareQueryException {
		return this.getNameDao().getGidsByName(name);
	}

	@Override
	public Integer addGermplasmName(String nameValue, int gid, int userId, int nameTypeId, int locationId, Integer date)
			throws MiddlewareQueryException {
		Name name = new Name(null, gid, nameTypeId, 0, userId, nameValue, locationId, date, 0);
		return this.getGermplasmDataManager().addGermplasmName(name);
	}

	@Override
	public Integer addGermplasm(String nameValue, int userId) throws MiddlewareQueryException {
		Name name = new Name(null, null, 1, 1, userId, nameValue, 0, 0, 0);
		Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, userId, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public Integer addGermplasm(Germplasm germplasm, Name name) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().addGermplasm(germplasm, name);
	}

	@Override
	public Integer getProjectIdByNameAndProgramUUID(String name, String programUUID) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID);
	}

	@Override
	public MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(String property, String scale, String method,
			PhenotypicType role) throws MiddlewareQueryException {
		MeasurementVariable variable = null;
		StandardVariable standardVariable = null;
		Integer id = this.getStandardVariableIdByPropertyScaleMethodRole(property, scale, method, role);
		if (id != null) {
			standardVariable = this.getStandardVariableBuilder().create(id);
			return this.getMeasurementVariableTransformer().transform(standardVariable, false);
		}
		return variable;
	}

	@Override
	public void setTreatmentFactorValues(List<TreatmentVariable> treatmentFactors, int measurementDatasetID)
			throws MiddlewareQueryException {
		this.getWorkbookBuilder().setTreatmentFactorValues(treatmentFactors, measurementDatasetID);
	}

	@Override
	public Workbook getCompleteDataset(int datasetId, boolean isTrial) throws MiddlewareQueryException {
		Workbook workbook = this.getDataSetBuilder().buildCompleteDataset(datasetId, isTrial);
		this.setOrderVariableByRank(workbook, datasetId);
		return workbook;
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes() throws MiddlewareQueryException {
		return this.getGermplasmListManager().getGermplasmNameTypes();
	}

	@Override
	public Map<Integer, List<Name>> getNamesByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.getNameDao().getNamesByGidsInMap(gids);
	}

	@Override
	public int countGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException {
		return (int) this.getGermplasmListManager().countGermplasmListDataByListId(listId);
	}

	@Override
	public int countListDataProjectGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException {
		return (int) this.getGermplasmListManager().countListDataProjectGermplasmListDataByListId(listId);
	}

	@Override
	public Method getMethodById(int id) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getMethodByID(id);
	}

	@Override
	public Method getMethodByCode(String code, String programUUID) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getMethodByCode(code, programUUID);
	}

	@Override
	public Method getMethodByName(String name) throws MiddlewareQueryException {
		return this.getGermplasmDataManager().getMethodByName(name);
	}

	@Override
	public void deleteStudy(int studyId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			this.getStudyDestroyer().deleteStudy(studyId);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public List<Long> getFavoriteProjectLocationIds(String programUUID) throws MiddlewareQueryException {
		List<ProgramFavorite> favList =
				this.getGermplasmDataManager().getProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, Integer.MAX_VALUE, programUUID);
		List<Long> longVals = new ArrayList<Long>();
		if (favList != null && !favList.isEmpty()) {
			for (ProgramFavorite fav : favList) {
				longVals.add(Long.valueOf(Integer.toString(fav.getEntityId())));
			}
		}
		return longVals;
	}

	@Override
	public List<Integer> getFavoriteProjectMethods(String programUUID) throws MiddlewareQueryException {
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
	public List<GermplasmList> getGermplasmListsByProjectId(int projectId, GermplasmListType type) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
	}

	@Override
	public List<ListDataProject> getListDataProject(int listId) throws MiddlewareQueryException {
		return this.getListDataProjectDAO().getByListId(listId);
	}

	@Override
	public ListDataProject getListDataProjectByStudy(int projectId, GermplasmListType type, int plotId) throws MiddlewareQueryException {
		return this.getListDataProjectDAO().getByStudy(projectId, type, plotId);
	}

	@Override
	public ListDataProject getListDataProjectByListIdAndEntryNo(int listId, int entryNo) throws MiddlewareQueryException {
		return this.getListDataProjectDAO().getByListIdAndEntryNo(listId, entryNo);
	}

	@Override
	public void deleteListDataProjects(int projectId, GermplasmListType type) throws MiddlewareQueryException {
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
			int userId) throws MiddlewareQueryException {

		int listId = 0;
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			listId = this.getListDataProjectSaver().saveOrUpdateListDataProject(projectId, type, originalListId, listDatas, userId);

			trans.commit();
		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with saveOrUpdateListDataProject(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}
		return listId;
	}

	@Override
	public void updateGermlasmListInfoStudy(int crossesListId, int studyId) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			this.getListDataProjectSaver().updateGermlasmListInfoStudy(crossesListId, studyId);

			trans.commit();

		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with updateGermlasmListInfoStudy(): " + e.getMessage(), e,
					FieldbookServiceImpl.LOG);
		}

	}

	@Override
	public void saveStudyColumnOrdering(Integer studyId, String studyName, List<Integer> orderedTermIds) throws MiddlewareQueryException {
		Integer plotDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(studyId, studyName);
		this.getStudyDataManager().updateVariableOrdering(plotDatasetId, orderedTermIds);
	}

	@Override
	public boolean setOrderVariableByRank(Workbook workbook) throws MiddlewareQueryException {
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

	public boolean setOrderVariableByRank(Workbook workbook, Integer plotDatasetId) throws MiddlewareQueryException {
		if (workbook != null) {
			List<Integer> storedInIds = new ArrayList<Integer>();
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
	public void addListDataProjectList(List<ListDataProject> listDataProjectList) throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			for (ListDataProject listDataProject : listDataProjectList) {
				listDataProject.setListDataProjectId(this.getListDataProjectDAO().getNextId("listDataProjectId"));
				listDataProject.setList(this.getGermplasmListById(listDataProject.getList().getId()));
				this.getListDataProjectDAO().save(listDataProject);
			}
			trans.commit();
		} catch (Exception e) {
			FieldbookServiceImpl.LOG.error(e.getMessage(), e);
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with addListDataProjectList(): " + e.getMessage(), e, FieldbookServiceImpl.LOG);
		}
	}

	@Override
	public StandardVariable getStandardVariableByName(String name) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().getByName(name);
	}

}
