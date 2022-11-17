package org.generationcp.middleware.ruleengine.naming.expression;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestExpression {

	public static final Logger LOG = LoggerFactory.getLogger(TestExpression.class);

	public void printResult(final List<StringBuilder> values, final AdvancingSource source) {
		LOG.debug("RESULTS=");
		for (final StringBuilder value : values) {
			LOG.debug("\t" + value);
		}
	}

	public String buildResult(final List<StringBuilder> values) {
		String result = "";
		for (final StringBuilder value : values) {
			result = new StringBuilder().append(result).append(value).toString();
		}
		return result;
	}

	public AdvancingSource createAdvancingSourceTestData(final BasicGermplasmDTO originGermplasm,
		final Method originGermplasmMethod, final Method method,
		final String name,
		final String season, final Integer plantSelected) {
		final List<BasicNameDTO> names = new ArrayList<>();
		names.add(this.createBasicNameDTO(3, 0, name + "_three"));
		names.add(this.createBasicNameDTO(5, 0, name + "_five"));
		names.add(this.createBasicNameDTO(2, 1, name + "_two"));

		final AdvancingSource source = new AdvancingSource(originGermplasm, names, new ObservationUnitRow(), new ObservationUnitRow(),
			method, originGermplasmMethod, season,
			RandomStringUtils.randomAlphabetic(10), plantSelected);
		source.setRootName(name);
		return source;
	}

	public AdvancingSource createAdvancingSourceTestData(final String name, final String separator, final String prefix, final String count,
		final String suffix, final boolean isBulking, final Integer plantSelected) {

		final Method method = new Method();
		method.setSeparator(separator);
		method.setPrefix(prefix);
		method.setCount(count);
		method.setSuffix(suffix);
		if (isBulking) {
			method.setGeneq(TermId.BULKING_BREEDING_METHOD_CLASS.getId());
		} else {
			method.setGeneq(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());
		}

		final BasicGermplasmDTO germplasm = new BasicGermplasmDTO();
		final AdvancingSource source = this.createAdvancingSourceTestData(germplasm, new Method(), method, name, "Dry", plantSelected);
		source.setRootName(name);
		return source;
	}

	public List<StringBuilder> createInitialValues(final String designation, final AdvancingSource source) {
		final List<StringBuilder> builders = new ArrayList<>();

		final StringBuilder builder = new StringBuilder();
		builder.append(designation).append(this.getNonNullValue(source.getBreedingMethod().getSeparator()))
			.append(this.getNonNullValue(source.getBreedingMethod().getPrefix()))
			.append(this.getNonNullValue(source.getBreedingMethod().getCount()))
			.append(this.getNonNullValue(source.getBreedingMethod().getSuffix()));
		builders.add(builder);

		return builders;
	}

	public BasicGermplasmDTO createBasicGermplasmDTO(final Integer gid,
		final Integer gpid1, final Integer gpid2,
		final Integer gnpgs, final Integer mid) {
		final BasicGermplasmDTO germplasm = new BasicGermplasmDTO();
		germplasm.setGid(gid);
		germplasm.setGpid1(gpid1);
		germplasm.setGpid2(gpid2);
		germplasm.setGnpgs(gnpgs);
		germplasm.setMethodId(mid);
		return germplasm;
	}

	public Method createDerivativeMethod(final String prefix, final String count, final String suffix, final String separator,
		final boolean isBulking) {
		return createBreedingMethod(prefix, count, suffix, "DER", 323, "G", "UDM", "Unknown derivative method",
			"Unknown derivative method in self fertilising species: for storing historic pedigrees", separator, isBulking);
	}

	public Method createGenerativeMethod(final String prefix, final String count, final String suffix, final String separator,
		final boolean isBulking) {
		return createBreedingMethod(prefix, count, suffix, "GEN", 1, "G", "UGM", "Unknown generative method",
			"Unknown generative method for storing historic pedigrees for self fertilizing species.", separator, isBulking);
	}

	public Method createBreedingMethod(final String prefix, final String count, final String suffix, final String mType, final Integer mid,
		final String mgrp, final String mcode, final String mname, final String mdesc, final String separator, final boolean isBulking) {
		final Method method = new Method();
		method.setMid(mid);
		method.setPrefix(prefix);
		method.setCount(count);
		method.setSuffix(suffix);
		method.setMtype(mType);
		method.setMid(mid);
		method.setMgrp(mgrp);
		method.setMcode(mcode);
		method.setMname(mname);
		method.setMdesc(mdesc);
		method.setSeparator(separator);
		if (isBulking) {
			method.setGeneq(TermId.BULKING_BREEDING_METHOD_CLASS.getId());
		} else {
			method.setGeneq(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId());
		}
		return method;
	}

	public String getNonNullValue(String value) {
		return value != null ? value : "";
	}

	private BasicNameDTO createBasicNameDTO(final Integer typeId, final Integer nstat, final String value) {
		final BasicNameDTO name = new BasicNameDTO();
		name.setGid(1);
		name.setTypeId(typeId);
		name.setNstat(nstat);
		name.setNval(value);
		return name;
	}
}
