
package org.generationcp.middleware.hibernate;

import java.util.Properties;

import org.mockito.Mockito;

public class XATestUtility {

	public static final Object DB_HOST = "testHost";
	public static final Object DB_PORT = "43306";
	public static final Object DB_USERNAME = "DbUserName";
	public static final Object DB_PASSWORD = "DbPassword";
	public static final Object DB_WORKBENCH_NAME = "TestWorkbench";
	public static final Object CONNECTIONPOOL_XADRIVER_NAME = "com.test.XADriver";
	public static final Object CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT = "300";
	public static final Object CONNECTIONPOOL_MAINTENANCE_INTERVAL = "350";
	public static final Object CONNECTIONPOOL_TEST_QUERY = "Select * from TheUniverse";
	public static final Object CONNECTIONPOOL_MIN_POOL_SIZE = "50";
	public static final Object CONNECTIONPOOL_MAX_POOL_SIZE = "100";
	public static final Object CONNECTIONPOOL_MAX_IDLE_TIME = "300";

	static XADataSourceProperties mockProperties() {
		final Properties properties = Mockito.mock(Properties.class);

		Mockito.when(properties.get(XADataSourceProperties.DB_HOST)).thenReturn(XATestUtility.DB_HOST);
		Mockito.when(properties.get(XADataSourceProperties.DB_PORT)).thenReturn(XATestUtility.DB_PORT);
		Mockito.when(properties.get(XADataSourceProperties.DB_USERNAME)).thenReturn(XATestUtility.DB_USERNAME);
		Mockito.when(properties.get(XADataSourceProperties.DB_PASSWORD)).thenReturn(XATestUtility.DB_PASSWORD);
		Mockito.when(properties.get(XADataSourceProperties.DB_WORKBENCH_NAME)).thenReturn(XATestUtility.DB_WORKBENCH_NAME);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_XADRIVER_NAME)).thenReturn(
				XATestUtility.CONNECTIONPOOL_XADRIVER_NAME);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT)).thenReturn(
				XATestUtility.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_MAINTENANCE_INTERVAL)).thenReturn(
				XATestUtility.CONNECTIONPOOL_MAINTENANCE_INTERVAL);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_TEST_QUERY)).thenReturn(XATestUtility.CONNECTIONPOOL_TEST_QUERY);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_MIN_POOL_SIZE)).thenReturn(
				XATestUtility.CONNECTIONPOOL_MIN_POOL_SIZE);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_MAX_POOL_SIZE)).thenReturn(
				XATestUtility.CONNECTIONPOOL_MAX_POOL_SIZE);
		Mockito.when(properties.get(XADataSourceProperties.CONNECTIONPOOL_MAX_IDLE_TIME)).thenReturn(
				XATestUtility.CONNECTIONPOOL_MAX_IDLE_TIME);

		final XADataSourceProperties xaDataSourceProperties = new XADataSourceProperties(properties);
		return xaDataSourceProperties;
	}
}
