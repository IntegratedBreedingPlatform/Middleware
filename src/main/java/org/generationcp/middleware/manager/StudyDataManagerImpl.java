package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.CharacterDataDAO;
import org.generationcp.middleware.dao.FactorDAO;
import org.generationcp.middleware.dao.NumericDataDAO;
import org.generationcp.middleware.dao.StudyDAO;
import org.generationcp.middleware.dao.StudyEffectDAO;
import org.generationcp.middleware.dao.VariateDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.util.HibernateUtil;

public class StudyDataManagerImpl implements StudyDataManager
{
	private HibernateUtil hibernateUtilForLocal;
	private HibernateUtil hibernateUtilForCentral;

	public StudyDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral)
	{
		this.hibernateUtilForLocal = hibernateUtilForLocal;
		this.hibernateUtilForCentral = hibernateUtilForCentral;
	}

	@Override
	public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows) throws QueryException
	{
		//TODO handle local-central
		if(this.hibernateUtilForCentral == null)
			throw new QueryException("This method only works with a connection to a central instance for now.");
		
		NumericDataDAO dataNDao = new NumericDataDAO();
		dataNDao.setSession(this.hibernateUtilForCentral.getCurrentSession());
		
		CharacterDataDAO dataCDao = new CharacterDataDAO();
		dataCDao.setSession(this.hibernateUtilForCentral.getCurrentSession());
		
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
			factorDao.setSession(this.hibernateUtilForCentral.getCurrentSession());
			
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
	
	/**
	 * Returns the appropriate HibernateUtil based on the given id. 
	 * If the id is negative, hibernateUtilForLocal is returned
	 * If the id is positive, hibernateUtilForCentral is returned
	 * 
	 * @return
	 * @throws QueryException
	 */
	private HibernateUtil getHibernateUtil(Integer id) throws QueryException{
		if((id > 0) && (this.hibernateUtilForCentral != null)){
			return hibernateUtilForCentral;
		}else if ((id < 0) && (this.hibernateUtilForLocal != null)){
			return hibernateUtilForLocal;
		}
		return null;
	}
	
	@Override
	public List<Factor> getFactorsByStudyID(Integer studyId) throws QueryException{
		
		FactorDAO factorDao = new FactorDAO();
		HibernateUtil hibernateUtil = getHibernateUtil(studyId);

		if (hibernateUtil != null){
			factorDao.setSession(hibernateUtil.getCurrentSession());
		} else {
			return new ArrayList<Factor>();
		}

		List<Factor> factors = factorDao.getByStudyID(studyId);
		return factors;
	}

	@Override
	public List<Variate> getVariatesByStudyID(Integer studyId) throws QueryException{
		
		VariateDAO variateDao = new VariateDAO();
		HibernateUtil hibernateUtil = getHibernateUtil(studyId);

		if (hibernateUtil != null){
			variateDao.setSession(hibernateUtil.getCurrentSession());
		} else {
			return new ArrayList<Variate>();
		}
		
		List<Variate> variates = variateDao.getByStudyID(studyId);
		return variates;
		
	}

	@Override
	public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws QueryException{
		
		StudyEffectDAO studyEffectDao = new StudyEffectDAO();
		HibernateUtil hibernateUtil = getHibernateUtil(studyId);

		if (hibernateUtil != null){
			studyEffectDao.setSession(hibernateUtil.getCurrentSession());
		} else {
			return new ArrayList<StudyEffect>();
		}
		
		List<StudyEffect> studyEffect = studyEffectDao.getByStudyID(studyId);
		return studyEffect;
		
	}
	
	@Override
	public List<Study> getAllTopLevelStudies(int start, int numOfRows, Database instance) throws QueryException {
		StudyDAO dao = new StudyDAO();
		
		if(instance == Database.CENTRAL) {
			if(this.hibernateUtilForCentral != null) {
				dao.setSession(hibernateUtilForCentral.getCurrentSession());
			} else {
				throw new QueryException("The central instance was specified for the search but there is no database connection " +
						"for central provided.");
			}
		}
		else if(instance == Database.LOCAL) {
			if(this.hibernateUtilForLocal != null) {
				dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
			}
			else {
				throw new QueryException("The local instance was specified for the search but there is no database connection " +
					"for local provided.");
			}
		}
		
		List<Study> topLevelStudies = dao.getTopLevelStudies(start, numOfRows);
		
		return topLevelStudies;
	}
	
	@Override
	public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws QueryException {
		StudyDAO dao = new StudyDAO();
		
		if(parentFolderId < 0 && this.hibernateUtilForLocal != null) {
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		} else if(parentFolderId > 0 && this.hibernateUtilForCentral != null) {
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		} else {
			return null;
		}
		
		List<Study> studies = dao.getStudiesByParentFolderID(parentFolderId, start, numOfRows);
		
		return studies;
	}

}
