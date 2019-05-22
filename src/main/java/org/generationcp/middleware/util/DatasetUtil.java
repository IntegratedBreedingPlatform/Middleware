
package org.generationcp.middleware.util;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DatasetType;

import java.util.List;

public class DatasetUtil {

	public static final String OLD_PLOT_DATASET_NAME_PREFIX = "MEASUREMENT EFEC";
	public static final String OLD_PLOT_DATASET_NAME_SUFFIX = "-MEASUREMENT";
	public static final String NEW_PLOT_DATASET_NAME_SUFFIX = "-PLOTDATA";

	public static final String OLD_ENVIRONMENT_DATASET_NAME_PREFIX = "TRIAL_";
	public static final String OLD_ENVIRONMENT_DATASET_NAME_SUFFIX = "-TRIAL";
	public static final String
		NEW_ENVIRONMENT_DATASET_NAME_SUFFIX = "-ENVIRONMENT";

	private DatasetUtil() {
		// a utility class should not have a public constructor
	}

	public static DataSet getTrialDataSet(final StudyDataManager studyDataManager, final int studyId) {
		final List<DataSet> summaryDatasets = studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (summaryDatasets == null || summaryDatasets.isEmpty()) {
			final List<DataSet> plotDatasets = studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
			for (final DataSet dataSet : plotDatasets) {
				final String name = dataSet.getName();
				if (name != null
					&& (name.startsWith(DatasetUtil.OLD_ENVIRONMENT_DATASET_NAME_PREFIX) || name
					.endsWith(DatasetUtil.NEW_ENVIRONMENT_DATASET_NAME_SUFFIX))) {
					return dataSet;
				} else if ((name == null || name != null && !name.startsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_PREFIX) && !name
					.endsWith(DatasetUtil.NEW_PLOT_DATASET_NAME_SUFFIX)) && (dataSet != null
					&& dataSet.getVariableTypes().getVariableTypes() != null)) {

					boolean aTrialDataset = true;
					for (final DMSVariableType variableType : dataSet.getVariableTypes().getVariableTypes()) {
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
		} else {
			return summaryDatasets.get(0);
		}
		return null;
	}

	public static DataSet getMeansDataSet(final StudyDataManager studyDataManager, final int studyId) {
		return studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.MEANS_DATA.getId()).get(0);
	}

	public static Integer getPlotDataSetId(final StudyDataManager studyDataManager, final int studyId) {
		final DataSet plotDataset = getPlotDataSet(studyDataManager, studyId);
		if (plotDataset != null) {
			return plotDataset.getId();
		}
		return null;
	}

	public static DataSet getPlotDataSet(final StudyDataManager studyDataManager, final int studyId) {
		final List<DataSet> plotDatasets = studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		if (plotDatasets == null) {
			return null;
		}
		if (plotDatasets.size() == 1) {
			return plotDatasets.get(0);
		}
		for (final DataSet dataSet : plotDatasets) {
			final String name = dataSet.getName();
			if (name != null
				&& (name.startsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_PREFIX) || name
				.endsWith(DatasetUtil.NEW_PLOT_DATASET_NAME_SUFFIX))) {
				return dataSet;
			} else if ((name == null || name != null && !name.startsWith(DatasetUtil.OLD_ENVIRONMENT_DATASET_NAME_PREFIX) && !name
				.endsWith(DatasetUtil.NEW_ENVIRONMENT_DATASET_NAME_SUFFIX)) && (dataSet != null
				&& dataSet.getVariableTypes().getVariableTypes() != null)) {

				boolean aPlotDataset = false;
				for (final DMSVariableType variableType : dataSet.getVariableTypes().getVariableTypes()) {
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
		return null;
	}

}
