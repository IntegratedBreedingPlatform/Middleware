package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.CharacterDataDAO;
import org.generationcp.middleware.dao.FactorDAO;
import org.generationcp.middleware.dao.NumericDataDAO;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.util.HibernateUtil;

public class StudyDataManagerImpl implements StudyDataManager
{
	private HibernateUtil hibernateUtil;
	
	public StudyDataManagerImpl(HibernateUtil hibernateUtil)
	{
		this.hibernateUtil = hibernateUtil;
	}
	
	@Override
	public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows)
	{
		NumericDataDAO dataNDao = new NumericDataDAO();
		dataNDao.setSession(this.hibernateUtil.getCurrentSession());
		
		CharacterDataDAO dataCDao = new CharacterDataDAO();
		dataCDao.setSession(this.hibernateUtil.getCurrentSession());
		
		Set<Integer> ounitIds = new HashSet<Integer>();
		
		//first get the observation unit ids for the rows in datasets which has the data specified in the filter
		//check numeric data
		ounitIds.addAll(dataNDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));
		//check character data
		ounitIds.addAll(dataCDao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, start, numOfRows));
		
		//use the retrieved observation unit ids to get the GIDs being observed in the rows in datasets identified by the 
		//observation unit ids
		if(!ounitIds.isEmpty())
		{
			FactorDAO factorDao = new FactorDAO();
			factorDao.setSession(this.hibernateUtil.getCurrentSession());
			
			Set<Integer> gids = factorDao.getGIDSGivenObservationUnitIds(ounitIds, start, numOfRows*2);
			List<Integer> toreturn = new ArrayList<Integer>();
			toreturn.addAll(gids);
			return toreturn;
		}
		else
		{
			return new ArrayList<Integer>();
		}
	}

}
