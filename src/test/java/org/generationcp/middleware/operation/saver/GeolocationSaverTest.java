
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class GeolocationSaverTest {

	private GeolocationSaver geolocationSaver;
	private static final List<Enumeration> ENV_1_VALID_VALUES = Arrays.<Enumeration>asList(new Enumeration[] {
			new Enumeration(10, "1", "1 - Low", 1), new Enumeration(11, "2", "2 - Medium", 2), new Enumeration(13, "3", "3 - High", 3)});
	private static final List<Enumeration> ENV_2_VALID_VALUES =
			Arrays.<Enumeration>asList(new Enumeration[] {new Enumeration(20, "P", "Pass", 1), new Enumeration(21, "F", "Fail", 2)});

	private enum EnvironmentVariable {

		TRIAL_INSTANCE(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.TRIAL_INSTANCE_FACTOR.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "1",
				TermId.NUMERIC_VARIABLE.getId(), null), //

		LATITUDE(TermId.LATITUDE.getId(), TermId.LATITUDE.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "1.5",
				TermId.NUMERIC_VARIABLE.getId(), null), LONGITUDE(TermId.LONGITUDE.getId(), TermId.LONGITUDE.toString(),
						PhenotypicType.TRIAL_ENVIRONMENT, "3.6", TermId.NUMERIC_VARIABLE.getId(), null), //

		GEODETIC_DATUM(TermId.GEODETIC_DATUM.getId(), TermId.GEODETIC_DATUM.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "1",
				TermId.CHARACTER_VARIABLE.getId(), null), //

		ALTITUDE(TermId.ALTITUDE.getId(), TermId.ALTITUDE.toString(), PhenotypicType.TRIAL_ENVIRONMENT, "5.5",
				TermId.NUMERIC_VARIABLE.getId(), null), //

		ENV_WITH_VALID_VALUE(1, "ENV_1", PhenotypicType.TRIAL_ENVIRONMENT, "3", TermId.CATEGORICAL_VARIABLE.getId(),
				GeolocationSaverTest.ENV_1_VALID_VALUES), //

		ENV_WITH_CUSTOM_VALUE(2, "ENV_2", PhenotypicType.TRIAL_ENVIRONMENT, "UNKNOWN", TermId.CATEGORICAL_VARIABLE.getId(),
				GeolocationSaverTest.ENV_2_VALID_VALUES), //

		ENV_NUMERIC(3, "ENV_2", PhenotypicType.TRIAL_ENVIRONMENT, "5", TermId.NUMERIC_VARIABLE.getId(), null), //

		VARIATE_1(4, "VARIATE_1", PhenotypicType.VARIATE, "7", TermId.NUMERIC_VARIABLE.getId(), null), //

		VARIATE_2(5, "VARIATE_2", PhenotypicType.VARIATE, "2", TermId.NUMERIC_VARIABLE.getId(), null);

		private final int id;
		private final String name;
		private final PhenotypicType role;
		private final String value;
		private final int dataTypeId;
		private final List<Enumeration> validValues;

		EnvironmentVariable(final int id, final String name, final PhenotypicType role, final String value, final int dataTypeId,
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
		this.geolocationSaver = new GeolocationSaver(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testCreateOrUpdate() throws MiddlewareQueryException {
		final MeasurementRow row = this.createMeasurementRow();
		final VariableList factors = this.createVariableList();
		final Geolocation geolocation = this.geolocationSaver.createOrUpdate(factors, row, null);
		Assert.assertNotNull(geolocation);
		Assert.assertEquals(EnvironmentVariable.TRIAL_INSTANCE.getValue(), geolocation.getDescription());
		Assert.assertEquals(EnvironmentVariable.LATITUDE.getValue(), geolocation.getLatitude().toString());
		Assert.assertEquals(EnvironmentVariable.LONGITUDE.getValue(), geolocation.getLongitude().toString());
		Assert.assertEquals(EnvironmentVariable.GEODETIC_DATUM.getValue(), geolocation.getGeodeticDatum());
		Assert.assertEquals(EnvironmentVariable.ALTITUDE.getValue(), geolocation.getAltitude().toString());
		Assert.assertNotNull(geolocation.getProperties());
		Assert.assertEquals(3, geolocation.getProperties().size());
		int propertyIndex = 0;
		for (final GeolocationProperty property : geolocation.getProperties()) {
			propertyIndex++;
			EnvironmentVariable environmentVariable = null;
			switch (propertyIndex) {
				case 1:
					environmentVariable = EnvironmentVariable.ENV_WITH_VALID_VALUE;
					break;
				case 2:
					environmentVariable = EnvironmentVariable.ENV_WITH_CUSTOM_VALUE;
					break;
				case 3:
					environmentVariable = EnvironmentVariable.ENV_NUMERIC;
					break;
			}

			Assert.assertTrue(environmentVariable.getId() == property.getTypeId());
			Assert.assertEquals(environmentVariable.getDatabaseValue(), property.getValue());
		}
		Assert.assertEquals(2, geolocation.getVariates().size());
		int variateIndex = 0;
		for (final Variable variable : geolocation.getVariates().getVariables()) {
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
			Assert.assertEquals(environmentVariable.getValue(), variable.getValue());
		}
	}

	private MeasurementRow createMeasurementRow() {
		final MeasurementRow row = new MeasurementRow();
		row.setDataList(this.createMeasurementDataList());
		return row;
	}

	private List<MeasurementData> createMeasurementDataList() {
		final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		for (int i = 0; i < 10; i++) {
			final EnvironmentVariable variable = EnvironmentVariable.values()[i];
			final String label = variable.getName();
			final String value = variable.getValue();
			dataList.add(this.createMeasurementData(label, value));
		}
		return dataList;
	}

	private MeasurementData createMeasurementData(final String label, final String value) {
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		measurementData.setValue(value);
		return measurementData;
	}

	private VariableList createVariableList() {
		final VariableList variableList = new VariableList();
		int i = 0;
		for (final EnvironmentVariable variable : EnvironmentVariable.values()) {
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

	@Test
	public void testSetGeolocation() {
		final Geolocation geolocation = new Geolocation();
		for (int i = 0; i < 5; i++) {
			final EnvironmentVariable variable = EnvironmentVariable.values()[i];
			this.geolocationSaver.setGeolocation(geolocation, variable.getId(), variable.getValue());
			switch (i) {
				case 0:
					Assert.assertEquals(variable.getValue(), geolocation.getDescription());
					break;
				case 1:
					Assert.assertEquals(variable.getValue(), geolocation.getLatitude().toString());
					break;
				case 2:
					Assert.assertEquals(variable.getValue(), geolocation.getLongitude().toString());
					break;
				case 3:
					Assert.assertEquals(variable.getValue(), geolocation.getGeodeticDatum());
					break;
				case 4:
					Assert.assertEquals(variable.getValue(), geolocation.getAltitude().toString());
					break;
			}
		}
	}
}
