
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.generationcp.middleware.pojos.User;

@Entity
@Table(name = "users_roles")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "userid", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable=false)
	private Role role;

	public UserRole() {
	}

	public UserRole(final User user, final Integer roleId) {
		this.user = user;
		this.role = new Role(roleId);
	}
	
	public UserRole(final User user, final Role role) {
		this.user = user;
		this.role = role;
	}
	
	

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserRole [User=" + this.user + ", Role=" + this.role + "]";
	}
	
	public String getCapitalizedRole() {
		return this.getRole().getCapitalizedName();
	}

}
