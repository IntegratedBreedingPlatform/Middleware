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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.operation.saver.ListDataProjectSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListMetadata;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the GermplasmListManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@SuppressWarnings("unchecked")
@Transactional
public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager {

	public static final int MAX_CROSS_NAME_SIZE = 240;
	public static final String TRUNCATED = "(truncated)";

	private DaoFactory daoFactory;

	@Resource
	private UserService userService;

	@Resource
	private ListDataProjectSaver ListDataProjectSaver;

	/**
	 * Caches the udflds table. udflds should be small so this cache should be fine in terms of size. The string is the database url. So the
	 * cache is per database url.
	 */
	private static final Cache<String, List<UserDefinedField>> germplasmListTypeCache =
		CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(10, TimeUnit.MINUTES).build();

	/**
	 * Function that loads the germplasmListTypeCache. Note this cannot be static.
	 **/
	private FunctionBasedGuavaCacheLoader<String, List<UserDefinedField>> functionBasedGermplasmListTypeGuavaCacheLoader;

	public GermplasmListManagerImpl() {
		this.bindCacheLoaderFunctionToCache();
	}

	private void bindCacheLoaderFunctionToCache() {
		this.functionBasedGermplasmListTypeGuavaCacheLoader =
			new FunctionBasedGuavaCacheLoader<>(
				germplasmListTypeCache, new Function<String, List<UserDefinedField>>() {

				@Override
				public List<UserDefinedField> apply(final String key) {
					return GermplasmListManagerImpl.this.getGermpasmListTypesFromDb();
				}
			});
	}

	public GermplasmListManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
		this.bindCacheLoaderFunctionToCache();

	}

	public GermplasmListManagerImpl(final HibernateSessionProvider sessionProvider, final String databaseName) {
		super(sessionProvider, databaseName);
		this.daoFactory = new DaoFactory(sessionProvider);
		this.bindCacheLoaderFunctionToCache();

	}

	@Override
	public GermplasmList getGermplasmListById(final Integer id) {
		return this.daoFactory.getGermplasmListDAO().getById(id, false);
	}

	@Override
	public List<GermplasmList> getAllGermplasmLists(final int start, final int numOfRows) {
		return this.daoFactory.getGermplasmListDAO().getAllExceptDeleted(start, numOfRows);
	}

	@Override
	public long countAllGermplasmLists() {
		return this.daoFactory.getGermplasmListDAO().countAllExceptDeleted();
	}

	@Override
	public List<GermplasmList> getGermplasmListByName(
		final String name, final String programUUID, final int start, final int numOfRows,
		final Operation operation) {
		return this.daoFactory.getGermplasmListDAO().getByName(name, programUUID, operation, start, numOfRows);
	}

	@Override
	public long countGermplasmListByName(final String name, final Operation operation) {
		return this.daoFactory.getGermplasmListDAO().countByName(name, operation);
	}

	@Override
	public long countGermplasmListByStatus(final Integer status) {
		return this.daoFactory.getGermplasmListDAO().countByStatus(status);
	}

	@Override
	public List<GermplasmList> getGermplasmListByGID(final Integer gid, final int start, final int numOfRows) {
		return this.daoFactory.getGermplasmListDAO().getByGID(gid, start, numOfRows);
	}

	@Override
	public long countGermplasmListByGID(final Integer gid) {

		return this.daoFactory.getGermplasmListDAO().countByGID(gid);
	}

	@Override
	public long countGermplasmListByGIDandProgramUUID(final Integer gid, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().countByGIDandProgramUUID(gid, programUUID);
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListId(final Integer id) {
		return this.daoFactory.getGermplasmListDataDAO().getByListId(id);
	}

	@Override
	public long countGermplasmListDataByListId(final Integer id) {
		return this.daoFactory.getGermplasmListDataDAO().countByListId(id);
	}

	@Override
	public long countListDataProjectGermplasmListDataByListId(final Integer id) {
		return this.getListDataProjectDAO().countByListId(id);
	}

	@Override
	public List<GermplasmListData> getGermplasmListDataByListIdAndGID(final Integer listId, final Integer gid) {
		return Arrays.asList(this.daoFactory.getGermplasmListDataDAO().getByListIdAndGid(listId, gid));
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndEntryId(final Integer listId, final Integer entryId) {
		return this.daoFactory.getGermplasmListDataDAO().getByListIdAndEntryId(listId, entryId);
	}

	@Override
	public GermplasmListData getGermplasmListDataByListIdAndLrecId(final Integer listId, final Integer lrecId) {
		return this.daoFactory.getGermplasmListDataDAO().getByListIdAndLrecId(listId, lrecId);
	}

	@Override
	public List<GermplasmList> getAllTopLevelLists(final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getAllTopLevelLists(programUUID);
	}

	@Override
	public Integer addGermplasmList(final GermplasmList germplasmList) {
		final List<GermplasmList> list = new ArrayList<>();
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
		final List<GermplasmList> list = new ArrayList<>();
		list.add(germplasmList);
		final List<Integer> idList = this.updateGermplasmList(list);
		return !idList.isEmpty() ? idList.get(0) : null;
	}

	@Override
	public List<Integer> updateGermplasmList(final List<GermplasmList> germplasmLists) {
		return this.addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmList(final List<GermplasmList> germplasmLists, final Operation operation) {

		final List<Integer> germplasmListIds = new ArrayList<>();
		try {

			for (GermplasmList germplasmList : germplasmLists) {
				if (operation == Operation.ADD) {
					germplasmList = this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
					germplasmListIds.add(germplasmList.getId());
				} else if (operation == Operation.UPDATE) {
					germplasmListIds.add(germplasmList.getId());
					this.daoFactory.getGermplasmListDAO().merge(germplasmList);
				}
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm List: GermplasmListManager.addOrUpdateGermplasmList(germplasmLists="
					+ germplasmLists + ", operation-" + operation + "): " + e.getMessage(),
				e);
		}

		return germplasmListIds;
	}

	@Override
	public int deleteGermplasmListByListId(final Integer listId) {
		final GermplasmList germplasmList = this.getGermplasmListById(listId);
		return this.deleteGermplasmList(germplasmList);
	}

	@Override
	public int deleteGermplasmListByListIdPhysically(final Integer listId) {
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		this.deleteGermplasmListDataByListId(listId);
		this.daoFactory.getGermplasmListDAO().deleteGermplasmListByListIdPhysically(listId);
		return listId;
	}

	@Override
	public int deleteGermplasmList(final GermplasmList germplasmList) {
		final List<GermplasmList> list = new ArrayList<>();
		list.add(germplasmList);
		return this.deleteGermplasmList(list);
	}

	@Override
	public int deleteGermplasmListsByProgram(final String programUUID) {
		final List<GermplasmList> lists = this.daoFactory.getGermplasmListDAO().getListsByProgram(programUUID);
		return this.deleteGermplasmList(lists);
	}

	@Override
	public int deleteGermplasmList(final List<GermplasmList> germplasmLists) {
		int germplasmListsDeleted = 0;
		try {

			for (final GermplasmList germplasmList : germplasmLists) {

				germplasmList.setStatus(9);
				this.updateGermplasmList(germplasmList);

				germplasmListsDeleted++;
			}
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while deleting Germplasm List: GermplasmListManager.deleteGermplasmList(germplasmLists="
					+ germplasmLists + "): " + e.getMessage(),
				e);
		}
		return germplasmListsDeleted;
	}

	@Override
	public Integer addGermplasmListData(final GermplasmListData germplasmListData) {
		final List<GermplasmListData> list = new ArrayList<>();
		list.add(germplasmListData);
		final List<Integer> ids = this.addGermplasmListData(list);
		return !ids.isEmpty() ? ids.get(0) : null;
	}

	@Override
	public List<Integer> addGermplasmListData(final List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
	}

	@Override
	public List<Integer> updateGermplasmListData(final List<GermplasmListData> germplasmListDatas) {
		return this.addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
	}

	private List<Integer> addOrUpdateGermplasmListData(final List<GermplasmListData> germplasmListDatas, final Operation operation) {

		final List<Integer> idGermplasmListDataSaved = new ArrayList<>();
		try {
			final GermplasmListDataDAO dao = new GermplasmListDataDAO();
			dao.setSession(this.getActiveSession());

			final List<Integer> deletedListEntryIds = new ArrayList<>();

			for (final GermplasmListData germplasmListData : germplasmListDatas) {

				String groupName = germplasmListData.getGroupName();
				if (groupName.length() > MAX_CROSS_NAME_SIZE) {
					groupName = groupName.substring(0, MAX_CROSS_NAME_SIZE - 1);
					groupName = groupName + TRUNCATED;
					germplasmListData.setGroupName(groupName);
				}

				final GermplasmListData recordSaved = this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);
				idGermplasmListDataSaved.add(recordSaved.getId());
				if (germplasmListData.getStatus() != null && germplasmListData.getStatus().intValue() == 9) {
					deletedListEntryIds.add(germplasmListData.getId());
				}
			}

			if (!deletedListEntryIds.isEmpty()) {
				this.daoFactory.getTransactionDAO().cancelUnconfirmedTransactionsForListEntries(deletedListEntryIds);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Germplasm List Data: GermplasmListManager.addOrUpdateGermplasmListData(germplasmListDatas="
					+ germplasmListDatas + ", operation=" + operation + "): " + e.getMessage(),
				e);
		}

		return idGermplasmListDataSaved;
	}

	@Override
	public int deleteGermplasmListDataByListId(final Integer listId) {

		int germplasmListDataDeleted = 0;
		try {
			germplasmListDataDeleted = this.daoFactory.getGermplasmListDataDAO().deleteByListId(listId);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
					+ listId + "): " + e.getMessage(),
				e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public int deleteGermplasmListDataByListIdLrecId(final Integer listId, final Integer lrecId) {
		final GermplasmListData germplasmListData = this.getGermplasmListDataByListIdAndLrecId(listId, lrecId);
		return this.deleteGermplasmListData(germplasmListData);
	}

	@Override
	public int deleteGermplasmListData(final GermplasmListData germplasmListData) {
		final List<GermplasmListData> list = new ArrayList<>();
		list.add(germplasmListData);
		return this.deleteGermplasmListData(list);
	}

	@Override
	public int deleteGermplasmListData(final List<GermplasmListData> germplasmListDatas) {

		int germplasmListDataDeleted = 0;
		try {

			for (final GermplasmListData germplasmListData : germplasmListDatas) {
				this.daoFactory.getGermplasmListDataDAO().makeTransient(germplasmListData);
				germplasmListDataDeleted++;
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListData(germplasmListDatas="
					+ germplasmListDatas + "): " + e.getMessage(),
				e);
		}

		return germplasmListDataDeleted;
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(final Integer parentId, final String programUUID) {

		return this.daoFactory.getGermplasmListDAO().getByParentFolderId(parentId, programUUID);
	}

	@Override
	public GermplasmList getLastSavedGermplasmListByUserId(final Integer userID, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getLastCreatedByUserID(userID, programUUID);
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderIdBatched(
		final Integer parentId, final String programUUID,
		final int batchSize) {
		return this.daoFactory.getGermplasmListDAO().getByParentFolderId(parentId, programUUID);
	}

	@SuppressWarnings({"rawtypes", "deprecation"})
	@Override
	public List<UserDefinedField> getGermplasmListTypes() {
		try {
			// Get the database url. This is how we will cache the UDFLDS across crops.
			final String url = this.getActiveSession().connection().getMetaData().getURL();
			return this.functionBasedGermplasmListTypeGuavaCacheLoader.get(url).get();

		} catch (final HibernateException | SQLException e) {
			throw new MiddlewareQueryException("Problems connecting to the database. P"
				+ "lease contact administrator for assistance.", e);
		}
	}

	private List<UserDefinedField> getGermpasmListTypesFromDb() {
		final List<UserDefinedField> toReturn = new ArrayList<>();

		final List results = this.daoFactory.getGermplasmListDAO().getGermplasmListTypes();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final Integer fldno = (Integer) result[0];
				final String ftable = (String) result[1];
				final String ftype = (String) result[2];
				final String fcode = (String) result[3];
				final String fname = (String) result[4];
				final String ffmt = (String) result[5];
				final String fdesc = (String) result[6];
				final Integer lfldno = (Integer) result[7];
				final Integer fuid = (Integer) result[8];
				final Integer fdate = (Integer) result[9];
				final Integer scaleid = (Integer) result[10];

				final UserDefinedField userDefinedField =
					new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserDefinedField> getGermplasmNameTypes() {

		final List<UserDefinedField> toReturn = new ArrayList<>();

		final List results = this.daoFactory.getGermplasmListDAO().getGermplasmNameTypes();

		for (final Object o : results) {
			final Object[] result = (Object[]) o;
			if (result != null) {
				final Integer fldno = (Integer) result[0];
				final String ftable = (String) result[1];
				final String ftype = (String) result[2];
				final String fcode = (String) result[3];
				final String fname = (String) result[4];
				final String ffmt = (String) result[5];
				final String fdesc = (String) result[6];
				final Integer lfldno = (Integer) result[7];
				final Integer fuid = (Integer) result[8];
				final Integer fdate = (Integer) result[9];
				final Integer scaleid = (Integer) result[10];

				final UserDefinedField userDefinedField =
					new UserDefinedField(fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid);
				toReturn.add(userDefinedField);
			}
		}
		return toReturn;
	}

	@Override
	public Map<Integer, GermplasmListMetadata> getGermplasmListMetadata(final List<GermplasmList> listIds) {
		final List<Integer> listIdsFromGermplasmList = this.getListIdsFromGermplasmList(listIds);
		return this.getGermpasmListMetadata(listIdsFromGermplasmList);
	}

	private Map<Integer, GermplasmListMetadata> getGermpasmListMetadata(final List<Integer> listIdsFromGermplasmList) {
		final Map<Integer, GermplasmListMetadata> listMetadata = new HashMap<>();

		final List<Object[]> queryResults = this.daoFactory.getGermplasmListDAO().getAllListMetadata(listIdsFromGermplasmList);
		final List<Integer> userIds = new ArrayList<>();
		for (final Object[] row : queryResults) {
			final Integer listId = (Integer) row[0];
			final Integer entryCount = (Integer) row[1];
			final Integer ownerId = (Integer) row[2];
			userIds.add(ownerId);
			listMetadata.put(listId, new GermplasmListMetadata(listId, entryCount, ownerId));
		}
		// Retrieve list owner names from workbench db
		if (!userIds.isEmpty()) {
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			for (final GermplasmListMetadata data : listMetadata.values()) {
				if (data.getOwnerId() != null) {
					data.setOwnerName(userIDFullNameMap.get(data.getOwnerId()));
				}
			}
		}
		return listMetadata;
	}

	private List<Integer> getListIdsFromGermplasmList(final List<GermplasmList> germplasmListParent) {
		final List<Integer> listIdsToRetrieveCount = new ArrayList<>();
		for (final GermplasmList parentList : germplasmListParent) {
			if (!parentList.isFolder()) {
				listIdsToRetrieveCount.add(parentList.getId());
			}
		}
		return listIdsToRetrieveCount;
	}

	@Override
	public List<GermplasmList> searchForGermplasmList(final String q, final Operation o) {
		return this.searchForGermplasmList(q, null, o);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public List<GermplasmList> searchForGermplasmList(final String q, final String programUUID, final Operation o) {
		final List<GermplasmList> results = new ArrayList<>();
		results.addAll(this.daoFactory.getGermplasmListDAO().searchForGermplasmLists(q, programUUID, o));
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
	public List<ListDataProject> retrieveSnapshotListData(final Integer listID) {
		return this.getListDataProjectDAO().getByListId(listID);
	}

	@Override
	public List<ListDataProject> retrieveSnapshotListDataWithParents(final Integer listID) {
		final List<ListDataProject> dataList = this.getListDataProjectDAO().getListDataProjectWithParents(listID);
		final Iterable<Integer> gidList = Iterables.transform(dataList, new Function<ListDataProject, Integer>() {

			public Integer apply(final ListDataProject data) {
				return data.getGermplasmId();
			}

			;
		});
		// Append to maleParents of ListDataProject other progenitors of GIDs from the list, if any
		final Map<Integer, List<GermplasmParent>> progenitorsMap =
			this.daoFactory.getGermplasmDao().getParentsFromProgenitorsForGIDsMap(Lists.newArrayList(gidList));
		for (final ListDataProject data : dataList) {
			final List<GermplasmParent> progenitors = progenitorsMap.get(data.getGermplasmId());
			if (progenitors != null) {
				data.addMaleParents(progenitors);
			}
		}
		return dataList;
	}

	@Override
	public List<GermplasmListData> retrieveGermplasmListDataWithParents(final Integer listID) {
		// Retrieve each cross with gpid1 and gpid2 parents info
		final List<GermplasmListData> dataList =
			this.daoFactory.getGermplasmListDataDAO().retrieveGermplasmListDataWithImmediateParents(listID);
		final Iterable<Integer> gidList = Iterables.transform(dataList, new Function<GermplasmListData, Integer>() {

			public Integer apply(final GermplasmListData data) {
				return data.getGid();
			}

			;
		});
		// Append to maleParents of CrossListData other progenitors of GIDs from the list, if any
		final Map<Integer, List<GermplasmParent>> progenitorsMap =
			this.daoFactory.getGermplasmDao().getParentsFromProgenitorsForGIDsMap(Lists.newArrayList(gidList));
		for (final GermplasmListData data : dataList) {
			final List<GermplasmParent> progenitors = progenitorsMap.get(data.getGid());
			if (progenitors != null) {
				data.addMaleParents(progenitors);
			}
		}
		return dataList;
	}

	@Override
	public Integer retrieveDataListIDFromListDataProjectListID(final Integer listDataProjectListID) {
		return this.daoFactory.getGermplasmListDAO().getListDataListIDFromListDataProjectListID(listDataProjectListID);
	}

	@Override
	public List<GermplasmList> getGermplasmListByGIDandProgramUUID(
		final Integer gid, final int start, final int numOfRows,
		final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getByGIDandProgramUUID(gid, start, numOfRows, programUUID);
	}

	@Override
	public List<GermplasmList> getAllGermplasmListsByProgramUUID(final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getListsByProgramUUID(programUUID);
	}

	@Override
	public List<GermplasmList> getAllGermplasmListsByIds(final List<Integer> listIds) {
		return this.daoFactory.getGermplasmListDAO().getAllGermplasmListsById(listIds);
	}

	@Override
	public Map<Integer, ListMetadata> getGermplasmFolderMetadata(final List<GermplasmList> germplasmLists) {
		final List<Integer> folderIdsToRetrieveFolderCount = this.getFolderIdsFromGermplasmList(germplasmLists);
		return this.daoFactory.getGermplasmListDAO().getGermplasmFolderMetadata(folderIdsToRetrieveFolderCount);
	}

	private List<Integer> getFolderIdsFromGermplasmList(final List<GermplasmList> listIds) {
		final List<Integer> folderIdsToRetrieveFolderCount = new ArrayList<>();
		for (final GermplasmList parentList : listIds) {
			if (parentList.isFolder()) {
				folderIdsToRetrieveFolderCount.add(parentList.getId());
			}
		}
		return folderIdsToRetrieveFolderCount;
	}

	private void performGermplasmListEntriesDeletion(final List<Integer> germplasms, final Integer listId) {
		for (final Integer gid : germplasms) {
			final GermplasmListData germplasmListData = this.daoFactory.getGermplasmListDataDAO().getByListIdAndGid(listId, gid);
			this.deleteGermplasmListData(germplasmListData);
		}

		// Change entry IDs on listData
		final List<GermplasmListData> listDatas = this.getGermplasmListDataByListId(listId);
		Integer entryId = 1;
		for (final GermplasmListData listData : listDatas) {
			listData.setEntryId(entryId);
			entryId++;
		}
		this.updateGermplasmListData(listDatas);
	}

	private void performListDataProjectEntriesDeletion(final List<Integer> germplasms, final Integer listId) {
		final List<GermplasmList> germplasmLists = this.daoFactory.getGermplasmListDAO().getByListRef(listId);
		for (final GermplasmList germplasmList : germplasmLists) {
			this.ListDataProjectSaver.performListDataProjectEntriesDeletion(germplasms, germplasmList.getId());
		}
	}

	@Override
	public List<Integer> deleteGermplasms(final List<Integer> germplasms, final Integer listId) {

		final List<Integer> notDeletableGermplasmList = this.validateGermplasmForDeletion(germplasms);

		final List<Integer> gidsDelete = new ArrayList<>(CollectionUtils.disjunction(germplasms, notDeletableGermplasmList));

		if (!gidsDelete.isEmpty()) {
			final GermplasmDAO dao = this.getGermplasmDao();
			dao.deleteGermplasms(gidsDelete);

			this.performGermplasmListEntriesDeletion(gidsDelete, listId);
			this.performListDataProjectEntriesDeletion(gidsDelete, listId);
		}

		return gidsDelete;
	}

	private List<Integer> validateGermplasmForDeletion(final List<Integer> germplasms) {

		final Set<Integer> codeFixedGermplasms = this.getCodeFixedGidsByGidList(germplasms);
		final Set<Integer> germplasmOffspringByGIDs = this.getGermplasmOffspringByGIDs(germplasms);
		final Set<Integer> germplasmsUsedInEntryList = this.getGermplasmUsedInEntryList(germplasms);
		final Set<Integer> germplasmsWithOpenLots = this.getGidsWithOpenLots(germplasms);
		final Set<Integer> germplasmsInOneOrMoreLists = this.getGermplasmUsedInMoreThanOneList(germplasms);

		final Set<Integer> all = new HashSet<>();

		all.addAll(codeFixedGermplasms);
		all.addAll(germplasmOffspringByGIDs);
		all.addAll(germplasmsUsedInEntryList);
		all.addAll(germplasmsWithOpenLots);
		all.addAll(germplasmsInOneOrMoreLists);

		return Lists.newArrayList(all);
	}

	protected Set<Integer> getCodeFixedGidsByGidList(final List<Integer> gids) {
		try {
			final Set<Integer> set = new HashSet<>();
			final GermplasmDAO dao = this.getGermplasmDao();
			final List<Germplasm> germplasms = dao.getByGIDList(gids);
			for (final Germplasm germplasm : germplasms) {
				if (germplasm.getMgid() > 0) {
					set.add(germplasm.getGid());
				}
			}
			return set;
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while getting code fixed status: GermplasmDataManager.getCodeFixedStatusByGidList(gids=" + gids
					+ "): " + e.getMessage(),
				e);
		}
	}

	private Set<Integer> getGidsWithOpenLots(final List<Integer> gids) {
		try {
			final LotDAO dao = this.daoFactory.getLotDao();
			return dao.getGermplasmsWithOpenLots(gids);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while getting gids with open lots: GermplasmDataManager.getGidsWithOpenLots(gids=" + gids + "): "
					+ e.getMessage(),
				e);
		}
	}

	private Set<Integer> getGermplasmOffspringByGIDs(final List<Integer> gids) {
		try {
			return this.getGermplasmDao().getGermplasmOffspringByGIDs(gids).keySet();
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while getting gids thart belongs to more than one list: GermplasmDataManager.getGermplasmUsedInMoreThanOneList(gids="
					+ gids + "): " + e.getMessage(),
				e);
		}
	}

	private Set<Integer> getGermplasmUsedInMoreThanOneList(final List<Integer> gids) {
		try {
			final GermplasmListDAO dao = this.daoFactory.getGermplasmListDAO();
			return dao.getGermplasmUsedInMoreThanOneList(gids).keySet();
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while getting gids thart belongs to more than one list: GermplasmDataManager.getGermplasmUsedInMoreThanOneList(gids="
					+ gids + "): " + e.getMessage(),
				e);
		}
	}

	private Set<Integer> getGermplasmUsedInEntryList(final List<Integer> gids) {
		try {
			final ListDataProjectDAO dao = this.getListDataProjectDAO();
			return dao.getGermplasmUsedInEntryList(gids).keySet();
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while getting gids that are being used in an Entry List: GermplasmDataManager.getGermplasmUsedInEntryList(gids="
					+ gids + "): " + e.getMessage(),
				e);
		}
	}

}
