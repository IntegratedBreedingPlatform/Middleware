package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Person;

public class PersonTestDataInitializer {

	public Person createPerson() {
		final Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("Test");
		person.setMiddleName("");
		person.setLastName("Person");
		person.setPositionName("Admin");
		person.setTitle("Amin");
		person.setExtension("1");
		person.setFax("2");
		person.setEmail("person@blizzard.com");
		person.setNotes("notes");
		person.setContact("3");
		person.setLanguage(-1);
		person.setPhone("4");
		return person;
	}
}
