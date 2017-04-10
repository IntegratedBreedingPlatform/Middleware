
package org.generationcp.middleware.operation.saver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
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

public class GeolocationSaverTest {

	public static final String EXPERIMENT_DESIGN_FACTOR_RCBD = "RCBD";
	public static final int EXPERIMENT_DESIGN_FACTOR_RCBD_ID = 10110;

	private GeolocationSaver geolocationSaver;
	private Geolocation geolocation;

	private enum EnvironmentVariable {

		TRIAL_INSTANCE(
			TermId.TRIAL_INSTANCE_FACTOR.getId(),
			TermId.TRIAL_INSTANCE_FACTOR.toString(),
			PhenotypicType.TRIAL_ENVIRONMENT,
			"1"),
		LATITUDE(TermId.LATITUDE.getId(), TermId.LATITUDE.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "1.5"),
		LONGITUDE(TermId.LONGITUDE.getId(), TermId.LONGITUDE.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "3.6"),
		GEODETIC_DATUM(TermId.GEODETIC_DATUM.getId(), TermId.GEODETIC_DATUM.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "1"),
		ALTITUDE(TermId.ALTITUDE.getId(), TermId.ALTITUDE.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "5.5"),
		ENV_1(1, "ENV_1", PhenotypicType.TRIAL_ENVIRONMENT, "3"),
		ENV_2(2, "ENV_2", PhenotypicType.TRIAL_ENVIRONMENT, "4"),
		ENV_3(3, "ENV_2", PhenotypicType.TRIAL_ENVIRONMENT, "5"),
		ENV_4(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), TermId.EXPERIMENT_DESIGN_FACTOR.name(), PhenotypicType.TRIAL_ENVIRONMENT,
			EXPERIMENT_DESIGN_FACTOR_RCBD),
		VARIATE_1(4, "VARIATE_1", PhenotypicType.VARIATE, "7"),
		VARIATE_2(5, "VARIATE_2", PhenotypicType.VARIATE, "2");

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
	}

	@Before
	public void setUp() throws MiddlewareQueryException {
		this.geolocationSaver = Mockito.spy(new GeolocationSaver(Mockito.mock(HibernateSessionProvider.class)));
		Mockito.doReturn(this.geolocation).when(this.geolocationSaver).getGeolocationById(1);
	}

	@Test
	public void testCreateOrUpdate() throws MiddlewareQueryException {
		MeasurementRow row = this.createMeasurementRow();
		VariableList factors = this.createVariableList();
		Geolocation geolocation = this.geolocationSaver.createOrUpdate(factors, row, null);
		assertNotNull(geolocation);
		assertEquals(EnvironmentVariable.TRIAL_INSTANCE.getValue(), geolocation.getDescription());
		assertEquals(EnvironmentVariable.LATITUDE.getValue(), geolocation.getLatitude().toString());
		assertEquals(EnvironmentVariable.LONGITUDE.getValue(), geolocation.getLongitude().toString());
		assertEquals(EnvironmentVariable.GEODETIC_DATUM.getValue(), geolocation.getGeodeticDatum());
		assertEquals(EnvironmentVariable.ALTITUDE.getValue(), geolocation.getAltitude().toString());
		assertNotNull(geolocation.getProperties());
		assertEquals(4, geolocation.getProperties().size());
		int propertyIndex = 0;
		assertGeoPropertiesLoop: for (GeolocationProperty property : geolocation.getProperties()) {
			propertyIndex++;
			EnvironmentVariable environmentVariable = null;
			switch (propertyIndex) {
				case 1:
					environmentVariable = EnvironmentVariable.ENV_1;
					break;
				case 2:
					environmentVariable = EnvironmentVariable.ENV_2;
					break;
				case 3:
					environmentVariable = EnvironmentVariable.ENV_3;
					break;
				case 4:
					assertEquals(String.valueOf(GeolocationSaverTest.EXPERIMENT_DESIGN_FACTOR_RCBD_ID), property.getValue());
					continue assertGeoPropertiesLoop;
			}
			assertTrue(environmentVariable.getId() == property.getTypeId());
			assertEquals(environmentVariable.getValue(), property.getValue());
		}
		assertEquals(2, geolocation.getVariates().size());
		int variateIndex = 0;
		for (Variable variable : geolocation.getVariates().getVariables()) {
			variateIndex++;
			EnvironmentVariable environmentVariable = null;
			switch (variateIndex) {
				case 1:
					environmentVariable = EnvironmentVariable.VARIATE_1;
					break;
				case 2:
					environmentVariable = EnvironmentVariable.VARIATE_2;
					break;
			}
			assertEquals(environmentVariable.getValue(), variable.getValue());
		}
	}

	private MeasurementRow createMeasurementRow() {
		MeasurementRow row = new MeasurementRow();
		row.setDataList(this.createMeasurementDataList());
		return row;
	}

	private List<MeasurementData> createMeasurementDataList() {
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		for (int i = 0; i < EnvironmentVariable.values().length; i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
			String label = variable.getName();
			String value = variable.getValue();
			dataList.add(this.createMeasurementData(label, value));
		}
		return dataList;
	}

	private MeasurementData createMeasurementData(String label, String value) {
		MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		measurementData.setValue(value);
		return measurementData;
	}

	private VariableList createVariableList() {
		VariableList variableList = new VariableList();
		for (int i = 0; i < EnvironmentVariable.values().length; i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
			int standardVariableId = variable.getId();
			String name = variable.getName();
			String description = variable.getName() + "_DESC";
			String value = variable.getValue();
			PhenotypicType role = variable.getRole();
			variableList.add(this.createVariable(standardVariableId, name, description, value, i + 1, role));
		}
		return variableList;
	}

	private Variable createVariable(int standardVariableId, String name, String description, String value, int rank, PhenotypicType role) {
		Variable variable = new Variable();
		variable.setVariableType(this.createVariableType(standardVariableId, name, description, rank, role));
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
		standardVariable.setEnumerations(new ArrayList<Enumeration>());
		standardVariable.getEnumerations()
			.add(new Enumeration(GeolocationSaverTest.EXPERIMENT_DESIGN_FACTOR_RCBD_ID, GeolocationSaverTest.EXPERIMENT_DESIGN_FACTOR_RCBD,
				"", 1));
		return standardVariable;
	}

	@Test
	public void testSetGeolocation() {
		Geolocation geolocation = new Geolocation();
		for (int i = 0; i < 5; i++) {
			EnvironmentVariable variable = EnvironmentVariable.values()[i];
			this.geolocationSaver.setGeolocation(geolocation, variable.getId(), variable.getValue());
			switch (i) {
				case 0:
					assertEquals(variable.getValue(), geolocation.getDescription());
					break;
				case 1:
					assertEquals(variable.getValue(), geolocation.getLatitude().toString());
					break;
				case 2:
					assertEquals(variable.getValue(), geolocation.getLongitude().toString());
					break;
				case 3:
					assertEquals(variable.getValue(), geolocation.getGeodeticDatum());
					break;
				case 4:
					assertEquals(variable.getValue(), geolocation.getAltitude().toString());
					break;
			}
		}
	}
}
