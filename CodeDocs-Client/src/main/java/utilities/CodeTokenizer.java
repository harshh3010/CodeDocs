package utilities;

import java.util.ArrayList;

public class CodeTokenizer {

    public static class Token {
        private final int startIndex;
        private final int endIndex;
        private final String style;

        public Token(int startIndex, int endIndex, String style) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.style = style;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public String getStyle() {
            return style;
        }
    }

    private final String line;
    private final CodeHighlightingTrie trie;

    public CodeTokenizer(String line, CodeHighlightingTrie trie) {
        this.line = line;
        this.trie = trie;
    }

    public ArrayList<Token> getStyles() {
        ArrayList<Token> tokenList = new ArrayList<>();
        int i;
        String temp = "";
        int start = 0;
        char quote = '#';

        for (i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quote != '#') {
                if (line.charAt(i) == quote && line.charAt(i - 1) != '\\') {
                    Token token = new Token(start, i, "quotes");
                    tokenList.add(token);
                    start = i + 1;
                    temp = "";
                    quote = '#';
                }
            } else if (ch == '\"' || ch == '\'') {
                quote = ch;
                start = i;
                temp = "";
            } else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                if (!temp.isEmpty()) {
                    Token token = new Token(start, i, trie.search(temp));
                    tokenList.add(token);
                }
                start = i + 1;
                temp = "";
            } else if (ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ';' || ch == ':') {
                Token token = new Token(i, i + 1, trie.search(ch + ""));
                tokenList.add(token);
                start = i + 1;
                temp = "";
            } else {
                temp = temp + ch;
            }
        }

        if (!temp.isEmpty() || quote != '#') {
            String style;
            if (quote == '#') {
                style = trie.search(temp);
            } else {
                style = "quotes";
            }
            Token token = new Token(start, line.length(), style);
            tokenList.add(token);
        }

        return tokenList;
    }
}