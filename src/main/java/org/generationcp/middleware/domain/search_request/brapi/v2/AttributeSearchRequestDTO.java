package org.generationcp.middleware.domain.search_request.brapi.v2;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class AttributeSearchRequestDTO extends SearchRequestDto {

	private List<String> attributeDbIds;
	private List<String> attributeNames;
	private List<String> dataTypes;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;
	private List<String> methodDbIds;
	private List<String> ontologyDbIds;
	private int page;
	private int pageSize;
	private List<String> scaleDbIds;
	private List<String> studyDbId;
	private List<String> traitClasses;
	private List<String> traitDbIds;

	public List<String> getDataTypes() {
		return this.dataTypes;
	}

	public void setDataTypes(final List<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public List<String> getExternalReferenceIDs() {
		return this.externalReferenceIDs;
	}

	public void setExternalReferenceIDs(final List<String> externalReferenceIDs) {
		this.externalReferenceIDs = externalReferenceIDs;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getMethodDbIds() {
		return this.methodDbIds;
	}

	public void setMethodDbIds(final List<String> methodDbIds) {
		this.methodDbIds = methodDbIds;
	}

	public List<String> getAttributeDbIds() {
		return this.attributeDbIds;
	}

	public void setAttributeDbIds(final List<String> attributeDbIds) {
		this.attributeDbIds = attributeDbIds;
	}

	public List<String> getAttributeNames() {
		return this.attributeNames;
	}

	public void setAttributeNames(final List<String> attributeNames) {
		this.attributeNames = attributeNames;
	}

	public List<String> getOntologyDbIds() {
		return this.ontologyDbIds;
	}

	public void setOntologyDbIds(final List<String> ontologyDbIds) {
		this.ontologyDbIds = ontologyDbIds;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getScaleDbIds() {
		return this.scaleDbIds;
	}

	public void setScaleDbIds(final List<String> scaleDbIds) {
		this.scaleDbIds = scaleDbIds;
	}

	public List<String> getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final List<String> studyDbId) {
		this.studyDbId = studyDbId;
	}

	public List<String> getTraitClasses() {
		return this.traitClasses;
	}

	public void setTraitClasses(final List<String> traitClasses) {
		this.traitClasses = traitClasses;
	}

	public List<String> getTraitDbIds() {
		return this.traitDbIds;
	}

	public void setTraitDbIds(final List<String> traitDbIds) {
		this.traitDbIds = traitDbIds;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
