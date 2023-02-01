package org.generationcp.middleware.manager.ontology.daoElements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;

import java.util.ArrayList;
import java.util.List;

public class VariableFilter {

	private String programUuid;
	private boolean fetchAll;
	private boolean favoritesOnly;
	private boolean showObsoletes = true;
	private SqlTextFilter nameFilter;

	private final List<Integer> methodIds = new ArrayList<>();
	private final List<Integer> propertyIds = new ArrayList<>();
	private final List<Integer> scaleIds = new ArrayList<>();
	private final List<Integer> variableIds = new ArrayList<>();
	private final List<Integer> excludedVariableIds = new ArrayList<>();
	private final List<DataType> dataTypes = new ArrayList<>();
	private final List<VariableType> variableTypes = new ArrayList<>();
	private final List<String> propertyClasses = new ArrayList<>();
	private final List<String> names = new ArrayList<>();
	private final List<Integer> datasetIds = new ArrayList<>();
	private final List<String> germplasmUUIDs = new ArrayList<>();
	private final List<Integer> lotIds = new ArrayList<>();

	public String getProgramUuid() {
		return this.programUuid;
	}

	public void setProgramUuid(final String programUuid) {
		this.programUuid = programUuid;
	}

	public boolean isFetchAll() {
		return this.fetchAll;
	}

	public void setFetchAll(final boolean fetchAll) {
		this.fetchAll = fetchAll;
	}

	public boolean isFavoritesOnly() {
		return this.favoritesOnly;
	}

	public void setFavoritesOnly(final boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
	}

	public List<Integer> getMethodIds() {
		return this.methodIds;
	}

	public void addMethodId(final Integer id) {
		this.methodIds.add(id);
	}

	public List<Integer> getPropertyIds() {
		return this.propertyIds;
	}

	public void addPropertyId(final Integer id) {
		this.propertyIds.add(id);
	}

	public List<Integer> getScaleIds() {
		return this.scaleIds;
	}

	public void addScaleId(final Integer id) {
		this.scaleIds.add(id);
	}

	public List<Integer> getVariableIds() {
		return this.variableIds;
	}

	public void addVariableId(final Integer id) {
		this.variableIds.add(id);
	}

	public void addVariableIds(final List<Integer> ids) {
		this.variableIds.addAll(ids);
	}

	public List<Integer> getExcludedVariableIds() {
		return this.excludedVariableIds;
	}

	public void addExcludedVariableId(final Integer id) {
		this.excludedVariableIds.add(id);
	}

	public List<DataType> getDataTypes() {
		return this.dataTypes;
	}

	public void addDataType(final DataType dataType) {
		this.dataTypes.add(dataType);
	}

	public List<VariableType> getVariableTypes() {
		return this.variableTypes;
	}

	public void addVariableType(final VariableType variableType) {
		this.variableTypes.add(variableType);
	}

	public List<String> getPropertyClasses() {
		return this.propertyClasses;
	}

	public void addPropertyClass(final String className) {
		this.propertyClasses.add(className);
	}

	public List<String> getNames() {
		return this.names;
	}

	public void addName(final String name) {
		this.names.add(name);
	}

	public List<Integer> getDatasetIds() {
		return this.datasetIds;
	}

	public void addDatasetId(final Integer datasetId) {
		this.datasetIds.add(datasetId);
	}

	public List<String> getGermplasmUUIDs() {
		return this.germplasmUUIDs;
	}

	public void addGermplasmUUID(final String germplasmUUIDs) {
		this.germplasmUUIDs.add(germplasmUUIDs);
	}

	public List<Integer> getLotIds() {
		return this.lotIds;
	}

	public void addLotId(final Integer lotId) {
		this.lotIds.add(lotId);
	}

	public boolean isShowObsoletes() {
		return this.showObsoletes;
	}

	public void setShowObsoletes(final boolean showObsoletes) {
		this.showObsoletes = showObsoletes;
	}

	public SqlTextFilter getNameFilter() {
		return nameFilter;
	}

	public void setNameFilter(final SqlTextFilter nameFilter) {
		this.nameFilter = nameFilter;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("programUuid", this.programUuid)
			.append("fetchAll", this.fetchAll)
			.append("favoritesOnly", this.favoritesOnly)
			.append("methodIds", this.methodIds)
			.append("propertyIds", this.propertyIds)
			.append("scaleIds", this.scaleIds)
			.append("variableIds", this.variableIds)
			.append("excludedVariableIds", this.excludedVariableIds)
			.append("dataTypes", this.dataTypes)
			.append("variableTypes", this.variableTypes)
			.append("propertyClasses", this.propertyClasses)
			.append("names", this.names)
			.append("datasetIds", this.datasetIds)
			.append("germplasmUUIDs", this.germplasmUUIDs)
			.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;

		if (o == null || this.getClass() != o.getClass())
			return false;

		final VariableFilter that = (VariableFilter) o;

		return new EqualsBuilder().append(this.fetchAll, that.fetchAll).append(this.favoritesOnly, that.favoritesOnly)
			.append(this.programUuid, that.programUuid).append(this.methodIds, that.methodIds).append(this.propertyIds, that.propertyIds)
			.append(this.scaleIds, that.scaleIds).append(this.variableIds, that.variableIds).append(this.excludedVariableIds, that.excludedVariableIds)
			.append(this.dataTypes, that.dataTypes).append(this.variableTypes, that.variableTypes).append(this.propertyClasses, that.propertyClasses)
			.append(this.names, that.names).append(this.datasetIds, that.datasetIds).append(this.germplasmUUIDs, that.germplasmUUIDs).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.programUuid).append(this.fetchAll).append(this.favoritesOnly).append(this.methodIds).append(
			this.propertyIds)
			.append(this.scaleIds).append(this.variableIds).append(this.excludedVariableIds).append(this.dataTypes).append(
				this.variableTypes)
			.append(this.propertyClasses)
			.append(this.names).append(this.datasetIds).append(this.germplasmUUIDs).toHashCode();
	}

}
