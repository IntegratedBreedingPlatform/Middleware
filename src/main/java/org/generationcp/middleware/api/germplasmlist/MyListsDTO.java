package org.generationcp.middleware.api.germplasmlist;

import java.util.ArrayList;
import java.util.List;

public class MyListsDTO {

	private String name;
	private String type;
	private String typeName;
	private String date;
	private String folder;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getFolder() {
		return this.folder;
	}

	public void setFolder(final String folder) {
		this.folder = folder;
	}
}
