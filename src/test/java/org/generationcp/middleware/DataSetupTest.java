package org.generationcp.middleware;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DataSetupTest extends ServiceIntegraionTest {

	private static CVTermDao cvTermDao;
	private static CVDao cvDao;
	
	private static DataImportService dataImportService;
	
	@BeforeClass
	public static void setUp() {
		cvTermDao = new CVTermDao();
		cvDao = new CVDao();
		cvTermDao.setSession(centralSessionUtil.getCurrentSession());
		cvDao.setSession(centralSessionUtil.getCurrentSession());
		
		dataImportService = serviceFactory.getDataImportService();
	}
	
	@Test
	@Ignore
	public void testAddCVTerm() throws MiddlewareQueryException {
		
		CV cv = cvDao.getById(1000);	
		CVTerm term = new CVTerm();		
		term.setCv(cv.getCvId());
		int randomInt = new Random().nextInt();
		term.setName("Test CV Term " + randomInt);
		term.setDefinition("Test CV Term Definition " + randomInt);
		term.setCvTermId(cvTermDao.getPositiveId("cvTermId"));
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);
		
		Transaction tx = centralSessionUtil.getCurrentSession().beginTransaction();
		cvTermDao.save(term);
		tx.commit();
	}
	
    @Test
    public void testCreateNursery() throws MiddlewareQueryException {
        
    	Workbook workbook = new Workbook();
    	int randomInt = new Random().nextInt(1000);
    	
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
    			WorkbookTest.PROP_BREEDING_METHOD, WorkbookTest.APPLIED, "BMETH_CODE", WorkbookTest.CHAR, 
    			null, WorkbookTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(8080, "STUDY_INSTITUTE", "Study institute - conducted (DBCV)", 
    			WorkbookTest.PROP_INSTITUTE, WorkbookTest.CONDUCTED, WorkbookTest.DBCV, WorkbookTest.CHAR, 
    			"CIMMYT", WorkbookTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.STUDY_NAME.getId(), "STUDY_NAME", "Study - assigned (DBCV)", 
    			WorkbookTest.PROP_STUDY, WorkbookTest.ASSIGNED, WorkbookTest.DBCV, WorkbookTest.CHAR, 
    			studyDetails.getStudyName(), WorkbookTest.STUDY, TermId.STUDY_NAME_STORAGE.getId(), true));

    	conditions.add(createMeasurementVariable(TermId.STUDY_TITLE.getId(), "STUDY_TITLE", "Study title - assigned (text)", 
    			WorkbookTest.PROP_STUDY_TITLE, WorkbookTest.ASSIGNED, WorkbookTest.SCALE_TEXT, WorkbookTest.CHAR, 
    			studyDetails.getTitle(), WorkbookTest.STUDY, TermId.STUDY_TITLE_STORAGE.getId(), true));

    	conditions.add(createMeasurementVariable(TermId.START_DATE.getId(), "START_DATE", "Start date - assigned (date)", 
    			WorkbookTest.PROP_START_DATE, WorkbookTest.ASSIGNED, WorkbookTest.DATE, WorkbookTest.CHAR, 
    			studyDetails.getStartDate(), WorkbookTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.STUDY_OBJECTIVE.getId(), "STUDY_OBJECTIVE", "Objective - described (text)", 
    			WorkbookTest.PROP_OBJECTIVE, WorkbookTest.DESCRIBED, WorkbookTest.SCALE_TEXT, WorkbookTest.CHAR, 
    			studyDetails.getObjective(), WorkbookTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	conditions.add(createMeasurementVariable(TermId.END_DATE.getId(), "END_DATE", "End date - assigned (date)", 
    			WorkbookTest.PROP_END_DATE, WorkbookTest.ASSIGNED, WorkbookTest.DATE, WorkbookTest.CHAR, 
    			studyDetails.getEndDate(), WorkbookTest.STUDY, TermId.STUDY_INFORMATION.getId(), true));
    	
    	workbook.setConditions(conditions);
    	
    	//Constants
    	List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();
    	constants.add(createMeasurementVariable(8270, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",  
    			"Soil acidity", "Ph meter", "pH", 
    			WorkbookTest.NUMERIC, "7", WorkbookTest.STUDY, TermId.OBSERVATION_VARIATE.getId(), false));
    	workbook.setConstants(constants);
    	
    	
    	//Factors
    	List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
    	factors.add(createMeasurementVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO", "Germplasm entry - enumerated (number)", 
    			"Germplasm entry", WorkbookTest.ENUMERATED, WorkbookTest.NUMBER, 
    			WorkbookTest.NUMERIC, null, WorkbookTest.ENTRY, TermId.ENTRY_NUMBER_STORAGE.getId(), true));
    	
    	factors.add(createMeasurementVariable(TermId.DESIG.getId(), "DESIGNATION", "Germplasm identifier - assigned (DBCV)", 
    			"Germplasm id", WorkbookTest.ASSIGNED, WorkbookTest.DBCV, 
    			WorkbookTest.CHAR, null, WorkbookTest.ENTRY, TermId.ENTRY_DESIGNATION_STORAGE.getId(), true));
    	
    	factors.add(createMeasurementVariable(TermId.CROSS.getId(), "CROSS", "The pedigree string of the germplasm", 
    			"Cross history", WorkbookTest.ASSIGNED, WorkbookTest.PEDIGREE_STRING, 
    			WorkbookTest.CHAR, null, WorkbookTest.ENTRY, TermId.GERMPLASM_ENTRY_STORAGE.getId(), true));
    	
    	factors.add(createMeasurementVariable(TermId.GID.getId(), "GID", "Germplasm identifier - assigned (DBID)", 
    			"Germplasm id", WorkbookTest.ASSIGNED, WorkbookTest.DBID, 
    			WorkbookTest.NUMERIC, null, WorkbookTest.ENTRY, TermId.ENTRY_GID_STORAGE.getId(), true));
    	
    	factors.add(createMeasurementVariable(TermId.PLOT_NO.getId(), "PLOT_NO", "Field plot - enumerated (number)", 
    			"Field plot", WorkbookTest.ENUMERATED, WorkbookTest.NUMBER, 
    			WorkbookTest.NUMERIC, null, WorkbookTest.PLOT, TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), true));
    	workbook.setFactors(factors);
    	
    	//Variates
    	List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
    	variates.add(createMeasurementVariable(20316, "EH", "Height between the base of a plant to the insertion of the top (uppermost) ear of the same plant in centimeter (cm).", 
    			"Ear height", WorkbookTest.MEASURED, WorkbookTest.CM, WorkbookTest.NUMERIC, 
    			null, WorkbookTest.PLOT, TermId.OBSERVATION_VARIATE.getId(), false));
    	
    	workbook.setVariates(variates);
    	    	
        int id = dataImportService.saveDataset(workbook, true, false);
        Debug.print(studyDetails.getStudyName() + " created with id = " + id);
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

}
