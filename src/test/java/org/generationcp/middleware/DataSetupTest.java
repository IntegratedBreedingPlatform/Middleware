package org.generationcp.middleware;

import java.util.ArrayList;
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
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSetupTest extends ServiceIntegraionTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataSetupTest.class);

	private static DataImportService dataImportService;
	private static GermplasmDataManager germplasmManager;
	private static GermplasmListManager germplasmListManager;
	private static FieldbookService middlewareFieldbookService;
	
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
		dataImportService = serviceFactory.getDataImportService();
		germplasmManager = managerFactory.getGermplasmDataManager();
		germplasmListManager = managerFactory.getGermplasmListManager();
		middlewareFieldbookService = serviceFactory.getFieldbookService();
	}
	
    @Test
    public void testCreateNursery() throws MiddlewareQueryException {
        
		int randomInt = new Random().nextInt(100);
		
		//Germplasm
		Integer[] gids = new Integer[NUMBER_OF_GERMPLASM];
		for (int i = 0; i < NUMBER_OF_GERMPLASM; i++) {
			gids[i] = createGermplasm(GERMPLSM_PREFIX + i);
		}
				
		//Germplasm list
		GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt, Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		Integer germplasmListId = germplasmListManager.addGermplasmList(germplasmList);
		
		//Germplasm list data
        List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();
        for (int i = 0; i < NUMBER_OF_GERMPLASM; i++) {
        	germplasmListData.add(new GermplasmListData(null, germplasmList, gids[i], i, "EntryCode" + i, GERMPLSM_PREFIX + i + " Source", GERMPLSM_PREFIX + i, GERMPLSM_PREFIX + "Group A", 0, 0));
		}
        germplasmListManager.addGermplasmListData(germplasmListData);
        
        
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
    	
    	conditions.add(createMeasurementVariable(TermId.BREEDING_METHOD_CODE.getId(), "STUDY_BM_CODE", "Breeding method applied to all plots in a study (CODE)", 
    			PROP_BREEDING_METHOD, APPLIED, "BMETH_CODE", CHAR, 
    			null, STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(8080, "STUDY_INSTITUTE", "Study institute - conducted (DBCV)", 
    			PROP_INSTITUTE, CONDUCTED, DBCV, CHAR, 
    			"CIMMYT", STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.STUDY_NAME.getId(), "STUDY_NAME", "Study - assigned (DBCV)", 
    			PROP_STUDY, ASSIGNED, DBCV, CHAR, 
    			studyDetails.getStudyName(), STUDY, TermId.STUDY_NAME_STORAGE.getId(), true));

    	conditions.add(createMeasurementVariable(TermId.STUDY_TITLE.getId(), "STUDY_TITLE", "Study title - assigned (text)", 
    			PROP_STUDY_TITLE, ASSIGNED, SCALE_TEXT, CHAR, 
    			studyDetails.getTitle(), STUDY, TermId.STUDY_TITLE_STORAGE.getId(), true));

    	conditions.add(createMeasurementVariable(TermId.START_DATE.getId(), "START_DATE", "Start date - assigned (date)", 
    			PROP_START_DATE, ASSIGNED, DATE, CHAR, 
    			studyDetails.getStartDate(), STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.STUDY_OBJECTIVE.getId(), "STUDY_OBJECTIVE", "Objective - described (text)", 
    			PROP_OBJECTIVE, DESCRIBED, SCALE_TEXT, CHAR, 
    			studyDetails.getObjective(), STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.END_DATE.getId(), "END_DATE", "End date - assigned (date)", 
    			PROP_END_DATE, ASSIGNED, DATE, CHAR, 
    			studyDetails.getEndDate(), STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	workbook.setConditions(conditions);
    	
    	//Constants
    	List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();
    	constants.add(createMeasurementVariable(8270, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",  
    			"Soil acidity", "Ph meter", "pH", 
    			NUMERIC, "7", STUDY, TermId.OBSERVATION_VARIATE.getId(), false));
    	workbook.setConstants(constants);
    	
    	
    	//Factors
    	List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
    	MeasurementVariable entryFactor = createMeasurementVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO", "Germplasm entry - enumerated (number)", 
    			"Germplasm entry", ENUMERATED, NUMBER, 
    			NUMERIC, null, ENTRY, TermId.ENTRY_NUMBER_STORAGE.getId(), true);
		factors.add(entryFactor);
    	
    	MeasurementVariable designationFactor = createMeasurementVariable(TermId.DESIG.getId(), "DESIGNATION", "Germplasm designation - assigned (DBCV)", 
    			"Germplasm Designation", ASSIGNED, DBCV, 
    			CHAR, null, DESIG, TermId.ENTRY_DESIGNATION_STORAGE.getId(), true);
		factors.add(designationFactor);
    	
    	MeasurementVariable crossFactor = createMeasurementVariable(TermId.CROSS.getId(), "CROSS", "The pedigree string of the germplasm", 
    			"Cross history", ASSIGNED, PEDIGREE_STRING, 
    			CHAR, null, CROSS, TermId.GERMPLASM_ENTRY_STORAGE.getId(), true);
		factors.add(crossFactor);
    	
    	MeasurementVariable gidFactor = createMeasurementVariable(TermId.GID.getId(), "GID", "Germplasm identifier - assigned (DBID)", 
    			"Germplasm id", ASSIGNED, DBID, 
    			NUMERIC, null, GID, TermId.ENTRY_GID_STORAGE.getId(), true);
		factors.add(gidFactor);
    	
    	MeasurementVariable plotFactor = createMeasurementVariable(TermId.PLOT_NO.getId(), "PLOT_NO", "Field plot - enumerated (number)", 
    			"Field plot", ENUMERATED, NUMBER, 
    			NUMERIC, null, PLOT, TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), true);
		factors.add(plotFactor);
		
    	workbook.setFactors(factors);
    	
    	//Variates
    	List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
    	MeasurementVariable variate = createMeasurementVariable(18000, "Grain_yield", "Grain yield -dry and weigh (kg/ha)", 
    			GRAIN_YIELD, DRY_AND_WEIGH, KG_HA, NUMERIC, 
                null, PLOT, TermId.OBSERVATION_VARIATE.getId(), false);
		variates.add(variate);
    	
    	workbook.setVariates(variates);

    	// Observations
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		MeasurementRow row;
		List<MeasurementData> dataList;
		for (int i = 0; i < NUMBER_OF_GERMPLASM; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			MeasurementData entryData = new MeasurementData(entryFactor.getLabel(), String.valueOf(i));
			entryData.setMeasurementVariable(entryFactor);
			dataList.add(entryData);
			
			MeasurementData designationData = new MeasurementData(designationFactor.getLabel(), GERMPLSM_PREFIX + i);
			designationData.setMeasurementVariable(designationFactor);
			dataList.add(designationData);
			
			MeasurementData crossData = new MeasurementData(crossFactor.getLabel(), GERMPLSM_PREFIX + i + "MP-" + i + "/" + GERMPLSM_PREFIX + i + "FP-" + i);
			crossData.setMeasurementVariable(crossFactor);
			dataList.add(crossData);
			
			MeasurementData gidData = new MeasurementData(gidFactor.getLabel(),  String.valueOf(gids[i]));
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
        int nurseryStudyId = dataImportService.saveDataset(workbook, true, false, null);
        LOG.info("Nursery " + studyDetails.getStudyName() + " created. ID: " + nurseryStudyId);
        

        // Convert germplasm list we created into ListDataProject entries
        List<ListDataProject> listDataProjects = new ArrayList<ListDataProject>();
        for(GermplasmListData gpListData : germplasmListData) {
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
        int nurseryListId = middlewareFieldbookService.saveOrUpdateListDataProject(nurseryStudyId, GermplasmListType.NURSERY, germplasmListId, listDataProjects, 1);
                
        //Load and check some basics
        Workbook nurseryWorkbook = middlewareFieldbookService.getNurseryDataSet(nurseryStudyId);
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
                
        //Assert.assertEquals(conditions.size(), nurseryWorkbook.getConditions().size());
        Assert.assertEquals(constants.size(), nurseryWorkbook.getConstants().size());
        //Assert.assertEquals(factors.size(), nurseryWorkbook.getFactors().size());
        Assert.assertEquals(variates.size(), nurseryWorkbook.getVariates().size());
        Assert.assertEquals(observations.size(), nurseryWorkbook.getObservations().size());
        
        //Assert list data got saved with Nursery
        List<ListDataProject> listDataProject = middlewareFieldbookService.getListDataProject(nurseryListId);
        Assert.assertEquals(germplasmListData.size(), listDataProject.size());
    }
    
	private MeasurementVariable createMeasurementVariable(int termId, String name, String description, 
			String property, String method, String scale, 
			String dataType, String value, String label, int storedIn, boolean isFactor) {

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
	
	private Integer createGermplasm(String germplasmName) throws MiddlewareQueryException {
		Germplasm g = new Germplasm();
        g.setGdate(Integer.valueOf(20141014));
        g.setGnpgs(Integer.valueOf(0));
        g.setGpid1(Integer.valueOf(0));
        g.setGpid2(Integer.valueOf(0));
        g.setGrplce(Integer.valueOf(0));
        g.setLocationId(Integer.valueOf(1));
        g.setMethodId(Integer.valueOf(1));
        g.setMgid(Integer.valueOf(1));
        g.setUserId(Integer.valueOf(1));
        g.setReferenceId(Integer.valueOf(1));

        Name n = new Name();
        n.setLocationId(Integer.valueOf(1));
        n.setNdate(Integer.valueOf(20141014));
        n.setNval(germplasmName);
        n.setReferenceId(Integer.valueOf(1));
        n.setTypeId(Integer.valueOf(1));
        n.setUserId(Integer.valueOf(1));

        germplasmManager.addGermplasm(g, n);
        
        return g.getGid();
	}
}
