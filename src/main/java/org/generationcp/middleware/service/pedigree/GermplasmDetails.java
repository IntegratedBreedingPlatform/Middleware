
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

public class GermplasmDetails {

	private final Germplasm germplasm;
	private final Method method;
	private final Integer numberOfProgenitors;

	public GermplasmDetails(Germplasm germplasm, Method method, Integer numberOfProgenitors) {
		this.germplasm = germplasm;
		this.method = method;
		this.numberOfProgenitors = numberOfProgenitors;

	}

	public Germplasm getGermplasm() {
		return germplasm;
	}

	public Method getMethod() {
		return method;
	}

	public Integer getNumberOfProgenitors() {
		return numberOfProgenitors;
	}

}
