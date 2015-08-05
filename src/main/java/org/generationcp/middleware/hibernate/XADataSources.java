
package org.generationcp.middleware.hibernate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.google.common.collect.ImmutableMap;

/**
 * Bean to register all crop database.
 */

public class XADataSources implements BeanDefinitionRegistryPostProcessor, InitializingBean {

	private static final String XA_DATA_SOURCE_CLASS_NAME = "xaDataSourceClassName";

	private static final String DATA_SOURCE_ATTRIBUTE = "dataSource";

	private static final String DATA_SOURCE = "DataSource";

	private static final String SESSION_FACTORY = "_SessionFactory";

	private static final String XA_PREFIX = "XA_";

	private static final String PASSWORD_PROPERTY = "password";

	private static final String USER = "user";

	private static final String URL = "URL";

	private static final String XA_PROPERTIES = "xaProperties";

	private static final String BORROW_CONNECTION_TIMEOUT = "borrowConnectionTimeout";

	private static final String TEST_QUERY = "testQuery";

	private static final String MIN_POOL_SIZE = "minPoolSize";

	private static final String MAX_POOL_SIZE = "maxPoolSize";

	private static final String MAX_IDLE_TIME = "maxIdleTime";

	private static final String MAINTENANCE_INTERVAL = "maintenanceInterval";

	private static final String UNIQUE_RESOURCE_NAME = "uniqueResourceName";

	private DataSource workbenchDataSource;

	private String userName = "root";

	private String password = "";

	private String xaDriverName = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";

	private String borrowConnectionTimeout = "30";

	private String maxIdleTime = "60";

	private String maintenanceInterval = "60";

	private String testQuery = "Select 1";

	private String maxPoolSize = "50";

	private String minPoolSize = "3";

	private String workbenchJdbcUrl;

	private String hibernateConfigurationLocation = "classpath:ibpmidware_hib.cfg.xml";

	private String host;

	private String port;
	
	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		Connection connection = null;
		try {
			
			final Resource resource = new ClassPathResource("/database.properties");
			final Properties props = PropertiesLoaderUtils.loadProperties(resource);
			this.host = (String) props.get("db.host");
			this.port = (String) props.get("db.port");
			this.userName = (String) props.get("db.username");
			this.password = (String) props.get("db.password");
			String dbName = (String) props.get("db.workbench.name");
			
			createXADataSource(registry, dbName);

			Class.forName("com.mysql.jdbc.Driver");
			connection =
					DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbName, userName, password);

