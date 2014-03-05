/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.utils.test;

import java.lang.reflect.Field;

public class Debug {

    public static void println(int indent, String s) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(s);
    }

    public static void print(int indent, String s) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.print(s);
    }
    
    public static void printObject(int indent, Object obj){
        
        // Print class name
        println(indent, obj.getClass().getSimpleName() + ": ");

        // Print fields
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // to access private fields
            try {
                println(indent + 3, field.getName() + " = "  + field.get(obj));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }        
    }
    
}
