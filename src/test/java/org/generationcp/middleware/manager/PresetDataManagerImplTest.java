package org.generationcp.middleware.manager;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PresetDataManagerImplTest extends DataManagerIntegrationTest {

	static PresetDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception  {
		manager = new PresetDataManagerImpl(DataManagerIntegrationTest.managerFactory.getSessionProviderForLocal(),DataManagerIntegrationTest.managerFactory.getSessionProviderForCentral());
	}

	@Test
	public void testCRUDProgramPresetDAO() throws Exception {
		ProgramPreset preset = new ProgramPreset();
		preset.setConfiguration("<configuration/>");
		preset.setIsDefault(Boolean.TRUE);
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setProgramUuid(1);

		ProgramPreset results = manager.saveOrUpdateProgramPreset(preset);

		assertTrue("we retrieve the saved primary id",results.getProgramPresetsId() > 0);

		Integer id = results.getProgramPresetsId();

		// test retrieve from database using id
		ProgramPreset retrievedResult = manager.getProgramPresetById(id);

		assertEquals("we retrieved the correct object from database",results,retrievedResult);

		// we test deletion, also serves as cleanup
		manager.deleteProgramPreset(id);

		assertNull("program preset with id=" + id + " should no longer exist",manager.getProgramPresetById(id));
	}

	@Test
	public void testGetAllProgramPresetFromProgram() throws Exception {
		List<ProgramPreset> fullList = initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = manager.getAllProgramPresetFromProgram(j);

			for (ProgramPreset p : presetsList) {
				assertEquals("should only retrieve all standard presets with same program",Integer.valueOf(j),p.getProgramUuid());
			}
		}

		for (ProgramPreset p : fullList) {
			manager.deleteProgramPreset(p.getProgramPresetsId());
		}
	}

	@Test
	public void testGetProgramPresetFromProgramAndTool() throws Exception {
		List<ProgramPreset> fullList = initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = manager.getProgramPresetFromProgramAndTool(j, j);

			for (ProgramPreset p : presetsList) {
				assertEquals("should only retrieve all standard presets with same tool",Integer.valueOf(j),p.getToolId());
				assertEquals("should only retrieve all standard presets with same program",Integer.valueOf(j),p.getProgramUuid());
			}
		}

		for (ProgramPreset p : fullList) {
			manager.deleteProgramPreset(p.getProgramPresetsId());
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndToolAndToolSection() throws Exception {
		List<ProgramPreset> fullList = initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = manager.getProgramPresetFromProgramAndTool(j, j);

			for (ProgramPreset p : presetsList) {
				assertEquals("should only retrieve all standard presets with same tool",Integer.valueOf(j),p.getToolId());
				assertEquals("should only retrieve all standard presets with same program",Integer.valueOf(j),p.getProgramUuid());
				assertEquals("should only retrieve all standard presets with same tool section","tool_section_" + j,p.getToolSection());

			}
		}

		for (ProgramPreset p : fullList) {
			manager.deleteProgramPreset(p.getProgramPresetsId());
		}

	}

	protected List<ProgramPreset> initializeProgramPresets() throws MiddlewareQueryException {
		List<ProgramPreset> fullList = new ArrayList<ProgramPreset>();

		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 6; i++) {
				ProgramPreset preset = new ProgramPreset();
				preset.setConfiguration("<configuration/>");
				preset.setName("configuration_" + j + "_" + i);
				preset.setToolSection("tool_section_" + j);
				preset.setToolId(j);
				preset.setProgramUuid(j);

				fullList.add(manager.saveOrUpdateProgramPreset(preset));
			}
		}
		return fullList;
	}

}