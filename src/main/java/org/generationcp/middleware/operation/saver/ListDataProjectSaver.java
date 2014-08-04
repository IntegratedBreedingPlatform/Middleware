package org.generationcp.middleware.operation.saver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;

public class ListDataProjectSaver extends Saver {

	public ListDataProjectSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public int saveOrUpdateListDataProject(int projectId,
			GermplasmListType type, Integer originalListId,
			List<ListDataProject> listDatas) throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();

		boolean isAdvanced = type == GermplasmListType.ADVANCED; 
		GermplasmList snapList = isAdvanced ? null : getGermplasmList(projectId, type);
		boolean isCreate = snapList == null;
		
		if (isCreate) {
			snapList = createInitialGermplasmList(projectId, originalListId, type);
		}
		
		if (originalListId != null) {
			updateGermplasmListInfo(snapList, originalListId);
		}
		else {
			setDefaultGermplasmListInfo(snapList);
		}
		
		getGermplasmListDAO().saveOrUpdate(snapList);
		
		if (!isCreate && !isAdvanced) {  //delete old list data projects
			getListDataProjectDAO().deleteByListId(snapList.getId());
		}
		
		if (listDatas != null) {
			for (ListDataProject listDataProject : listDatas) {
				prepareListDataProjectForSaving(listDataProject, snapList);
				getListDataProjectDAO().save(listDataProject);
			}
		}
		
		return snapList.getId();
	}
	
	private GermplasmList createInitialGermplasmList(int projectId, Integer originalListId, GermplasmListType type) throws MiddlewareQueryException {
		GermplasmList snapList = new GermplasmList(); 
		
		snapList.setId(getGermplasmListDAO().getNegativeId("id"));
		snapList.setProjectId(projectId);
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		snapList.setDate(Long.valueOf(format.format(new Date())));
		snapList.setStatus(1);
		snapList.setType(type.name());
		snapList.setParent(null);
		snapList.setListRef(originalListId);
		Long dateLong = Long.valueOf(System.currentTimeMillis());
		String name = type.name() + "-" + dateLong;
		snapList.setName(name);
		snapList.setDescription(name);

		return snapList;
	}
	
	private void updateGermplasmListInfo(GermplasmList snapList, int originalListId) throws MiddlewareQueryException {
		setWorkingDatabase(originalListId);
		GermplasmList origList = getGermplasmListDAO().getById(originalListId);
		requireLocalDatabaseInstance();
		if (origList != null) {
			snapList.setListLocation(origList.getListLocation());
			snapList.setUserId(origList.getUserId());
			snapList.setNotes(origList.getNotes());
			snapList.setsDate(origList.getsDate());
			snapList.seteDate(origList.geteDate());
		}
		else {
			setDefaultGermplasmListInfo(snapList);
		}
	}

	private void setDefaultGermplasmListInfo(GermplasmList snapList) {
		snapList.setListLocation(null);
		snapList.setUserId(0); //database default
		snapList.setNotes(null);
		snapList.setsDate(null);
		snapList.seteDate(null);
	}
	
	private GermplasmList getGermplasmList(int projectId, GermplasmListType type) throws MiddlewareQueryException {
		setWorkingDatabase(projectId);
		GermplasmList gList = null;
		List<GermplasmList> tempList = getGermplasmListDAO().getByProjectIdAndType(projectId, type);
		requireLocalDatabaseInstance();
		if (tempList != null && !tempList.isEmpty()) {
			gList = tempList.get(0);
		}
		return gList;
	}
	
	private void prepareListDataProjectForSaving(ListDataProject listDataProject, GermplasmList snapList) throws MiddlewareQueryException {
		listDataProject.setListDataProjectId(getListDataProjectDAO().getNegativeId("listDataProjectId"));
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
}
