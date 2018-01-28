package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;

public class DataSetTestDataInitializer {

	private static final int TRIAL_DATASET_ID = 2;
	private static final int MEASUREMENT_DATASET_ID = 3;
	private static final int MEANS_DATASET_ID = 4;

	public static List<DataSet> createTrialDatasetsTestData(final String datasetName) {
		final List<DataSet> trialDatasets = new ArrayList<>();
		trialDatasets.add(DataSetTestDataInitializer.createTrialDatasetTestData(datasetName));
		return trialDatasets;
	}

	public static DataSet createTrialDatasetTestData(final String datasetName) {
		final DataSet trialDataset = new DataSet();
		trialDataset.setName(datasetName);
		trialDataset.setDescription(datasetName);
		trialDataset.setDataSetType(DataSetType.SUMMARY_DATA);
		trialDataset.setId(DataSetTestDataInitializer.TRIAL_DATASET_ID);
		trialDataset.setVariableTypes(VariableTypeListTestDataInitializer.createTrialVariableTypesTestData());
		return trialDataset;
	}

	public static List<DataSet> createPlotDatasetsTestData(final String datasetName) {
		final List<DataSet> plotDatasets = new ArrayList<>();
		plotDatasets.add(DataSetTestDataInitializer.createPlotDatasetTestData(datasetName, false));
		return plotDatasets;
	}

	public static DataSet createPlotDatasetTestData(final String datasetName, final boolean isTrial) {
		final DataSet plotDataset = new DataSet();
		plotDataset.setName(datasetName);
		plotDataset.setDescription(datasetName);
		plotDataset.setDataSetType(DataSetType.PLOT_DATA);
		if (isTrial) {
			plotDataset.setId(DataSetTestDataInitializer.TRIAL_DATASET_ID);
			plotDataset.setVariableTypes(VariableTypeListTestDataInitializer.createTrialVariableTypesTestData());
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
		meansDataset.setDataSetType(DataSetType.MEANS_DATA);
		meansDataset.setVariableTypes(VariableTypeListTestDataInitializer.createMeansVariableTypesTestData());
		return meansDataset;
	}
}
