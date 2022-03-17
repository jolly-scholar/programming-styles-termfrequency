import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class extractWordsWithZ implements IExtractWords {


    public List<String> extractWords(String filepath){

        List<String> words_list = new ArrayList<>();
        List<String> stop_words_list = new ArrayList<String>();

        try {
            // Load stop words
            Files.lines(Path.of("../stop_words.txt"))
                    .flatMap(line -> Arrays.stream(line.split(",")))
                    .map(String::valueOf)
                    .forEach(word -> stop_words_list.add(word));

            // Load TXT and filter. Plus only the words with Z
            Files.lines(Path.of(filepath))
                    .flatMap(line -> Arrays.stream(line.split("[^a-zA-Z]+"))
                    .map(String::toLowerCase)
                    .filter(line_word -> !stop_words_list.contains(line_word)  && line_word.length() >= 2))
                    .filter(word -> word.contains("z"))
                    .forEach(word -> words_list.add(word));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return words_list;
    }
}
