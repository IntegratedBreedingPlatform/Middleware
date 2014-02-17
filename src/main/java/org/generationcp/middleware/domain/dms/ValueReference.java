/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

public class ValueReference extends Reference implements Comparable<ValueReference>{

    public ValueReference(int id, String name) {
        super.setId(id);
        super.setName(name);
    }

    public ValueReference(int id, String name, String description) {
        super.setId(id);
        super.setName(name);
        super.setDescription(description);
    }

    @Override
    public int compareTo(ValueReference o) {
        return getName().compareTo(o.getName());
    }
}
