package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyrus on 12/16/14.
 */
public class ProgramPresetDAO extends GenericDAO<ProgramPreset, Integer> {

	public ProgramPresetDAO(final Session session) {
		super(session);
	}

	public ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException {
		return getById(id);
	}

	@SuppressWarnings("unchecked")
	public List<ProgramPreset> getAllProgramPresetFromProgram(String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
				"error in: ProgramPresetDAO.getAllProgramPresetFromProgram(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	@SuppressWarnings("unchecked")
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId) throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));
			criteria.add(Restrictions.eq("toolId", toolId));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
				"error in: ProgramPresetDAO.getProgramPresetFromProgramAndTool(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId, String toolSection)
		throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.or(
				Restrictions.eq("programUuid", programUUID),
				Restrictions.isNull("programUuid")));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
				"error in: ProgramPresetDAO.getProgramPresetFromProgramAndTool(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	public List<ProgramPreset> getProgramPresetFromProgramAndToolByName(String presetName, String programUUID, int toolId,
		String toolSection) throws MiddlewareQueryException {

		try {
			Criteria criteria = this.getSession().createCriteria(ProgramPreset.class);

			criteria.add(Restrictions.eq("programUuid", programUUID));
			criteria.add(Restrictions.eq("toolId", toolId));
			criteria.add(Restrictions.eq("toolSection", toolSection));
			criteria.add(Restrictions.like("name", presetName));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
				"error in: ProgramPresetDAO.getProgramPresetFromProgramAndToolByName(programId=" + programUUID + "): " + e.getMessage(), e);
		}

		return new ArrayList<ProgramPreset>();
	}

	public ProgramPreset saveOrUpdateProgramPreset(ProgramPreset programPreset) throws MiddlewareQueryException {

		try {
			ProgramPreset result = saveOrUpdate(programPreset);

			return result;

		} catch (HibernateException e) {
			this.logAndThrowException(
				"Cannot perform: ProgramPresetDAO.saveOrUpdateProgramPreset(programPresetName=" + programPreset.getName() + "): "
					+ e.getMessage(), e);
		}
		return null;
	}

	public void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException {
		try {
			ProgramPreset preset = getById(programPresetId);
			getSession().delete(preset);
		} catch (HibernateException e) {
			this.logAndThrowException("Cannot delete preset: ProgramPresetDAO.deleteProgramPreset(programPresetId=" + programPresetId
				+ "): " + e.getMessage(), e);
		}
	}
}
