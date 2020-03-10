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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Set of trial environments.
 */
public class TrialInstances {

	private final Set<TrialInstance> trialInstances = new LinkedHashSet<TrialInstance>();

	public void add(TrialInstance trialInstance) {
		if (trialInstance != null) {
			this.trialInstances.add(trialInstance);
		}
	}

	public void addAll(Collection<TrialInstance> trialInstances) {
		if (trialInstances != null) {
			for (TrialInstance environment : trialInstances) {
				this.add(environment);
			}
		}
	}

	public void addAll(TrialInstances trialInstances) {
		if (trialInstances != null) {
			for (TrialInstance environment : trialInstances.getTrialInstances()) {
				this.add(environment);
			}
		}
	}

	public Set<TrialInstance> getTrialInstances() {
		return this.trialInstances;
	}

	public List<Variable> getVariablesByLocalName(String localName) {
		List<Variable> vars = new ArrayList<Variable>();
		for (TrialInstance trialInstance : this.trialInstances) {
			Variable var = trialInstance.getVariables().findByLocalName(localName);
			if (var != null) {
				vars.add(var);
			}

		}
		Collections.sort(vars, new Comparator<Variable>() {

			@Override
			public int compare(Variable o1, Variable o2) {
				if (o1 == null || o2 == null) {
					return o1 == null ? -1 : 1;
				}

				if (o1.getValue() == null && o2.getValue() == null) {
					return 0;
				}

				if (o1.getValue() == null || o2.getValue() == null) {
					return o1.getValue() == null ? -1 : 1;
				}

				return o1.getValue().compareTo(o2.getValue());
			}
		});

		return vars;
	}

	public TrialInstance findOnlyOneByLocalName(String localName, String value) {
		TrialInstance found = null;
		for (TrialInstance trialInstance : this.trialInstances) {
			if (trialInstance.containsValueByLocalName(localName, value)) {
				if (found == null) {
					found = trialInstance;
				} else {
					found = null;
					break;
				}
			}
		}
		return found;
	}

	public int countByLocalName(String localName, String value) {
		int count = 0;
		for (TrialInstance trialInstance : this.trialInstances) {
			if (trialInstance.containsValueByLocalName(localName, value)) {
				count++;
			}
		}
		return count;
	}

	public void print(int indent) {
		for (TrialInstance trialInstance : this.trialInstances) {
			trialInstance.print(indent);
		}
	}

	public int size() {
		return this.trialInstances != null ? this.trialInstances.size() : 0;
	}
}
