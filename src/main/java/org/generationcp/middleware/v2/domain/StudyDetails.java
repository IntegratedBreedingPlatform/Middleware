package org.generationcp.middleware.v2.domain;

public class StudyDetails {

	private Integer id;

	private String name;

	private String title;

	private String objective;

	private Integer primaryInvestigator;

	private String type;

	private Integer startDate;

	private Integer endDate;

	private Integer user;

	private Integer status;

	private Integer creationDate;
	
	public StudyDetails(){		
	}

	public StudyDetails(Integer id, String name, String title,
			String objective, Integer primaryInvestigator, String type,
			Integer startDate, Integer endDate, Integer user, Integer status,
			Integer creationDate) {
		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.objective = objective;
		this.primaryInvestigator = primaryInvestigator;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.user = user;
		this.status = status;
		this.creationDate = creationDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public Integer getPrimaryInvestigator() {
		return primaryInvestigator;
	}

	public void setPrimaryInvestigator(Integer primaryInvestigator) {
		this.primaryInvestigator = primaryInvestigator;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStartDate() {
		return startDate;
	}

	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public Integer getEndDate() {
		return endDate;
	}

	public void setEndDate(Integer endDate) {
		this.endDate = endDate;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Integer creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudyDetails other = (StudyDetails) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudyDetails [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", title=");
		builder.append(title);
		builder.append(", objective=");
		builder.append(objective);
		builder.append(", primaryInvestigator=");
		builder.append(primaryInvestigator);
		builder.append(", type=");
		builder.append(type);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", user=");
		builder.append(user);
		builder.append(", status=");
		builder.append(status);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append("]");
		return builder.toString();
	}

}
