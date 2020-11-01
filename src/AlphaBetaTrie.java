import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AlphaBetaTrie {
    HashMap<StringPair, Double> all_aligments_with_prob = new HashMap<>();
    private Trie<Trie<Double>> alphaBetaTrie;
    private int window;

    public AlphaBetaTrie(int window) {
        this.window = window;
        calculateAlignmentCount(window);
        makeBetaAlphaTrie();

    }

    public void calculateAlignmentCount(int window) {
        for (Map.Entry<String, ArrayList<String>> correct_misspeled : Utils.getTrainMisspelledWords().entrySet()) {
            if (Pattern.matches("[a-z]+", correct_misspeled.getKey().toLowerCase())) {
                for (String missppeled : correct_misspeled.getValue()) {
                    if (Pattern.matches("[a-z]+", missppeled.toLowerCase())) {
                        ArrayList<StringPair> alignments = EditDistance.getAlignment(
                                correct_misspeled.getKey().toLowerCase(), missppeled.toLowerCase(), window);
                        for (StringPair align : alignments) {
                            align = this.strip(align);
                            if(align.key.length() ==0 || align.value.length()==0){
                                continue;
                            }
                            Double prevCount = all_aligments_with_prob.get(align);

                            if (prevCount == null) {
                                prevCount = 0.0;
                            }
                            all_aligments_with_prob.put(align, prevCount + 1);
                        }
                    }
                }
            }

        }
        System.out.println(all_aligments_with_prob.size());
    }

    private void makeBetaAlphaTrie() {
        alphaBetaTrie = new Trie<>();

        // first create the alpha tries for each RHS
        Map<String, Trie<Double>> alphaTries = new HashMap<>();
        Map<String, Double> aCount = AlphaBetaTrie.alphaCount(this.window);
        for (Map.Entry<StringPair, Double> alignment_prob : all_aligments_with_prob.entrySet()) {
            String alpha_alignment = alignment_prob.getKey().key;
            String beta_alignment = alignment_prob.getKey().value;

            if (!alphaTries.containsKey(beta_alignment)) {
                alphaTries.put(beta_alignment, new Trie<Double>());
            }
            double abcount = alignment_prob.getValue();
            Trie<Double> beta = alphaTries.get(beta_alignment);

            beta.put(new StringBuilder(alpha_alignment).reverse().toString(), (abcount + 1)
                    / (aCount.containsKey(alpha_alignment) ? aCount.get(alpha_alignment) : 100000000000000000.0));
            

        }
        for (String alpha : alphaTries.keySet()) {
            alphaBetaTrie.put(new StringBuilder(alpha).reverse().toString(), alphaTries.get(alpha));
        }

    }

    public static Map<String, Double> alphaCount(int window) {
        int normalizer = 12711;
        Map<String, Double> count = new HashMap<String, Double>();
        Map<String, Double> freq = Utils.getFreqFileRaw();
        for (Map.Entry<String, Double> entry : freq.entrySet()) {
            String word = entry.getKey();
            for (int i = 0; i < word.length(); i++) {
                for (int j = 1; j <= window+1; j++) {
                    if (i + j >= word.length()) {
                        break;
                    }
                    String substr = word.substring(i, i + j);
                    double c = count.containsKey(substr) ? count.get(substr) : 0;
                    count.put(substr, c + (freq.get(word) / normalizer));
                }
            }

        }
        return count;
    }

    public Trie<Trie<Double>>.Node getRoot() {
        return this.alphaBetaTrie.getRoot();
    }

    private StringPair strip(StringPair pair) {
        pair.key = pair.key.replace("*", "");
        pair.value = pair.value.replace("*", "");
        return pair;
    }
}
