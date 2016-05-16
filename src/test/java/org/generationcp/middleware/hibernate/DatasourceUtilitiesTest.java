
package org.generationcp.middleware.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.collect.ImmutableMap;
import com.mysql.jdbc.PreparedStatement;

public class DatasourceUtilitiesTest {

	private static final String PROPERTY1 = "Property1";
	private static final String PROPERTY2 = "Property2";
	private static final String PROPERTY3 = "Property3";

	private static final String PROPERTY_VALUE3 = "PropertyValue3";
	private static final String PROPERTY_VALUE2 = "PropertyValue2";
	private static final String PROPERTY_VALUE1 = "PropertyValue1";

	private static final String ATTRIBUTE1 = "Attribute1";
	private static final String ATTRIBUTE2 = "Attribute2";
	private static final String ATTRIBUTE3 = "Attribute3";

	private static final String ATTRIBUTE_VALUE1 = "AttributeValue1";
	private static final String ATTRIBUTE_VALUE2 = "AttributeValue2";
	private static final String ATTRIBUTE_VALUE3 = "AttributeValue3";

	/**
	 * Make sure that properties and attributes are set correctly into the root bean definition.
	 *
	 * @throws Exception in case of any error with the test
	 */
	@Test
	public void testCreateRootBeanDefinition() throws Exception {

		final DatasourceUtilities xaDatasourceUtilities = new DatasourceUtilities();

		final Map<String, Object> attributes =
				ImmutableMap.<String, Object>of(DatasourceUtilitiesTest.ATTRIBUTE1, DatasourceUtilitiesTest.ATTRIBUTE_VALUE1,
						DatasourceUtilitiesTest.ATTRIBUTE2, DatasourceUtilitiesTest.ATTRIBUTE_VALUE2,
						DatasourceUtilitiesTest.ATTRIBUTE3, DatasourceUtilitiesTest.ATTRIBUTE_VALUE3);

		final Map<String, Object> properties =
				ImmutableMap.<String, Object>of(DatasourceUtilitiesTest.PROPERTY1, DatasourceUtilitiesTest.PROPERTY_VALUE1,
						DatasourceUtilitiesTest.PROPERTY2, DatasourceUtilitiesTest.PROPERTY_VALUE2,
						DatasourceUtilitiesTest.PROPERTY3, DatasourceUtilitiesTest.PROPERTY_VALUE3);

		final RootBeanDefinition rootBeanDefinition =
				xaDatasourceUtilities.createRootBeanDefinition(DatasourceUtilities.class, attributes, properties);
		Assert.assertEquals("Root bean definition must have attribute 1", DatasourceUtilitiesTest.ATTRIBUTE_VALUE1,
				rootBeanDefinition.getAttribute(DatasourceUtilitiesTest.ATTRIBUTE1));
		Assert.assertEquals("Root bean definition must have attribute 2", DatasourceUtilitiesTest.ATTRIBUTE_VALUE2,
				rootBeanDefinition.getAttribute(DatasourceUtilitiesTest.ATTRIBUTE2));
		Assert.assertEquals("Root bean definition must have attribute 3", DatasourceUtilitiesTest.ATTRIBUTE_VALUE3,
				rootBeanDefinition.getAttribute(DatasourceUtilitiesTest.ATTRIBUTE3));

		final MutablePropertyValues propertyValues = rootBeanDefinition.getPropertyValues();
		Assert.assertEquals("Root bean definition must have attribute 1", DatasourceUtilitiesTest.PROPERTY_VALUE1, propertyValues
				.getPropertyValue(DatasourceUtilitiesTest.PROPERTY1).getValue());
		Assert.assertEquals("Root bean definition must have attribute 1", DatasourceUtilitiesTest.PROPERTY_VALUE2, propertyValues
				.getPropertyValue(DatasourceUtilitiesTest.PROPERTY2).getValue());
		Assert.assertEquals("Root bean definition must have attribute 1", DatasourceUtilitiesTest.PROPERTY_VALUE3, propertyValues
				.getPropertyValue(DatasourceUtilitiesTest.PROPERTY3).getValue());

	}

	@Test
	public void testGetWorkbenchDataSource() throws Exception {
		final DatasourceUtilities xaDatasourceUtilities = new DatasourceUtilities();
		final DataSourceProperties xaDataSourceProperties = XATestUtility.mockProperties();

		final DriverManagerDataSource workbenchDataSource =
				xaDatasourceUtilities.getWorkbenchDataSource(xaDataSourceProperties);
		Assert.assertEquals("Username must be what we set it to", XATestUtility.DB_USERNAME, workbenchDataSource.getUsername());
		Assert.assertEquals("Password must be what we set it to", XATestUtility.DB_PASSWORD, workbenchDataSource.getPassword());

		Assert.assertEquals("Url must get correctly deriver", "jdbc:mysql://" + XATestUtility.DB_HOST + ":" + XATestUtility.DB_PORT + "/"
				+ XATestUtility.DB_WORKBENCH_NAME, workbenchDataSource.getUrl());

	}

	@Test
	public void testRetrieveMergedDatabases() throws Exception {
		final DatasourceUtilities xaDatasourceUtilities = new DatasourceUtilities();
		final DriverManagerDataSource mockWorkbenchDataSource = Mockito.mock(DriverManagerDataSource.class);
		final Connection mockConnection = Mockito.mock(Connection.class);
		final PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);
		final ResultSet mockResultSet = Mockito.mock(ResultSet.class);

		Mockito.when(mockWorkbenchDataSource.getConnection()).thenReturn(mockConnection);
		Mockito.when(mockConnection.prepareStatement(Matchers.anyString())).thenReturn(mockPreparedStatement);
		Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
		Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
		Mockito.when(mockResultSet.getString(1)).thenReturn("DB1").thenReturn("DB2");

		final List<String> retrieveMergedDatabases = xaDatasourceUtilities.retrieveCropDatabases(mockWorkbenchDataSource);
		Assert.assertTrue("Must contain DB1", retrieveMergedDatabases.contains("DB1"));
		Assert.assertTrue("Must contain DB2", retrieveMergedDatabases.contains("DB2"));

	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveMergedDatabasesExceptionalCase() throws Exception {
		final DatasourceUtilities xaDatasourceUtilities = new DatasourceUtilities();
		final DriverManagerDataSource mockWorkbenchDataSource = Mockito.mock(DriverManagerDataSource.class);
		Mockito.when(mockWorkbenchDataSource.getConnection()).thenThrow(new SQLException("Could not access the database"));

		xaDatasourceUtilities.retrieveCropDatabases(mockWorkbenchDataSource);
	}
}
