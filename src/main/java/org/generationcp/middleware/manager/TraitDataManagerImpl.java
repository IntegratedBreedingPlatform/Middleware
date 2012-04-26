package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.ScaleContinuousDAO;
import org.generationcp.middleware.dao.ScaleDAO;
import org.generationcp.middleware.dao.ScaleDiscreteDAO;
import org.generationcp.middleware.dao.TraitDAO;
import org.generationcp.middleware.dao.TraitMethodDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.ScaleDiscretePK;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Session;

public class TraitDataManagerImpl implements TraitDataManager
{
	private HibernateUtil hibernateUtilForLocal;
	private HibernateUtil hibernateUtilForCentral;
	
	public TraitDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral)
	{
		this.hibernateUtilForLocal = hibernateUtilForLocal;
		this.hibernateUtilForCentral = hibernateUtilForCentral;
	}
	
	@Override
	public Scale getScaleByID(Integer id)
	{
		ScaleDAO dao = new ScaleDAO();
		
		if(id < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(id > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		Scale scale = dao.findById(id, false);
		return scale;
	}

	@Override
	public List<Scale> getAllScales(int start, int numOfRows) throws QueryException
	{
		//TODO handle local-central
		if(this.hibernateUtilForCentral == null)
			throw new QueryException("This method only works with a connection to a central instance for now.");
		ScaleDAO dao = new ScaleDAO();
		dao.setSession(hibernateUtilForCentral.getCurrentSession());
		return dao.getAll(start, numOfRows);
	}
	
	@Override
	public int countAllScales()
	{
		int count = 0;
		
		if(this.hibernateUtilForLocal != null)
		{
			ScaleDAO dao = new ScaleDAO();
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
			count = count + dao.countAll().intValue();
		}
			
		if(this.hibernateUtilForCentral != null)
		{
			ScaleDAO centralDao = new ScaleDAO();
			centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
			count = count + centralDao.countAll().intValue();
		}
		
		return count;
	}
	
	@Override
	public String getScaleDiscreteDescription(Integer scaleId, String value)
	{
		ScaleDiscreteDAO dao = new ScaleDiscreteDAO();
		
		if(scaleId < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(scaleId > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		ScaleDiscretePK id = new ScaleDiscretePK();
		id.setScaleId(scaleId);
		id.setValue(value);
		
		ScaleDiscrete sd = dao.findById(id, false);
		return sd.getValueDescription();
	}
	
	@Override
	public List<ScaleDiscrete> getDiscreteValuesOfScale(Integer scaleId)
	{
		ScaleDiscreteDAO dao = new ScaleDiscreteDAO();
		
		if(scaleId < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(scaleId > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return new ArrayList<ScaleDiscrete>();
		
		return dao.getByScaleId(scaleId);
	}
	
	@Override
	public ScaleContinuous getRangeOfContinuousScale(Integer scaleId)
	{
		ScaleContinuousDAO dao = new ScaleContinuousDAO();
		
		if(scaleId < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(scaleId > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		return dao.findById(scaleId, false);
	}
	
	@Override
	public Trait getTraitById(Integer id)
	{
		TraitDAO dao = new TraitDAO();
		
		if(id < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(id > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		return dao.getByTraitId(id);
	}
	
	@Override
	public List<Trait> getAllTraits(int start, int numOfRows) throws QueryException
	{
		//TODO handle local-central
		if(this.hibernateUtilForCentral == null)
			throw new QueryException("This method only works with a connection to a central instance for now.");	
		TraitDAO dao = new TraitDAO();
		dao.setSession(hibernateUtilForCentral.getCurrentSession());
		return dao.getAll(start, numOfRows);
	}
	
	@Override
	public int countAllTraits()
	{
		int count = 0;
		
		if(this.hibernateUtilForLocal != null)
		{
			TraitDAO dao = new TraitDAO();
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
			count = count + dao.countAll().intValue();
		}
		
		if(this.hibernateUtilForCentral != null)
		{
			TraitDAO centralDao = new TraitDAO();
			centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
			count = count + centralDao.countAll().intValue();
		}
		
		return count;
	}
	
	@Override
	public TraitMethod getTraitMethodById(Integer id)
	{
		TraitMethodDAO dao = new TraitMethodDAO();
		
		if(id < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(id > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		return dao.findById(id, false);
	}
	
	@Override
	public List<TraitMethod> getAllTraitMethods(int start, int numOfRows) throws QueryException
	{
		//TODO handle local-central
		if(this.hibernateUtilForCentral == null)
			throw new QueryException("This method only works with a connection to a central instance for now.");
		TraitMethodDAO dao = new TraitMethodDAO();
		dao.setSession(hibernateUtilForCentral.getCurrentSession());
		return dao.getAll(start, numOfRows);
	}
	
	@Override
	public int countAllTraitMethods()
	{
		int count = 0;
		
		if(this.hibernateUtilForLocal != null)
		{
			TraitMethodDAO dao = new TraitMethodDAO();
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
			count = count + dao.countAll().intValue();
		}
		
		if(this.hibernateUtilForCentral != null)
		{
			TraitMethodDAO centralDao = new TraitMethodDAO();
			centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
			count = count + centralDao.countAll().intValue();
		}
		
		return count;
	}
	
	@Override
	public List<TraitMethod> getTraitMethodsByTraitId(Integer traitId)
	{
		TraitMethodDAO dao = new TraitMethodDAO();
		
		if(traitId < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(traitId > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return new ArrayList<TraitMethod>();
		
		return dao.getByTraitId(traitId);
	}
	
	@Override
	public List<Scale> getScalesByTraitId(Integer traitId)
	{
		ScaleDAO dao = new ScaleDAO();
		
		if(traitId < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(hibernateUtilForLocal.getCurrentSession());
		else if(traitId > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(hibernateUtilForCentral.getCurrentSession());
		else
			return new ArrayList<Scale>();
		
		return dao.getByTraitId(traitId);
	}
	
}
