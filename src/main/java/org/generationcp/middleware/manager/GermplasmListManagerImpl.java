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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GermplasmListManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@SuppressWarnings("unchecked")
@Transactional
public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager {

	public GermplasmListManagerImpl() {
	}

	public GermplasmListManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmListManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public GermplasmList getGermplasmListById(Integer id) {
		return this.getGermplasmListDAO().getById(id, false);
	}

	@Override
	public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows) {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countAllGermplasmLists() {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countAllExceptDeleted", new Object[] {}, new Class[] {});
	}

	@Override
	public List<GermplasmList> getGermplasmListByName(String name, String programUUID, int start, int numOfRows, Operation operation)
 {
		return this.getGermplasmListDAO().getByName(name, programUUID, operation, start, numOfRows);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
 {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getByName",
				new Object[] {name, operation, start, numOfRows}, new Class[] {String.class, Operation.class, Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countGermplasmListByName(String name, Operation operation) {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public long countGermplasmListByName(String name, Operation operation, Database instance) {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	@Override
	public long countGermplasmListByStatus(Integer status, Database instance) {
		return this.getGermplasmListDAO().countByStatus(status);
	}

	@Override
	public List<GermplasmList> getGermplasmListByGID(Integer gid, int start, int numOfRows) {

		List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListByGID(Integer gid) {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListId(Integer id) {
		return this.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public long countGermplasmListDataByListId(Integer id) {
		return this.getGermplasmListDataDAO().countByListId(id);
	}

	@Override
	public long countListDataProjectGermplasmListDataByListId(Integer id) {

		return this.countFromInstanceByIdAndMethod(this.getListDataProjectDAO(), id, "countByListId", new Object[] {id},
				new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) {

		return this.getFromInstanceByIdAndMethod(this.getGermplasmListDataDAO(), listId, "getByListIdAndGID", new Object[] {listId, gid},
				new Class[] {Integer.class, Integer.class});
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) {
		return this.getGermplasmListDataDAO().getByListIdAndEntryId(listId, entryId);
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndLrecId(Integer listId, Integer lrecId) {
		return this.getGermplasmListDataDAO().getByListIdAndLrecId(listId, lrecId);
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) {

		List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDataDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListDataByGID(Integer gid) {

		return this.countAllByMethod(this.getGermplasmListDataDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance) {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllTopLevelLists", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public List<GermplasmList> getAllTopLevelListsBatched(String programUUID, int batchSize) {
		List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

		long topLevelCount = this.getGermplasmListDAO().countAllTopLevelLists(programUUID);
		int start = 0;
		while (start < topLevelCount) {
			topLevelFolders.addAll(this.getGermplasmListDAO().getAllTopLevelLists(programUUID, start, batchSize));
			start += batchSize;
		}

		return topLevelFolders;
	}

	@Override
	public long countAllTopLevelLists(String programUUID) {
		return this.getGermplasmListDAO().countAllTopLevelLists(programUUID);
	}

	@Override
	public Integer addGermplasmList(GermplasmList germplasmList) {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		List<Integer> idList = this.addGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmList(GermplasmList germplasmList) {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		List<Integer> idList = this.updateGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmList(List<GermplasmList> germplasmLists, Operation operation)
 {

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

			throw new MiddlewareQueryException(
					"Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
							+ germplasmLists + ", operation-" + operation + "): " + e.getMessage(), e);
		}

		return germplasmListIds;
	}

	@Override
	public int deleteGermplasmListByListId(Integer listId) {
		GermplasmList germplasmList = this.getGermplasmListById(listId);
		return this.deleteGermplasmList(germplasmList);
	}

	@Override
	public int deleteGermplasmList(GermplasmList germplasmList) {
		List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		return this.deleteGermplasmList(list);
	}

	@Override
	public int deleteGermplasmListsByProgram(String programUUID) {
		List<GermplasmList> lists = this.getGermplasmListDAO().getListsByProgram(programUUID);
		return this.deleteGermplasmList(lists);
	}

	@Override
	public int deleteGermplasmList(List<GermplasmList> germplasmLists) {
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
			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
							+ germplasmLists + "): " + e.getMessage(), e);
		}
		return germplasmListsDeleted;
	}

	@Override
	public Integer addGermplasmListData(GermplasmListData germplasmListData) {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		List<Integer> ids = this.addGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmListData(GermplasmListData germplasmListData) {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		List<Integer> ids = this.updateGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmListData(List<GermplasmListData> germplasmListDatas, Operation operation)
 {

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

			throw new MiddlewareQueryException(
					"Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idGermplasmListDataSaved;
	}

	@Override
	public int deleteGermplasmListDataByListId(Integer listId) {

		int germplasmListDataDeleted = 0;
		try {
			germplasmListDataDeleted = this.getGermplasmListDataDAO().deleteByListId(listId);
			this.getTransactionDao().cancelUnconfirmedTransactionsForLists(Arrays.asList(new Integer[] {listId}));

		} catch (Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
							+ listId + "): " + e.getMessage(), e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) {
		GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListDataByListIdLrecId(Integer listId, Integer lrecId) {
		GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndLrecId(listId, lrecId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListData(GermplasmListData germplasmListData) {
		List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		return this.deleteGermplasmListData(list);
	}

	@Override
	public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) {

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

			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + "): " + e.getMessage(), e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, String programUUID, int start, int numOfRows)
 {

		return this.getGermplasmListDAO().getByParentFolderId(parentId, programUUID, start, numOfRows);
	}

	@Override
	public GermplasmList getLastSavedGermplasmListByUserId(Integer userID, String programUUID) {
		return this.getGermplasmListDAO().getLastCreatedByUserID(userID, programUUID);
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, String programUUID, int batchSize)
 {
		List<GermplasmList> childLists = new ArrayList<GermplasmList>();
		int start = 0;
		long childListCount = this.getGermplasmListDAO().countByParentFolderId(parentId, programUUID);
		while (start < childListCount) {
			childLists.addAll(this.getGermplasmListDAO().getByParentFolderId(parentId, programUUID, start, batchSize));
			start += batchSize;
		}
		return childLists;
	}

	@Override
	public long countGermplasmListByParentFolderId(Integer parentId, String programUUID) {
		return this.getGermplasmListDAO().countByParentFolderId(parentId, programUUID);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmListTypes() {
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
	public List<UserDefinedField> getGermplasmNameTypes() {
		List<UserDefinedField> toReturn = new ArrayList<UserDefinedField>();

		List results = this.getFromInstanceByMethod(this.getGermplasmListDAO(), Database.LOCAL, "getGermplasmNameTypes", new Object[] {},
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
	public List<GermplasmList> searchForGermplasmList(String q, String programUUID, Operation o) {
		List<GermplasmList> results = new ArrayList<GermplasmList>();
		results.addAll(this.getGermplasmListDAO().searchForGermplasmLists(q, programUUID, o));
		return results;
	}

	@Override
	public List<ListDataInfo> saveListDataColumns(List<ListDataInfo> listDataCollection) {
		return this.getListDataPropertySaver().saveProperties(listDataCollection);
	}

	@Override
	public GermplasmListNewColumnsInfo getAdditionalColumnsForList(Integer listId) {
		return this.getListDataPropertyDAO().getPropertiesForList(listId);
	}

	@Override
	public List<ListDataProperty> saveListDataProperties(List<ListDataProperty> listDataProps) {
		return this.getListDataPropertySaver().saveListDataProperties(listDataProps);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListData(Integer listID) {
		return this.getListDataProjectDAO().getByListId(listID);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListDataWithParents(Integer listID) {
		return this.getListDataProjectDAO().getListDataProjectWithParents(listID);
	}

	@Override
	public Integer retrieveDataListIDFromListDataProjectListID(Integer listDataProjectListID) {
		return this.getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listDataProjectListID);
	}

	@Override
	public GermplasmList getGermplasmListByListRef(Integer listRef) {
		return this.getGermplasmListDAO().getByListRef(listRef);
	}

}
