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

public class TraitDataManagerImpl extends DataManager implements TraitDataManager{

    public TraitDataManagerImpl() {
    }

    public TraitDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public TraitDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public Scale getScaleByID(Integer id) {
        ScaleDAO dao = new ScaleDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return (Scale) dao.getById(id, false);
    }

    @Override
    public List<Scale> getAllScales(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        ScaleDAO dao = new ScaleDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Scale>();
        }
        return dao.getAll(start, numOfRows);

    }

    @Override
    public long countAllScales() {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            ScaleDAO dao = new ScaleDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        if (sessionForCentral != null) {
            ScaleDAO centralDao = new ScaleDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countAll();
        }

        return count;
    }

    @Override
    public String getScaleDiscreteDescription(Integer scaleId, String value) {
        ScaleDiscreteDAO dao = new ScaleDiscreteDAO();
        Session session = getSession(scaleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        ScaleDiscretePK id = new ScaleDiscretePK();
        id.setScaleId(scaleId);
        id.setValue(value);

        ScaleDiscrete sd = dao.getById(id, false);

        if (sd != null) {
            return sd.getValueDescription();
        } else {
            return null;
        }
    }

    @Override
    public List<ScaleDiscrete> getDiscreteValuesOfScale(Integer scaleId) throws MiddlewareQueryException {
        ScaleDiscreteDAO dao = new ScaleDiscreteDAO();
        Session session = getSession(scaleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<ScaleDiscrete>();
        }

        return dao.getByScaleId(scaleId);
    }

    @Override
    public ScaleContinuous getRangeOfContinuousScale(Integer scaleId) {
        ScaleContinuousDAO dao = new ScaleContinuousDAO();
        Session session = getSession(scaleId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getById(scaleId, false);
    }

    @Override
    public Trait getTraitById(Integer id) throws MiddlewareQueryException {
        TraitDAO dao = new TraitDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getByTraitId(id);
    }

    @Override
    public List<Trait> getAllTraits(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        TraitDAO dao = new TraitDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Trait>();
        }
        return dao.getAll(start, numOfRows);
    }

    @Override
    public long countAllTraits() {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            TraitDAO dao = new TraitDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        if (sessionForCentral != null) {
            TraitDAO centralDao = new TraitDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countAll();
        }

        return count;
    }

    @Override
    public TraitMethod getTraitMethodById(Integer id) {
        TraitMethodDAO dao = new TraitMethodDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getById(id, false);
    }

    @Override
    public List<TraitMethod> getAllTraitMethods(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        TraitMethodDAO dao = new TraitMethodDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<TraitMethod>();
        }

        return dao.getAll(start, numOfRows);
    }

    @Override
    public long countAllTraitMethods() {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            TraitMethodDAO dao = new TraitMethodDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        if (sessionForCentral != null) {
            TraitMethodDAO centralDao = new TraitMethodDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countAll();
        }

        return count;
    }

    @Override
    public List<TraitMethod> getTraitMethodsByTraitId(Integer traitId) throws MiddlewareQueryException {
        TraitMethodDAO dao = new TraitMethodDAO();
        Session session = getSession(traitId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<TraitMethod>();
        }

        return dao.getByTraitId(traitId);
    }

    @Override
    public List<Scale> getScalesByTraitId(Integer traitId) throws MiddlewareQueryException {
        ScaleDAO dao = new ScaleDAO();
        Session session = getSession(traitId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Scale>();
        }

        return dao.getByTraitId(traitId);
    }

    @Override
    public void addTraitMethod(TraitMethod traitMethod) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            TraitMethodDAO dao = new TraitMethodDAO();
            dao.setSession(session);

            dao.saveOrUpdate(traitMethod);

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving TraitMethod: TraitDataManager.addTraitMethod(traitMethod="
                    + traitMethod + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
    }

    @Override
    public void deleteTraitMethod(TraitMethod traitMethod) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            TraitMethodDAO dao = new TraitMethodDAO();
            dao.setSession(session);

            dao.makeTransient(traitMethod);

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while saving TraitMethod: TraitDataManager.deleteTraitMethod(traitMethod=" + traitMethod + "): "
                            + e.getMessage(), e);
        } finally {
            session.flush();
        }
    }

}
