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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.CharacterLevel;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.CharacterLevelPK;
import org.generationcp.middleware.pojos.DatasetCondition;
import org.generationcp.middleware.pojos.StudyInfo;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class CharacterLevelDAO extends GenericDAO<CharacterLevel, CharacterLevelPK>{

    @SuppressWarnings("rawtypes")
    public List<CharacterLevelElement> getValuesByOunitIDList(List<Integer> ounitIdList) throws QueryException {

        List<CharacterLevelElement> levelValues = new ArrayList<CharacterLevelElement>();

        if (ounitIdList == null || ounitIdList.isEmpty()){
            return levelValues;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(CharacterLevel.GET_BY_OUNIT_ID_LIST);
            query.setParameterList("ounitIdList", ounitIdList);

            List results = query.list();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer ounitId = (Integer) result[0];
                    Integer factorId = (Integer) result[1];
                    String factorName = (String) result[2];
                    String value = (String) result[3];

                    CharacterLevelElement levelElement = new CharacterLevelElement(ounitId, factorId, factorName, value);

                    levelValues.add(levelElement);
                }
            }

            return levelValues;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Character Level Values by list of Observation Unit IDs query: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("rawtypes")
    public List<DatasetCondition> getConditionAndValueByFactorIdAndLevelNo(Integer factorId, Integer levelNo) throws QueryException {
        try {
            List<DatasetCondition> toreturn = new ArrayList<DatasetCondition>();
            
            SQLQuery query = getSession().createSQLQuery(CharacterLevel.GET_CONDITION_AND_VALUE);
            query.setParameter("factorid", factorId);
            query.setParameter("levelno", levelNo);
            
            List results = query.list();
            for(Object o : results) {
                Object[] result = (Object[]) o;
                String name = (String) result[0];
                String value = (String) result[1];
                Integer traitid = (Integer) result[2];
                Integer scaleid = (Integer) result[3];
                Integer methodid = (Integer) result[4];
                String type = (String) result[5];
                
                DatasetCondition condition = new DatasetCondition(factorId, name, value, traitid, scaleid, methodid, type);
                toreturn.add(condition);
            }
            
            return toreturn;
        } catch (Exception ex) {
            throw new QueryException("Error with get Condition and value by factorid and levelno query, " +
                    "given factorid = " + factorId + " and levelno = " + levelNo +
                    ": " + ex.getMessage(), ex);
        }
    }
    
    public long countStudyInformationByGID(Long gid) throws QueryException {
        try {
            Query query = getSession().createSQLQuery(CharacterLevel.COUNT_STUDIES_BY_GID);
            query.setParameter("gid", gid);
            
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.longValue();
        } catch (Exception ex) {
            throw new QueryException("Error with count study information by GID query: " +
                    ex.getMessage(), ex);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public List<StudyInfo> getStudyInformationByGID(Long gid) throws QueryException {
        try {
            List<StudyInfo> toreturn = new ArrayList<StudyInfo>();
            Query query = getSession().createSQLQuery(CharacterLevel.GET_STUDIES_BY_GID);
            query.setParameter("gid", gid);
            
            List results = query.list();
            for(Object o : results) {
                Object[] result = (Object[]) o;
                Integer studyid = (Integer) result[0];
                String name = (String) result[1];
                String title = (String) result[2];
                String objective = (String) result[3];
                BigInteger rowCount = (BigInteger) result[4];
                
                StudyInfo info = new StudyInfo(studyid, name.trim(), title.trim(), objective.trim(), rowCount.intValue());
                toreturn.add(info);
            }
            
            return toreturn;
            
        } catch (Exception ex) {
            throw new QueryException("Error with get study information by GID query: " +
                    ex.getMessage(), ex);
        }
    }
}
