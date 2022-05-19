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
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the GermplasmListManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@SuppressWarnings("unchecked")
@Transactional
public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager {
	private static final int MAX_CROSS_NAME_SIZE = 4985;
	private static final String TRUNCATED = "(truncated)";

	private DaoFactory daoFactory;

	@Resource
	private UserService userService;

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
	public List<GermplasmList> getGermplasmListByGID(final Integer gid, final int start, final int numOfRows) {
		return this.daoFactory.getGermplasmListDAO().getByGID(gid, start, numOfRows);
	}

	@Override
	public long countGermplasmListByGID(final Integer gid) {

		return this.daoFactory.getGermplasmListDAO().countByGID(gid);
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
	public List<GermplasmListData> getGermplasmListDataByListIdAndGID(final Integer listId, final Integer gid) {
		return Arrays.asList(this.daoFactory.getGermplasmListDataDAO().getByListIdAndGid(listId, gid));
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
				if (germplasmListData.getStatus() != null && germplasmListData.getStatus() == 9) {
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
		try {
			return this.daoFactory.getGermplasmListDataDAO().deleteByListId(listId);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while deleting Germplasm List Data: GermplasmListManager.deleteGermplasmListDataByListId(listId="
					+ listId + "): " + e.getMessage(),
				e);
		}
	}

	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(final Integer parentId, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getByParentFolderId(parentId, programUUID);
	}

	/**
	 * Returns a list of {@code GermplasmList} child records given a parent id.
	 *
	 * @param parentId - the ID of the parent to retrieve the child lists
	 * @return Returns a List of GermplasmList POJOs for the child lists
	 */
	@Override
	public List<GermplasmList> getGermplasmListByParentFolderId(final Integer parentId) {
		return this.daoFactory.getGermplasmListDAO().getByParentFolderId(parentId);
	}

	@Override
	public GermplasmList getLastSavedGermplasmListByUserId(final Integer userID, final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getLastCreatedByUserID(userID, programUUID);
	}

	@SuppressWarnings({"rawtypes", "deprecation"})
	@Override
	public List<UserDefinedField> getGermplasmListTypes() {
		try {
			// Get the database url. This is how we will cache the UDFLDS across crops.
			final String url = this.getActiveSession().doReturningWork(connection -> connection.getMetaData().getURL());

			return this.functionBasedGermplasmListTypeGuavaCacheLoader.get(url).get();

		} catch (final HibernateException e) {
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
	public Map<Integer, ListMetadata> getGermplasmListMetadata(final List<GermplasmList> listIds) {
		final List<Integer> listIdsFromGermplasmList = this.getListIdsFromGermplasmList(listIds);
		return this.daoFactory.getGermplasmListDAO().getGermplasmListMetadata(listIdsFromGermplasmList);
	}

	private List<Integer> getListIdsFromGermplasmList(final List<GermplasmList> germplasmListParent) {
		final List<Integer> listIdsToRetrieveCount = new ArrayList<>();
		for (final GermplasmList parentList : germplasmListParent) {
			listIdsToRetrieveCount.add(parentList.getId());
		}
		return listIdsToRetrieveCount;
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
	public List<GermplasmList> getAllGermplasmListsByProgramUUID(final String programUUID) {
		return this.daoFactory.getGermplasmListDAO().getListsByProgramUUID(programUUID);
	}

	@Override
	public List<GermplasmList> getAllGermplasmListsByIds(final List<Integer> listIds) {
		return this.daoFactory.getGermplasmListDAO().getAllGermplasmListsById(listIds);
	}

	@Override
	public void populateGermplasmListCreatedByName(final List<GermplasmList> germplasmLists) {
		final List<Integer> userIds = Lists.transform(germplasmLists, new Function<GermplasmList, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final GermplasmList input) {
				return input.getUserId();
			}
		});
		final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
		for (final GermplasmList germplasmList : germplasmLists) {
			germplasmList.setCreatedBy(userIDFullNameMap.get(germplasmList.getUserId()));
		}
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
