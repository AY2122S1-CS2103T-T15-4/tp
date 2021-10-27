package safeforhall.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import safeforhall.logic.Logic;
import safeforhall.logic.commands.ClearCommand;
import safeforhall.logic.commands.CommandResult;
import safeforhall.logic.commands.DeadlineCommand;
import safeforhall.logic.commands.ExitCommand;
import safeforhall.logic.commands.HelpCommand;
import safeforhall.logic.commands.IncludeCommand;
import safeforhall.logic.commands.add.AddEventCommand;
import safeforhall.logic.commands.add.AddPersonCommand;
import safeforhall.logic.commands.delete.DeleteEventCommand;
import safeforhall.logic.commands.delete.DeletePersonCommand;
import safeforhall.logic.commands.edit.EditEventCommand;
import safeforhall.logic.commands.edit.EditPersonCommand;
import safeforhall.logic.commands.exceptions.CommandException;
import safeforhall.logic.commands.find.FindEventCommand;
import safeforhall.logic.commands.find.FindPersonCommand;
import safeforhall.logic.commands.view.ViewEventCommand;
import safeforhall.logic.commands.view.ViewPersonCommand;
import safeforhall.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;

    private ArrayList<String> historicals = new ArrayList<>();
    private int current = 0;
    private boolean isResidentTab = true;
    private String currentString = "";
    private String currentParamters = "";

    @FXML
    private TextFlow commandTextField;

    @FXML
    private TextField main;

    @FXML
    private Text suggestions;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        main.setBackground(Background.EMPTY);
        main.setPrefWidth(1600);
        suggestions.setFill(Color.GRAY);
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        main.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        handleCommandEntered();
        handleInput();
        handleHistory();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String commandText = main.getText();
                historicals.add(commandText);
                current = historicals.size() - 1;
                if (commandText.equals("")) {
                    return;
                }

                try {
                    commandExecutor.execute(commandText);
                    main.setText("");
                } catch (CommandException | ParseException e) {
                    setStyleToIndicateCommandFailure();
                }
                event.consume();
            }
        });
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        main.getStyleClass().remove(ERROR_STYLE_CLASS);
    }


    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = main.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Handles the up and down key events to re-instate past commands.
     */
    private void handleHistory() {
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (historicals.isEmpty() || !(event.getTarget() instanceof TextField)) {
                return;
            }

            // Simplified if statement not used to allow other key pressed to propagate
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                String newText;
                if (event.getCode() == KeyCode.UP) {
                    if (current > 0) {
                        current--;
                    }
                    newText = historicals.get(current);
                } else {
                    if (current < historicals.size() - 1) {
                        current++;
                        newText = historicals.get(current);
                    } else {
                        current = historicals.size();
                        newText = "";
                    }
                }
                event.consume();
                main.setText(newText);
                main.positionCaret(newText.length());
            }
        });
    }

    private void handleInput() {
        getRoot().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN) {
                currentString = main.getText();
                String[] processString = processMain(currentString.trim());
                String commandString = processString[0];
                if (processString.length > 1) {
                    currentParamters = processString[1].trim();
                } else {
                    currentParamters = "";
                }
                String suggestion = mapSuggestion(commandString.trim(), isResidentTab);
                if (!suggestion.equals("")) {
                    String displayedSuggestion = getDisplaySuggestion(suggestion);
                    suggestions.setText("\n  " + displayedSuggestion);
                } else {
                    suggestions.setText("");
                }
            }
        });
    }

    private String[] processMain(String mainText) {
        return mainText.split(" ", 2);
    }

    private String compareParts(StringBuilder stringBuilder, String[] suggestionParts, String[] parameterParts) {
        for (String suggestionPart : suggestionParts) {
            boolean isEntered = false;
            for (String parameterPart : parameterParts) {
                boolean isOneCharPrefix = parameterPart.length() > 1 && parameterPart.charAt(1) == '/'
                        && suggestionPart.substring(0, 2).equals(parameterPart.substring(0, 2));
                boolean isTwoCharPrefix = parameterPart.length() > 2 && parameterPart.charAt(2) == '/'
                        && suggestionPart.substring(0, 3).equals(parameterPart.substring(0, 3));
                boolean isIndex = (suggestionPart.equals("INDEXES") || suggestionPart.equals("INDEX")
                        || suggestionPart.equals("[INDEXES]") || suggestionPart.equals("[INDEX]"))
                        && parameterPart.matches("\\d+");
                boolean isLateKeyword = parameterPart.contains("k/l") && suggestionPart.equals("d2/DATE");
                boolean isOptionalOneCharPrefix = suggestionPart.charAt(0) == '['
                        && parameterPart.length() > 1
                        && parameterPart.charAt(1) == '/'
                        && suggestionPart.substring(1, 3).equals(parameterPart.substring(0, 2));
                boolean isOptionalTwoCharPrefix = suggestionPart.charAt(0) == '['
                        && parameterPart.length() > 2
                        && parameterPart.charAt(2) == '/'
                        && suggestionPart.substring(1, 4).equals(parameterPart.substring(0, 3));

                if (isOneCharPrefix) {
                    isEntered = true;
                    break;
                } else if (isTwoCharPrefix) {
                    isEntered = true;
                    break;
                } else if (isIndex) {
                    isEntered = true;
                    break;
                } else if (isLateKeyword) {
                    isEntered = true;
                } else if (isOptionalOneCharPrefix) {
                    isEntered = true;
                    break;
                } else if (isOptionalTwoCharPrefix) {
                    isEntered = true;
                    break;
                }
            }
            if (!isEntered) {
                stringBuilder.append(suggestionPart).append(" ");
            }
        }
        return stringBuilder.toString();
    }

    private String getDisplaySuggestion(String suggestion) {
        if (currentParamters.equals("")) {
            return suggestion;
        } else {
            StringBuilder displayedSuggestion = new StringBuilder();
            String[] suggestionParts = suggestion.split(" ");
            String[] parameterParts = currentParamters.split(" ");

            return compareParts(displayedSuggestion, suggestionParts, parameterParts);
        }
    }

    private String mapSuggestion(String currentString, boolean isResidentTab) {
        if (isResidentTab) {
            switch (currentString) {
            case AddPersonCommand.COMMAND_WORD:
                return AddPersonCommand.PARAMETERS;
            case DeletePersonCommand.COMMAND_WORD:
                return DeletePersonCommand.PARAMETERS;
            case EditPersonCommand.COMMAND_WORD:
                return EditPersonCommand.PARAMETERS;
            case ViewPersonCommand.COMMAND_WORD:
                return ViewPersonCommand.PARAMETERS;
            case ClearCommand.COMMAND_WORD:
                return ClearCommand.PARAMETERS;
            case ExitCommand.COMMAND_WORD:
                return ExitCommand.PARAMETERS;
            case FindPersonCommand.COMMAND_WORD:
                return FindPersonCommand.PARAMETERS;
            case HelpCommand.COMMAND_WORD:
                return HelpCommand.PARAMETERS;
            case DeadlineCommand.COMMAND_WORD:
                return DeadlineCommand.PARAMETERS;
            default:
                return "";
            }
        } else {
            switch (currentString) {
            case AddEventCommand.COMMAND_WORD:
                return AddEventCommand.PARAMETERS;
            case DeleteEventCommand.COMMAND_WORD:
                return DeleteEventCommand.PARAMETERS;
            case EditEventCommand.COMMAND_WORD:
                return EditEventCommand.PARAMETERS;
            case ViewEventCommand.COMMAND_WORD:
                return ViewEventCommand.PARAMETERS;
            case ClearCommand.COMMAND_WORD:
                return ClearCommand.PARAMETERS;
            case ExitCommand.COMMAND_WORD:
                return ExitCommand.PARAMETERS;
            case FindEventCommand.COMMAND_WORD:
                return FindEventCommand.PARAMETERS;
            case HelpCommand.COMMAND_WORD:
                return HelpCommand.PARAMETERS;
            case IncludeCommand.COMMAND_WORD:
                return IncludeCommand.PARAMETERS;
            default:
                return "";
            }
        }
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see Logic#execute(String, Boolean)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    public void setIsResidentTab(boolean isResidentTab) {
        this.isResidentTab = isResidentTab;
    }

    /**
     * Refreshes the suggestion when changing tabs .
     */
    public void refreshSuggestions() {
        currentString = main.getText();
        String[] processString = processMain(currentString.trim());
        String commandString = processString[0];
        if (processString.length > 1) {
            currentParamters = processString[1].trim();
        }
        String suggestion = mapSuggestion(commandString.trim(), isResidentTab);
        if (!suggestion.equals("")) {
            suggestions.setText("  " + suggestion);
        } else {
            suggestions.setText("");
        }
    }
}
