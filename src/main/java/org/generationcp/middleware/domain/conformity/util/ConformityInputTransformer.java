
package org.generationcp.middleware.domain.conformity.util;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.exceptions.ConformityException;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public class ConformityInputTransformer {

	public static final String GID_KEY = "GID";
	public static final String ALIAS_KEY = "Alias";
	public static final String LINE_KEY = "Line";
	public static final String S_NUMBER_KEY = "SNo";

	public static UploadInput transformInput(Map<String, String>[] parentMapArray, Map<String, String>[] populationMapArray)
			throws ConformityException {
		if (parentMapArray == null || parentMapArray.length != 2) {
			throw new ConformityException("Expecting two parent inputs");
		}

		UploadInput input = new UploadInput();

		for (int i = 0; i < parentMapArray.length; i++) {
			Integer parentGid = Integer.parseInt(parentMapArray[i].get(ConformityInputTransformer.GID_KEY));
			if (i == 0) {
				input.setParentAGID(parentGid);
			} else {
				input.setParentBGID(parentGid);
			}

			ConformityInputTransformer.transformMap(parentMapArray[i], input);
		}

		for (Map<String, String> populationMap : populationMapArray) {
			ConformityInputTransformer.transformMap(populationMap, input);
		}

		return input;

	}

	protected static void transformMap(Map<String, String> dataMap, UploadInput input) {
		ConformityGermplasmInput entry = new ConformityGermplasmInput();
		String gidString = dataMap.get(ConformityInputTransformer.GID_KEY);
		// remove each of the distinct keys from the map so that only the marker values will remain later on
		dataMap.remove(ConformityInputTransformer.GID_KEY);

		if (gidString != null) {
			Integer gid = Integer.parseInt(gidString);
			entry.setGid(gid);
		}

		entry.setLine(dataMap.get(ConformityInputTransformer.LINE_KEY));
		dataMap.remove(ConformityInputTransformer.LINE_KEY);

		// currently, alias is not used
		entry.setAlias(dataMap.get(ConformityInputTransformer.ALIAS_KEY));
		dataMap.remove(ConformityInputTransformer.ALIAS_KEY);

		String lineNoString = dataMap.get(ConformityInputTransformer.S_NUMBER_KEY);
		if (lineNoString != null) {
			Integer lineNumber = Integer.parseInt(lineNoString);
			entry.setsNumber(lineNumber);
		}

		dataMap.remove(ConformityInputTransformer.S_NUMBER_KEY);

		Map<String, String> markerValues = new HashMap<String, String>(dataMap);
		entry.setMarkerValues(markerValues);

		input.addEntry(entry);
	}
}
