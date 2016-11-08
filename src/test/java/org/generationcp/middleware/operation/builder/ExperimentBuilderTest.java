
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stock;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExperimentBuilderTest extends IntegrationTestBase {

	public static final String STOCK_MODEL_ENTRY_NO = "1";
	public static final int STOCK_MODEL_DBXREF_ID = 1234;
	public static final String STOCK_MODEL_DESIGNATION_NAME = "DESIGNATION NAME";
	public static final String STOCK_MODEL_ENTRY_CODE = "2";
	public static final String STOCK_MODEL_ENTRY_TYPE_VALUE = "Entry Type Value";
	static ExperimentBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new ExperimentBuilder(this.sessionProvder);
	}

	@Test
	public void testCreateVariable() throws MiddlewareQueryException {
		int typeId = 1000;
		ExperimentProperty property = new ExperimentProperty();
		VariableTypeList variableTypes = new VariableTypeList();
		DMSVariableType variableType = new DMSVariableType();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(typeId);
		variableType.setStandardVariable(standardVariable);
		variableTypes.add(variableType);
		property.setTypeId(typeId);
		PhenotypicType role = PhenotypicType.TRIAL_DESIGN;
		Variable variable = builder.createVariable(property, variableTypes, role);
		Assert.assertEquals("The role should be the same as what that was set", variable.getVariableType().getRole(), role);
	}

	@Test
	public void testCreateLocationFactor_ThereIsMatching() {
		Geolocation geoLocation = new Geolocation();
		String description = "XXX";
		geoLocation.setDescription(description);
		DMSVariableType variableType = new DMSVariableType();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		variableType.setStandardVariable(standardVariable);
		Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(),
				description);
	}

	@Test
	public void testCreateLocationFactor_ThereIsLocationValue() {
		int typeId = 1000;
		Geolocation geoLocation = new Geolocation();
		List<GeolocationProperty> properties = new ArrayList<GeolocationProperty>();
		GeolocationProperty e = new GeolocationProperty();
		e.setType(typeId);
		e.setValue("XXX");
		properties.add(e);
		geoLocation.setProperties(properties);
		String description = "XXX";
		geoLocation.setDescription(description);
		DMSVariableType variableType = new DMSVariableType();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(typeId);
		variableType.setStandardVariable(standardVariable);
		Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(),
				description);
	}

	@Test
	public void testCreateLocationFactor_ThereIsNoMatchingLocationValue() {
		int typeId = 1000;
		Geolocation geoLocation = new Geolocation();
		List<GeolocationProperty> properties = new ArrayList<GeolocationProperty>();
		GeolocationProperty e = new GeolocationProperty();
		e.setType(typeId);
		properties.add(e);
		geoLocation.setProperties(properties);
		String description = "XXX";
		geoLocation.setDescription(description);
		DMSVariableType variableType = new DMSVariableType();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(1001);
		variableType.setStandardVariable(standardVariable);
		Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertNull("The variable be null", variable);
	}

	@Test
	public void testCreateGermplasmFactorForEntryNo() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_NO);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(STOCK_MODEL_ENTRY_NO, variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForGID() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.GID);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(String.valueOf(STOCK_MODEL_DBXREF_ID), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForDesignation() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.DESIG);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(STOCK_MODEL_DESIGNATION_NAME, variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForEntryCode() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_CODE);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(STOCK_MODEL_ENTRY_CODE, variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForEntryType() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_TYPE);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(STOCK_MODEL_ENTRY_TYPE_VALUE, variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForNonGermplasmTermId() {

		final StockModel stockModel = this.createStockModel();
		// Create an dmsVariable which is not a germplasm factor
		final DMSVariableType variableType = this.createDMSVariableType(TermId.BLOCK_NO);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNull(variable);
	}


	private DMSVariableType createDMSVariableType(final TermId termId) {

		final DMSVariableType dmsVariableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(termId.getId());
		dmsVariableType.setStandardVariable(standardVariable);
		return dmsVariableType;
	}

	private StockModel createStockModel() {
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(STOCK_MODEL_ENTRY_NO);
		stockModel.setDbxrefId(STOCK_MODEL_DBXREF_ID);
		stockModel.setName(STOCK_MODEL_DESIGNATION_NAME);
		stockModel.setValue(STOCK_MODEL_ENTRY_CODE);

		Set<StockProperty> stockProperties = new HashSet<>();
		final StockProperty stockProperty = new StockProperty();
		stockProperty.setStock(stockModel);
		stockProperty.setValue(STOCK_MODEL_ENTRY_TYPE_VALUE);
		stockProperty.setTypeId(TermId.ENTRY_TYPE.getId());
		stockProperties.add(stockProperty);

		stockModel.setProperties(stockProperties);

		return stockModel;

	}
}
