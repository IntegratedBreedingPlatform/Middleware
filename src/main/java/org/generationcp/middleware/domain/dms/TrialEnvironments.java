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
public class TrialEnvironments {

	private final Set<TrialEnvironment> trialEnvironments = new LinkedHashSet<TrialEnvironment>();

	public void add(TrialEnvironment trialEnvironment) {
		if (trialEnvironment != null) {
			this.trialEnvironments.add(trialEnvironment);
		}
	}

	public void addAll(Collection<TrialEnvironment> trialEnvironments) {
		if (trialEnvironments != null) {
			for (TrialEnvironment environment : trialEnvironments) {
				this.add(environment);
			}
		}
	}

	public void addAll(TrialEnvironments trialEnvironments) {
		if (trialEnvironments != null) {
			for (TrialEnvironment environment : trialEnvironments.getTrialEnvironments()) {
				this.add(environment);
			}
		}
	}

	public Set<TrialEnvironment> getTrialEnvironments() {
		return this.trialEnvironments;
	}

	public List<Variable> getVariablesByLocalName(String localName) {
		List<Variable> vars = new ArrayList<Variable>();
		for (TrialEnvironment trialEnvironment : this.trialEnvironments) {
			Variable var = trialEnvironment.getVariables().findByLocalName(localName);
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

	public TrialEnvironment findOnlyOneByLocalName(String localName, String value) {
		TrialEnvironment found = null;
		for (TrialEnvironment trialEnvironment : this.trialEnvironments) {
			if (trialEnvironment.containsValueByLocalName(localName, value)) {
				if (found == null) {
					found = trialEnvironment;
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
		for (TrialEnvironment trialEnvironment : this.trialEnvironments) {
			if (trialEnvironment.containsValueByLocalName(localName, value)) {
				count++;
			}
		}
		return count;
	}

	public void print(int indent) {
		for (TrialEnvironment trialEnvironment : this.trialEnvironments) {
			trialEnvironment.print(indent);
		}
	}

	public int size() {
		return this.trialEnvironments != null ? this.trialEnvironments.size() : 0;
	}
}
