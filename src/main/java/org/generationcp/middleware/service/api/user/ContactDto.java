package org.generationcp.middleware.service.api.user;

public class ContactDto {

    private Integer contactDbId;

    private String name;

    private String email;

    private String type;

    public ContactDto(final Integer contactDbId, final String name, final String email, final String type) {
        this.contactDbId = contactDbId;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public Integer getContactDbId() {
        return contactDbId;
    }

    public void setContactDbId(final Integer contactDbId) {
        this.contactDbId = contactDbId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
