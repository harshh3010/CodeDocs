package utilities;

import java.util.ArrayList;

public class CodeTokenizer {

    public static class Token {
        private int startIndex;
        private int endIndex;
        private String style;

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }
    }

    private String line;
    private CodeHighlightingTrie trie;

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
                    Token token = new Token();
                    token.setStartIndex(start);
                    token.setEndIndex(i);
                    token.setStyle("quotes");
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
                    Token token = new Token();
                    token.setStartIndex(start);
                    token.setEndIndex(i);
                    token.setStyle(trie.search(temp));
                    tokenList.add(token);
                }
                start = i + 1;
                temp = "";
            } else if (ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ';' || ch == ':') {
                Token token = new Token();
                token.setStartIndex(i);
                token.setEndIndex(i+1);
                String s = trie.search(ch + "");
                token.setStyle(s);
                tokenList.add(token);
                start = i + 1;
                temp = "";
            } else {
                temp = temp + ch;
            }
        }
        if (!temp.isEmpty() || quote != '#') {
            Token token = new Token();
            token.setStartIndex(start);
            if (quote == '#') {
                token.setStyle(trie.search(temp));
                token.setEndIndex(start + temp.length());
            } else {
                token.setStyle("quotes");
                token.setEndIndex(line.length());
            }
            tokenList.add(token);
        }
        return tokenList;
    }

}