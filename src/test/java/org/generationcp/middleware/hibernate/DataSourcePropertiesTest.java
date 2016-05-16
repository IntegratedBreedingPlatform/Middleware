
package org.generationcp.middleware.hibernate;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DataSourcePropertiesTest {


	private final Properties properties = Mockito.mock(Properties.class);

	@Test
	public void testXADataSourceProperties() throws Exception {

		final DataSourceProperties xaDataSourceProperties = DataSourceTestUtility.mockProperties();

		Assert.assertEquals(xaDataSourceProperties.getHost(), DataSourceTestUtility.DB_HOST);
		Assert.assertEquals(xaDataSourceProperties.getPort(), DataSourceTestUtility.DB_PORT);
		Assert.assertEquals(xaDataSourceProperties.getUserName(), DataSourceTestUtility.DB_USERNAME);
		Assert.assertEquals(xaDataSourceProperties.getPassword(), DataSourceTestUtility.DB_PASSWORD);

		Assert.assertEquals(xaDataSourceProperties.getWorkbenchDbName(), DataSourceTestUtility.DB_WORKBENCH_NAME);
		Assert.assertEquals(xaDataSourceProperties.getXaDriverName(), DataSourceTestUtility.CONNECTIONPOOL_XADRIVER_NAME);
		Assert.assertEquals(xaDataSourceProperties.getBorrowConnectionTimeout(), DataSourceTestUtility.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT);
		Assert.assertEquals(xaDataSourceProperties.getMaintenanceInterval(), DataSourceTestUtility.CONNECTIONPOOL_MAINTENANCE_INTERVAL);
		Assert.assertEquals(xaDataSourceProperties.getReapTimeout(), DataSourceTestUtility.CONNECTIONPOOL_REAP_TIMEOUT);
		Assert.assertEquals(xaDataSourceProperties.getMinPoolSize(), DataSourceTestUtility.CONNECTIONPOOL_MIN_POOL_SIZE);
		Assert.assertEquals(xaDataSourceProperties.getMaxPoolSize(), DataSourceTestUtility.CONNECTIONPOOL_MAX_POOL_SIZE);
		Assert.assertEquals(xaDataSourceProperties.getMaxIdleTime(), DataSourceTestUtility.CONNECTIONPOOL_MAX_IDLE_TIME);

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
