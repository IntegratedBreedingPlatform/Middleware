
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;

import java.util.List;

public class VariableTypeListTransformer extends Transformer {

	protected HibernateSessionProvider sessionProvider;

	public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal) {
		this.sessionProvider = sessionProviderForLocal;
	}

	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, String programUUID) throws MiddlewareException {

		return this.transform(measurementVariables, 1, programUUID);
	}

	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, int rank, String programUUID)
		throws MiddlewareException {

		VariableTypeList variableTypeList = new VariableTypeList();

		if (measurementVariables != null && !measurementVariables.isEmpty()) {
			for (MeasurementVariable measurementVariable : measurementVariables) {
				StandardVariable standardVariable;

				if (measurementVariable.getTermId() != 0) {// in etl v2, standard variables are already created before saving the study
					standardVariable = this.getStandardVariableBuilder().create(measurementVariable.getTermId(), programUUID);
				} else {
					standardVariable =
						this.getStandardVariableBuilder().findOrSave(
							measurementVariable.getName(),
							measurementVariable.getDescription(),
							measurementVariable.getProperty(),
							measurementVariable.getScale(),
							measurementVariable.getMethod(),
							measurementVariable.getRole(),
							measurementVariable.getVariableType(),
							measurementVariable.getDataType(),
							programUUID);
				}

				measurementVariable.setTermId(standardVariable.getId());

				DMSVariableType dmsVariableType =
					new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, rank++);

				VariableType variableType = measurementVariable.getVariableType();

				if (variableType == null) {
					variableType = OntologyDataHelper.mapFromPhenotype(measurementVariable.getRole(), measurementVariable.getProperty());
				}

				dmsVariableType.setVariableType(variableType);
				standardVariable.setPhenotypicType(variableType.getRole());
				dmsVariableType.setRole(variableType.getRole());
				dmsVariableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
				variableTypeList.add(dmsVariableType);
			}
		}

		return variableTypeList;
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}

}
