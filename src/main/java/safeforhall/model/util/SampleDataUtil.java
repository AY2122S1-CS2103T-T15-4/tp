package safeforhall.model.util;

import safeforhall.model.AddressBook;
import safeforhall.model.ReadOnlyAddressBook;
import safeforhall.model.event.Capacity;
import safeforhall.model.event.Event;
import safeforhall.model.event.EventDate;
import safeforhall.model.event.EventName;
import safeforhall.model.event.ResidentList;
import safeforhall.model.event.Venue;
import safeforhall.model.person.Email;
import safeforhall.model.person.Faculty;
import safeforhall.model.person.LastDate;
import safeforhall.model.person.Name;
import safeforhall.model.person.Person;
import safeforhall.model.person.Phone;
import safeforhall.model.person.Room;
import safeforhall.model.person.VaccStatus;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Room("E417"), new Phone("87438807"),
                    new Email("alexyeoh@example.com"), new VaccStatus("T"),
                    new Faculty("SoC"), new LastDate("01-10-2021"), new LastDate("10-10-2021")),
            new Person(new Name("Bernice Yu"), new Room("A213"), new Phone("99272758"),
                    new Email("berniceyu@example.com"), new VaccStatus("F"),
                    new Faculty("FASS"), new LastDate("10-10-2021"), new LastDate("11-10-2021")),
            new Person(new Name("Charlotte Oliveiro"), new Room("B423"), new Phone("93210283"),
                    new Email("charlotte@example.com"), new VaccStatus("T"),
                    new Faculty("SoC"), new LastDate("11-10-2021"), new LastDate("12-10-2021")),
            new Person(new Name("David Li"), new Room("C112"), new Phone("91031282"),
                    new Email("lidavid@example.com"), new VaccStatus("T"),
                    new Faculty("SDE"), new LastDate("02-10-2021"), new LastDate("01-10-2021")),
            new Person(new Name("Irfan Ibrahim"), new Room("D422"), new Phone("92492021"),
                    new Email("irfan@example.com"), new VaccStatus("T"),
                    new Faculty("FoE"), new LastDate("12-10-2021"), new LastDate("12-10-2021")),
            new Person(new Name("Roy Balakrishnan"), new Room("A309"), new Phone("92624417"),
                    new Email("royb@example.com"), new VaccStatus("T"),
                    new Faculty("BIZ"), new LastDate("20-10-2021"), new LastDate("21-10-2021")),
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        sampleAb.addEvent(new Event(new EventName("Powerlifting"), new EventDate("03-01-2021"),
                new Venue("Gym"), new Capacity("5"), new ResidentList("David Li",
                "David Li; Room: C112; Phone: 91031282; Email: lidavid@example.com; Vaccinated: T;"
                        + " Faculty: SDE; Last Fet Date: 02-10-2021; Last Collection Date: 01-10-2021")));
        return sampleAb;
    }
}
