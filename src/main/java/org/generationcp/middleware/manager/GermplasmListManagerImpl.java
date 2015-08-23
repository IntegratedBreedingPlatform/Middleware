/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.DatabaseBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GermplasmListManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@SuppressWarnings("unchecked")
@Transactional
public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListManagerImpl.class);

	public GermplasmListManagerImpl() {
	}

	public GermplasmListManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmListManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public GermplasmList getGermplasmListById(Integer id) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().getById(id, false);
	}

	@Override
	public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countAllGermplasmLists() throws MiddlewareQueryException {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countAllExceptDeleted", new Object[] {}, new Class[] {});
	}

	@Override
	public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation)
			throws MiddlewareQueryException {
		return this.getGermplasmListDAO().getByName(name, operation, start, numOfRows);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
			throws MiddlewareQueryException {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getByName", new Object[] {name, operation, start,
				numOfRows}, new Class[] {String.class, Operation.class, Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countGermplasmListByName(String name, Operation operation) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public long countGermplasmListByName(String name, Operation operation, Database instance) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	@Override
	public List<GermplasmList> getGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance)
			throws MiddlewareQueryException {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getByStatus", new Object[] {status, start, numOfRows},
				new Class[] {Integer.class, Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countGermplasmListByStatus(Integer status, Database instance) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().countByStatus(status);
	}

	@Override
	public List<GermplasmList> getGermplasmListByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {

		List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListByGID(Integer gid) throws MiddlewareQueryException {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getGermplasmListDataDAO().getByListId(id, start, numOfRows);
	}

	@Override
	public long countGermplasmListDataByListId(Integer id) throws MiddlewareQueryException {
		return this.getGermplasmListDataDAO().countByListId(id);
	}

	@Override
	public long countListDataProjectGermplasmListDataByListId(Integer id) throws MiddlewareQueryException {

		return this.countFromInstanceByIdAndMethod(this.getListDataProjectDAO(), id, "countByListId", new Object[] {id},
				new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException {

		return this.getFromInstanceByIdAndMethod(this.getGermplasmListDataDAO(), listId, "getByListIdAndGID", new Object[] {listId, gid},
				new Class[] {Integer.class, Integer.class});
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
		return this.getGermplasmListDataDAO().getByListIdAndEntryId(listId, entryId);
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndLrecId(Integer listId, Integer lrecId) throws MiddlewareQueryException {
		return this.getGermplasmListDataDAO().getByListIdAndLrecId(listId, lrecId);
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {

		List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDataDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListDataByGID(Integer gid) throws MiddlewareQueryException {

		return this.countAllByMethod(this.getGermplasmListDataDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllTopLevelLists", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public List<GermplasmList> getAllTopLevelListsBatched(int batchSize) throws MiddlewareQueryException {
		List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

		long topLevelCount = this.getGermplasmListDAO().countAllTopLevelLists();
		int start = 0;
		while (start < topLevelCount) {
			topLevelFolders.addAll(this.getGermplasmListDAO().getAllTopLevelLists(start, batchSize));
			start += batchSize;
		}

		return topLevelFolders;
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<GermplasmList> getAllTopLevelListsBatched(int batchSize, Database instance) throws MiddlewareQueryException {
		List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

		long topLevelCount = this.getGermplasmListDAO().countAllTopLevelLists();
		int start = 0;
		while (start < topLevelCount) {
			topLevelFolders.addAll(this.getGermplasmListDAO().getAllTopLevelLists(start, batchSize));
			start += batchSize;
		}

		return topLevelFolders;
	}

	@Override
	public long countAllTopLevelLists(Database instance) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().countAllTopLevelLists();
	}

	@Override
	public Integer addGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		List<Integer> idList = this.addGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		List<Integer> idList = this.updateGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmList(List<GermplasmList> germplasmLists, Operation operation) throws MiddlewareQueryException {

		List<Integer> germplasmListIds = new ArrayList<Integer>();
		try {
			for (GermplasmList germplasmList : germplasmLists) {
				if (operation == Operation.ADD) {
					germplasmList = this.getGermplasmListDAO().saveOrUpdate(germplasmList);
					germplasmListIds.add(germplasmList.getId());
				} else if (operation == Operation.UPDATE) {
					germplasmListIds.add(germplasmList.getId());
					this.getGermplasmListDAO().merge(germplasmList);
				}
			}

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
							+ germplasmLists + ", operation-" + operation + "): " + e.getMessage(), e, GermplasmListManagerImpl.LOG);
		}

		return germplasmListIds;
	}

	@Override
	public int deleteGermplasmListByListId(Integer listId) throws MiddlewareQueryException {
		GermplasmList germplasmList = this.getGermplasmListById(listId);
		return this.deleteGermplasmList(germplasmList);
	}

	@Override
	public int deleteGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		return this.deleteGermplasmList(list);
	}
	
	@Override
	public int deleteGermplasmListsByProgram(String programUUID) throws MiddlewareQueryException {
		List<GermplasmList> lists = this.getGermplasmListDAO().getListsByProgram(programUUID);
		return this.deleteGermplasmList(lists);
	}

	@Override
	public int deleteGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException {
		int germplasmListsDeleted = 0;
		try {
			// begin delete transaction
			List<Integer> listIds = new ArrayList<Integer>();
			for (GermplasmList germplasmList : germplasmLists) {
				listIds.add(germplasmList.getId());
			}

			if (!listIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForLists(listIds);
			}

			for (GermplasmList germplasmList : germplasmLists) {

				germplasmList.setStatus(9);
				this.updateGermplasmList(germplasmList);

				germplasmListsDeleted++;
			}
		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
							+ germplasmLists + "): " + e.getMessage(), e, GermplasmListManagerImpl.LOG);
		}
		return germplasmListsDeleted;
	}

	@Override
	public Integer addGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		List<Integer> ids = this.addGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		List<Integer> ids = this.updateGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmListData(List<GermplasmListData> germplasmListDatas, Operation operation)
			throws MiddlewareQueryException {

		List<Integer> idGermplasmListDataSaved = new ArrayList<Integer>();
		try {
			GermplasmListDataDAO dao = new GermplasmListDataDAO();
			dao.setSession(this.getActiveSession());

			List<Integer> deletedListEntryIds = new ArrayList<Integer>();

			for (GermplasmListData germplasmListData : germplasmListDatas) {

				GermplasmListData recordSaved = this.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
				idGermplasmListDataSaved.add(recordSaved.getId());
				if (germplasmListData.getStatus() != null && germplasmListData.getStatus().intValue() == 9) {
					deletedListEntryIds.add(germplasmListData.getId());
				}
			}

			if (!deletedListEntryIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForListEntries(deletedListEntryIds);
			}
			

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(), e, GermplasmListManagerImpl.LOG);
		}
		return idGermplasmListDataSaved;
	}

	@Override
	public int deleteGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException {

		int germplasmListDataDeleted = 0;
		try {
			germplasmListDataDeleted = this.getGermplasmListDataDAO().deleteByListId(listId);
			this.getTransactionDao().cancelUnconfirmedTransactionsForLists(Arrays.asList(new Integer[] {listId}));
		} catch (Exception e) {
			this.logAndThrowException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
							+ listId + "): " + e.getMessage(), e, GermplasmListManagerImpl.LOG);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
		GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListDataByListIdLrecId(Integer listId, Integer lrecId) throws MiddlewareQueryException {
		GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndLrecId(listId, lrecId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		return this.deleteGermplasmListData(list);
	}

	@Override
	public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException {

		int germplasmListDataDeleted = 0;
		try {
			// begin delete transaction


			List<Integer> listEntryIds = new ArrayList<Integer>();
			for (GermplasmListData germplasmListData : germplasmListDatas) {
				listEntryIds.add(germplasmListData.getId());
			}

			if (!listEntryIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForListEntries(listEntryIds);
			}

			for (GermplasmListData germplasmListData : germplasmListDatas) {
				this.getGermplasmListDataDAO().makeTransient(germplasmListData);
				germplasmListDataDeleted++;
			}
			

		} catch (Exception e) {

			this.logAndThrowException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + "): " + e.getMessage(), e, GermplasmListManagerImpl.LOG);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) throws MiddlewareQueryException {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), Database.LOCAL, "getByParentFolderId", new Object[] {parentId,
				start, numOfRows}, new Class[] {Integer.class, Integer.TYPE, Integer.TYPE});
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, int batchSize) throws MiddlewareQueryException {
		List<GermplasmList> childLists = new ArrayList<GermplasmList>();
		int start = 0;
		long childListCount = this.getGermplasmListDAO().countByParentFolderId(parentId);
		while (start < childListCount) {
			childLists.addAll(this.getGermplasmListDAO().getByParentFolderId(parentId, start, batchSize));
			start += batchSize;
		}
		return childLists;
	}

	@Override
	public long countGermplasmListByParentFolderId(Integer parentId) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().countByParentFolderId(parentId);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmListTypes() throws MiddlewareQueryException {
		List<UserDefinedField> toReturn = new ArrayList<UserDefinedField>();

		List results = this.getGermplasmListDAO().getGermplasmListTypes();

		for (Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer fldno = (Integer) result[0];
				String ftable = (String) result[1];
				String ftype = (String) result[2];
				String fcode = (String) result[3];
				String fname = (String) result[4];
				String ffmt = (String) result[5];
				String fdesc = (String) result[6];
				Integer lfldno = (Integer) result[7];
				User user = this.getUserDao().getById((Integer) result[8], false);
				Integer fdate = (Integer) result[9];
				Integer scaleid = (Integer) result[10];

				UserDefinedField userDefinedField =
						new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, user, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmNameTypes() throws MiddlewareQueryException {
		List<UserDefinedField> toReturn = new ArrayList<UserDefinedField>();

		List results =
				this.getFromInstanceByMethod(this.getGermplasmListDAO(), Database.LOCAL, "getGermplasmNameTypes", new Object[] {},
						new Class[] {});

		for (Object o : results) {
			Object[] result = (Object[]) o;
			if (result != null) {
				Integer fldno = (Integer) result[0];
				String ftable = (String) result[1];
				String ftype = (String) result[2];
				String fcode = (String) result[3];
				String fname = (String) result[4];
				String ffmt = (String) result[5];
				String fdesc = (String) result[6];
				Integer lfldno = (Integer) result[7];
				User user = this.getUserDao().getById((Integer) result[8], false);
				Integer fdate = (Integer) result[9];
				Integer scaleid = (Integer) result[10];

				UserDefinedField userDefinedField =
						new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, user, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}

	@Override
	public List<GermplasmList> searchForGermplasmList(String q, Operation o) throws MiddlewareQueryException {
		List<GermplasmList> results = new ArrayList<GermplasmList>();
		results.addAll(this.getGermplasmListDAO().searchForGermplasmLists(q, o));
		return results;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<GermplasmList> searchForGermplasmList(String q, Operation o, boolean searchPublicData) throws MiddlewareQueryException {
		List<GermplasmList> results = new ArrayList<GermplasmList>();
		results.addAll(this.getGermplasmListDAO().searchForGermplasmLists(q, o));
		return results;
	}

	@Override
	public List<ListDataInfo> saveListDataColumns(List<ListDataInfo> listDataCollection) throws MiddlewareQueryException {
		return this.getListDataPropertySaver().saveProperties(listDataCollection);
	}

	@Override
	public GermplasmListNewColumnsInfo getAdditionalColumnsForList(Integer listId) throws MiddlewareQueryException {
		return this.getListDataPropertyDAO().getPropertiesForList(listId);
	}

	@Override
	public List<ListDataProperty> saveListDataProperties(List<ListDataProperty> listDataProps) throws MiddlewareQueryException {
		return this.getListDataPropertySaver().saveListDataProperties(listDataProps);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListData(Integer listID) throws MiddlewareQueryException {
		return this.getListDataProjectDAO().getByListId(listID);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListDataWithParents(Integer listID) throws MiddlewareQueryException {
		return this.getListDataProjectDAO().getListDataProjectWithParents(listID);
	}

	@Override
	public Integer retrieveDataListIDFromListDataProjectListID(Integer listDataProjectListID) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listDataProjectListID);
	}

	@Override
	public GermplasmList getGermplasmListByListRef(Integer listRef) throws MiddlewareQueryException {
		return this.getGermplasmListDAO().getByListRef(listRef);
	}
}
