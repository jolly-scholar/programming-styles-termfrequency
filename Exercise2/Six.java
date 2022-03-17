/* SWE262P: Exercise 2: Pipeline - Joseph Lee
TermFrequency - tokenizes words in the inputted txt file and counts the term frequencies of 25 most counted words.
Use the command line to run. (e.g. java TermFrequency.java pride-and-prejudice.txt)
*/

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Six{

    public static void main(String[] args) {
        printTop25(sortListDesc(getTermFrequency(readInputDir(args))));        
    }
    
    private static Path readInputDir(String[] args) {
        if(args.length != 1){
            System.out.println("Invalid Input");
            return null;
        }
        Path filepath = Paths.get(args[0]);
        return filepath;
    }

    private static Set<String> loadStopWords(String filepath) {
        String str = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filepath));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
        String[] words = str.split(",");
        Set<String> stop_words = new HashSet<>();
        stop_words.addAll(Arrays.asList(words));

        return stop_words;
    }


      private static List<Map.Entry<String, Integer>> sortListDesc(HashMap<String, Integer> frequencies) {
          List<Map.Entry<String, Integer>> sorted_list = new ArrayList<>(frequencies.entrySet());
          Collections.sort(sorted_list, new Comparator<Map.Entry<String, Integer>>() {
              public int compare(Map.Entry<String, Integer> o1,
                                Map.Entry<String, Integer> o2) {
                  return o2.getValue().compareTo(o1.getValue());
              }
          });
          return sorted_list;
      }

    private static HashMap<String, Integer> getTermFrequency(Path filepath) {
        HashMap<String, Integer> frequencies = new HashMap<>();
        Set<String> stop_words = loadStopWords("/home/runner/SWE262Java/stop_words.txt");        // Load Stop words
        
        try {
            try (Stream<String> lines = Files.lines(filepath)) {
                lines.forEach(line -> {
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
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frequencies;
    }


    private static void printTop25(List<Map.Entry<String, Integer>> sorted_list) {
        StringBuilder sb = new StringBuilder("---------- Word counts (top 25) -----------\n");
        for (int i = 0; i < 25; ++i) {
            final Map.Entry<String, Integer> entry = sorted_list.get(i);
            sb.append(entry.getKey()).append("  -  ").append(entry.getValue()).append("\n");
        }
        System.out.println(sb);
    }

}