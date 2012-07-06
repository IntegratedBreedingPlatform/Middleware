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

import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.HibernateUtil;

public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    public GenotypicDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        //TODO
        super(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws QueryException{
        NameDAO nameDao = new NameDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(gIds.get(0));  //TODO: Verify DB option

        if (hibernateUtil != null) {
            nameDao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Integer>();
        }

        return (List<Integer>) nameDao.getNameIdsByGermplasmIds(gIds);
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws QueryException {
        NameDAO nameDao = new NameDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(nIds.get(0)); //TODO: Verify DB option

        if (hibernateUtil != null) {
            nameDao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Name>();
        }

        return (List<Name>) nameDao.getNamesByNameIds(nIds);
    }
}