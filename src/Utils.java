import java.io.*;
import java.util.*;

public class Utils {
    static double min_value_frq = 0.0000001;
    private static HashMap<String, ArrayList<String>> trainMisspelledWords = new HashMap<>();
    private static HashMap<String, ArrayList<String>> testMisspelledWords = new HashMap<>();

    public static void init(double trainRatio) {
        HashMap<String, ArrayList<String>> allWords = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("common_misspelling.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                String[] pair = str.split(";");
                String misspel = pair[0].toLowerCase();
                String incorrect = pair[1].toLowerCase();
                String[] correctWords = incorrect.split(",");
                for (String word : correctWords) {
                    ArrayList<String> misspelledWords = allWords.get(word.trim());
                    if (misspelledWords == null) {
                        misspelledWords = new ArrayList<>();
                        allWords.put(word.trim(), misspelledWords);
                    }
                    misspelledWords.add(misspel);
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for (Map.Entry<String, ArrayList<String>> entry : allWords.entrySet()) {
            if (Math.random() < trainRatio) {
                trainMisspelledWords.put(entry.getKey(), entry.getValue());
            } else {
                testMisspelledWords.put(entry.getKey(), entry.getValue());
            }
        }

    }
    public static boolean isAlphanumeric(String str)
    {
        char[] charArray = str.toCharArray();
        for(char c:charArray)
        {
            if (!Character.isLetter(c))
                return false;
        }
        return true;
    }

    public static ArrayList<String> get_words() {
        ArrayList<String> all_words = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("linux.words"));
            String str;
            while ((str = in.readLine()) != null) {
                if (isAlphanumeric(str)){
                    all_words.add(str.toLowerCase());
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return all_words;
    }

    /**
     * @return correct word -> [misspelled]
     */
    public static HashMap<String, ArrayList<String>> getTrainMisspelledWords() {

        return trainMisspelledWords;
    }

    /**
     * @return correct word -> [misspelled]
     */
    public static HashMap<String, ArrayList<String>> getTestMisspelledWords() {

        return testMisspelledWords;
    }

    public static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static Map<String, Double> getFreqFile() {
        FileInputStream stream = null;
        HashMap<String, Long> freq = new HashMap<>();
        long total = 0;
        try {
            stream = new FileInputStream("freq_words.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String strLine;
        try {
            while ((strLine = reader.readLine()) != null) {
                String[] tokens = strLine.split("\t");
                freq.put(tokens[0], Long.parseLong(tokens[1]));
                total += Long.parseLong(tokens[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, Double> freq2 = new HashMap<String, Double>();
        for (Map.Entry<String, Long> e : freq.entrySet())
            freq2.put(e.getKey(), (double) (e.getValue() / (double) total));
        return freq2;
    }

    public static Map<String, Double> getFreqFileRaw() {
        FileInputStream stream = null;
        HashMap<String, Double> freq = new HashMap<>();
        try {
            stream = new FileInputStream("freq_words.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String strLine;
        try {
            while ((strLine = reader.readLine()) != null) {
                String[] tokens = strLine.split("\t");
                freq.put(tokens[0], Double.parseDouble(tokens[1]));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freq;
    }
}
