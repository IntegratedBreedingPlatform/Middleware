package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservationLevel {

	private String levelName;

	private Integer levelOrder;

	public ObservationLevel(final Integer levelOrder, final String levelName){
		this.levelOrder = levelOrder;
		this.levelName = levelName;
	}

	public ObservationLevel(final DatasetDTO datasetDTO){
		this.levelOrder = datasetDTO.getDatasetTypeId();
		this.levelName = datasetDTO.getName();
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(final String levelName) {
		this.levelName = levelName;
	}

	public Integer getLevelOrder() {
		return this.levelOrder;
	}

	public void setLevelOrder(final Integer levelOrder) {
		this.levelOrder = levelOrder;
	}


}
