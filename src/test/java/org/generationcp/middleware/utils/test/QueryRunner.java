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
package org.generationcp.middleware.utils.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.exceptions.ConfigException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryRunner{
    
    private static final Logger LOG = LoggerFactory.getLogger(QueryRunner.class);

    private static HibernateUtil hibernateUtil;

    public static void createSession(String dbInstance){
        if (dbInstance == null){
            dbInstance = "central";
        }
        try {
            hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", dbInstance));
        } catch (ConfigException e) {
            LOG.error(e.getMessage(), e);
        } catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    private static Session getSession(){
        if (hibernateUtil == null){
            createSession(null);
        }
        return hibernateUtil.getCurrentSession();
    }
    
    public static void runUpdate(GenericDAO<?, ?> dao, String sql){
        if (dao != null){
            dao.setSession(getSession());
        }
        
        try {
            
            SQLQuery query = getSession().createSQLQuery(sql);
            query.executeUpdate();
            
        } catch (HibernateException e) {
            Debug.println(3, "Error with run. " + e.getMessage());
        }

    }
    public static List<?> runQuery(GenericDAO<?, ?> dao, String sql){
        if (dao != null){
            dao.setSession(getSession());
        }
        
        try {
            
            SQLQuery query = getSession().createSQLQuery(sql);
            return query.list();
            
        } catch (HibernateException e) {
            Debug.println(3, "Error with run. " + e.getMessage());
        }
        return null;

    }


}
