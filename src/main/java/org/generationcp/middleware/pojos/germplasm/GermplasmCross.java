/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.germplasm;


public class GermplasmCross implements GermplasmCrossElement {

	private static final long serialVersionUID = 7519980544099378460L;

	private GermplasmCrossElement firstParent;
	private GermplasmCrossElement secondParent;
	private int numberOfCrossesBefore; // the number of crosses before this cross

	public GermplasmCrossElement getFirstParent() {
		return this.firstParent;
	}

	public void setFirstParent(GermplasmCrossElement firstParent) {
		this.firstParent = firstParent;
	}

	public GermplasmCrossElement getSecondParent() {
		return this.secondParent;
	}

	public void setSecondParent(GermplasmCrossElement secondParent) {
		this.secondParent = secondParent;
	}

	public int getNumberOfCrossesBefore() {
		return this.numberOfCrossesBefore;
	}

	public void setNumberOfCrossesBefore(int numberOfCrossesBefore) {
		this.numberOfCrossesBefore = numberOfCrossesBefore;
	}

	@Override
	public String toString() {
		StringBuilder toreturn = new StringBuilder();

		if (this.firstParent != null) {
			toreturn.append(this.firstParent.toString());
		} else {
			toreturn.append("Unknown");
		}

		// number of slashes between first and second parent depends on the number
		// of crosses made
		if (this.numberOfCrossesBefore == 0) {
			toreturn.append("/");
		} else if (this.numberOfCrossesBefore == 1) {
			toreturn.append("//");
		} else if (this.numberOfCrossesBefore == 2) {
			toreturn.append("///");
		} else {
			toreturn.append("/");
			toreturn.append(this.numberOfCrossesBefore + 1);
			toreturn.append("/");
		}

		if (this.secondParent != null) {
			toreturn.append(this.secondParent.toString());
		} else {
			toreturn.append("Unknown");
		}

		return toreturn.toString();
	}
}
