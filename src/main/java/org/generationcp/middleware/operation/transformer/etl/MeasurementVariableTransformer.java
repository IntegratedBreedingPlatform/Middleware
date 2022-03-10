
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeasurementVariableTransformer extends Transformer {

	public MeasurementVariableTransformer() {
	}

	public List<MeasurementVariable> transform(final VariableTypeList variableTypeList, final boolean isFactor) {
		return this.transform(variableTypeList, isFactor, false);
	}

	public List<MeasurementVariable> transform(final VariableTypeList variableTypeList, final boolean isFactor, final boolean isStudy) {

		final List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

		if (variableTypeList != null && !variableTypeList.isEmpty()) {
			for (final DMSVariableType dmsVariableType : variableTypeList.getVariableTypes()) {
				final MeasurementVariable measurementVariable = this.transform(dmsVariableType, isFactor, isStudy);
				measurementVariables.add(measurementVariable);
			}
		}

		return measurementVariables;
	}

	public MeasurementVariable transform(final DMSVariableType dmsVariableType, final boolean isFactor, final boolean isStudy) {
		final StandardVariable stdVariable = dmsVariableType.getStandardVariable();
		String label = this.getLabelBasedOnRole(stdVariable.getPhenotypicType());
		if (!isFactor && isStudy) {
			label = PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0);
		}

		final MeasurementVariable measurementVariable =
				new MeasurementVariable(stdVariable.getId(), dmsVariableType.getLocalName(), stdVariable.getDescription(), stdVariable
						.getScale().getName(), stdVariable.getMethod().getName(), stdVariable.getProperty().getName(), stdVariable
						.getDataType().getName(), "", label);
		measurementVariable.setRole(dmsVariableType.getRole());
		measurementVariable.setVariableType(dmsVariableType.getVariableType());
		measurementVariable.setFactor(isFactor);
		measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
		measurementVariable.setPossibleValues(this.transformPossibleValues(stdVariable.getEnumerations()));
		measurementVariable.setFormula(stdVariable.getFormula());
		if (stdVariable.getConstraints() != null) {
			measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
			measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
		}
		if (dmsVariableType.getTreatmentLabel() != null && !"".equals(dmsVariableType.getTreatmentLabel())) {
			measurementVariable.setTreatmentLabel(dmsVariableType.getTreatmentLabel());
		}
		return measurementVariable;
	}

	public Set<MeasurementVariable> transform(final VariableList variableList, final boolean isFactor, final boolean isStudy) {

		final Set<MeasurementVariable> measurementVariables = new HashSet<>();

		if (variableList != null && !variableList.isEmpty()) {
			for (final Variable variable : variableList.getVariables()) {
				final DMSVariableType dmsVariableType = variable.getVariableType();
				final MeasurementVariable measurementVariable = this.transform(dmsVariableType, isFactor, !isStudy);
				measurementVariable.setValue(variable.getDisplayValue());
				if (!measurementVariables.contains(measurementVariable)) {
					measurementVariables.add(measurementVariable);
				}
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

			final String label = this.getLabelBasedOnRole(stdVariable.getPhenotypicType());

			measurementVariable =
					new MeasurementVariable(stdVariable.getId(), stdVariable.getName(), stdVariable.getDescription(), stdVariable
							.getScale().getName(), stdVariable.getMethod().getName(), stdVariable.getProperty().getName(), stdVariable
							.getDataType().getName(), "", label);
			measurementVariable.setFactor(isFactor);
			measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
			measurementVariable.setPossibleValues(this.transformPossibleValues(stdVariable.getEnumerations()));
			measurementVariable.setFormula(stdVariable.getFormula());
			if (stdVariable.getConstraints() != null) {
				measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
				measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
			}
			measurementVariable.setRole(stdVariable.getPhenotypicType());
		}

		return measurementVariable;
	}

	public MeasurementVariable transform(final StandardVariable standardVariable, final boolean isFactor, final VariableType variableType) {
		final MeasurementVariable measurementVariable = this.transform(standardVariable, isFactor);
		measurementVariable.setVariableType(variableType);
		measurementVariable.setAlias(standardVariable.getName());
		return measurementVariable;
	}

	private String getLabelBasedOnRole(final PhenotypicType role) {
		// TODO: review PhenotypicType.UNASSIGNED empty labelList
		if (role == null || CollectionUtils.isEmpty(role.getLabelList())) {
			return "";
		}
		return role.getLabelList().get(0);
	}
}
