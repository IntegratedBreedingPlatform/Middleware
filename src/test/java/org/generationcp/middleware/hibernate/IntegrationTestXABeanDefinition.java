
package org.generationcp.middleware.hibernate;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.generationcp.middleware.hibernate.DatasourceUtilities.*;

public class IntegrationTestXABeanDefinition {

	static final String CHARACTER_ENCODING = "characterEncoding";

	static final String USE_UNICODE = "useUnicode";

	static final String CACHE_PREP_STMTS = "cachePrepStmts";

	static final String USE_SERVER_PREP_STMTS = "useServerPrepStmts";

	static final String DATA_SOURCE_ATTRIBUTE = "dataSource";

	static final String DATA_SOURCE = "DataSource";

	static final String PIN_GLOBAL_TX_TO_PHYSICAL_CONNECTION = "pinGlobalTxToPhysicalConnection";

	static final String XA_DATA_SOURCE_CLASS_NAME = "xaDataSourceClassName";

	static final String XA_PREFIX = "XA_";

	static final String PASSWORD_PROPERTY = "password";

	static final String USER = "user";

	static final String URL = "URL";

	static final String XA_PROPERTIES = "xaProperties";

	static final String BORROW_CONNECTION_TIMEOUT = "borrowConnectionTimeout";
	
	static final String TEST_QUERY = "testQuery";
	
	static final String REAP_TIMEOUT = "reapTimeout";

	static final String MIN_POOL_SIZE = "minPoolSize";

	static final String MAX_POOL_SIZE = "maxPoolSize";

	static final String MAX_IDLE_TIME = "maxIdleTime";

	static final String MAINTENANCE_INTERVAL = "maintenanceInterval";

	static final String UNIQUE_RESOURCE_NAME = "uniqueResourceName";

	private DatasourceUtilities xaDatasourceUtilities;

	private static final Logger LOG = LoggerFactory.getLogger(DatasourceUtilities.class);

	

	public IntegrationTestXABeanDefinition() {
		this.xaDatasourceUtilities = new DatasourceUtilities();

	}

	/**
	 * Create all XA related beans for applicable database i.e. workbench + all applicable cropdatabases
	 * 
	 * @param registry interface to register the data source and session factory bean
	 * @param xaDataSourceProperties applicable xaDataSource properties
	 */
	void createAllXARelatedBeans(final BeanDefinitionRegistry registry,
			final DataSourceProperties xaDataSourceProperties, final String cropName) {
		LOG.debug("Creating datasource and session factory related beans.");
		this.createXAConnectionBeans(registry, xaDataSourceProperties.getWorkbenchDbName(), xaDataSourceProperties);

		LOG.debug("Connect to Crop Database");

		LOG.debug(String.format("Creating '%s' datasource and session factory related beans.", cropName));
			this.createXAConnectionBeans(registry, cropName, xaDataSourceProperties);

	}

