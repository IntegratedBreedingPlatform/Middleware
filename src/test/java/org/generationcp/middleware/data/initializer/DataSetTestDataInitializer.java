package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.ArrayList;
import java.util.List;

public class DataSetTestDataInitializer {

	private static final int STUDY_DATASET_ID = 2;
	private static final int MEASUREMENT_DATASET_ID = 3;
	private static final int MEANS_DATASET_ID = 4;

	public static DataSet createStudyDatasetTestData(final String datasetName) {

		final DataSet dataSet = new DataSet();
		dataSet.setName(datasetName);
		dataSet.setDescription(datasetName);
		dataSet.setDatasetType(new DatasetType(DatasetTypeEnum.SUMMARY_DATA.getId()));
		dataSet.setId(DataSetTestDataInitializer.STUDY_DATASET_ID);
		dataSet.setVariableTypes(VariableTypeListTestDataInitializer.createStudyVariableTypesTestData());
		return dataSet;
	}

	public static DataSet createPlotDatasetTestData(final String datasetName) {
		return DataSetTestDataInitializer.createPlotDatasetTestData(datasetName, false);
	}

	public static DataSet createPlotDatasetTestData(final String datasetName, final boolean isStudy) {

		final DataSet plotDataset = new DataSet();
		plotDataset.setName(datasetName);
		plotDataset.setDescription(datasetName);
		plotDataset.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		if (isStudy) {
			plotDataset.setId(DataSetTestDataInitializer.STUDY_DATASET_ID);
			plotDataset.setVariableTypes(VariableTypeListTestDataInitializer.createStudyVariableTypesTestData());
		} else {
			plotDataset.setVariableTypes(VariableTypeListTestDataInitializer.createPlotVariableTypesTestData());
			plotDataset.setId(DataSetTestDataInitializer.MEASUREMENT_DATASET_ID);
		}
		return plotDataset;
	}

	public static DataSet createMeansDatasetTestData(final String datasetName) {

		final DataSet meansDataset = new DataSet();
		meansDataset.setId(DataSetTestDataInitializer.MEANS_DATASET_ID);
		meansDataset.setName(datasetName);
		meansDataset.setDescription(datasetName);
		meansDataset.setDatasetType(new DatasetType(DatasetTypeEnum.MEANS_DATA.getId()));
		meansDataset.setVariableTypes(VariableTypeListTestDataInitializer.createMeansVariableTypesTestData());
		return meansDataset;
	}
}
