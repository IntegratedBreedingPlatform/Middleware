package org.generationcp.middleware.dao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public ListDataPropertyDAO(){
		
	}

	/**
	 * Retrieves listdataprop record with given listDataId and columnName
	 * 
	 * @param listDataId
	 * @param columnName
	 * @return null if no record found for given parameters
	 * @throws MiddlewareQueryException
	 */
	public ListDataProperty getByListDataIDAndColumnName(Integer listDataId, 
			String columnName)throws MiddlewareQueryException{
		try {
        	if (listDataId != null && columnName != null && !columnName.isEmpty()){
	            Criteria criteria = getSession().createCriteria(ListDataProperty.class);
	            criteria.createAlias("listData", "l");
	            criteria.add(Restrictions.eq("l.id", listDataId));
	            criteria.add(Restrictions.eq("column", columnName));
	            return (ListDataProperty) criteria.uniqueResult();
        	}
        	
        } catch (HibernateException e) {
            logAndThrowException("Error with getByListDataIDAndColumnName(listdata ID=" + 
            		listDataId + ", column= " + columnName + ") " +
            		"query from ListDataProperty " + e.getMessage(), e);
        }
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public GermplasmListNewColumnsInfo getPropertiesForList(Integer listId) throws MiddlewareQueryException{
		String sql = " SELECT DISTINCT column_name,  listdata_id, value" +
		" FROM listdataprops p " +
		" INNER JOIN listdata d ON d.lrecid = p.listdata_id " +
		" WHERE d.listid = :listId " + 
		" ORDER BY p.column_name,p.listdataprop_id DESC";

		GermplasmListNewColumnsInfo listInfo = new GermplasmListNewColumnsInfo(listId);
		try {
			
			Query query = getSession().createSQLQuery(sql);
			query.setParameter("listId", listId);
			List<Object[]> recordList =  query.list();

			Map<String, List<ListDataColumnValues>> columnValuesMap = new LinkedHashMap<String, List<ListDataColumnValues>>();
			List<ListDataColumnValues> columnValues = new ArrayList<ListDataColumnValues>();
			String lastColumn = null;
			
			for (Object[] record : recordList){
				String column = (String) record[0];
				Integer listDataId = (Integer) record[1];
				String value = (String) record[2];
				
				if (lastColumn == null){
					lastColumn = column;
				}
				//reset list of values for next column
				if (!lastColumn.equals(column)){
					columnValuesMap.put(lastColumn, columnValues);
					columnValues = new ArrayList<ListDataColumnValues>();
					lastColumn = column;
				}
				columnValues.add(new ListDataColumnValues(column, listDataId, value));
			}
			
			// insert to map last column data
			if (columnValues.size() > 0){
				columnValuesMap.put(lastColumn, columnValues);
			}
			
			
			listInfo.setColumnValuesMap(columnValuesMap);
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getColumnNamesForList method for List : " + listId + e.getMessage(), e);
		}
		
		return listInfo;
	}
}
