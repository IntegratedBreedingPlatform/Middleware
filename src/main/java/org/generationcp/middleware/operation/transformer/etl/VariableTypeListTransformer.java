
package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class VariableTypeListTransformer extends Transformer {

	public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, String programUUID) throws MiddlewareException {

		return this.transform(measurementVariables, 1, programUUID);
	}

	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, int rank, String programUUID)
			throws MiddlewareException {

		final Monitor monitor = MonitorFactory.start("CreateTrial.bms.middleware.VariableTypeListTransformer.transform");

		try {
			VariableTypeList variableTypeList = new VariableTypeList();

			if (measurementVariables != null && !measurementVariables.isEmpty()) {
				for (MeasurementVariable measurementVariable : measurementVariables) {
					StandardVariable standardVariable;

					if (measurementVariable.getTermId() != 0) {// in etl v2, standard variables are already created before saving the study
						standardVariable = this.getStandardVariableBuilder().create(measurementVariable.getTermId(), programUUID);
					} else {
						standardVariable = this.getStandardVariableBuilder().findOrSave(measurementVariable.getName(),
								measurementVariable.getDescription(), measurementVariable.getProperty(), measurementVariable.getScale(),
								measurementVariable.getMethod(), measurementVariable.getRole(), measurementVariable.getVariableType(),
								measurementVariable.getDataType(), programUUID);
					}

					measurementVariable.setTermId(standardVariable.getId());

					DMSVariableType dmsVariableType = new DMSVariableType(measurementVariable.getName(),
							measurementVariable.getDescription(), standardVariable, rank++);

					VariableType variableType = measurementVariable.getVariableType();

					if (variableType == null) {
						variableType =
								OntologyDataHelper.mapFromPhenotype(measurementVariable.getRole(), measurementVariable.getProperty());
					}

					dmsVariableType.setVariableType(variableType);
					standardVariable.setPhenotypicType(variableType.getRole());
					dmsVariableType.setRole(variableType.getRole());
					dmsVariableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
					variableTypeList.add(dmsVariableType);
				}
			}

			return variableTypeList;
		} finally {
			monitor.stop();
		}
	}
}
