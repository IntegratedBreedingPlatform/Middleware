package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.UIDGenerator;

import java.util.List;

public class GermplasmGuidGenerator {

	private static final UIDGenerator.UID_ROOT UID_ROOT = UIDGenerator.UID_ROOT.GERMPLASM;
	public static final int SUFFIX_LENGTH = 8;

	public static void generateGermplasmGuids(final CropType crop, final List<Germplasm> germplasmList) {
		UIDGenerator.<Germplasm>generate(crop, germplasmList, UID_ROOT, SUFFIX_LENGTH,
			new UIDGenerator.UIDAdapter<Germplasm>() {

				@Override
				public String getUID(final Germplasm entry) {
					return entry.getGermplasmUUID();
				}

				@Override
				public void setUID(final Germplasm entry, final String uid) {
					entry.setGermplasmUUID(uid);
				}
			});
	}

}
