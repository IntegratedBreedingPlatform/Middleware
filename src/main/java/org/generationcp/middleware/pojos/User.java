package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public User()
	{
	}
	
	public User(Integer userid)
	{
		super();
		this.userid = userid;
	}

	public User(Integer userid, Integer instalid, Integer status,
			Integer access, Integer type, String name, String password,
			Integer personid, Integer adate, Integer cdate)
	{
		super();
		this.userid = userid;
		this.instalid = instalid;
		this.status = status;
		this.access = access;
		this.type = type;
		this.name = name;
		this.password = password;
		this.personid = personid;
		this.adate = adate;
		this.cdate = cdate;
	}


	@Id
	@Basic(optional = false)
	@Column(name = "userid")
	private Integer userid;
	
	@Column(name = "instalid")
	private Integer instalid;
	
	@Column(name = "ustatus")
	private Integer status;
	
	@Column(name = "uaccess")
	private Integer access;
	
	@Column(name = "utype")
	private Integer type;
	
	@Column(name = "uname")
	private String name;

	@Column(name = "upswd")
	private String password;
	
	@Column(name = "personid")
	private Integer personid;
	
	@Column(name = "adate")
	private Integer adate;
	
	@Column(name = "cdate")
	private Integer cdate;

	@Override
	public String toString()
	{
		return "User [userid=" + userid + ", instalid=" + instalid
				+ ", status=" + status + ", access=" + access + ", type="
				+ type + ", name=" + name + ", password=" + password
				+ ", personid=" + personid + ", adate=" + adate + ", cdate="
				+ cdate + "]";
	}
	
}
