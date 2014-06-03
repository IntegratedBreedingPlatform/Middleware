package org.generationcp.middleware.domain.conformity.util;

import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.exceptions.ConformityException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class ConformityInputTransformer {

    public static final String GID_KEY = "GID";
    public static final String ALIAS_KEY = "Alias";
    public static final String LINE_KEY = "Line";
    public static final String LINE_NUMBER_KEY = "SNo";

    public UploadInput transformInput(Map<String, String>[] parentMapArray, Map<String, String>[] populationMapArray) throws ConformityException{
        if (parentMapArray == null || parentMapArray.length != 2) {
            throw new ConformityException("Expecting two parent inputs");
        }

        UploadInput input = new UploadInput();

        for (int i = 0; i < parentMapArray.length; i++) {
            Integer parentGid = Integer.parseInt(parentMapArray[i].get(GID_KEY));
            if (i == 0) {
                input.setParentAGID(parentGid);
            } else {
                input.setParentBGID(parentGid);
            }

            transformMap(parentMapArray[i], input);
        }

        for (Map<String, String> populationMap : populationMapArray) {
            transformMap(populationMap, input);
        }

        return input;

    }

    protected void transformMap(Map<String, String> dataMap, UploadInput input) {
        ConformityGermplasmInput entry = new ConformityGermplasmInput();
        String gidString = dataMap.get(GID_KEY);
        // remove each of the distinct keys from the map so that only the marker values will remain later on
        dataMap.remove(GID_KEY);

        if (gidString != null) {
            Integer gid = Integer.parseInt(gidString);
            entry.setGid(gid);
        }

        entry.setName(dataMap.get(LINE_KEY));
        dataMap.remove(LINE_KEY);

        // currently, alias is not used
        dataMap.remove(ALIAS_KEY);

        String lineNoString = dataMap.get(LINE_NUMBER_KEY);
        if (lineNoString != null) {
            Integer lineNumber = Integer.parseInt(lineNoString);
            entry.setLineNumber(lineNumber);
        }

        dataMap.remove(LINE_NUMBER_KEY);

        Map<String, String> markerValues = new HashMap<String, String>(dataMap);
        entry.setMarkerValues(markerValues);

        input.addEntry(entry);
    }
}
