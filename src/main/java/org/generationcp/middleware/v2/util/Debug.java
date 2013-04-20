package org.generationcp.middleware.v2.util;

public class Debug {

	public static void println(int index, String s) {
		for (int i = 0; i < index; i++) {
			System.out.print(" ");
		}
		System.out.println(s);
	}
}
