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
package org.generationcp.middleware.operation.saver;

import static org.junit.Assert.*;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.generationcp.middleware.utils.test.VariableTypeListDataUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class WorkbookSaverTest extends TestOutputFormatter {
    
    private static WorkbookSaver workbookSaver;
    
    @BeforeClass
    public static void setUp() {
    	workbookSaver = new WorkbookSaver(Mockito.mock(HibernateSessionProvider.class));
    }
        
    @Test
    public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWithEnvironmentAndVariates() {
    	VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
    	VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(true);

    	VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
    	
    	assertEquals("Expected an aditional entry for trial instance but found none.", 
    			effectVariables.size()+1, plotVariables.size());
    	assertFalse("Expected non trial environment and non constant variables but found at least one.", areTrialAndConstantsInList(plotVariables, effectVariables));
    }
    
    private boolean areTrialAndConstantsInList(VariableTypeList plotVariables, VariableTypeList effectVariables) {		
		if (plotVariables != null) {
			for (VariableType var : plotVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() != TermId.TRIAL_INSTANCE_FACTOR.getId() && 
					(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(
						Integer.valueOf(var.getStandardVariable().getStoredIn().getId())) || 
					(PhenotypicType.VARIATE.getTypeStorages().contains(
						Integer.valueOf(var.getStandardVariable().getStoredIn().getId())) &&
						!isInOriginalPlotDataset(var.getStandardVariable().getId(), effectVariables)))) {
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean isInOriginalPlotDataset(int id, VariableTypeList effectVariables) {
		if (effectVariables != null) {
			for (VariableType var : effectVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() == id) {
					return true;
				}
			}
		}
		return false;
	}

	@Test
    public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWOEnvironmentAndVariates() {
    	VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
    	VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);
    	
    	VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
    	
    	assertEquals("Expected an aditional entry for trial instance but found none.",
    			effectVariables.size()+1, plotVariables.size());
    	assertFalse("Expected non trial environment and non constant variables but found at least one.", 
    			areTrialAndConstantsInList(plotVariables, effectVariables));
    }
    
    @Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndTrialFactor() {
    	VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
    	VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);
    	
    	VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
    	
    	assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
    }
    
    @Test
    public void testPropagationOfTrialFactorsWOTrialVariablesWithTrialFactor() {
    	VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
    	VariableTypeList trialVariables = null;
    	
    	VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
    	
    	assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
    }
    
    @Test
    public void testPropagationOfTrialFactorsWOTrialVariablesAndTrialFactor() {
    	VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
    	VariableTypeList trialVariables = null;
    	
    	VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
    	
    	assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
    }
    
}
