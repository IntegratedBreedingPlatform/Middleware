package org.generationcp.middleware.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.oms.OntologyVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.domain.oms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

/**
 * This is a temporary class to hold methods related to populating variable types on the (new) OntologyVariable.
 * These methods will be moved to proper service classes once they exist.
 *
 */
public class OntologyVariableOperations extends StandardVariableBuilder {
	
	public OntologyVariableOperations(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	//Will be used when reading OntologyVariable
	void populateVariableType(OntologyVariable var, CVTerm varTerm) throws MiddlewareQueryException {
		List<TermProperty> termProperties = createTermProperties(varTerm.getCvTermId());
		if (!termProperties.isEmpty()) {
			for(TermProperty prop : termProperties) {
				if(TermId.VARIABLE_TYPE.getId() == prop.getTypeId()) {
					var.addVariableType(VariableType.valueOf(prop.getValue()));
				}
			}			
		}
	}
	
	//Will be used when creating a new OntologyVariable or updating an existing one
	void saveOrUpdateVariableTypes(OntologyVariable stdVar) throws MiddlewareQueryException {
		List<TermProperty> existingProperties = getStandardVariableBuilder().createTermProperties(stdVar.getTerm().getId());
		Map<VariableType, TermProperty> termMap = new HashMap<VariableType, TermProperty>();
		Set<VariableType> existingVariableTypes = new HashSet<VariableType>();
		
		for (TermProperty termProperty : existingProperties) {
			if (termProperty.getTypeId() == TermId.VARIABLE_TYPE.getId()) {
				VariableType variableType = VariableType.valueOf(termProperty.getValue());
				existingVariableTypes.add(variableType);
				termMap.put(variableType, termProperty);
			}
		}
		
		int i = 0;
		for (VariableType type : stdVar.getVariableTypes()) {
			if (!existingVariableTypes.contains(type)) { //add if new
				CVTermProperty property = new CVTermProperty();
	            int nextId = getCvTermPropertyDao().getNextId("cvTermPropertyId");
	            property.setCvTermPropertyId(nextId);
	            property.setTypeId(TermId.VARIABLE_TYPE.getId());
	            property.setValue(type.toString());
	            property.setRank(i++);
	            property.setCvTermId(stdVar.getTerm().getId());
	            getCvTermPropertyDao().save(property);
			}
		}
		
		// Remove variable type properties which are not part of incoming set.
		Set<VariableType> toRemove = new HashSet<VariableType>(existingVariableTypes);
		toRemove.removeAll(stdVar.getVariableTypes());
		
		for (VariableType type : toRemove) {
			CVTermProperty cvTermProp = getCvTermPropertyDao().getById(termMap.get(type).getTermPropertyId());
			getCvTermPropertyDao().makeTransient(cvTermProp);
		}
	}

}
