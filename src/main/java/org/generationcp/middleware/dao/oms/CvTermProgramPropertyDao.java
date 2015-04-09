package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermProgramProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.Arrays;
import java.util.List;


public class CvTermProgramPropertyDao extends GenericDAO<CVTermProgramProperty, Integer> {

    private static final String INVALID_TYPE_FOR_PROPERTY = "Invalid type for property";

    public List<CVTermProgramProperty> getByCvTermAndProgram(Integer cvTermId, String programUuid) throws MiddlewareQueryException {

        List properties;

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            criteria.add(Restrictions.eq("programId", programUuid));
            properties = criteria.list();

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error at getByCvTermAndProgram=" + cvTermId + " query on CvTermProgramPropertyDao: " + e.getMessage(), e);
        }

        return properties;
    }

    public List<CVTermProgramProperty> getByCvTermTypeProgram(Integer cvTermId, Integer typeId, String programUuid) throws MiddlewareQueryException {

        List properties;

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("programUuid", programUuid));
            properties = criteria.list();

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error at getByCvTermTypeProgram=" + cvTermId + " query on CvTermProgramPropertyDao: " + e.getMessage(), e);
        }

        return properties;
    }

    /**
     * Save cvterm properties with program for values with default rank 0
     * @param cvTermId termid
     * @param typeId TypeId for the property.
     *               1. Alias       1111
     *               2. MinValue    1113
     *               3. MaxValue    1115
     * @param programUuid program uuid
     * @param value value of property
     *             Rank should be zero and only single entry should be there.
     *             Rank is kept for its future usage where we have multiple property for same type.
     * @return created object
     * @throws MiddlewareQueryException
     */
    public CVTermProgramProperty save(Integer cvTermId, Integer typeId, String programUuid, String value) throws MiddlewareQueryException, MiddlewareException {

        List<CVTermProgramProperty> properties = getByCvTermTypeProgram(cvTermId, typeId, programUuid);
        //check for uniqueness
        if(properties.isEmpty()){
            return save(new CVTermProgramProperty(getNextId(CVTermProgramProperty.ID_NAME), cvTermId, typeId, programUuid, value, 0));
        }

        //Accept properties with alias, minvalue or maxvalue only
        if(!Arrays.asList(TermId.ALIAS.getId(), TermId.MIN_VALUE.getId(), TermId.MAX_VALUE.getId()).contains(typeId)){
            throw new MiddlewareException(INVALID_TYPE_FOR_PROPERTY);
        }

        CVTermProgramProperty property = properties.get(0);
        property.setProgramUuid(programUuid);
        property.setValue(value);
        property.setRank(0);
        return merge(property);
    }
}
