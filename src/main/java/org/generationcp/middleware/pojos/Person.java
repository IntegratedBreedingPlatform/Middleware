/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.util.Util;

@Entity
@Table(name = "persons")
public class Person implements Serializable{

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
    
    @Column(name = "contact")
    private String contact;

    public Person() {
    }

    public Person(Integer id, String firstName, String lastName, String middleName, Integer instituteId, String title, String positionName,
            Integer language, String phone, String extension, String fax, String email, String notes) {
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
    
    /**
     * Create a copy of this Person object.
     * Note that this method does not copy the {@link Person#id} field.
     * 
     * @return
     */
    public Person copy() {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setMiddleName(middleName);
        person.setInstituteId(instituteId);
        person.setTitle(title);
        person.setPositionName(positionName);
        person.setLanguage(language);
        person.setPhone(phone);
        person.setExtension(extension);
        person.setFax(fax);
        person.setEmail(email);
        person.setNotes(notes);
        person.setContact(contact);
        
        return person;
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
    
    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        if(!StringUtil.isEmpty(firstName)){
            initials.append(firstName.trim().charAt(0));
        }
        if(!StringUtil.isEmpty(middleName)) {
            initials.append(middleName.trim().charAt(0));
        }
        if(!StringUtil.isEmpty(lastName)) {
            initials.append(lastName.trim().charAt(0));
        }
        
        return initials.toString().toLowerCase();
    }
    
    public String getInitialsWithTimestamp() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyMMddHHmmssSS");
        String timestamp = timestampFormat.format(currentTime);
        
        return getInitials() + timestamp;
    }
    
    public String getDisplayName() {
        String displayName = StringUtil.joinIgnoreEmpty(" "
                                                        ,firstName == null || Util.isOneOf(firstName, "-", "'-'") ? "" : firstName
                                                        ,middleName == null || Util.isOneOf(middleName, "-", "'-'") ? "" : middleName
                                                        ,lastName == null || Util.isOneOf(lastName, "-", "'-'") ? "" : lastName
                                                        );
        return displayName;
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
    
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [id=");
        builder.append(id);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", middleName=");
        builder.append(middleName);
        builder.append(", instituteId=");
        builder.append(instituteId);
        builder.append(", title=");
        builder.append(title);
        builder.append(", positionName=");
        builder.append(positionName);
        builder.append(", language=");
        builder.append(language);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", extension=");
        builder.append(extension);
        builder.append(", fax=");
        builder.append(fax);
        builder.append(", email=");
        builder.append(email);
        builder.append(", notes=");
        builder.append(notes);
        builder.append(", contact=");
        builder.append(contact);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!Person.class.isInstance(obj)) {
            return false;
        }

        Person otherObj = (Person) obj;

        return new EqualsBuilder().append(id, otherObj.id).isEquals();
    }

}
