package org.generationcp.middleware.utils.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is helper class to generate row id for unit test. So for each dao class, the number sequence is dedicated.
 */
public final class UnitTestDaoIDGenerator {

	private static final ConcurrentHashMap<Class, AtomicInteger> mapper = new ConcurrentHashMap<>();

	private UnitTestDaoIDGenerator() {
	}

	//Initialize with higher value
	public static int generateId(Class _class) {
		mapper.putIfAbsent(_class, new AtomicInteger(10000000));
		return mapper.get(_class).getAndIncrement();
	}
}
