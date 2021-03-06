
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stock;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExperimentBuilderTest extends IntegrationTestBase {

	static ExperimentBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new ExperimentBuilder(this.sessionProvder);
	}

	@Test
	public void testCreateVariable() {
		final int typeId = 1000;
		final ExperimentProperty property = new ExperimentProperty();
		final VariableTypeList variableTypes = new VariableTypeList();
		final DMSVariableType variableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(typeId);
		variableType.setStandardVariable(standardVariable);
		variableTypes.add(variableType);
		property.setTypeId(typeId);
		final PhenotypicType role = PhenotypicType.TRIAL_DESIGN;
		final Variable variable = builder.createVariable(property, variableTypes, role);
		Assert.assertEquals("The role should be the same as what that was set", variable.getVariableType().getRole(), role);
	}

	@Test
	public void testCreateLocationFactorThereIsMatching() {
		final Geolocation geoLocation = new Geolocation();
		final String description = "XXX";
		geoLocation.setDescription(description);
		final DMSVariableType variableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		variableType.setStandardVariable(standardVariable);
		final Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(),
				description);
	}

	@Test
	public void testCreateLocationFactorThereIsLocationValue() {
		final int typeId = 1000;
		final Geolocation geoLocation = new Geolocation();
		final List<GeolocationProperty> properties = new ArrayList<GeolocationProperty>();
		final GeolocationProperty e = new GeolocationProperty();
		e.setType(typeId);
		e.setValue("XXX");
		properties.add(e);
		geoLocation.setProperties(properties);
		final String description = "XXX";
		geoLocation.setDescription(description);
		final DMSVariableType variableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(typeId);
		variableType.setStandardVariable(standardVariable);
		final Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(),
				description);
	}

	@Test
	public void testCreateLocationFactorThereIsNoMatchingLocationValue() {
		final int typeId = 1000;
		final Geolocation geoLocation = new Geolocation();
		final List<GeolocationProperty> properties = new ArrayList<GeolocationProperty>();
		final GeolocationProperty e = new GeolocationProperty();
		e.setType(typeId);
		properties.add(e);
		geoLocation.setProperties(properties);
		final String description = "XXX";
		geoLocation.setDescription(description);
		final DMSVariableType variableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(1001);
		variableType.setStandardVariable(standardVariable);
		final Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertNull("The variable be null", variable);
	}

	@Test
	public void testCreateGermplasmFactorForEntryNo() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_NO);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(stockModel.getUniqueName(), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForGID() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.GID);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(String.valueOf(stockModel.getGermplasm().getGid()), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForDesignation() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.DESIG);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(stockModel.getName(), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForEntryCode() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_CODE);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(stockModel.getValue(), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForEntryType() {

		final StockModel stockModel = this.createStockModel();
		final DMSVariableType variableType = this.createDMSVariableType(TermId.ENTRY_TYPE);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNotNull(variable);
		Assert.assertEquals(stockModel.getProperties().iterator().next().getValue(), variable.getValue());
	}

	@Test
	public void testCreateGermplasmFactorForNonGermplasmTermId() {

		final StockModel stockModel = this.createStockModel();
		// Create an dmsVariable which is not a germplasm factor
		final DMSVariableType variableType = this.createDMSVariableType(TermId.BLOCK_NO);

		final Variable variable = builder.createGermplasmFactor(stockModel, variableType);

		Assert.assertNull(variable);
	}
	
	@Test
	public void testAddGermplasmFactors() {
		final StockModel stockModel = this.createStockModel();
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setStock(stockModel);
		final Map<Integer, StockModel> stockMap = new HashMap<Integer, StockModel>();
		stockMap.put(stockModel.getStockId(), stockModel);
		final VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_NO));
		variableTypes.add(this.createDMSVariableType(TermId.GID));
		variableTypes.add(this.createDMSVariableType(TermId.DESIG));
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_CODE));
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_TYPE));
		
		final VariableList factors = new VariableList();
		builder.addGermplasmFactors(factors, experimentModel, variableTypes, stockMap);
		final List<Variable> variables = factors.getVariables();
		Assert.assertEquals(5, variables.size());
		final Iterator<Variable> iterator = variables.iterator();
		verifyFactorVariable(iterator.next(), TermId.ENTRY_NO.getId(), stockModel.getUniqueName());
		verifyFactorVariable(iterator.next(), TermId.GID.getId(), String.valueOf(stockModel.getGermplasm().getGid()));
		verifyFactorVariable(iterator.next(), TermId.DESIG.getId(), stockModel.getName());
		verifyFactorVariable(iterator.next(), TermId.ENTRY_CODE.getId(), stockModel.getValue());
		verifyFactorVariable(iterator.next(), TermId.ENTRY_TYPE.getId(), stockModel.getProperties().iterator().next().getValue());
	}
	
	@Test
	public void testAddGermplasmFactors_NoStock() {
		final StockModel stockModel = this.createStockModel();
		final ExperimentModel experimentModel = new ExperimentModel();
		final Map<Integer, StockModel> stockMap = new HashMap<Integer, StockModel>();
		stockMap.put(stockModel.getStockId(), stockModel);
		final VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_NO));
		variableTypes.add(this.createDMSVariableType(TermId.GID));
		variableTypes.add(this.createDMSVariableType(TermId.DESIG));
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_CODE));
		variableTypes.add(this.createDMSVariableType(TermId.ENTRY_TYPE));
		
		final VariableList factors = new VariableList();
		builder.addGermplasmFactors(factors, experimentModel, variableTypes, stockMap);
		Assert.assertTrue(factors.getVariables().isEmpty());
	}
	
	private void verifyFactorVariable(final Variable variable, final int id, final String value) {
		Assert.assertEquals(id, variable.getVariableType().getId());
		Assert.assertEquals(value, variable.getValue());
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
		stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(20));
		stockModel.setGermplasm(new Germplasm(new Random().nextInt(Integer.MAX_VALUE)));
		stockModel.setName(RandomStringUtils.randomAlphanumeric(20));
		stockModel.setValue(RandomStringUtils.randomAlphanumeric(20));

		final Set<StockProperty> stockProperties = new HashSet<>();
		final StockProperty stockProperty = new StockProperty();
		stockProperty.setStock(stockModel);
		stockProperty.setValue(RandomStringUtils.randomAlphanumeric(20));
		stockProperty.setTypeId(TermId.ENTRY_TYPE.getId());
		stockProperties.add(stockProperty);

		stockModel.setProperties(stockProperties);

		return stockModel;

	}
}
