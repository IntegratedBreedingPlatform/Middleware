
package org.generationcp.middleware.liquibase;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.hibernate.XADataSourceProperties;
import org.generationcp.middleware.hibernate.XADatasourceUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import liquibase.integration.spring.SpringLiquibase;

public class LiquibaseInitBean implements BeanDefinitionRegistryPostProcessor {

	private XADatasourceUtilities datasourceUtilities;
	private XADataSourceProperties dataSourceProperties;

	private static final Logger LOG = LoggerFactory.getLogger(LiquibaseInitBean.class);

	public LiquibaseInitBean() {
		try {
			this.datasourceUtilities = new XADatasourceUtilities();
			final Resource resource = new ClassPathResource("/database.properties");
			final Properties props = PropertiesLoaderUtils.loadProperties(resource);
			this.dataSourceProperties = new XADataSourceProperties(props);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to get the list of crop databases to apply liquibase update to.", e);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// NOOP
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		final SingleConnectionDataSource singleConnectionDataSource =
				this.datasourceUtilities.getSingleConnectionDataSource(this.dataSourceProperties);

		final List<String> cropDatabases = this.datasourceUtilities.retrieveCropDatabases(singleConnectionDataSource);

		for (final String cropDatabase : cropDatabases) {
			LOG.debug(String.format("Creating '%s' DataSource and SpringLiquibase beans.", cropDatabase));
			this.registerBeanDefinitions(registry, cropDatabase, this.dataSourceProperties);
		}
	}

	void registerBeanDefinitions(final BeanDefinitionRegistry registry, final String cropDatabaseName,
			final XADataSourceProperties dataSourceProperties) {

		/* The DataSource root bean definition. */
		BeanDefinitionBuilder dataSourceBeanDefinitionBuilder =
				BeanDefinitionBuilder.rootBeanDefinition(DriverManagerDataSource.class)
				.addPropertyValue("driverClassName", "com.mysql.jdbc.Driver") //
				.addPropertyValue("url",
						"jdbc:mysql://" + dataSourceProperties.getHost() + ":" + dataSourceProperties.getPort() + "/" + cropDatabaseName) //
				.addPropertyValue("username", dataSourceProperties.getUserName()) //
				.addPropertyValue("password", dataSourceProperties.getPassword()); //

		final String dataSourceBeanName = cropDatabaseName.toUpperCase() + "_LiquibaseDataSource";
		registry.registerBeanDefinition(dataSourceBeanName, dataSourceBeanDefinitionBuilder.getBeanDefinition());

		LOG.debug(String.format("Created data source bean defintion for database '%s' with bean name '%s'.", cropDatabaseName, dataSourceBeanName));

		/* The SpringLiquibase root bean definition. */
		BeanDefinitionBuilder springLiquibaseBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringLiquibase.class)
				.addPropertyValue("dataSource", dataSourceBeanDefinitionBuilder.getBeanDefinition()) //
				.addPropertyValue("changeLog", "classpath:crop_db_changelog.xml") //
				.addPropertyValue("dropFirst", false) //
				.addPropertyValue("shouldRun", true); // TODO drive this via profiles, for PROD we want this false.

		final String springLiquibaseBeanName = cropDatabaseName.toUpperCase() + "_SpringLiquibaseBean";
		registry.registerBeanDefinition(springLiquibaseBeanName, springLiquibaseBeanDefinitionBuilder.getBeanDefinition());

		LOG.debug(String.format("Created SpringLiquibase bean defintion for database '%s' with bean name '%s'.", cropDatabaseName,
				springLiquibaseBeanName));
	}

}
