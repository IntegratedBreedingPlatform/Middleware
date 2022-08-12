package org.generationcp.middleware.api.breedingmethod;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BreedingMethodNewRequest {

	private String code;
	private String name;
	private String description;
	private String type;
	private String group;
	private Integer methodClass;
	private Integer numberOfProgenitors;
	private String separator;
	private String prefix;
	private String count;
	private String suffix;
	private String snameTypeCode;

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}

	public Integer getMethodClass() {
		return this.methodClass;
	}

	public void setMethodClass(final Integer methodClass) {
		this.methodClass = methodClass;
	}

	public Integer getNumberOfProgenitors() {
		return this.numberOfProgenitors;
	}

	public void setNumberOfProgenitors(final Integer numberOfProgenitors) {
		this.numberOfProgenitors = numberOfProgenitors;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getCount() {
		return count;
	}

	public void setCount(final String count) {
		this.count = count;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public String getSnameTypeCode() {
		return snameTypeCode;
	}

	public void setSnameTypeCode(final String snameTypeCode) {
		this.snameTypeCode = snameTypeCode;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
