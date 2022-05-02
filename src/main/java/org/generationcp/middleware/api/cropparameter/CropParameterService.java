package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.pojos.CropParameter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CropParameterService {

	List<CropParameter> getCropParameter(Pageable pageable);

	void modifyCropParameter(String key, CropParameterPatchRequestDTO request);
}
