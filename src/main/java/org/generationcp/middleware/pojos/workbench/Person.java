package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "persons")
public class Person implements Serializable {
    private static final long serialVersionUID = -3159738927364282485L;

    @Id
    @Basic(optional = false)
    @Column(name = "personid")
    private Integer id;

    @Column(name = "fname")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column(name = "ioname")
    private String middleName;

    @Column(name = "institid")
    private Integer instituteId;

    @Column(name = "ptitle")
    private String title;

    @Column(name = "poname")
    private String positionName;

    @Column(name = "plangu")
    private Integer language;

    @Column(name = "pphone")
    private String phone;

    @Column(name = "pextent")
    private String extension;

    @Column(name = "pfax")
    private String fax;

    @Column(name = "pemail")
    private String email;

    @Column(name = "pnotes")
    private String notes;

    public Person() {
    }

    public Person(Integer id, String firstName, String lastName,
	    String middleName, Integer instituteId, String title,
	    String positionName, Integer language, String phone,
	    String extension, String fax, String email, String notes) {
	super();
	this.id = id;
	this.firstName = firstName;
	this.lastName = lastName;
	this.middleName = middleName;
	this.instituteId = instituteId;
	this.title = title;
	this.positionName = positionName;
	this.language = language;
	this.phone = phone;
	this.extension = extension;
	this.fax = fax;
	this.email = email;
	this.notes = notes;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getMiddleName() {
	return middleName;
    }

    public void setMiddleName(String middleName) {
	this.middleName = middleName;
    }

    public Integer getInstituteId() {
	return instituteId;
    }

    public void setInstituteId(Integer instituteId) {
	this.instituteId = instituteId;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getPositionName() {
	return positionName;
    }

    public void setPositionName(String positionName) {
	this.positionName = positionName;
    }

    public Integer getLanguage() {
	return language;
    }

    public void setLanguage(Integer language) {
	this.language = language;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public String getExtension() {
	return extension;
    }

    public void setExtension(String extension) {
	this.extension = extension;
    }

    public String getFax() {
	return fax;
    }

    public void setFax(String fax) {
	this.fax = fax;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Override
    public String toString() {
	return "Person [id=" + id + ", firstName=" + firstName + ", lastName="
		+ lastName + ", middleName=" + middleName + ", instituteId="
		+ instituteId + ", title=" + title + ", positionName="
		+ positionName + ", language=" + language + ", phone=" + phone
		+ ", extension=" + extension + ", fax=" + fax + ", email="
		+ email + ", notes=" + notes + "]";
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!Person.class.isInstance(obj))
	    return false;

	Person otherObj = (Person) obj;

	return new EqualsBuilder().append(id, otherObj.id).isEquals();
    }

}
