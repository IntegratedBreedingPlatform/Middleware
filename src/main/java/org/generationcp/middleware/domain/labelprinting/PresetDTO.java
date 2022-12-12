package org.generationcp.middleware.domain.labelprinting;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@AutoProperty
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(value = {@JsonSubTypes.Type(value = LabelPrintingPresetDTO.class, name = "LabelPrintingPreset")})
public class PresetDTO implements Serializable{

	public static class View {

		public static class Configuration {

		}

		public static class Qualified extends Configuration {

		}
	}


	@JsonView(View.Qualified.class)
	private Integer id;

	private String programUUID;

	private Integer toolId;

	private String toolSection;

	@JsonView(View.Qualified.class)
	private String name;

	@JsonView(View.Configuration.class)
	private String type;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getToolId() {
		return toolId;
	}

	public void setToolId(final Integer toolId) {
		this.toolId = toolId;
	}

	public String getToolSection() {
		return toolSection;
	}

	public void setToolSection(final String toolSection) {
		this.toolSection = toolSection;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
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
