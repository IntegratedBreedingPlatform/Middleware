package org.generationcp.middleware.service.impl.study;

public class SampleSequence {

	private Integer gid;
	private Integer plotNo;

	public Integer getGid() {
		return gid;
	}

	public Integer getPlotNo() {
		return plotNo;
	}

	public SampleSequence(final Integer gid, final Integer plotNo) {
		this.gid = gid;
		this.plotNo = plotNo;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SampleSequence))
			return false;

		final SampleSequence sequence = (SampleSequence) o;

		if (getGid() != null ? !getGid().equals(sequence.getGid()) : sequence.getGid() != null)
			return false;
		return getPlotNo() != null ? getPlotNo().equals(sequence.getPlotNo()) : sequence.getPlotNo() == null;
	}

	@Override
	public int hashCode() {
		int result = getGid() != null ? getGid().hashCode() : 0;
		result = 31 * result + (getPlotNo() != null ? getPlotNo().hashCode() : 0);
		return result;
	}

}
