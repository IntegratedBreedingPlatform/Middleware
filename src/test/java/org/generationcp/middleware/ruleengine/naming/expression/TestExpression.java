
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestExpression {

	public static final Logger LOG = LoggerFactory.getLogger(TestExpression.class);

	public void printResult(List<StringBuilder> values, AdvancingSource source) {
		LOG.debug("DESIG = " + source.getGermplasm().getDesig());
		LOG.debug("RESULTS=");
		for (StringBuilder value : values) {
			LOG.debug("\t" + value);
		}
	}

	public String buildResult(List<StringBuilder> values) {
		String result = "";
		for (StringBuilder value : values) {
			result = result + value;
		}
		return result;
	}

	public AdvancingSource createAdvancingSourceTestData(final Method method,final ImportedGermplasm germplasm, final String name, final String season,
		final String studyName) {
		final List<Name> names = new ArrayList<>();
		names.add(new Name(1, new Germplasm(1), 3, 0, name + "_three", 0, 0, 0));
		names.add(new Name(1, new Germplasm(1), 5, 0, name + "_five", 0, 0, 0));
		names.add(new Name(1, new Germplasm(1), 2, 1, name + "_two", 0, 0, 0));

		final AdvancingSource source = new AdvancingSource(germplasm, names, 2, method, false, studyName, "1");
		source.setRootName(name);
		source.setSeason(season);
		return source;

	}

	public AdvancingSource createAdvancingSourceTestData(String name, String separator, String prefix, String count, String suffix,
			boolean isBulking) {

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

		final ImportedGermplasm germplasm = new ImportedGermplasm();
		germplasm.setDesig(name);
		final List<Name> names = new ArrayList<Name>();
		names.add(new Name(1, new Germplasm(1), 3, 0, name + "_three", 0, 0, 0));
		names.add(new Name(1, new Germplasm(1), 5, 0, name + "_five", 0, 0, 0));
		names.add(new Name(1, new Germplasm(1), 2, 1, name + "_two", 0, 0, 0));

		final AdvancingSource source = new AdvancingSource(germplasm, names, 2, method, false, "MNL", "1");
		source.setRootName(name);
		source.setSeason("Dry");
		source.setStudyName("NurseryTest");
		return source;
	}

	public List<StringBuilder> createInitialValues(AdvancingSource source) {
		final List<StringBuilder> builders = new ArrayList<>();

		final StringBuilder builder = new StringBuilder();
		builder.append(source.getGermplasm().getDesig()).append(this.getNonNullValue(source.getBreedingMethod().getSeparator()))
		.append(this.getNonNullValue(source.getBreedingMethod().getPrefix()))
		.append(this.getNonNullValue(source.getBreedingMethod().getCount()))
		.append(this.getNonNullValue(source.getBreedingMethod().getSuffix()));
		builders.add(builder);

		return builders;
	}

	public ImportedGermplasm createImportedGermplasm(final Integer entryNumber, final String designation, final String gid, final Integer gpid1, final Integer gpid2,
													 final Integer gnpgs, final Integer mid) {
		final ImportedGermplasm germplasm = new ImportedGermplasm();
		germplasm.setEntryNumber(entryNumber);
		germplasm.setDesig(designation);
		germplasm.setGid(gid);
		germplasm.setGpid1(gpid1);
		germplasm.setGpid2(gpid2);
		germplasm.setGnpgs(gnpgs);
		germplasm.setBreedingMethodId(mid);
		return germplasm;
	}

	public Method createDerivativeMethod(final String prefix, final String count, final String suffix, final String separator, final boolean isBulking) {
		return createBreedingMethod(prefix, count, suffix, "DER", 323, "G", "UDM", "Unknown derivative method",
			"Unknown derivative method in self fertilising species: for storing historic pedigrees", separator, isBulking);
	}

	public Method createGenerativeMethod(final String prefix, final String count, final String suffix, final String separator, final boolean isBulking) {
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
}
