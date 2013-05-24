package org.generationcp.middleware.v2.domain;

import java.util.HashSet;
import java.util.Set;

public class TrialEnvironments {

	private Set<TrialEnvironment> trialEnvironments = new HashSet<TrialEnvironment>();
	
	public void add(TrialEnvironment trialEnvironment) {
		if (trialEnvironment != null) {
		    trialEnvironments.add(trialEnvironment);
		}
	}
	
	public TrialEnvironment findOnlyOneByLocalName(String localName, String value) {
		TrialEnvironment found = null;
		for (TrialEnvironment trialEnvironment : trialEnvironments) {
			if (trialEnvironment.containsValueByLocalName(localName, value)) {
				if (found == null) found = trialEnvironment;
				else { found = null; break; }
			}
		}
		return found;
	}
	
	public int countByLocalName(String localName, String value) {
		int count = 0;
		for (TrialEnvironment trialEnvironment : trialEnvironments) {
			if (trialEnvironment.containsValueByLocalName(localName, value)) {
				count++;
			}
		}
		return count;
	}

	public void print(int indent) {
		for (TrialEnvironment trialEnvironment : trialEnvironments) {
			trialEnvironment.print(indent);
		}
	}
}
