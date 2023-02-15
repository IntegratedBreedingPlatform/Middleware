
package org.generationcp.middleware.hibernate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

/**
 *	Utilities to help us create bean definitions progrmatically.
 */
public class DatasourceUtilities {

	private static final String SESSION_FACTORY = "_SessionFactory";

	private static final Logger LOG = LoggerFactory.getLogger(DatasourceUtilities.class);

	/**
	 * This method enables us to create a root bean definition i.e. programmatically create spring beans. This is doing programmatically
	 * what one does via annotations or XML configuration.
	 *
	 * @param klass The target {@link Class} for which we are creating a root bean definition.
	 * @param attributes {@link Map} of bean attributes that must be set
	 * @param properties {@link Map} of bean properties that must be set
	 * @return the newly created root bean definition
	 */
	public RootBeanDefinition createRootBeanDefinition(final Class<?> klass, final Map<String, Object> attributes,
			final Map<String, Object> properties) {

		final RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(klass);
		rootBeanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
		rootBeanDefinition.setTargetType(klass);
		LOG.debug(String.format("Created bean defintion for class '%s'", klass.getName()));

		for (final Entry<String, Object> attributeEntry : attributes.entrySet()) {
			rootBeanDefinition.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
		}

		LOG.debug(String.format("Set the following attributes '%s' to bean '%s'", attributes.toString().replace(",", ",\n"), klass.getName()));

		final MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();

		for (final Entry<String, Object> propertieEntry : properties.entrySet()) {

			mutablePropertyValues.add(propertieEntry.getKey(), propertieEntry.getValue());
		}
		rootBeanDefinition.setPropertyValues(mutablePropertyValues);

		LOG.debug(String.format("Set the following properties '%s' to bean '%s'", properties.toString().replace(",", ",\n"), klass.getName()));

		return rootBeanDefinition;
	}

	/**
	 * Gets a list of crop databases.
	 *
	 * @param workbenchDataSource data source to the Workbench db.
	 * @return a list of crop databases
	 */
	public List<String> retrieveCropDatabases(final DriverManagerDataSource workbenchDataSource) {
		Connection connection = null;
		final List<String> cropDatabases = new ArrayList<String>();
		try {
			connection = workbenchDataSource.getConnection();
			final PreparedStatement preparedStatement = connection.prepareStatement("Select db_name from workbench_crop");
			final ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				cropDatabases.add(rs.getString(1));
			}
			LOG.debug(String.format("Found the following crop database '%s'.", cropDatabases.toString().replace(",", ",\n")));

			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(preparedStatement);
		} catch (final SQLException e) {
			throw new IllegalStateException(
					"Unable to get the list of database that we need to register. Please contact your administrator for further assistance.",
					e);
		} finally {
			// Use this helper method so we don't have to check for null
			JdbcUtils.closeConnection(connection);
		}
		return cropDatabases;
	}

	/**
	 * Get a data source to the Workbench db. It is the responsibility of the method that opens the connection to also clean up the
	 * connection.
	 *
	 * @param xaDataSourceProperties to enable us to access the workbench database.
	 * @return {@link DriverManagerDataSource} to the workbench database.
	 */
	public DriverManagerDataSource getWorkbenchDataSource(final DataSourceProperties xaDataSourceProperties) {
		final DriverManagerDataSource workbenchDataSource = new DriverManagerDataSource();
		workbenchDataSource.setUsername(xaDataSourceProperties.getUserName());
		workbenchDataSource.setPassword(xaDataSourceProperties.getPassword());
		workbenchDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		workbenchDataSource.setUrl("jdbc:mysql://" + xaDataSourceProperties.getHost() + ":" + xaDataSourceProperties.getPort() + "/"
				+ xaDataSourceProperties.getWorkbenchDbName());
		return workbenchDataSource;
	}

	/**
	 * @param cropDatabaseName the database for which we want to access the session factory
	 * @return the designated name of programmatically registered session factory
	 */
	public static String computeSessionFactoryName(final String cropDatabaseName) {
		return cropDatabaseName.toUpperCase() + DatasourceUtilities.SESSION_FACTORY;
	}

}
