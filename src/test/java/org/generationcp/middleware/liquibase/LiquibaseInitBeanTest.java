
package org.generationcp.middleware.liquibase;

import java.util.Properties;

import org.generationcp.middleware.hibernate.DataSourceProperties;
import org.generationcp.middleware.hibernate.DatasourceUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.google.common.collect.Lists;

public class LiquibaseInitBeanTest {

	@Test
	public void testBeanDefinitionRegistations() {

		DatasourceUtilities dsUtils = Mockito.mock(DatasourceUtilities.class);
		DataSourceProperties dsProperties = new DataSourceProperties(new Properties());

		Mockito.doReturn(Lists.newArrayList("ibdbv2_maize_merged", "ibdbv2_wheat_merged")).when(dsUtils)
				.retrieveCropDatabases(Mockito.any(SingleConnectionDataSource.class));

		BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();

		LiquibaseInitBean bean = new LiquibaseInitBean(dsUtils, dsProperties);

		bean.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);

		Assert.assertEquals(
				"Total of 6 beans should be registered. 2 beans per database (1 DriverManagerDataSource bean 1 SpringLiquibase bean) x 3 databases (workbench, maize, wheat).",
				6, beanDefinitionRegistry.getBeanDefinitionCount());

		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("WORKBENCH_LiquibaseDataSource"));
		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("IBDBV2_MAIZE_MERGED_LiquibaseDataSource"));
		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("IBDBV2_WHEAT_MERGED_LiquibaseDataSource"));

		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("WORKBENCH_SpringLiquibaseBean"));
		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("IBDBV2_MAIZE_MERGED_SpringLiquibaseBean"));
		Assert.assertTrue(beanDefinitionRegistry.containsBeanDefinition("IBDBV2_WHEAT_MERGED_SpringLiquibaseBean"));
	}

}
