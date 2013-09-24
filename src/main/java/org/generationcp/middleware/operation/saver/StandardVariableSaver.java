/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.util.StringUtil;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;
	
	public StandardVariableSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Integer save(StandardVariable stdVar) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		CVTerm varTerm = createCvTerm(stdVar);
		int varId = varTerm.getCvTermId();
		stdVar.setId(varId);

		if (stdVar.getConstraints() != null) {
			addConstraint(varTerm, TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			addConstraint(varTerm, TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}
		
		getCvTermDao().save(varTerm);		
		
		addSynonyms(varTerm, stdVar.getNameSynonyms());

		getCvTermDao().update(varTerm);
		
		saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
		saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
		saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());
		saveRelationship(varId, TermId.STORED_IN.getId(), stdVar.getStoredIn());
		if(stdVar.getIsA()!=null) {//optional
			saveRelationship(varId, TermId.IS_A.getId(), stdVar.getIsA());
		}
		
		saveEnumerations(varId, stdVar.getEnumerations());
		
		return stdVar.getId();
	}
	
	private CVTerm createCvTerm(StandardVariable stdVar) throws MiddlewareQueryException {
		CVTerm cvTerm = new CVTerm();
		
		cvTerm.setCvTermId(getCvTermDao().getNegativeId("cvTermId"));
		cvTerm.setCv(CV_VARIABLES);
		cvTerm.setName(stdVar.getName());
		cvTerm.setDefinition(stdVar.getDescription());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		
		getCvTermDao().save(cvTerm);
		return cvTerm;
	}
	
	private void saveRelationship(int subjectId, int typeId, Term object) throws MiddlewareQueryException {
		if (object != null) {
			saveCvTermRelationship(subjectId, typeId, object.getId());
			
		} else {
			throw new MiddlewareQueryException("The CvTermRelationship field is required for " + subjectId + " with relationship type of " + typeId);
		}
	}
	
	private void saveCvTermRelationship(int subjectId, int typeId, int objectId) throws MiddlewareQueryException {
		CVTermRelationship relationship = new CVTermRelationship();
		
		relationship.setCvTermRelationshipId(getCvTermRelationshipDao().getNegativeId("cvTermRelationshipId"));
		relationship.setSubjectId(subjectId);
		relationship.setTypeId(typeId);
		relationship.setObject(objectId);
		
		getCvTermRelationshipDao().save(relationship);
	}
	
	private void addConstraint(CVTerm varTerm, int typeId, Integer constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			CVTermProperty property = new CVTermProperty();
			
			property.setCvTermPropertyId(getCvTermPropertyDao().getNegativeId("cvTermPropertyId"));
			property.setTypeId(typeId);
			property.setValue(constraintValue.toString());
			property.setRank(0);
			property.setCvTerm(varTerm);
			
			if (varTerm.getProperties() == null) {
				varTerm.setProperties(new ArrayList<CVTermProperty>());
			}
			varTerm.getProperties().add(property);
		}
	}
	
	private void addSynonyms(CVTerm varTerm, List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
		if (nameSynonyms != null && nameSynonyms.size() > 0) {
			for (NameSynonym nameSynonym : nameSynonyms) {
				if (!StringUtil.isEmpty(nameSynonym.getName())) {
					CVTermSynonym cvTermSynonym = new CVTermSynonym();
					
					cvTermSynonym.setCvTermSynonymId(getCvTermSynonymDao().getNegativeId("cvTermSynonymId"));
					cvTermSynonym.setSynonym(nameSynonym.getName());
					cvTermSynonym.setTypeId(nameSynonym.getType().getId());
					cvTermSynonym.setCvTerm(varTerm);
					
					if (varTerm.getSynonyms() == null) {
						varTerm.setSynonyms(new ArrayList<CVTermSynonym>());
					}
					varTerm.getSynonyms().add(cvTermSynonym);
				}
			}
		}
	}
	
	private void saveEnumerations(int varId, List<Enumeration> enumerations) throws MiddlewareQueryException {
		if (enumerations != null && enumerations.size() > 0) {
			for (Enumeration enumeration : enumerations) {
				saveCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
			}
		}
	}
}
