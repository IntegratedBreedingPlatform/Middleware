
package org.generationcp.middleware.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.hibernate.Session;

import java.util.List;

/**
 * Class to enable us to save data to the phenotype table, phenotype_outlier table and the nd experiment phenotype table in a performant
 * manner.
 * 
 */
public class Measurements {

	private final PhenotypeSaver phenotypeSaver;
	private final Session session;

	Measurements(final Session session, final PhenotypeSaver phenotypeSaver) {
		this.session = session;
		this.phenotypeSaver = phenotypeSaver;
	}

	PhenotypeOutlier createPhenotypeOutlierFromMeasurement(final MeasurementData measurementData) {

		final PhenotypeOutlier phenotypeOutlier = new PhenotypeOutlier();
		phenotypeOutlier.setPhenotypeId(measurementData.getMeasurementDataId());
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

		final Integer phenotypeId = measurementData.getMeasurementDataId();
		if (phenotypeId != null && phenotypeId != 0) {
			phenotype.setPhenotypeId(phenotypeId);
		}

		phenotype.setValueStatus(measurementData.getValueStatus());

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
				if (!measurementData.isEditable() || (measurementData.getMeasurementDataId() == null || measurementData.getMeasurementDataId() == 0)
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
								: measurementData.getValue(), phenotype, measurementData.getMeasurementVariable().getDataTypeId(), measurementData.getValueStatus());
				// This is not great but essential because the workbook
				// object must be updated so that it has new phenotype id. This
				// id is then piped back to the UI and is used in subsequent calls to
				// determine if we need to update or add phenotype values
				measurementData.setMeasurementDataId(phenotype.getPhenotypeId());

			}

		}
	}
}
