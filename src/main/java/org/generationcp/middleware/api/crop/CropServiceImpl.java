package org.generationcp.middleware.api.crop;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CropServiceImpl implements CropService {

	private final WorkbenchDaoFactory daoFactory;

	public CropServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<CropType> getInstalledCropDatabases() {
		return this.daoFactory.getCropTypeDAO().getAll();
	}

	@Override
	public List<CropType> getAvailableCropsForUser(final int workbenchUserId) {
		return this.daoFactory.getCropTypeDAO().getAvailableCropsForUser(workbenchUserId);
	}

	@Override
	public CropType getCropTypeByName(final String cropName) {
		return this.daoFactory.getCropTypeDAO().getByName(cropName);
	}

}
