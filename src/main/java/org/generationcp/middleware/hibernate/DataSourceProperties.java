
package org.generationcp.middleware.hibernate;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DataSourceProperties {

	static final String CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT = "connectionpool.borrow.connection.timeout";

	static final String CONNECTIONPOOL_MAINTENANCE_INTERVAL = "connectionpool.maintenance.interval";

	static final String CONNECTIONPOOL_MAX_POOL_SIZE = "connectionpool.max.pool.size";

	static final String CONNECTIONPOOL_MIN_POOL_SIZE = "connectionpool.min.pool.size";

	static final String CONNECTIONPOOL_MAX_IDLE_TIME = "connectionpool.max.idle.time";

	static final String CONNECTIONPOOL_REAP_TIMEOUT = "connectionpool.reap.timeout";

	static final String CONNECTIONPOOL_XADRIVER_NAME = "connectionpool.xadriver.name";

	static final String DB_HOST = "db.host";

	static final String DB_PORT = "db.port";

	static final String DB_USERNAME = "db.username";

	static final String DB_WORKBENCH_NAME = "db.workbench.name";

	static final String DB_PASSWORD = "db.password";

	private String borrowConnectionTimeout = "30";

	private String host = "localhost";

	private String maintenanceInterval = "60";

	private String maxPoolSize = "100";

	private String minPoolSize = "3";

	private String port = "3306";

	private String userName = "root";

	private String password = "";

	private String workbenchDbName = "workbench";

	private String xaDriverName = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";

	private String maxIdleTime = "120";

	private final String hibernateConfigurationLocation = "classpath:ibpmidware_hib.cfg.xml";

	private String reapTimeout = "300";

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceProperties.class);

	public DataSourceProperties(final Properties properties) {

		this.host = this.getPropertyValue(properties, this.host, DataSourceProperties.DB_HOST);
		this.port = this.getPropertyValue(properties, this.port, DataSourceProperties.DB_PORT);
		this.userName = this.getPropertyValue(properties, this.userName, DataSourceProperties.DB_USERNAME);
		this.password = this.getPropertyValue(properties, this.password, DataSourceProperties.DB_PASSWORD);
		this.workbenchDbName = this.getPropertyValue(properties, this.workbenchDbName, DataSourceProperties.DB_WORKBENCH_NAME);
		this.xaDriverName = this.getPropertyValue(properties, this.xaDriverName, DataSourceProperties.CONNECTIONPOOL_XADRIVER_NAME);
		this.borrowConnectionTimeout =
				this.getPropertyValue(properties, this.borrowConnectionTimeout,
						DataSourceProperties.CONNECTIONPOOL_BORROW_CONNECTION_TIMEOUT);
		this.maintenanceInterval =
				this.getPropertyValue(properties, this.maintenanceInterval, DataSourceProperties.CONNECTIONPOOL_MAINTENANCE_INTERVAL);
		this.minPoolSize = this.getPropertyValue(properties, this.minPoolSize, DataSourceProperties.CONNECTIONPOOL_MIN_POOL_SIZE);
		this.maxPoolSize = this.getPropertyValue(properties, this.maxPoolSize, DataSourceProperties.CONNECTIONPOOL_MAX_POOL_SIZE);
		this.maxIdleTime = this.getPropertyValue(properties, this.maxIdleTime, DataSourceProperties.CONNECTIONPOOL_MAX_IDLE_TIME);
		this.reapTimeout = this.getPropertyValue(properties, this.reapTimeout, DataSourceProperties.CONNECTIONPOOL_REAP_TIMEOUT);

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
	public String getHost() {
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
	public String getPort() {
		return this.port;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @return the workbenchDbName
	 */
	public String getWorkbenchDbName() {
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

	public String getReapTimeout() {
		return this.reapTimeout ;
	}

}
