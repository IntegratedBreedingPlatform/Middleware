
package org.generationcp.middleware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSetupTest extends DataManagerIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(DataSetupTest.class);

	private static WorkbenchDataManager workbenchDataManager;
	private static UserDataManager userDataManager;
	private static DataImportService dataImportService;
	private static GermplasmDataManager germplasmManager;
	private static GermplasmListManager germplasmListManager;
	private static FieldbookService middlewareFieldbookService;
	private static GermplasmTestDataGenerator germplasmTestDataGenerator;

	private static final int NUMBER_OF_GERMPLASM = 20;
	private static final String GERMPLSM_PREFIX = "GP-VARIETY-";

	private static final String PROP_BREEDING_METHOD = "Breeding Method";
	private static final String PROP_INSTITUTE = "Institute";
	private static final String PROP_STUDY = "Study";
	private static final String PROP_STUDY_TITLE = "Study Title";
	private static final String PROP_START_DATE = "Start Date";
	private static final String PROP_END_DATE = "End Date";
	private static final String PROP_OBJECTIVE = "Study Objective";

	private static final String CHAR = "C";
	private static final String NUMERIC = "N";

	private static final String ASSIGNED = "ASSIGNED";
	private static final String APPLIED = "APPLIED";

	private static final String STUDY = "STUDY";
	private static final String ENTRY = "ENTRY";
	private static final String PLOT = "PLOT";

	private static final String CONDUCTED = "CONDUCTED";
	private static final String DBCV = "DBCV";
	private static final String DBID = "DBID";

	private static final String SCALE_TEXT = "Text";
	private static final String ENUMERATED = "ENUMERATED";
	private static final String DESCRIBED = "Described";
	private static final String DATE = "Date (yyyymmdd)";

	private static final String GID = "GID";
	private static final String DESIG = "DESIG";
	private static final String CROSS = "CROSS";
	private static final String NUMBER = "NUMBER";
	private static final String PEDIGREE_STRING = "PEDIGREE STRING";

	private static final String KG_HA = "kg/ha";
	private static final String GRAIN_YIELD = "Grain Yield";
	private static final String DRY_AND_WEIGH = "Dry and weigh";

	@BeforeClass
	public static void setUp() {
		DataSetupTest.workbenchDataManager =
				new WorkbenchDataManagerImpl(new HibernateSessionPerThreadProvider(
						MiddlewareIntegrationTest.workbenchSessionUtil.getSessionFactory()));
		DataSetupTest.dataImportService = DataManagerIntegrationTest.managerFactory.getDataImportService();
		DataSetupTest.germplasmManager = DataManagerIntegrationTest.managerFactory.getGermplasmDataManager();
		DataSetupTest.germplasmListManager = DataManagerIntegrationTest.managerFactory.getGermplasmListManager();
		DataSetupTest.middlewareFieldbookService = DataManagerIntegrationTest.managerFactory.getFieldbookMiddlewareService();
		DataSetupTest.userDataManager = DataManagerIntegrationTest.managerFactory.getUserDataManager();
		DataSetupTest.germplasmTestDataGenerator = new GermplasmTestDataGenerator(DataSetupTest.germplasmManager);
	}

	@Test
	public void setUpBasicTestData() throws MiddlewareQueryException {
		String programUUID = this.createWorkbenchProgram();
		this.createNursery(programUUID);
	}

	private String createWorkbenchProgram() throws MiddlewareQueryException {

		Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("Joe");
		person.setMiddleName("The");
		person.setLastName("Breeder");
		person.setPositionName("Plant Breeder");
		person.setTitle("Mr.");
		person.setExtension("123");
		person.setFax("No Fax");
		person.setEmail("joe.breeder@ibp.org");
		person.setNotes("No Notes");
		person.setContact("No Contact");
		person.setLanguage(1);
		person.setPhone("02121212121");
		DataSetupTest.workbenchDataManager.addPerson(person);

		User workbenchUser = new User();
		workbenchUser.setInstalid(1);
		workbenchUser.setStatus(1);
		workbenchUser.setAccess(1);
		workbenchUser.setType(1);
		workbenchUser.setName("joe");
		workbenchUser.setPassword("b");
		workbenchUser.setPersonid(person.getId());
		workbenchUser.setAdate(20150101);
		workbenchUser.setCdate(20150101);
		workbenchUser.setRoles(Arrays.asList(new UserRole(workbenchUser, "ADMIN")));

		DataSetupTest.workbenchDataManager.addUser(workbenchUser);

		CropType cropType = DataSetupTest.workbenchDataManager.getCropTypeByName("maize");
		if (cropType == null) {
			cropType = new CropType("maize");
			cropType.setDbName("ibdbv2_maize_merged");
			cropType.setVersion("4.0.0");
			DataSetupTest.workbenchDataManager.addCropType(cropType);
		}

		Project program = new Project();
		program.setProjectName("Draught Resistance in Maize");
		program.setUserId(workbenchUser.getUserid());
		program.setStartDate(new Date(System.currentTimeMillis()));
		program.setCropType(cropType);
		program.setLastOpenDate(new Date(System.currentTimeMillis()));
		DataSetupTest.workbenchDataManager.addProject(program);

		List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		List<Role> allRolesList = DataSetupTest.workbenchDataManager.getAllRoles();
		for (Role role : allRolesList) {
			ProjectUserRole projectUserRole = new ProjectUserRole();
			projectUserRole.setUserId(workbenchUser.getUserid());
			projectUserRole.setRole(role);
			projectUserRole.setProject(program);
			projectUserRoles.add(projectUserRole);
		}
		DataSetupTest.workbenchDataManager.addProjectUserRole(projectUserRoles);

		User cropDBUser = workbenchUser.copy();
		Person cropDBPerson = person.copy();
		DataSetupTest.userDataManager.addPerson(cropDBPerson);
		cropDBUser.setPersonid(cropDBPerson.getId());
		DataSetupTest.userDataManager.addUser(cropDBUser);

		IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(workbenchUser.getUserid());
		ibdbUserMap.setProjectId(program.getProjectId());
		ibdbUserMap.setIbdbUserId(cropDBUser.getUserid());
		DataSetupTest.workbenchDataManager.addIbdbUserMap(ibdbUserMap);

		ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), workbenchUser.getUserid());
		DataSetupTest.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);

		return program.getUniqueID();
	}

	private void createNursery(String programUUID) throws MiddlewareQueryException {

		int randomInt = new Random().nextInt(100);

		// Germplasm
		Integer[] gids =
				DataSetupTest.germplasmTestDataGenerator.createGermplasmRecords(DataSetupTest.NUMBER_OF_GERMPLASM,
						DataSetupTest.GERMPLSM_PREFIX);

		// Germplasm list
		GermplasmList germplasmList =
				new GermplasmList(null, "Test Germplasm List " + randomInt, Long.valueOf(20141014), "LST", Integer.valueOf(1),
						"Test Germplasm List", null, 1);
		Integer germplasmListId = DataSetupTest.germplasmListManager.addGermplasmList(germplasmList);

		// Germplasm list data
		List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();
		for (int i = 0; i < DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
			germplasmListData.add(new GermplasmListData(null, germplasmList, gids[i], i, "EntryCode" + i, DataSetupTest.GERMPLSM_PREFIX + i
					+ " Source", DataSetupTest.GERMPLSM_PREFIX + i, DataSetupTest.GERMPLSM_PREFIX + "Group A", 0, 0));
		}
		DataSetupTest.germplasmListManager.addGermplasmListData(germplasmListData);

		// Now the Nursery creation via the Workbook

		Workbook workbook = new Workbook();
		// Basic Details
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		studyDetails.setStudyName("Test Nursery " + randomInt);
		studyDetails.setObjective(studyDetails.getStudyName() + " Objective");
		studyDetails.setTitle(studyDetails.getStudyName() + " Description");
		studyDetails.setStartDate("2014-10-01");
		studyDetails.setEndDate("2014-10-31");
		studyDetails.setParentFolderId(1);
		workbook.setStudyDetails(studyDetails);

		// Conditions
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

		conditions.add(this.createMeasurementVariable(TermId.BREEDING_METHOD_CODE.getId(), "STUDY_BM_CODE",
				"Breeding method applied to all plots in a study (CODE)", DataSetupTest.PROP_BREEDING_METHOD, DataSetupTest.APPLIED,
				"BMETH_CODE", DataSetupTest.CHAR, null, DataSetupTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));

		conditions.add(this.createMeasurementVariable(8080, "STUDY_INSTITUTE", "Study institute - conducted (DBCV)",
				DataSetupTest.PROP_INSTITUTE, DataSetupTest.CONDUCTED, DataSetupTest.DBCV, DataSetupTest.CHAR, "CIMMYT",
				DataSetupTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));

		conditions.add(this.createMeasurementVariable(TermId.STUDY_NAME.getId(), "STUDY_NAME", "Study - assigned (DBCV)",
				DataSetupTest.PROP_STUDY, DataSetupTest.ASSIGNED, DataSetupTest.DBCV, DataSetupTest.CHAR, studyDetails.getStudyName(),
				DataSetupTest.STUDY, TermId.STUDY_NAME_STORAGE.getId(), true));

		conditions.add(this.createMeasurementVariable(TermId.STUDY_TITLE.getId(), "STUDY_TITLE", "Study title - assigned (text)",
				DataSetupTest.PROP_STUDY_TITLE, DataSetupTest.ASSIGNED, DataSetupTest.SCALE_TEXT, DataSetupTest.CHAR,
				studyDetails.getTitle(), DataSetupTest.STUDY, TermId.STUDY_TITLE_STORAGE.getId(), true));

		conditions.add(this.createMeasurementVariable(TermId.START_DATE.getId(), "START_DATE", "Start date - assigned (date)",
				DataSetupTest.PROP_START_DATE, DataSetupTest.ASSIGNED, DataSetupTest.DATE, DataSetupTest.CHAR, studyDetails.getStartDate(),
				DataSetupTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));

		conditions.add(this.createMeasurementVariable(TermId.STUDY_OBJECTIVE.getId(), "STUDY_OBJECTIVE", "Objective - described (text)",
				DataSetupTest.PROP_OBJECTIVE, DataSetupTest.DESCRIBED, DataSetupTest.SCALE_TEXT, DataSetupTest.CHAR,
				studyDetails.getObjective(), DataSetupTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));

		conditions.add(this.createMeasurementVariable(TermId.END_DATE.getId(), "END_DATE", "End date - assigned (date)",
				DataSetupTest.PROP_END_DATE, DataSetupTest.ASSIGNED, DataSetupTest.DATE, DataSetupTest.CHAR, studyDetails.getEndDate(),
				DataSetupTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));

		workbook.setConditions(conditions);

		// Constants
		List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();
		constants.add(this.createMeasurementVariable(8270, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)", "Soil acidity", "Ph meter",
				"pH", DataSetupTest.NUMERIC, "7", DataSetupTest.STUDY, TermId.OBSERVATION_VARIATE.getId(), false));
		workbook.setConstants(constants);

		// Factors
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		MeasurementVariable entryFactor =
				this.createMeasurementVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO", "Germplasm entry - enumerated (number)",
						"Germplasm entry", DataSetupTest.ENUMERATED, DataSetupTest.NUMBER, DataSetupTest.NUMERIC, null,
						DataSetupTest.ENTRY, TermId.ENTRY_NUMBER_STORAGE.getId(), true);
		factors.add(entryFactor);

		MeasurementVariable designationFactor =
				this.createMeasurementVariable(TermId.DESIG.getId(), "DESIGNATION", "Germplasm designation - assigned (DBCV)",
						"Germplasm Designation", DataSetupTest.ASSIGNED, DataSetupTest.DBCV, DataSetupTest.CHAR, null, DataSetupTest.DESIG,
						TermId.ENTRY_DESIGNATION_STORAGE.getId(), true);
		factors.add(designationFactor);

		MeasurementVariable crossFactor =
				this.createMeasurementVariable(TermId.CROSS.getId(), "CROSS", "The pedigree string of the germplasm", "Cross history",
						DataSetupTest.ASSIGNED, DataSetupTest.PEDIGREE_STRING, DataSetupTest.CHAR, null, DataSetupTest.CROSS,
						TermId.GERMPLASM_ENTRY_STORAGE.getId(), true);
		factors.add(crossFactor);

		MeasurementVariable gidFactor =
				this.createMeasurementVariable(TermId.GID.getId(), "GID", "Germplasm identifier - assigned (DBID)", "Germplasm id",
						DataSetupTest.ASSIGNED, DataSetupTest.DBID, DataSetupTest.NUMERIC, null, DataSetupTest.GID,
						TermId.ENTRY_GID_STORAGE.getId(), true);
		factors.add(gidFactor);

		MeasurementVariable plotFactor =
				this.createMeasurementVariable(TermId.PLOT_NO.getId(), "PLOT_NO", "Field plot - enumerated (number)", "Field plot",
						DataSetupTest.ENUMERATED, DataSetupTest.NUMBER, DataSetupTest.NUMERIC, null, DataSetupTest.PLOT,
						TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), true);
		factors.add(plotFactor);

		workbook.setFactors(factors);

		// Variates
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		MeasurementVariable variate =
				this.createMeasurementVariable(18000, "Grain_yield", "Grain yield -dry and weigh (kg/ha)", DataSetupTest.GRAIN_YIELD,
						DataSetupTest.DRY_AND_WEIGH, DataSetupTest.KG_HA, DataSetupTest.NUMERIC, null, DataSetupTest.PLOT,
						TermId.OBSERVATION_VARIATE.getId(), false);
		variates.add(variate);

		workbook.setVariates(variates);

		// Observations
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		MeasurementRow row;
		List<MeasurementData> dataList;
		for (int i = 0; i < DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			MeasurementData entryData = new MeasurementData(entryFactor.getLabel(), String.valueOf(i));
			entryData.setMeasurementVariable(entryFactor);
			dataList.add(entryData);

			MeasurementData designationData = new MeasurementData(designationFactor.getLabel(), DataSetupTest.GERMPLSM_PREFIX + i);
			designationData.setMeasurementVariable(designationFactor);
			dataList.add(designationData);

			MeasurementData crossData =
					new MeasurementData(crossFactor.getLabel(), DataSetupTest.GERMPLSM_PREFIX + i + "MP-" + i + "/"
							+ DataSetupTest.GERMPLSM_PREFIX + i + "FP-" + i);
			crossData.setMeasurementVariable(crossFactor);
			dataList.add(crossData);

			MeasurementData gidData = new MeasurementData(gidFactor.getLabel(), String.valueOf(gids[i]));
			gidData.setMeasurementVariable(gidFactor);
			dataList.add(gidData);

			MeasurementData plotData = new MeasurementData(plotFactor.getLabel(), String.valueOf(i));
			plotData.setMeasurementVariable(plotFactor);
			dataList.add(plotData);

			MeasurementData variateData = new MeasurementData(variate.getLabel(), String.valueOf(new Random().nextInt(100)));
			variateData.setMeasurementVariable(variate);
			dataList.add(variateData);

			row.setDataList(dataList);
			observations.add(row);
		}
		workbook.setObservations(observations);

		// Save the workbook
		int nurseryStudyId = DataSetupTest.dataImportService.saveDataset(workbook, true, false, programUUID);
		DataSetupTest.LOG.info("Nursery " + studyDetails.getStudyName() + " created. ID: " + nurseryStudyId);

		// Convert germplasm list we created into ListDataProject entries
		List<ListDataProject> listDataProjects = new ArrayList<ListDataProject>();
		for (GermplasmListData gpListData : germplasmListData) {
			ListDataProject listDataProject = new ListDataProject();
			listDataProject.setCheckType(0);
			listDataProject.setGermplasmId(gpListData.getGid());
			listDataProject.setDesignation(gpListData.getDesignation());
			listDataProject.setEntryId(gpListData.getEntryId());
			listDataProject.setEntryCode(gpListData.getEntryCode());
			listDataProject.setSeedSource(gpListData.getSeedSource());
			listDataProject.setGroupName(gpListData.getGroupName());
			listDataProjects.add(listDataProject);
		}
		// Add listdata_project entries
		int nurseryListId =
				DataSetupTest.middlewareFieldbookService.saveOrUpdateListDataProject(nurseryStudyId, GermplasmListType.NURSERY,
						germplasmListId, listDataProjects, 1);

		// Load and check some basics
		Workbook nurseryWorkbook = DataSetupTest.middlewareFieldbookService.getNurseryDataSet(nurseryStudyId);
		Assert.assertNotNull(nurseryWorkbook);

		StudyDetails nurseryStudyDetails = nurseryWorkbook.getStudyDetails();
		Assert.assertNotNull(nurseryStudyDetails);
		Assert.assertNotNull(nurseryStudyDetails.getId());

		Assert.assertEquals(studyDetails.getStudyName(), nurseryStudyDetails.getStudyName());
		Assert.assertEquals(studyDetails.getTitle(), nurseryStudyDetails.getTitle());
		Assert.assertEquals(studyDetails.getObjective(), nurseryStudyDetails.getObjective());
		Assert.assertEquals(studyDetails.getStartDate(), nurseryStudyDetails.getStartDate());
		Assert.assertEquals(studyDetails.getEndDate(), nurseryStudyDetails.getEndDate());
		Assert.assertEquals(studyDetails.getStudyType(), nurseryStudyDetails.getStudyType());

		// Assert.assertEquals(conditions.size(), nurseryWorkbook.getConditions().size());
		Assert.assertEquals(constants.size(), nurseryWorkbook.getConstants().size());
		// Assert.assertEquals(factors.size(), nurseryWorkbook.getFactors().size());
		Assert.assertEquals(variates.size(), nurseryWorkbook.getVariates().size());
		Assert.assertEquals(observations.size(), nurseryWorkbook.getObservations().size());

		// Assert list data got saved with Nursery
		List<ListDataProject> listDataProject = DataSetupTest.middlewareFieldbookService.getListDataProject(nurseryListId);
		Assert.assertEquals(germplasmListData.size(), listDataProject.size());
	}

	private MeasurementVariable createMeasurementVariable(int termId, String name, String description, String property, String method,
			String scale, String dataType, String value, String label, int storedIn, boolean isFactor) {

		MeasurementVariable variable = new MeasurementVariable();

		variable.setTermId(termId);
		variable.setName(name);
		variable.setDescription(description);
		variable.setProperty(property);
		variable.setMethod(method);
		variable.setScale(scale);
		variable.setDataType(dataType);
		variable.setValue(value);
		variable.setLabel(label);
		variable.setStoredIn(storedIn);
		variable.setFactor(isFactor);

		return variable;
	}

}
