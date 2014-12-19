package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.presets.ProgramPreset;

import java.util.List;

/**
 * Created by cyrus on 12/16/14.
 */
public interface PresetDataManager {

	/**
	 * Returns the programPreset given its primary key id
	 * @param id
	 * @return
	 * @throws MiddlewareQueryException
	 */
	ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException;

	/**
	 * Returns all program preset given programUUID
	 * @param programId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getAllProgramPresetFromProgram(int programId)
			throws MiddlewareQueryException;

	/**
	 * Returns all program preset given programUUID and toolID
	 * @param programId
	 * @param toolId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getProgramPresetFromProgramAndTool(int programId, int toolId)
			throws MiddlewareQueryException;

	List<ProgramPreset> getProgramPresetFromProgramAndTool(int programId, int toolId,
			String toolSection)
			throws MiddlewareQueryException;

	/**
	 * save or update a progam preset
	 *
	 * @param programPreset
	 * @return
	 * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
	 */
	ProgramPreset addProgramPreset(ProgramPreset programPreset) throws
			MiddlewareQueryException;

	/**
	 * delete program preset by id
	 *
	 * @param programPresetId
	 * @throws MiddlewareQueryException
	 */
	void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException;
}
