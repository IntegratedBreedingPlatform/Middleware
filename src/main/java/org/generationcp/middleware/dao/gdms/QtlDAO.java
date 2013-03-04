package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;


public class QtlDAO  extends GenericDAO<Qtl, Integer>{

    @SuppressWarnings("rawtypes")
    public List<QtlDetailElement> getQtlDetailsByName(String name, int start, int numOfRows) throws MiddlewareQueryException{
        List<QtlDetailElement> toReturn = new ArrayList<QtlDetailElement>();

        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_BY_NAME);
            query.setParameter("qtlName", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    String qtlName = (String) result[0];
                    String mapName = (String) result[1];
                    String chromosome = (String) result[2];
                    Float minPosition = (Float) result[3];
                    Float maxPosition = (Float) result[4];
                    String trait = (String) result[5];
                    String experiment = (String) result[6];
                    String leftFlankingMarker = (String) result[7];
                    String rightFlankingMarker = (String) result[8];
                    Integer effect = (Integer) result[9];
                    Float scoreValue = (Float) result[10];
                    Float rSquare = (Float) result[11];
                    String interactions = (String) result[12];
                    String tRName = (String) result[13];
                    String ontology = (String) result[14];
                    
                    
                    QtlDetailElement element = new QtlDetailElement(qtlName, mapName, chromosome, minPosition, maxPosition, trait,
                            experiment, leftFlankingMarker, rightFlankingMarker, effect, scoreValue, rSquare,
                            interactions, tRName, ontology);
                    toReturn.add(element);
                }
            }

            return toReturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getQtlDetailsByName(name=" + name + ") query from gdms_qtl_details: " + e.getMessage(), e);
        }
    }

    public long countQtlDetailsByName(String name) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_BY_NAME);
            query.setParameter("qtlName", name);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countQtlDetailsByName(name=" + name + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getQtlByTrait(String trait, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Qtl.GET_QTL_BY_TRAIT);
            query.setParameter("qtlTrait", trait);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();        
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getQtlByTrait(trait=" + trait + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
    }

    public long countQtlByTrait(String trait) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Qtl.COUNT_QTL_BY_TRAIT);
            query.setParameter("qtlTrait", trait);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countQtlByTrait(trait=" + trait + ") query from gdms_qtl_details: "
                    + e.getMessage(), e);
        }
    }


}
