package safeforhall.logic.commands.view;

import static safeforhall.logic.commands.CommandTestUtil.assertCommandSuccess;
import static safeforhall.logic.commands.CommandTestUtil.showPersonAtIndex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import safeforhall.model.Model;
import safeforhall.model.ModelManager;
import safeforhall.model.UserPrefs;
import safeforhall.testutil.TypicalIndexes;
import safeforhall.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ViewCommand.
 */
public class ViewEventCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ViewEventCommand(), model, ViewEventCommand.MESSAGE_SUCCESS, expectedModel);
    }

    //TODO: after Rebecca's code is merged
    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, TypicalIndexes.INDEX_FIRST_PERSON);
        assertCommandSuccess(new ViewEventCommand(), model, ViewEventCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
