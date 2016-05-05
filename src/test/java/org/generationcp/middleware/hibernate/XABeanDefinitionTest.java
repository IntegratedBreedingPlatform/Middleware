
package org.generationcp.middleware.hibernate;

import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.google.common.collect.ImmutableList;

public class XABeanDefinitionTest {

	private final XADataSourceProperties xaDataSourceProperties = XATestUtility.mockProperties();
	private final String cropDatabaseName = "TestCrop";

	@Test
	public void testGetDatabaseConnectionProperties() throws Exception {

		final XABeanDefinition xaBeanDefinition = new XABeanDefinition();
		final Properties databaseConnectionProperties =
				xaBeanDefinition.getDatabaseConnectionProperties(this.cropDatabaseName, this.xaDataSourceProperties);

		Assert.assertEquals(XATestUtility.DB_USERNAME, databaseConnectionProperties.getProperty(XABeanDefinition.USER));
		Assert.assertEquals(XATestUtility.DB_PASSWORD, databaseConnectionProperties.getProperty(XABeanDefinition.PASSWORD_PROPERTY));
		Assert.assertEquals("jdbc:mysql://" + this.xaDataSourceProperties.getHost() + ":" + this.xaDataSourceProperties.getPort() + "/"
				+ this.cropDatabaseName, databaseConnectionProperties.getProperty(XABeanDefinition.URL));
		Assert.assertEquals("true", databaseConnectionProperties.getProperty(XABeanDefinition.PIN_GLOBAL_TX_TO_PHYSICAL_CONNECTION));

	}

	@Test
	public void testGetDataSourceBeanDefinitionProperties() throws Exception {
		final XABeanDefinition xaBeanDefinition = new XABeanDefinition();
		final Map<String, Object> dataSourceBeanDefinitionProperties =
				xaBeanDefinition.getDataSourceBeanDefinitionProperties(this.cropDatabaseName, this.xaDataSourceProperties);

		Assert.assertTrue(((String) dataSourceBeanDefinitionProperties.get(XABeanDefinition.UNIQUE_RESOURCE_NAME))
				.startsWith(XABeanDefinition.XA_PREFIX + this.cropDatabaseName.toUpperCase()));

		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_MAINTENANCE_INTERVAL,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.MAINTENANCE_INTERVAL));

		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_MAINTENANCE_INTERVAL,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.MAINTENANCE_INTERVAL));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_MAX_IDLE_TIME,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.MAX_IDLE_TIME));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_MAX_POOL_SIZE,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.MAX_POOL_SIZE));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_MIN_POOL_SIZE,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.MIN_POOL_SIZE));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_TEST_QUERY, dataSourceBeanDefinitionProperties.get(XABeanDefinition.TEST_QUERY));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.BORROW_CONNECTION_TIMEOUT));

		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_XADRIVER_NAME,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.XA_DATA_SOURCE_CLASS_NAME));
		Assert.assertEquals(XATestUtility.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT,
				dataSourceBeanDefinitionProperties.get(XABeanDefinition.BORROW_CONNECTION_TIMEOUT));
		Assert.assertTrue(dataSourceBeanDefinitionProperties.get(XABeanDefinition.XA_PROPERTIES).getClass().equals(Properties.class));
	}

	@Test
	public void testCreateAllXADataSources() throws Exception {

		final DatasourceUtilities mockXaDatasourceUtilities = Mockito.mock(DatasourceUtilities.class);
		final XABeanDefinition xaBeanDefinition = new XABeanDefinition(mockXaDatasourceUtilities);
		final BeanDefinitionRegistry mockBeanDefinitionRegistry = Mockito.mock(BeanDefinitionRegistry.class);
		final SingleConnectionDataSource mockSingleConnectionDataSource = Mockito.mock(SingleConnectionDataSource.class);

		Mockito.when(mockXaDatasourceUtilities.retrieveCropDatabases((SingleConnectionDataSource) Matchers.anyObject())).thenReturn(
				ImmutableList.<String>of("Test_Merged"));
		final RootBeanDefinition mockRootBeanDefinition = Mockito.mock(RootBeanDefinition.class);
		Mockito.when(
				mockXaDatasourceUtilities.createRootBeanDefinition(Matchers.any(Class.class), Matchers.any(Map.class),
						Matchers.any(Map.class))).thenReturn(mockRootBeanDefinition);

		xaBeanDefinition.createAllXARelatedBeans(mockSingleConnectionDataSource, mockBeanDefinitionRegistry,
				this.xaDataSourceProperties);

		Mockito.verify(mockXaDatasourceUtilities, Mockito.times(2)).createRootBeanDefinition(Matchers.eq(AtomikosDataSourceBean.class),
				Matchers.any(Map.class), Matchers.any(Map.class));

		Mockito.verify(mockXaDatasourceUtilities, Mockito.times(2)).createRootBeanDefinition(
				Matchers.eq(AnnotationSessionFactoryBean.class), Matchers.any(Map.class), Matchers.any(Map.class));

		Mockito.verify(mockBeanDefinitionRegistry, Mockito.times(1)).registerBeanDefinition(
				"Test_Merged".toUpperCase() + XABeanDefinition.DATA_SOURCE, mockRootBeanDefinition);
		Mockito.verify(mockBeanDefinitionRegistry, Mockito.times(1)).registerBeanDefinition(
				this.xaDataSourceProperties.getWorkbenchDbName().toUpperCase() + XABeanDefinition.DATA_SOURCE, mockRootBeanDefinition);

	}
}
