package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ProgramDto {

	private Long id;

	private String uuid;

	private String name;

	private CropDto crop;

	public ProgramDto() {
	}

	public ProgramDto(final Long id, String uuid, final String name, final CropDto crop) {
		this.id = id;
		this.name = name;
		this.crop = crop;
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public CropDto getCrop() {
		return crop;
	}

	public void setCrop(final CropDto crop) {
		this.crop = crop;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
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
