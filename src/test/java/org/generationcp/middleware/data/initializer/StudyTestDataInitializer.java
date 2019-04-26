
package org.generationcp.middleware.data.initializer;

import java.util.Random;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSetType;
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
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

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

	private final StudyDataManagerImpl studyDataManager;
	private final OntologyDataManager ontologyManager;
	private final Project commonTestProject;
	private final GermplasmDataManager germplasmDataDM;
	private final LocationDataManager locationDataManager;
	private final UserDataManager userDataManager;
	private Integer gid;

	public StudyTestDataInitializer(final StudyDataManagerImpl studyDataManagerImpl, final OntologyDataManager ontologyDataManager,
			final Project testProject, final GermplasmDataManager germplasmDataDM, final LocationDataManager locationDataManager, final UserDataManager userDataManager) {
		this.studyDataManager = studyDataManagerImpl;
		this.ontologyManager = ontologyDataManager;
		this.commonTestProject = testProject;
		this.germplasmDataDM = germplasmDataDM;
		this.locationDataManager = locationDataManager;
		this.userDataManager = userDataManager;
	}

	public StudyReference addTestStudy() throws Exception {
		return this.addTestStudy(StudyTestDataInitializer.STUDY_NAME, this.commonTestProject.getUniqueID(), StudyTypeDto.getTrialDto(),
			StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
				.END_DATE, StudyTestDataInitializer.OBJECTIVE);
	}
	
	public StudyReference addTestStudy(final String uniqueId) throws Exception {
		return this.addTestStudy(StudyTestDataInitializer.STUDY_NAME, uniqueId, StudyTypeDto.getTrialDto(), StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
			.END_DATE,StudyTestDataInitializer.OBJECTIVE);
	}
	
	public StudyReference addTestStudy(final StudyTypeDto studyType, final String studyName) throws Exception {
		return this.addTestStudy(studyName, this.commonTestProject.getUniqueID(), studyType, StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.START_DATE, StudyTestDataInitializer
			.END_DATE, StudyTestDataInitializer.OBJECTIVE);
	}

	public StudyReference addTestStudy(final String studyName, final String uniqueId, final StudyTypeDto studyType, final String description, final String startDate, final String endDate, final String objective) throws
		Exception {
		final VariableTypeList typeList = new VariableTypeList();
		final VariableList variableList = new VariableList();

		final StudyValues studyValues = this.createStudyValues(variableList);
		
		final Integer userId = this.addTestUser();

		final CropType crop = new CropType();
		final StudyReference addedStudy = this.studyDataManager.addStudy(crop, StudyTestDataInitializer.PARENT_FOLDER_ID, typeList, studyValues, uniqueId,
			studyType, description, startDate, endDate, objective, studyName, String.valueOf(userId));
		addedStudy.setOwnerId(userId);
		return addedStudy;
	}

	private Integer addTestUser() {
		final Integer personId = this.userDataManager.addPerson(PersonTestDataInitializer.createPerson());
		final User user = UserTestDataInitializer.createActiveUser();
		user.setPersonid(personId);
		final Integer userId = this.userDataManager.addUser(user);
		return userId;
	}

	public StudyReference addTestStudy(final String studyName, final StudyTypeDto studyType, final String seasonId, final String locationId,
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
		final Integer userId = this.addTestUser();

		final StudyReference addedStudy = this.studyDataManager.addStudy(new CropType(), StudyTestDataInitializer.PARENT_FOLDER_ID, typeList, studyValues, this.commonTestProject.getUniqueID(), studyType, StudyTestDataInitializer.STUDY_DESCRIPTION + "_" + studyName, startDate, StudyTestDataInitializer
				.END_DATE, StudyTestDataInitializer.OBJECTIVE, studyName, String.valueOf(userId));
		addedStudy.setOwnerId(userId);
		return addedStudy;
	}
	
	private StudyValues createStudyValues(final VariableList variableList) throws Exception {

		final StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		final VariableList locationVariableList = this.createEnvironment("Description", "1.0", "2.0", "data", "3.0", "RCBD");
		studyValues.setLocationId(this.studyDataManager.addTrialEnvironment(locationVariableList));

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.gid = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());
		final VariableList germplasmVariableList =
				this.createGermplasm("unique name", String.valueOf(this.gid), "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(this.studyDataManager.addStock(germplasmVariableList));

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

	private VariableList createEnvironment(final String trialInstance, final String latitude, final String longitude, final String data,
			final String altitude, final String experimentDesign) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstance, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LATITUDE.getId(), latitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LONGITUDE.getId(), longitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.GEODETIC_DATUM.getId(), data, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.ALTITUDE.getId(), altitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), experimentDesign, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}
	
	private VariableList createEnvironmentWithLocationAndSeason(final String trialInstance, final String experimentDesign, final String siteName, final String locationId, final String seasonId) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstance, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), experimentDesign, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.TRIAL_LOCATION.getId(), siteName, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LOCATION_ID.getId(), locationId, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.SEASON_VAR.getId(), seasonId, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}

	private VariableList createGermplasm(final String name, final String gid, final String designation, final String code,
			final String property1, final String property2) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.ENTRY_NO.getId(), name, 1, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.GID.getId(), gid, 2, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.DESIG.getId(), designation, 3, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.ENTRY_CODE.getId(), code, 4, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.CHECK.getId(), property1, 5, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.CROSS.getId(), property2, 6, PhenotypicType.GERMPLASM));
		return variableList;
	}

	public DmsProject createFolderTestData(final String uniqueId) throws MiddlewareQueryException {
		final int randomInt = new Random().nextInt(10000);
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(StudyTestDataInitializer.TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(StudyTestDataInitializer.TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		final int folderId = this.studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, dmsProject.getName(),
				dmsProject.getDescription(), dmsProject.getProgramUUID(), StudyTestDataInitializer.OBJECTIVE);
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}

	public DatasetReference addTestDataset(final int studyId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Dataset Description");
		datasetValues.setType(DataSetType.MEANS_DATA);

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

		return this.studyDataManager.addDataSet(studyId, typeList, datasetValues, null);
	}

	public DatasetReference addTestDataset(final int studyId, final DataSetType datasetType) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Dataset Description");
		datasetValues.setType(datasetType);

		final DMSVariableType variableType = this.createVariableType(TermId.LOCATION_ID.getId(), "Location Id", "Location Id", 1);
		variableType.setLocalName("LOCATION_NAME");
		typeList.add(variableType);

		final DMSVariableType variableType2 = this.createVariableType(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), "Design Factor", "Design Factor", 2);
		variableType2.setLocalName("EXPERIMENT_DESIGN_FACTOR");
		typeList.add(variableType2);

		return this.studyDataManager.addDataSet(studyId, typeList, datasetValues, null);

	}
	
	public Integer addEnvironmentDataset(final CropType crop, final int studyId, final String locationId, final String seasonId) throws Exception {
		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName("ENVIRONMENT " + StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Environment Dataset");
		datasetValues.setType(DataSetType.SUMMARY_DATA);
		final DatasetReference dataSet = this.studyDataManager.addDataSet(studyId, new VariableTypeList(), datasetValues, null);

		this.addEnvironmentToDataset(crop, dataSet.getId(), locationId, seasonId);

		return dataSet.getId();
	}

	public Integer addEnvironmentToDataset(final CropType crop, final Integer datasetId, final String locationId, final String seasonId)
		throws Exception {
		final VariableList
			locationVariableList = this.createEnvironmentWithLocationAndSeason("1", "RCBD", "SOME SITE NAME", locationId, seasonId);
		final int geolocationId = this.studyDataManager.addTrialEnvironment(locationVariableList);

		final ExperimentValues experimentValue = new ExperimentValues();
		experimentValue.setLocationId(geolocationId);
		this.studyDataManager.addExperiment(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT, experimentValue);

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

	public Integer getGid() {
		return this.gid;
	}

	public Integer addTestLocation(final String locationName){
		final Location location = new Location();
		location.setCntryid(1);
		location.setLabbr("");
		location.setLname(locationName);
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(1);
		location.setSnl1id(1);
		location.setSnl2id(1);
		location.setSnl3id(1);
		location.setUniqueID(this.commonTestProject.getUniqueID());

		// add the location
		return this.locationDataManager.addLocation(location);
	}
}
