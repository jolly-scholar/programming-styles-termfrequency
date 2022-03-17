import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  countWordsFirstLetter plugin:
 *  - Counts the words in a given list of strings.
 */
public class countWords implements ICountTermFrequency {

    @Override
    public HashMap<String, Integer> countWords(List<String> words) {

        Map<String,Integer> map = words.stream()
                .collect(Collectors.toMap(word -> word, frequency -> 1, Integer::sum));

        HashMap<String, Integer> frequency_map = new HashMap<>(map);
        return frequency_map;
    }
}
