package org.generationcp.middleware.pojos.workbench.releasenote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "release_note")
public class ReleaseNote implements Serializable {

	private static final long serialVersionUID = 8881675178003578053L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "release_note_id")
	private Integer id;

	@Column(name = "version", nullable = false)
	private String version;

	@Column(name = "release_date", nullable = false)
	private Date releaseDate;

	@Column(name = "has_coming_soon", nullable = false)
	private boolean hasComingSoon = false;

	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	private ReleaseNote() {
	}

	public ReleaseNote(final String version, final Date releaseDate, final String fileName) {
		this.version = version;
		this.releaseDate = releaseDate;
		this.fileName = fileName;
	}

	public Integer getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public boolean getHasComingSoon() {
		return hasComingSoon;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void enable() {
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkbenchUser)) {
			return false;
		}

		final ReleaseNote otherObj = (ReleaseNote) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReleaseNote [id=");
		builder.append(this.id);
		builder.append(", version=");
		builder.append(this.version);
		builder.append(", releaseDate=");
		builder.append(this.releaseDate);
		builder.append(", hasComingSoon=");
		builder.append(this.hasComingSoon);
		builder.append(", enabled=");
		builder.append(this.enabled);

		builder.append("]");
		return builder.toString();
	}

}
