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
package org.generationcp.middleware.utils.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;

/**
 * @author Joyce Avestro
 *
 */
public class FieldMapDataUtil{
	
	private static final int FIELDBOOK_ID = 100;
	private static final String FIELDBOOK_NAME = "test fieldbook";
	
	public static final int DATASET_ID = 1;
	private static final String DATASET_NAME = "test-trial-PLOT";
	
	//data for TrialInstanceInfo
	public static final int BLOCK_ID = -1;
    private static final String BLOCK_NAME = "block";
    private static final int ENTRY_COUNT = 1;
    private static final boolean HAS_FIELDMAP = true;
    private static final String TRIAL_INSTANCE_NO = "1";
    private static final String FIELD_NAME = "field";
    
    //data for FieldMapInfo
    private static final int BLOCK_NO = 1;
    private static final int COLUMN = 1;
    private static final int ENTRY_NUMBER = 1;
    private static final String GERMPLASM_NAME = "CIMCAL1";
    private static final String PLOT_COORDINATE = "col 1 range 1";
    private static final int RANGE = 1;
    private static final String STUDY_NAME = "labelPrintingTest";
    
    public static final int ROWS_IN_BLOCK = 10;
    public static final int RANGES_IN_BLOCK = 10;
    public static final int NUMBER_OF_ROWS_IN_PLOT = 2;
    public static final int PLANTING_ORDER = 0;
    public static final int MACHINE_ROW_CAPACITY = 2;
    public static final int FIELD_ID = -2;
    
	public static List<FieldMapInfo> createFieldMapInfoList(boolean isTrial) {
    	List<FieldMapInfo> fieldMapInfoList = new ArrayList<FieldMapInfo>();
		FieldMapInfo fieldMapInfo = new FieldMapInfo();
		
		ArrayList<FieldMapDatasetInfo> datasets = createFieldMapDatasetInfo(isTrial);
		fieldMapInfo.setDatasets(datasets);
		fieldMapInfo.setFieldbookId(FIELDBOOK_ID);
		fieldMapInfo.setFieldbookName(FIELDBOOK_NAME);
		fieldMapInfo.setTrial(isTrial);
		fieldMapInfoList.add(fieldMapInfo);
		
		return fieldMapInfoList;
	}
	
	private static ArrayList<FieldMapDatasetInfo> createFieldMapDatasetInfo(boolean isTrial) {
    	ArrayList<FieldMapDatasetInfo> datasets = new ArrayList<FieldMapDatasetInfo>();
    	
    	FieldMapDatasetInfo dataset = new FieldMapDatasetInfo();
    	
    	ArrayList<FieldMapTrialInstanceInfo> trialInstances =  new ArrayList<FieldMapTrialInstanceInfo>();
    	trialInstances.add(createFieldMapTrialInstanceInfo());

    	dataset.setDatasetId(DATASET_ID);
    	dataset.setDatasetName(DATASET_NAME);
    	dataset.setTrialInstances(trialInstances);
    	datasets.add(dataset);
    	
    	return datasets;
    }
	
	private static FieldMapTrialInstanceInfo createFieldMapTrialInstanceInfo() {
    	FieldMapTrialInstanceInfo trialInstanceInfo = new FieldMapTrialInstanceInfo();
    	
    	trialInstanceInfo.setBlockId(BLOCK_ID);
    	trialInstanceInfo.setBlockName(BLOCK_NAME);
    	trialInstanceInfo.setEntryCount(ENTRY_COUNT);
    	trialInstanceInfo.setFieldMapLabels(createFieldMapLabels());
    	trialInstanceInfo.setHasFieldMap(HAS_FIELDMAP);
    	trialInstanceInfo.setTrialInstanceNo(TRIAL_INSTANCE_NO);
    	trialInstanceInfo.setFieldName(FIELD_NAME);
    	
    	return trialInstanceInfo;
    }
	
	private static List<FieldMapLabel> createFieldMapLabels() {
    	List<FieldMapLabel> labels = new ArrayList<FieldMapLabel>();
    	
    	FieldMapLabel label = new FieldMapLabel();
    	label.setBlockNo(BLOCK_NO);
    	label.setColumn(COLUMN);
    	label.setDatasetId(DATASET_ID);
    	label.setEntryNumber(ENTRY_NUMBER);
    	label.setGermplasmName(GERMPLASM_NAME);
    	label.setPlotCoordinate(PLOT_COORDINATE);
    	label.setRange(RANGE);
    	label.setStudyName(STUDY_NAME);
    	labels.add(label);
    	
    	return labels;
    }
}
