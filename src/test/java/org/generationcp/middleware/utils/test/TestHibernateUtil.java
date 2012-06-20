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

package org.generationcp.middleware.utils.test;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;

public class TestHibernateUtil{

    @Test
    public void testHibernateUtil() throws Exception {
        // HibernateUtil util = new HibernateUtil("localhost", "3306",
        // "iris_myisam_20100330", "root", "lich27king");
        HibernateUtil util = new HibernateUtil("localhost", "3306", "ibdbv1_rice_central", "ibdb_user", "ibdb_password");
        Session session = util.getCurrentSession();
        Query query = session.createQuery("FROM Germplasm");
        query.setFirstResult(0);
        query.setMaxResults(5);
        List<Germplasm> results = query.list();

        for (Germplasm g : results) {
            System.out.println(g);
        }
    }
}
