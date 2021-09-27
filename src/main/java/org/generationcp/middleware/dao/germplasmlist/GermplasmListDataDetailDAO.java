package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GermplasmListDataDetailDAO extends GenericDAO<GermplasmListDataDetail, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataDetailDAO.class);

	public GermplasmListDataDetailDAO() {
	}

	public GermplasmListDataDetailDAO(final Session session) {
		super(session);
	}

	public void deleteByListIdAndVariableIds(final Integer listId, final Set<Integer> variableIds) {
		try {
			final String query =
				"DELETE FROM list_data_details ldd INNER JOIN listdata ld ON (ld.llrecid = ldd.lrecid) WHERE ld.listid = :listId AND ldd.variable_id IN :variableIds";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameter("listId", listId);
			sqlQuery.setParameterList("variableIds", variableIds);
			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with deleteByListIdAndVariableIds(listId=" + listId + ", variableIds=" + variableIds
					+ ") query from GermplasmListDataDetailDAO " + e.getMessage();
			GermplasmListDataDetailDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}