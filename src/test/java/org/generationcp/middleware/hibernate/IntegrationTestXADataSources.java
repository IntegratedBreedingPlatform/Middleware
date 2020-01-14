
package org.generationcp.middleware.hibernate;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.util.Properties;

/**
 * Helps us create XA Session Factories for all crop database. This class must be declared as a singleton bean via annotations or via xml
 * config.
 */

public class IntegrationTestXADataSources implements BeanDefinitionRegistryPostProcessor {

	private DatasourceUtilities xaDatasourceUtilities;
	private IntegrationTestXABeanDefinition xaBeanDefinition;
	private DataSourceProperties xaDataSourceProperties;
	private String cropDbname;

	public IntegrationTestXADataSources(final String cropDbname) {
		try {
			this.xaDatasourceUtilities = new DatasourceUtilities();
			this.xaBeanDefinition = new IntegrationTestXABeanDefinition();
			final Resource resource = new ClassPathResource("/database.properties");
			final Properties props = PropertiesLoaderUtils.loadProperties(resource);
			this.xaDataSourceProperties = new DataSourceProperties(props);
			this.cropDbname = cropDbname;
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Unable to get the list of database that we need to register. Please contact your administrator for further assistance.",
					e);
		}
	}


	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		final DriverManagerDataSource singleConnectionDataSource =
				this.xaDatasourceUtilities.getWorkbenchDataSource(this.xaDataSourceProperties);
		this.xaBeanDefinition.createAllXARelatedBeans(singleConnectionDataSource, registry, this.xaDataSourceProperties, this.cropDbname);
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// Do not need to add any functionality to this method because we just need to override postProcessBeanDefinitionRegistry
	}

}
