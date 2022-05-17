package org.generationcp.middleware.service.api.crop;

import org.generationcp.middleware.pojos.workbench.CropGenotypingParameter;
import org.generationcp.middleware.service.impl.crop.CropGenotypingParameterDTO;

public class CropGenotypingParameterMapper {

	public CropGenotypingParameterDTO map(final CropGenotypingParameter cropGenotypingParameter) {
		final CropGenotypingParameterDTO cropGenotypingParameterDTO = new CropGenotypingParameterDTO();
		cropGenotypingParameterDTO.setCropName(cropGenotypingParameter.getCropName());
		cropGenotypingParameterDTO.setEndpoint(cropGenotypingParameter.getEndpoint());
		cropGenotypingParameterDTO.setTokenEndpoint(cropGenotypingParameter.getTokenEndpoint());
		cropGenotypingParameterDTO.setUserName(cropGenotypingParameter.getUserName());
		cropGenotypingParameterDTO.setPassword(cropGenotypingParameter.getPassword());
		cropGenotypingParameterDTO.setProgramId(cropGenotypingParameter.getProgramId());
		return cropGenotypingParameterDTO;
	}

	public CropGenotypingParameter map(final CropGenotypingParameterDTO cropGenotypingParameterDTO) {
		final CropGenotypingParameter cropGenotypingParameter = new CropGenotypingParameter();
		cropGenotypingParameter.setCropName(cropGenotypingParameterDTO.getCropName());
		cropGenotypingParameter.setEndpoint(cropGenotypingParameterDTO.getEndpoint());
		cropGenotypingParameter.setTokenEndpoint(cropGenotypingParameterDTO.getTokenEndpoint());
		cropGenotypingParameter.setUserName(cropGenotypingParameterDTO.getUserName());
		cropGenotypingParameter.setPassword(cropGenotypingParameterDTO.getPassword());
		cropGenotypingParameter.setProgramId(cropGenotypingParameterDTO.getProgramId());
		return cropGenotypingParameter;
	}

	public CropGenotypingParameterDTO map(final CropGenotypingParameterDTO from, final CropGenotypingParameter to) {
		final CropGenotypingParameterDTO cropGenotypingParameterDTO = new CropGenotypingParameterDTO();
		to.setEndpoint(from.getEndpoint());
		to.setTokenEndpoint(from.getTokenEndpoint());
		to.setUserName(from.getUserName());
		to.setPassword(from.getPassword());
		to.setProgramId(from.getUserName());
		return cropGenotypingParameterDTO;
	}

}
