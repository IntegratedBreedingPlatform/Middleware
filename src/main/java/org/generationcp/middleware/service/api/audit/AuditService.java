package org.generationcp.middleware.service.api.audit;

import org.generationcp.middleware.service.impl.audit.GermplasmNameChangeDTO;

import java.util.List;

public interface AuditService {

	List<GermplasmNameChangeDTO> getNameChangesByGidAndNameId(final Integer gid, Integer nameId);

}
