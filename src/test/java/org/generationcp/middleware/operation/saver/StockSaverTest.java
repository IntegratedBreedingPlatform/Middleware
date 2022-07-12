
package org.generationcp.middleware.operation.saver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.generationcp.middleware.domain.dms.DMSVariableType;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class StockSaverTest {

	private StockSaver stockSaver;
	private StockModel stockModel;

	private enum StockVariable {

		ENTRY_NO(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.toString(), PhenotypicType.ENTRY_DETAIL, "1",
			new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "")),

		ENTRY_TYPE(TermId.ENTRY_TYPE.getId(), TermId.ENTRY_TYPE.toString(), PhenotypicType.ENTRY_DETAIL, "10170",
			new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable", "")),

		ENTRY_CODE(TermId.ENTRY_CODE.getId(), TermId.ENTRY_CODE.toString(), PhenotypicType.ENTRY_DETAIL, "ASD",
			new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "")),
		GID(TermId.GID.getId(), TermId.GID.toString(), PhenotypicType.GERMPLASM, "4",
			new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "")),
		GERMPLASM_1(1, "GERMPLASM_1", PhenotypicType.GERMPLASM, "3",
			new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "")),
		GERMPLASM_2(2, "GERMPLASM_2", PhenotypicType.GERMPLASM, "4",
			new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));

		private int id;
		private String name;
		private PhenotypicType role;
		private String value;

		private Term dataType;

		StockVariable(int id, String name, PhenotypicType role, String value, Term dataType) {
			this.id = id;
			this.name = name;
			this.role = role;
			this.value = value;
			this.dataType = dataType;
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

		public Term getDataType() {
			return this.dataType;
		}
	}

	@Before
	public void setUp() {
		this.stockSaver = Mockito.spy(new StockSaver(Mockito.mock(HibernateSessionProvider.class)));
	}

	@Test
	public void testCreateOrUpdate() {
		VariableList factors = this.createVariableList();
		this.stockModel = this.stockSaver.createStock(factors, this.stockModel);
		assertNotNull(this.stockModel);
		assertEquals(StockVariable.ENTRY_NO.getValue(), this.stockModel.getUniqueName());
		assertEquals(StockVariable.GID.getValue(), this.stockModel.getGermplasm().getGid().toString());
		assertNotNull(this.stockModel.getProperties());
		assertEquals(2, this.stockModel.getProperties().size());
		for (StockProperty property : this.stockModel.getProperties()) {
			StockVariable stockVariable = null;
			switch (property.getTypeId()) {
				case 8255:
					stockVariable = StockVariable.ENTRY_TYPE;
					break;
				case 8300:
					stockVariable = StockVariable.ENTRY_CODE;
					break;
			}
			assertEquals(stockVariable.getValue(), property.getValue());
		}
	}

	private VariableList createVariableList() {
		VariableList variableList = new VariableList();
		for (int i = 0; i < StockVariable.values().length; i++) {
			StockVariable stVariable = StockVariable.values()[i];
			int standardVariableId = stVariable.getId();
			String name = stVariable.getName();
			String description = stVariable.getName() + "_DESC";
			String value = stVariable.getValue();
			PhenotypicType role = stVariable.getRole();

			Variable variable = this.createVariable(standardVariableId, name, description, value, i + 1, role, stVariable.getDataType());
			variableList.add(variable);
		}
		return variableList;
	}

	private Variable createVariable(int standardVariableId, String name, String description, String value, int rank, PhenotypicType role, Term dataType) {
		Variable variable = new Variable();
		variable.setVariableType(this.createVariableType(standardVariableId, name, description, rank, role));
		variable.getVariableType().getStandardVariable().setDataType(dataType);
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createVariableType(int standardVariableId, String name, String description, int rank, PhenotypicType role) {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(name);
		variableType.setLocalDescription(description);
		variableType.setRole(role);
		variableType.setStandardVariable(this.createStandardVariable(standardVariableId));
		variableType.setRank(rank);
		return variableType;
	}

	private StandardVariable createStandardVariable(int id) {
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(id);
		return standardVariable;
	}
}
