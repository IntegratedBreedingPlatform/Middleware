/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.service;

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.DataImportService;

public class DataImportServiceImpl extends Service implements DataImportService {

	public DataImportServiceImpl(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	@Override
	public int saveDataset(Workbook workbook) {
		// TODO Auto-generated method stub
		return 0;
	}

}
