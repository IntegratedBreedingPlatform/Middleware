package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.Multimap;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;

public class GermplasmMethodValidator {

	public boolean isNewBreedingMethodValid(final Method oldMethod, final Method newMethod, final String germplasmIdentifier,
		final Multimap<String, Object[]> conflictErrors) {
		if (!this.isMethodTypeMatch(oldMethod.getMtype(), newMethod.getMtype())) {
			conflictErrors.put("germplasm.update.breeding.method.mismatch", new String[] {
				germplasmIdentifier,
				String.format("%s (%s)", oldMethod.getMname(), oldMethod.getMtype())});
			return false;
		} else if (!this.isValidMethodProgenitor(oldMethod, newMethod)) {
			conflictErrors.put("germplasm.update.number.of.progenitors.mismatch", new String[] {
				germplasmIdentifier});
			return false;
		}
		return true;
	}

	private boolean isMethodTypeMatch(final String oldMethodType, final String newMethodType) {
		return this.isGenerative(newMethodType) && this.isGenerative(oldMethodType)
			|| this.isMaintenanceOrDerivative(newMethodType) && this.isMaintenanceOrDerivative(oldMethodType);
	}

	private boolean isValidMethodProgenitor(final Method oldMethod, final Method newMethod) {
		return this.isMaintenanceOrDerivative(oldMethod.getMtype()) || oldMethod.getMprgn().equals(newMethod.getMprgn());
	}

	public boolean isGenerative(final String methodType) {
		return methodType.equals(MethodType.GENERATIVE.getCode());
	}

	public boolean isMaintenanceOrDerivative(final String methodType) {
		return methodType.equals(MethodType.DERIVATIVE.getCode()) || methodType.equals(MethodType.MAINTENANCE.getCode());
	}

}
