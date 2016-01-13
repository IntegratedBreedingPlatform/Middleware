
package org.generationcp.middleware.service.pedigree;


public class GermplasmNode {

	GermplasmDetails root;

	GermplasmNode femaleParent;

	GermplasmNode maleParent;

	public GermplasmDetails getRoot() {
		return root;
	}

	public void setRoot(GermplasmDetails root) {
		this.root = root;
	}

	public GermplasmNode getFemaleParent() {
		return femaleParent;
	}

	public void setFemaleParent(GermplasmNode femaleParent) {
		this.femaleParent = femaleParent;
	}

	public GermplasmNode getMaleParent() {
		return maleParent;
	}

	public void setMaleParent(GermplasmNode maleParent) {
		this.maleParent = maleParent;
	}

}
