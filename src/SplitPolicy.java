import java.util.ArrayList;

public class SplitPolicy implements SpellingPolicy {
    private ArrayList<String>word_list;
    SplitPolicy(ArrayList<String> word_list){
        this.word_list = word_list;
    }

    public String checkWord(String word){
        String w = "";
        for (int j = 0; j < word.length() + 1; j++) {
            if(this.word_list.contains(word.substring(0,j)) &&this.word_list.contains(word.substring(j,word.length()))){
                w = word.substring(0,j)+" "+word.substring(j,word.length());
            }
        }
        return w;

    }
    public String getMostMatchingWord(String word_to_search) {
        return checkWord(word_to_search);
    }

    public ArrayList<String> getTopMatchWords(String word_to_search, int top){
        ArrayList<String> a = new ArrayList<>();
        a.add(checkWord(word_to_search));
        return a;
    }
}
