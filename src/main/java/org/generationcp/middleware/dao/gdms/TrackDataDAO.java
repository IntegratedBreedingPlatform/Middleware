/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.gdms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.TrackData;
import org.hibernate.Session;

/**
 * DAO class for {@link TrackData}.
 *
 * @author Joyce Avestro
 */

public class TrackDataDAO extends GenericDAO<TrackData, Integer> {

	public TrackDataDAO(final Session session) {
		super(session);
	}
}
