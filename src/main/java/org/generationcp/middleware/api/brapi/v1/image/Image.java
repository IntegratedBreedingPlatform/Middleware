package org.generationcp.middleware.api.brapi.v1.image;

public class Image extends ImageNewRequest {

	private String imageDbId;

	public String getImageDbId() {
		return this.imageDbId;
	}

	public void setImageDbId(final String imageDbId) {
		this.imageDbId = imageDbId;
	}
}
