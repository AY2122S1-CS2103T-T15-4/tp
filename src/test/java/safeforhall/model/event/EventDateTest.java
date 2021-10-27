package safeforhall.model.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static safeforhall.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EventDateTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventDate(null));
    }

    @Test
    public void isValidEventDate() {
        // null dates
        assertThrows(NullPointerException.class, () -> new EventDate(null));

        // invalid dates
        assertFalse(EventDate.isValidEventDate(""));
        assertFalse(EventDate.isValidEventDate("10.10.2021"));
        assertFalse(EventDate.isValidEventDate("10/10/2021"));

        // valid dates
        assertTrue(EventDate.isValidEventDate("21-10-2021"));
    }

    @Test
    public void isPast() {
        EventDate past = new EventDate("01-01-2021");
        assertTrue(past.isPast());

        EventDate past2 = new EventDate("10-12-2000");
        assertTrue(past2.isPast());
    }
}
