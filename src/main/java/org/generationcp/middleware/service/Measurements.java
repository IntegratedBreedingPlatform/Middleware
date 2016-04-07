
package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.operation.saver.PhenotypeOutlierSaver;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.google.common.base.Preconditions;

/**
 * Class to enable us to save data to the phenotype table, phenotype_outlier table and the nd experiment phenotype table in a performant
 * manner.
 * 
 */
public class Measurements {

	private final PhenotypeSaver phenotypeSaver;
	private final PhenotypeOutlierSaver phenotypeOutlierSaver;
	private final Session session;

	Measurements(final Session session, final PhenotypeSaver phenotypeSaver, final PhenotypeOutlierSaver phenotypeOutlierSaver) {
		this.session = session;
		this.phenotypeSaver = phenotypeSaver;
		this.phenotypeOutlierSaver = phenotypeOutlierSaver;
	}

	/**
	 * @param observations list of observations to save
	 */
	void saveMeasurements(final List<MeasurementRow> observations) {
		// Changing the fulsh mode will have huge performance implications.
		// Please be careful when doing this. The current stratergy below
		// facilitates batch inserts.
		final FlushMode originalFlushMode = this.session.getFlushMode();
		try {
			Preconditions.checkNotNull(observations, "Observation list passed in must cannot be null ");
			this.session.setFlushMode(FlushMode.MANUAL);
			this.saveMeasurementData(observations);
			this.saveOutliers(observations);
			this.session.flush();
		} finally {
			if (this.session != null) {
				this.session.setFlushMode(originalFlushMode);
			}
		}
	}

	/**
	 * Saves the old value of the measurements that are marked as "missing" to the Phenotype_Outlier table.
	 * 
	 * @param observations
	 */
	void saveOutliers(List<MeasurementRow> observations) {

		for (final MeasurementRow measurementRow : observations) {
			final List<MeasurementData> dataList = measurementRow.getDataList();

			if (dataList == null || dataList.isEmpty()) {
				continue;
			}

			List<PhenotypeOutlier> outlierList = new ArrayList<PhenotypeOutlier>();
			for (final MeasurementData measurementData : dataList) {

				// When a measurement is marked as missing, we should log its old value in the phenotype_outlier table.
				// Add a log ONLY if the data has changed.
				if (MeasurementData.MISSING_VALUE.equals(measurementData.getValue())
						&& !measurementData.getValue().equals(measurementData.getOldValue())) {

					outlierList.add(this.createPhenotypeOutlierFromMeasurement(measurementData));
				}

				// After saving, the new value now becomes the old value, this will be piped back to the UI.
				measurementData.setOldValue(measurementData.getValue());

			}

			this.phenotypeOutlierSaver.savePhenotypeOutliers(outlierList);

		}
	}

	PhenotypeOutlier createPhenotypeOutlierFromMeasurement(final MeasurementData measurementData) {

		PhenotypeOutlier phenotypeOutlier = new PhenotypeOutlier();
		phenotypeOutlier.setPhenotypeId(measurementData.getPhenotypeId());
		phenotypeOutlier.setValue(measurementData.getOldValue());
		return phenotypeOutlier;

	}

	/**
	 * 
	 * @param measurementData measurementData used to create your {@link Phenotype} object that can be saved
	 */
	Phenotype createPhenotypeFromMeasurement(final MeasurementData measurementData) {

		final Phenotype phenotype = new Phenotype();

		phenotype.setValue(measurementData.getValue());
		final int cValue = NumberUtils.toInt(measurementData.getcValueId());
		if (cValue != 0 && measurementData.getMeasurementVariable().getDataTypeId() == TermId.CATEGORICAL_VARIABLE.getId()) {
			phenotype.setcValue(cValue);
		}
		phenotype.setName(measurementData.getLabel());

		final int observableId = NumberUtils.toInt(measurementData.getDataType());
		if (observableId != 0) {
			phenotype.setObservableId(observableId);
		}

		final Integer phenotypeId = measurementData.getPhenotypeId();
		if (phenotypeId != null && phenotypeId != 0) {
			phenotype.setPhenotypeId(phenotypeId);
		}

		return phenotype;

	}

	/**
	 * @param observations list of observations to save
	 */
	void saveMeasurementData(final List<MeasurementRow> observations) {
		// save variates
		for (final MeasurementRow measurementRow : observations) {
			final List<MeasurementData> dataList = measurementRow.getDataList();
			if (dataList == null || dataList.isEmpty()) {
				continue;
			}
			for (final MeasurementData measurementData : dataList) {

				// TODO Change the UI so that we are never send back any data
				if (!measurementData.isEditable() || (measurementData.getPhenotypeId() == null || measurementData.getPhenotypeId() == 0)
						&& StringUtils.isBlank(measurementData.getcValueId()) && StringUtils.isBlank(measurementData.getValue())
						|| measurementData.getMeasurementVariable().getRole() != PhenotypicType.VARIATE) {
					continue;
				}
				final MeasurementVariable measurementVariable = measurementData.getMeasurementVariable();
				Preconditions.checkNotNull(measurementVariable, "The sky is falling. Measurement variable is null. "
						+ "Defenesive this should never happen.");

				final Phenotype phenotype = this.createPhenotypeFromMeasurement(measurementData);

				this.phenotypeSaver.saveOrUpdate(measurementRow.getExperimentId(), measurementVariable.getTermId(),
						measurementData.getcValueId() != null && !"".equals(measurementData.getcValueId()) ? measurementData.getcValueId()
								: measurementData.getValue(), phenotype, measurementData.getMeasurementVariable().getDataTypeId());
				// This is not great but essential because the workbook
				// object must be updated so that it has new phenotype id. This
				// id is then piped back to the UI and is used in subsequent calls to
				// determine if we need to update or add phenotype values
				measurementData.setPhenotypeId(phenotype.getPhenotypeId());

			}

		}
	}
}
