package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.Method;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class BreedingMethodMapper {

	public void map(final BreedingMethodNewRequest from, final Method to) {
		to.setMcode(from.getCode().toUpperCase());
		to.setMname(from.getName());
		to.setMdesc(from.getDescription());
		to.setMtype(from.getType());
		to.setMgrp(from.getGroup());
		to.setGeneq(from.getMethodClass());
		to.setMprgn(from.getNumberOfProgenitors());
		to.setLmid(Method.LOCAL_METHOD_ID_DEFAULT);
		to.setMattr(Method.METHOD_ATTRIBUTE_DEFAULT);
		to.setMfprg(Method.NO_FEMALE_PARENTS_DEFAULT);
		to.setReference(Method.METHOD_REFERENCE_DEFAULT);

		to.setSeparator(from.getSeparator());
		to.setPrefix(from.getPrefix());
		to.setCount(from.getCount());
		to.setSuffix(from.getSuffix());
	}

	public void mapForUpdate(final BreedingMethodNewRequest from, final Method to) {
			to.setMcode(from.getCode().toUpperCase());
			to.setMname(from.getName());
			to.setMdesc(from.getDescription());
			to.setMtype(from.getType());
			to.setMgrp(from.getGroup());
			to.setGeneq(from.getMethodClass());
			to.setMprgn(from.getNumberOfProgenitors());
			to.setSeparator(from.getSeparator());
			to.setPrefix(from.getPrefix());
			to.setCount(from.getCount());
			to.setSuffix(from.getSuffix());
	}
}
