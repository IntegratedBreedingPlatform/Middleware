package org.generationcp.middleware.api.germplasm;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

public class GermplasmGuidGenerator {

	public static final String MID_STRING = "G";
	public static final int SUFFIX_LENGTH = 8;

	public static void generateGermplasmGuids(final CropType crop, final List<Germplasm> germplasmList) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(germplasmList));

		final boolean doUseUUID = crop.isUseUUID();
		for (final Germplasm germplasm : germplasmList) {
			if (germplasm.getGermplasmUUID() == null) {
				if (doUseUUID) {
					germplasm.setGermplasmUUID(UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					germplasm.setGermplasmUUID(cropPrefix + MID_STRING
						+ RandomStringUtils.randomAlphanumeric(SUFFIX_LENGTH));
				}
			}
		}
	}

}
