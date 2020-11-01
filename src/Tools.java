import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Tools {
    public static void present_gui(){
        String word_to_search = JOptionPane.showInputDialog("put the word you are looking for :");
        JList list = new JList(new String[] {"" +
                "N-gram", "Split", "NoisyChannel","ImprovedNoisyChannel","All"});

        JOptionPane.showMessageDialog(
                null, list, "chose which policy you want to use", JOptionPane.PLAIN_MESSAGE);
        String top_word = "policy";
        if (list.getSelectedIndices()[0] == 0){
            JList how_gram = new JList(new String[] {"" +
                    "2-gram", "3-gram", "4-gram"});
            JOptionPane.showMessageDialog(
                    null, how_gram, "chose which n-gram policy you like", JOptionPane.PLAIN_MESSAGE);
            Ngram n = new Ngram(Utils.get_words(),how_gram.getSelectedIndices()[0]+2,true);
            top_word = n.getMostMatchingWord(word_to_search);
        }if(list.getSelectedIndices()[0] == 1){
            top_word = new SplitPolicy(Utils.get_words()).getMostMatchingWord(word_to_search);
        }
        if(list.getSelectedIndices()[0] == 2){
            top_word = new NoisyChannel(Utils.get_words()).getMostMatchingWord(word_to_search);
        }
        if(list.getSelectedIndices()[0] == 3){
            top_word = new ImprovedNoisyChannel(Utils.get_words(),2).getMostMatchingWord(word_to_search);
        }
        if(list.getSelectedIndices()[0] == 4){

            Ngram n = new Ngram(Utils.get_words(),2,true);
            String top_word_n_gram = n.getMostMatchingWord(word_to_search);
            top_word = new SplitPolicy(Utils.get_words()).getMostMatchingWord(word_to_search);
            String noysie_top_word = new NoisyChannel(Utils.get_words()).getMostMatchingWord(word_to_search);
            String improved_top_word = new ImprovedNoisyChannel(Utils.get_words(),2).getMostMatchingWord(word_to_search);
            String msg = "ngram got : "+top_word_n_gram+"\n"+"noisy got : "+noysie_top_word+"\n"+"improved got : "+improved_top_word ;
            JOptionPane.showMessageDialog(null, msg, " The Results ", JOptionPane.PLAIN_MESSAGE);


        }else {

            int result = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "the sum word you searched : " + top_word);

            if (result == 0)
                JOptionPane.showMessageDialog(null, " good job", " The Results ", JOptionPane.PLAIN_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, " oh no!!!", " The Results ", JOptionPane.PLAIN_MESSAGE);
        }
    }
    public static void check_all_words_in_policy(SpellingPolicy n){
        int misstake = 0;
        int correct = 0;
        int i = 0;
        try {
        FileWriter myPoasativeWriter = new FileWriter("correct.txt");
        FileWriter myNegativeWriter = new FileWriter("neg.txt");
        BufferedWriter bwriter_myNegativeWriter = new BufferedWriter(myNegativeWriter);


        for(Map.Entry<String, ArrayList<String>> en : Utils.getTestMisspelledWords().entrySet()){
            i+=1;
            for(String missppeled: en.getValue()){
                missppeled =missppeled.toLowerCase();
                String top_word = n.getMostMatchingWord(missppeled);
                String correct_word= en.getKey().toLowerCase();
                if(correct_word.equals(top_word)){
                    correct+=1;
                    myPoasativeWriter.write("the word ="+missppeled+" found = "+top_word+" should ="+en.getKey()+"\n");
                }else{
                    bwriter_myNegativeWriter.write("the word ="+missppeled+" found = "+top_word+" should ="+en.getKey()+"\n");
                    misstake+=1;
                }
            }
            System.out.println("-----------------------------------");
            System.out.println("num masstake :"+misstake+" num correct :"+correct);
        }
        bwriter_myNegativeWriter.close();
        myPoasativeWriter.close();
        System.out.println("finally the total precentage was "+((double)correct/(double) (correct+misstake)));
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println("num masstake :"+misstake+" num correct :"+correct);
    }
}
