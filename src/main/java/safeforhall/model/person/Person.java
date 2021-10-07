package safeforhall.model.person;

import static safeforhall.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Room room;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final VaccStatus vaccStatus;
    private final Faculty faculty;
    private final LastFetDate lastFetDate;
    private final LastCollectionDate lastCollectionDate;

    /**
     * Every field must be present and only last 3 can be null.
     */
    public Person(Name name, Room room, Phone phone, Email email, VaccStatus vaccStatus,
                    Faculty faculty, LastFetDate lastFetDate, LastCollectionDate lastCollectionDate) {
        // Optionals: faculty, lastFetDate, lastCollectionDate
        requireAllNonNull(name, room, phone, email, vaccStatus);
        this.name = name;
        this.room = room;
        this.phone = phone;
        this.email = email;
        this.vaccStatus = vaccStatus;
        this.faculty = faculty;
        this.lastFetDate = lastFetDate;
        this.lastCollectionDate = lastCollectionDate;
    }

    public Name getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public VaccStatus getVaccStatus() {
        return vaccStatus;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public LastFetDate getLastFetDate() {
        return lastFetDate;
    }

    public LastCollectionDate getLastCollectionDate() {
        return lastCollectionDate;
    }

    /**
     * Returns true if both persons have the same name and room.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName())
                && otherPerson.getRoom().equals(getRoom());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return otherPerson.getName().equals(getName())
                && otherPerson.getRoom().equals(getRoom())
                && otherPerson.getPhone().equals(getPhone())
                && otherPerson.getEmail().equals(getEmail());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, room, phone, email, vaccStatus);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append("; Room: ")
                .append(getRoom())
                .append("; Phone: ")
                .append(getPhone())
                .append("; Email: ")
                .append(getEmail())
                .append("; Vaccinated: ")
                .append(getVaccStatus())
                .append("; Faculty: ")
                .append(getFaculty());

        return builder.toString();
    }

}
