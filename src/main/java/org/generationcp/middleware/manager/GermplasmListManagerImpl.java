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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListMetadata;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;


/**
 * Implementation of the GermplasmListManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@SuppressWarnings("unchecked")
@Transactional
public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListManagerImpl.class);

	public GermplasmListManagerImpl() {
	}

	public GermplasmListManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmListManagerImpl(final HibernateSessionProvider sessionProvider, final String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public GermplasmList getGermplasmListById(final Integer id) {
		return this.getGermplasmListDAO().getById(id, false);
	}

	@Override
	public List<GermplasmList> getAllGermplasmLists(final int start, final int numOfRows) {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<GermplasmList> getAllGermplasmLists(final int start, final int numOfRows, final Database instance) {
		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllExceptDeleted", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countAllGermplasmLists() {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countAllExceptDeleted", new Object[] {}, new Class[] {});
	}

	@Override
	public List<GermplasmList> getGermplasmListByName(final String name, final String programUUID, final int start, final int numOfRows,
			final Operation operation) {
		return this.getGermplasmListDAO().getByName(name, programUUID, operation, start, numOfRows);
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public List<GermplasmList> getGermplasmListByName(final String name, final int start, final int numOfRows, final Operation operation,
			final Database instance) {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getByName", new Object[] {name, operation, start,
				numOfRows}, new Class[] {String.class, Operation.class, Integer.TYPE, Integer.TYPE});
	}

	@Override
	public long countGermplasmListByName(final String name, final Operation operation) {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public long countGermplasmListByName(final String name, final Operation operation, final Database instance) {
		return this.getGermplasmListDAO().countByName(name, operation);
	}

	@Override
	public long countGermplasmListByStatus(final Integer status, final Database instance) {
		return this.getGermplasmListDAO().countByStatus(status);
	}

	@Override
	public List<GermplasmList> getGermplasmListByGID(final Integer gid, final int start, final int numOfRows) {

		final List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListByGID(final Integer gid) {

		return this.countAllByMethod(this.getGermplasmListDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListId(final Integer id) {
		return this.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public long countGermplasmListDataByListId(final Integer id) {
		return this.getGermplasmListDataDAO().countByListId(id);
	}

	@Override
	public long countListDataProjectGermplasmListDataByListId(final Integer id) {

		return this.countFromInstanceByIdAndMethod(this.getListDataProjectDAO(), id, "countByListId", new Object[] {id},
				new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListIdAndGID(final Integer listId, final Integer gid) {

		return this.getFromInstanceByIdAndMethod(this.getGermplasmListDataDAO(), listId, "getByListIdAndGID", new Object[] {listId, gid},
				new Class[] {Integer.class, Integer.class});
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndEntryId(final Integer listId, final Integer entryId) {
		return this.getGermplasmListDataDAO().getByListIdAndEntryId(listId, entryId);
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndLrecId(final Integer listId, final Integer lrecId) {
		return this.getGermplasmListDataDAO().getByListIdAndLrecId(listId, lrecId);
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByGID(final Integer gid, final int start, final int numOfRows) {

		final List<String> methodNames = Arrays.asList("countByGID", "getByGID");
		return this.getFromCentralAndLocalByMethod(this.getGermplasmListDataDAO(), methodNames, start, numOfRows, new Object[] {gid},
				new Class[] {Integer.class});
	}

	@Override
	public long countGermplasmListDataByGID(final Integer gid) {

		return this.countAllByMethod(this.getGermplasmListDataDAO(), "countByGID", new Object[] {gid}, new Class[] {Integer.class});
	}

	@Override
	public List<GermplasmList> getAllTopLevelLists(final int start, final int numOfRows, final Database instance) {

		return this.getFromInstanceByMethod(this.getGermplasmListDAO(), instance, "getAllTopLevelLists", new Object[] {start, numOfRows},
				new Class[] {Integer.TYPE, Integer.TYPE});
	}

	@Override
	public List<GermplasmList> getAllTopLevelListsBatched(final String programUUID, final int batchSize) {
		final List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();

		final long topLevelCount = this.getGermplasmListDAO().countAllTopLevelLists(programUUID);
		int start = 0;
		while (start < topLevelCount) {
			topLevelFolders.addAll(this.getGermplasmListDAO().getAllTopLevelLists(programUUID, start, batchSize));
			start += batchSize;
		}

		return topLevelFolders;
	}

	@Override
	public long countAllTopLevelLists(final String programUUID) {
		return this.getGermplasmListDAO().countAllTopLevelLists(programUUID);
	}

	@Override
	public Integer addGermplasmList(final GermplasmList germplasmList) {
		final List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		final List<Integer> idList = this.addGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmList(final List<GermplasmList> germplasmLists) {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmList(final GermplasmList germplasmList) {
		final List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		final List<Integer> idList = this.updateGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmList(final List<GermplasmList> germplasmLists) {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmList(final List<GermplasmList> germplasmLists, final Operation operation) {

		final List<Integer> germplasmListIds = new ArrayList<Integer>();
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

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
							+ germplasmLists + ", operation-" + operation + "): " + e.getMessage(), e);
		}

		return germplasmListIds;
	}

	@Override
	public int deleteGermplasmListByListId(final Integer listId) {
		final GermplasmList germplasmList = this.getGermplasmListById(listId);
		return this.deleteGermplasmList(germplasmList);
	}

	@Override
	public int deleteGermplasmList(final GermplasmList germplasmList) {
		final List<GermplasmList> list = new ArrayList<GermplasmList>();
		list.add(germplasmList);
		return this.deleteGermplasmList(list);
	}

	@Override
	public int deleteGermplasmListsByProgram(final String programUUID) {
		final List<GermplasmList> lists = this.getGermplasmListDAO().getListsByProgram(programUUID);
		return this.deleteGermplasmList(lists);
	}

	@Override
	public int deleteGermplasmList(final List<GermplasmList> germplasmLists) {
		int germplasmListsDeleted = 0;
		try {
			// begin delete transaction

			final List<Integer> listIds = new ArrayList<Integer>();
			for (final GermplasmList germplasmList : germplasmLists) {
				listIds.add(germplasmList.getId());
			}

			if (!listIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForLists(listIds);
			}

			for (final GermplasmList germplasmList : germplasmLists) {

				germplasmList.setStatus(9);
				this.updateGermplasmList(germplasmList);

				germplasmListsDeleted++;
			}
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
							+ germplasmLists + "): " + e.getMessage(), e);
		}
		return germplasmListsDeleted;
	}

	@Override
	public Integer addGermplasmListData(final GermplasmListData germplasmListData) {
		final List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		final List<Integer> ids = this.addGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmListData(final List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
	}

	@Override
	public Integer updateGermplasmListData(final GermplasmListData germplasmListData) {
		final List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		final List<Integer> ids = this.updateGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmListData(final List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmListData(final List<GermplasmListData> germplasmListDatas, final Operation operation) {

		final List<Integer> idGermplasmListDataSaved = new ArrayList<Integer>();
		try {
			final GermplasmListDataDAO dao = new GermplasmListDataDAO();
			dao.setSession(this.getActiveSession());

			final List<Integer> deletedListEntryIds = new ArrayList<Integer>();

			for (final GermplasmListData germplasmListData : germplasmListDatas) {

				final GermplasmListData recordSaved = this.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
				idGermplasmListDataSaved.add(recordSaved.getId());
				if (germplasmListData.getStatus() != null && germplasmListData.getStatus().intValue() == 9) {
					deletedListEntryIds.add(germplasmListData.getId());
				}
			}

			if (!deletedListEntryIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForListEntries(deletedListEntryIds);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(), e);
		}

		return idGermplasmListDataSaved;
	}

	@Override
	public int deleteGermplasmListDataByListId(final Integer listId) {

		int germplasmListDataDeleted = 0;
		try {
			germplasmListDataDeleted = this.getGermplasmListDataDAO().deleteByListId(listId);
			this.getTransactionDao().cancelUnconfirmedTransactionsForLists(Arrays.asList(new Integer[] {listId}));

		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
							+ listId + "): " + e.getMessage(), e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public int deleteGermplasmListDataByListIdEntryId(final Integer listId, final Integer entryId) {
		final GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndEntryId(listId, entryId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListDataByListIdLrecId(final Integer listId, final Integer lrecId) {
		final GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndLrecId(listId, lrecId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListData(final GermplasmListData germplasmListData) {
		final List<GermplasmListData> list = new ArrayList<GermplasmListData>();
		list.add(germplasmListData);
		return this.deleteGermplasmListData(list);
	}

	@Override
	public int deleteGermplasmListData(final List<GermplasmListData> germplasmListDatas) {

		int germplasmListDataDeleted = 0;
		try {
			// begin delete transaction

			final List<Integer> listEntryIds = new ArrayList<Integer>();
			for (final GermplasmListData germplasmListData : germplasmListDatas) {
				listEntryIds.add(germplasmListData.getId());
			}

			if (!listEntryIds.isEmpty()) {
				this.getTransactionDao().cancelUnconfirmedTransactionsForListEntries(listEntryIds);
			}

			for (final GermplasmListData germplasmListData : germplasmListDatas) {
				this.getGermplasmListDataDAO().makeTransient(germplasmListData);
				germplasmListDataDeleted++;
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
							+ germplasmListDatas + "): " + e.getMessage(), e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(final Integer parentId, final String programUUID, final int start,
			final int numOfRows) {

		return this.getGermplasmListDAO().getByParentFolderId(parentId, programUUID, start, numOfRows);
	}

	@Override
	public GermplasmList getLastSavedGermplasmListByUserId(final Integer userID, final String programUUID) {
		return this.getGermplasmListDAO().getLastCreatedByUserID(userID, programUUID);
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderIdBatched(final Integer parentId, final String programUUID, final int batchSize) {
		final List<GermplasmList> childLists = new ArrayList<GermplasmList>();
		int start = 0;
		final long childListCount = this.getGermplasmListDAO().countByParentFolderId(parentId, programUUID);
		while (start < childListCount) {
			childLists.addAll(this.getGermplasmListDAO().getByParentFolderId(parentId, programUUID, start, batchSize));
			start += batchSize;
		}
		return childLists;
	}

	@Override
	public long countGermplasmListByParentFolderId(final Integer parentId, final String programUUID) {
		return this.getGermplasmListDAO().countByParentFolderId(parentId, programUUID);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmListTypes() {
		final List<UserDefinedField> toReturn = new ArrayList<UserDefinedField>();

		final List results = this.getGermplasmListDAO().getGermplasmListTypes();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				Integer fldno = (Integer) result[0];
				String ftable = (String) result[1];
				String ftype = (String) result[2];
				String fcode = (String) result[3];
				String fname = (String) result[4];
				String ffmt = (String) result[5];
				String fdesc = (String) result[6];
				Integer lfldno = (Integer) result[7];
				Integer fuid = (Integer) result[8];
				Integer fdate = (Integer) result[9];
				Integer scaleid = (Integer) result[10];

				UserDefinedField userDefinedField =
						new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmNameTypes() {
		final List<UserDefinedField> toReturn = new ArrayList<UserDefinedField>();

		final List results =
				this.getFromInstanceByMethod(this.getGermplasmListDAO(), Database.LOCAL, "getGermplasmNameTypes", new Object[] {},
						new Class[] {});

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				Integer fldno = (Integer) result[0];
				String ftable = (String) result[1];
				String ftype = (String) result[2];
				String fcode = (String) result[3];
				String fname = (String) result[4];
				String ffmt = (String) result[5];
				String fdesc = (String) result[6];
				Integer lfldno = (Integer) result[7];
				Integer fuid = (Integer) result[8];
				Integer fdate = (Integer) result[9];
				Integer scaleid = (Integer) result[10];

				UserDefinedField userDefinedField =
						new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}
	
	@Override
	public Map<Integer, GermplasmListMetadata> getAllGermplasmListMetadata() {
		Map<Integer, GermplasmListMetadata> listMetadata = new HashMap<>();

		List<Object[]> queryResults = this.getGermplasmListDAO().getAllListMetadata();

		for (Object[] row : queryResults) {
			Integer listId = (Integer) row[0];
			Integer entryCount = (Integer) row[1];
			String ownerUser = (String) row[2];
			String ownerFirstName = (String) row[3];
			String ownerLastName = (String) row[4];

			String owner = "";
			if (StringUtils.isNotBlank(ownerFirstName) && StringUtils.isNotBlank(ownerLastName)) {
				owner = ownerFirstName + " " + ownerLastName;
			} else {
				owner = Strings.nullToEmpty(ownerUser);
			}

			listMetadata.put(listId, new GermplasmListMetadata(listId, entryCount, owner));
		}

		return listMetadata;
	}

	@Override
	public List<GermplasmList> searchForGermplasmList(String q, Operation o) throws MiddlewareQueryException {
		List<GermplasmList> results = new ArrayList<GermplasmList>();
		results.addAll(this.getGermplasmListDAO().searchForGermplasmLists(q, o));
		return results;
	}

	@Override
	public List<GermplasmList> searchForGermplasmList(final String q, final String programUUID, final Operation o) {
		final List<GermplasmList> results = new ArrayList<GermplasmList>();
		results.addAll(this.getGermplasmListDAO().searchForGermplasmLists(q, programUUID, o));
		return results;
	}

	@Override
	public List<ListDataInfo> saveListDataColumns(final List<ListDataInfo> listDataCollection) {
		return this.getListDataPropertySaver().saveProperties(listDataCollection);
	}

	@Override
	public GermplasmListNewColumnsInfo getAdditionalColumnsForList(final Integer listId) {
		return this.getListDataPropertyDAO().getPropertiesForList(listId);
	}

	@Override
	public List<ListDataProperty> saveListDataProperties(final List<ListDataProperty> listDataProps) {
		return this.getListDataPropertySaver().saveListDataProperties(listDataProps);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListData(final Integer listID) {
		return this.getListDataProjectDAO().getByListId(listID);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListDataWithParents(final Integer listID) {
		return this.getListDataProjectDAO().getListDataProjectWithParents(listID);
	}

	@Override
	public Integer retrieveDataListIDFromListDataProjectListID(final Integer listDataProjectListID) {
		return this.getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listDataProjectListID);
	}

	@Override
	public GermplasmList getGermplasmListByListRef(final Integer listRef) {
		return this.getGermplasmListDAO().getByListRef(listRef);
	}

}
