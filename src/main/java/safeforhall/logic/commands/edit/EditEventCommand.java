package safeforhall.logic.commands.edit;

import static java.util.Objects.requireNonNull;
import static safeforhall.logic.parser.CliSyntax.PREFIX_TYPE_EVENT;
import static safeforhall.model.Model.PREDICATE_SHOW_ALL_EVENTS;

import java.util.List;
import java.util.Optional;

import safeforhall.commons.core.Messages;
import safeforhall.commons.core.index.Index;
import safeforhall.commons.util.CollectionUtil;
import safeforhall.logic.commands.Command;
import safeforhall.logic.commands.CommandResult;
import safeforhall.logic.commands.exceptions.CommandException;
import safeforhall.logic.parser.CliSyntax;
import safeforhall.model.Model;
import safeforhall.model.event.Capacity;
import safeforhall.model.event.Event;
import safeforhall.model.event.EventDate;
import safeforhall.model.event.EventName;
import safeforhall.model.event.Venue;

/**
 * Edits the details of an existing event in the address book.
 */
public class EditEventCommand extends Command {
    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the events identified "
            + "by the index numbers used in the displayed events list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (positive integers only) "
            + "[" + CliSyntax.PREFIX_NAME + "NAME] "
            + "[" + CliSyntax.PREFIX_DATE + "DATE] "
            + "[" + CliSyntax.PREFIX_VENUE + "VENUE] "
            + "[" + CliSyntax.PREFIX_CAPACITY + "CAPACITY] \n"
            + "Example: " + COMMAND_WORD + " 1 "
            + CliSyntax.PREFIX_DATE + "20-01-2021 "
            + CliSyntax.PREFIX_CAPACITY + "50";

    public static final String MESSAGE_EDIT_EVENT_SUCCESS = "Edited Events: \n%1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in the address book.";

    private final Index targetIndex;
    private final EditEventDescriptor editEventDescriptor;

    /**
     * @param targetIndex Index of Event in the filtered event list to edit
     * @param editEventDescriptor details to edit the event with
     */
    public EditEventCommand(Index targetIndex, EditEventDescriptor editEventDescriptor) {
        requireNonNull(targetIndex);
        requireNonNull(editEventDescriptor);

        this.targetIndex = targetIndex;
        this.editEventDescriptor = new EditEventDescriptor(editEventDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Event> lastShownList = model.getFilteredEventList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Event eventToEdit = lastShownList.get(targetIndex.getZeroBased());
        Event editedEvent = createEditedEvent(eventToEdit, editEventDescriptor);

        if (!eventToEdit.isSameEvent(editedEvent) && model.hasEvent(editedEvent)) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        }

        model.setEvent(eventToEdit, editedEvent);
        model.updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
        return new CommandResult(String.format(MESSAGE_EDIT_EVENT_SUCCESS, editedEvent));
    }

    /**
     * Creates and returns a {@code Event} with the details of {@code eventToEdit}
     * edited with {@code editEventDescriptor}.
     */
    private static Event createEditedEvent(Event eventToEdit, EditEventDescriptor editEventDescriptor) {
        assert eventToEdit != null;

        EventName updatedName = editEventDescriptor.getName().orElse(eventToEdit.getEventName());
        EventDate updatedDate = editEventDescriptor.getDate().orElse(eventToEdit.getEventDate());
        Venue updatedVenue = editEventDescriptor.getVenue().orElse(eventToEdit.getVenue());
        Capacity updatedCapacity = editEventDescriptor.getCapacity().orElse(eventToEdit.getCapacity());

        return new Event(updatedName, updatedDate, updatedVenue, updatedCapacity);
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditEventCommand)) {
            return false;
        }

        // state check
        EditEventCommand e = (EditEventCommand) other;
        return targetIndex.equals(e.targetIndex)
                && editEventDescriptor.equals(e.editEventDescriptor);
    }

    /**
     * Stores the details to edit the event with. Each non-empty field value will replace the
     * corresponding field value of the event.
     */
    public static class EditEventDescriptor {
        private EventName name;
        private EventDate date;
        private Venue venue;
        private Capacity capacity;

        public EditEventDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditEventDescriptor(EditEventDescriptor toCopy) {
            setName(toCopy.name);
            setDate(toCopy.date);
            setVenue(toCopy.venue);
            setCapacity(toCopy.capacity);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, date, venue, capacity);
        }

        public void setName(EventName name) {
            this.name = name;
        }

        public Optional<EventName> getName() {
            return Optional.ofNullable(name);
        }

        public void setDate(EventDate date) {
            this.date = date;
        }

        public Optional<EventDate> getDate() {
            return Optional.ofNullable(date);
        }

        public void setVenue(Venue venue) {
            this.venue = venue;
        }

        public Optional<Venue> getVenue() {
            return Optional.ofNullable(venue);
        }

        public void setCapacity(Capacity capacity) {
            this.capacity = capacity;
        }

        public Optional<Capacity> getCapacity() {
            return Optional.ofNullable(capacity);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditEventDescriptor)) {
                return false;
            }

            // state check
            EditEventDescriptor e = (EditEventDescriptor) other;

            return getName().equals(e.getName())
                    && getDate().equals(e.getDate())
                    && getVenue().equals(e.getVenue())
                    && getCapacity().equals(e.getCapacity());
        }
    }


}
