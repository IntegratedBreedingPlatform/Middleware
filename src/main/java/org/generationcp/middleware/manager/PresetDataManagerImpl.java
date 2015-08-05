
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by cyrus on 12/16/14.
 */
@Transactional
public class PresetDataManagerImpl extends DataManager implements PresetDataManager {

	public PresetDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public PresetDataManagerImpl() {
		super();
	}

	@Override
	public ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException {
		return this.getProgramPresetDAO().getById(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProgramPreset> getAllProgramPresetFromProgram(String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId) throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));
			criteria.add(Restrictions.eq("toolId", toolId));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId, String toolSection)
					throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndToolByName(String presetName, String programUUID, int toolId,
			String toolSection) throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getCurrentSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name", presetName));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"error in: WorkbenchDataManager.getAllProgramPresetFromProgram(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@Override
	public ProgramPreset saveOrUpdateProgramPreset(ProgramPreset programPreset) throws MiddlewareQueryException {



		try {
			ProgramPreset result = this.getProgramPresetDAO().saveOrUpdate(programPreset);



			return result;

		} catch (HibernateException e) {

			this.logAndThrowException(
					"Cannot perform: WorkbenchDataManager.deleteProgramPreset(programPresetName=" + programPreset.getName() + "): "
							+ e.getMessage(), e);
		} finally {
			this.getCurrentSession().flush();
		}

		return null;
	}

	@Override
	public void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException {


		try {

			ProgramPreset preset = this.getProgramPresetDAO().getById(programPresetId);
			this.getCurrentSession().delete(preset);


		} catch (HibernateException e) {

			this.logAndThrowException("Cannot delete preset: WorkbenchDataManager.deleteProgramPreset(programPresetId=" + programPresetId
					+ "): " + e.getMessage(), e);
		} finally {
			this.getCurrentSession().flush();
		}
	}
}
