package org.generationcp.middleware.service.migrator;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.migrator.api.MigratorService;
import org.hibernate.Query;
import org.hibernate.Session;

public class MigratorServiceImpl extends Service implements MigratorService {

	private static final int RESET_VALUE = 0;

	public MigratorServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public MigratorServiceImpl(HibernateSessionProvider sessionProvider,
			String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	public void resetLocalReferenceId(String tableName, String columnName)
			throws MiddlewareQueryException {
		String queryString = "UPDATE " + tableName + " SET " + columnName
				+ " = :" + columnName + " where " + columnName + " != 0";
		Session session = this.getCurrentSession();

		Query q = session.createSQLQuery(queryString);
		q.setInteger(columnName, RESET_VALUE);
		q.executeUpdate();
	}
}
