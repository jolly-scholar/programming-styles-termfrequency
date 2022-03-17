import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Style 32: Double map reduce -
 *
 * Constraints:
 * Input data is divided in chunks, similar to what an inverse multiplexer does to input signals
 * A map function applies a given worker function to each chunk of data, potentially in parallel
 * The results of the many worker functions are reshuffled in a way that allows for the reduce step to be also parallelized
 * The reshuffled chunks of data are given as input to a second map function that takes a reducible function as input
 *
 * Possible names:
 * Map-reduce
 * Hadoop style
 * Double inverse multiplexer
 *
 * https://github.com/crista/exercises-in-programming-style/tree/master/32-double-map-reduce
 */
public class ThirtyTwo {

    static List<String> stop_words = new ArrayList<>();

    /**
     * partition - Partition the input data_str (a big string) into chunks of nlines.
     */
    public static List<List<String>> partition(String data_str, int nlines){
        List<String> lines = Arrays.asList(data_str.split("\n"));
        List<List<String>> partition_set = new ArrayList<>();

        // for each line
        int line = 0;
        while(line < lines.size()){
            List<String> partition;

            if(line + nlines > lines.size()){
                partition = new ArrayList<>(lines.subList(line, lines.size()));
            } else {
                partition = new ArrayList<>(lines.subList(line, line + nlines));
            }
            partition_set.add(partition);
            line = line + nlines;
        }

        return partition_set;
    }

    /***
     *  split_words -
     *     Takes a string, returns a list of pairs (word, 1),
     *     one for each word in the input, so
     *     [(w1, 1), (w2, 1), ..., (wn, 1)]
     */
    public static List<Map.Entry<String, Integer>> split_word(List<String> partiion){
        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        partiion.forEach(line -> {
            Arrays.stream(line.toLowerCase(Locale.ROOT).split("[^a-zA-Z0-9]+"))
                    .filter(word -> !stop_words.contains(word) && word.length() >= 2)
                    .collect(Collectors.toList())
                    .forEach(word -> result.add(new AbstractMap.SimpleEntry<String, Integer>(word, 1)));
        });

        return result;
    }

    /**
     * regroup -
     *     Takes a list of lists of pairs of the form
     *     [[(w1, 1), (w2, 1), ..., (wn, 1)],
     *      [(w1, 1), (w2, 1), ..., (wn, 1)],
     *      ...]
     *     and returns a dictionary mapping each unique word to the
     *     corresponding list of pairs, so
     *     { w1 : [(w1, 1), (w1, 1)...],
     *       w2 : [(w2, 1), (w2, 1)...],
     *       ...}
     */
    public static HashMap<Character, List<Map.Entry<String, Integer>>> regroup(List<List<Map.Entry<String, Integer>>> pairs_list){
        HashMap<Character, List<Map.Entry<String, Integer>>> mapping = new HashMap<>();

        for(List<Map.Entry<String, Integer>> pairs : pairs_list){
            pairs.forEach(entry -> {
                Character c = entry.getKey().charAt(0);
                if(mapping.containsKey(c)) {
                    mapping.get(c).add(entry);
                } else {
                    mapping.put(c, new ArrayList<>());
                    mapping.get(c).add(entry);
                }
            });
        }
        return mapping;
    }


    /**
     * count_words -
     *     Takes a mapping of the form (word, [(word, 1), (word, 1)...)])
     *     and returns a pair (word, frequency), where frequency is the
     *     sum of all the reported occurrences
     */
    public static HashMap<String,Integer> count_words(HashMap<Character, List<Map.Entry<String, Integer>>> mapping){

        HashMap<String, Integer> result = new HashMap<>();
        for (Map.Entry entry : mapping.entrySet()) {
            List<Map.Entry<String, Integer>> pair_list = (List<Map.Entry<String, Integer>>) entry.getValue();
            for (int i = 0; i < pair_list.size(); i++) {
                    String word = pair_list.get(i).getKey();
                if (result.containsKey(word)) {
                    result.put(word, result.get(word) + 1);
                } else {
                    result.put(word, 1);
                }
            }
        }
        return result;
    }


    public static List<Map.Entry<String, Integer>> sort(HashMap<String,Integer> word_freq) {

        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(word_freq.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }

    ///// AUXILARY FXNS
    /**
     * LoadStopWords - Load stop words from TXT
     */
    public static void LoadStopwords(){
        try {
            Files.lines(Path.of("stop_words.txt"))
                    .flatMap(line -> Arrays.stream(line.split(",")))
                    .map(String::valueOf)
                    .forEach(word -> stop_words.add(word));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load File - Load the TXT into a raw String
     */
    public static String LoadFile(String filepath){
        String content = null;
        try {
            content = Files.readString(Path.of(filepath), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void print_freqs(List<Map.Entry<String, Integer>> freq_map){

        final StringBuilder result = new StringBuilder();
        System.out.print("---------- Word counts (top 25) -----------\n");
        for (int i = 0; i < 25; ++i) {
            final Map.Entry<String, Integer> entry = freq_map.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    /**
     * Main Method
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid Input");
            return;
        }
        String filename = args[0];

        // Load stop words
        LoadStopwords();

        // Load TXT
        String raw_data = LoadFile(filename);

        List<List<Map.Entry<String, Integer>>> splits = partition(raw_data, 200).parallelStream()
                .map(ThirtyTwo::split_word)
                .collect(Collectors.toList());

        HashMap<Character, List<Map.Entry<String, Integer>>> splits_per_word = regroup(splits);
        List<Map.Entry<String, Integer>> freq_map = sort(count_words(splits_per_word));

        //print
        print_freqs(freq_map);
    }
}