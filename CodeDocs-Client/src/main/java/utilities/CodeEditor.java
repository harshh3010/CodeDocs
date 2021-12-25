package utilities;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import mainClasses.editor.EditorConnection;
import models.Peer;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.CaretNode;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import requests.peerRequests.StreamContentChangeRequest;
import requests.peerRequests.StreamContentSelectionRequest;
import requests.peerRequests.StreamCursorPositionRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class will be used for creating the main code editing region. It extends StackPane, the bottom
 * most layer is a textarea and above that is a layer for rendering labels to display names of users currently
 * editing the CodeDoc.
 */
public class CodeEditor extends StackPane {

    private final Pane pane = new Pane();// Holds labels to display user names
    private final StyleClassedTextArea textArea = new StyleClassedTextArea(); // Textarea for code editing
    private final ContextMenu suggestionMenu = new ContextMenu(); // Menu to display code suggestions

    private final LanguageType languageType; // Programming language for current editor instance
    private CodeHighlightingTrie codeHighlightingTrie; // Trie for highlighting syntax
    private CodeAutocompleteTrie codeAutocompleteTrie; // Trie for auto-completing code

    private final EditorConnection editorConnection; // Reference to current editor connection for streaming info to other users

    private final CaretNode myCursor;
    private final HashMap<String, CaretNode> userCursors = new HashMap<>(); // Cursors for connected users
    private final HashMap<String, Label> userLabels = new HashMap<>();  // Labels for connected users

    private boolean isEditable; // Editing mode of current code editor instance
    private boolean syntaxHighlightingOn = true; // Syntax highlighting status
    private boolean autocompleteOn = true; // Code auto-complete status
    private boolean isDirty;


