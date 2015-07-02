package org.generationcp.middleware.pojos;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name = "user_program_tree_state")
public class UserProgramTreeState {
	private int userProgramTreeStateId;
	private Integer userId;
	private String programUuid;
	private String treeType;
	private String treeState;
	
	@GeneratedValue
	@Id @Column(name = "user_program_tree_state_id")
	public int getUserProgramTreeStateId() {
		return userProgramTreeStateId;
	}
	public void setUserProgramTreeStateId(int userProgramTreeStateId) {
		this.userProgramTreeStateId = userProgramTreeStateId;
	}
	@Basic @Column(name = "userid")
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	@Basic @Column(name = "program_uuid")
	public String getProgramUuid() {
		return programUuid;
	}
	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}
	@Basic @Column(name = "tree_type")
	public String getTreeType() {
		return treeType;
	}
	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}
	@Basic @Column(name = "tree_state")
	public String getTreeState() {
		return treeState;
	}
	public void setTreeState(String treeState) {
		this.treeState = treeState;
	}
	
	
}
