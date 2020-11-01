import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

public class ImprovedNoisyChannel implements SpellingPolicy {

    Trie<DictTrieData> dictTrie;
    AlphaBetaTrie abTrie;
    int window;

    public String getMostMatchingWord(String wordToSearch) {
        return getTopMatchWords(wordToSearch, 1).get(0);
    }

    public ArrayList<String> getTopMatchWords(String wordToSearch, int top) {
        PriorityQueue<WordScore> maxHeap = new PriorityQueue<>(new Comparator<WordScore>() {

            @Override
            public int compare(WordScore o1, WordScore o2) {
                if (o1.score < o2.score) {
                    return 1;
                }
                return -1;
            }

        });
        FillVector(wordToSearch, dictTrie.getRoot(), maxHeap, "");
        ArrayList<String> top_match_words = new ArrayList<>();
        for (int i = 0; i < top; i++) {
            top_match_words.add(maxHeap.poll().word);
        }
        return top_match_words;
    }

    private class WordScore {
        public String word;
        public Double score;

        public WordScore(String word, Double score) {
            this.word = word;
            this.score = score;
        }
    }

    private void FillVector(String wordToSearch, Trie<DictTrieData>.Node node, PriorityQueue<WordScore> maxHeap,
            String currWord) {
        int length = wordToSearch.length() + 1;
        Double[] vec = new Double[length];
        // initialize vector
        for (int i = 0; i < length; i++) {
            vec[i] = 0.0;
        }
        if (node.getValue() == null) {
            node.setValue(new DictTrieData(false));
        }
        node.getValue().setVec(vec);

        for (int i = 0; i < length; i++) {
            if (node.getKey() == null && i == 0) {
                // top right cell is 1.0
                // corresponds to replacing nothing with nothing.
                vec[i] = 1.0;
                continue;
            }

            Trie<Trie<Double>>.Node alphaNode = abTrie.getRoot();
            String alpha = "";
            for (int j = i; j >= 0; j--) {
                Trie<DictTrieData>.Node current = node;
                Trie<Double> betaTrie = null;
                if (alphaNode != null) {
                    betaTrie = alphaNode.getValue();
                }
                Trie<Double>.Node betaNode = null;
                if (betaTrie != null) {
                    betaNode = betaTrie.getRoot();
                }
                String beta = "";
                do {
                    if (alpha.equals(beta) & alpha.length()>0) {
                        vec[i] = Math.max(vec[i], current.getValue().getVec()[j]);
                    } else if ((betaNode != null) && (betaNode.getValue() != null)) {
                        vec[i] = Math.max(vec[i], betaNode.getValue() * current.getValue().getVec()[j]);
                    }
                    if (current.getKey() != null) {
                        beta += current.getKey();
                        if(beta.length()>this.window+1){
                            break;
                        }
                        if (betaNode != null) {
                            betaNode = betaNode.findChild(current.getKey());
                        }
                    }
                    current = current.getParent();
                } while (current != null);
                if (j <= wordToSearch.length()) {
                    if (j > 0) {
                        if (alphaNode != null) {
                            alphaNode = alphaNode.findChild(wordToSearch.charAt(j - 1));
                        }
                        alpha += wordToSearch.charAt(j - 1);
                        if(alpha.length()>this.window+1){
                            break;
                        }
                    }

                }
            }
        }

        if (node.getValue().getIsWord()) {
            maxHeap.add(new WordScore(currWord, node.getValue().getVec()[length - 1]*EditDistance.get_freq(currWord)));
        }
        for (Trie<DictTrieData>.Node child : node.getChildren()) {
            FillVector(wordToSearch, child, maxHeap, currWord + child.getKey());
        }

    }

    public ImprovedNoisyChannel(ArrayList<String> words, int window) {
        this.window = window;
        this.abTrie = new AlphaBetaTrie(window);
        this.dictTrie = new Trie<>();
        for (String word : words) {
            word = word.toLowerCase();
            if (Pattern.matches("[a-z]+", word)) {
                dictTrie.put(word, new DictTrieData(true));
            }

        }
    }

}
