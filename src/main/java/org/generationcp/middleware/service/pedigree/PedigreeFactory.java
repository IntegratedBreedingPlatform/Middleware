
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.workbench.CropType.CropEnum;
import org.generationcp.middleware.service.api.PedigreeService;

public class PedigreeFactory {

	public static final String PROFILE_CIMMYT = "CIMMYT";
	public static final String PROFILE_DEFAULT = "DEFAULT";

	private PedigreeFactory() {
		// do nothing
	}

	public static PedigreeService getPedigreeService(final HibernateSessionProvider sessionProvider, final String pedigreeProfile,
			final String cropType) {
		if (PedigreeFactory.isCimmytWheat(pedigreeProfile, cropType)) {
			return new PedigreeCimmytWheatServiceImpl(sessionProvider);
		}
		return new PedigreeServiceImpl(sessionProvider, cropType);
	}

	// TODO Remove the duplicate org.generationcp.commons.util.CrossingUtil.isCimmytWheat(String, String) in Commons and update references
	// to Commons method references.
	public static boolean isCimmytWheat(final String profile, final String crop) {
		if (profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT)
				&& CropEnum.WHEAT.toString().equalsIgnoreCase(crop)) {
			return true;
		}
		return false;
	}
}
