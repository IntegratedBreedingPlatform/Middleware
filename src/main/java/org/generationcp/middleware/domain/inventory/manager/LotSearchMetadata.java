package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.math.BigInteger;
import java.util.Map;

@AutoProperty
public class LotSearchMetadata {

	private Map<String, BigInteger> lotsCountByScaleName;

	public LotSearchMetadata() {
	}

	public LotSearchMetadata(final Map<String, BigInteger> lotsCountByScaleName) {
		this.lotsCountByScaleName = lotsCountByScaleName;
	}

	public Map<String, BigInteger> getLotsCountByScaleName() {
		return lotsCountByScaleName;
	}


	public void setLotsCountByScaleName(final Map<String, BigInteger> lotsCountByScaleName) {
		this.lotsCountByScaleName = lotsCountByScaleName;
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
