package models;

import javafx.css.Style;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import utilities.CodeHighlightingTrie;
import utilities.CodeTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class CodeEditor {

    private StyleClassedTextArea textArea;
    private ArrayList<String> reservedWords;
    private  CodeHighlightingTrie trie;

    public CodeEditor(String initialContent) {
        textArea = new StyleClassedTextArea();

        textArea.appendText(initialContent);
        textArea.setWrapText(true);

        // TODO: Do using CSS
        textArea.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/test.css")).toExternalForm());
        textArea.setLineHighlighterFill(Paint.valueOf("#e3e3e3"));
        textArea.setLineHighlighterOn(true);

        textArea.setOnKeyTyped(keyEvent -> inputHandler(keyEvent));

        // TODO: Do using language type
        reservedWords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case",
                "catch", "char", "class", "const", "continue", "default",
                "double", "do", "else", "enum", "extends", "false",
                "final", "finally", "float", "for", "goto", "if",
                "implements", "import", "instanceof", "int", "interface", "long",
                "native", "new", "null", "package", "private", "protected",
                "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient",
                "true", "try", "void", "volatile", "while"));

        trie = new CodeHighlightingTrie();
        for(String word : reservedWords) {
            trie.insert(word, "keyword");
        }
    }

    private void inputHandler(KeyEvent keyEvent) {

        int currentLine = textArea.getCurrentParagraph();
        String currentLineText = textArea.getText(currentLine);

        // TODO: Highlight for comments and strings
        CodeTokenizer tokenizer = new CodeTokenizer(currentLineText);
        for(CodeTokenizer.Token token : tokenizer.getTokens()) {
            String styleClass = trie.search(token.getWord());
            textArea.setStyle(currentLine, token.getStartIndex(), token.getEndIndex(), Collections.singleton(styleClass));
        }
    }

    public StyleClassedTextArea getTextArea() {
        return textArea;
    }
}
