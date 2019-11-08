
package org.generationcp.middleware.service.impl.inventory;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.LotIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class LotIDGeneratorImpl implements LotIDGenerator {

	public static final String MID_STRING = "L";

	public static final int SUFFIX_LENGTH = 8;

	@Override
	public void generateLotIds(final CropType crop, final List<Lot> lots) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(lots));

		final boolean doUseUUID = crop.isUseUUID();
		for (final Lot lot : lots) {
			if (lot.getLotUuId() == null) {
				if (doUseUUID) {
					lot.setLotUuId(UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					lot.setLotUuId(cropPrefix + LotIDGeneratorImpl.MID_STRING
						+ RandomStringUtils.randomAlphanumeric(LotIDGeneratorImpl.SUFFIX_LENGTH));
				}
			}
		}
	}

}
