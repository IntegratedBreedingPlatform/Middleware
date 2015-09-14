
package org.generationcp.middleware.hibernate;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

class XADataSourceProperties {

	static final String CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT = "connectionpool.borrow.connection.timeout";

	static final String CONNECTIONPOOL_MAINTENANCE_INTERVAL = "connectionpool.maintenance.interval";

	static final String CONNECTIONPOOL_MAX_POOL_SIZE = "connectionpool.max.pool.size";

	static final String CONNECTIONPOOL_MIN_POOL_SIZE = "connectionpool.min.pool.size";

	static final String CONNECTIONPOOL_MAX_IDLE_TIME = "connectionpool.max.idle.time";

	static final String CONNECTIONPOOL_TEST_QUERY = "connectionpool.test.query";

	static final String CONNECTIONPOOL_XADRIVER_NAME = "connectionpool.xadriver.name";

	static final String DB_HOST = "db.host";

	static final String DB_PORT = "db.port";

	static final String DB_USERNAME = "db.username";

	static final String DB_WORKBENCH_NAME = "db.workbench.name";

	static final String DB_PASSWORD = "db.password";

	private String borrowConnectionTimeout = "30";

	private String host = "localhost";

	private String maintenanceInterval = "60";

	private String maxPoolSize = "50";

	private String minPoolSize = "3";

	private String port = "3306";

	private String testQuery = "Select 1";

	private String userName = "root";

	private String password = "";

	private String workbenchDbName = "workbench";

	private String xaDriverName = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";

	private String maxIdleTime = "30";

	private final String hibernateConfigurationLocation = "classpath:ibpmidware_hib.cfg.xml";

	private static final Logger LOG = LoggerFactory.getLogger(XADataSourceProperties.class);

	XADataSourceProperties(final Properties properties) {

		this.host = this.getPropertyValue(properties, this.host, XADataSourceProperties.DB_HOST);
		this.port = this.getPropertyValue(properties, this.port, XADataSourceProperties.DB_PORT);
		this.userName = this.getPropertyValue(properties, this.userName, XADataSourceProperties.DB_USERNAME);
		this.password = this.getPropertyValue(properties, this.password, XADataSourceProperties.DB_PASSWORD);
		this.workbenchDbName = this.getPropertyValue(properties, this.workbenchDbName, XADataSourceProperties.DB_WORKBENCH_NAME);
		this.xaDriverName = this.getPropertyValue(properties, this.xaDriverName, XADataSourceProperties.CONNECTIONPOOL_XADRIVER_NAME);
		this.borrowConnectionTimeout =
				this.getPropertyValue(properties, this.borrowConnectionTimeout,
						XADataSourceProperties.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT);
		this.maintenanceInterval =
				this.getPropertyValue(properties, this.maintenanceInterval, XADataSourceProperties.CONNECTIONPOOL_MAINTENANCE_INTERVAL);
		this.testQuery = this.getPropertyValue(properties, this.testQuery, XADataSourceProperties.CONNECTIONPOOL_TEST_QUERY);
		this.minPoolSize = this.getPropertyValue(properties, this.minPoolSize, XADataSourceProperties.CONNECTIONPOOL_MIN_POOL_SIZE);
		this.maxPoolSize = this.getPropertyValue(properties, this.maxPoolSize, XADataSourceProperties.CONNECTIONPOOL_MAX_POOL_SIZE);
		this.maxIdleTime = this.getPropertyValue(properties, this.maxIdleTime, XADataSourceProperties.CONNECTIONPOOL_MAX_IDLE_TIME);

	}

	private String getPropertyValue(final Properties properties, final String defaultValue, final String property) {
		final String valueInPropertyField = (String) properties.get(property);
		if (Strings.isNullOrEmpty(valueInPropertyField)) {
			LOG.info(String.format("Property value '%s' did not have any value specified in the database.properties." + " Using default value '%s'.",
					property, defaultValue));
			return defaultValue;
		}
		LOG.info(String.format("Property value '%s' found in database.properties." + " Using value specifed in properties file '%s'.", property,
				valueInPropertyField));
		return valueInPropertyField;
	}

	/**
	 * @return the borrowConnectionTimeout
	 */
	String getBorrowConnectionTimeout() {
		return this.borrowConnectionTimeout;
	}

	/**
	 * @return the host
	 */
	String getHost() {
		return this.host;
	}

	/**
	 * @return the maintenanceInterval
	 */
	String getMaintenanceInterval() {
		return this.maintenanceInterval;
	}

	/**
	 * @return the maxPoolSize
	 */
	String getMaxPoolSize() {
		return this.maxPoolSize;
	}

	/**
	 * @return the minPoolSize
	 */
	String getMinPoolSize() {
		return this.minPoolSize;
	}

	/**
	 * @return the port
	 */
	String getPort() {
		return this.port;
	}

	/**
	 * @return the testQuery
	 */
	String getTestQuery() {
		return this.testQuery;
	}

	/**
	 * @return the userName
	 */
	String getUserName() {
		return this.userName;
	}

	/**
	 * @return the workbenchDbName
	 */
	String getWorkbenchDbName() {
		return this.workbenchDbName;
	}

	/**
	 * @return the xaDriverName
	 */
	String getXaDriverName() {
		return this.xaDriverName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @return the maxIdleTime
	 */
	public String getMaxIdleTime() {
		return this.maxIdleTime;
	}

	/**
	 * @return the hibernateConfigurationLocation
	 */
	public String getHibernateConfigurationLocation() {
		return this.hibernateConfigurationLocation;
	}

}
