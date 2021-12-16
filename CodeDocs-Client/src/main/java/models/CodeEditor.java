package models;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import utilities.CodeAutocompleteTrie;
import utilities.CodeHighlightingTrie;
import utilities.CodeTokenizer;

import java.util.*;

public class CodeEditor {

    private final StyleClassedTextArea textArea;
    private final CodeHighlightingTrie codeHighlightingTrie;
    private final CodeAutocompleteTrie codeAutocompleteTrie;

    public CodeEditor(String initialContent) {
        textArea = new StyleClassedTextArea();

        textArea.appendText(initialContent);
        textArea.setWrapText(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

        // TODO: Do using CSS
        textArea.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/test.css")).toExternalForm());
        textArea.setLineHighlighterFill(Paint.valueOf("#e3e3e3"));
        textArea.setLineHighlighterOn(true);
        textArea.setContextMenu(new ContextMenu());

        textArea.setOnKeyTyped(keyEvent -> inputHandler(keyEvent));

        // TODO: Do using language type
        ArrayList<String> reservedWords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case",
                "catch", "char", "class", "const", "continue", "default",
                "double", "do", "else", "enum", "extends", "false",
                "final", "finally", "float", "for", "goto", "if",
                "implements", "import", "instanceof", "int", "interface", "long",
                "native", "new", "null", "package", "private", "protected",
                "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient",
                "true", "try", "void", "volatile", "while"));

        codeHighlightingTrie = new CodeHighlightingTrie();
        for (String word : reservedWords) {
            codeHighlightingTrie.insert(word, "keyword");
        }
        codeHighlightingTrie.insert(";", "symbol");
        codeHighlightingTrie.insert(":", "symbol");
        codeHighlightingTrie.insert("(", "parenthesis");
        codeHighlightingTrie.insert(")", "parenthesis");
        codeHighlightingTrie.insert("{", "brace");
        codeHighlightingTrie.insert("}", "brace");
        codeHighlightingTrie.insert("[", "bracket");
        codeHighlightingTrie.insert("]", "bracket");

        // TODO: Add more words
        codeAutocompleteTrie = new CodeAutocompleteTrie();
        for (String word : reservedWords) {
            codeAutocompleteTrie.insert(word);
        }
    }

    private void inputHandler(KeyEvent keyEvent) {

        int currentLine = textArea.getCurrentParagraph();
        String currentLineText = textArea.getText(currentLine);

        CodeTokenizer tokenizer = new CodeTokenizer(currentLineText, codeHighlightingTrie);
        for (CodeTokenizer.Token token : tokenizer.getStyles()) {
            textArea.setStyle(currentLine, token.getStartIndex(), token.getEndIndex(), Collections.singleton(token.getStyle()));
        }

        ContextMenu contextMenu = textArea.getContextMenu();
        contextMenu.getItems().clear();

        String lastWord = tokenizer.getLast();
        ArrayList<String> suggestions = new ArrayList<>();
        if (!lastWord.isEmpty()) {
            suggestions = codeAutocompleteTrie.getRecommendations(tokenizer.getLast());
        }

        if (!suggestions.isEmpty()) {
            // TODO: See some good approach

            for (String suggestion : suggestions) {
                MenuItem menuItem = new MenuItem(tokenizer.getLast() + suggestion);
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        textArea.appendText(suggestion);
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

    public StyleClassedTextArea getTextArea() {
        return textArea;
    }
}
