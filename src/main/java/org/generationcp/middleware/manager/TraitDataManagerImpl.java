/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.ScaleContinuousDAO;
import org.generationcp.middleware.dao.ScaleDAO;
import org.generationcp.middleware.dao.ScaleDiscreteDAO;
import org.generationcp.middleware.dao.TraitDAO;
import org.generationcp.middleware.dao.TraitMethodDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.ScaleDiscretePK;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraitDataManagerImpl extends DataManager implements TraitDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(TraitDataManagerImpl.class);

    private ScaleContinuousDAO scaleContinuousDao;
    private ScaleDAO scaleDao;
    private ScaleDiscreteDAO scaleDiscreteDao;
    private TraitDAO traitDao;
    private TraitMethodDAO traitMethodDao;

    public TraitDataManagerImpl() {
    }

    public TraitDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public TraitDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }
    
    private ScaleContinuousDAO getScaleContinuousDao() {
        if (scaleContinuousDao == null){
            scaleContinuousDao = new ScaleContinuousDAO();
        }
        scaleContinuousDao.setSession(getActiveSession());
        return scaleContinuousDao;
    }
    
    private ScaleDAO getScaleDao() {
        if (scaleDao == null){
            scaleDao = new ScaleDAO();
        }
        scaleDao.setSession(getActiveSession());
        return scaleDao;
    }

    private ScaleDiscreteDAO getScaleDiscreteDao() {
        if (scaleDiscreteDao == null){
            scaleDiscreteDao = new ScaleDiscreteDAO();
        }
        scaleDiscreteDao.setSession(getActiveSession());
        return scaleDiscreteDao;
    }
    
    private TraitDAO getTraitDao() {
        if (traitDao == null){
            traitDao = new TraitDAO();
        }
        traitDao.setSession(getActiveSession());
        return traitDao;
    }

    private TraitMethodDAO getTraitMethodDao() {
        if (traitMethodDao == null){
            traitMethodDao = new TraitMethodDAO();
        }
        traitMethodDao.setSession(getActiveSession());
        return traitMethodDao;
    }

    @Override
    public Scale getScaleByID(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)){
            return (Scale) getScaleDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Scale> getAllScales(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)){
            return getScaleDao().getAll(start, numOfRows);
        }
        return new ArrayList<Scale>();
    }

    @Override
    public long countAllScales() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getScaleDao());
    }

    @Override
    public String getScaleDiscreteDescription(Integer scaleId, String value) throws MiddlewareQueryException {
        if (setWorkingDatabase(scaleId)){
            ScaleDiscretePK id = new ScaleDiscretePK();
            id.setScaleId(scaleId);
            id.setValue(value);
            ScaleDiscrete sd = getScaleDiscreteDao().getById(id, false);
            if (sd != null) {
                return sd.getValueDescription();
            } 
        }
        return null;
    }

    @Override
    public List<ScaleDiscrete> getDiscreteValuesOfScale(Integer scaleId) throws MiddlewareQueryException {
        if (setWorkingDatabase(scaleId)){
            return getScaleDiscreteDao().getByScaleId(scaleId);
        }
        return new ArrayList<ScaleDiscrete>();
    }

    @Override
    public ScaleContinuous getRangeOfContinuousScale(Integer scaleId) throws MiddlewareQueryException {
        if (setWorkingDatabase(scaleId)){
            return getScaleContinuousDao().getById(scaleId, false);
        }
        return null;
    }

    @Override
    public Trait getTraitById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)){
            return getTraitDao().getByTraitId(id);
        }
        return null;
    }

    @Override
    public List<Trait> getAllTraits(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)){
            return getTraitDao().getAll(start, numOfRows);
        }
        return new ArrayList<Trait>();
    }

    @Override
    public long countAllTraits() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getTraitDao());
    }

    @Override
    public TraitMethod getTraitMethodById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)){
            return getTraitMethodDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<TraitMethod> getAllTraitMethods(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)){
            return getTraitMethodDao().getAll(start, numOfRows);
        }
        return new ArrayList<TraitMethod>();
    }

    @Override
    public long countAllTraitMethods() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getTraitMethodDao());
    }

    @Override
    public List<TraitMethod> getTraitMethodsByTraitId(Integer traitId) throws MiddlewareQueryException {
        if (setWorkingDatabase(traitId)){
            return getTraitMethodDao().getByTraitId(traitId);
        }
        return new ArrayList<TraitMethod>();
    }

    @Override
    public List<Scale> getScalesByTraitId(Integer traitId) throws MiddlewareQueryException {
        if (setWorkingDatabase(traitId)){
            return getScaleDao().getByTraitId(traitId);
        }
        return new ArrayList<Scale>();
    }

    @Override
    public Integer addTraitMethod(TraitMethod traitMethod) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idTraitMethodSaved = null; 
        try {
            trans = session.beginTransaction();

            TraitMethod recordSaved = getTraitMethodDao().saveOrUpdate(traitMethod);
            idTraitMethodSaved = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving TraitMethod: TraitDataManager.addTraitMethod(traitMethod="
                    + traitMethod + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idTraitMethodSaved;
    }

    @Override
    public void deleteTraitMethod(TraitMethod traitMethod) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getActiveSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getTraitMethodDao().makeTransient(traitMethod);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving TraitMethod: TraitDataManager.deleteTraitMethod(traitMethod=" + traitMethod + "): "
                            + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

    }

}