			// final Connection connection = workbenchDataSource.getConnection();
			final PreparedStatement preparedStatement = connection.prepareStatement("show databases like ?");
			preparedStatement.setString(1, "ibdbv2%");
			final ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				final String cropDatabaseName = rs.getString(1);
				createXADataSource(registry, cropDatabaseName);
			}
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(preparedStatement);
		} catch (final SQLException | IOException | ClassNotFoundException e) {
			throw new IllegalStateException(
					"Unable to get the list of database that we need to register. Please contanct your administrator for further assistance.",
					e);
		} finally {
			// Use this helper method so we don't have to check for null
			JdbcUtils.closeConnection(connection);
		}
	}

	private void createXADataSource(final BeanDefinitionRegistry registry, final String cropDatabaseName) {

		final RootBeanDefinition dataSourceBeanDefinition =
				createRootBeanDefinition(AtomikosDataSourceBean.class,
						ImmutableMap.<String, Object>of("init-method", "init", "destroy-method", "close", "depends-on", "transactionManager"),
						getDataSourceBeanDefinitionProperties(cropDatabaseName));
		registry.registerBeanDefinition(cropDatabaseName.toUpperCase() + DATA_SOURCE, dataSourceBeanDefinition);

		final ImmutableMap<String, Object> sessionFactoryBeanDefinitionProperties =
				ImmutableMap.<String, Object>of(DATA_SOURCE_ATTRIBUTE, dataSourceBeanDefinition, "configLocation",
						hibernateConfigurationLocation, "configurationClass", org.hibernate.cfg.AnnotationConfiguration.class);
		final RootBeanDefinition createRootBeanDefinition =
				createRootBeanDefinition(AnnotationSessionFactoryBean.class, ImmutableMap.<String, Object>of(),
						sessionFactoryBeanDefinitionProperties);
		registry.registerBeanDefinition(computeSessionFactoryName(cropDatabaseName), createRootBeanDefinition);
	}

	public static String computeSessionFactoryName(final String cropDatabaseName) {
		return cropDatabaseName.toUpperCase() + SESSION_FACTORY;
	}

	private RootBeanDefinition createRootBeanDefinition(final Class<?> klass, final Map<String, Object> attributes,
			final Map<String, Object> properties) {
		final RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(klass);
		rootBeanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
		rootBeanDefinition.setTargetType(klass);

		for (final Entry<String, Object> attributeEntry : attributes.entrySet()) {
			rootBeanDefinition.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
		}

		final MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();

		for (final Entry<String, Object> propertieEntry : properties.entrySet()) {

			mutablePropertyValues.add(propertieEntry.getKey(), propertieEntry.getValue());
		}
		rootBeanDefinition.setPropertyValues(mutablePropertyValues);
		return rootBeanDefinition;
	}

	private Map<String, Object> getDataSourceBeanDefinitionProperties(final String cropDatabaseName) {
		final Map<String, Object> dataSourceBeanDefinitionProperties = new HashMap<String, Object>();

		dataSourceBeanDefinitionProperties.put(UNIQUE_RESOURCE_NAME,
				XA_PREFIX + cropDatabaseName.toUpperCase() + "_" + System.currentTimeMillis());
		dataSourceBeanDefinitionProperties.put(MAINTENANCE_INTERVAL, maintenanceInterval);
		dataSourceBeanDefinitionProperties.put(MAX_IDLE_TIME, maxIdleTime);
		dataSourceBeanDefinitionProperties.put(MAX_POOL_SIZE, maxPoolSize);

		dataSourceBeanDefinitionProperties.put(MIN_POOL_SIZE, minPoolSize);
		dataSourceBeanDefinitionProperties.put(TEST_QUERY, testQuery);
		dataSourceBeanDefinitionProperties.put(BORROW_CONNECTION_TIMEOUT, borrowConnectionTimeout);
		dataSourceBeanDefinitionProperties.put(XA_DATA_SOURCE_CLASS_NAME, xaDriverName);

		dataSourceBeanDefinitionProperties.put(XA_PROPERTIES, getDatabaseConnectionProperties(cropDatabaseName));
		return dataSourceBeanDefinitionProperties;
	}

	private Properties getDatabaseConnectionProperties(final String cropDatabaseName) {
		final Properties databaseConnectionProperties = new Properties();
		databaseConnectionProperties.setProperty(URL, "jdbc:mysql://" + host + ":" + port + "/" + cropDatabaseName);
		databaseConnectionProperties.setProperty(USER, userName);
		databaseConnectionProperties.setProperty(PASSWORD_PROPERTY, password);
		return databaseConnectionProperties;
	}

	/**
	 * @return the workbenchDataSource
	 */
	public DataSource getWorkbenchDataSource() {
		return workbenchDataSource;
	}

	/**
	 * @param workbenchDataSource the workbenchDataSource to set
	 */
	public void setWorkbenchDataSource(DataSource workbenchDataSource) {
		this.workbenchDataSource = workbenchDataSource;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the xaDriverName
	 */
	public String getXaDriverName() {
		return xaDriverName;
	}

	/**
	 * @param xaDriverName the xaDriverName to set
	 */
	public void setXaDriverName(String xaDriverName) {
		this.xaDriverName = xaDriverName;
	}

	/**
	 * @return the borrowConnectionTimeout
	 */
	public String getBorrowConnectionTimeout() {
		return borrowConnectionTimeout;
	}

	/**
	 * @param borrowConnectionTimeout the borrowConnectionTimeout to set
	 */
	public void setBorrowConnectionTimeout(String borrowConnectionTimeout) {
		this.borrowConnectionTimeout = borrowConnectionTimeout;
	}

	/**
	 * @return the maxIdleTime
	 */
	public String getMaxIdleTime() {
		return maxIdleTime;
	}

	/**
	 * @param maxIdleTime the maxIdleTime to set
	 */
	public void setMaxIdleTime(String maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	/**
	 * @return the maintenanceInterval
	 */
	public String getMaintenanceInterval() {
		return maintenanceInterval;
	}

	/**
	 * @param maintenanceInterval the maintenanceInterval to set
	 */
	public void setMaintenanceInterval(String maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}

	/**
	 * @return the testQuery
	 */
	public String getTestQuery() {
		return testQuery;
	}

	/**
	 * @param testQuery the testQuery to set
	 */
	public void setTestQuery(String testQuery) {
		this.testQuery = testQuery;
	}

	/**
	 * @return the maxPoolSize
	 */
	public String getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * @param maxPoolSize the maxPoolSize to set
	 */
	public void setMaxPoolSize(String maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * @return the minPoolSize
	 */
	public String getMinPoolSize() {
		return minPoolSize;
	}

	/**
	 * @param minPoolSize the minPoolSize to set
	 */
	public void setMinPoolSize(String minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	/**
	 * @return the workbenchJdbcUrl
	 */
	public String getWorkbenchJdbcUrl() {
		return workbenchJdbcUrl;
	}

	/**
	 * @param workbenchJdbcUrl the workbenchJdbcUrl to set
	 */
	public void setWorkbenchJdbcUrl(String workbenchJdbcUrl) {
		this.workbenchJdbcUrl = workbenchJdbcUrl;
	}

	/**
	 * @return the hibernateConfigurationLocation
	 */
	public String getHibernateConfigurationLocation() {
		return hibernateConfigurationLocation;
	}

	/**
	 * @param hibernateConfigurationLocation the hibernateConfigurationLocation to set
	 */
	public void setHibernateConfigurationLocation(String hibernateConfigurationLocation) {
		this.hibernateConfigurationLocation = hibernateConfigurationLocation;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

}
