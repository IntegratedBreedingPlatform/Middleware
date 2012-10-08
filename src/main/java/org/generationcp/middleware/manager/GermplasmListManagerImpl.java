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

public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager{

    public GermplasmListManagerImpl() {
    }

    public GermplasmListManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GermplasmListManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public GermplasmList getGermplasmListById(Integer id) {
        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        GermplasmList list = dao.getById(id, false);
        return list;
    }

    @Override
    public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.getAll(start, numOfRows);
    }

    @Override
    public long countAllGermplasmLists() {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        if (sessionForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countAll();
        }

        return count;
    }

    @Override
    public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
            throws MiddlewareQueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.getByName(name, start, numOfRows, operation);

    }

    @Override
    public long countGermplasmListByName(String name, Operation operation) throws MiddlewareQueryException {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countByName(name, operation);
        }

        if (sessionForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countByName(name, operation);
        }

        return count;
    }

    @Override
    public List<GermplasmList> getGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        GermplasmListDAO dao = new GermplasmListDAO();

        Session session = getSession(instance);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.getByStatus(status, start, numOfRows);

    }

    @Override
    public long countGermplasmListByStatus(Integer status) throws MiddlewareQueryException {
        long count = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countByStatus(status);
        }

        if (sessionForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countByStatus(status);
        }

        return count;
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmListData>();
        }

        return dao.getByListId(id, start, numOfRows);
    }

    @Override
    public long countGermplasmListDataByListId(Integer id) throws MiddlewareQueryException {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }

        return dao.countByListId(id);
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        Session session = getSession(listId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmListData>();
        }

        return dao.getByListIdAndGID(listId, gid);
    }

    @Override
    public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        Session session = getSession(listId);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getByListIdAndEntryId(listId, entryId);
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        GermplasmListDataDAO dao = new GermplasmListDataDAO();

        List<GermplasmListData> toreturn = new ArrayList<GermplasmListData>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            centralCount = dao.countByGID(gid);

            if (centralCount > start) {
                toreturn.addAll(dao.getByGID(gid, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                    if (sessionForLocal != null) {
                        dao.setSession(sessionForLocal);
                        localCount = dao.countByGID(gid);

                        if (localCount > 0) {
                            toreturn.addAll(dao.getByGID(gid, 0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;

                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByGID(gid);

                    if (localCount > relativeLimit) {
                        toreturn.addAll(dao.getByGID(gid, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countByGID(gid);

            if (localCount > start) {
                toreturn.addAll(dao.getByGID(gid, start, numOfRows));
            }
        }

        return toreturn;
    }

    @Override
    public long countGermplasmListDataByGID(Integer gid) throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;

        if (sessionForLocal != null) {
            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(sessionForLocal);
            count = count + dao.countByGID(gid);
        }

        if (sessionForCentral != null) {
            GermplasmListDataDAO centralDao = new GermplasmListDataDAO();
            centralDao.setSession(sessionForCentral);
            count = count + centralDao.countByGID(gid);
        }

        return count;
    }

    @Override
    public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(instance);

        List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmList>();
        }

        topLevelFolders = dao.getAllTopLevelLists(start, numOfRows);

        return topLevelFolders;
    }

    @Override
    public List<GermplasmList> getAllTopLevelListsBatched(int batchSize, Database instance) throws MiddlewareQueryException {
        List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();
        Session session = getSession(instance);
        if (session == null) {
            return topLevelFolders;
        }

        // initialize session & transaction
        Transaction trans = null;

        try {
            // begin transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            long topLevelCount = dao.countAllTopLevelLists();
            int start = 0;
            while (start < topLevelCount) {
                topLevelFolders.addAll(dao.getAllTopLevelLists(start, batchSize));
                start += batchSize;
            }

            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while retrieving top level folders: GermplasmListManager.getAllTopLevelListsBatched(batchSize="
                            + batchSize + ", database=" + instance + "): " + e.getMessage(), e);
        }

        return topLevelFolders;
    }

    @Override
    public long countAllTopLevelLists(Database instance) throws MiddlewareQueryException {
        long count = 0;

        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(instance);
        dao.setSession(session);
        count = dao.countAllTopLevelLists();

        return count;
    }

    @Override
    public Integer addGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        List<Integer> idList = addGermplasmList(list);
        return idList.get(0);
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
        return idList.get(0);
    }

    @Override
    public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
        return addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmList(List<GermplasmList> germplasmLists, Operation operation) throws MiddlewareQueryException {
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListsSaved = 0;
        List<Integer> germplasmListIds = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            for (GermplasmList germplasmList : germplasmLists) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("id");
                    germplasmListIds.add(negativeId);
                    germplasmList.setId(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    dao.validateId(germplasmList);
                    germplasmListIds.add(germplasmList.getId());
                }
                dao.saveOrUpdate(germplasmList);
                germplasmListsSaved++;
                if (germplasmListsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
                            + germplasmLists + ", operation-" + operation + "): " + e.getMessage(), e);
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
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListsDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            for (GermplasmList germplasmList : germplasmLists) {
                dao.makeTransient(germplasmList);
                germplasmListsDeleted++;
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
                            + germplasmLists + "): " + e.getMessage(), e);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListsDeleted;
    }

    @Override
    public int addGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return addGermplasmListData(list);
    }

    @Override
    public int addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
    }

    @Override
    public int updateGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return updateGermplasmListData(list);
    }

    @Override
    public int updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
    }

    private int addOrUpdateGermplasmListData(List<GermplasmListData> germplasmListDatas, Operation operation)
            throws MiddlewareQueryException {
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataSaved = 0;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeListId = dao.getNegativeId("id");
                    germplasmListData.setId(negativeListId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    dao.validateId(germplasmListData);
                }
                dao.saveOrUpdate(germplasmListData);
                germplasmListDataSaved++;
                if (germplasmListDataSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
                            + germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(), e);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListDataSaved;
    }

    @Override
    public int deleteGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException {
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            germplasmListDataDeleted = dao.deleteByListId(listId);
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
                            + listId + "): " + e.getMessage(), e);
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
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                dao.makeTransient(germplasmListData);
                germplasmListDataDeleted++;
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
                            + germplasmListDatas + "): " + e.getMessage(), e);
        } finally {
            sessionForLocal.flush();
        }

        return germplasmListDataDeleted;
    }

    @Override
    public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) throws MiddlewareQueryException {

        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(parentId);
        List<GermplasmList> childLists;

        if (session != null) {
            dao.setSession(session);
            childLists = dao.getByParentFolderId(parentId, start, numOfRows);
        } else {
            childLists = new ArrayList<GermplasmList>();
        }

        return childLists;
    }

    @Override
    public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, int batchSize) throws MiddlewareQueryException {
        List<GermplasmList> childLists = new ArrayList<GermplasmList>();
        Session session = getSession(parentId);
        if (session == null) {
            return childLists;
        }

        // initialize session & transaction
        Transaction trans = null;

        try {
            // begin transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            int start = 0;
            long childListCount = dao.countByParentFolderId(parentId);
            while (start < childListCount) {
                childLists.addAll(dao.getByParentFolderId(parentId, start, batchSize));
                start += batchSize;
            }
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException(
                    "Error encountered while retrieving germplasm sub-lists: GermplasmListManager.getGermplasmListByParentFolderIdBatched(parentId="
                            + parentId + ", batchSize=" + batchSize + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }

        return childLists;
    }

    @Override
    public long countGermplasmListByParentFolderId(Integer parentId) throws MiddlewareQueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        Session session = getSession(parentId);
        long result = 0;

        if (session != null) {
            dao.setSession(session);
            result = dao.countByParentFolderId(parentId);
        }

        return result;
    }

}
