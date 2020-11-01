import java.util.ArrayList;

public interface SpellingPolicy {
    abstract  public String getMostMatchingWord(String word_to_search);
    abstract public ArrayList<String> getTopMatchWords(String word_to_search, int top);
}
