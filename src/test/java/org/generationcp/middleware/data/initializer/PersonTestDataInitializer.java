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
		person.setTitle("Admin");
		person.setExtension("1");
		person.setFax("2");
		person.setEmail("person@blizzard.com");
		person.setNotes("notes");
		person.setContact("3");
		person.setLanguage(-1);
		person.setPhone("4");
		return person;
	}
	
	public Person createPerson(int id) {
		Person person = new Person("First Name", "Middle Name", "Last Name");
		person.setId(id);
		return person;
	}
}
