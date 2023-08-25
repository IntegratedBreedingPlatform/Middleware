package org.generationcp.middleware.api.template;

public enum TemplateType {
    DESCRIPTORS("DESCRIPTORS");

    private final String name;

    TemplateType(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
