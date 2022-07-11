
package org.generationcp.middleware;

import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Ignore("This is just for seeding some test data. Not intended to run regularly on CI.")
@TransactionConfiguration(defaultRollback = false)
public class DataSetupTest extends IntegrationTestBase {

    private static final String LOCATION_NAME_PROP = "Location name";

    private static final String LOCATION = "LOCATION";

    private static final Logger LOG = LoggerFactory.getLogger(DataSetupTest.class);
    public static final String STUDY_INSTITUTE = "STUDY_INSTITUTE";

    @Autowired
    private CropService cropService;

    @Autowired
    private UserService userService;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private GermplasmListManager germplasmListManager;

    @Autowired
    private FieldbookService middlewareFieldbookService;

	@Autowired
	private ProgramService programService;

    private DaoFactory daoFactory;

    private GermplasmTestDataGenerator germplasmTestDataGenerator;

    public static final int NUMBER_OF_GERMPLASM = 20;
    public static final String GERMPLSM_PREFIX = "GP-VARIETY-";
    public static final String LOCATION_NAME = "LOCATION_NAME";

    private static final String PROP_BREEDING_METHOD = "Breeding Method";
    private static final String PROP_INSTITUTE = "Institute";

    private static final String CHAR = "C";
    private static final String NUMERIC = "N";

    private static final String ASSIGNED = "ASSIGNED";
    private static final String APPLIED = "APPLIED";

    private static final String STUDY = "STUDY";
    private static final String TRIAL = "TRIAL";
    private static final String ENTRY = "ENTRY";
    private static final String PLOT = "PLOT";
    private static final String REP_NO = "REP_NO";


    private static final String CONDUCTED = "CONDUCTED";
    private static final String DBCV = "DBCV";
    private static final String DBID = "DBID";

    private static final String SCALE_TEXT = "Text";
    private static final String ENUMERATED = "ENUMERATED";
    private static final String OBSERVED = "Observed";

    private static final String GID = "GID";
    private static final String DESIG = "DESIG";
    private static final String CROSS = "CROSS";
    private static final String NUMBER = "NUMBER";
    private static final String PEDIGREE_STRING = "PEDIGREE STRING";

    private static final String KG_HA = "kg/ha";
    private static final String GRAIN_YIELD = "Grain Yield";
    private static final String DRY_AND_WEIGH = "Dry and weigh";
    private final String cropPrefix = "ABCD";

