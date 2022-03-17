import java.util.HashMap;
import java.util.List;

public interface ICountTermFrequency {
    HashMap<String, Integer> countWords(List<String> words);
}
