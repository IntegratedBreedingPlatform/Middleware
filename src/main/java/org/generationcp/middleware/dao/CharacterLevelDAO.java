package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.CharacterLevel;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.CharacterLevelPK;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

public class CharacterLevelDAO extends GenericDAO<CharacterLevel, CharacterLevelPK> {
	
	public List<CharacterLevelElement> getValuesByOunitIDList(List<Integer> ounitIdList) throws QueryException {
		try {
			SQLQuery query = getSession().createSQLQuery(CharacterLevel.GET_BY_OUNIT_ID_LIST);
			query.setParameterList("ounitIdList", ounitIdList);
			
			List<CharacterLevelElement> levelValues = new ArrayList<CharacterLevelElement>();
			
			List results = query.list();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if(result != null)
				{
					Integer ounitId = (Integer) result[0];
					Integer factorId = (Integer) result[1];
					String factorName = (String) result[2];
					String value = (String) result[3];
					
					CharacterLevelElement levelElement = new CharacterLevelElement(ounitId, factorId, factorName, value);
					
					levelValues.add(levelElement);
				}
			}
			
			return levelValues;
		} catch(HibernateException ex) {
			throw new QueryException("Error with get Character Level Values by list of Observation Unit IDs query: " + ex.getMessage());
		}
	}
	
}
