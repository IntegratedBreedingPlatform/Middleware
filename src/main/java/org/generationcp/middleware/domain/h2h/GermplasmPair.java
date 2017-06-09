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

package org.generationcp.middleware.domain.h2h;

import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.util.Debug;

/**
 * This class contains a pair of Germplasm IDs used in Cross-Study Queries / Head-to-Head Comparison
 *
 */
public class GermplasmPair {

	private int gid1;

	private int gid2;

	private TrialEnvironments environments;

	public GermplasmPair(int gid1, int gid2) {
		super();
		this.gid1 = gid1;
		this.gid2 = gid2;
	}

	public GermplasmPair(int gid1, int gid2, TrialEnvironments environments) {
		super();
		this.gid1 = gid1;
		this.gid2 = gid2;
		this.environments = environments;
	}

	public int getGid1() {
		return this.gid1;
	}

	public void setGid1(int gid1) {
		this.gid1 = gid1;
	}

	public int getGid2() {
		return this.gid2;
	}

	public void setGid2(int gid2) {
		this.gid2 = gid2;
	}

	public TrialEnvironments getTrialEnvironments() {
		return this.environments;
	}

	public void setTrialEnvironments(TrialEnvironments environments) {
		this.environments = environments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.gid1;
		result = prime * result + this.gid2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		GermplasmPair other = (GermplasmPair) obj;
		if (this.gid1 != other.gid1) {
			return false;
		}
		if (this.gid2 != other.gid2) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmPair [gId1=");
		builder.append(this.gid1);
		builder.append(", gId2=");
		builder.append(this.gid2);
		builder.append(", environments=");
		if (this.environments != null) {
			for (TrialEnvironment env : this.environments.getTrialEnvironments()) {
				builder.append(env.toString()).append(", ");
			}
		} else {
			builder.append(" ").append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "GermplasmPair:");
		Debug.println(indent + 3, "gid1 = " + this.gid1);
		Debug.println(indent + 3, "gid2 = " + this.gid2);
		Debug.println(indent + 3, "# common environments = " + this.environments.size());
		this.environments.print(indent + 6);
		Debug.println(indent + 3, "# common environments = " + this.environments.size());

	}

}
