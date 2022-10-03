package org.generationcp.middleware.ruleengine.naming.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmNamingServiceImpl implements GermplasmNamingService {

	@Autowired
	private KeySequenceRegisterService keySequenceRegisterService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Override
	public String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException {
		Integer nextNumberInSequence = this.getNextSequence(setting.getPrefix());

		final Integer optionalStartNumber = setting.getStartNumber();

		if (optionalStartNumber != null && optionalStartNumber > 0 && nextNumberInSequence > optionalStartNumber) {
			final String nextName = this.buildDesignationNameInSequence(nextNumberInSequence, setting);
			final String invalidStatingNumberErrorMessage =
				"Starting sequence number should be higher than or equal to next name in the sequence: " + nextName + ".";
			throw new InvalidGermplasmNameSettingException(invalidStatingNumberErrorMessage);
		}

		if (optionalStartNumber != null && nextNumberInSequence < optionalStartNumber) {
			nextNumberInSequence = optionalStartNumber;
		}

		return this.buildDesignationNameInSequence(nextNumberInSequence, setting);
	}

	String buildDesignationNameInSequence(final Integer number, final GermplasmNameSetting setting) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.buildPrefixString(setting));
		sb.append(this.getNumberWithLeadingZeroesAsString(number, setting.getNumOfDigits()));

		if (!StringUtils.isEmpty(setting.getSuffix())) {
			sb.append(this.buildSuffixString(setting));
		}
		return sb.toString();
	}

	String buildPrefixString(final GermplasmNameSetting setting) {
		final String prefix = !StringUtils.isEmpty(setting.getPrefix()) ? setting.getPrefix().trim() : "";
		if (setting.isAddSpaceBetweenPrefixAndCode()) {
			return prefix + " ";
		}
		return prefix;
	}

	String buildSuffixString(final GermplasmNameSetting setting) {
		final String suffix = setting.getSuffix();
		if (suffix != null) {
			if (setting.isAddSpaceBetweenSuffixAndCode()) {
				return " " + suffix.trim();
			}
			return suffix.trim();
		}
		return "";
	}

	@Override
	public String getNumberWithLeadingZeroesAsString(final Integer number, final Integer numOfDigits) {
		if (numOfDigits == null || numOfDigits <= 0) {
			return number.toString();
		}
		return String.format("%0" + numOfDigits + "d", number);
	}

	@Override
	public String generateNextNameAndIncrementSequence(final GermplasmNameSetting setting) {
		Integer nextNumberInSequence = this.getNextSequence(setting.getPrefix());
		final Integer optionalStartNumber = setting.getStartNumber();
		if (optionalStartNumber != null && nextNumberInSequence < optionalStartNumber) {
			nextNumberInSequence = optionalStartNumber;
		}
		this.keySequenceRegisterService
			.saveLastSequenceUsed(setting.getPrefix().trim(), nextNumberInSequence);

		return this.buildDesignationNameInSequence(nextNumberInSequence, setting);
	}

	@Override
	public int getNextSequence(final String keyPrefix) {
		if (!StringUtils.isEmpty(keyPrefix)) {
			final int nextSequenceNumber = this.keySequenceRegisterService.getNextSequence(keyPrefix.trim());
			if (nextSequenceNumber > 1) {
				return nextSequenceNumber;

				// If the sequence doesn't exist yet in key_sequence_register table, query in NAMES table for the latest one used
			} else {
				return Integer.valueOf(this.germplasmDataManager.getNextSequenceNumberAsString(keyPrefix.trim()));
			}
		}

		return 1;
	}

	@Override
	public int getNextNumberAndIncrementSequence(final String keyPrefix) {
		final int nextSequence = this.getNextSequence(keyPrefix);
		this.saveLastSequenceUsed(keyPrefix, nextSequence);
		return nextSequence;
	}

	@Override
	public void saveLastSequenceUsed(final String keyPrefix, final Integer lastSequenceUsed) {
		this.keySequenceRegisterService.saveLastSequenceUsed(keyPrefix, lastSequenceUsed);
	}
}
