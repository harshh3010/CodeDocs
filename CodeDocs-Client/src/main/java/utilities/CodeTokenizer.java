package utilities;

import java.util.ArrayList;

public class CodeTokenizer {

    public static class Token {
        private int startIndex;
        private int endIndex;
        private String word;

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

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }

    private String line;

    public CodeTokenizer(String line) {
        this.line = line;
    }

    public ArrayList<Token> getTokens() {

        ArrayList<Token> tokenList = new ArrayList<>();
        int i;
        String temp = "";
        int start = 0;
        for (i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                if(!temp.isEmpty()){
                    Token token = new Token();
                    token.setStartIndex(start);
                    token.setEndIndex(i);
                    token.setWord(temp);
                    start = i + 1;
                    tokenList.add(token);
                }
                temp = "";
            } else {
                temp = temp + ch;
            }
        }
        if(!temp.isEmpty()){
            Token token = new Token();
            token.setStartIndex(start);
            token.setEndIndex(start + temp.length());
            token.setWord(temp);
            tokenList.add(token);
        }
        return tokenList;
    }

}