package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.CharacterData;
import org.generationcp.middleware.pojos.CharacterDataPK;
import org.generationcp.middleware.pojos.NumericData;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CharacterDataDAO extends GenericDAO<CharacterData, CharacterDataPK>
{
	public List<Integer> getObservationUnitIdsByTraitScaleMethodAndValueCombinations
		(List<TraitCombinationFilter> filters, int start, int numOfRows)
	{
		Criteria crit = getSession().createCriteria(CharacterData.class);
		crit.createAlias("variate", "variate");
		crit.setProjection(Projections.distinct(Projections.property("id.observationUnitId")));
		
		//keeps track if at least one filter was added
		boolean filterAdded = false;
		
		for(TraitCombinationFilter combination : filters)
		{
			Object value = combination.getValue();
			
			//accept only String values
			if(value instanceof String)
			{
				crit.add(Restrictions.eq("variate.traitId", combination.getTraitId()));
				crit.add(Restrictions.eq("variate.scaleId", combination.getScaleId()));
				crit.add(Restrictions.eq("variate.methodId", combination.getMethodId()));
				crit.add(Restrictions.eq("value", value));
				
				filterAdded = true;
			}
		}
		
		if(filterAdded)
		{
			//if there is at least one filter, execute query and return results
			crit.setFirstResult(start);
			crit.setMaxResults(numOfRows);
			return crit.list();
		}
		else
		{
			//return empty list if no filter was added
			return new ArrayList<Integer>();
		}
	}
}
