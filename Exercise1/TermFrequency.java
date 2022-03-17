/* SWE262P: Exercise 1 - Joseph Lee
TermFrequency - tokenizes words in the inputted txt file and counts the term frequencies of 25 most counted words. 
Use the command line to run. (e.g. java TermFrequency.java pride-and-prejudice.txt)
*/

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class TermFrequency{

    public static Set<String> stop_words = new HashSet<>();
    public static HashMap<String, Integer> frequencies = new HashMap<>();
    public static Path filepath;

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }

        filepath = Paths.get(args[0]);

        // Load Stop words
        loadStopWords();

        // Load TXT and Count
        try {
            try (Stream<String> lines = Files.lines(filepath)) {
                lines.forEach(line -> { readLines(line); });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // sort results
        List<Map.Entry<String, Integer>> sorted_list = new ArrayList<>(frequencies.entrySet());
        Collections.sort(sorted_list, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1,
                                       Map.Entry<String, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

        // print results
        StringBuilder sb = new StringBuilder("---------- Word counts (top 25) -----------\n");
        for (int i = 0; i < 25; ++i) {
            final Map.Entry<String, Integer> entry = sorted_list.get(i);
            sb.append(entry.getKey()).append("  -  ").append(entry.getValue()).append("\n");
        }
        System.out.println(sb);
    }


    private static void readLines(String line) {
        String[] words = line.split("[^a-zA-Z]+");

        for (String word : words) {
            String w = word.toLowerCase();
            if (!stop_words.contains(w) && w.length() > 1) {
                if (frequencies.containsKey(w))
                    frequencies.put(w, frequencies.get(w)+1);
                else
                    frequencies.put(w, 1);
            }
        }
    }

    private static void loadStopWords() {
        String str = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("/home/runner/SWE262Java/stop_words.txt"));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
        String[] words = str.split(",");
        stop_words.addAll(Arrays.asList(words));
    }
}