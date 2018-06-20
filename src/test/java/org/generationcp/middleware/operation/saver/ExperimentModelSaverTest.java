package org.generationcp.middleware.operation.saver;

import junit.framework.Assert;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentModelSaverTest {

	@Mock
	private HibernateSessionProvider sessionProvider;

	private final ExperimentModelSaver experimentModelSaver = new ExperimentModelSaver(sessionProvider);

	@Test
	public void testCreateStudyDesignExperimentProperties() {


		final ExperimentModel experimentModel = new ExperimentModel();
		final VariableList factors = new VariableList();

		factors.add(this.createVariable(101, "Categorical Name 1", TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(this.createVariable(102, "999", TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(this.createVariable(103, "Hello", TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(this.createVariable(104, "1", TermId.NUMERIC_VARIABLE.getId(), VariableType.EXPERIMENTAL_DESIGN));
		factors.add(this.createVariable(105, "Environment", TermId.CHARACTER_VARIABLE.getId(), VariableType.ENVIRONMENT_DETAIL));

		final List<ExperimentProperty> experimentProperties = experimentModelSaver.createTrialDesignExperimentProperties(experimentModel, factors);

		Assert.assertEquals(4, experimentProperties.size());

		// Verify that only Study Design Factors are created
		Assert.assertEquals(Integer.valueOf(101), experimentProperties.get(0).getTypeId());
		Assert.assertEquals(Integer.valueOf(102), experimentProperties.get(1).getTypeId());
		Assert.assertEquals(Integer.valueOf(103), experimentProperties.get(2).getTypeId());
		Assert.assertEquals(Integer.valueOf(104), experimentProperties.get(3).getTypeId());

	}


	@Test
	public void testCreateTrialDesignPropertyVariableIsCategorical() {

		final Integer variableId = 101;
		final String variableValue = "Categorical Name 1";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = this.createVariable(variableId, variableValue, TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(String.valueOf(1234), experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	@Test
	public void testCreateTrialDesignPropertyVariableIsNumeric() {

		final Integer variableId = 101;
		final String variableValue = "20";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = this.createVariable(variableId, variableValue, TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(variableValue, experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	@Test
	public void testCreateStudyDesignPropertyVariableIsText() {

		final Integer variableId = 101;
		final String variableValue = "Hello";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = this.createVariable(variableId, variableValue, TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(variableValue, experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	private Variable createVariable(final Integer variableId, final String variableValue, final Integer dataTypeId, final VariableType variableType) {

		final Variable variable = new Variable();
		variable.setValue(variableValue);

		final StandardVariable standardVariable = new StandardVariable();
		final List<Enumeration> enumerations = new ArrayList<>();
		enumerations.add(new Enumeration(1234, "Categorical Name 1", "Categorical Name Description 1", 1));
		enumerations.add(new Enumeration(1235, "Categorical Name 2", "Categorical Name Description 2", 2));
		enumerations.add(new Enumeration(1236, "Categorical Name 3", "Categorical Name Description 3", 3));
		standardVariable.setEnumerations(enumerations);
		standardVariable.setDataType(new Term(dataTypeId, "",""));
		standardVariable.setId(variableId);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(variableType);
		dmsVariableType.setStandardVariable(standardVariable);
		dmsVariableType.setRole(variableType.getRole());
		dmsVariableType.setRank(99);

		variable.setVariableType(dmsVariableType);

		return variable;

	}

}
