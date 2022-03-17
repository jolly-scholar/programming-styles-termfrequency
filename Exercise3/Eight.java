import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/* SWE262P - Joseph
Style Eight: Recursive parse (infinite mirror)
 */

public class Eight {

    private static List<String> loadCSVWords(String filepath) throws IOException {
        List<String> stop_words_list = new ArrayList<String>();
        Files.lines(Path.of(filepath))
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(String::valueOf)
                .forEach(word -> stop_words_list.add(word));

        return stop_words_list;
    }

    // Recursive character parse function
    public static List<String> parse(StringReader reader, List<String> words_list, List<String> stop_words_list) throws IOException {

        // input character and string buffer
        int c_int = reader.read();
        StringBuilder word_buffer = new StringBuilder();

        // if end of line, return the list
        if ( c_int == -1 ) {
            return words_list;
        } else {
            char c = (char) c_int;
            // If the ith char in the input stream is alphabetical or numerical (ASCII)
            while((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') ){
                // Add the character to the word buffer
                word_buffer.append(Character.toLowerCase(c));
                c = (char) reader.read();
            }

            // If end of word
            if(!stop_words_list.contains(word_buffer.toString()) && word_buffer.length() >= 2){
                words_list.add(word_buffer.toString());
            }

            // Recurse until end of line is reached
            return parse(reader, words_list, stop_words_list);
        }
    }

    private static HashMap<String, Integer> sortDesc(HashMap<String, Integer> word_map) {

        List<Map.Entry<String,Integer>> list = new ArrayList<>(word_map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> frequencies =
                list.stream().limit(25)
                        .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1,v2)->v1,
                        LinkedHashMap::new));

        return frequencies;
    }

    private static Map<String, Integer> getTermFrequency(List<String> words_list) {

        Map<String,Integer> freq_map = words_list.stream()
                        .collect(Collectors.toMap(word -> word, frequency -> 1, Integer::sum));
        return freq_map;
    }

    public static void printTop25(HashMap<String,Integer> sorted_map){
        System.out.print("---------- Word counts (top 25) -----------\n");
        StringBuilder sb = new StringBuilder("---------- Word counts (top 25) -----------\n");
        sorted_map.entrySet().stream()
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));;
        return;
    }

     public static void main(String[] args) throws IOException {

        // Check input
        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }
        String filepath = args[0];

        // Scanner instances for reading the character inputs
        BufferedReader input_reader = new BufferedReader(new FileReader(filepath));
        List<String> words_list = new ArrayList<String>();
        List<String> stop_words = loadCSVWords("stop_words.txt");
        HashMap<String, Integer> frequency_map;

        String line = input_reader.readLine();
        while(line != null){
            StringReader rd = new StringReader(line);
            List<String> line_words = parse(rd, new ArrayList<String>() , stop_words );
            words_list.addAll(line_words);
            line = input_reader.readLine();
            rd.close();
        }
        input_reader.close();

        frequency_map = new HashMap<String, Integer>(getTermFrequency(words_list));
        HashMap<String, Integer> sorted_list = sortDesc(frequency_map);
        printTop25(sorted_list);

    }
}
