package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.Method;

public class BreedingMethodMapper {

	public void map(final BreedingMethodNewRequest from, final Method method) {
		method.setMcode(from.getCode());
		method.setMname(from.getName());
		method.setMdesc(from.getDescription());
		method.setMtype(from.getType());
		method.setMgrp(from.getGroup());
		method.setGeneq(from.getMethodClass());
		method.setMprgn(from.getNumberOfProgenitors());
		method.setLmid(Method.LOCAL_METHOD_ID_DEFAULT);
		method.setMattr(Method.METHOD_ATTRIBUTE_DEFAULT);
		method.setMfprg(Method.NO_FEMALE_PARENTS_DEFAULT);
		method.setReference(Method.METHOD_REFERENCE_DEFAULT);

		method.setSeparator(from.getSeparator());
		method.setPrefix(from.getPrefix());
		method.setCount(from.getCount());
		method.setSuffix(from.getSuffix());
	}
}
