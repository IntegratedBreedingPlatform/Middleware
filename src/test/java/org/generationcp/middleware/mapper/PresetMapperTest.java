package org.generationcp.middleware.mapper;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.labelprinting.FilePresetConfigurationDTO;
import org.generationcp.middleware.domain.labelprinting.LabelPrintingPresetDTO;
import org.generationcp.middleware.domain.labelprinting.PresetDTO;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.generationcp.middleware.preset.PresetMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PresetMapperTest extends IntegrationTestBase {

	protected final String programUuid = UUID.randomUUID().toString();

	private String name;

	private String programUUID;

	private String toolSection;

	private Integer toolId;

	private String type;

	private List<List<String>> selectedField;

	private LabelPrintingPresetDTO.BarcodeSetting barcodeSetting;

	private FilePresetConfigurationDTO filePresetConfigurationDTO;

	private String configuration;

	private Integer programPresetId;

	@Autowired
	private PresetMapper presetMapper;

	@Before
	public void init() {
		programUUID = RandomStringUtils.randomAlphabetic(10);
		name = RandomStringUtils.randomAlphabetic(10);
		toolSection = RandomStringUtils.randomAlphabetic(10);
		toolId = RandomUtils.nextInt();
		programPresetId = RandomUtils.nextInt();

		type = "LabelPrintingPreset";
		selectedField = Arrays.asList(Arrays.asList("STATIC_4", "STATIC_13"));
		barcodeSetting =
			new LabelPrintingPresetDTO.BarcodeSetting(Boolean.TRUE, Boolean.FALSE, Arrays.asList("STATIC_2"));
		filePresetConfigurationDTO = new FilePresetConfigurationDTO();
		filePresetConfigurationDTO.setOutputType("csv");
		configuration =
			"{\"type\":\"LabelPrintingPreset\",\"selectedFields\":[[\"STATIC_4\",\"STATIC_13\"]],\"barcodeSetting\":{\"barcodeNeeded\":true,\"automaticBarcode\":false,\"barcodeFields\":[\"STATIC_2\"]},\"includeHeadings\":true,\"fileConfiguration\":{\"outputType\":\"csv\"}}";
	}

	@Test
	public void testMapProgramPreset() {
		final LabelPrintingPresetDTO presetDTO = new LabelPrintingPresetDTO();
		presetDTO.setName(name);
		presetDTO.setToolSection(toolSection);
		presetDTO.setToolId(toolId);
		presetDTO.setProgramUUID(programUUID);
		presetDTO.setType(type);
		presetDTO.setSelectedFields(selectedField);
		presetDTO.setBarcodeSetting(barcodeSetting);
		presetDTO.setFileConfiguration(filePresetConfigurationDTO);

		final ProgramPreset programPreset = presetMapper.map(presetDTO);
		assertEquals(programPreset.getName(), presetDTO.getName());
		assertEquals(programPreset.getToolId(), presetDTO.getToolId());
		assertEquals(programPreset.getToolSection(), presetDTO.getToolSection());
		assertEquals(programPreset.getProgramUuid(), presetDTO.getProgramUUID());
		assertEquals(programPreset.getName(), presetDTO.getName());
		assertEquals(programPreset.getConfiguration(), configuration);

	}

	@Test
	public void testMapPresetDTO() {
		final ProgramPreset programPreset = new ProgramPreset();
		programPreset.setProgramPresetId(programPresetId);
		programPreset.setProgramUuid(programUuid);
		programPreset.setToolId(toolId);
		programPreset.setName(name);
		programPreset.setToolSection(toolSection);
		programPreset.setConfiguration(configuration);

		final PresetDTO presetDTO = presetMapper.map(programPreset);
		assertEquals(programPreset.getName(), presetDTO.getName());
		assertEquals(programPreset.getToolId(), presetDTO.getToolId());
		assertEquals(programPreset.getToolSection(), presetDTO.getToolSection());
		assertEquals(programPreset.getProgramUuid(), presetDTO.getProgramUUID());
		assertEquals(programPreset.getName(), presetDTO.getName());
		assertTrue(presetDTO instanceof LabelPrintingPresetDTO);
		final LabelPrintingPresetDTO labelPrintingPresetDTO = (LabelPrintingPresetDTO) presetDTO;

		assertEquals(labelPrintingPresetDTO.getBarcodeSetting(), barcodeSetting);
		assertEquals(labelPrintingPresetDTO.getSelectedFields(), selectedField);
		assertEquals(labelPrintingPresetDTO.getFileConfiguration(), filePresetConfigurationDTO);

	}

}
