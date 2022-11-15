/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.ruleengine.newnaming.service;

import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;

import java.util.List;

/**
 *
 * Service for Rules Based Naming.
 *
 */
public interface NamingConventionService {

	void generateAdvanceListName(List<AdvancingSource> advancingSources) throws RuleException;

}
