
package org.ibp.test.utilities;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * A helper test to test getter and setter solely for reducing noise in the test coverage.
 *
 */
public class TestGetterAndSetter {

	private static final String REGEX_ALL = ".*";
	private static final String REGEX_ESCAPE = "\\";
	final PodamFactory factory = new PodamFactoryImpl();

	public Test getTestSuite(final String testSuiteName, final String packageName) {
		try {
			final TestSuite suite = new TestSuite(testSuiteName);
			final TestGetterAndSetter testGetterAndSetter = new TestGetterAndSetter();
			final ArrayList<GetterAndSetterTestCase> testBmsApiGetterAndSetter;

			testBmsApiGetterAndSetter = testGetterAndSetter.testBmsApiGetterAndSetter(packageName);

			for (final GetterAndSetterTestCase getterAndSetterTestCase : testBmsApiGetterAndSetter) {
				suite.addTest(getterAndSetterTestCase);
			}
			return suite;
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private ArrayList<GetterAndSetterTestCase> testBmsApiGetterAndSetter(final String packageName) throws Exception {
		final List<Class<? extends Object>> findAllPojosInPackage = this.findAllPojosInPackage(packageName);
		final ArrayList<GetterAndSetterTestCase> getterAndSetterTestCases = new ArrayList<>();
		for (final Class<? extends Object> class1 : findAllPojosInPackage) {

			if (Modifier.isAbstract(class1.getModifiers())) {
				continue;
			}

			final Object source = this.factory.manufacturePojo(class1);
			final Object destination = class1.newInstance();
			this.privateCopy(destination, source);
			getterAndSetterTestCases.add(new GetterAndSetterTestCase(source, destination));
		}
		return getterAndSetterTestCases;
	}

	private Set<Class<? extends Object>> getAllClasses(final String packageName) throws Exception {
		final Set<Class<? extends Object>> allClasses = new HashSet<Class<? extends Object>>();

		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		final URL root = contextClassLoader.getResource(packageName.replace(".", "/"));

		// Look for all Java files under Project_Directory/target/classes/
		final String rootFolder = root.getFile().replace("test-classes", "classes");
		final Collection<File> files = FileUtils.listFiles(new File(rootFolder), new String[] {"class"}, true);
		// Find classes implementing ICommand.
		for (final File file : files) {

			final String className = this.getPackageNameFromFilePath(file);
			if (className.contains("$")) {
				continue;
			}

			// Need to load this class dynamically and thus call below.
			contextClassLoader.getResource(className);
			final Class<?> cls = Class.forName(className);
			if (Object.class.isAssignableFrom(cls)) {
				allClasses.add(cls);
			}
		}
		return allClasses;
	}

	private String getPackageNameFromFilePath(final File file) {
		// Remove everything from before Project_Directory/target/classes/ and replaces all / with .
		final String packageRelatedFileSuffix =
				file.getAbsolutePath().replaceAll(
						REGEX_ALL + "target" + REGEX_ESCAPE + File.separator + "classes" + REGEX_ESCAPE + File.separator, "");
		return packageRelatedFileSuffix.replace(File.separator, ".").replace(".class", "");
	}

	private void privateCopy(final Object destination, final Object source) throws Exception {
		final List<Field> fields = new ArrayList<Field>();
		this.getApplicableFields(fields, source.getClass());
		for (final Field field : fields) {

			if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
				// Skip static final fields
				continue;
			}

			if (PropertyUtils.isWriteable(source, field.getName())) {
				BeanUtils.setProperty(destination, field.getName(), PropertyUtils.getProperty(source, field.getName()));
			} else {

				if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				field.set(destination, field.get(source));
			}
		}
		final Class<?> superclass = source.getClass().getSuperclass();
		if(!superclass.equals(Object.class)) {

		}
	}

	private List<Field> getApplicableFields(List<Field> fields, final Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			fields = this.getApplicableFields(fields, type.getSuperclass());
		}

		return fields;
	}

	private List<Class<? extends Object>> findAllPojosInPackage(final String packageName) throws Exception {
		final Set<Class<? extends Object>> allClasses = this.getAllClasses(packageName);
		return ReflectionPojoUtilities.getAllPojos(allClasses);
	}

}
