package org.generationcp.middleware.service.api.crop;

import org.generationcp.middleware.service.impl.crop.CropGenotypingParameterDTO;

public interface CropGenotypingParameterService {

	CropGenotypingParameterDTO getCropGenotypingParameter(String cropName);

	void updateCropGenotypingParameter(CropGenotypingParameterDTO cropGenotypingParameterDTO);

	void createCropGenotypingParameter(CropGenotypingParameterDTO cropGenotypingParameterDTO);
}
