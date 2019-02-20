
package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.PresetService;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PresetServiceImpl implements PresetService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public PresetServiceImpl(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	public PresetServiceImpl() {
		super();
	}

	@Override
	public ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().getById(id);
	}

	@Override
	public List<ProgramPreset> getAllProgramPresetFromProgram(String programUUID) throws MiddlewareQueryException {
		return this.daoFactory.getProgramPresetDAO().getAllProgramPresetFromProgram(programUUID);
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().getProgramPresetFromProgramAndTool(programUUID, toolId);
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId, String toolSection)
					throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().getProgramPresetFromProgramAndTool(programUUID, toolId, toolSection);
	}

	@Override
	public List<ProgramPreset> getProgramPresetFromProgramAndToolByName(String presetName, String programUUID, int toolId,
			String toolSection) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().getProgramPresetFromProgramAndToolByName(presetName, programUUID, toolId, toolSection);
	}

	@Override
	public ProgramPreset saveOrUpdateProgramPreset(ProgramPreset programPreset) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().saveOrUpdate(programPreset);
	}

	@Override
	public void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException {
		daoFactory.getProgramPresetDAO().deleteProgramPreset(programPresetId);
	}

	@Override
	public ProgramPreset saveProgramPreset(final ProgramPreset programPreset) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().save(programPreset);
	}

	@Override
	public ProgramPreset updateProgramPreset(final ProgramPreset programPreset) throws MiddlewareQueryException {
		return daoFactory.getProgramPresetDAO().update(programPreset);
	}
}
