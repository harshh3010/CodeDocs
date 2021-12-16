package utilities;

public class CodeHighlightingTrie {

    private final int ALPHABET_SIZE = 256;

    private class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
        String styleClass;

        TrieNode() {
            isEndOfWord = false;
            styleClass = null;
            for (int i = 0; i < ALPHABET_SIZE; i++)
                children[i] = null;
        }
    }

    private TrieNode root;

    public CodeHighlightingTrie() {
        root = new TrieNode();
    }

    public void insert(String key, String styleClass) {

        int level, index, length = key.length();

        TrieNode trieNode = root;

        for (level = 0; level < length; level++) {
            index = key.charAt(level);
            if (trieNode.children[index] == null)
                trieNode.children[index] = new TrieNode();

            trieNode = trieNode.children[index];
        }
        trieNode.isEndOfWord = true;
        trieNode.styleClass = styleClass;
    }

    public String search(String key) {

        int level, index, length = key.length();

        TrieNode trieNode = root;
        for (level = 0; level < length; level++) {
            index = key.charAt(level);
            if (trieNode.children[index] == null)
                return null;
            trieNode = trieNode.children[index];
        }
        return (trieNode.styleClass);
    }
}
