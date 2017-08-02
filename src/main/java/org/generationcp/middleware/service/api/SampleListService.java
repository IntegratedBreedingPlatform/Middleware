package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.plant.PlantDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;

public interface SampleListService {

	public Integer createOrUpdateSampleList(SampleListDTO sampleListDto);

	public SampleListDTO getSampleList(Integer sampleListId);

}
