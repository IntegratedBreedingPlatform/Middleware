
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class MeasurementVariableTransformer extends Transformer {

	public MeasurementVariableTransformer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<MeasurementVariable> transform(final VariableTypeList variableTypeList, final boolean isFactor) {
		return this.transform(variableTypeList, isFactor, false);
	}

	public List<MeasurementVariable> transform(final VariableTypeList variableTypeList, final boolean isFactor, final boolean isTrial) {

		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		if (variableTypeList != null && !variableTypeList.isEmpty()) {
			for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {
				final StandardVariable stdVariable = variableType.getStandardVariable();
				String label = getLabelBasedOnRole(stdVariable.getPhenotypicType());
				if (!isFactor && isTrial) {
					label = PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
				}

				final MeasurementVariable measurementVariable =
						new MeasurementVariable(stdVariable.getId(), variableType.getLocalName(), stdVariable.getDescription(), stdVariable
								.getScale().getName(), stdVariable.getMethod().getName(), stdVariable.getProperty().getName(), stdVariable
								.getDataType().getName(), "", label);
				measurementVariable.setRole(variableType.getRole());
				measurementVariable.setVariableType(variableType.getVariableType());
				measurementVariable.setFactor(isFactor);
				measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
				measurementVariable.setPossibleValues(this.transformPossibleValues(stdVariable.getEnumerations()));
				if (stdVariable.getConstraints() != null) {
					measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
					measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
				}
				if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
					measurementVariable.setTreatmentLabel(variableType.getTreatmentLabel());
				}
				measurementVariables.add(measurementVariable);
			}
		}

		return measurementVariables;
	}

	public List<MeasurementVariable> transform(final VariableList variableList, final boolean isFactor, final boolean isStudy) {

		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		if (variableList != null && !variableList.isEmpty()) {
			for (final Variable variable : variableList.getVariables()) {
				final DMSVariableType variableType = variable.getVariableType();
				final StandardVariable stdVariable = variableType.getStandardVariable();
				String label = getLabelBasedOnRole(stdVariable.getPhenotypicType());
				// for trial constants
				if (!isFactor && !isStudy) {
					label = PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
				}
				final MeasurementVariable measurementVariable =
						new MeasurementVariable(stdVariable.getId(), variableType.getLocalName(), stdVariable.getDescription(), stdVariable
								.getScale().getName(), stdVariable.getMethod().getName(), stdVariable.getProperty().getName(), stdVariable
								.getDataType().getName(), "", label);			
				measurementVariable.setRole(variableType.getRole());
				measurementVariable.setFactor(isFactor);
				measurementVariable.setValue(variable.getDisplayValue());
				measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
				measurementVariable.setPossibleValues(this.transformPossibleValues(stdVariable.getEnumerations()));
				if (stdVariable.getConstraints() != null) {
					measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
					measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
				}
				if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
					measurementVariable.setTreatmentLabel(variableType.getTreatmentLabel());
				}
				measurementVariables.add(measurementVariable);
			}
		}

		return measurementVariables;
	}

	public List<ValueReference> transformPossibleValues(final List<Enumeration> enumerations) {
		final List<ValueReference> list = new ArrayList<ValueReference>();

		if (enumerations != null) {
			for (final Enumeration enumeration : enumerations) {
				final int enumerationId = enumeration.getId() == null ? 0 : enumeration.getId();
				list.add(new ValueReference(enumerationId, enumeration.getName(), enumeration.getDescription()));
			}
		}

		return list;
	}

	public MeasurementVariable transform(final StandardVariable stdVariable, final boolean isFactor) {
		MeasurementVariable measurementVariable = null;

		if (stdVariable != null) {
			
			final String label = getLabelBasedOnRole(stdVariable.getPhenotypicType());

			measurementVariable =
					new MeasurementVariable(stdVariable.getId(), stdVariable.getName(), stdVariable.getDescription(), stdVariable
							.getScale().getName(), stdVariable.getMethod().getName(), stdVariable.getProperty().getName(), stdVariable
							.getDataType().getName(), "", label);
			measurementVariable.setFactor(isFactor);
			measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
			measurementVariable.setPossibleValues(this.transformPossibleValues(stdVariable.getEnumerations()));
			if (stdVariable.getConstraints() != null) {
				measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
				measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
			}
			measurementVariable.setRole(stdVariable.getPhenotypicType());
		}

		return measurementVariable;
	}

	private String getLabelBasedOnRole(final PhenotypicType role) {
		if (role == null) {
			return "";
		}

		return role.getLabelList().get(0);
	}
}
