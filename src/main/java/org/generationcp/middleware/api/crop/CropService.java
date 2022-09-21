package org.generationcp.middleware.api.crop;

import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

public interface CropService {


	/**
	 * Get the list of all installed central crop databases.
	 *
	 * @return the installed central crops
	 */
	List<CropType> getInstalledCropDatabases();

	List<CropType> getAvailableCropsForUser(int workbenchUserId);

	/**
	 * Get the crop type corresponding to the given name.
	 *
	 * @param cropName - the crop name to match
	 * @return the CropType retrieved
	 */
	CropType getCropTypeByName(String cropName);

}
