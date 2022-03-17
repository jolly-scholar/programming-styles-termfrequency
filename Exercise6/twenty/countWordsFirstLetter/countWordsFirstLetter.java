
import java.util.HashMap;
import java.util.List;


/**
 *  countWordsFirstLetter plugin:
 *  - Counts the words in a given list of strings.
 */
public class countWordsFirstLetter implements ICountTermFrequency {

    @Override
    public HashMap<String, Integer> countWords(List<String> words) {

        HashMap<String, Integer> frequency_map = new HashMap<>();


        for(String word : words){
            String firstLetter = "";
            firstLetter += word.toLowerCase().charAt(0);

            if (frequency_map.containsKey(firstLetter)){
                frequency_map.put(firstLetter, frequency_map.get(firstLetter) + 1);
            } else {
                frequency_map.put(firstLetter, 1);
            }
        }

        return frequency_map;
    }
}

