
package org.generationcp.middleware.reports;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReporterFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReporterFactory.class);

	private static ReporterFactory factory;

	private static Map<String, Reporter> reportersMap;

	private ReporterFactory() {
	}

	static {
		ReporterFactory.reportersMap = new HashMap<>();
		ReporterFactory.factory = new ReporterFactory();

		try {
			ReporterFactory.initFactory();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			ReporterFactory.LOG.error("Reporter factory initialisation was not successful", e);
		}
	}

	/**
	 * Scans for subclasses of AbstractReporter and registers them in the factory
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private static void initFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException {
		Reflections reflections = new Reflections("org.generationcp.middleware.reports");
		Set<Class<? extends AbstractReporter>> classes = reflections.getSubTypesOf(AbstractReporter.class);

		for (Class<?> c : classes) {

			int classModifiers = c.getModifiers();
			if (!Modifier.toString(classModifiers).contains("abstract")) {
				Class<?> r = Class.forName(c.getName());
				Reporter instance = (Reporter) r.newInstance();
				ReporterFactory.instance().addReporter(instance);
			}
		}

	}

	/**
	 * Returns a singleton of ReporterFactory class
	 * 
	 * @return an instance of ReporterFactory
	 */
	public static ReporterFactory instance() {
		return ReporterFactory.factory;
	}

	/**
	 * Registers a Reporter in the Map of known Reporter types. The key for the map is the reporterCode from the Report being registered.
	 * 
	 * @param report
	 */
	private void addReporter(Reporter report) {
		if (ReporterFactory.reportersMap.containsKey(report.getReportCode())) {
			System.err.println("WARNINIG - ReporterFactory: overwriting report with code: " + report.getReportCode());
		}

		ReporterFactory.reportersMap.put(report.getReportCode(), report);
	}

	/**
	 * Factory method to obtain new instances of Reporter
	 * 
	 * @param reportKey
	 * @return an appropriate Reporter type for the reportKey parameter provided
	 * @throws MissingReportException when there is no Reporter for a given reportKey
	 */
	public Reporter createReporter(String reportKey) throws MissingReportException {
		if (ReporterFactory.reportersMap.containsKey(reportKey)) {
			return ReporterFactory.reportersMap.get(reportKey).createReporter();
		}

		throw new MissingReportException(reportKey);
	}

	public Set<String> getReportKeys() {
		return ReporterFactory.reportersMap.keySet();
	}

}
