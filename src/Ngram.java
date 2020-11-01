import java.util.*;

public class Ngram implements SpellingPolicy {
    ArrayList<String> word_list;
    int k;
    HashMap<String, ArrayList<Integer>> all_grams;
    boolean model_languge;

    Ngram(ArrayList<String> word_list, int k, boolean model_languge) {
        this.word_list = word_list;
        this.k = k;
        this.all_grams = new HashMap<>();
        this.model_languge = model_languge;
        int i = 0;
        for (String word : word_list) {
            ArrayList<String> word_ngram = create_index(k, word);
            add_n_gram(i, word_ngram);
            i += 1;
        }
    }

    public static ArrayList<String> create_index(int n, String str) {
        ArrayList<String> ngrams = new ArrayList<String>();
        // to add the start of the string
        if (str.length() >= n)
            ngrams.add("$" + str.substring(0, n - 1));
        for (int i = 0; i < str.length() - n + 1; i++)
            ngrams.add(str.substring(i, i + n));
        // to add the end of the string
        if (str.length() >= n)
            ngrams.add("$" + str.substring(str.length() - n + 1, str.length()));
        return ngrams;
    }

    private void add_n_gram(int word_idx, ArrayList<String> word_n_gram) {
        for (String gram : word_n_gram)
            if (this.all_grams.containsKey(gram)) {
                this.all_grams.get(gram).add(word_idx);
            } else {
                ArrayList<Integer> a = new ArrayList<>();
                a.add(word_idx);
                this.all_grams.put(gram, a);
            }
    }

    /**
     * calculate subset to calculate the edit distance on
     * @param word
     * @param top
     * @return
     */
    public ArrayList<String> getTopMatchWords(String word, int top) {
        ArrayList<String> top_words = new ArrayList<>();
        ArrayList<String> n_gram = create_index(this.k, word);
        HashMap<Integer, Double> all_scores = new HashMap<Integer, Double>();
        int len_gram = n_gram.size();
        for (String gram : n_gram) {
            ArrayList<Integer> with_word = new ArrayList<>();
            if (this.all_grams.containsKey(gram))
                with_word = this.all_grams.get(gram);
            for (Integer word_idx : with_word) {
                if (all_scores.containsKey(word_idx)) {
                    all_scores.put(word_idx, all_scores.get(word_idx) + 1);
                } else {
                    all_scores.put(word_idx, 1.0);
                }
            }
        }
        //compute  Jaccard coefficient
        for (Map.Entry<Integer, Double> en : all_scores.entrySet()) {
            en.setValue(en.getValue() / (double) (-en.getValue() + len_gram
                    + create_index(this.k, this.word_list.get(en.getKey())).size()));
        }
        HashMap<Integer, Double> sorted = Utils.sortByValue(all_scores);
        for (Map.Entry<Integer, Double> en : sorted.entrySet()) {
            if (top > 0) {
                top_words.add(this.word_list.get(en.getKey()));
                top -= 1;
            }
        }
        return top_words;

    }

    public String getMostMatchingWord(String word_to_search) {
        //get a subset to compute the Edit distance on
        ArrayList<String> top_match = this.getTopMatchWords(word_to_search, 30);
        if (this.model_languge){
            return EditDistance.get_top_matching_words_with_freq(word_to_search, top_match);
        }
        else{
            return EditDistance.get_top_matching_words(word_to_search, top_match);
        }
    }

}
