/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link TemplateSetting}.
 * 
 */
public class TemplateSettingDAO extends GenericDAO<TemplateSetting, Integer>{

    public TemplateSetting getByName(String name) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(TemplateSetting.class)
					.add(Restrictions.eq("name", name))
					.setMaxResults(1);
			return (TemplateSetting) criteria.uniqueResult();
		} catch (HibernateException e) {
			logAndThrowException("Error with getByName(name=" + name + ") query from TemplateSetting: " + e.getMessage(), e);
		}
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<TemplateSetting> getByTool(Tool tool) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(TemplateSetting.class)
                    .add(Restrictions.eq("tool", tool));
            return (List<TemplateSetting>) criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByTool(tool=" + tool + ") query from TemplateSetting: " + e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<TemplateSetting> getByProjectId(Integer projectId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(TemplateSetting.class)
                    .add(Restrictions.eq("projectId", projectId));
            return (List<TemplateSetting>) criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByProjectId(projectId=" + projectId + ") query from TemplateSetting: " + e.getMessage(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<TemplateSetting> get(TemplateSetting filter) throws MiddlewareQueryException {
        List<TemplateSetting> settings = new ArrayList<TemplateSetting>();
              
        try {
            Criteria criteria = getSession().createCriteria(TemplateSetting.class);
            
            if (filter.getTemplateSettingId() != null){
                criteria.add(Restrictions.eq("templateSettingId", filter.getTemplateSettingId()));
            }
            if (filter.getProjectId() != null){
                criteria.add(Restrictions.eq("projectId", filter.getProjectId()));
            }
            if (filter.getTool() != null && filter.getTool().getToolId() != null){
                criteria.add(Restrictions.eq("tool", filter.getTool()));
            }
            if (filter.getName() != null){
                criteria.add(Restrictions.eq("name", filter.getName()));
            }
            if (filter.getConfiguration() != null){
                criteria.add(Restrictions.eq("configuration", filter.getConfiguration()));
            }
            if (filter.getIsDefault() != null){
                criteria.add(Restrictions.eq("isDefault", filter.getIsDefault()));
            }
            return (List<TemplateSetting>) criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with get(templateFilter=" + filter + ") query from TemplateSetting: " + e.getMessage(), e);
        }
        
        
        return settings;
    }

}
