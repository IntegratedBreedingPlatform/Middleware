
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

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
	 * @throws MiddlewareQueryException
	 */
	public ListDataProperty getByListDataIDAndColumnName(Integer listDataId, String columnName) throws MiddlewareQueryException {
		try {
			if (listDataId != null && columnName != null && !columnName.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(ListDataProperty.class);
				criteria.createAlias("listData", "l");
				criteria.add(Restrictions.eq("l.id", listDataId));
				criteria.add(Restrictions.eq("column", columnName));
				return (ListDataProperty) criteria.uniqueResult();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByListDataIDAndColumnName(listdata ID=" + listDataId + ", column= " + columnName
					+ ") " + "query from ListDataProperty " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public GermplasmListNewColumnsInfo getPropertiesForList(Integer listId) throws MiddlewareQueryException {
		String sql =
				" SELECT DISTINCT column_name,  listdata_id, value" + " FROM listdataprops p "
						+ " INNER JOIN listdata d ON d.lrecid = p.listdata_id " + " WHERE d.listid = :listId "
						+ " ORDER BY p.listdataprop_id ASC";

		GermplasmListNewColumnsInfo listInfo = new GermplasmListNewColumnsInfo(listId);
		try {

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("listId", listId);
			List<Object[]> recordList = query.list();

			LinkedHashMap<String, List<ListDataColumnValues>> columnValuesMap = new LinkedHashMap<String, List<ListDataColumnValues>>();

			for (Object[] record : recordList) {
				List<ListDataColumnValues> columnValues = new ArrayList<ListDataColumnValues>();

				String column = (String) record[0];
				Integer listDataId = (Integer) record[1];
				String value = (String) record[2];

				if(columnValuesMap.containsKey(column)) {
					columnValues = columnValuesMap.get(column);
				}
				columnValues.add(new ListDataColumnValues(column, listDataId, value));
				columnValuesMap.put(column, columnValues);

			}
			listInfo.setColumnValuesMap(columnValuesMap);

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getColumnNamesForList method for List : " + listId + e.getMessage(), e);
		}

		return listInfo;
	}
}
