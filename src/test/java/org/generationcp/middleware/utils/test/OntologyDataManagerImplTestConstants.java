
package org.generationcp.middleware.utils.test;

/**
 * Created by CyrusVenn on 9/29/14.
 */
public interface OntologyDataManagerImplTestConstants {

	/* NEW contants used by the tests */

	// this should not exist in the db
	public static String TERM_NAME_NOT_EXISTING = "foo bar";
	public static int TERM_ID_NOT_EXISTING = 999999;

	// term exists but not a method
	public static String TERM_NAME_NOT_METHOD = "PANH";
	public static int TERM_ID_NOT_METHOD = 22066;

	// term does exist in central
	public static String TERM_NAME_IN_CENTRAL = "Applied";
	public static int TERM_ID_IN_CENTRAL = 4020;

	// term name is in synonyms
	public static String TERM_NAME_IS_IN_SYNONYMS = "NREP";

	public static String TERM_SYNONYM = "nblocks";

	// PSMT ID's
	public static int NONEXISTING_TERM_PROPERTY_ID = 50000;
	public static int EXPECTED_TERM_PROPERTY_ID = 2000;

	public static int NONEXISTING_TERM_SCALE_ID = 100000;
	public static int EXPECTED_TERM_SCALE_ID = 6015;

	public static int NONEXISTING_TERM_METHOD_ID = 100000;
	public static int EXPECTED_TERM_METHOD_ID = 4020;

	public static int NONEXISTING_TERM_TRAIT_CLASS_ID = 21744;
	public static int EXPECTED_TERM_TRAIT_CLASS_ID = 1340;

	/* OLD constants used by the tests */
	public static final Integer CV_TERM_ID = 1010;
	public static final String CV_TERM_NAME = "Study Information";
	public static final Integer STD_VARIABLE_ID = 8350; // 8310;
	public static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
	public static final int OBJECT_ID = 1340;

	public static final Integer CATEGORICAL_VARIABLE_TERM_ID = 8371;
	public static final Integer CROP_SESCND_VALID_VALUE_FROM_CENTRAL = 10290;

	public static final String[] COMMON_HEADERS = new String[] {"ENTRY_NO", "PLOT", "TRIAL", "STUDY"};
}
