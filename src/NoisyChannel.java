import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NoisyChannel implements SpellingPolicy {

    private ConfusionMatrix insertMatrix = new ConfusionMatrix();
    private ConfusionMatrix deleteMatrix = new ConfusionMatrix();
    private ConfusionMatrix substituteMatrix = new ConfusionMatrix();
    private ConfusionMatrix transposeMatrix = new ConfusionMatrix();
    private LetterCount count = new LetterCount();
    private Ngram ngram;
    private Map<String, Double> freq = Utils.getFreqFile();

    public String getMostMatchingWord(String word_to_search) {
        return this.getTopMatchWords(word_to_search, 50).get(0);
    }

    public ArrayList<String> getTopMatchWords(String wordToSearch, int top) {

        ArrayList<String> topMatchWords = ngram.getTopMatchWords(wordToSearch, 30);

        topMatchWords.sort(new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                if (first.equals(second)) {
                    return 0;
                }
                double firstScore = EditDistance.get_freq(first);
                double secondScore = EditDistance.get_freq(second);
                for (Edit edit : EditDistance.getEditArray(first, wordToSearch)){
                    firstScore *= getScore(first, wordToSearch, edit);
                }
                for (Edit edit : EditDistance.getEditArray(second, wordToSearch)){
                    secondScore *= getScore(second, wordToSearch, edit);
                }
                if (firstScore > secondScore) {
                    return -1;
                }
                return 1;
            }
        });
        return new ArrayList<String>(topMatchWords.subList(0, Math.min(top, topMatchWords.size())));
    }

    public double getScore(String source, String target, Edit edit) {
        if (source.equals(target)) {
            return 1;
        }
        switch (edit.type) {
            case INSERTION: {
                char xi = target.charAt(edit.target_index);
                char wim1;
                if (edit.source_index==0){
                    wim1 = '*';
                }
                else{
                    wim1 = source.charAt(edit.source_index-1);
                }
                 
                return ((double) this.insertMatrix.get(wim1, xi) + 1) / ((double) this.count.get(wim1) + 26);
            }
            case DELETION: {
                char wi = source.charAt(edit.source_index);
                char wim1;
                if (edit.source_index==0){
                    wim1 = '*';
                }
                else{
                    wim1 = source.charAt(edit.source_index-1);
                }
                return ((double) this.deleteMatrix.get(wim1, wi) + 1) / ((double) this.count.get(wim1,wi) + 26);
            }
            case SUBSTITUTION: {
                char xi = target.charAt(edit.target_index);
                char wi = source.charAt(edit.source_index);
                char wim1;
                if (edit.source_index==0){
                    wim1 = '*';
                }
                else{
                    wim1 = source.charAt(edit.source_index-1);
                }
                double a = this.substituteMatrix.get(xi, wi);
                double b = this.count.get(wim1);
                double c = ((double) this.substituteMatrix.get(xi, wi) + 1) /  ((double) this.count.get(wim1) + 26);
                return  ((double) this.substituteMatrix.get(xi, wi) + 1) /  ((double) this.count.get(wim1) + 26);
            }
            case TRANSPOSITION:{
                char wi = source.charAt(edit.source_index);
                char wip1;
                if (edit.source_index==source.length()){
                    wip1 = '*';
                }
                else{
                    wip1 = source.charAt(edit.source_index-1);
                }
                return ((double) this.transposeMatrix.get(wi, wip1) + 1) / ((double) this.count.get(wi,wip1) + 26);
            }
            default: {
                return 0;
            }

        }
    }

    public void print_matrixs() {
        System.out.println("this insert matrix");
        this.insertMatrix.print_matrix();
        System.out.println("this delete matrix");
        this.deleteMatrix.print_matrix();
        System.out.println("this substitue matrix");
        this.substituteMatrix.print_matrix();

    }

    public NoisyChannel(ArrayList<String> wordList) {
        HashMap<String, ArrayList<String>> misspelledWords = Utils.getTrainMisspelledWords();
        this.ngram = new Ngram(wordList, 2, false);

        // populate count
        for (String word : wordList) {
            count.add('*',word.charAt(0));
            for (int i = 0; i < word.length() - 1; i++) {
                count.add(word.charAt(i));
                count.add(word.charAt(i), word.charAt(i + 1));
            }
            count.add(word.charAt(word.length() - 1));
            count.add('*');
            count.add(word.charAt(word.length() - 1),'*');
        }

        // populate confusion matrices
        for (Map.Entry<String, ArrayList<String>> entry : misspelledWords.entrySet()) {
            String correct = entry.getKey();
            for (String incorrect : entry.getValue()) {
                ArrayList<Edit> pairs = EditDistance.getEditArray( correct.toLowerCase(),incorrect.toLowerCase());
                for (Edit pair : pairs) {
                    if (pair.type == EditType.INSERTION) {
                        if(pair.source_index != 0) {
                            this.insertMatrix.add(correct.charAt(pair.source_index-1), incorrect.charAt(pair.target_index));
                        }
                        else {
                            this.insertMatrix.add('*', incorrect.charAt(pair.target_index));
                        }

                    } else if (pair.type == EditType.DELETION) {
                        if (pair.source_index != 0){
                            this.deleteMatrix.add(correct.charAt(pair.source_index-1),correct.charAt(pair.source_index));
                        }else {
                            this.deleteMatrix.add('*', correct.charAt(pair.source_index));
                        }
                    } else if (pair.type == EditType.SUBSTITUTION){
                        this.substituteMatrix.add(correct.charAt(pair.source_index),correct.charAt(pair.source_index));
                    }else if (pair.type == EditType.TRANSPOSITION){
                        if (pair.source_index<correct.length())
                            this.transposeMatrix.add(correct.charAt(pair.source_index-1), correct.charAt(pair.source_index));
                    }
                }
            }
        }
    }
}