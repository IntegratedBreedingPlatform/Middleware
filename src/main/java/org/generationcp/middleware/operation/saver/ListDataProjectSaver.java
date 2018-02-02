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

	public static final int DEFAULT_ACTIVE = 1;
	private Saver daoFactory;

	public ListDataProjectSaver() {
		// does nothing
	}

	public ListDataProjectSaver(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new Saver(sessionProvider);
	}

	public ListDataProjectSaver(final Saver saver) {
		this.daoFactory = saver;
	}

	public int saveOrUpdateListDataProject(final int projectId, final GermplasmListType type, final Integer originalListId, final List<ListDataProject> listDatas,
			final int userId) {

		final boolean isAdvanced = type == GermplasmListType.ADVANCED || GermplasmListType.isCrosses(type);
		GermplasmList snapList = isAdvanced ? null : this.getGermplasmList(projectId, type);
		final boolean isCreate = snapList == null;

		if (isCreate) {
			snapList = this.createInitialGermplasmList(projectId, type);
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
			for (final ListDataProject listDataProject : listDatas) {
				this.prepareListDataProjectForSaving(listDataProject, snapList);
				this.daoFactory.getListDataProjectDAO().save(listDataProject);
			}
		}

		return snapList.getId();
	}

	protected GermplasmList createInitialGermplasmList(final int projectId, final GermplasmListType type) {

		final DmsProject project = this.daoFactory.getStudyDataManager().getProject(projectId);
		final GermplasmList snapList = new GermplasmList();
		snapList.setProjectId(projectId);
		snapList.setProgramUUID(project.getProgramUUID());
		snapList.setDate(Util.getCurrentDateAsLongValue());
		// Set the status to the default list status (1 - active)
		snapList.setStatus(DEFAULT_ACTIVE);
		snapList.setType(type.name());
		snapList.setParent(null);
		final Long dateLong = Long.valueOf(System.currentTimeMillis());
		final String name = type.name() + "-" + dateLong;
		snapList.setName(name);
		snapList.setDescription(name);

		return snapList;
	}

	protected void updateGermplasmListInfo(final GermplasmList germplasmList, final int originalListId, final int userId) {

		final GermplasmList originalGermplasmList = this.daoFactory.getGermplasmListDAO().getById(originalListId);

		if (originalGermplasmList != null) {
			germplasmList.setListLocation(originalGermplasmList.getListLocation());
			germplasmList.setUserId(originalGermplasmList.getUserId());
			germplasmList.setNotes(originalGermplasmList.getNotes());
			germplasmList.setsDate(originalGermplasmList.getsDate());
			germplasmList.seteDate(originalGermplasmList.geteDate());
			germplasmList.setName(originalGermplasmList.getName());
			germplasmList.setDescription(originalGermplasmList.getDescription());
			germplasmList.setListRef(originalListId);
			germplasmList.setProgramUUID(originalGermplasmList.getProgramUUID());
			germplasmList.setStatus(originalGermplasmList.getStatus());
		} else {
			this.setDefaultGermplasmListInfo(germplasmList, userId);
		}
	}

	public void updateGermlasmListInfoStudy(final int crossesListId, final int studyId) {
		final GermplasmList crossesList = this.daoFactory.getGermplasmListDAO().getById(crossesListId);
		if (crossesList != null) {
			crossesList.setProjectId(studyId);
		}
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(crossesList);
	}

	private void setDefaultGermplasmListInfo(final GermplasmList snapList, final int userId) {
		snapList.setListLocation(null);
		snapList.setUserId(userId);
		snapList.setNotes(null);
		snapList.setsDate(null);
		snapList.seteDate(null);
		snapList.setListRef(null);
	}

	private GermplasmList getGermplasmList(final int projectId, final GermplasmListType type) {
		GermplasmList gList = null;
		final List<GermplasmList> tempList = this.daoFactory.getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		if (tempList != null && !tempList.isEmpty()) {
			gList = tempList.get(0);
		}
		return gList;
	}

	private void prepareListDataProjectForSaving(final ListDataProject listDataProject, final GermplasmList snapList) {
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
		final ListDataProjectDAO listDataProjectDAO = this.daoFactory.getListDataProjectDAO();
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
			if (listDataProject != null) {
				this.daoFactory.getListDataProjectDAO().makeTransient(listDataProject);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
					"Error encountered while deleting List Data Project: ListDataProjectSaver.deleteListDataProject(listDataProject="
							+ listDataProject + "): " + e.getMessage(), e);
		}

	}

	private List<Integer> updateListDataProject(final List<ListDataProject> listDataProjects) {
		final List<Integer> idGermplasmListDataSaved = new ArrayList<>();
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
