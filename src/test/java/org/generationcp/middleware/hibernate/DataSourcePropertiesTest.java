
package org.generationcp.middleware.hibernate;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DataSourcePropertiesTest {


	private final Properties properties = Mockito.mock(Properties.class);

	@Test
	public void testXADataSourceProperties() throws Exception {

		final DataSourceProperties xaDataSourceProperties = XATestUtility.mockProperties();

		Assert.assertEquals(xaDataSourceProperties.getHost(), XATestUtility.DB_HOST);
		Assert.assertEquals(xaDataSourceProperties.getPort(), XATestUtility.DB_PORT);
		Assert.assertEquals(xaDataSourceProperties.getUserName(), XATestUtility.DB_USERNAME);
		Assert.assertEquals(xaDataSourceProperties.getPassword(), XATestUtility.DB_PASSWORD);

		Assert.assertEquals(xaDataSourceProperties.getWorkbenchDbName(), XATestUtility.DB_WORKBENCH_NAME);
		Assert.assertEquals(xaDataSourceProperties.getXaDriverName(), XATestUtility.CONNECTIONPOOL_XADRIVER_NAME);
		Assert.assertEquals(xaDataSourceProperties.getBorrowConnectionTimeout(), XATestUtility.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT);
		Assert.assertEquals(xaDataSourceProperties.getMaintenanceInterval(), XATestUtility.CONNECTIONPOOL_MAINTENANCE_INTERVAL);
		Assert.assertEquals(xaDataSourceProperties.getReapTimeout(), XATestUtility.CONNECTIONPOOL_REAP_TIMEOUT);
		Assert.assertEquals(xaDataSourceProperties.getMinPoolSize(), XATestUtility.CONNECTIONPOOL_MIN_POOL_SIZE);
		Assert.assertEquals(xaDataSourceProperties.getMaxPoolSize(), XATestUtility.CONNECTIONPOOL_MAX_POOL_SIZE);
		Assert.assertEquals(xaDataSourceProperties.getMaxIdleTime(), XATestUtility.CONNECTIONPOOL_MAX_IDLE_TIME);

		Assert.assertEquals(xaDataSourceProperties.getHibernateConfigurationLocation(), "classpath:ibpmidware_hib.cfg.xml");

	}

	@Test
	public void testDefaultValues() throws Exception {

		final DataSourceProperties xaDataSourceProperties = new DataSourceProperties(this.properties);

		Assert.assertEquals(xaDataSourceProperties.getHost(), "localhost");
		Assert.assertEquals(xaDataSourceProperties.getPort(), "3306");
		Assert.assertEquals(xaDataSourceProperties.getUserName(), "root");
		Assert.assertEquals(xaDataSourceProperties.getPassword(), "");

		Assert.assertEquals(xaDataSourceProperties.getWorkbenchDbName(), "workbench");
		Assert.assertEquals(xaDataSourceProperties.getXaDriverName(), "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		Assert.assertEquals(xaDataSourceProperties.getBorrowConnectionTimeout(), "30");
		Assert.assertEquals(xaDataSourceProperties.getMaintenanceInterval(), "60");
		Assert.assertEquals(xaDataSourceProperties.getReapTimeout(), "300");
		Assert.assertEquals(xaDataSourceProperties.getMinPoolSize(), "3");
		Assert.assertEquals(xaDataSourceProperties.getMaxPoolSize(), "100");
		Assert.assertEquals(xaDataSourceProperties.getMaxIdleTime(), "120");

		Assert.assertEquals(xaDataSourceProperties.getHibernateConfigurationLocation(), "classpath:ibpmidware_hib.cfg.xml");

	}
}
