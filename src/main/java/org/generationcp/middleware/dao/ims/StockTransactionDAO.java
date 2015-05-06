package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Daniel Villafuerte on 5/5/2015.
 */
public class StockTransactionDAO extends GenericDAO<StockTransaction, Integer>{
    public List<StockTransaction> getTransactionsForListDataProjectIDs(List<Integer> listDataProjectIDList) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(StockTransaction.class);
            criteria.createAlias("listDataProject", "ldp");
            criteria.add(Restrictions.in("ldp.listDataProjectId", listDataProjectIDList));

            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException(e.getMessage());
        }
    }
}
