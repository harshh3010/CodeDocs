package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Trie for implementing code autocomplete feature
 */
public class CodeAutocompleteTrie {

    /**
     * TrieNode represents each node in trie
     */
    public class TrieNode {
        Map<Character, TrieNode> children;
        char c;
        boolean isWord;

        public TrieNode(char c) {
            this.c = c;
            children = new HashMap<>();
        }

        public TrieNode() {
            children = new HashMap<>();
        }
    }

    private final TrieNode root;

    public CodeAutocompleteTrie() {
        root = new TrieNode();
    }

    /**
     * Function to insert a new word in trie
     *
     * @param word
     */
    public void insert(String word) {
        TrieNode trieNode = root;
        for (char c : word.toCharArray()) {
            if (trieNode.children.get(c) == null)
                trieNode.children.put(c, new TrieNode());
            trieNode = trieNode.children.get(c);
        }
        trieNode.isWord = true;
    }

    /**
     * Function to get recommendations corresponding to a specified prefix
     *
     * @param prefix the specified prefix
     */
    public ArrayList<String> getRecommendations(String prefix) {
        ArrayList<String> suggestions = new ArrayList<>();
        TrieNode trieNode = root;
        for (char c : prefix.toCharArray()) {
            if (trieNode.children.get(c) == null) {
                return suggestions;
            }
            trieNode = trieNode.children.get(c);
        }
        recommendationHelper(trieNode, "", suggestions);
        return suggestions;
    }

    /**
     * Helper function to recursively find out all recommendations for a prefix
     */
    private void recommendationHelper(TrieNode trieNode, String suggestion, ArrayList<String> suggestions) {
        if (trieNode.isWord && !suggestion.isEmpty()) {
            suggestions.add(suggestion);
        }
        for (char next : trieNode.children.keySet()) {
            if (trieNode.children.get(next) != null) {
                recommendationHelper(trieNode.children.get(next), suggestion + next, suggestions);
            }
        }
    }
}
