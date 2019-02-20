package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.presets.ProgramPreset;

/**
 * Created by cyrus on 12/16/14.
 */
public interface PresetService {

	/**
	 * Returns the programPreset given its primary key id
	 *
	 * @param id
	 * @return
	 * @throws MiddlewareQueryException
	 */
	ProgramPreset getProgramPresetById(int id) throws MiddlewareQueryException;

	/**
	 * save or update a progam preset
	 *
	 * @param programPreset
	 * @return
	 * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
	 */
	ProgramPreset saveOrUpdateProgramPreset(ProgramPreset programPreset) throws MiddlewareQueryException;

	/**
	 * delete program preset by id
	 *
	 * @param programPresetId
	 * @throws MiddlewareQueryException
	 */
	void deleteProgramPreset(int programPresetId) throws MiddlewareQueryException;

	/**
	 *
	 * @param presetName
	 * @param programUUID
	 * @param toolId
	 * @param toolSection
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getProgramPresetFromProgramAndToolByName(String presetName, String programUUID, int toolId, String toolSection)
					throws MiddlewareQueryException;

	/***
	 *
	 * @param programUUID
	 * @param toolId
	 * @param toolSection
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId, String toolSection)
			throws MiddlewareQueryException;

	/***
	 *
	 * @param programUUID
	 * @param toolId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getProgramPresetFromProgramAndTool(String programUUID, int toolId) throws MiddlewareQueryException;

	/***
	 *
	 * @param programUUID
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<ProgramPreset> getAllProgramPresetFromProgram(String programUUID) throws MiddlewareQueryException;
}
