package utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class CParser {

    private ArrayList<String> reservedWords = new ArrayList<>(Arrays.asList("auto", "break", "case", "char", "const",
            "continue", "default", "do", "double", "else", "enum", "extern",
            "float", "for", "goto",	"if", "int", "long", "register", "return", "short", "signed", "sizeof", "static",
            "struct", "switch",	"typedef", "union", "unsigned",	"void",	"volatile",	"while"));
    private ArrayList<String> symbol = new ArrayList<>(Arrays.asList(";",":",","));
    private ArrayList<String> parenthesis = new ArrayList<>(Arrays.asList("(",")"));
    private ArrayList<String> brace = new ArrayList<>(Arrays.asList("{","}"));
    private ArrayList<String> bracket = new ArrayList<>(Arrays.asList("[","]"));
    private static CParser instance = null;

    // private constructor restricted to this class itself
    private CParser() {
    }

    // static method to create instance of Singleton class
    public static CParser getInstance() {
        if (instance == null)
            instance = new CParser();
        return instance;
    }

    public ArrayList<String> getReservedWords() {
        return reservedWords;
    }

    public static void setInstance(CParser instance) {
        CParser.instance = instance;
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
