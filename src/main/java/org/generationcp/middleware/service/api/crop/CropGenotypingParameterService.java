package org.generationcp.middleware.service.api.crop;

import org.generationcp.middleware.service.impl.crop.CropGenotypingParameterDTO;

import java.util.Optional;

public interface CropGenotypingParameterService {

	Optional<CropGenotypingParameterDTO> getCropGenotypingParameterById(int cropGenotypingParameterId);

	Optional<CropGenotypingParameterDTO> getCropGenotypingParameter(String cropName);

	void updateCropGenotypingParameter(CropGenotypingParameterDTO cropGenotypingParameterDTO);

	CropGenotypingParameterDTO createCropGenotypingParameter(CropGenotypingParameterDTO cropGenotypingParameterDTO);
}
