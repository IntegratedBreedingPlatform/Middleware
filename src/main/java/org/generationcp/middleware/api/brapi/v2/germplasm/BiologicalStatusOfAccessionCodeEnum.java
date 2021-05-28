package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * MCPD (v2.1) (SAMPSTAT) 19. The coding scheme proposed can be used at 3 different levels of detail:
 * either by using the general codes such as 100, 200, 300, 400, or by using the more specific codes such as 110, 120, etc.
 * 100) Wild
 * 110) Natural
 * 120) Semi-natural/wild
 * 130) Semi-natural/sown
 * 200) Weedy
 * 300) Traditional cultivar/landrace
 * 400) Breeding/research material
 * 410) Breeders line
 * 411) Synthetic population
 * 412) Hybrid
 * 413) Founder stock/base population
 * 414) Inbred line (parent of hybrid cultivar)
 * 415) Segregating population
 * 416) Clonal selection
 * 420) Genetic stock
 * 421) Mutant (e.g. induced/insertion mutants, tilling populations)
 * 422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids, amphiploids)
 * 423) Other genetic stocks (e.g. mapping populations)
 * 500) Advanced or improved cultivar (conventional breeding methods)
 * 600) GMO (by genetic engineering)
 * 999) Other (Elaborate in REMARKS field)
 */
public enum BiologicalStatusOfAccessionCodeEnum {
	_100("100"),
	_110("110"),
	_120("120"),
	_130("130"),
	_200("200"),
	_300("300"),
	_400("400"),
	_410("410"),
	_411("411"),
	_412("412"),
	_413("413"),
	_414("414"),
	_415("415"),
	_416("416"),
	_420("420"),
	_421("421"),
	_422("422"),
	_423("423"),
	_500("500"),
	_600("600"),
	_999("999");

	private final String value;

	BiologicalStatusOfAccessionCodeEnum(final String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(this.value);
	}

	@JsonCreator
	public static BiologicalStatusOfAccessionCodeEnum fromValue(final String text) {
		for (final BiologicalStatusOfAccessionCodeEnum b : BiologicalStatusOfAccessionCodeEnum.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
