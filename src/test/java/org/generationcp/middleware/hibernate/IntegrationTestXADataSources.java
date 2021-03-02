
package org.generationcp.middleware.hibernate;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Helps us create XA Session Factories for all crop database. This class must be declared as a singleton bean via annotations or via xml
 * config.
 */

public class IntegrationTestXADataSources implements BeanDefinitionRegistryPostProcessor {

	private IntegrationTestXABeanDefinition xaBeanDefinition;
	private DataSourceProperties xaDataSourceProperties;
	private String cropDbname;

	public IntegrationTestXADataSources(final String cropDbname) {
		try {
			this.xaBeanDefinition = new IntegrationTestXABeanDefinition();
			final Resource resource = new ClassPathResource("/database.properties");
			final Properties props = PropertiesLoaderUtils.loadProperties(resource);
			this.xaDataSourceProperties = new DataSourceProperties(props);
			this.cropDbname = cropDbname == null ? "ibdbv2_maize_merged" : cropDbname;
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Unable to get the list of database that we need to register. Please contact your administrator for further assistance.",
					e);
		}
	}


	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		this.xaBeanDefinition.createAllXARelatedBeans(registry, this.xaDataSourceProperties, this.cropDbname);
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// Do not need to add any functionality to this method because we just need to override postProcessBeanDefinitionRegistry
	}

}
