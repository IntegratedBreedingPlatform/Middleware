
package org.generationcp.middleware.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.google.common.collect.ImmutableMap;

public class XABeanDefinition {

	static final String DATA_SOURCE_ATTRIBUTE = "dataSource";

	static final String DATA_SOURCE = "DataSource";

	static final String SESSION_FACTORY = "_SessionFactory";

	static final String PIN_GLOBAL_TX_TO_PHYSICAL_CONNECTION = "pinGlobalTxToPhysicalConnection";

	static final String XA_DATA_SOURCE_CLASS_NAME = "xaDataSourceClassName";

	static final String XA_PREFIX = "XA_";

	static final String PASSWORD_PROPERTY = "password";

	static final String USER = "user";

	static final String URL = "URL";

	static final String XA_PROPERTIES = "xaProperties";

	static final String BORROW_CONNECTION_TIMEOUT = "borrowConnectionTimeout";

	static final String TEST_QUERY = "testQuery";

	static final String MIN_POOL_SIZE = "minPoolSize";

	static final String MAX_POOL_SIZE = "maxPoolSize";

	static final String MAX_IDLE_TIME = "maxIdleTime";

	static final String MAINTENANCE_INTERVAL = "maintenanceInterval";

	static final String UNIQUE_RESOURCE_NAME = "uniqueResourceName";

	private DatasourceUtilities xaDatasourceUtilities = new DatasourceUtilities();

	private static final Logger LOG = LoggerFactory.getLogger(DatasourceUtilities.class);

	public XABeanDefinition() {
		this.xaDatasourceUtilities = new DatasourceUtilities();

	}

	public XABeanDefinition(final DatasourceUtilities xaDatasourceUtilities) {
		this.xaDatasourceUtilities = xaDatasourceUtilities;
	}

	/**
	 * Create all XA related beans for applicable database i.e. workbench + all applicable cropdatabases
	 * @param singleConnectionDataSource JDBC connection to the workbench database.
	 * @param registry interface to register the data source and session factory bean
	 * @param xaDataSourceProperties applicable xaDataSource properties
	 */
	void createAllXARelatedBeans(final SingleConnectionDataSource singleConnectionDataSource, final BeanDefinitionRegistry registry,
			final XADataSourceProperties xaDataSourceProperties) {
		LOG.debug("Creating datasource and session factory related beans.");
		this.createXAConnectionBeans(registry, xaDataSourceProperties.getWorkbenchDbName(), xaDataSourceProperties);

		LOG.debug("Retrieve all appliable crop database.");

		final List<String> cropDatabases = this.xaDatasourceUtilities.retrieveCropDatabases(singleConnectionDataSource);

		for (final String cropDatabase : cropDatabases) {
			LOG.debug(String.format("Creating '%s' datasource and session factory related beans.", cropDatabase));
			this.createXAConnectionBeans(registry, cropDatabase, xaDataSourceProperties);
		}

	}

