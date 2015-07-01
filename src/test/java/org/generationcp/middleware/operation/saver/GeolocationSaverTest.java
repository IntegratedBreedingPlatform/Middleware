package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GeolocationSaverTest {

	private GeolocationSaver geolocationSaver;
	private Geolocation geolocation;
	
	private enum EnvironmentVariable {
		
		TRIAL_INSTANCE(TermId.TRIAL_INSTANCE_FACTOR.getId(),
				TermId.TRIAL_INSTANCE_FACTOR.toString(),PhenotypicType.TRIAL_ENVIRONMENT,"1"),
		LATITUDE(TermId.LATITUDE.getId(),
				TermId.LATITUDE.toString(),PhenotypicType.TRIAL_ENVIRONMENT,"1.5"),
		LONGITUDE(TermId.LONGITUDE.getId(),
				TermId.LONGITUDE.toString(),PhenotypicType.TRIAL_ENVIRONMENT,"3.6"),
		GEODETIC_DATUM(TermId.GEODETIC_DATUM.getId(),
				TermId.GEODETIC_DATUM.toString(),PhenotypicType.TRIAL_ENVIRONMENT,"1"),
		ALTITUDE(TermId.ALTITUDE.getId(),
				TermId.ALTITUDE.toString(),PhenotypicType.TRIAL_ENVIRONMENT,"5.5"),
		ENV_1(1,"ENV_1",PhenotypicType.TRIAL_ENVIRONMENT,"3"),
		ENV_2(2,"ENV_2",PhenotypicType.TRIAL_ENVIRONMENT,"4"),
		ENV_3(3,"ENV_2",PhenotypicType.TRIAL_ENVIRONMENT,"5"),
		VARIATE_1(4,"VARIATE_1",PhenotypicType.VARIATE,"7"),
		VARIATE_2(5,"VARIATE_2",PhenotypicType.VARIATE,"2");
		
		private int id;
		private String name;
		private PhenotypicType role;
		private String value;

		EnvironmentVariable(int id, String name, PhenotypicType role, String value) {
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
		geolocationSaver = Mockito.spy(new GeolocationSaver(Mockito.mock(HibernateSessionProvider.class)));
		Mockito.doReturn(1).when(geolocationSaver).getGeolocationPropertyId();
		Mockito.doReturn(geolocation).when(geolocationSaver).getGeolocationById(1);
		Mockito.doReturn(1).when(geolocationSaver).getGeolocationId();
	}
	
	@Test
	public void testCreateOrUpdate() throws MiddlewareQueryException {
		MeasurementRow row = createMeasurementRow();
		VariableList factors = createVariableList();
		Geolocation geolocation = geolocationSaver.createOrUpdate(factors, row, null);
		assertNotNull(geolocation);
		assertEquals(EnvironmentVariable.TRIAL_INSTANCE.getValue(),geolocation.getDescription());
		assertEquals(EnvironmentVariable.LATITUDE.getValue(),geolocation.getLatitude().toString());
		assertEquals(EnvironmentVariable.LONGITUDE.getValue(),geolocation.getLongitude().toString());
		assertEquals(EnvironmentVariable.GEODETIC_DATUM.getValue(),geolocation.getGeodeticDatum());
		assertEquals(EnvironmentVariable.ALTITUDE.getValue(),geolocation.getAltitude().toString());
		assertNotNull(geolocation.getProperties());
		assertEquals(3, geolocation.getProperties().size());
		int propertyIndex = 0;
		for (GeolocationProperty property: geolocation.getProperties()) {
			propertyIndex++;
			EnvironmentVariable environmentVariable = null;
			switch(propertyIndex) {
				case 1: environmentVariable = EnvironmentVariable.ENV_1; 
					break;
				case 2: environmentVariable = EnvironmentVariable.ENV_2; 
					break;
				case 3: environmentVariable = EnvironmentVariable.ENV_3; 
					break;
			} 
			assertTrue(environmentVariable.getId()==property.getTypeId());
			assertEquals(environmentVariable.getValue(),property.getValue());
		}
		assertEquals(2, geolocation.getVariates().size());
		int variateIndex = 0;
		for (Variable variable: geolocation.getVariates().getVariables()) {
			variateIndex++;
			EnvironmentVariable environmentVariable = null;
			switch(variateIndex) {
				case 1: environmentVariable = EnvironmentVariable.VARIATE_1; 
					break;
				case 2: environmentVariable = EnvironmentVariable.VARIATE_2; 
					break;
			} 
			assertEquals(environmentVariable.getValue(),variable.getValue());
		}
	}

	private MeasurementRow createMeasurementRow() {
		MeasurementRow row = new MeasurementRow();
		row.setDataList(createMeasurementDataList());
		return row;
	}

	private List<MeasurementData> createMeasurementDataList() {
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		for(int i=0;i<10;i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
			String label = variable.getName();
			String value = variable.getValue();
			dataList.add(createMeasurementData(label,value));
		}
		return dataList;
	}

	private MeasurementData createMeasurementData(String label,String value) {
		MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		measurementData.setValue(value);
		return measurementData;
	}

	private VariableList createVariableList() {
		VariableList variableList = new VariableList();
		for(int i=0;i<10;i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
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
	
	@Test
	public void testSetGeolocation() {
		Geolocation geolocation = new Geolocation();
		for (int i=0;i<5;i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
			geolocationSaver.setGeolocation(geolocation, 
					variable.getId(), variable.getValue());
			switch(i) {
				case 0: assertEquals(variable.getValue(),geolocation.getDescription()); break;
				case 1: assertEquals(variable.getValue(),geolocation.getLatitude().toString()); break;
				case 2: assertEquals(variable.getValue(),geolocation.getLongitude().toString()); break;
				case 3: assertEquals(variable.getValue(),geolocation.getGeodeticDatum()); break;
				case 4: assertEquals(variable.getValue(),geolocation.getAltitude().toString()); break;
			}
		}
	}
}
