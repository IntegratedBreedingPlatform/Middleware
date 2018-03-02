package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "naming_config")
public class NamingConfiguration {

	@Id
	@Column(name = "id")
	private int id;

	@Basic
	@Column(name = "name")
	private String name;

	@Basic
	@Column(name = "type")
	private String type;

	@Basic
	@Column(name = "prefix")
	private String prefix;

	@Basic
	@Column(name = "suffix")
	private String suffix;

	@Basic
	@Column(name = "count")
	private String count;

	@Basic
	@Column(name = "separator")
	private String separator;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public String getCount() {
		return count;
	}

	public void setCount(final String count) {
		this.count = count;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
