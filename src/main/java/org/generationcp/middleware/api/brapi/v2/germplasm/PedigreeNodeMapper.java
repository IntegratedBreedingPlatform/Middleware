package org.generationcp.middleware.api.brapi.v2.germplasm;

import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Progenitor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PedigreeNodeMapper {

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	public static PedigreeNodeDTO map(final Germplasm germplasm, final boolean isIncludeParents) {
		final PedigreeNodeDTO pedigreeNodeDTO = new PedigreeNodeDTO();
		pedigreeNodeDTO.setGid(germplasm.getGid());
		pedigreeNodeDTO.setGermplasmDbId(germplasm.getGermplasmUUID());
		pedigreeNodeDTO.setBreedingMethodDbId(String.valueOf(germplasm.getMethod().getMid()));
		pedigreeNodeDTO.setBreedingMethodName(germplasm.getMethod().getMname());

		try {
			pedigreeNodeDTO.setCrossingYear(LocalDate.parse(String.valueOf(germplasm.getGdate()), dateTimeFormatter).getYear());
		} catch (final DateTimeParseException e) {
			// do nothing if gdate cannot be parsed
		}

		if (isIncludeParents) {
			pedigreeNodeDTO.setParents(createParents(germplasm));
		}
		return pedigreeNodeDTO;
	}

	private static List<PedigreeNodeReferenceDTO> createParents(final Germplasm germplasm) {
		final List<PedigreeNodeReferenceDTO> parents = new ArrayList<>();

		final PedigreeNodeReferenceDTO femalParentReference = new PedigreeNodeReferenceDTO();
		if (germplasm.getFemaleParent() != null) {
			final Germplasm femaleParent = germplasm.getFemaleParent();
			femalParentReference.setGid(femaleParent.getGid());
			femalParentReference.setGermplasmDbId(femaleParent.getGermplasmUUID());
		}

		if (germplasm.getGnpgs() > 0) {
			femalParentReference.setParentType(ParentType.FEMALE.name());
		} else {
			femalParentReference.setParentType(ParentType.POPULATION.name());
		}
		parents.add(femalParentReference);

		final PedigreeNodeReferenceDTO maleParentReference = new PedigreeNodeReferenceDTO();
		if (germplasm.getMaleParent() != null) {
			final Germplasm maleParent = germplasm.getMaleParent();
			maleParentReference.setGid(maleParent.getGid());
			maleParentReference.setGermplasmDbId(maleParent.getGermplasmUUID());
		}

		if (germplasm.getGnpgs() > 0) {
			maleParentReference.setParentType(ParentType.MALE.name());
		} else {
			maleParentReference.setParentType(ParentType.SELF.name());
		}
		parents.add(maleParentReference);

		for (final Progenitor progenitor : germplasm.getOtherProgenitors()) {
			if (progenitor.getProgenitorGermplasm() != null) {
				final PedigreeNodeReferenceDTO pedigreeNodeReferenceDTO =
					new PedigreeNodeReferenceDTO(progenitor.getProgenitorGermplasm().getGermplasmUUID(), "", ParentType.MALE.name());
				pedigreeNodeReferenceDTO.setGid(progenitor.getProgenitorGermplasm().getGid());
				parents.add(pedigreeNodeReferenceDTO);
			}
		}
		return parents;
	}

}
