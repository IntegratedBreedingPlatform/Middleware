
package org.generationcp.middleware.service.pedigree;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

/**
 * Tree structure that houses a germplasms entire ancestry.
 *
 */
public class GermplasmNode {

	/**
	 * The germplasm for which we are housing the tree
	 */
	private Germplasm germplasm;

	/**
	 * The female parent
	 */
	private GermplasmNode femaleParent;

	/**
	 * The male parent
	 */
	private GermplasmNode maleParent;

	/**
	 * The method used create this germplasm i.e method used while crossing the female and male parent.
	 */
	private Method method;

	public GermplasmNode(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public GermplasmNode getFemaleParent() {
		return this.femaleParent;
	}

	public void setFemaleParent(final GermplasmNode femaleParent) {
		this.femaleParent = femaleParent;
	}

	public GermplasmNode getMaleParent() {
		return this.maleParent;
	}

	public void setMaleParent(final GermplasmNode maleParent) {
		this.maleParent = maleParent;
	}

	public Method getMethod() {
		return this.method;
	}

	public void setMethod(final Method method) {
		this.method = method;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof GermplasmNode)) {
			return false;
		}
		final GermplasmNode castOther = (GermplasmNode) other;
		return new EqualsBuilder().append(this.germplasm, castOther.germplasm).append(this.femaleParent, castOther.femaleParent)
				.append(this.maleParent, castOther.maleParent).append(this.method, castOther.method).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.germplasm).append(this.femaleParent).append(this.maleParent).append(this.method)
				.toHashCode();
	}

}
