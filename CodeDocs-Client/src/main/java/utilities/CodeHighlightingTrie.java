package utilities;

/**
 * Trie for implementing code highlighting
 */
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

    /**
     * Function to insert a new word along with its style class in trie
     *
     * @param key        the token typed in code
     * @param styleClass the style class to be applied to word as specified in css
     */
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

    /**
     * Function to fetch the styleClass for a word in trie
     *
     * @param key the word to search for
     * @return styleClass if word found else null
     */
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
