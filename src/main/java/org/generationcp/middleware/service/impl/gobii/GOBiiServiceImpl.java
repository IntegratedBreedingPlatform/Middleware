package org.generationcp.middleware.service.impl.gobii;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.GOBiiContact;
import org.generationcp.middleware.service.api.gobii.GOBiiContactDTO;
import org.generationcp.middleware.service.api.gobii.GOBiiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GOBiiServiceImpl implements GOBiiService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public GOBiiServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<GOBiiContactDTO> getAllGOBiiContacts() {
		final List<GOBiiContact> goBiiContactList = this.workbenchDaoFactory.getGOBiiContactDao().getAll();
		final List<GOBiiContactDTO> goBiiContactDTOList = new ArrayList<>();
		for (final GOBiiContact goBiiContact : goBiiContactList) {
			goBiiContactDTOList.add(new GOBiiContactDTO(goBiiContact.getId(), goBiiContact.getFirstName(), goBiiContact.getLastName()));
		}
		return goBiiContactDTOList;
	}

}
