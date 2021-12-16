package utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class JavaParser {

    private ArrayList<String> reservedWords = new ArrayList<>(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue", "default",
            "double", "do", "else", "enum", "extends", "false",
            "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient",
            "true", "try", "void", "volatile", "while"));
    private ArrayList<String> symbol = new ArrayList<>(Arrays.asList(";",":",","));
    private ArrayList<String> parenthesis = new ArrayList<>(Arrays.asList("(",")"));
    private ArrayList<String> brace = new ArrayList<>(Arrays.asList("{","}"));
    private ArrayList<String> bracket = new ArrayList<>(Arrays.asList("[","]"));

    private static JavaParser instance = null;

    // private constructor restricted to this class itself
    private JavaParser() {
    }

    // static method to create instance of Singleton class
    public static JavaParser getInstance() {
        if (instance == null)
            instance = new JavaParser();
        return instance;
    }
    public ArrayList<String> getReservedWords() {
        return reservedWords;
    }

    public static void setInstance(JavaParser instance) {
        JavaParser.instance = instance;
    }

    public ArrayList<String> getSymbol() {
        return symbol;
    }

    public ArrayList<String> getParenthesis() {
        return parenthesis;
    }

    public ArrayList<String> getBrace() {
        return brace;
    }

    public ArrayList<String> getBracket() {
        return bracket;
    }
}
