import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.List;

enum Direction {
    DOWN, LEFT, DIAG_CHANGE, DIAG_NO_CHANGE, NOT_REAL,DIAG_TWO_BACK
}

public class EditDistance {
    private static Map<String, Double> freq = Utils.getFreqFile();

    public static double get_freq(String word){
        if (freq.containsKey(word))
            return freq.get(word);
        return 0.000000001;
    }

    public static String get_top_matching_words_with_freq(String word, ArrayList<String> words_rank) {
        double top_rank = 1000;
        String top_word = "";
        for (String w : words_rank) {
            double rank = calculateDistance(word, w);
            if (freq.containsKey(w))
                rank = rank - freq.get(w);
            if (top_rank > rank) {
                top_word = w;
                top_rank = rank;
            }
        }
        return top_word;
    }

    public static String get_top_matching_words(String word, ArrayList<String> words_rank) {
        double top_rank = 1000;
        String top_word = "";
        for (String w : words_rank) {
            double rank = calculateDistance(word, w);
            if (top_rank > rank) {
                top_word = w;
                top_rank = rank;
            }
        }
        return top_word;
    }

    /**
     * @param source word1
     * @param target word2
     * @return the edit distance between 2 words, if we add a letter/remove a
     *         letter/ switch 2 letters we campute all of thouse as edit distance 1.
     */
    public static int calculateDistance(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        // compute edit distance of base case
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int char_cost = 0;
                if (source.charAt(i - 1) != target.charAt(j - 1)) {
                    char_cost += 1;
                }
                int cost_with_no_switch = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1),
                        dist[i - 1][j - 1] + char_cost);
                int cost_with_switch = Integer.MAX_VALUE;
                if (i > 1 && j > 1 && source.charAt(i - 1) == target.charAt(j - 2)
                        && source.charAt(i - 2) == target.charAt(j - 1)) {
                    cost_with_switch = dist[i - 2][j - 2] + char_cost;
                }
                dist[i][j] = Math.min(cost_with_no_switch, cost_with_switch);
            }
        }
        return dist[sourceLength][targetLength];
    }

    public static ArrayList<Edit> getEditArray(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();
        Pair[][] DictMatrix = calculateDictMatrix(source, target);
        ArrayList<Edit> pairs = new ArrayList<>();
        while (sourceLength >0 && targetLength > 0) {
            switch (DictMatrix[sourceLength][targetLength].getKey()) {
                case DIAG_CHANGE: {
                    Edit e = new Edit();
                    e.type = EditType.SUBSTITUTION;
                    e.source_index = sourceLength - 1;
                    e.target_index = targetLength - 1;
                    pairs.add(e);
                    targetLength -= 1;
                    sourceLength -= 1;
                    break;
                }
                case DIAG_NO_CHANGE: {
                    targetLength -= 1;
                    sourceLength -= 1;
                    break;
                }
                case LEFT: {
                    Edit e = new Edit();
                    e.type = EditType.DELETION;
                    e.source_index = sourceLength-1;
                    e.target_index = targetLength;
                    pairs.add(e);
                    sourceLength -= 1;
                    break;
                }
                case DIAG_TWO_BACK:{
                    Edit e = new Edit();
                    e.type = EditType.TRANSPOSITION;
                    e.source_index = sourceLength - 1;
                    e.target_index = targetLength - 1;
                    pairs.add(e);
                    sourceLength -= 2;
                    targetLength -= 2;
                    break;
                }
                case DOWN: {
                    Edit e = new Edit();
                    e.type = EditType.INSERTION;
                    e.source_index = sourceLength;
                    e.target_index = targetLength-1;
                    pairs.add(e);
                    targetLength -= 1;
                    break;
                }
                case NOT_REAL: {
                    break;
                }
            }
        };
        while (targetLength>0){
            Edit e = new Edit();
            e.type = EditType.INSERTION;
            e.source_index = sourceLength;
            e.target_index = targetLength-1;
            pairs.add(e);
            targetLength -= 1;
        }
        while (sourceLength>0){
            Edit e = new Edit();
            e.type = EditType.DELETION;
            e.source_index = sourceLength-1;
            e.target_index = targetLength;
            pairs.add(e);
            sourceLength -= 1;
        }
        Collections.reverse(pairs);
        return pairs;
    }

    public static ArrayList<StringPair> getAlignment(String source, String target, int window) {
        int sourceLength = source.length();
        int targetLength = target.length();
        Pair[][] DictMatrix = calculateDictMatrix(source, target);
        ArrayList<StringPair> pairs = new ArrayList<>();
        while (sourceLength != 0 || targetLength != 0) {
            switch (DictMatrix[sourceLength][targetLength].getKey()) {
                case DIAG_CHANGE: {
                    pairs.add(new StringPair(source.charAt(sourceLength - 1), target.charAt(targetLength - 1)));
                    targetLength -= 1;
                    sourceLength -= 1;
                    break;
                }
                case DIAG_NO_CHANGE: {
                    pairs.add(new StringPair(source.charAt(sourceLength - 1), target.charAt(targetLength - 1)));
                    targetLength -= 1;
                    sourceLength -= 1;
                    break;
                }
                case DOWN: {
                    pairs.add(new StringPair(source.charAt(sourceLength - 1), '*'));
                    sourceLength -= 1;
                    break;
                }
                case LEFT: {
                    pairs.add(new StringPair('*', target.charAt(targetLength - 1)));
                    targetLength -= 1;
                    break;
                }
                case DIAG_TWO_BACK:{
                    pairs.add(new StringPair(source.charAt(sourceLength - 1), target.charAt(targetLength - 1)));
                    targetLength -=2;
                    sourceLength -=2;
                }
                case NOT_REAL: {
                    break;
                }
            }
        }
        Collections.reverse(pairs);
        ArrayList<StringPair> p = extendAlignments(pairs, window);
        return p;
    }

    public static Pair[][] calculateDictMatrix(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();
        Pair[][] dist = new Pair[sourceLength + 1][targetLength + 1];
        // compute edit distance of base case
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = new Pair(i, Direction.DOWN);
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = new Pair(j, Direction.LEFT);
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                Pair minimal_cost = new Pair(Integer.MAX_VALUE, Direction.NOT_REAL);
                if (source.charAt(i - 1) != target.charAt(j - 1)) {
                    minimal_cost.key = Direction.DIAG_CHANGE;
                    minimal_cost.value = dist[i - 1][j - 1].getValue() + 1;// if substitution is 1
                } else {
                    // no change
                    minimal_cost.key = Direction.DIAG_NO_CHANGE;
                    minimal_cost.value = dist[i - 1][j - 1].getValue();
                }
                if (i > 1 && j > 1 && source.charAt(i - 1) == target.charAt(j - 2)
                        && source.charAt(i - 2) == target.charAt(j - 1)) {
                    if(minimal_cost.value> dist[i - 2][j - 2].getValue()+1){
                        minimal_cost.value = dist[i - 2][j - 2].getValue()+1;
                        minimal_cost.key = Direction.DIAG_TWO_BACK;
                    }
                }
                if (dist[i - 1][j].getValue() + 1 < minimal_cost.value) {
                    minimal_cost.value = dist[i-1][j].getValue() + 1;
                    minimal_cost.key = Direction.LEFT;
                }
                if (dist[i][j - 1].getValue() + 1 < minimal_cost.value) {
                    minimal_cost.value = dist[i][j - 1].getValue() + 1;
                    minimal_cost.key = Direction.DOWN;
                }
                dist[i][j] = minimal_cost;
            }
        }
        return dist;
    }

    public static ArrayList<StringPair> extendAlignments(ArrayList<StringPair> alignments, int window) {
        ArrayList<StringPair> padded = new ArrayList<>();
        ArrayList<StringPair> combined = new ArrayList<>();
        padded.addAll(alignments);
        for (int i = 0; i < padded.size(); i++) {
            for (int j = 0; j <= window; j++) {
                int left_bound = i - j;
                int right_bound = i + j + 1;
                if (left_bound != i && i + 1 != right_bound) {
                    if (right_bound <= padded.size()) {
                        StringPair candit = combineAlignments(padded.subList(i, right_bound));
                        if (!candit.same_value()) {
                            combined.add(candit);
                        }
                    }
                }
                if (left_bound >= 0 && i + 1 <= padded.size()) {
                    StringPair candit = combineAlignments(padded.subList(left_bound, i + 1));
                    if (!candit.same_value()) {
                        combined.add(candit);
                    }
                }
            }
        }
        return combined;
    }

    public static StringPair combineAlignments(List<StringPair> alignments) {
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (StringPair a : alignments) {
            keys.append(a.key);
            values.append(a.value);
        }
        return new StringPair(keys.toString(), values.toString());
    }

}
