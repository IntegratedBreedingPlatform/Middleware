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
package org.generationcp.middleware.util;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Debug. Used to print debug information. 
 */
public class Debug {
    
    
    private static final Logger LOG = LoggerFactory.getLogger(Debug.class);

    private static StringBuffer printIndent(int indent){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        return sb;
    }
    
    /**
     * Println. Prints the given String with the given indent, followed by a newline.
     *
     * @param indent the indent
     * @param s the string to print
     */
    public static void println(int indent, String s) {
        StringBuffer sb = printIndent(indent);
        sb.append(s);
        if (LOG.isDebugEnabled()){
            LOG.debug(sb.toString());
        } else {
            System.out.println(sb.toString());
        }
    }

    /**
     * Println. Prints the obj.toString() with the given indent, followed by a newline.
     *
     * @param indent the indent
     * @param obj the object to print
     */
    public static void println(int indent, Object obj) {
        println(indent, obj.toString());
    }

    /**
     * Prints the String s with the given indent.
     *
     * @param indent the indent
     * @param s the String to print
     */
    public static void print(int indent, String s) {
        StringBuffer sb = printIndent(indent);
        sb.append(s);
        if (LOG.isDebugEnabled()){
            LOG.debug(sb.toString());
        } else {
            System.out.print(sb.toString());
        }
    }
    
    /**
     * Prints obj.toString() with the given indent.
     *
     * @param indent the indent
     * @param obj the object to print
     */
    public static void print(int indent, Object obj) {
        print(indent, obj.toString());
    }

    
    /**
     * Prints the formatted object. The class name, and each field is on one line.
     *
     * @param indent the indent
     * @param obj the object to print
     */
    public static void printFormattedObject(int indent, Object obj){
        
        // Print class name
        println(indent, obj.getClass().getSimpleName() + ": ");

        // Print fields
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // to access private fields
            try {
                println(indent + 3, field.getName() + " = "  + field.get(obj));
            } catch (IllegalArgumentException e) {
                if (LOG.isDebugEnabled()){
                    LOG.error(e.getMessage(), e);
                } else {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (LOG.isDebugEnabled()){
                    LOG.error(e.getMessage(), e);
                } else {
                    e.printStackTrace();
                }
            }
        }        
    }

    /**
     * Prints multiple formatted objects. 
     *
     * @param indent the indent
     * @param objects the objects to print
     */
    public static void printFormattedObjects(int indent, List<?> objects){
        if (objects != null && objects.size() > 0){
            for (Object obj : objects){
                printFormattedObject(indent, obj);
            }
        }
        println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
    }

    /**
     * Prints the obj.toString().
     *
     * @param indent the indent
     * @param obj the object to print
     */
    public static void printObject(int indent, Object obj) {
        println(indent, obj.toString());
    }

    /**
     * Prints the obj.toString() of the objects passed.
     *
     * @param indent the indent
     * @param objects the objects to print
     */
    public static void printObjects(int indent, List<?> objects){
        if (objects != null && objects.size() > 0){
            for (Object obj : objects){
                printObject(indent, obj);
            }
        }
        println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
    }
    
}
