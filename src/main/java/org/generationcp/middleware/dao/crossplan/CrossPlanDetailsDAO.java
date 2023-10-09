package org.generationcp.middleware.dao.crossplan;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.CrossPlanDetails;
import org.hibernate.Session;

public class CrossPlanDetailsDAO extends GenericDAO<CrossPlanDetails,Integer> {


    public CrossPlanDetailsDAO(Session session) {
        super(session);
    }
}
