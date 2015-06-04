
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PresetDataManagerImplTest extends DataManagerIntegrationTest {

	static PresetDataManager manager;
	private static final String DUMMY_PROGRAM_UUID = "12345678899";

	@BeforeClass
	public static void setUp() throws Exception {
		PresetDataManagerImplTest.manager = new PresetDataManagerImpl(DataManagerIntegrationTest.managerFactory.getSessionProvider());
	}

	@Test
	public void testCRUDProgramPresetDAO() throws Exception {
		ProgramPreset preset = new ProgramPreset();
		preset.setConfiguration("<configuration/>");
		preset.setIsDefault(Boolean.TRUE);
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setProgramUuid(PresetDataManagerImplTest.DUMMY_PROGRAM_UUID);

		ProgramPreset results = PresetDataManagerImplTest.manager.saveOrUpdateProgramPreset(preset);

		Assert.assertTrue("we retrieve the saved primary id", results.getProgramPresetId() > 0);

		Integer id = results.getProgramPresetId();

		// test retrieve from database using id
		ProgramPreset retrievedResult = PresetDataManagerImplTest.manager.getProgramPresetById(id);

		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		// we test deletion, also serves as cleanup
		PresetDataManagerImplTest.manager.deleteProgramPreset(id);

		Assert.assertNull("program preset with id=" + id + " should no longer exist",
				PresetDataManagerImplTest.manager.getProgramPresetById(id));
	}

	@Test
	public void testGetAllProgramPresetFromProgram() throws Exception {
		List<ProgramPreset> fullList = this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = PresetDataManagerImplTest.manager.getAllProgramPresetFromProgram(String.valueOf(j));

			for (ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
			}
		}

		for (ProgramPreset p : fullList) {
			PresetDataManagerImplTest.manager.deleteProgramPreset(p.getProgramPresetId());
		}
	}

	@Test
	public void testGetProgramPresetFromProgramAndTool() throws Exception {
		List<ProgramPreset> fullList = this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = PresetDataManagerImplTest.manager.getProgramPresetFromProgramAndTool(String.valueOf(j), j);

			for (ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same tool", Integer.valueOf(j), p.getToolId());
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
			}
		}

		for (ProgramPreset p : fullList) {
			PresetDataManagerImplTest.manager.deleteProgramPreset(p.getProgramPresetId());
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndToolAndToolSection() throws Exception {
		List<ProgramPreset> fullList = this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			List<ProgramPreset> presetsList = PresetDataManagerImplTest.manager.getProgramPresetFromProgramAndTool(String.valueOf(j), j);

			for (ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same tool", Integer.valueOf(j), p.getToolId());
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
				Assert.assertEquals("should only retrieve all standard presets with same tool section", "tool_section_" + j,
						p.getToolSection());

			}
		}

		for (ProgramPreset p : fullList) {
			PresetDataManagerImplTest.manager.deleteProgramPreset(p.getProgramPresetId());
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndToolByName() throws Exception {
		List<ProgramPreset> fullList = this.initializeProgramPresets();

		// this should exists
		List<ProgramPreset> result =
				PresetDataManagerImplTest.manager.getProgramPresetFromProgramAndToolByName("configuration_1_1", String.valueOf(1), 1,
						"tool_section_1");

		Assert.assertTrue("result should not be empty", result.size() > 0);
		Assert.assertEquals("Should return the same name", "configuration_1_1", result.get(0).getName());

		// cleanup
		for (ProgramPreset p : fullList) {
			PresetDataManagerImplTest.manager.deleteProgramPreset(p.getProgramPresetId());
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
				preset.setProgramUuid(String.valueOf(j));

				fullList.add(PresetDataManagerImplTest.manager.saveOrUpdateProgramPreset(preset));
			}
		}
		return fullList;
	}

}
