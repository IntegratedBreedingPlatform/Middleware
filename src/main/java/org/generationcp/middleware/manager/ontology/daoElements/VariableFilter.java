package org.generationcp.middleware.manager.ontology.daoElements;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;

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

	public String getProgramUuid() {
		return programUuid;
	}

	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}

	public boolean isFetchAll() {
		return fetchAll;
	}

	public void setFetchAll(boolean fetchAll) {
		this.fetchAll = fetchAll;
	}

	public boolean isFavoritesOnly() {
		return favoritesOnly;
	}

	public void setFavoritesOnly(boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
	}

	public List<Integer> getMethodIds() {
		return methodIds;
	}

	public void addMethodId(Integer id){
		this.methodIds.add(id);
	}

	public List<Integer> getPropertyIds() {
		return propertyIds;
	}

	public void addPropertyId(Integer id){
		this.propertyIds.add(id);
	}

	public List<Integer> getScaleIds() {
		return scaleIds;
	}

	public void addScaleId(Integer id){
		this.scaleIds.add(id);
	}

	public List<Integer> getVariableIds() {
		return variableIds;
	}

	public void addVariableId(Integer id){
		this.variableIds.add(id);
	}

	public List<Integer> getExcludedVariableIds() {
		return excludedVariableIds;
	}

	public void addExcludedVariableId(Integer id){
		this.excludedVariableIds.add(id);
	}

	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	public void addDataType(DataType dataType){
		this.dataTypes.add(dataType);
	}

	public List<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void addVariableType(VariableType variableType){
		this.variableTypes.add(variableType);
	}

	public List<String> getPropertyClasses() {
		return propertyClasses;
	}

	public void addPropertyClass(String className){
		this.propertyClasses.add(className);
	}

	@Override
	public String toString() {
		return "VariableFilter{" +
				"programUuid='" + programUuid + '\'' +
				", fetchAll=" + fetchAll +
				", favoritesOnly=" + favoritesOnly +
				", methodIds=" + methodIds +
				", propertyIds=" + propertyIds +
				", scaleIds=" + scaleIds +
				", variableIds=" + variableIds +
				", dataTypes=" + dataTypes +
				", variableTypes=" + variableTypes +
				", propertyClasses=" + propertyClasses +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof VariableFilter))
			return false;

		VariableFilter that = (VariableFilter) o;

		if (isFetchAll() != that.isFetchAll())
			return false;
		if (isFavoritesOnly() != that.isFavoritesOnly())
			return false;
		if (getProgramUuid() != null ? !getProgramUuid().equals(that.getProgramUuid()) : that.getProgramUuid() != null)
			return false;
		if (getMethodIds() != null ? !getMethodIds().equals(that.getMethodIds()) : that.getMethodIds() != null)
			return false;
		if (getPropertyIds() != null ? !getPropertyIds().equals(that.getPropertyIds()) : that.getPropertyIds() != null)
			return false;
		if (getScaleIds() != null ? !getScaleIds().equals(that.getScaleIds()) : that.getScaleIds() != null)
			return false;
		if (getVariableIds() != null ? !getVariableIds().equals(that.getVariableIds()) : that.getVariableIds() != null)
			return false;
		if (getDataTypes() != null ? !getDataTypes().equals(that.getDataTypes()) : that.getDataTypes() != null)
			return false;
		if (getVariableTypes() != null ? !getVariableTypes().equals(that.getVariableTypes()) : that.getVariableTypes() != null)
			return false;
		return !(getPropertyClasses() != null ? !getPropertyClasses().equals(that.getPropertyClasses()) : that.getPropertyClasses() != null);
	}

	@Override
	public int hashCode() {
		int result = getProgramUuid() != null ? getProgramUuid().hashCode() : 0;
		result = 31 * result + (isFetchAll() ? 1 : 0);
		result = 31 * result + (isFavoritesOnly() ? 1 : 0);
		result = 31 * result + (getMethodIds() != null ? getMethodIds().hashCode() : 0);
		result = 31 * result + (getPropertyIds() != null ? getPropertyIds().hashCode() : 0);
		result = 31 * result + (getScaleIds() != null ? getScaleIds().hashCode() : 0);
		result = 31 * result + (getVariableIds() != null ? getVariableIds().hashCode() : 0);
		result = 31 * result + (getDataTypes() != null ? getDataTypes().hashCode() : 0);
		result = 31 * result + (getVariableTypes() != null ? getVariableTypes().hashCode() : 0);
		result = 31 * result + (getPropertyClasses() != null ? getPropertyClasses().hashCode() : 0);
		return result;
	}
}
