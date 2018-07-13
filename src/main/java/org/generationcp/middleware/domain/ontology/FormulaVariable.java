package org.generationcp.middleware.domain.ontology;

public class FormulaVariable extends Term {

	private Integer targetTermId;

	public FormulaVariable() {
	}

	public FormulaVariable(final Integer id, final String name, final int targetTermId) {
		this.setId(id);
		this.targetTermId = targetTermId;
		this.setName(name);
	}

	public Integer getTargetTermId() {
		return targetTermId;
	}

	public void setTargetTermId(final Integer targetTermId) {
		this.targetTermId = targetTermId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.getName() == null ? 0 : this.getName().hashCode());
		result = prime * result + this.getId();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final FormulaVariable other = (FormulaVariable) obj;

		if (this.getId() != other.getId()) {
			return false;
		}

		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

}
