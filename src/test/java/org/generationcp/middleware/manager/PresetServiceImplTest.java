package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PresetService;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PresetServiceImplTest extends IntegrationTestBase {

	@Autowired
	private PresetService presetService;

	private static final String DUMMY_PROGRAM_UUID = "12345678899";

	@Test
	public void testCRUDProgramPresetDAO() {
		final ProgramPreset preset = new ProgramPreset();
		preset.setConfiguration("<configuration/>");
		preset.setIsDefault(Boolean.TRUE);
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setProgramUuid(PresetServiceImplTest.DUMMY_PROGRAM_UUID);

		final ProgramPreset results = this.presetService.saveOrUpdateProgramPreset(preset);

		Assert.assertTrue("we retrieve the saved primary id", results.getProgramPresetId() > 0);

		final Integer id = results.getProgramPresetId();

		// test retrieve from database using id
		final ProgramPreset retrievedResult = this.presetService.getProgramPresetById(id);

		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		// we test deletion
		this.presetService.deleteProgramPreset(id);

		Assert.assertNull("program preset with id=" + id + " should no longer exist", this.presetService.getProgramPresetById(id));
	}

	@Test
	public void testGetAllProgramPresetFromProgram() {
		this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			final List<ProgramPreset> presetsList = this.presetService.getAllProgramPresetFromProgram(String.valueOf(j));

			for (final ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
			}
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndTool() {
		this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			final List<ProgramPreset> presetsList = this.presetService.getProgramPresetFromProgramAndTool(String.valueOf(j), j);

			for (final ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same tool", Integer.valueOf(j), p.getToolId());
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
			}
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndToolAndToolSection() {
		this.initializeProgramPresets();

		for (int j = 1; j < 3; j++) {
			final List<ProgramPreset> presetsList = this.presetService.getProgramPresetFromProgramAndTool(String.valueOf(j), j);

			for (final ProgramPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same tool", Integer.valueOf(j), p.getToolId());
				Assert.assertEquals("should only retrieve all standard presets with same program", String.valueOf(j), p.getProgramUuid());
				Assert.assertEquals("should only retrieve all standard presets with same tool section", "tool_section_" + j,
						p.getToolSection());

			}
		}

	}

	@Test
	public void testGetProgramPresetFromProgramAndToolByName() throws Exception {
		this.initializeProgramPresets();

		// this should exists
		final List<ProgramPreset> result =
				this.presetService.getProgramPresetFromProgramAndToolByName("configuration_1_1", String.valueOf(1), 1, "tool_section_1");

		Assert.assertTrue("result should not be empty", result.size() > 0);
		Assert.assertEquals("Should return the same name", "configuration_1_1", result.get(0).getName());

	}

	@Test
	public void testSaveProgramPreset() {
		ProgramPreset preset = new ProgramPreset();
		preset.setConfiguration("<configuration/>");
		preset.setIsDefault(Boolean.TRUE);
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setProgramUuid(PresetServiceImplTest.DUMMY_PROGRAM_UUID);
		preset = presetService.saveProgramPreset(preset);
		final ProgramPreset storedProgramPreset = presetService.getProgramPresetById(preset.getProgramPresetId());

		Assert.assertEquals(preset.getConfiguration(), storedProgramPreset.getConfiguration());
		Assert.assertEquals(preset.getIsDefault(), storedProgramPreset.getIsDefault());
		Assert.assertEquals(preset.getName(), storedProgramPreset.getName());
		Assert.assertEquals(preset.getToolId(), storedProgramPreset.getToolId());
		Assert.assertEquals(preset.getProgramUuid(), storedProgramPreset.getProgramUuid());

	}

	@Test
	public void testUpdateProgramPreset() {
		ProgramPreset preset = new ProgramPreset();
		preset.setConfiguration("<configuration/>");
		preset.setIsDefault(Boolean.TRUE);
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setProgramUuid(PresetServiceImplTest.DUMMY_PROGRAM_UUID);
		preset = presetService.saveProgramPreset(preset);
		final ProgramPreset storedProgramPreset = presetService.getProgramPresetById(preset.getProgramPresetId());

		final String newName = "newName";
		storedProgramPreset.setName(newName);

		presetService.updateProgramPreset(storedProgramPreset);

		final ProgramPreset updatedStoredProgramPreset = presetService.getProgramPresetById(preset.getProgramPresetId());
		Assert.assertEquals(storedProgramPreset.getName(), updatedStoredProgramPreset.getName());

	}

	protected void initializeProgramPresets() throws MiddlewareQueryException {

		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 6; i++) {
				final ProgramPreset preset = new ProgramPreset();
				preset.setConfiguration("<configuration/>");
				preset.setName("configuration_" + j + "_" + i);
				preset.setToolSection("tool_section_" + j);
				preset.setToolId(j);
				preset.setProgramUuid(String.valueOf(j));

				this.presetService.saveOrUpdateProgramPreset(preset);
			}
		}
	}

}
