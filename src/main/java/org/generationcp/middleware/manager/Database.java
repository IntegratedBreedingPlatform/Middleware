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

package org.generationcp.middleware.manager;

/**
 * For specifying local instance or central instance of IBDB.
 *
 * @author Kevin Manansala
 *
 */
@Deprecated
// TODO Remove the enum and all its references. In merged DB world, there is not central/local.
public enum Database {
	LOCAL, CENTRAL
}