    @Before
    public void setUp() {
        if (this.germplasmTestDataGenerator == null) {
            this.daoFactory = new DaoFactory(this.sessionProvder);
            this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);
        }
    }

    @Test
    public void setUpBasicTestData() {
        final String programUUID = this.createWorkbenchProgram();
        this.createNursery(programUUID, this.cropPrefix);
    }

    private String createWorkbenchProgram() {

        final Person person = new Person();
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
        this.userService.addPerson(person);

        final WorkbenchUser workbenchUser = new WorkbenchUser();
        workbenchUser.setInstalid(1);
        workbenchUser.setStatus(1);
        workbenchUser.setAccess(1);
        workbenchUser.setType(1);
        workbenchUser.setName("joe");
        // Bcrypt string for password "b" generated at
        // https://www.bcrypt-generator.com/
        workbenchUser.setPassword("$2a$08$sfZD1PpIrk3KHcqvUarui.eWRir4OXWYEaVSNcvyVK6EtkB5RzYl.");
        workbenchUser.setPerson(person);
        workbenchUser.setAssignDate(20150101);
        workbenchUser.setCloseDate(20150101);
        // Role ID 1 = ADMIN
        workbenchUser.setRoles(Arrays.asList(new UserRole(workbenchUser, 1)));

        this.userService.addUser(workbenchUser);

        CropType cropType = this.cropService.getCropTypeByName("maize");
        if (cropType == null) {
            cropType = new CropType("maize");
            cropType.setDbName("ibdbv2_maize_merged");
            cropType.setVersion("4.0.0");
            this.daoFactory.getCropTypeDAO().saveOrUpdate(cropType);
        }

        final Project program = new Project();
        program.setProjectName("Draught Resistance in Maize" + new Random().nextInt(100));
        program.setUserId(workbenchUser.getUserid());
        program.setStartDate(new Date(System.currentTimeMillis()));
        program.setCropType(cropType);
        program.setLastOpenDate(new Date(System.currentTimeMillis()));
        this.programService.addProgram(program);

        // FIXME (BMS-4631) replace this with adding to workbench_project_user_info
//		this.workbenchDataManager.addProjectUserRole(projectUserRoles);

        final ProjectUserInfo pUserInfo = new ProjectUserInfo(program,
                workbenchUser);
        this.userService.saveOrUpdateProjectUserInfo(pUserInfo);

        return program.getUniqueID();
    }

    private void createNursery(final String programUUID, final String cropPrefix) {

        // Create Germplasm
        final Integer[] gids = this.germplasmTestDataGenerator.createGermplasmRecords(DataSetupTest.NUMBER_OF_GERMPLASM,
                DataSetupTest.GERMPLSM_PREFIX);

        this.createNurseryForGermplasm(programUUID, gids, cropPrefix);
    }

    public int createNurseryForGermplasm(final String programUUID, final Integer[] gids, final String cropPrefix) {
        final int randomInt = new Random().nextInt(100);

        // Germplasm list
        final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
                Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);

        final Integer germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);
        germplasmList.setProgramUUID(programUUID);

        // Germplasm list data
        final List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();
        for (int i = 0; i < DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
            germplasmListData.add(new GermplasmListData(null, germplasmList, gids[i], i, "EntryCode" + i,
                    DataSetupTest.GERMPLSM_PREFIX + i + " Source", DataSetupTest.GERMPLSM_PREFIX + "Group A",
                0, 0));
        }
        this.germplasmListManager.addGermplasmListData(germplasmListData);

        // Now the Nursery creation via the Workbook

        final Workbook workbook = new Workbook();
        // Basic Details
        final StudyDetails studyDetails = new StudyDetails();
        studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
        studyDetails.setStudyName("Test Nursery " + randomInt);
        studyDetails.setObjective(studyDetails.getStudyName() + " Objective");
        studyDetails.setDescription(studyDetails.getStudyName() + " Description");
        studyDetails.setStartDate("20151001");
        studyDetails.setEndDate("20151031");
        studyDetails.setParentFolderId(1);
        studyDetails.setCreatedBy("1");
        workbook.setStudyDetails(studyDetails);

        // Conditions
        final List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

        conditions.add(this.createMeasurementVariable(TermId.BREEDING_METHOD_CODE.getId(), "STUDY_BM_CODE",
                "Breeding method applied to all plots in a study (CODE)", DataSetupTest.PROP_BREEDING_METHOD,
                DataSetupTest.APPLIED, "BMETH_CODE", DataSetupTest.CHAR, null, DataSetupTest.STUDY,
                PhenotypicType.STUDY, true));

        conditions.add(this.createMeasurementVariable(8080, STUDY_INSTITUTE, "Study institute - conducted (DBCV)",
                DataSetupTest.PROP_INSTITUTE, DataSetupTest.CONDUCTED, DataSetupTest.DBCV, DataSetupTest.CHAR, "CIMMYT",
                DataSetupTest.STUDY, PhenotypicType.STUDY, true));

        conditions.add(this.createMeasurementVariable(TermId.TRIAL_LOCATION.getId(), LOCATION_NAME, LOCATION_NAME + " - description",
                LOCATION, DataSetupTest.ASSIGNED, LOCATION_NAME_PROP, DataSetupTest.CHAR,
                "Default Breeding Location", DataSetupTest.STUDY, PhenotypicType.TRIAL_ENVIRONMENT, true));

        // Need to set TRIAL_INSTANCE # manually since we're adding other environment level conditions
        conditions.add(this.createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL_INSTANCE", "Trial instance - enumerated (number)",
                "Trial Instance", DataSetupTest.ENUMERATED, DataSetupTest.NUMBER, DataSetupTest.NUMERIC,
                "1", DataSetupTest.TRIAL, PhenotypicType.TRIAL_ENVIRONMENT, true));
        conditions.add(this.createMeasurementVariable(TermId.ALTITUDE.getId(), "SITE_ALT", "Altitude of site observed",
                "Altitude", DataSetupTest.OBSERVED, "m", DataSetupTest.NUMERIC,
                "143", DataSetupTest.TRIAL, PhenotypicType.TRIAL_ENVIRONMENT, true));
        conditions.add(this.createMeasurementVariable(TermId.LATITUDE.getId(), "SITE_LAT", "Latitude of site - observed (decimal + for N, - for S)",
                "Latitude", DataSetupTest.OBSERVED, "Decimal + for N, - for S", DataSetupTest.NUMERIC,
                "422", DataSetupTest.TRIAL, PhenotypicType.TRIAL_ENVIRONMENT, true));
        conditions.add(this.createMeasurementVariable(TermId.LONGITUDE.getId(), "SITE_LONG", "Longitude of site - observed (decimal + for E , - for W)",
                "Longitude", DataSetupTest.OBSERVED, "Decimal + for E, - for W", DataSetupTest.NUMERIC,
                "242", DataSetupTest.TRIAL, PhenotypicType.TRIAL_ENVIRONMENT, true));
        conditions.add(this.createMeasurementVariable(TermId.GEODETIC_DATUM.getId(), "SITE_DATUM", "Geodetic datum for lat, long - observed (text)",
                "Geodeticdatum", DataSetupTest.OBSERVED, DataSetupTest.SCALE_TEXT, DataSetupTest.NUMERIC,
                "224", DataSetupTest.TRIAL, PhenotypicType.TRIAL_ENVIRONMENT, true));

        workbook.setConditions(conditions);

        // Constants
        final List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();
        constants.add(this.createMeasurementVariable(8270, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",
                "Soil acidity", "Ph meter", "pH", DataSetupTest.NUMERIC, "7", DataSetupTest.STUDY,
                PhenotypicType.VARIATE, false));
        workbook.setConstants(constants);

        // Factors
        final List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
        final MeasurementVariable entryFactor = this.createMeasurementVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO",
                "Germplasm entry - enumerated (number)", "Germplasm entry", DataSetupTest.ENUMERATED,
                DataSetupTest.NUMBER, DataSetupTest.NUMERIC, null, DataSetupTest.ENTRY, PhenotypicType.GERMPLASM, true);
        factors.add(entryFactor);

        final MeasurementVariable designationFactor = this.createMeasurementVariable(TermId.DESIG.getId(),
                "DESIGNATION", "Germplasm designation - assigned (DBCV)", "Germplasm Designation",
                DataSetupTest.ASSIGNED, DataSetupTest.DBCV, DataSetupTest.CHAR, null, DataSetupTest.DESIG,
                PhenotypicType.GERMPLASM, true);
        factors.add(designationFactor);

        final MeasurementVariable crossFactor = this.createMeasurementVariable(TermId.CROSS.getId(), "CROSS",
                "The pedigree string of the germplasm", "Cross history", DataSetupTest.ASSIGNED,
                DataSetupTest.PEDIGREE_STRING, DataSetupTest.CHAR, null, DataSetupTest.CROSS, PhenotypicType.GERMPLASM,
                true);
        factors.add(crossFactor);

        final MeasurementVariable gidFactor = this.createMeasurementVariable(TermId.GID.getId(), "GID",
                "Germplasm identifier - assigned (DBID)", "Germplasm id", DataSetupTest.ASSIGNED, DataSetupTest.DBID,
                DataSetupTest.NUMERIC, null, DataSetupTest.GID, PhenotypicType.GERMPLASM, true);
        factors.add(gidFactor);

        final MeasurementVariable plotFactor = this.createMeasurementVariable(TermId.PLOT_NO.getId(), "PLOT_NO",
                "Field plot - enumerated (number)", "Field plot", DataSetupTest.ENUMERATED, DataSetupTest.NUMBER,
                DataSetupTest.NUMERIC, null, DataSetupTest.PLOT, PhenotypicType.TRIAL_DESIGN, true);

        factors.add(plotFactor);

        final MeasurementVariable repNoFactor = this.createMeasurementVariable(TermId.REP_NO.getId(), "REP_NO",
            "Replication - assigned (number)", "Replication factor", DataSetupTest.ENUMERATED, DataSetupTest.NUMBER,
            DataSetupTest.NUMERIC, null, DataSetupTest.REP_NO, PhenotypicType.TRIAL_DESIGN, true);
        factors.add(repNoFactor);

        workbook.setFactors(factors);

        // Variates
        final List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
        final MeasurementVariable variate = this.createMeasurementVariable(51570, "GY_Adj_kgha",
                "Grain yield BY Adjusted GY - Computation IN Kg/ha", DataSetupTest.GRAIN_YIELD, DataSetupTest.DRY_AND_WEIGH,
                DataSetupTest.KG_HA, DataSetupTest.NUMERIC, null, DataSetupTest.PLOT, PhenotypicType.VARIATE, false);
        variates.add(variate);

        workbook.setVariates(variates);

        // Observations
        final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
        MeasurementRow row;
        List<MeasurementData> dataList;
        Integer plotNumber = 1;
        for (int repNo = 1; repNo <= 2; repNo++) {
            for (int i = 0; i < DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
                row = new MeasurementRow();
                dataList = new ArrayList<>();
                final MeasurementData entryData = new MeasurementData(entryFactor.getLabel(), String.valueOf(i));
                entryData.setMeasurementVariable(entryFactor);
                dataList.add(entryData);

                final MeasurementData designationData = new MeasurementData(designationFactor.getLabel(),
                    DataSetupTest.GERMPLSM_PREFIX + i);
                designationData.setMeasurementVariable(designationFactor);
                dataList.add(designationData);

                final MeasurementData crossData = new MeasurementData(crossFactor.getLabel(), DataSetupTest.GERMPLSM_PREFIX
                    + i + "MP-" + i + "/" + DataSetupTest.GERMPLSM_PREFIX + i + "FP-" + i);
                crossData.setMeasurementVariable(crossFactor);
                dataList.add(crossData);

                final MeasurementData gidData = new MeasurementData(gidFactor.getLabel(), String.valueOf(gids[i]));
                gidData.setMeasurementVariable(gidFactor);
                dataList.add(gidData);

                final MeasurementData plotData = new MeasurementData(plotFactor.getLabel(), String.valueOf(plotNumber++));
                plotData.setMeasurementVariable(plotFactor);
                dataList.add(plotData);

                final MeasurementData repNoData = new MeasurementData(repNoFactor.getLabel(), String.valueOf(repNo));
                repNoData.setMeasurementVariable(repNoFactor);
                dataList.add(repNoData);

                final MeasurementData variateData = new MeasurementData(variate.getLabel(),
                    String.valueOf(new Random().nextInt(100)));
                variateData.setMeasurementVariable(variate);
                dataList.add(variateData);

                row.setDataList(dataList);
                observations.add(row);
            }
        }

        workbook.setObservations(observations);

        // Save the workbook
        final CropType crop = new CropType();
        crop.setPlotCodePrefix(cropPrefix);
        final int nurseryStudyId = this.dataImportService.saveDataset(workbook, true, false, programUUID, crop);
        DataSetupTest.LOG.info("Nursery " + studyDetails.getStudyName() + " created. ID: " + nurseryStudyId);

        // Load and check some basics
        final Workbook nurseryWorkbook = this.middlewareFieldbookService.getStudyDataSet(nurseryStudyId);
        Assert.assertNotNull(nurseryWorkbook);

        final StudyDetails nurseryStudyDetails = nurseryWorkbook.getStudyDetails();
        Assert.assertNotNull(nurseryStudyDetails);
        Assert.assertNotNull(nurseryStudyDetails.getId());

        Assert.assertEquals(studyDetails.getStudyName(), nurseryStudyDetails.getStudyName());
        Assert.assertEquals(studyDetails.getDescription(), nurseryStudyDetails.getDescription());
        Assert.assertEquals(studyDetails.getObjective(), nurseryStudyDetails.getObjective());
        Assert.assertEquals(studyDetails.getStartDate(), nurseryStudyDetails.getStartDate());
        Assert.assertEquals(studyDetails.getEndDate(), nurseryStudyDetails.getEndDate());
        Assert.assertEquals(studyDetails.getStudyType(), nurseryStudyDetails.getStudyType());

        // Assert.assertEquals(conditions.size(),
        // nurseryWorkbook.getConditions().size());
        Assert.assertEquals(constants.size(), nurseryWorkbook.getConstants().size());
        // Assert.assertEquals(factors.size(),
        // nurseryWorkbook.getFactors().size());
        Assert.assertEquals(variates.size(), nurseryWorkbook.getVariates().size());

        return nurseryStudyId;
    }

    private MeasurementVariable createMeasurementVariable(final int termId, final String name, final String description,
                                                          final String property, final String method, final String scale, final String dataType, final String value,
                                                          final String label, final PhenotypicType role, final boolean isFactor) {

        final MeasurementVariable variable = new MeasurementVariable();

        variable.setTermId(termId);
        variable.setName(name);
        variable.setDescription(description);
        variable.setProperty(property);
        variable.setMethod(method);
        variable.setScale(scale);
        variable.setDataType(dataType);
        variable.setValue(value);
        variable.setLabel(label);
        variable.setFactor(isFactor);
        variable.setRole(role);

        return variable;
    }

    public void setGermplasmListManager(final GermplasmListManager germplasmListManager) {
        this.germplasmListManager = germplasmListManager;
    }

    public void setDataImportService(final DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    public void setMiddlewareFieldbookService(final FieldbookService middlewareFieldbookService) {
        this.middlewareFieldbookService = middlewareFieldbookService;
    }

}
