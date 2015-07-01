package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ExperimentBuilderTest  extends DataManagerIntegrationTest {
	static ExperimentBuilder builder;
	
	@BeforeClass
	public static void setUp() throws Exception {
		HibernateSessionProvider sessionProvider = DataManagerIntegrationTest.managerFactory.getSessionProvider();
		builder = new ExperimentBuilder(sessionProvider);
	}
	
	@Test
	public void testCreateVariable() throws MiddlewareQueryException{
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
	public void testCreateLocationFactor_ThereIsMatching(){
		Geolocation geoLocation = new Geolocation();
		String description = "XXX";
		geoLocation.setDescription(description);
		DMSVariableType variableType = new DMSVariableType();
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		variableType.setStandardVariable(standardVariable);
		Variable variable = builder.createLocationFactor(geoLocation, variableType);
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(), description);
	}
	
	@Test
	public void testCreateLocationFactor_ThereIsLocationValue(){
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
		Assert.assertEquals("The variable description should be set properly since there is a mathcing variable", variable.getValue(), description);
	}

	@Test
	public void testCreateLocationFactor_ThereIsNoMatchingLocationValue(){
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
}
