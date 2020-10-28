package org.generationcp.middleware.api.germplasm;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GermplasmGuidGeneratorImpl implements GermplasmGuidGenerator {

	public static final String MID_STRING = "G";
	public static final int SUFFIX_LENGTH = 15;

	@Override
	public void generateObservationUnitIds(final CropType crop, final List<Germplasm> germplasmList) {
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
