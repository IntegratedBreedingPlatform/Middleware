package org.generationcp.middleware.manager;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyrus on 12/16/14.
 */
public class PresetDataManagerImpl extends DataManager implements PresetDataManager {
	public PresetDataManagerImpl(
			HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public PresetDataManagerImpl() {
		super();
	}

	@Override
	public ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException {
		return this.getProgramPresetDAO().getById(id);
	}

	@SuppressWarnings("unchecked") @Override
	public List<ProgramPreset> getAllProgramPresetFromProgram(int programId)
			throws MiddlewareQueryException {
		try {
			Criteria criteria = getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programId));

			return criteria.list();
		} catch (HibernateException e) {
			logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId="
							+ programId + "): "
							+ e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@SuppressWarnings("unchecked") @Override
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(int programId, int toolId)
			throws MiddlewareQueryException {

		try {
			Criteria criteria = getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programId));
			criteria.add(Restrictions.eq("toolId", toolId));

			return criteria.list();
		} catch (HibernateException e) {
			logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId="
							+ programId + "): "
							+ e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(int programId, int toolId,
			String toolSection)
			throws MiddlewareQueryException {

		try {
			Criteria criteria = getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programId));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (HibernateException e) {
			logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId="
							+ programId + "): "
							+ e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndToolByName(String presetName,int programId, int toolId,
			String toolSection)
			throws MiddlewareQueryException {

		try {
			Criteria criteria = getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programId));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name",presetName));

			return criteria.list();
		} catch (HibernateException e) {
			logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId="
							+ programId + "): "
							+ e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}


	@Override
	public ProgramPreset saveOrUpdateProgramPreset(ProgramPreset programPreset)
			throws MiddlewareQueryException {

		Transaction transaction = getCurrentSession().beginTransaction();

		try {
			ProgramPreset result = this.getProgramPresetDAO().saveOrUpdate(programPreset);

			transaction.commit();

			return result;

		} catch (HibernateException e) {
			rollbackTransaction(transaction);
			logAndThrowException(
					"Cannot perform: WorkbenchDataManager.deleteProgramPreset(programPresetName="
							+ programPreset.getName() + "): "
							+ e.getMessage(), e);
		} finally {
			getCurrentSession().flush();
		}

		return null;
	}

	@Override
	public void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException {
		Transaction transaction = getCurrentSession().beginTransaction();

		try {

			ProgramPreset preset = this.getProgramPresetDAO().getById(programPresetId);
			getCurrentSession().delete(preset);
			transaction.commit();

		} catch (HibernateException e) {
			rollbackTransaction(transaction);
			logAndThrowException(
					"Cannot delete preset: WorkbenchDataManager.deleteProgramPreset(programPresetId="
							+ programPresetId + "): "
							+ e.getMessage(), e);
		} finally {
			getCurrentSession().flush();
		}
	}
}
