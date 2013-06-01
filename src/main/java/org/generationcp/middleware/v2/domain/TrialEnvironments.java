package org.generationcp.middleware.v2.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrialEnvironments {

	private Set<TrialEnvironment> trialEnvironments = new HashSet<TrialEnvironment>();
	
	public void add(TrialEnvironment trialEnvironment) {
		if (trialEnvironment != null) {
		    trialEnvironments.add(trialEnvironment);
		}
	}
	
	public List<Variable> getVariablesByLocalName(String localName){
		List<Variable> vars = new ArrayList<Variable>();
		for (TrialEnvironment trialEnvironment : trialEnvironments) {
			Variable var = trialEnvironment.getVariables().findByLocalName(localName);
			if (var != null) vars.add(var);
			
		}
		Collections.sort(vars, new  Comparator<Variable>() {

			@Override
			public int compare(Variable o1, Variable o2) {
				// TODO Auto-generated method stub
				return o1.getValue().compareTo(o2.getValue());
			}
		}
		);
		
		return vars;
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
