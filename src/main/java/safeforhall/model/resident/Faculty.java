package safeforhall.model.resident;

import static java.util.Objects.requireNonNull;
import static safeforhall.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Faculty {

    public static final String MESSAGE_CONSTRAINTS = "Faculty can take any values, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String faculty;

    /**
     * Constructs an {@code Faculty}.
     *
     * @param faculty A valid faculty.
     */
    public Faculty(String faculty) {
        requireNonNull(faculty);
        checkArgument(isValidAddress(faculty), MESSAGE_CONSTRAINTS);
        this.faculty = faculty;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return faculty;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Faculty // instanceof handles nulls
                && faculty.equals(((Faculty) other).faculty)); // state check
    }

    @Override
    public int hashCode() {
        return faculty.hashCode();
    }

}
