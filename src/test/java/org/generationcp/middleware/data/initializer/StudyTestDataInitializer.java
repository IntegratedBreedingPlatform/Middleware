
package org.generationcp.middleware.data.initializer;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.operation.saver.GeolocationSaver;
import org.generationcp.middleware.operation.saver.StockSaver;
import org.generationcp.middleware.operation.saver.StudySaver;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Contains Study related test data initializers
 */
public class StudyTestDataInitializer {

	public static final String STUDY_NAME = "STUDY NAME";
	public static final String STUDY_DESCRIPTION = "STUDY DESCRIPTION";
	public static final Integer STUDY_ID = 10010;
	public static final int PARENT_FOLDER_ID = 1;
	private static final String TEST_FOLDER_NAME = "TEST_FOLDER_NAME";
	private static final String TEST_FOLDER_DESC = "TEST_FOLDER_DESC";
	public static final String DATASET_NAME = "DATA SET NAME";
	public static final String START_DATE = "20160606";
	public static final String END_DATE = "20160606";
	public static final String OBJECTIVE = "OBJ1";
	public static int datasetId = 255;
	private static final String GERMPLASM_PREFIX = "GERMPLASM_PREFIX";

	private final StudyDataManagerImpl studyDataManager;
	private final OntologyDataManager ontologyManager;
	private final Project commonTestProject;
	private final LocationDataManager locationDataManager;
	private Integer geolocationId;
	private final HibernateSessionProvider sessionProvider;

	public StudyTestDataInitializer(
		final StudyDataManagerImpl studyDataManagerImpl, final OntologyDataManager ontologyDataManager,
		final Project testProject, final LocationDataManager locationDataManager, final HibernateSessionProvider provider) {
		this.studyDataManager = studyDataManagerImpl;
		this.ontologyManager = ontologyDataManager;
		this.commonTestProject = testProject;
		this.locationDataManager = locationDataManager;
		this.sessionProvider = provider;
	}

	public StudyReference addTestStudy() throws Exception {
		return this.addTestStudy(StudyTestDataInitializer.STUDY_NAME, this.commonTestProject.getUniqueID(), StudyTypeDto.getTrialDto(),
			StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
				.END_DATE, StudyTestDataInitializer.OBJECTIVE);
	}

	public StudyReference addTestStudy(final String uniqueId) throws Exception {
		return this.addTestStudy(StudyTestDataInitializer.STUDY_NAME, uniqueId, StudyTypeDto.getTrialDto(),
			StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
				.END_DATE, StudyTestDataInitializer.OBJECTIVE);
	}

	public StudyReference addTestStudy(final StudyTypeDto studyType, final String studyName) throws Exception {
		return this.addTestStudy(studyName, this.commonTestProject.getUniqueID(), studyType, StudyTestDataInitializer.STUDY_DESCRIPTION,
			StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
				.END_DATE, StudyTestDataInitializer.OBJECTIVE);
	}

	public StudyReference addTestStudy(
		final String studyName, final String uniqueId, final StudyTypeDto studyType, final String description, final String startDate,
		final String endDate, final String objective) throws
		Exception {
		final VariableTypeList typeList = new VariableTypeList();
		final VariableList variableList = new VariableList();

		final StudyValues studyValues = this.createStudyValues(variableList);

		final Integer userId = 1;

		final CropType crop = new CropType();

		final DmsProject project = new StudySaver(this.sessionProvider)
			.saveStudy(crop, StudyTestDataInitializer.PARENT_FOLDER_ID, typeList, studyValues, true, uniqueId, studyType, description,
				startDate, endDate, objective, studyName, String.valueOf(userId));

		final StudyReference addedStudy =
			new StudyReference(project.getProjectId(), project.getName(), project.getDescription(), uniqueId, studyType);

		addedStudy.setOwnerId(userId);
		return addedStudy;
	}

