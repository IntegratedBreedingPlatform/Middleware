
package org.generationcp.middleware.liquibase;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.hibernate.DataSourceProperties;
import org.generationcp.middleware.hibernate.DatasourceUtilities;
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

import liquibase.integration.spring.SpringLiquibase;

/**
 * Dynamically registers {@link SpringLiquibase} bean definitions into the application context for all crop databases + workbench. The
 * process of liquibase applying db changes happens as part of SpringLiquibase bean init sequence, see
 * {@link SpringLiquibase#afterPropertiesSet()}
 * 
 * @author Naymesh Mistry
 */
public class LiquibaseInitBean implements BeanDefinitionRegistryPostProcessor {

	private DatasourceUtilities datasourceUtilities;
	private DataSourceProperties dataSourceProperties;

	private static final Logger LOG = LoggerFactory.getLogger(LiquibaseInitBean.class);

	public LiquibaseInitBean() {
		try {
			this.datasourceUtilities = new DatasourceUtilities();
			final Resource resource = new ClassPathResource("/database.properties");
			final Properties props = PropertiesLoaderUtils.loadProperties(resource);
			this.dataSourceProperties = new DataSourceProperties(props);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to get the list of crop databases to apply liquibase update to.", e);
		}
	}

	LiquibaseInitBean(DatasourceUtilities datasourceUtilities, DataSourceProperties dataSourceProperties) {
		this.datasourceUtilities = datasourceUtilities;
		this.dataSourceProperties = dataSourceProperties;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// We do not have need overriding or adding properties to beans so this is a NOOP.
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		final DriverManagerDataSource workbenchDataSource =
				this.datasourceUtilities.getWorkbenchDataSource(this.dataSourceProperties);

		// Workbench
		LOG.debug(String.format("Creating DataSource and SpringLiquibase beans for database '%s'.",
				this.dataSourceProperties.getWorkbenchDbName()));
		this.registerBeanDefinitions(registry, this.dataSourceProperties.getWorkbenchDbName(), this.dataSourceProperties,
				"liquibase/workbench_master.xml");

		// Crop databases
		final List<String> cropDatabases = this.datasourceUtilities.retrieveCropDatabases(workbenchDataSource);
		for (final String cropDatabase : cropDatabases) {
			LOG.debug(String.format("Creating DataSource and SpringLiquibase beans for database '%s'.", cropDatabase));
			this.registerBeanDefinitions(registry, cropDatabase, this.dataSourceProperties, "liquibase/crop_master.xml");
		}
	}

	void registerBeanDefinitions(final BeanDefinitionRegistry registry, final String databaseName,
			final DataSourceProperties dataSourceProperties, final String changeLogName) {

		/* The DataSource root bean definition. */
		BeanDefinitionBuilder dataSourceBeanDefinitionBuilder =
				BeanDefinitionBuilder.rootBeanDefinition(DriverManagerDataSource.class)
				.addPropertyValue("driverClassName", "org.mariadb.jdbc.Driver") //
				.addPropertyValue("url",
						"jdbc:mysql://" + dataSourceProperties.getHost() + ":" + dataSourceProperties.getPort() + "/" + databaseName) //
				.addPropertyValue("username", dataSourceProperties.getUserName()) //
				.addPropertyValue("password", dataSourceProperties.getPassword()); //

		final String dataSourceBeanName = databaseName.toUpperCase() + "_LiquibaseDataSource";
		registry.registerBeanDefinition(dataSourceBeanName, dataSourceBeanDefinitionBuilder.getBeanDefinition());

		LOG.debug(String.format("Created data source bean defintion for database '%s' with bean name '%s'.", databaseName, dataSourceBeanName));

		/* The SpringLiquibase root bean definition. */
		BeanDefinitionBuilder springLiquibaseBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringLiquibase.class)
				.addPropertyValue("dataSource", dataSourceBeanDefinitionBuilder.getBeanDefinition()) //
				.addPropertyValue("changeLog", "classpath:" + changeLogName) //
				.addPropertyValue("dropFirst", false) //
				.addPropertyValue("shouldRun", true); // TODO drive this via profiles, for PROD we want this false.

		final String springLiquibaseBeanName = databaseName.toUpperCase() + "_SpringLiquibaseBean";
		registry.registerBeanDefinition(springLiquibaseBeanName, springLiquibaseBeanDefinitionBuilder.getBeanDefinition());

		LOG.debug(String.format("Created SpringLiquibase bean defintion for database '%s' with bean name '%s'.", databaseName,
				springLiquibaseBeanName));
	}

}
