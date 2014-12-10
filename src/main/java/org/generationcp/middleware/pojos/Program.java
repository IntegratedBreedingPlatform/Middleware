
package org.generationcp.middleware.pojos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.generationcp.middleware.pojos.dms.DmsProject;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "program")
public class Program {

	@Id
	@GeneratedValue
	@Column(name = "program_id")
	@Basic(optional = false)
	private Integer id;

	@Column(name = "program_name")
	@Basic(optional = false)
	private String name;

	@Column(name = "start_date")
	private Date startDate;
	
	@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "program_project", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "project_id") })
    @NotFound(action = NotFoundAction.IGNORE)
	private List<DmsProject> projects = new ArrayList<DmsProject>();
	
	@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "program_user", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    @NotFound(action = NotFoundAction.IGNORE)
	private List<User> users = new ArrayList<User>();
	
	public Program() {
	
	}

	public Program(Integer id, String name, Date startDate) {
		this.id = id;
		this.name = name;
		this.startDate = startDate;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	public List<DmsProject> getProjects() {
		return projects;
	}

	public void addProject(DmsProject project) {
		if(project != null) {
			projects.add(project);
		}
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void addUser(User user) {
		if(user != null) {
			users.add(user);
		}
	}

	@Override
	public String toString() {
		return "Program [id=" + id + ", name=" + name + ", startDate=" + startDate + "]";
	}
	
}
