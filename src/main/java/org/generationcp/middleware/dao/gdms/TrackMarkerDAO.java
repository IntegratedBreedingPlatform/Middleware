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
import org.generationcp.middleware.pojos.gdms.TrackMarker;
import org.hibernate.Session;

/**
 * DAO class for {@link TrackMarker}.
 *
 * @author Joyce Avestro
 */

public class TrackMarkerDAO extends GenericDAO<TrackMarker, Integer> {

	public TrackMarkerDAO(final Session session) {
		super(session);
	}
}
