/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.generationcp.middleware.domain.oms.TermSummary;

public class ValueReference extends Reference implements Serializable, Comparable<ValueReference> {

	private static final long serialVersionUID = 1L;

	private String key;

	public ValueReference() {
		super();
	}

	public ValueReference(final int id, final String name) {
		super.setId(id);
		this.setKey(String.valueOf(id));
		super.setName(name);
	}

	public ValueReference(final String key, final String name) {
		this.setKey(key);
		super.setName(name);
	}

	public ValueReference(final String key, final String name, final String description) {
		this.setKey(key);
		super.setName(name);
		super.setDescription(description);
	}

	public ValueReference(final int id, final String name, final String description) {
		super.setId(id);
		this.setKey(String.valueOf(id));
		super.setName(name);
		super.setDescription(description);
	}

	public ValueReference(final TermSummary termSummary) {
		super.setId(termSummary.getId());
		this.setKey(termSummary.getId().toString());
		super.setName(termSummary.getName());
		super.setDescription(termSummary.getDefinition());
	}

	@Override
	public int compareTo(final ValueReference o) {
		return this.getName().compareTo(o.getName());
	}

	/**
	 * @return the code
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Not all categorical variables found in crop database are in a=x format where 'a' is name and 'x' is description
	 * If its not in a=x format, we'll append the name to the description
	 *
	 * @return the updated description
	 */
	public String getDisplayDescription() {
		if (this.getDescription() != null && this.getDescription().split("=", 2).length != 2) {
			// description is not in a=x format we make this then name=description
			return this.getName() + "= " + this.getDescription();
		}

		return this.getDescription();
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
