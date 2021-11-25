package org.generationcp.middleware.api.inventory.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AutoProperty
public class StudyTransactionsDto extends TransactionDto {

	public StudyTransactionsDto(final Integer transactionId, final String createdByUsername, final String transactionType,
		final Double amount, final Double lotAvailableBalance, final String notes, final Date createdDate, final Integer lotId, final String lotUUID,
		final Integer gid, final String designation, final String stockId, final Integer scaleId, final String scaleName,
		final String lotStatus, final String transactionStatus, final Integer locationId, final String locationName,
		final String locationAbbr, final String comments, final String germplasmUUID) {

		super(transactionId, createdByUsername, transactionType, amount, lotAvailableBalance, notes, createdDate, lotId, lotUUID, gid, designation, stockId,
			scaleId, scaleName, lotStatus, transactionStatus, locationId, locationName, locationAbbr, comments, germplasmUUID);
		this.observationUnits = new ArrayList<>();
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ObservationUnitDto {
		private Integer ndExperimentId;
		private Integer transactionId;
		private Integer instanceNo;
		private String entryType;
		private Integer entryNo;
		private Integer plotNo;
		private Integer repNo;
		private Integer blockNo;
		private String obsUnitId;

		public Integer getNdExperimentId() {
			return this.ndExperimentId;
		}

		public void setNdExperimentId(final Integer ndExperimentId) {
			this.ndExperimentId = ndExperimentId;
		}

		public Integer getTransactionId() {
			return this.transactionId;
		}

		public void setTransactionId(final Integer transactionId) {
			this.transactionId = transactionId;
		}

		public Integer getInstanceNo() {
			return this.instanceNo;
		}

		public void setInstanceNo(final Integer instanceNo) {
			this.instanceNo = instanceNo;
		}

		public String getEntryType() {
			return this.entryType;
		}

		public void setEntryType(final String entryType) {
			this.entryType = entryType;
		}

		public Integer getEntryNo() {
			return this.entryNo;
		}

		public void setEntryNo(final Integer entryNo) {
			this.entryNo = entryNo;
		}

		public Integer getPlotNo() {
			return this.plotNo;
		}

		public void setPlotNo(final Integer plotNo) {
			this.plotNo = plotNo;
		}

		public Integer getRepNo() {
			return this.repNo;
		}

		public void setRepNo(final Integer repNo) {
			this.repNo = repNo;
		}

		public Integer getBlockNo() {
			return this.blockNo;
		}

		public void setBlockNo(final Integer blockNo) {
			this.blockNo = blockNo;
		}

		public String getObsUnitId() {
			return this.obsUnitId;
		}

		public void setObsUnitId(final String obsUnitId) {
			this.obsUnitId = obsUnitId;
		}
	}

	private List<ObservationUnitDto> observationUnits;
	private ExperimentTransactionType experimentTransactionType;

	public List<ObservationUnitDto> getObservationUnits() {
		return this.observationUnits;
	}

	public void setObservationUnits(final List<ObservationUnitDto> observationUnits) {
		this.observationUnits = observationUnits;
	}

	public ExperimentTransactionType getExperimentTransactionType() {
		return experimentTransactionType;
	}

	public void setExperimentTransactionType(final ExperimentTransactionType experimentTransactionType) {
		this.experimentTransactionType = experimentTransactionType;
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
