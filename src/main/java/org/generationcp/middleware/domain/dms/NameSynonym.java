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

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;

/**
 * Contains the synonym of a name.
 */
public class NameSynonym implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;

	private final NameType type;

	public NameSynonym(String name, NameType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public NameType getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NameSynonym)) {
			return false;
		}
		NameSynonym other = (NameSynonym) obj;
		return this.name.equals(other.name) && this.type == other.type;
	}

	@Override
	public String toString() {
		return "[" + this.name + ": " + this.type + "]";
	}
}
