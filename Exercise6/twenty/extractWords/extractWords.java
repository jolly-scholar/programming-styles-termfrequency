import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *  extractWords plugin:
 *  - It loads stop_words.txt file, and it
 */

public class extractWords implements IExtractWords {

    @Override
    public List<String> extractWords(String filepath){

        List<String> words_list = new ArrayList<>();
        List<String> stop_words_list = new ArrayList<String>();

        try {
            // Load stop words
            Files.lines(Path.of("../stop_words.txt"))
                    .flatMap(line -> Arrays.stream(line.split(",")))
                    .map(String::valueOf)
                    .forEach(word -> stop_words_list.add(word));

            // Load TXT and filter
            Files.lines(Path.of(filepath))
                    .flatMap(line -> Arrays.stream(line.split("[^a-zA-Z]+"))
                            .map(String::toLowerCase)
                            .filter(line_word -> !stop_words_list.contains(line_word) && line_word.length() >= 2))
                    .forEach(word -> words_list.add(word));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return words_list;
    }
}
