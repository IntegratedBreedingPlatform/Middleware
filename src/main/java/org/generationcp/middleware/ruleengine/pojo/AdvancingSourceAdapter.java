package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

public interface AdvancingSourceAdapter {

	// TODO: why the gid is a string?
	String getOriginGermplasmGid();

	Integer getOriginGermplasmGpid1();

	Integer getOriginGermplasmGpid2();

	Integer getOriginGermplasmGnpgs();

	String getOriginGermplasmBreedingMethodType();

	ObservationUnitRow getTrialInstanceObservation();

	boolean isBulkingMethod();

}
