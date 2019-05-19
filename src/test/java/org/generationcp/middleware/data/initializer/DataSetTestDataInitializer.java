package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.ArrayList;
import java.util.List;

public class DataSetTestDataInitializer {

	private static final int STUDY_DATASET_ID = 2;
	private static final int MEASUREMENT_DATASET_ID = 3;
	private static final int MEANS_DATASET_ID = 4;

	public static List<DataSet> createStudyDatasetsTestData(final String datasetName) {
		final List<DataSet> trialDatasets = new ArrayList<>();
		trialDatasets.add(DataSetTestDataInitializer.createStudyDatasetTestData(datasetName));
		return trialDatasets;
	}

	public static DataSet createStudyDatasetTestData(final String datasetName) {

		final DataSet dataSet = new DataSet();
		dataSet.setName(datasetName);
		dataSet.setDescription(datasetName);
		dataSet.setDatasetType(new DatasetType(DatasetType.SUMMARY_DATA));
		dataSet.setId(DataSetTestDataInitializer.STUDY_DATASET_ID);
		dataSet.setVariableTypes(VariableTypeListTestDataInitializer.createStudyVariableTypesTestData());
		return dataSet;
	}

	public static List<DataSet> createPlotDatasetsTestData(final String datasetName) {
		final List<DataSet> plotDatasets = new ArrayList<>();
		plotDatasets.add(DataSetTestDataInitializer.createPlotDatasetTestData(datasetName, false));
		return plotDatasets;
	}

	public static DataSet createPlotDatasetTestData(final String datasetName, final boolean isStudy) {

		final DataSet plotDataset = new DataSet();
		plotDataset.setName(datasetName);
		plotDataset.setDescription(datasetName);
		plotDataset.setDatasetType(new DatasetType(DatasetType.PLOT_DATA));
		if (isStudy) {
			plotDataset.setId(DataSetTestDataInitializer.STUDY_DATASET_ID);
			plotDataset.setVariableTypes(VariableTypeListTestDataInitializer.createStudyVariableTypesTestData());
		} else {
			plotDataset.setVariableTypes(VariableTypeListTestDataInitializer.createPlotVariableTypesTestData());
			plotDataset.setId(DataSetTestDataInitializer.MEASUREMENT_DATASET_ID);
		}
		return plotDataset;
	}

	public static List<DataSet> createMeansDatasetsTestData(final String datasetName) {
		final List<DataSet> meansDataset = new ArrayList<>();
		meansDataset.add(DataSetTestDataInitializer.createMeansDatasetTestData(datasetName));
		return meansDataset;
	}

	public static DataSet createMeansDatasetTestData(final String datasetName) {

		final DataSet meansDataset = new DataSet();
		meansDataset.setId(DataSetTestDataInitializer.MEANS_DATASET_ID);
		meansDataset.setName(datasetName);
		meansDataset.setDescription(datasetName);
		meansDataset.setDatasetType(new DatasetType(DatasetType.MEANS_DATA));
		meansDataset.setVariableTypes(VariableTypeListTestDataInitializer.createMeansVariableTypesTestData());
		return meansDataset;
	}
}
