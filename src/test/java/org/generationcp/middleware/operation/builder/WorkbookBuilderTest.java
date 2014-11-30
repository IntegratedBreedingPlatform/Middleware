/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import java.util.List;

import org.generationcp.middleware.ServiceIntegraionTest;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.etl.WorkbookTest;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookBuilderTest extends ServiceIntegraionTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilderTest.class);
    
    private static DataImportService dataImportService;
    
    private static FieldbookService fieldbookService;
    
    private static WorkbookBuilder workbookBuilder;
    
    @BeforeClass
	public static void setUp() throws Exception {
    	workbookBuilder = new WorkbookBuilder(Mockito.mock(HibernateSessionProvider.class));
    	dataImportService = serviceFactory.getDataImportService();
    	fieldbookService = serviceFactory.getFieldbookService();
    }
 
    @Test
    public void testCheckingOfMeasurementDatasetWithError() {
    	try {
    		workbookBuilder.checkMeasurementDataset(null);
    	} catch (MiddlewareQueryException e) {
    		LOG.error(e.getMessage(), e);
    		Assert.assertEquals("Expected code ", ErrorCode.STUDY_FORMAT_INVALID.getCode(), e.getCode());
    	}
    }
    
    @Test
    public void testCheckingOfMeasurementDatasetWithoutError() {
    	boolean hasError = false;
    	try {
    		workbookBuilder.checkMeasurementDataset(1);
    	} catch (MiddlewareQueryException e) {
    		LOG.error(e.getMessage(), e);
    		Assert.fail("Expected no error but got one");
    		hasError = true;
    	}
    	Assert.assertFalse("Expected no error but got one", hasError);
    }
    
    @Test
    public void testGetTrialObservationsForNursery() throws MiddlewareQueryException {
    	WorkbookTest.setTestWorkbook(null);
    	Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.N);
    	
    	int id = dataImportService.saveDataset(workbook);
    	
    	Workbook createdWorkbook = fieldbookService.getNurseryDataSet(id);
    	
    	Assert.assertTrue("Expected correct values for constants but did not match with old workbook.", 
    			areConstantsMatch(workbook.getConstants(), createdWorkbook.getConstants()));
    	
    }
	
    private boolean areConstantsMatch(List<MeasurementVariable> constants,
			List<MeasurementVariable> constants2) {
    	if (constants != null && constants2 != null) {
    		for (MeasurementVariable var : constants) {
    			if (!isMactchInNewConstantList(constants2, var.getTermId(), var.getValue())) {
    				return false;
    			}
    		}
    	}
    	return true;
	}

	private boolean isMactchInNewConstantList(List<MeasurementVariable> constants, int termId,
			String value) {
		if (constants != null) {
			for (MeasurementVariable var : constants) {
				if (var.getTermId() == termId) {
					return var.getValue().equals(value);
				}
			}
		}
		return false;
	}

	@Test
    public void testGetTrialObservationsForTrial() throws MiddlewareQueryException {
		WorkbookTest.setTestWorkbook(null);
		Workbook workbook = WorkbookTest.getTestWorkbook(10, StudyType.T);
    	
    	int id = dataImportService.saveDataset(workbook);
    	
    	Workbook createdWorkbook = fieldbookService.getTrialDataSet(id);
    	
    	Assert.assertTrue("Expected correct values for trial observations but did not match with old workbook.", 
    			areConstantsCorrect(createdWorkbook.getConstants(), createdWorkbook.getTrialObservations()));
    }

	private boolean areConstantsCorrect(List<MeasurementVariable> constants, List<MeasurementRow> trialObservations) {
		if (trialObservations != null && constants != null) {
			for (MeasurementRow row : trialObservations) {
				return areConstantsInRow(row.getDataList(), constants); 
			}
		}
		return false;
	}

	private boolean areConstantsInRow(List<MeasurementData> dataList,
			List<MeasurementVariable> constants) {
		for (MeasurementVariable var : constants) {
			for (MeasurementData data : dataList) {
				if (data.getMeasurementVariable().getTermId() == var.getTermId() && !data.getValue().equals("1")) {
					return false;
				}
			}
		}
		return true;
	}

}
