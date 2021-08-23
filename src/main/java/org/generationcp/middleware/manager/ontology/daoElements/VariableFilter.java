package org.generationcp.middleware.manager.ontology.daoElements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.ArrayList;
import java.util.List;

public class VariableFilter {
	private String programUuid;
	private boolean fetchAll;
	private boolean favoritesOnly;

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

	public String getProgramUuid() {
		return programUuid;
	}

	public void setProgramUuid(final String programUuid) {
		this.programUuid = programUuid;
	}

	public boolean isFetchAll() {
		return fetchAll;
	}

	public void setFetchAll(final boolean fetchAll) {
		this.fetchAll = fetchAll;
	}

	public boolean isFavoritesOnly() {
		return favoritesOnly;
	}

	public void setFavoritesOnly(final boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
	}

	public List<Integer> getMethodIds() {
		return methodIds;
	}

	public void addMethodId(final Integer id) {
		this.methodIds.add(id);
	}

	public List<Integer> getPropertyIds() {
		return propertyIds;
	}

	public void addPropertyId(final Integer id) {
		this.propertyIds.add(id);
	}

	public List<Integer> getScaleIds() {
		return scaleIds;
	}

	public void addScaleId(final Integer id) {
		this.scaleIds.add(id);
	}

	public List<Integer> getVariableIds() {
		return variableIds;
	}

	public void addVariableId(final Integer id) {
		this.variableIds.add(id);
	}

	public List<Integer> getExcludedVariableIds() {
		return excludedVariableIds;
	}

	public void addExcludedVariableId(final Integer id) {
		this.excludedVariableIds.add(id);
	}

	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	public void addDataType(final DataType dataType) {
		this.dataTypes.add(dataType);
	}

	public List<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void addVariableType(final VariableType variableType) {
		this.variableTypes.add(variableType);
	}

	public List<String> getPropertyClasses() {
		return propertyClasses;
	}

	public void addPropertyClass(final String className) {
		this.propertyClasses.add(className);
	}

	public List<String> getNames() {
		return names;
	}

	public void addName(final String name) {
		this.names.add(name);
	}

	public List<Integer> getDatasetIds() {
		return datasetIds;
	}

	public void addDatasetId(final Integer datasetId) {
		this.datasetIds.add(datasetId);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("programUuid", programUuid)
			.append("fetchAll", fetchAll)
			.append("favoritesOnly", favoritesOnly)
			.append("methodIds", methodIds)
			.append("propertyIds", propertyIds)
			.append("scaleIds", scaleIds)
			.append("variableIds", variableIds)
			.append("excludedVariableIds", excludedVariableIds)
			.append("dataTypes", dataTypes)
			.append("variableTypes", variableTypes)
			.append("propertyClasses", propertyClasses)
			.append("names", names)
			.append("datasetIds", datasetIds)
			.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		final VariableFilter that = (VariableFilter) o;

		return new EqualsBuilder().append(fetchAll, that.fetchAll).append(favoritesOnly, that.favoritesOnly)
			.append(programUuid, that.programUuid).append(methodIds, that.methodIds).append(propertyIds, that.propertyIds)
			.append(scaleIds, that.scaleIds).append(variableIds, that.variableIds).append(excludedVariableIds, that.excludedVariableIds)
			.append(dataTypes, that.dataTypes).append(variableTypes, that.variableTypes).append(propertyClasses, that.propertyClasses)
			.append(names, that.names).append(datasetIds, that.datasetIds).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(programUuid).append(fetchAll).append(favoritesOnly).append(methodIds).append(propertyIds)
			.append(scaleIds).append(variableIds).append(excludedVariableIds).append(dataTypes).append(variableTypes)
			.append(propertyClasses)
			.append(names).append(datasetIds).toHashCode();
	}
}
