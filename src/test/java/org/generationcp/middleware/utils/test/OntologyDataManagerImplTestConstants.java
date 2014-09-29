package org.generationcp.middleware.utils.test;

/**
 * Created by CyrusVenn on 9/29/14.
 */
public interface OntologyDataManagerImplTestConstants {
    /* NEW contants used by the tests */

    // this should not exist in the db
    public static String TERM_NAME_NOT_EXISTING = "foo bar";

    // term exists but not a method
    public static String TERM_NAME_NOT_METHOD = "PANH";

    // term does exist in central
    public static String TERM_NAME_IN_CENTRAL = "Vegetative Stage";

    // term name is in synonyms
    public static String TERM_NAME_IS_IN_SYNONYMS = "Accession Name";

    // this Standard Variables (ids) should exist in the Database
    public static int NONEXISTING_STANDARD_VARIABLE = 20643;
    public static int EXPECTED_STANDARD_VARIABLE = 20954;


    /* OLD constants used by the tests */
    public static final Integer CV_TERM_ID = 1010;
    public static final String CV_TERM_NAME = "Study Information";
    public static final Integer STD_VARIABLE_ID = 8350; // 8310;
    public static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;


}
