import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* Style 9:
    Functional interface in java:
    https://www.geeksforgeeks.org/functional-interfaces-java/
    https://www.youtube.com/watch?v=GADhzhK6NU0&ab_channel=JavaTechie
*/

public class Nine {


    // fxn 1): Read the file and store it in a List
    private static void getPath(String[] args,
                                       BiConsumer<Path,
                                           BiConsumer<Stream<String>,
                                               BiConsumer<HashMap<String, Integer>,
                                                   BiConsumer<HashMap<String, Integer>,
                                                       Consumer<Object>>>>> function) {
        if (args.length != 1) {
            System.out.println("Invalid Input");
            System.exit(1);
        }

        // Define filename and start creating frequency map
        String filename = args[0];
        function.accept(Path.of(filename), Nine::createFrequencyMap);
        return;
    }

    private static void readLines(Path path,
                                           BiConsumer<Stream<String>,
                                                   BiConsumer<HashMap<String, Integer>,
                                                           BiConsumer<HashMap<String, Integer>,
                                                                   Consumer<Object>>>> function) {
        try {
            function.accept(Files.lines(path), Nine::sort_desc);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // fxn 2) Receive the list, load the stop_words, and create frequency map
    private static void createFrequencyMap(Stream<String> lines,
                                           BiConsumer<HashMap<String, Integer>,
                                                   BiConsumer<HashMap<String,Integer>,
                                                           Consumer<Object>>> function) {

            // Load stop_words.txt to compare
            List<String> stop_words_list =  new ArrayList<String>();
            try {
                Files.lines(Path.of("stop_words.txt"))
                        .flatMap(line -> Arrays.stream(line.split(",")))
                        .map(String::valueOf)
                        .forEach(word -> stop_words_list.add(word));
            } catch (IOException e) {
                System.out.println("Failed to load stop_words.txt, make sure this exists in the root directory.");
                e.printStackTrace();
            }

            // Compare the stop_words_list and words_list and add to the new_list if
            HashMap<String, Integer> words_freq_map = new HashMap<String, Integer>();
            lines.forEach(line -> {
                String[] words = line.split("[^a-zA-Z]+");
                for (String word : words) {
                    String w = word.toLowerCase();
                    if (!stop_words_list.contains(w) && w.length() > 1) {
                        if (words_freq_map.containsKey(w)) {
                            words_freq_map.put(w, words_freq_map.get(w) + 1);
                        } else {
                            words_freq_map.put(w, 1);
                        }
                    }
                }
            });

            function.accept(words_freq_map, Nine::print);
            return;
        }


    // Step 3: Sort
    private static void sort_desc(HashMap<String, Integer> frequency_map,
                                        BiConsumer<HashMap<String,Integer>,
                                            Consumer<Object>> function) {
        
            //create set and turn to list
            Set<Map.Entry<String, Integer>> s = frequency_map.entrySet();
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(s);

            //sort by values using comparator
            Collections.sort(list, new Comparator<Map.Entry<String,Integer>>(){
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            // Then convert back to map
            HashMap<String, Integer> sorted_list =
                    list.stream().limit(25)
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1,v2)->v1,
                                    LinkedHashMap::new));


            function.accept(sorted_list, Nine::do_nothing);
        }


    // Step 4: Print
    private static void print(HashMap<String, Integer> sorted_map,
                              Consumer<Object> obj){

            System.out.print("---------- Word counts (top 25) -----------\n");
            sorted_map.entrySet().stream()
                    .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));

            return;
        }

    // Do nothing until the end
    private static void do_nothing(Object object) { }

    // Main function.
    public static void main(String args[]) {
        getPath(args, Nine::readLines);
    }

}