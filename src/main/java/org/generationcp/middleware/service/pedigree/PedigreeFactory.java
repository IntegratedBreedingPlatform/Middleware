
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.workbench.CropType.CropEnum;
import org.generationcp.middleware.service.api.PedigreeService;

public class PedigreeFactory {

	public static final String PROFILE_CIMMYT = "CIMMYT";
	public static final String PROFILE_DEFAULT = "DEFAULT";

	public static PedigreeService getPedigreeService(HibernateSessionProvider sessionProvider, String pedigreeProfile, String cropType) {
		if (cropType != null && PedigreeFactory.PROFILE_CIMMYT.equalsIgnoreCase(pedigreeProfile)
				&& CropEnum.WHEAT.toString().equalsIgnoreCase(cropType)) {
			return new PedigreeCimmytWheatServiceImpl(sessionProvider);
		}
		return new PedigreeDefaultServiceImpl(sessionProvider);
	}
}
