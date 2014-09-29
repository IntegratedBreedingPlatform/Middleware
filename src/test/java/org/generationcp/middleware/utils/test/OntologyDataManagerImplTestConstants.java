package org.generationcp.middleware.utils.test;

/**
 * Created by CyrusVenn on 9/29/14.
 */
public interface OntologyDataManagerImplTestConstants {
    // NEW contants used by the tests

    public static String TERM_NAME_NOT_EXISTING = "foo bar"; // this should not exist in the db
    public static String TERM_NAME_NOT_METHOD = "PANH"; // term exists but not a method
    public static String TERM_NAME_IN_CENTRAL = "Vegetative Stage"; // term does exist in central

    // OLD constants used by the tests
    public static final Integer CV_TERM_ID = 1010;
    public static final String CV_TERM_NAME = "Study Information";
    public static final Integer STD_VARIABLE_ID = 8350; // 8310;
    public static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;


}
