package org.generationcp.middleware.domain.germplasm.importation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonPropertyOrder({
	"connectUsing", "germplasmList"})
public class GermplasmImportRequestDto {

	public enum PedigreeConnectionType {
		NONE, GID, GUID
	}

	public GermplasmImportRequestDto() {
	}

	private PedigreeConnectionType connectUsing;
	private List<GermplasmImportDTO> germplasmList;

	public PedigreeConnectionType getConnectUsing() {
		return connectUsing;
	}

	public void setConnectUsing(final PedigreeConnectionType connectUsing) {
		this.connectUsing = connectUsing;
	}

	public List<GermplasmImportDTO> getGermplasmList() {
		return germplasmList;
	}

	public void setGermplasmList(final List<GermplasmImportDTO> germplasmList) {
		this.germplasmList = germplasmList;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