	/**
	 * Create the data source and session factory beans
	 * @param registry interface for registeries that hold bean definitions
	 * @param cropDatabaseName the name of the database for which we need to create the data source and session factory beans
	 * @param xaDataSourceProperties properties values to be used when creating these beans
	 */
	void createXAConnectionBeans(final BeanDefinitionRegistry registry, final String cropDatabaseName,
			final DataSourceProperties xaDataSourceProperties) {

		final RootBeanDefinition dataSourceBeanDefinition =
				this.xaDatasourceUtilities.createRootBeanDefinition(AtomikosDataSourceBean.class, ImmutableMap.of(
						"init-method", "init", "destroy-method", "close", "depends-on", "transactionManager"), this
						.getDataSourceBeanDefinitionProperties(cropDatabaseName, xaDataSourceProperties));
		final String beanName = cropDatabaseName.toUpperCase() + IntegrationTestXABeanDefinition.DATA_SOURCE;
		registry.registerBeanDefinition(beanName, dataSourceBeanDefinition);

		LOG.debug(String.format("Created data source bean defintion for database '%s' with bean name '%s'.", cropDatabaseName, beanName));

		final ImmutableMap<String, Object> sessionFactoryBeanDefinitionProperties =
				ImmutableMap.of(IntegrationTestXABeanDefinition.DATA_SOURCE_ATTRIBUTE, dataSourceBeanDefinition, "configLocation",
						xaDataSourceProperties.getHibernateConfigurationLocation(), "configurationClass",
						org.hibernate.cfg.AnnotationConfiguration.class);
		final RootBeanDefinition createRootBeanDefinition =
				this.xaDatasourceUtilities.createRootBeanDefinition(AnnotationSessionFactoryBean.class, ImmutableMap.of(),
						sessionFactoryBeanDefinitionProperties);
		final String sessionFactoryBeanName = computeSessionFactoryName(cropDatabaseName);
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
			final DataSourceProperties xaDataSourceProperties) {
		final Map<String, Object> dataSourceBeanDefinitionProperties = new HashMap<>();

		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.UNIQUE_RESOURCE_NAME,
				IntegrationTestXABeanDefinition.XA_PREFIX + cropDatabaseName.toUpperCase() + "_" + System.currentTimeMillis());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.MAINTENANCE_INTERVAL, xaDataSourceProperties.getMaintenanceInterval());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.MAX_IDLE_TIME, xaDataSourceProperties.getMaxIdleTime());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.MAX_POOL_SIZE, xaDataSourceProperties.getMaxPoolSize());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.MIN_POOL_SIZE, xaDataSourceProperties.getMinPoolSize());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.REAP_TIMEOUT, xaDataSourceProperties.getReapTimeout());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.TEST_QUERY, xaDataSourceProperties.getTestQuery());

		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.BORROW_CONNECTION_TIMEOUT,
				xaDataSourceProperties.getBorrowConnectionTimeout());
		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.XA_DATA_SOURCE_CLASS_NAME, xaDataSourceProperties.getXaDriverName());

		dataSourceBeanDefinitionProperties.put(IntegrationTestXABeanDefinition.XA_PROPERTIES,
				this.getDatabaseConnectionProperties(cropDatabaseName, xaDataSourceProperties));
		return dataSourceBeanDefinitionProperties;
	}

	/**
	 * @param cropDatabaseName the database for which we want connection properties
	 * @param xaDataSourceProperties the applicable properties values
	 * @return database connection properties
	 */
	Properties getDatabaseConnectionProperties(final String cropDatabaseName, final DataSourceProperties xaDataSourceProperties) {
		final Properties databaseConnectionProperties = new Properties();
		databaseConnectionProperties.setProperty(IntegrationTestXABeanDefinition.URL, "jdbc:mysql://" + xaDataSourceProperties.getHost() + ":"
				+ xaDataSourceProperties.getPort() + "/" + cropDatabaseName);
		databaseConnectionProperties.setProperty(IntegrationTestXABeanDefinition.USER, xaDataSourceProperties.getUserName());
		databaseConnectionProperties.setProperty(IntegrationTestXABeanDefinition.PASSWORD_PROPERTY, xaDataSourceProperties.getPassword());
		databaseConnectionProperties.setProperty(IntegrationTestXABeanDefinition.PIN_GLOBAL_TX_TO_PHYSICAL_CONNECTION, "true");
		databaseConnectionProperties.setProperty(USE_SERVER_PREP_STMTS, "true");
		databaseConnectionProperties.setProperty(CACHE_PREP_STMTS, "true");
		databaseConnectionProperties.setProperty(USE_UNICODE, "true");
		databaseConnectionProperties.setProperty(CHARACTER_ENCODING, "UTF-8");

		// useLocalSessionState property form driver doc: Should the driver use the in-transaction state provided by the MySQL protocol to
		// determine if a commit() or rollback() should actually be sent to the database?
		// Yes we want to, otherwise very large number of "select @@session.tx_read_only" queries are run by driver before each query which
		// we consider wasteful.
		databaseConnectionProperties.setProperty("useLocalSessionState", "true");

		// cacheServerConfiguration from driver doc: Should the driver cache the results of 'SHOW VARIABLES' and 'SHOW COLLATION' on a
		// per-URL basis? Yes we want to.
		databaseConnectionProperties.setProperty("cacheServerConfiguration", "true");
		
		// Removing this property will result in a huge performance impact on the pedigree generation
		// The stored procedure getGermplasmWithNamesAndAncestry in crop databases uses group concat and thus this property
		// This ensures that strings in the stored procedure are not silently truncated.
		databaseConnectionProperties.setProperty("sessionVariables", "group_concat_max_len=500000");

		return databaseConnectionProperties;
	}
}
