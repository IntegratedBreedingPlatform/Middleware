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

package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Trait;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class TraitDAO extends GenericDAO<Trait, Integer>{

    @SuppressWarnings("rawtypes")
    public Trait getByTraitId(Integer id) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Trait.class);
            crit.add(Restrictions.eq("traitId", id));
            crit.add(Restrictions.eq("nameStatus", Integer.valueOf(1)));
            List results = crit.list();

            if (results.isEmpty()) {
                return null;
            } else {
                return (Trait) results.get(0);
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByTraitId(id=" + id + ") query from Trait: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Trait getReplicationTrait() throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Trait.class);
            crit.add(Restrictions.eq("name", "replication"));
            List results = crit.list();
            
            if(results.isEmpty()) {
                return null;
            } else {
                return (Trait) results.get(0);
            }
        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error with getReplicationTrait() query: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Trait getBlockTrait() throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Trait.class);
            crit.add(Restrictions.eq("name", "block"));
            List results = crit.list();
            
            if(results.isEmpty()) {
                return null;
            } else {
                return (Trait) results.get(0);
            }
        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error with getBlockTrait() query: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Trait getEnvironmentTrait() throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Trait.class);
            crit.add(Restrictions.disjunction()
                    .add(Restrictions.eq("name", "environment name"))
                    .add(Restrictions.eq("name", "envname")));
            
            List results = crit.list();
            
            if(results.isEmpty()) {
                return null;
            } else {
                return (Trait) results.get(0);
            }
        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error with getEnvironmentTrait() query: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Trait getDesignTrait() throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Trait.class);
            crit.add(Restrictions.disjunction()
                    .add(Restrictions.eq("name", "experimental design"))
                    .add(Restrictions.eq("name", "EXPERIMENTAL DESIGN"))
                    .add(Restrictions.eq("name", "design")));
            
            List results = crit.list();
            
            if(results.isEmpty()) {
                return null;
            } else {
                return (Trait) results.get(0);
            }
        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error with getDesignTrait() query: " + e.getMessage(), e);
        }
    }    
    
}
