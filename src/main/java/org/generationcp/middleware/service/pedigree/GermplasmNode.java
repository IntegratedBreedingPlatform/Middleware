
package org.generationcp.middleware.service.pedigree;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

public class GermplasmNode {

	private Germplasm germplasm;

	private GermplasmNode femaleParent;

	private GermplasmNode maleParent;

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

	public void printTree() {
		printThisTree(this);
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof GermplasmNode))
			return false;
		GermplasmNode castOther = (GermplasmNode) other;
		return new EqualsBuilder().append(germplasm, castOther.germplasm).append(femaleParent, castOther.femaleParent)
				.append(maleParent, castOther.maleParent).append(method, castOther.method).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(germplasm).append(femaleParent).append(maleParent).append(method).toHashCode();
	}

	private void printThisTree(final GermplasmNode node) {

		final Queue<GermplasmNode> nodes = new ArrayDeque<GermplasmNode>();
		nodes.add(node);

		int currentLevel = 1;
		int depthDecreaseCounter = 1;
		int nextCounterToIncreaseLeave = 0;

		String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t";
		System.out.print("1" + tabs);
		while (!nodes.isEmpty()) {
			final GermplasmNode currentGermplasmNode = nodes.poll();
			if(currentGermplasmNode != null && currentGermplasmNode.getGermplasm() != null
					&& currentGermplasmNode.getGermplasm().getPreferredName() !=null) {
				System.out.print("\t[" + currentGermplasmNode.getGermplasm().getGid() + " "
					+ currentGermplasmNode.getGermplasm().getPreferredName().getNval() + "]");
			}
			if (currentGermplasmNode.getFemaleParent() != null) {
				nodes.add(currentGermplasmNode.getFemaleParent());
				nextCounterToIncreaseLeave++;
			}

			if (currentGermplasmNode.getMaleParent() != null) {
				nodes.add(currentGermplasmNode.getMaleParent());
				nextCounterToIncreaseLeave++;
			}

			if (--depthDecreaseCounter == 0) {
				currentLevel++;
				tabs = tabs.replaceFirst("\t", "");
				System.out.println();
				if (!nodes.isEmpty()) {
					System.out.print(currentLevel + tabs);
				}
				depthDecreaseCounter = nextCounterToIncreaseLeave;
				nextCounterToIncreaseLeave = 0;
			}

		}
	}
}
