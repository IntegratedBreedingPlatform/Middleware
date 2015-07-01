package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StockSaverTest {

	private StockSaver stockSaver;
	private StockModel stockModel;
	
	private enum StockVariable {
		
		ENTRY_NO(TermId.ENTRY_NO.getId(),
				TermId.ENTRY_NO.toString(),PhenotypicType.GERMPLASM,"1"),
		GID(TermId.GID.getId(),
				TermId.GID.toString(),PhenotypicType.GERMPLASM,"4"),
		DESIG(TermId.DESIG.getId(),
				TermId.DESIG.toString(),PhenotypicType.GERMPLASM,"SDFTY"),
		ENTRY_CODE(TermId.ENTRY_CODE.getId(),
				TermId.ENTRY_CODE.toString(),PhenotypicType.GERMPLASM,"CODE1"),
		GERMPLASM_1(1,"GERMPLASM_1",PhenotypicType.GERMPLASM,"3"),
		GERMPLASM_2(2,"GERMPLASM_2",PhenotypicType.GERMPLASM,"4");
		
		private int id;
		private String name;
		private PhenotypicType role;
		private String value;

		StockVariable(int id, String name, PhenotypicType role, String value) {
			this.id = id;
			this.name = name;
			this.role = role;
			this.value = value;
		}
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public PhenotypicType getRole() {
			return role;
		}
		public String getValue() {
			return value;
		}
	}

	@Before
	public void setUp() throws MiddlewareQueryException {
		stockSaver = Mockito.spy(new StockSaver(Mockito.mock(HibernateSessionProvider.class)));
		Mockito.doReturn(1).when(stockSaver).getStockPropertyId();
		Mockito.doReturn(1).when(stockSaver).getStockId();
	}
	
	@Test
	public void testCreateOrUpdate() throws MiddlewareQueryException {
		VariableList factors = createVariableList();
		stockModel = stockSaver.createStock(factors, stockModel);
		assertNotNull(stockModel);
		assertEquals(StockVariable.ENTRY_NO.getValue(),stockModel.getUniqueName());
		assertEquals(StockVariable.GID.getValue(),stockModel.getDbxrefId().toString());
		assertEquals(StockVariable.DESIG.getValue(),stockModel.getName().toString());
		assertEquals(StockVariable.ENTRY_CODE.getValue(),stockModel.getValue());
		assertNotNull(stockModel.getProperties());
		assertEquals(2, stockModel.getProperties().size());
		for (StockProperty property: stockModel.getProperties()) {
			StockVariable stockVariable = null;
			switch(property.getTypeId()) {
				case 1: stockVariable = StockVariable.GERMPLASM_1; 
					break;
				case 2: stockVariable = StockVariable.GERMPLASM_2; 
					break;
			} 
			assertEquals(stockVariable.getValue(),property.getValue());
		}
	}

	private VariableList createVariableList() {
		VariableList variableList = new VariableList();
		for(int i=0;i<6;i++) {
			StockVariable variable = StockVariable.values()[i];
			int standardVariableId = variable.getId();
			String name = variable.getName();
			String description = variable.getName()+"_DESC";
			String value = variable.getValue();
			PhenotypicType role = variable.getRole();
			variableList.add(createVariable(standardVariableId,name,description,value,i+1,role));
		}
		return variableList;
	}

	private Variable createVariable(int standardVariableId, String name, 
			String description, String value, int rank, PhenotypicType role) {
		Variable variable = new Variable();
		variable.setVariableType(createVariableType(
				standardVariableId,name,description,rank,role));
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createVariableType(int standardVariableId,
			String name, String description, int rank, PhenotypicType role) {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(name);
		variableType.setLocalDescription(description);
		variableType.setRole(role);
		variableType.setStandardVariable(createStandardVariable(standardVariableId));
		variableType.setRank(rank);
		return variableType;
	}

	private StandardVariable createStandardVariable(int id) {
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(id);
		return standardVariable;
	}
}
