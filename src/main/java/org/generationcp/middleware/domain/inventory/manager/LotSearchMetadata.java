package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.generationcp.middleware.util.serializer.MapNullKeySerializer;
import org.generationcp.middleware.util.serializer.NullKeyReplacement;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.math.BigInteger;
import java.util.Map;

@AutoProperty
public class LotSearchMetadata {

	@JsonSerialize(using = MapNullKeySerializer.class)
	@NullKeyReplacement("NULL_VALUES")
	private Map<String, BigInteger> lotsCountByUnitName;

	public LotSearchMetadata() {
	}

	public LotSearchMetadata(final Map<String, BigInteger> lotsCountByUnitName) {
		this.lotsCountByUnitName = lotsCountByUnitName;
	}

	public Map<String, BigInteger> getLotsCountByUnitName() {
		return lotsCountByUnitName;
	}


	public void setLotsCountByUnitName(final Map<String, BigInteger> lotsCountByUnitName) {
		this.lotsCountByUnitName = lotsCountByUnitName;
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
