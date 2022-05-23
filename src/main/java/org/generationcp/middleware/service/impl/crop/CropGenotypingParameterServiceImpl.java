package org.generationcp.middleware.service.impl.crop;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropGenotypingParameter;
import org.generationcp.middleware.service.api.crop.CropGenotypingParameterMapper;
import org.generationcp.middleware.service.api.crop.CropGenotypingParameterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CropGenotypingParameterServiceImpl implements CropGenotypingParameterService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	@Value("${db.encryption.secret.passphrase}")
	private String secretPassphrase;

	public CropGenotypingParameterServiceImpl(final HibernateSessionProvider workbenchSessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(workbenchSessionProvider);
	}

	@Override
	public Optional<CropGenotypingParameterDTO> getCropGenotypingParameter(final String cropName) {
		final CropGenotypingParameter cropGenotypingParameter =
			this.workbenchDaoFactory.getCropGenotyingParameterDAO(this.secretPassphrase).getByCropName(cropName);
		if (cropGenotypingParameter != null) {
			return Optional.of(new CropGenotypingParameterMapper().map(cropGenotypingParameter));
		} else {
			// Return an empty object
			return Optional.empty();
		}
	}

	@Override
	public void updateCropGenotypingParameter(final CropGenotypingParameterDTO cropGenotypingParameterDTO) {
		final CropGenotypingParameter cropGenotypingParameter =
			this.workbenchDaoFactory.getCropGenotyingParameterDAO(this.secretPassphrase)
				.getByCropName(cropGenotypingParameterDTO.getCropName());
		new CropGenotypingParameterMapper().map(cropGenotypingParameterDTO, cropGenotypingParameter);
		this.workbenchDaoFactory.getCropGenotyingParameterDAO(this.secretPassphrase).saveOrUpdate(cropGenotypingParameter);
	}

	@Override
	public void createCropGenotypingParameter(final CropGenotypingParameterDTO cropGenotypingParameterDTO) {
		final CropGenotypingParameter cropGenotypingParameter = new CropGenotypingParameterMapper().map(cropGenotypingParameterDTO);
		this.workbenchDaoFactory.getCropGenotyingParameterDAO(this.secretPassphrase).save(cropGenotypingParameter);
	}
}
