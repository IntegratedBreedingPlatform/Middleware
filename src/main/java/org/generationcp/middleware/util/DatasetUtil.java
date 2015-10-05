
package org.generationcp.middleware.util;

import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;

public class DatasetUtil {

	public final static String OLD_PLOT_DATASET_NAME_PREFIX = "MEASUREMENT EFEC_";
	public final static String NEW_PLOT_DATASET_NAME_SUFFIX = "-PLOTDATA";
	public final static String OLD_SUMMARY_DATASET_NAME_PREFIX = "TRIAL_";
	public final static String NEW_SUMMARY_DATASET_NAME_SUFFIX = "-ENVIRONMENT";

	public static DataSet getTrialDataSet(StudyDataManager studyDataManager, int studyId) throws MiddlewareException {
		List<DataSet> summaryDatasets = studyDataManager.getDataSetsByType(studyId, DataSetType.SUMMARY_DATA);
		if (summaryDatasets == null || summaryDatasets.isEmpty()) {
			List<DataSet> plotDatasets = studyDataManager.getDataSetsByType(studyId, DataSetType.PLOT_DATA);
			for (DataSet dataSet : plotDatasets) {
				String name = dataSet.getName();
				if (name != null
						&& (name.startsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_PREFIX) || name
								.endsWith(DatasetUtil.NEW_PLOT_DATASET_NAME_SUFFIX))) {
					continue;
				} else if (name != null
						&& (name.startsWith(DatasetUtil.OLD_SUMMARY_DATASET_NAME_PREFIX) || name
								.endsWith(DatasetUtil.NEW_SUMMARY_DATASET_NAME_SUFFIX))) {
					return dataSet;
				} else {
					if (dataSet != null && dataSet.getVariableTypes().getVariableTypes() != null) {
						boolean aTrialDataset = true;
						for (DMSVariableType variableType : dataSet.getVariableTypes().getVariableTypes()) {
							if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
								aTrialDataset = false;
								break;
							}
						}
						if (aTrialDataset) {
							return dataSet;
						}
					}
				}
			}
		} else {
			return summaryDatasets.get(0);
		}
		return null;
	}

	public static DataSet getMeansDataSet(StudyDataManager studyDataManager, int studyId) throws MiddlewareException {
		return studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA).get(0);
	}

	public static Integer getPlotDataSetId(StudyDataManager studyDataManager, int studyId) {
		DataSet plotDataset = getPlotDataSet(studyDataManager, studyId);
		if (plotDataset != null) {
			return plotDataset.getId();
		}
		return null;
	}

	public static DataSet getPlotDataSet(StudyDataManager studyDataManager, int studyId) {
		List<DataSet> plotDatasets = studyDataManager.getDataSetsByType(studyId, DataSetType.PLOT_DATA);
		if (plotDatasets == null) {
			return null;
		}
		if (plotDatasets.size() == 1) {
			return plotDatasets.get(0);
		}
		for (DataSet dataSet : plotDatasets) {
			String name = dataSet.getName();
			if (name != null
					&& (name.startsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_PREFIX) || name
							.endsWith(DatasetUtil.NEW_PLOT_DATASET_NAME_SUFFIX))) {
				return dataSet;
			} else if (name == null || name != null && !name.startsWith(DatasetUtil.OLD_SUMMARY_DATASET_NAME_PREFIX)
					&& !name.endsWith(DatasetUtil.NEW_SUMMARY_DATASET_NAME_SUFFIX)) {
				if (dataSet != null && dataSet.getVariableTypes().getVariableTypes() != null) {
					boolean aPlotDataset = false;
					for (DMSVariableType variableType : dataSet.getVariableTypes().getVariableTypes()) {
						if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
							aPlotDataset = true;
							break;
						}
					}
					if (aPlotDataset) {
						return dataSet;
					}
				}
			}
		}
		return null;
	}

}
