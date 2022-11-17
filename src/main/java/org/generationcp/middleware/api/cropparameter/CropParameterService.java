package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.pojos.CropParameter;
import org.generationcp.middleware.service.impl.crop.CropGenotypingParameterDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CropParameterService {

	List<CropParameter> getCropParameters(Pageable pageable);

	void modifyCropParameter(String key, CropParameterPatchRequestDTO request);

	Optional<CropParameter> getCropParameter(CropParameterEnum cropParameterEnum);

	Optional<CropGenotypingParameterDTO> getCropGenotypingParameter(String cropName);
}
