import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Iterators {

    private static String filepath;

    public static void main(String[] args) {

        // Check input
        if (args.length != 1) {
            System.out.println("Invalid Input");
            return;
        }
        filepath = args[0];

        try {

            WordLines lines = new WordLines(filepath);
            AllWords words = new AllWords(lines);
            StopWordsFilter words_filtered = new StopWordsFilter(words);
            CountAndSort freq_map_iter = new CountAndSort(words_filtered);
            List<Map.Entry<String,Integer>> output = freq_map_iter.next();

            System.out.print("---------- Word counts (top 25) -----------\n");
            output.stream()
                    .limit(25)
                    .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    /**
     * line_iterator class which iterates through the input TXT lines.
     */
    public static class WordLines implements Iterator<String> {
        public Iterator<String> word_lines;

        WordLines(String filepath) throws IOException {
            this.word_lines =  Files.lines(Path.of(filepath))
                    .iterator();
        }

        @Override
        public boolean hasNext() {
            return word_lines.hasNext();
        }

        @Override
        public String next() {
            return word_lines.next();
        }
    }

    /**
     * AllWords iterates through each line from the file, and iterates through the words.
     */
    public static class AllWords implements Iterator<String> {
        public Iterator<String> prior; //lines
        public Iterator<String> words_iter;
        public List<String> line_words;

        AllWords(Iterator<String> p){
            this.prior = p;
            this.line_words = Arrays.stream(prior.next().toLowerCase().split(("[^a-zA-Z0-9]+"))).collect(Collectors.toList());
            this.words_iter = line_words.iterator();
        }

        @Override
        public boolean hasNext() {
            return prior.hasNext();
        }

        @Override
        public String next() {

            // Iterate out words in line
            while(words_iter.hasNext()){
                return words_iter.next();
            }

            // Advance lines
            if(prior.hasNext()){
                line_words = Arrays.stream(prior.next().toLowerCase().split(("[^a-zA-Z0-9]+"))).collect(Collectors.toList());
                words_iter = line_words.iterator();
            }
            return "";
        }
    }


    /**
     *  StopWordsFilter iterates through words and filters stop words
     */
    public static class StopWordsFilter implements Iterator<String>{
        public Iterator<String> prior;
        public List<String> stop_words = new ArrayList<>();

        StopWordsFilter(Iterator<String> p) throws IOException {
            this.prior = p;

            //Loading stops words into the list
            Files.lines(Path.of("stop_words.txt"))
                    .flatMap(line -> Arrays.stream(line.split(",")))
                    .map(String::valueOf)
                    .forEach(word -> stop_words.add(word));
        }

        @Override
        public boolean hasNext() {
            return prior.hasNext();
        }

        @Override
        public String next() {
            while(prior.hasNext()){
                String word = prior.next();

                // Filter words_iter using stop words
                if(!stop_words.contains(word) && word.length() >= 2 ){
                    return word;
                }
                return null;
            }
            return null;
        }
    }


    /**
     * CountAndSort iterates through words iterator and constructs an iteratable Map
     */
    public static class CountAndSort implements Iterator<List<Map.Entry<String, Integer>>>{

        private Iterator<String> prior;
        private HashMap<String, Integer> freq_map = new HashMap<String,Integer>();

        CountAndSort(Iterator<String> p){
            this.prior = p;
        }

        @Override
        public boolean hasNext() {
            return prior.hasNext();
        }

        @Override
        public List<Map.Entry<String, Integer>> next() {

            // Iterate thru and construct frequency map
            int count = 1;
            while(prior.hasNext()){
                String word = prior.next();
                count++;

                if(word!=null){
                    if(freq_map.containsKey(word)){
                        // word already exists, increment counter
                        freq_map.put(word, freq_map.get(word) + 1);
                    } else {
                        // new word, create new entry
                        freq_map.put(word, 1);
                    }
                }
            }

            //Convert hashmap and sort
            List<Map.Entry<String, Integer>> sorted_freq_map = new ArrayList<>(freq_map.entrySet());
            Collections.sort(sorted_freq_map, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            return sorted_freq_map;
        }
    }
}