    /**
     * @param languageType     The programming language of CodeDoc opened in current editor instance
     * @param editorConnection Reference to editor connection for streaming
     * @param initialContent   Initial content to be displayed in editor
     */
    public CodeEditor(LanguageType languageType, EditorConnection editorConnection, String initialContent, boolean isEditable) {
        this.editorConnection = editorConnection;
        this.languageType = languageType;

        // Setting-up two layers of CodeEditor
        pane.setMouseTransparent(true); // The topmost layer will not recognize mouse clicks
        this.getChildren().add(textArea);
        this.getChildren().add(pane);

        textArea.setWrapText(true); // Prevent horizontal scrolling in code editor
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea)); // Display line numbers
        textArea.setContextMenu(suggestionMenu); // Attaching suggestion menu to text area

        // Setting up the code editor
        this.isEditable = isEditable;
        textArea.setEditable(isEditable);
        setupLanguageParser();

        // Adding initial text to be displayed
        textArea.appendText(initialContent);
        highlightCode();

        setupInputHandler();
        setupTextChangeHandler();
        setupTextSelectionHandler();
        setupCursorPositionHandler();

        myCursor = new CaretNode(UserApi.getInstance().getId(), textArea, 0);
        if(!isEditable) {
            myCursor.setVisible(true);
            textArea.addCaret(myCursor);
        }

        isDirty = false;
    }

    /**
     * This method initializes the highlighting and autocomplete tries
     * depending on the programming language.
     */
    private void setupLanguageParser() {

        // Arrays for storing the reserved words and symbols for current programming language
        ArrayList<String> keywords, symbol, parenthesis, bracket, braces;
        if (languageType == LanguageType.JAVA) {
            keywords = JavaParser.getInstance().getReservedWords();
            symbol = JavaParser.getInstance().getSymbol();
            parenthesis = JavaParser.getInstance().getParenthesis();
            braces = JavaParser.getInstance().getBrace();
            bracket = JavaParser.getInstance().getBracket();
        } else {
            keywords = CParser.getInstance().getReservedWords();
            symbol = CParser.getInstance().getSymbol();
            parenthesis = CParser.getInstance().getParenthesis();
            braces = CParser.getInstance().getBrace();
            bracket = CParser.getInstance().getBracket();
        }

        codeAutocompleteTrie = new CodeAutocompleteTrie();
        codeHighlightingTrie = new CodeHighlightingTrie();

        // Storing all keywords for highlighting and auto-complete
        for (String word : keywords) {
            codeHighlightingTrie.insert(word, "keyword");
            codeAutocompleteTrie.insert(word);
        }

        // Storing all symbols for highlighting
        for (String word : symbol) {
            codeHighlightingTrie.insert(word, "symbol");
        }
        for (String word : parenthesis) {
            codeHighlightingTrie.insert(word, "parenthesis");
        }
        for (String word : braces) {
            codeHighlightingTrie.insert(word, "brace");
        }
        for (String word : bracket) {
            codeHighlightingTrie.insert(word, "bracket");
        }
    }

    /**
     * This method attaches a change listener on the text changes occurring in textArea
     * and specified actions are performed on text changes
     */
    private void setupTextChangeHandler() {
        textArea.plainTextChanges().subscribe(plainTextChange -> {

            isDirty = true;

            // Highlighting the syntax
            highlightCode();

            // If the editor was in write mode, then stream the content changes
            if (isEditable) {
                streamContent(plainTextChange);
//                streamCursorPosition();
            }
        });
    }

    /**
     * This method attaches a change listener on the text selection in textArea and the
     * specified actions are performed when some range of text is selected
     */
    private void setupTextSelectionHandler() {
        textArea.selectionProperty().addListener((observableValue, indexRange, t1) -> {

            // If the editor is opened in write mode then stream the text selection to other users
            if (isEditable) {
                streamTextSelection();
            }
        });
    }

    /**
     * This method attaches a change listener on the caret position in textArea and the
     * specified actions are performed when cursor position changes
     */
    private void setupCursorPositionHandler() {
        textArea.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                myCursor.moveTo(textArea.getCaretPosition());
                streamCursorPosition();
            }
        });
    }

    /**
     * This method attaches an event listener for key presses on the textArea
     */
    private void setupInputHandler() {
        textArea.setOnKeyTyped(keyEvent -> {

            // If the editor is opened in write mode then display code suggestions to the user
            if (isEditable) {
                autocompleteCode();
                streamCursorPosition();
            }
        });
    }

    /**
     * This method streams some part of the content of code editor to other online users
     *
     * @param plainTextChange stores the info of the text change that is to be streamed
     */
    private void streamContent(PlainTextChange plainTextChange) {
        try {
            // Creating a new content streaming request
            StreamContentChangeRequest contentChangeRequest = new StreamContentChangeRequest();

            // Setting the newly inserted text
            contentChangeRequest.setInsertedContent(plainTextChange.getInserted());

            // Setting the start position of newly inserted text
            contentChangeRequest.setInsertedStart(plainTextChange.getPosition());

            // Setting the old removed text
            contentChangeRequest.setRemovedContent(plainTextChange.getRemoved());

            // Setting the end position of removed text
            contentChangeRequest.setRemovedEnd(plainTextChange.getRemovalEnd());

            // Writing the request to all online users
            for (Peer peer : editorConnection.getConnectedPeers().values()) {
                peer.getOutputStream().writeObject(contentChangeRequest);
                peer.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method streams current caret position to other online users
     */
    private void streamCursorPosition() {

        try {
            // Creating a new cursor position streaming request
            StreamCursorPositionRequest request = new StreamCursorPositionRequest();

            // Setting current user's id for making changes at receiver's end
            request.setUserId(UserApi.getInstance().getId());

            // Setting the current caret position
            if (isEditable) {
                request.setPosition(textArea.getCaretPosition());
            } else {
                request.setPosition(myCursor.getPosition());
            }

            // Writing the request to all online users
            for (Peer peer : editorConnection.getConnectedPeers().values()) {
                peer.getOutputStream().writeObject(request);
                peer.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method streams text selection range to other online users
     */
    private void streamTextSelection() {
        try {
            // Getting the selection range
            int start = textArea.getSelection().getStart();
            int end = textArea.getSelection().getEnd();

            // If selection range is non-zero then streaming it
            if (start != end) {
                // Creating a new streaming request
                StreamContentSelectionRequest request = new StreamContentSelectionRequest();

                // Setting the selection range
                request.setStart(start);
                request.setEnd(end);

                // Writing the request to all online users
                for (Peer peer : editorConnection.getConnectedPeers().values()) {
                    peer.getOutputStream().writeObject(request);
                    peer.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function performs the code highlighting
     */
    private void highlightCode() {

        // Do nothing if highlighting is disabled
        if (!syntaxHighlightingOn) {
            return;
        }

        // TODO: Use text change approach

        // Receive the style classes corresponding to all tokens
        CodeTokenizer tokenizer = new CodeTokenizer(textArea.getText(), codeHighlightingTrie);

        // Applying the style class in ranges corresponding to tokens
        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
            int start = token.getStartIndex();
            int end = token.getEndIndex();
            String styleClass = token.getStyle();
            textArea.setStyleClass(start, end, styleClass);
        }
    }

    /**
     * This function displays code suggestions to user and handles auto-completion
     * by attaching action listener on suggestions
     */
    private void autocompleteCode() {

        // Do nothing if auto-complete is disabled
        if (!autocompleteOn) {
            return;
        }

        // Fetch the last typed word
        String lastWord = getLastTypedWord();

        // Fetch all the suggestions using auto-complete trie
        ArrayList<String> suggestions = new ArrayList<>();
        if (!lastWord.isEmpty()) {
            suggestions = codeAutocompleteTrie.getRecommendations(lastWord);
        }

        // Adding the suggestions on the suggestion menu
        suggestionMenu.getItems().clear();
        if (!suggestions.isEmpty()) {
            for (String suggestion : suggestions) {
                MenuItem menuItem = new MenuItem(lastWord + suggestion);
                menuItem.setOnAction(actionEvent -> {
                    // Auto-completing the remaining part on user's choice
                    textArea.insertText(textArea.getCaretPosition(), suggestion);
                    suggestionMenu.hide();
                });
                suggestionMenu.getItems().add(menuItem);
            }

            // Getting the cursor's position on screen to display menu at same position as cursor
            double x = textArea.getCaretBounds().get().getCenterX();
            double y = textArea.getCaretBounds().get().getCenterY();

            // Displaying the suggestion menu to the user
            suggestionMenu.show(textArea, x, y);
        } else {

            // Hiding already visible suggestion menu
            suggestionMenu.hide();
        }
    }

    /**
     * This utility function return the last-typed/currently-typing word
     *
     * @return last typed word in current line
     */
    private String getLastTypedWord() {

        // Getting the current line number
        int currentLine = textArea.getCurrentParagraph();

        StringBuilder lastWord = new StringBuilder();

        // Parsing the current line in reverse order until a white space is encountered
        int idx = textArea.getCaretColumn() - 1;
        while (idx >= 0 && !(textArea.getText(currentLine, idx, currentLine, idx + 1).trim().isEmpty())) {
            lastWord.insert(0, textArea.getText(currentLine, idx, currentLine, idx + 1));
            idx--;
        }

        return lastWord.toString();
    }

    /**
     * This method is used for applying styles to the current code editor instance
     *
     * @param resource         is the path to css file
     * @param currentLineColor is the hex value of highlighting color for current line
     */
    public void applyContentStyle(String resource, String currentLineColor) {

        // Applying the stylesheet
        textArea.getStylesheets().add(resource);

        // Setting line highlight color
        textArea.setLineHighlighterFill(Paint.valueOf(currentLineColor));
        textArea.setLineHighlighterOn(true);
    }

    /**
     * This method is for adding a new cursor on code editor
     *
     * @param userId for the user corresponding to the cursor
     */
    public void addCursor(String userId) {

        // Creating the cursor and storing in the hashmap
        userCursors.put(userId, new CaretNode(userId, textArea, 0));

        // Marking the cursor visible
        userCursors.get(userId).setVisible(true);

        // Rendering cursor on text area
        textArea.addCaret(userCursors.get(userId));

        // Creating a label to display user's name
        Label label = new Label();
        label.setText(editorConnection.getConnectedPeers().get(userId).getUser().getFirstName());
        label.setVisible(true);
        label.setStyle("-fx-text-fill: white;");

        // Adding this label to the topmost layer and in the hashmap
        pane.getChildren().add(label);
        userLabels.put(userId, label);
    }

    /**
     * This function removes the cursor corresponding to a specified user
     *
     * @param userId represents the user of the cursor
     */
    public void removeCursor(String userId) {

        // Remove the label for user from top layer and hashmap
        pane.getChildren().remove(userLabels.get(userId));
        userLabels.remove(userId);

        // Remove the caret from text area and hashmap
        textArea.removeCaret(userCursors.get(userId));
        userCursors.remove(userId);
    }


    /**
     * This function moves the cursor for specific user on specific position
     *
     * @param userId of the user of cursor
     * @param pos    is the new position of cursor
     */
    public void moveCursor(String userId, int pos) {
        int mxPos = textArea.getLength();

        // Cursor should not be outside the valid range
        if (pos <= mxPos) {

            // If no cursor exists, create a new one
            if (userCursors.get(userId) == null) {
                addCursor(userId);
            }

            // Changing the caret position
            userCursors.get(userId).moveTo(pos);

            // Fetching cursor position and text area position relative to screen
            double x = userCursors.get(userId).getCaretBounds().get().getCenterX();
            double y = userCursors.get(userId).getCaretBounds().get().getCenterY();
            double x1 = pane.localToScreen(pane.getBoundsInLocal()).getMinX();
            double y1 = pane.localToScreen(pane.getBoundsInLocal()).getMinY();

            // Using the positions to move the label on top layer along with caret
            userLabels.get(userId).setLayoutX(x - x1);
            userLabels.get(userId).setLayoutY(y - y1);
        }
    }

    /**
     * Selects the content in specified range
     *
     * @param start pos of the range
     * @param end   pos of the range
     */
    public void selectContent(int start, int end) {
        textArea.selectRange(start, end);
    }


    /**
     * Inserts content at start position
     */
    public void insertContent(int start, String content) {
        textArea.insertText(start, content);
    }

    /**
     * Replaces content between start and end
     */
    public void removeContent(int start, int end) {
        textArea.deleteText(start, end);
    }

    /**
     * Enables syntax highlighting
     */
    public void enableSyntaxHighlighting() {
        syntaxHighlightingOn = true;
    }

    /**
     * Disables syntax highlighting
     */
    public void disableSyntaxHighlighting() {
        syntaxHighlightingOn = false;
    }

    /**
     * Enables auto-complete
     */
    public void enableAutocompleteCode() {
        autocompleteOn = true;
    }

    /**
     * Disables auto-complete
     */
    public void disableAutocompleteCode() {
        autocompleteOn = false;
    }

    /**
     * Sets the code editor as editable
     */
    public void setEditable(boolean editable) {
        isEditable = editable;

        // Setting up the code editor
        textArea.setEditable(isEditable);
        if(!isEditable) {
            myCursor.setVisible(true);
        }
        setupInputHandler();
        setupTextChangeHandler();
        setupTextSelectionHandler();
        setupCursorPositionHandler();
    }

    /**
     * Fetches the content of code editor
     */
    public String getText() {
        return textArea.getText();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
