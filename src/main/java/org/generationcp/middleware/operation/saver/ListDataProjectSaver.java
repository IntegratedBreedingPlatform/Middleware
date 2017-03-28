
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ListDataProjectSaver {

	private Saver daoFactory;

	public ListDataProjectSaver(HibernateSessionProvider sessionProviderForLocal) {
		this.daoFactory = new Saver(sessionProviderForLocal);
	}

	public ListDataProjectSaver(Saver daoFactory) {
		this.daoFactory = daoFactory;
	}

	public int saveOrUpdateListDataProject(int projectId, GermplasmListType type, Integer originalListId, List<ListDataProject> listDatas,
			int userId) throws MiddlewareQueryException {

		boolean isAdvanced = type == GermplasmListType.ADVANCED || GermplasmListType.isCrosses(type);
		GermplasmList snapList = isAdvanced ? null : this.getGermplasmList(projectId, type);
		boolean isCreate = snapList == null;

		if (isCreate) {
			snapList = this.createInitialGermplasmList(projectId, originalListId, type);
		}

		if (originalListId != null) {
			this.updateGermplasmListInfo(snapList, originalListId, userId);
		} else {
			this.setDefaultGermplasmListInfo(snapList, userId);
		}

		this.daoFactory.getGermplasmListDAO().saveOrUpdate(snapList);

		if (!isCreate && !isAdvanced) {
			// delete old list data projects
			this.daoFactory.getListDataProjectDAO().deleteByListId(snapList.getId());
		}

		if (listDatas != null) {
			for (ListDataProject listDataProject : listDatas) {
				this.prepareListDataProjectForSaving(listDataProject, snapList);
				this.daoFactory.getListDataProjectDAO().save(listDataProject);
			}
		}

		return snapList.getId();
	}

	private GermplasmList createInitialGermplasmList(int projectId, Integer originalListId, GermplasmListType type)
			throws MiddlewareQueryException {

		DmsProject project = this.daoFactory.getStudyDataManager().getProject(projectId);
		GermplasmList snapList = new GermplasmList();
		snapList.setProjectId(projectId);
		snapList.setProgramUUID(project.getProgramUUID());
		snapList.setDate(Util.getCurrentDateAsLongValue());
		snapList.setStatus(1);
		snapList.setType(type.name());
		snapList.setParent(null);
		Long dateLong = Long.valueOf(System.currentTimeMillis());
		String name = type.name() + "-" + dateLong;
		snapList.setName(name);
		snapList.setDescription(name);

		return snapList;
	}

	private void updateGermplasmListInfo(GermplasmList germplasmList, int originalListId, int userId) throws MiddlewareQueryException {
		GermplasmList origList = this.daoFactory.getGermplasmListDAO().getById(originalListId);
		if (origList != null) {
			germplasmList.setListLocation(origList.getListLocation());
			germplasmList.setUserId(origList.getUserId());
			germplasmList.setNotes(origList.getNotes());
			germplasmList.setsDate(origList.getsDate());
			germplasmList.seteDate(origList.geteDate());
			germplasmList.setName(origList.getName());
			germplasmList.setUserId(origList.getUserId());
			germplasmList.setDescription(origList.getDescription());
			germplasmList.setListRef(originalListId);
		} else {
			this.setDefaultGermplasmListInfo(germplasmList, userId);
		}
	}

	public void updateGermlasmListInfoStudy(int crossesListId, int studyId) throws MiddlewareQueryException {
		GermplasmList crossesList = this.daoFactory.getGermplasmListDAO().getById(crossesListId);
		if (crossesList != null) {
			crossesList.setProjectId(studyId);
		}
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(crossesList);
	}

	private void setDefaultGermplasmListInfo(GermplasmList snapList, int userId) {
		snapList.setListLocation(null);
		snapList.setUserId(userId);
		snapList.setNotes(null);
		snapList.setsDate(null);
		snapList.seteDate(null);
		snapList.setListRef(null);
	}

	private GermplasmList getGermplasmList(int projectId, GermplasmListType type) throws MiddlewareQueryException {
		GermplasmList gList = null;
		List<GermplasmList> tempList = this.daoFactory.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		if (tempList != null && !tempList.isEmpty()) {
			gList = tempList.get(0);
		}
		return gList;
	}

	private void prepareListDataProjectForSaving(ListDataProject listDataProject, GermplasmList snapList) throws MiddlewareQueryException {
		listDataProject.setList(snapList);
		if (listDataProject.getCheckType() == null) {
			listDataProject.setCheckType(0);
		}
		if (listDataProject.getEntryId() == null) {
			listDataProject.setEntryId(0);
		}
		if (listDataProject.getEntryCode() == null) {
			listDataProject.setEntryCode("-");
		}
		if (listDataProject.getSeedSource() == null) {
			listDataProject.setSeedSource("-");
		}
		if (listDataProject.getDesignation() == null) {
			listDataProject.setDesignation("-");
		}
		if (listDataProject.getGroupName() == null) {
			listDataProject.setGroupName("-");
		}
	}

	public void performListDataProjectEntriesDeletion(final List<Integer> germplasms, final Integer listId) {
		ListDataProjectDAO listDataProjectDAO = this.daoFactory.getListDataProjectDAO();
		for (final Integer gid : germplasms) {
			final ListDataProject listDataProject = listDataProjectDAO.getByListIdAndGid(listId, gid);
			this.deleteListDataProject(listDataProject);
		}

		// Change entry IDs on listData
		final List<ListDataProject> listDatas = listDataProjectDAO.getByListId(listId);
		Integer entryId = 1;
		for (final ListDataProject listData : listDatas) {
			listData.setEntryId(entryId);
			entryId++;
		}
		this.updateListDataProject(listDatas);
	}

	private void deleteListDataProject(final ListDataProject listDataProject) {
		try {
			this.daoFactory.getListDataProjectDAO().makeTransient(listDataProject);
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting List Data Project: ListDataProjectSaver.deleteListDataProject(listDataProject="
					+ listDataProject + "): " + e.getMessage(), e);
		}

	}

	private List<Integer> updateListDataProject(final List<ListDataProject> listDataProjects) {
		final List<Integer> idGermplasmListDataSaved = new ArrayList<Integer>();
		try {

			for (final ListDataProject germplasmListData : listDataProjects) {

				final ListDataProject recordSaved = this.daoFactory.getListDataProjectDAO().saveOrUpdate(germplasmListData);
				idGermplasmListDataSaved.add(recordSaved.getListDataProjectId());

			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving List Data Project: ListDataProjectSaver.updateListDataProject(listDataProjects="
					+ listDataProjects + "): " + e.getMessage(), e);
		}

		return idGermplasmListDataSaved;
	}

}
