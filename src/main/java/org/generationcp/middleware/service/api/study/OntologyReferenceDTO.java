package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.service.api.BrapiView;

import java.util.ArrayList;
import java.util.List;

public class OntologyReferenceDTO {

	private List<DocumentationLink> documentationLinks = new ArrayList<>();
	private String ontologyDbId;
	private String ontologyName;
	private String version;

	// Getter Methods

	public String getOntologyDbId() {
		return this.ontologyDbId;
	}

	public String getOntologyName() {
		return this.ontologyName;
	}

	public String getVersion() {
		return this.version;
	}

	public List<DocumentationLink> getDocumentationLinks() {
		return this.documentationLinks;
	}

	// Setter Methods
	public void setOntologyDbId(final String ontologyDbId) {
		this.ontologyDbId = ontologyDbId;
	}

	public void setOntologyName(final String ontologyName) {
		this.ontologyName = ontologyName;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public void setDocumentationLinks(
		final List<DocumentationLink> documentationLinks) {
		this.documentationLinks = documentationLinks;
	}

	public class DocumentationLink {

		private String ontologyURL = "https://ontology.org";
		private String type = "WEBPAGE";

		@JsonView(BrapiView.BrapiV1_3.class)
		private String url = "https://cropontology.org";

		@JsonProperty("URL")
		public String getOntologyURL() {
			return this.ontologyURL;
		}

		public void setOntologyURL(final String ontologyURL) {
			this.ontologyURL = ontologyURL;
		}

		public String getType() {
			return this.type;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public String getUrl() {
			return this.url;
		}

		public void setUrl(final String url) {
			this.url = url;
		}

	}
}
