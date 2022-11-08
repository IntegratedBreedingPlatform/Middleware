package org.generationcp.middleware.api.nametype;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmNameTypeDTO extends GermplasmNameTypeRequestDTO {

	private Integer id;
	private Integer date;
	private String userName;

	public GermplasmNameTypeDTO() {

	}

	public GermplasmNameTypeDTO(final Integer id, final String code, final String name) {
		this.id = id;
		this.setCode(code);
		this.setName(name);
	}

	public GermplasmNameTypeDTO(final Integer id, final String code, final String name, final String description) {
		this.id = id;
		this.setCode(code);
		this.setName(name);
		this.setDescription(name);
	}

	public GermplasmNameTypeDTO(final Integer id, final String code, final String name, final String description, final String userName, final Integer date) {
		this.setId(id);
		this.setCode(code);
		this.setName(name);
		this.setDescription(description);
		this.setUserName(userName);
		this.setDate(date);
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(final Integer date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
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
