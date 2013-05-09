package org.generationcp.middleware.v2.domain.saver;

import java.util.List;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.Enumeration;
import org.generationcp.middleware.v2.domain.NameSynonym;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.CVTermSynonym;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;
	
	public StandardVariableSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void save(StandardVariable stdVar) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		int varId = saveCvTerm(stdVar);
		
		saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
		saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
		saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());
		saveRelationship(varId, TermId.STORED_IN.getId(), stdVar.getStoredIn());
		
		if (stdVar.getConstraints() != null) {
			saveConstraint(TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			saveConstraint(TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}
		
		saveSynonyms(varId, stdVar.getNameSynonyms());
		
		saveEnumerations(varId, stdVar.getEnumerations());
	}
	
	private int saveCvTerm(StandardVariable stdVar) throws MiddlewareQueryException {
		CVTerm cvTerm = new CVTerm();
		
		cvTerm.setCvTermId(getCvTermDao().getNegativeId("cvTermId"));
		cvTerm.setCv(CV_VARIABLES);
		cvTerm.setName(stdVar.getName());
		cvTerm.setDefinition(stdVar.getDescription());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		
		getCvTermDao().save(cvTerm);
		return cvTerm.getCvTermId();
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
	
	private void saveConstraint(int typeId, Integer constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			CVTermProperty property = new CVTermProperty();
			
			property.setCvTermPropertyId(getCvTermPropertyDao().getNegativeId("cvTermPropertyId"));
			property.setTypeId(typeId);
			property.setValue(constraintValue.toString());
			
			getCvTermPropertyDao().save(property);
		}
	}
	
	private void saveSynonyms(int varId, List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
		if (nameSynonyms != null && nameSynonyms.size() > 0) {
			for (NameSynonym nameSynonym : nameSynonyms) {
				if (!StringUtil.isEmpty(nameSynonym.getName())) {
					CVTermSynonym cvTermSynonym = new CVTermSynonym();
					
					cvTermSynonym.setCvTermSynonymId(getCvTermSynonymDao().getNegativeId("cvTermSynonymId"));
					cvTermSynonym.setCvTermId(varId);
					cvTermSynonym.setSynonym(nameSynonym.getName());
					cvTermSynonym.setTypeId(nameSynonym.getType().getId());
					
					getCvTermSynonymDao().save(cvTermSynonym);
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
