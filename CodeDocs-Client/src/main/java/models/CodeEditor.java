package models;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import mainClasses.EditorConnection;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import requests.peerRequests.StreamContentChangeRequest;
import requests.peerRequests.StreamContentSelectionRequest;
import requests.peerRequests.StreamCursorPositionRequest;
import utilities.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class CodeEditor {

    private final LanguageType languageType;
    private final StyleClassedTextArea textArea;
    private CodeHighlightingTrie codeHighlightingTrie;
    private CodeAutocompleteTrie codeAutocompleteTrie;
    private final boolean hasWritePermissions;
    private final boolean hasControl;
    private int previousParagraph = 0;

    public CodeEditor(String initialContent, LanguageType languageType, boolean hasWritePermissions, boolean hasControl) {

        this.hasControl = hasControl;
        this.hasWritePermissions = hasWritePermissions;

        this.languageType = languageType;
        textArea = new StyleClassedTextArea();

        if (!hasWritePermissions || !hasControl) {
            textArea.setEditable(false);
        }

        textArea.setWrapText(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        textArea.setContextMenu(new ContextMenu());

        // TODO: Do using CSS
        textArea.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/test.css")).toExternalForm());
        textArea.setLineHighlighterFill(Paint.valueOf("#690026"));
        textArea.setLineHighlighterOn(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        textArea.setOnKeyTyped(keyEvent -> inputHandler(keyEvent));

        setupLanguageParser();
        setupTextChangeHandler();
        setupTextSelectionHandler();
        setupCursorPositionHandler();

        textArea.appendText(initialContent);
    }

    public StyleClassedTextArea getTextArea() {
        return textArea;
    }

    private void setupLanguageParser() {

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
        for (String word : keywords) {
            codeHighlightingTrie.insert(word, "keyword");
            codeAutocompleteTrie.insert(word);
        }
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

    private void inputHandler(KeyEvent keyEvent) {

        if(hasControl) {
            int currentLine = textArea.getCurrentParagraph();

//        String currentLineText = textArea.getText(currentLine);
//
//        CodeTokenizer tokenizer = new CodeTokenizer(currentLineText, codeHighlightingTrie);
//        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
//            textArea.setStyle(currentLine, token.getStartIndex(), token.getEndIndex(), Collections.singleton(token.getStyle()));
//        }

            autocompleteCode(currentLine);
        }
    }

    private void setupTextChangeHandler() {
        textArea.plainTextChanges().subscribe(new Consumer<PlainTextChange>() {
            @Override
            public void accept(PlainTextChange plainTextChange) {
                highlightCode();
                if (hasControl) {
                    try {
                        streamContent(plainTextChange);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupTextSelectionHandler() {
        textArea.selectionProperty().addListener(new ChangeListener<IndexRange>() {
            @Override
            public void changed(ObservableValue<? extends IndexRange> observableValue, IndexRange indexRange, IndexRange t1) {
               if(hasControl) {
                   try {
                       streamSelection();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
        });
    }

    private void setupCursorPositionHandler() {
        textArea.caretPositionProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                if(hasControl) {
                    try {
                        streamCursorPosition();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void streamCursorPosition() throws IOException {
        StreamCursorPositionRequest request = new StreamCursorPositionRequest();
        request.setPosition(textArea.getCaretPosition());

        for (Peer peer : EditorConnection.connectedPeers.values()) {
            peer.getOutputStream().writeObject(request);
            peer.getOutputStream().flush();
        }

    }

    private void streamSelection() throws IOException {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if(start != end) {
            StreamContentSelectionRequest request = new StreamContentSelectionRequest();
            request.setStart(start);
            request.setEnd(end);

            for (Peer peer : EditorConnection.connectedPeers.values()) {
                peer.getOutputStream().writeObject(request);
                peer.getOutputStream().flush();
            }
        }
    }

    private void streamContent(PlainTextChange plainTextChange) throws IOException {
        StreamContentChangeRequest contentChangeRequest = new StreamContentChangeRequest();
        contentChangeRequest.setInsertedContent(plainTextChange.getInserted());
        contentChangeRequest.setInsertedStart(plainTextChange.getPosition());
        contentChangeRequest.setRemovedContent(plainTextChange.getRemoved());
        contentChangeRequest.setRemovedEnd(plainTextChange.getRemovalEnd());
        for (Peer peer : EditorConnection.connectedPeers.values()) {
            peer.getOutputStream().writeObject(contentChangeRequest);
            peer.getOutputStream().flush();
        }
    }

    private void highlightCode() {

        CodeTokenizer tokenizer = new CodeTokenizer(textArea.getText(), codeHighlightingTrie);
        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
            int start = token.getStartIndex();
            int end = token.getEndIndex();
            String styleClass = token.getStyle();
            textArea.setStyleClass(start, end, styleClass);
        }

//        //TODO : resolve bugs
//        Get prev and next paragraph and apply tokenization
//        System.out.println(textArea.getCurrentParagraph());
//        String content = plainTextChange.getInserted();
//        CodeTokenizer tokenizer = new CodeTokenizer(content, codeHighlightingTrie);
//        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
//            int start = token.getStartIndex() + plainTextChange.getPosition();
//            int end = token.getEndIndex() + plainTextChange.getPosition();
//
//            String styleClass = token.getStyle();
//            textArea.setStyleClass(start, end, styleClass);
//        }
    }

    private void autocompleteCode(int currentLine) {
        String lastWord = "";
        int idx = textArea.getCaretColumn() - 1;
        while (idx >= 0 && !(textArea.getText(currentLine, idx, currentLine, idx + 1).trim().isEmpty())) {
            lastWord = textArea.getText(currentLine, idx, currentLine, idx + 1) + lastWord;
            idx--;
        }

        ArrayList<String> suggestions = new ArrayList<>();
        if (!lastWord.isEmpty()) {
            suggestions = codeAutocompleteTrie.getRecommendations(lastWord);
        }
        ContextMenu contextMenu = textArea.getContextMenu();
        contextMenu.getItems().clear();
        if (!suggestions.isEmpty()) {
            // TODO: See some good approach

            for (String suggestion : suggestions) {
                MenuItem menuItem = new MenuItem(lastWord + suggestion);
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        textArea.insertText(textArea.getCaretPosition(), suggestion);
                        contextMenu.hide();
                    }
                });
                contextMenu.getItems().add(menuItem);
            }

            double x = textArea.getCaretBounds().get().getCenterX();
            double y = textArea.getCaretBounds().get().getCenterY();

            contextMenu.show(textArea, x, y);
        } else {
            contextMenu.hide();
        }
    }
}
