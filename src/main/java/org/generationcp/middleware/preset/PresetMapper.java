package org.generationcp.middleware.preset;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.domain.labelprinting.PresetDTO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.pojos.presets.ProgramPreset;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by clarysabel on 2/19/19.
 */
@Component
public class PresetMapper {

	private ObjectMapper jacksonMapper;

	public PresetMapper() {
		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	}

	public ProgramPreset map (final PresetDTO presetDTO) {
		final ProgramPreset programPreset = new ProgramPreset();
		programPreset.setName(presetDTO.getName());
		programPreset.setToolId(presetDTO.getToolId());
		programPreset.setToolSection(presetDTO.getToolSection());
		programPreset.setProgramUuid(presetDTO.getProgramUUID());
		try {
			programPreset.setConfiguration(this.jacksonMapper.writerWithView(PresetDTO.View.Configuration.class).writeValueAsString(presetDTO));
		} catch (final Exception e) {
			throw new MiddlewareRequestException("preset.mapping.internal.error", null, LocaleContextHolder.getLocale());
		}
		return programPreset;
	}

	public ProgramPreset map (final PresetDTO presetDTO, final ProgramPreset programPreset) {
		programPreset.setName(presetDTO.getName());
		programPreset.setToolId(presetDTO.getToolId());
		programPreset.setToolSection(presetDTO.getToolSection());
		programPreset.setProgramUuid(presetDTO.getProgramUUID());
		try {
			programPreset.setConfiguration(this.jacksonMapper.writerWithView(PresetDTO.View.Configuration.class).writeValueAsString(presetDTO));
		} catch (final Exception e) {
			throw new MiddlewareRequestException("preset.mapping.internal.error", null, LocaleContextHolder.getLocale());
		}
		return programPreset;
	}

	public PresetDTO map (final ProgramPreset programPreset) {
		final PresetDTO presetDTO;
		try {
			presetDTO = this.jacksonMapper.readValue(programPreset.getConfiguration(), PresetDTO.class);
		} catch (final Exception e) {
			throw new MiddlewareRequestException("preset.mapping.internal.error", null, LocaleContextHolder.getLocale());
		}
		presetDTO.setToolId(programPreset.getToolId());
		presetDTO.setProgramUUID(programPreset.getProgramUuid());
		presetDTO.setToolSection(programPreset.getToolSection());
		presetDTO.setName(programPreset.getName());
		presetDTO.setId(programPreset.getProgramPresetId());
		return presetDTO;
	}

}
