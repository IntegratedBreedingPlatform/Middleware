package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

public interface AdvancingSourceAdapter {

	Integer getOriginGermplasmGid();

	Integer getOriginGermplasmGpid1();

	Integer getOriginGermplasmGpid2();

	Integer getOriginGermplasmGnpgs();

	ObservationUnitRow getTrialInstanceObservation();

	boolean isBulkingMethod();

}
