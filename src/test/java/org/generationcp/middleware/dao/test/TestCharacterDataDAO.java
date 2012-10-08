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

package org.generationcp.middleware.dao.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.CharacterDataDAO;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCharacterDataDAO{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;
    private CharacterDataDAO dao;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
        dao = new CharacterDataDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testGetObservationUnitIdsByTraitScaleMethodAndValueCombinations() throws Exception {
        TraitCombinationFilter combination = new TraitCombinationFilter(Integer.valueOf(1101), Integer.valueOf(163), Integer.valueOf(335), "7");
        List<TraitCombinationFilter> filters = new ArrayList<TraitCombinationFilter>();
        filters.add(combination);

        List<Integer> results = dao.getObservationUnitIdsByTraitScaleMethodAndValueCombinations(filters, 0, 10);
        System.out.println("testGetObservationUnitIdsByTraitScaleMethodAndValueCombinations RESULTS:");
        for (Integer result : results){
            System.out.println(result);
        }
    }

    @After
    public void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
