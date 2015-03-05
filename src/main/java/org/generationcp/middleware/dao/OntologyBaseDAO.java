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

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.hibernate.Session;

public abstract class OntologyBaseDAO extends BaseDAO {

    private CVTermDao cvTermDao;
    private CVTermRelationshipDao cvTermRelationshipDao;
    private CvTermPropertyDao cvTermPropertyDao;

    public OntologyBaseDAO(){
        this.cvTermDao = new CVTermDao();
        this.cvTermRelationshipDao = new CVTermRelationshipDao();
        this.cvTermPropertyDao = new CvTermPropertyDao();
    }

    @Override
    public void setSession(Session session){
        super.setSession(session);
        this.cvTermDao.setSession(session);
        this.cvTermPropertyDao.setSession(session);
        this.cvTermRelationshipDao.setSession(session);
    }
    
    protected CVTermDao getCvTermDao() { return this.cvTermDao; }
    protected CVTermRelationshipDao getCvTermRelationshipDao() { return this.cvTermRelationshipDao; }
    protected CvTermPropertyDao getCvTermPropertyDao() { return this.cvTermPropertyDao; }

}
