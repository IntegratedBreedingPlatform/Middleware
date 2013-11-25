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
package org.generationcp.middleware.pojos.workbench;

/**
 * The name of tools (e.g. germplasm_browser, study_browser).
 *
 */
public enum ToolName {
    // Breeding Planner native app
     breeding_planner
    
    // BreedingView native app
    ,breeding_view
    
    // fieldbook native appapp
    ,breeding_manager
    ,fieldbook
    ,ibfb_germplasm_import
    
    // MBDT native app
    ,mbdt
    
    // OptiMAS native app
    ,optimas
    
    // BreedingManager webapp
    ,bm_list_manager
    ,crossing_manager
    ,germplasm_import
    ,list_manager
    ,nursery_template_wizard
    
    // DatasetImporter webapp
    ,dataset_importer
    
    // GDMS webapp
    ,gdms
    
    // GermplasmStudyBrowser webapp
    ,germplasm_browser
    ,germplasm_list_browser
    ,germplasm_headtohead
    ,germplasm_mainheadtohead
    ,query_for_adapted_germplasm
    ,study_browser
    
    // ibpwebservice webapp
    ,ibpwebservice
    
    // not needed anymore?
    ,germplasm_phenotypic
}
