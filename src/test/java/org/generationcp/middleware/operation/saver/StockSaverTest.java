
package org.generationcp.middleware.operation.saver;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class StockSaverTest {

	private StockSaver stockSaver;
	private StockModel stockModel;
	private static final List<Enumeration> GERMPLASM_1_VALID_VALUES = Arrays.<Enumeration>asList(new Enumeration[] {
			new Enumeration(10, "1", "1 - Low", 1), new Enumeration(11, "2", "2 - Medium", 2), new Enumeration(13, "3", "3 - High", 3)});
	private static final List<Enumeration> GERMPLASM_2_VALID_VALUES =
			Arrays.<Enumeration>asList(new Enumeration[] {new Enumeration(20, "P", "Pass", 1), new Enumeration(21, "F", "Fail", 2)});

	private enum StockVariable {

		ENTRY_NO(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.toString(), PhenotypicType.GERMPLASM, "1", TermId.NUMERIC_VARIABLE.getId(),
				null), GID(TermId.GID.getId(), TermId.GID.toString(), PhenotypicType.GERMPLASM, "4",
						TermId.GERMPLASM_LIST_DATA_TYPE.getId(), null), DESIG(TermId.DESIG.getId(), TermId.DESIG.toString(),
								PhenotypicType.GERMPLASM, "SDFTY", TermId.GERMPLASM_LIST_DATA_TYPE.getId(),
								null), ENTRY_CODE(TermId.ENTRY_CODE.getId(), TermId.ENTRY_CODE.toString(), PhenotypicType.GERMPLASM,
										"CODE1", TermId.CHARACTER_VARIABLE.getId(), null), GERMPLASM_WITH_VALID_VALUE(1, "GERMPLASM_1",
												PhenotypicType.GERMPLASM, "2", TermId.CATEGORICAL_VARIABLE.getId(),
												StockSaverTest.GERMPLASM_1_VALID_VALUES), GERMPLASM_WITH_CUSTOM_VALUE(2, "GERMPLASM_2",
														PhenotypicType.GERMPLASM, "UNKNOWN", TermId.CATEGORICAL_VARIABLE.getId(),
														StockSaverTest.GERMPLASM_2_VALID_VALUES), GERMPLASM_CHARACTER(3, "GERMPLASM_3",
																PhenotypicType.GERMPLASM, "GERMPLASM 123",
																TermId.CHARACTER_VARIABLE.getId(), null);

		private final int id;
		private final String name;
		private final PhenotypicType role;
		private final String value;
		private final int dataTypeId;
		private final List<Enumeration> validValues;

		StockVariable(final int id, final String name, final PhenotypicType role, final String value, final int dataTypeId,
				final List<Enumeration> validValues) {
			this.id = id;
			this.name = name;
			this.role = role;
			this.value = value;
			this.dataTypeId = dataTypeId;
			this.validValues = validValues;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public PhenotypicType getRole() {
			return this.role;
		}

		public String getValue() {
			return this.value;
		}

		public int getDataTypeId() {
			return this.dataTypeId;
		}

		public List<Enumeration> getValidValues() {
			return this.validValues;
		}

		public String getDatabaseValue() {
			if (this.dataTypeId != TermId.CATEGORICAL_VARIABLE.getId() || this.validValues == null) {
				return this.value;
			}
			for (final Enumeration validValue : this.validValues) {
				if (validValue.getName().equalsIgnoreCase(this.value)) {
					return validValue.getId().toString();
				}
			}
			return this.value;
		}

	}

	@Before
	public void setUp() throws MiddlewareQueryException {
		this.stockSaver = new StockSaver(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testCreateOrUpdate() throws MiddlewareQueryException {
		final VariableList factors = this.createVariableList();
		this.stockModel = this.stockSaver.createStock(factors, this.stockModel);
		Assert.assertNotNull(this.stockModel);
		Assert.assertEquals(StockVariable.ENTRY_NO.getValue(), this.stockModel.getUniqueName());
		Assert.assertEquals(StockVariable.GID.getValue(), this.stockModel.getDbxrefId().toString());
		Assert.assertEquals(StockVariable.DESIG.getValue(), this.stockModel.getName().toString());
		Assert.assertEquals(StockVariable.ENTRY_CODE.getValue(), this.stockModel.getValue());
		Assert.assertNotNull(this.stockModel.getProperties());
		Assert.assertEquals(3, this.stockModel.getProperties().size());
		for (final StockProperty property : this.stockModel.getProperties()) {
			StockVariable stockVariable = null;
			switch (property.getTypeId()) {
				case 1:
					stockVariable = StockVariable.GERMPLASM_WITH_VALID_VALUE;
					break;
				case 2:
					stockVariable = StockVariable.GERMPLASM_WITH_CUSTOM_VALUE;
					break;
				case 3:
					stockVariable = StockVariable.GERMPLASM_CHARACTER;
					break;
			}
			Assert.assertTrue(stockVariable.getId() == property.getTypeId());
			Assert.assertEquals(stockVariable.getDatabaseValue(), property.getValue());
		}
	}

	private VariableList createVariableList() {
		final VariableList variableList = new VariableList();
		int i = 0;
		for (final StockVariable variable : StockVariable.values()) {
			variableList.add(this.createVariable(variable.getId(), variable.getName(), variable.getValue(), i + 1, variable.getRole(),
					variable.getDataTypeId(), variable.getValidValues()));
			i++;
		}
		return variableList;
	}

	private Variable createVariable(final int standardVariableId, final String name, final String value, final int rank,
			final PhenotypicType role, final int dataTypeId, final List<Enumeration> validValues) {
		final Variable variable = new Variable();
		variable.setVariableType(this.createVariableType(standardVariableId, name, rank, role, dataTypeId, validValues));
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createVariableType(final int standardVariableId, final String name, final int rank, final PhenotypicType role,
			final int dataTypeId, final List<Enumeration> validValues) {
		final DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(name);
		variableType.setLocalDescription(name + "_DESC");
		variableType.setRole(role);
		variableType.setStandardVariable(this.createStandardVariable(standardVariableId, dataTypeId, validValues));
		variableType.setRank(rank);
		return variableType;
	}

	private StandardVariable createStandardVariable(final int id, final int dataTypeId, final List<Enumeration> validValues) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(id);

		final Term dataType = new Term();
		dataType.setId(dataTypeId);
		standardVariable.setDataType(dataType);

		standardVariable.setEnumerations(validValues);

		return standardVariable;
	}
}