	public StudyReference addTestStudy(
		final String studyName, final StudyTypeDto studyType, final String seasonId, final String locationId,
		final String startDate) throws Exception {

		final VariableTypeList typeList = new VariableTypeList();
		final VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.LOCATION_ID.getId(), locationId, 5, PhenotypicType.STUDY);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.SEASON_VAR.getId(), seasonId, 6, PhenotypicType.STUDY);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		final StudyValues studyValues = this.createStudyValues(variableList);
		final Integer userId = 1;

		final DmsProject project = new StudySaver(this.sessionProvider)
			.saveStudy(new CropType(), StudyTestDataInitializer.PARENT_FOLDER_ID, typeList, studyValues, true,
				this.commonTestProject.getUniqueID(), studyType, StudyTestDataInitializer.STUDY_DESCRIPTION + "_" + studyName,
				startDate, StudyTestDataInitializer
					.END_DATE, StudyTestDataInitializer.OBJECTIVE, studyName, String.valueOf(userId));

		final StudyReference addedStudy =
			new StudyReference(project.getProjectId(), project.getName(), project.getDescription(), this.commonTestProject.getUniqueID(),
				studyType);

		addedStudy.setOwnerId(userId);
		return addedStudy;
	}

	private StudyValues createStudyValues(final VariableList variableList) throws Exception {

		final StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		final VariableList locationVariableList = this.createEnvironment("Description", "1.0", "2.0", "data", "3.0", "RCBD");

		final GeolocationSaver geolocationSaver = new GeolocationSaver(this.sessionProvider);
		final Geolocation geolocation = geolocationSaver.saveGeolocation(locationVariableList, null);

		this.geolocationId = geolocation.getLocationId();
		studyValues.setLocationId(this.geolocationId);

		return studyValues;
	}

	private Variable createVariable(final int termId, final String value, final int rank, final PhenotypicType type) throws Exception {
		final StandardVariable stVar = this.ontologyManager.getStandardVariable(termId, this.commonTestProject.getUniqueID());

		final DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		final Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		vtype.setLocalName(value);
		vtype.setRole(type);
		return var;
	}

	private VariableList createEnvironment(
		final String trialInstance, final String latitude, final String longitude, final String data,
		final String altitude, final String experimentDesign) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstance, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LATITUDE.getId(), latitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LONGITUDE.getId(), longitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.GEODETIC_DATUM.getId(), data, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.ALTITUDE.getId(), altitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList
			.add(this.createVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), experimentDesign, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}

	public VariableList createEnvironmentWithLocationAndSeason(
		final String trialInstance, final String siteName, final String locationId, final String seasonId)
		throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstance, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.TRIAL_LOCATION.getId(), siteName, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LOCATION_ID.getId(), locationId, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.SEASON_VAR.getId(), seasonId, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}

	private VariableList createGermplasm(
		final String entryNumber, final String gid, final String designation, final String code,
		final String check, final String cross) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.ENTRY_NO.getId(), entryNumber, 1, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.GID.getId(), gid, 2, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.DESIG.getId(), designation, 3, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.ENTRY_CODE.getId(), code, 4, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.ENTRY_TYPE.getId(), check, 5, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.CROSS.getId(), cross, 6, PhenotypicType.GERMPLASM));
		return variableList;
	}

	public DmsProject createFolderTestData(final String uniqueId) {
		return createFolderTestData(uniqueId, null);
	}

	public DmsProject createFolderTestData(final String uniqueId, final Integer parentId) {
		final int randomInt = new Random().nextInt(10000);
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(StudyTestDataInitializer.TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(StudyTestDataInitializer.TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		final Integer parentFolderId = parentId != null ? parentId : DmsProject.SYSTEM_FOLDER_ID;
		final DmsProject parent = new DmsProject();
		parent.setProjectId(parentFolderId);
		dmsProject.setParent(parent);
		final int folderId = this.studyDataManager.addSubFolder(parentFolderId, dmsProject.getName(),
			dmsProject.getDescription(), dmsProject.getProgramUUID(), StudyTestDataInitializer.OBJECTIVE);
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}

	public DatasetReference addTestDataset(final int studyId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Dataset Description");

		DMSVariableType variableType =
			this.createVariableType(51570, "GY_Adj_kgha", "Grain yield BY Adjusted GY - Computation IN Kg/ha", 4);
		variableType.setLocalName("GY_Adj_kgha");
		typeList.add(variableType);

		variableType =
			this.createVariableType(20444, "SCMVInc_Cmp_pct", "Sugarcane mosaic virus incidence BY SCMVInc - Computation IN %", 5);
		variableType.setLocalName("Aphid damage");
		typeList.add(variableType);

		variableType = this.createVariableType(TermId.PLOT_NO.getId(), "Plot No", "Plot No", 6);
		variableType.setLocalName("Plot No");
		typeList.add(variableType);

		return this.studyDataManager.addDataSet(studyId, typeList, datasetValues, null, DatasetTypeEnum.MEANS_DATA.getId());
	}

	public DatasetReference addTestDataset(final int studyId, final int datasetTypeId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Dataset Description");

		final DMSVariableType variableType = this.createVariableType(TermId.LOCATION_ID.getId(), "Location Id", "Location Id", 1);
		variableType.setLocalName("LOCATION_NAME");
		typeList.add(variableType);

		final DMSVariableType variableType2 =
			this.createVariableType(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), "Design Factor", "Design Factor", 2);
		variableType2.setLocalName("EXPERIMENT_DESIGN_FACTOR");
		typeList.add(variableType2);

		return this.studyDataManager.addDataSet(studyId, typeList, datasetValues, null, datasetTypeId);

	}

	public Integer createEnvironmentDataset(final CropType crop, final int studyId, final String locationId, final String seasonId)
		throws Exception {
		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName("ENVIRONMENT " + StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Environment Dataset");
		final DatasetReference dataSet =
			this.studyDataManager.addDataSet(studyId, new VariableTypeList(), datasetValues, null, DatasetTypeEnum.SUMMARY_DATA.getId());

		this.geolocationId = this.addEnvironmentToDataset(crop, dataSet.getId(), 1, locationId, seasonId);

		return dataSet.getId();
	}

	public Integer addEnvironmentToDataset(final CropType crop, final Integer datasetId, final Integer trialInstance, final String locationId, final String seasonId)
		throws Exception {
		final VariableList
			locationVariableList = this.createEnvironmentWithLocationAndSeason(String.valueOf(trialInstance), "SOME SITE NAME", locationId, seasonId);
		final GeolocationSaver geolocationSaver = new GeolocationSaver(this.sessionProvider);
		final Geolocation geolocation = geolocationSaver.saveGeolocation(locationVariableList, null);

		this.geolocationId = geolocation.getLocationId();
		final ExperimentValues experimentValue = new ExperimentValues();
		experimentValue.setLocationId(geolocationId);

		final ExperimentModelSaver experimentModelSaver = new ExperimentModelSaver(this.sessionProvider);
		experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT, experimentValue);

		return geolocationId;
	}

	private DMSVariableType createVariableType(final int termId, final String name, final String description, final int rank)
		throws Exception {
		final StandardVariable stdVar = this.ontologyManager.getStandardVariable(termId, this.commonTestProject.getUniqueID());
		final DMSVariableType vtype = new DMSVariableType();
		vtype.setLocalName(name);
		vtype.setLocalDescription(description);
		vtype.setRank(rank);
		vtype.setStandardVariable(stdVar);
		vtype.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		return vtype;
	}

	public Integer addTestLocation(final String locationName) {
		final Country country = this.locationDataManager.getCountryById(1);
		final Location province = this.locationDataManager.getLocationByID(1001);

		final Location location = new Location();
		location.setCountry(country);
		location.setLabbr(RandomStringUtils.randomAlphabetic(4).toUpperCase());
		location.setLname(locationName);
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(1);
		location.setProvince(province);
		location.setSnl2id(1);
		location.setSnl3id(1);

		// add the location
		return this.locationDataManager.addLocation(location);
	}

	public List<Integer> addStudyGermplasm(final Integer studyId, final Integer startingEntryNumber, final List<Integer> gids) throws Exception {
		Integer entryNumber = startingEntryNumber;
		final List<Integer> entryIds = new ArrayList<>();
		for (final Integer gid : gids) {
			final StockModel stockModel = new StockModel();
			stockModel.setUniqueName("1");
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
			stockModel.setName("Germplasm " + RandomStringUtils.randomAlphanumeric(5));
			stockModel.setIsObsolete(false);
			stockModel.setGermplasm(new Germplasm(gid));
			stockModel.setProject(new DmsProject(studyId));

			final VariableList variableList =
				this.createGermplasm(String.valueOf(entryNumber), gid.toString(), StudyTestDataInitializer.GERMPLASM_PREFIX + gid,
					RandomStringUtils.randomAlphanumeric(5), String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId()),
					RandomStringUtils.randomAlphanumeric(5));
			final StockSaver stockSaver = new StockSaver(this.sessionProvider);
			final int entryId = stockSaver.saveStock(studyId, variableList);
			entryIds.add(entryId);
			entryNumber++;
		}
		return entryIds;
	}

}
