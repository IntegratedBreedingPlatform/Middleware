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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class TermBuilder extends Builder {

	public TermBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Term get(int termId) throws MiddlewareQueryException {
		Term term = null;
		term = TermBuilder.mapCVTermToTerm(this.getCvTermDao().getById(termId));
		return term;
	}

	public static Term mapCVTermToTerm(CVTerm cVTerm) throws MiddlewareQueryException {
		Term term = null;

		if (cVTerm != null) {
			term = new Term(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
			term.setObsolete(cVTerm.isObsolete());
			term.setVocabularyId(cVTerm.getCv());
			// No longer populate properties !! This is a major change. However, no caller was using properties anyway!
		}
		return term;
	}

	public List<Term> getTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();
		List<CVTerm> cvTerms = this.getCvTermDao().getTermsByCvId(cvId, 0, 0);
		for (CVTerm cvTerm : cvTerms) {
			terms.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}
		return terms;
	}

	public List<Term> getTermsByCvId(CvId cvId, int start, int numOfRows) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();
		List<CVTerm> cvTerms = this.getCvTermDao().getTermsByCvId(cvId, start, numOfRows);
		for (CVTerm cvTerm : cvTerms) {
			terms.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}
		return terms;
	}

	public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
		return findTermByName(name, cvId.getId());
	}

	public Term findTermByName(String name, int cvId) throws MiddlewareQueryException {
		return this.mapToTerm(this.getCvTermDao().getByNameAndCvId(name, cvId));
	}

	private Term mapToTerm(CVTerm cvTerm) {
		Term term = null;
		if (cvTerm != null) {
			term = new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
			term.setObsolete(cvTerm.isObsolete());
			term.setVocabularyId(cvTerm.getCv());
		}
		return term;
	}

	public List<Term> getTermsByIds(List<Integer> ids) throws MiddlewareQueryException {
		List<Term> terms = null;

		List<CVTerm> cvTerms = this.getCvTermDao().getByIds(ids);
		if (cvTerms != null) {
			terms = new ArrayList<Term>();
			for (CVTerm cvTerm : cvTerms) {
				Term term = TermBuilder.mapCVTermToTerm(cvTerm);
				terms.add(term);
			}
		}

		return terms;
	}

	public Term findOrSaveTermByName(String name, CvId cv) throws MiddlewareException {
		Term term = this.findTermByName(name, cv);
		if (term == null) {
			term = this.getTermSaver().save(name, name, cv);
			// assign unclassified trait class
			CVTerm cvTerm = this.getCvTermDao().getById(TermId.GENERAL_TRAIT_CLASS.getId());
			Integer typeClass = null;
			if (cvTerm != null) {
				typeClass = TermId.GENERAL_TRAIT_CLASS.getId();
			} else {
				typeClass = TermId.ONTOLOGY_TRAIT_CLASS.getId();
			}
			this.getCvTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), typeClass);
		}
		return term;
	}

	public Term getTermOfProperty(int termId, int cvId) throws MiddlewareQueryException {
		Term term = null;
		term = TermBuilder.mapCVTermToTerm(this.getCvTermDao().getTermOfProperty(termId, cvId));
		return term;
	}

	public Term getTermOfClassOfProperty(int termId, int cvId, int isATermId) throws MiddlewareQueryException {
		Term term = null;
		List<Integer> list = this.getCvTermRelationshipDao().getObjectIdByTypeAndSubject(isATermId, termId);
		// since we're getting the isA relationship, we're only expecting only one object (only hasValue has many result)
		if (list != null && !list.isEmpty()) {
			Integer objectId = list.get(0);
			term = TermBuilder.mapCVTermToTerm(this.getCvTermDao().getById(objectId));
		}
		return term;
	}

	public List<org.generationcp.middleware.domain.oms.Scale> getAllInventoryScales() throws MiddlewareQueryException {
		List<org.generationcp.middleware.domain.oms.Scale> list = 
				new ArrayList<org.generationcp.middleware.domain.oms.Scale>();
		list.addAll(this.getCvTermDao().getAllInventoryScales());
		return list;
	}
	
	public org.generationcp.middleware.domain.oms.Scale getInventoryScaleByName(final String name) throws MiddlewareQueryException {
		return this.getCvTermDao().getInventoryScaleByName(name);
	}
	
	public Term findOrSaveProperty(String name, String definition, String cropOntologyId, Set<String> traitClasses)
			throws MiddlewareException {
		Term term = this.findTermByName(name, CvId.PROPERTIES);
		if (term == null) {
			Property property = new Property();
			property.setName(name);
			property.setDefinition(definition);
			property.setCropOntologyId(cropOntologyId);
			for(String traitClass : traitClasses) {
				property.addClass(traitClass);
			}
			getOntologyPropertyDataManager().addProperty(property);
			return property;
		}
		return term;
	}
	
	public Set<String> getDefaultTraitClasses() throws MiddlewareQueryException {
		Set<String> traitClasses = new LinkedHashSet<String>();
		CVTerm cvTerm = this.getCvTermDao().getById(TermId.GENERAL_TRAIT_CLASS.getId());
		if (cvTerm != null) {
			traitClasses.add(cvTerm.getName());
		} else {
			cvTerm = this.getCvTermDao().getById(TermId.ONTOLOGY_TRAIT_CLASS.getId());
			if (cvTerm != null) {
				traitClasses.add(cvTerm.getName());
			}
		}
		return traitClasses;
	}
	
	public Term findOrSaveScale(String name, String definition, String dataTypeName, String minValue, String maxValue,
			Map<String, String> categories) throws MiddlewareException {
		Term term = this.findTermByName(name, CvId.SCALES);
		if (term == null) {
			Scale scale = new Scale();
			scale.setName(name);
			scale.setDefinition(definition);
			scale.setDataType(DataType.getByName(dataTypeName));
			scale.setMinValue(minValue);
			scale.setMaxValue(maxValue);
			if(categories!=null) {
				for (String category : categories.keySet()) {
					scale.addCategory(new TermSummary(null, category, categories.get(category)));
				}
			}
			getOntologyScaleDataManager().addScale(scale);
			return scale;
		}
		return term;
	}

	public Term findOrSaveMethod(String name, String definition) throws MiddlewareException {
		Term term = this.findTermByName(name, CvId.METHODS);
		if (term == null) {
			Method method = new Method();
			method.setName(name);
			method.setDefinition(definition);
			getOntologyMethodDataManager().addMethod(method);
			return method;
		}
		return term;
	}
}