	/**
	 * Create the data source and session factory beans
	 * @param registry interface for registeries that hold bean definitions
	 * @param cropDatabaseName the name of the database for which we need to create the data source and session factory beans
	 * @param xaDataSourceProperties properties values to be used when creating these beans
	 */
	void createXAConnectionBeans(final BeanDefinitionRegistry registry, final String cropDatabaseName,
			final XADataSourceProperties xaDataSourceProperties) {

		final RootBeanDefinition dataSourceBeanDefinition =
				this.xaDatasourceUtilities.createRootBeanDefinition(AtomikosDataSourceBean.class, ImmutableMap.<String, Object>of(
						"init-method", "init", "destroy-method", "close", "depends-on", "transactionManager"), this
						.getDataSourceBeanDefinitionProperties(cropDatabaseName, xaDataSourceProperties));
		final String beanName = cropDatabaseName.toUpperCase() + XABeanDefinition.DATA_SOURCE;
		registry.registerBeanDefinition(beanName, dataSourceBeanDefinition);

		LOG.debug(String.format("Created data source bean defintion for database '%s' with bean name '%s'.", cropDatabaseName, beanName));

		final ImmutableMap<String, Object> sessionFactoryBeanDefinitionProperties =
				ImmutableMap.<String, Object>of(XABeanDefinition.DATA_SOURCE_ATTRIBUTE, dataSourceBeanDefinition, "configLocation",
						xaDataSourceProperties.getHibernateConfigurationLocation(), "configurationClass",
						org.hibernate.cfg.AnnotationConfiguration.class);
		final RootBeanDefinition createRootBeanDefinition =
				this.xaDatasourceUtilities.createRootBeanDefinition(AnnotationSessionFactoryBean.class, ImmutableMap.<String, Object>of(),
						sessionFactoryBeanDefinitionProperties);
		final String sessionFactoryBeanName = this.xaDatasourceUtilities.computeSessionFactoryName(cropDatabaseName);
		registry.registerBeanDefinition(sessionFactoryBeanName, createRootBeanDefinition);

		LOG.debug(String.format("Created session factory bean defintion for database '%s' with bean name '%s'.", cropDatabaseName, sessionFactoryBeanName));

	}

	/**
	 * Get bean properties
	 *
	 * @param cropDatabaseName the database for which we want bean properties
	 * @param xaDataSourceProperties the applicable properties values
	 * @return {@link Map} of applicable properties
	 */
	Map<String, Object> getDataSourceBeanDefinitionProperties(final String cropDatabaseName,
			final XADataSourceProperties xaDataSourceProperties) {
		final Map<String, Object> dataSourceBeanDefinitionProperties = new HashMap<String, Object>();

		dataSourceBeanDefinitionProperties.put(XABeanDefinition.UNIQUE_RESOURCE_NAME,
				XABeanDefinition.XA_PREFIX + cropDatabaseName.toUpperCase() + "_" + System.currentTimeMillis());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.MAINTENANCE_INTERVAL, xaDataSourceProperties.getMaintenanceInterval());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.MAX_IDLE_TIME, xaDataSourceProperties.getMaxIdleTime());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.MAX_POOL_SIZE, xaDataSourceProperties.getMaxPoolSize());

		dataSourceBeanDefinitionProperties.put(XABeanDefinition.MIN_POOL_SIZE, xaDataSourceProperties.getMinPoolSize());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.TEST_QUERY, xaDataSourceProperties.getTestQuery());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.BORROW_CONNECTION_TIMEOUT,
				xaDataSourceProperties.getBorrowConnectionTimeout());
		dataSourceBeanDefinitionProperties.put(XABeanDefinition.XA_DATA_SOURCE_CLASS_NAME, xaDataSourceProperties.getXaDriverName());

		dataSourceBeanDefinitionProperties.put(XABeanDefinition.XA_PROPERTIES,
				this.getDatabaseConnectionProperties(cropDatabaseName, xaDataSourceProperties));
		return dataSourceBeanDefinitionProperties;
	}

	/**
	 * @param cropDatabaseName the database for which we want connection properties
	 * @param xaDataSourceProperties the applicable properties values
	 * @return database connection properties
	 */
	Properties getDatabaseConnectionProperties(final String cropDatabaseName, final XADataSourceProperties xaDataSourceProperties) {
		final Properties databaseConnectionProperties = new Properties();
		databaseConnectionProperties.setProperty(XABeanDefinition.URL, "jdbc:mysql://" + xaDataSourceProperties.getHost() + ":"
				+ xaDataSourceProperties.getPort() + "/" + cropDatabaseName);
		databaseConnectionProperties.setProperty(XABeanDefinition.USER, xaDataSourceProperties.getUserName());
		databaseConnectionProperties.setProperty(XABeanDefinition.PASSWORD_PROPERTY, xaDataSourceProperties.getPassword());
		databaseConnectionProperties.setProperty(XABeanDefinition.PIN_GLOBAL_TX_TO_PHYSICAL_CONNECTION, "true");

		return databaseConnectionProperties;
	}
}
