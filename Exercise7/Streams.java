import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Streams {
    public static void main(String[] args) throws IOException {
        // Check input
        if (args.length != 1) {
            System.out.println("Invalid Input");
            return;
        }

        // Load stop words through line
        List<String> stop_words_list = new ArrayList<String>();
        Files.lines(Path.of("stop_words.txt"))
                .flatMap(line -> Arrays.stream(line.split(","))) // Stream lines
                .map(String::valueOf)
                .forEach(word -> stop_words_list.add(word));
        
        // Stream words from text file into map, sort, and print
        System.out.print("---------- Word counts (top 25) -----------\n");
        Files.lines(Path.of(args[0]))
                .flatMap(line -> Arrays.stream(line.split("[^a-zA-Z]+")) // Stream lines
                .map(String::toLowerCase)
                .filter(line_word -> !stop_words_list.contains(line_word) && line_word.length() >= 2))
                .collect(Collectors.toMap(word -> word, frequency -> 1, Integer::sum))
                .entrySet().stream() //Stream Map entries
                .sorted(Map.Entry.comparingByValue((a, b) -> b - a)).limit(25)
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }
}