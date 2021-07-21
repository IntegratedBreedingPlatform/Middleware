
package org.generationcp.middleware.dao;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for ListDataProperty POJO
 *
 * @author Darla Ani
 *
 */
public class ListDataPropertyDAO extends GenericDAO<ListDataProperty, Integer> {

	public ListDataPropertyDAO() {

	}

	/**
	 * Retrieves listdataprop record with given listDataId and columnName
	 *
	 * @param listDataId
	 * @param columnName
	 * @return null if no record found for given parameters
	 */
	public ListDataProperty getByListDataIDAndColumnName(final Integer listDataId, final String columnName) {
		try {
			if (listDataId != null && columnName != null && !columnName.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(ListDataProperty.class);
				criteria.createAlias("listData", "l");
				criteria.add(Restrictions.eq("l.id", listDataId));
				criteria.add(Restrictions.eq("column", columnName));
				return (ListDataProperty) criteria.uniqueResult();
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByListDataIDAndColumnName(listdata ID=" + listDataId + ", column= " + columnName
				+ ") " + "query from ListDataProperty " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public GermplasmListNewColumnsInfo getPropertiesForList(final Integer listId) {
		final String sql =
				" SELECT DISTINCT column_name,  listdata_id, value" + " FROM listdataprops p "
						+ " INNER JOIN listdata d ON d.lrecid = p.listdata_id " + " WHERE d.listid = :listId "
						+ " ORDER BY p.listdataprop_id ASC";

		final GermplasmListNewColumnsInfo listInfo = new GermplasmListNewColumnsInfo(listId);
		try {

			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("listId", listId);
			final List<Object[]> recordList = query.list();

			final Map<String, List<ListDataColumnValues>> columnValuesMap = new LinkedHashMap<>();

			for (final Object[] record : recordList) {

				final String column = (String) record[0];
				final Integer listDataId = (Integer) record[1];
				final String value = (String) record[2];

				columnValuesMap.putIfAbsent(column, new ArrayList<>());
				columnValuesMap.get(column).add(new ListDataColumnValues(column, listDataId, value));
				listInfo.addColumn(column);
			}
			listInfo.setColumnValuesMap(columnValuesMap);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getColumnNamesForList method for List : " + listId + e.getMessage(), e);
		}

		return listInfo;
	}

	public boolean isOntologyVariableInUse(final Integer variableId) {
		try {
			final String sql =
				"select count(1) from listdataprops ldp inner join cvterm cv on ldp.column_name = cv.name and cv.cvterm_id = :variableId";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("variableId", variableId);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with isOntologyVariableInUse method for variableId : " + variableId + e.getMessage(), e);
		}
	}

	public boolean isNameTypeInUse(final String nameType) {
		try {
			final String sql = "select count(1) from listdataprops ldp inner join cvterm cv on ldp.column_name = :nameType";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("nameType", nameType);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with isNameTypeInUse method for nameType : " + nameType + e.getMessage(), e);
		}
	}
}
