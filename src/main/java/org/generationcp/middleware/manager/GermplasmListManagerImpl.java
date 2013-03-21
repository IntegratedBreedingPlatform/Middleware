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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager{

    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListManagerImpl.class);

    private GermplasmListDAO germplasmListDao;
	private GermplasmListDataDAO germplasmListDataDao;
	
	private GermplasmListDAO getGermplasmListDAO() {
		if (germplasmListDao == null) {
			germplasmListDao = new GermplasmListDAO();
		}
		germplasmListDao.setSession(getActiveSession());
		return germplasmListDao;
	}
	
	private GermplasmListDataDAO getGermplasmListDataDAO() {
		if (germplasmListDataDao == null) {
			germplasmListDataDao = new GermplasmListDataDAO();
		}
		germplasmListDataDao.setSession(getActiveSession());
		return germplasmListDataDao;
	}
	
    public GermplasmListManagerImpl() {
    }

    public GermplasmListManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GermplasmListManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public GermplasmList getGermplasmListById(Integer id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getGermplasmListDAO().getById(id, false);
        }
        
        return null;
    }

    @Override
    public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
        	return getGermplasmListDAO().getAllExceptDeleted(start, numOfRows);
        }
        
        return new ArrayList<GermplasmList>();
    }

    @Override
    public long countAllGermplasmLists() throws MiddlewareQueryException {
        long count = 0;

        if (setWorkingDatabase(Database.LOCAL)) {
        	count = count + getGermplasmListDAO().countAllExceptDeleted();
        }
        
        if (setWorkingDatabase(Database.CENTRAL)) {
        	count = count + getGermplasmListDAO().countAllExceptDeleted();
        }
        
        return count;
    }

    @Override
    public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
            throws MiddlewareQueryException {
        
    	if (setWorkingDatabase(instance)) {
    		return getGermplasmListDAO().getByName(name, operation, start, numOfRows);
    	}
    	
    	return new ArrayList<GermplasmList>();
    }

    @Override
    public long countGermplasmListByName(String name, Operation operation) throws MiddlewareQueryException {
        long count = 0;

        if (setWorkingDatabase(Database.LOCAL)) {
        	count = count + getGermplasmListDAO().countByName(name, operation);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
        	count = count + getGermplasmListDAO().countByName(name, operation);
        }

        return count;
    }

    @Override
    public List<GermplasmList> getGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        
    	if (setWorkingDatabase(instance)) {
    		return getGermplasmListDAO().getByStatus(status, start, numOfRows);
    	}
    	return new ArrayList<GermplasmList>();
    }

    @Override
    public long countGermplasmListByStatus(Integer status) throws MiddlewareQueryException {
        long count = 0;

        if (setWorkingDatabase(Database.LOCAL)) {
        	count = count + getGermplasmListDAO().countByStatus(status);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
        	count = count + getGermplasmListDAO().countByStatus(status);
        }

        return count;
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {

    	if (setWorkingDatabase(id)) {
        	return getGermplasmListDataDAO().getByListId(id, start, numOfRows);
        }
        
        return new ArrayList<GermplasmListData>();
    }

    @Override
    public long countGermplasmListDataByListId(Integer id) throws MiddlewareQueryException {
        
        if (setWorkingDatabase(id)) {
            return getGermplasmListDataDAO().countByListId(id);
        }
        
        return 0;
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException {
        if (setWorkingDatabase(listId)) {
        	return getGermplasmListDataDAO().getByListIdAndGID(listId, gid);
        }
        
        return new ArrayList<GermplasmListData>();
    }

    @Override
    public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
        if (setWorkingDatabase(listId)) {
            return getGermplasmListDataDAO().getByListIdAndEntryId(listId, entryId);
        }
        
        return null;
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        List<GermplasmListData> toreturn = new ArrayList<GermplasmListData>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            centralCount = getGermplasmListDataDAO().countByGID(gid);

            if (centralCount > start) {
                toreturn.addAll(getGermplasmListDataDAO().getByGID(gid, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL)) {
                        localCount = getGermplasmListDataDAO().countByGID(gid);

                        if (localCount > 0) {
                            toreturn.addAll(getGermplasmListDataDAO().getByGID(gid, 0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;

                if (setWorkingDatabase(Database.LOCAL)) {
                    localCount = getGermplasmListDataDAO().countByGID(gid);

                    if (localCount > relativeLimit) {
                        toreturn.addAll(getGermplasmListDataDAO().getByGID(gid, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            localCount = getGermplasmListDataDAO().countByGID(gid);

            if (localCount > start) {
                toreturn.addAll(getGermplasmListDataDAO().getByGID(gid, start, numOfRows));
            }
        }

        return toreturn;
    }

    @Override
    public long countGermplasmListDataByGID(Integer gid) throws MiddlewareQueryException {
        long count = 0;

        if (setWorkingDatabase(Database.LOCAL)) {
            count = count + getGermplasmListDataDAO().countByGID(gid);
        }

        if (setWorkingDatabase(Database.CENTRAL)) {
            count = count + getGermplasmListDataDAO().countByGID(gid);
        }

        return count;
    }

    @Override
    public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getGermplasmListDAO().getAllTopLevelLists(start, numOfRows);
        }

        return new ArrayList<GermplasmList>();
    }

    @Override
    public List<GermplasmList> getAllTopLevelListsBatched(int batchSize, Database instance) throws MiddlewareQueryException {
        List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

        if (setWorkingDatabase(instance)) {
        	long topLevelCount = getGermplasmListDAO().countAllTopLevelLists();
        	int start = 0;
            while (start < topLevelCount) {
                topLevelFolders.addAll(getGermplasmListDAO().getAllTopLevelLists(start, batchSize));
                start += batchSize;
            }
        }
        
        return topLevelFolders;
    }

    @Override
    public long countAllTopLevelLists(Database instance) throws MiddlewareQueryException {
        long count = 0;

        if (setWorkingDatabase(instance)) {
        	count = getGermplasmListDAO().countAllTopLevelLists();
        }
        return count;
    }

    @Override
    public Integer addGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        List<Integer> idList = addGermplasmList(list);
        return idList.size() > 0 ? idList.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
        return addOrUpdateGermplasmList(germplasmLists, Operation.ADD);
    }

    @Override
    public Integer updateGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        List<Integer> idList = updateGermplasmList(list);
        return idList.size() > 0 ? idList.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
        return addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmList(List<GermplasmList> germplasmLists, Operation operation) throws MiddlewareQueryException {
    	requireLocalDatabaseInstance();
    	Session sessionForLocal = getCurrentSessionForLocal();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListsSaved = 0;
        List<Integer> germplasmListIds = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            for (GermplasmList germplasmList : germplasmLists) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = getGermplasmListDAO().getNegativeId("id");
                    germplasmListIds.add(negativeId);
                    germplasmList.setId(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    getGermplasmListDAO().validateId(germplasmList);
                    germplasmListIds.add(germplasmList.getId());
                }
                getGermplasmListDAO().saveOrUpdate(germplasmList);
                germplasmListsSaved++;
                if (germplasmListsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    getGermplasmListDAO().flush();
                    getGermplasmListDAO().clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
        	rollbackTransaction(trans);
        	logAndThrowException(
                    "Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
                            + germplasmLists + ", operation-" + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListIds;
    }

    @Override
    public int deleteGermplasmListByListId(Integer listId) throws MiddlewareQueryException {
        GermplasmList germplasmList = getGermplasmListById(listId);
        return deleteGermplasmList(germplasmList);
    }

    @Override
    public int deleteGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        return deleteGermplasmList(list);
    }

    @Override
    public int deleteGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
    	requireLocalDatabaseInstance();
        Session sessionForLocal = getCurrentSessionForLocal();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListsDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            List<GermplasmListData> listDataList;
            for (GermplasmList germplasmList : germplasmLists) {
            	
            	//fetch list data for cascade delete
            	int count = (int) getGermplasmListDataDAO().countByListId(germplasmList.getId());
            	if (count > 0) {
	            	listDataList = getGermplasmListDataDAO().getByListId(germplasmList.getId(), 0, count);
	            	germplasmList.setListData(listDataList);
            	}
            	
            	//delete GermplasmList
                getGermplasmListDAO().makeTransient(germplasmList);
                germplasmListsDeleted++;
            }
            
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
                            + germplasmLists + "): " + e.getMessage(), e, LOG);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListsDeleted;
    }

    @Override
    public Integer addGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        List<Integer> ids = addGermplasmListData(list);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
    }

    @Override
    public Integer updateGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        List<Integer> ids = updateGermplasmListData(list);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmListData(List<GermplasmListData> germplasmListDatas, Operation operation)
            throws MiddlewareQueryException {
        
    	requireLocalDatabaseInstance();
        Session sessionForLocal = getCurrentSessionForLocal();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataSaved = 0;
        List<Integer> idGermplasmListDataSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeListId = getGermplasmListDataDAO().getNegativeId("id");
                    germplasmListData.setId(negativeListId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    getGermplasmListDataDAO().validateId(germplasmListData);
                }
                GermplasmListData recordSaved = getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
                idGermplasmListDataSaved.add(recordSaved.getId());
                germplasmListDataSaved++;
                if (germplasmListDataSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                	getGermplasmListDataDAO().flush();
                	getGermplasmListDataDAO().clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
                            + germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(), e, LOG);
        } finally {
            sessionForLocal.flush();
        }

        return idGermplasmListDataSaved;
    }

    @Override
    public int deleteGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException {
    	requireLocalDatabaseInstance();
        Session sessionForLocal = getCurrentSessionForLocal();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            germplasmListDataDeleted = getGermplasmListDataDAO().deleteByListId(listId);
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
                            + listId + "): " + e.getMessage(), e, LOG);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListDataDeleted;
    }

    @Override
    public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
        GermplasmListData germplasmListData = getGermplasmListDataByListIdAndEntryId(listId, entryId);
        return deleteGermplasmListData(germplasmListData);
    }

    @Override
    public int deleteGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return deleteGermplasmListData(list);
    }

    @Override
    public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
    	requireLocalDatabaseInstance();
        Session sessionForLocal = getCurrentSessionForLocal();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                getGermplasmListDataDAO().makeTransient(germplasmListData);
                germplasmListDataDeleted++;
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
                            + germplasmListDatas + "): " + e.getMessage(), e, LOG);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListDataDeleted;
    }

    @Override
    public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) throws MiddlewareQueryException {

        List<GermplasmList> childLists = new ArrayList<GermplasmList>();

        if (setWorkingDatabase(parentId)) {
            childLists = getGermplasmListDAO().getByParentFolderId(parentId, start, numOfRows);
        }

        return childLists;
    }

    @Override
    public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, int batchSize) throws MiddlewareQueryException {
    	List<GermplasmList> childLists = new ArrayList<GermplasmList>();
    	setWorkingDatabase(parentId);
        Session session = getSession(parentId);
        if (session == null) {
            return childLists;
        }

        // initialize session & transaction
        Transaction trans = null;

        try {
            // begin transaction
            trans = session.beginTransaction();

            int start = 0;
            long childListCount = getGermplasmListDAO().countByParentFolderId(parentId);
            while (start < childListCount) {
                childLists.addAll(getGermplasmListDAO().getByParentFolderId(parentId, start, batchSize));
                start += batchSize;
            }
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while retrieving germplasm sub-lists: GermplasmListManager.getGermplasmListByParentFolderIdBatched(parentId="
                            + parentId + ", batchSize=" + batchSize + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return childLists;
    }

    @Override
    public long countGermplasmListByParentFolderId(Integer parentId) throws MiddlewareQueryException {
        long result = 0;

        if (setWorkingDatabase(parentId)) {
            result = getGermplasmListDAO().countByParentFolderId(parentId);
        }

        return result;
    }

}
