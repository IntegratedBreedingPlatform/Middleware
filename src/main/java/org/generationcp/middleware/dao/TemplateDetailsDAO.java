package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.template.TemplateDetailsDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.TemplateDetails;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateDetailsDAO extends GenericDAO<TemplateDetails, Integer> {

    public TemplateDetailsDAO(final Session session) {
        super(session);
    }

    public void deleteByTemplateId(final Integer templateId) {
        try {
            this.getSession().createSQLQuery("delete from template_details where template_id = (:templateId) ")
                    .setParameter("templateId", templateId)
                    .executeUpdate();
        } catch (final HibernateException e) {
            final String message = "Error with deleteByTemplateId(templateId=" + templateId + ") in TemplateDetailsDAO: " + e.getMessage();
            throw new MiddlewareQueryException(message, e);
        }
    }

    public Map<Integer, List<TemplateDetailsDTO>> getTemplateDetailsMapByTemplateIds(final List<Integer> templateIds) {
        try {
            final SQLQuery query = this.getSession()
                    .createSQLQuery("SELECT td.template_id, td.variable_id, td.name, td.type "
                            + " FROM template_details td "
                            + " WHERE td.template_id IN (:templateIds) ");
            query.setParameterList("templateIds", templateIds);

            final List<Object[]> list = query.list();

            final Map<Integer, List<TemplateDetailsDTO>> templateDetailsMap = new HashMap<>();
            for (final Object[] row : list) {
                final Integer templateId = (Integer) row[0];
                templateDetailsMap.putIfAbsent(templateId, new ArrayList<>());
                templateDetailsMap.get(templateId).add(new TemplateDetailsDTO((Integer) row[1], (String) row[2], (String) row[3]));
            }
            return templateDetailsMap;
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException("Error at getDescriptorsMapByTemplateIds() query on TemplateDetailsDAO: " + e.getMessage(), e);
        }
    }

    public boolean isVariableUsedByTemplate(final int variableId) {
        try {
            final SQLQuery query = this.getSession()
                    .createSQLQuery("SELECT COUNT(*) FROM template_details WHERE variable_id = :variableId ");
            query.setParameter("variableId", variableId);
            return ((BigInteger) query.uniqueResult()).longValue() > 0;
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException("Error at isVariableUsedByTemplate() query on TemplateDetailsDAO: " + e.getMessage(), e);
        }
    }
}
