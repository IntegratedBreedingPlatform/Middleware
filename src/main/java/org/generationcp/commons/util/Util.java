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

package org.generationcp.commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Util{

    /**
     * Get the boolean value of <code>value</code>.
     * 
     * @param value
     * @return the boolean value of <code>value</code>. If <code>value</code> is
     *         null, this method returns false.
     */
    public static boolean getValue(Boolean value) {
        return getValue(value, false);
    }

    public static boolean getValue(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Test whether <code>obj</code> is equal to one of the specified objects.
     * 
     * @param obj
     * @param obj1
     * @param objs
     * @return
     */
    public static boolean isOneOf(Object obj, Object... objs) {
        if (objs == null) {
            return false;
        }

        for (Object tmp : objs) {
            if (obj.equals(tmp)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAllNull(Object... args) {
        for (Object obj : args) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether <code>value</code> is equal to all of the specified values.
     * 
     * @param value
     * @param values
     * @return
     */
    public static boolean isAllEqualTo(Double value, Double... values) {
        if (values == null) {
            return false;
        }

        for (Double val : values) {
            if (!value.equals(val)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Test whether the specified list is "empty". A <code>null</code> value is
     * considered "empty".
     * 
     * @param list
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static int max(int value1, int... values) {
        int max = value1;

        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static <T> List<T> makeReadOnlyList(T... objects) {
        if (objects == null) {
            return Collections.unmodifiableList(new ArrayList<T>());
        }

        return Arrays.asList(objects);
    }
}
