package models;

import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.PlainTextChange;
import utilities.*;

import java.util.*;
import java.util.function.Consumer;

public class CodeEditor {

    private final LanguageType languageType;
    private final StyleClassedTextArea textArea;
    private CodeHighlightingTrie codeHighlightingTrie;
    private CodeAutocompleteTrie codeAutocompleteTrie;

    public CodeEditor(String initialContent, LanguageType languageType) {

        this.languageType = languageType;
        textArea = new StyleClassedTextArea();

        textArea.setWrapText(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        textArea.setContextMenu(new ContextMenu());

        // TODO: Do using CSS
        textArea.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/test.css")).toExternalForm());
        textArea.setLineHighlighterFill(Paint.valueOf("#e3e3e3"));
        textArea.setLineHighlighterOn(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        textArea.setOnKeyTyped(keyEvent -> inputHandler(keyEvent));

        setupLanguageParser();
        highlightCode();
        textArea.appendText(initialContent);

    }

    private void inputHandler(KeyEvent keyEvent) {

        int currentLine = textArea.getCurrentParagraph();
        String currentLineText = textArea.getText(currentLine);

        highlightCode();

        CodeTokenizer tokenizer = new CodeTokenizer(currentLineText, codeHighlightingTrie);
        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
            textArea.setStyle(currentLine, token.getStartIndex(), token.getEndIndex(), Collections.singleton(token.getStyle()));
        }
        autocompleteCode(currentLine);

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

    private void highlightCode() {
        //TODO : resolve bugs
        textArea.plainTextChanges().subscribe(new Consumer<PlainTextChange>() {
            @Override
            public void accept(PlainTextChange plainTextChange) {
                String content = plainTextChange.getInserted();
                CodeTokenizer tokenizer = new CodeTokenizer(content, codeHighlightingTrie);
                for (CodeTokenizer.Token token : tokenizer.getStyles()) {
                    int start = token.getStartIndex()+plainTextChange.getPosition();
                    int end  = token.getEndIndex()+plainTextChange.getPosition();

                    String styleClass = token.getStyle();
                    textArea.setStyleClass(start, end, styleClass);
                }
            }
        });
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

        if (!suggestions.isEmpty()) {
            // TODO: See some good approach

            ContextMenu contextMenu = textArea.getContextMenu();
            contextMenu.getItems().clear();

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
        }
    }
}
