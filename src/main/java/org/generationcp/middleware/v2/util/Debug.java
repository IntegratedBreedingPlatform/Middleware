package org.generationcp.middleware.v2.util;

public class Debug {

	public static void println(int indent, String s) {
		for (int i = 0; i < indent; i++) {
			System.out.print(" ");
		}
		System.out.println(s);
	}
}
