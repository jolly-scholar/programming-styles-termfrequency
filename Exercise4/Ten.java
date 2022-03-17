import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ten{

    private static class theOne {
        private Object object;

        public theOne(String[] object) {
            this.object = object;
        }

        public theOne bind(Function<Object, Object> function) {
            object = function.apply(object);
            return this;
        }

        public void print() {
            System.out.println(object);
        }
    }

    private static final Function<Object, Object> getFilePath = (object) -> {
        final String[] args = (String[]) object;
        
        if(args.length != 1){
            System.out.println("Invalid Input");
            System.exit(1);
        }

        final Path path = Path.of(args[0]);
        if (!path.toFile().exists()) {
            System.err.println(path + " does not exist.");
            System.exit(1);
        }

        return path;
    };

    private static final Function<Object, Object> getLines = (object) -> {
        final Path path = (Path) object;
        try {
            return Files.lines(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    };

    private static final Function<Object, Object> toFreqMap = (object) -> {
        final Stream<String> lines = (Stream<String>) object;

        // Load Stop words
        List<String> stop_words_list = new ArrayList<String>();
        try {
            Files.lines(Path.of("stop_words.txt"))
                    .flatMap(line -> Arrays.stream(line.split(",")))
                    .map(String::valueOf)
                    .forEach(word -> stop_words_list.add(word));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> frequency_map = new HashMap<>();

        lines.forEach(line ->{
            String[] words = line.split("[^a-zA-Z]+");
            for (String word : words) {
                String w = word.toLowerCase();
                if (!stop_words_list.contains(w) && w.length() > 1) {
                    if (frequency_map.containsKey(w)) {
                        frequency_map.put(w, frequency_map.get(w) + 1);
                    } else {
                        frequency_map.put(w, 1);
                    }
                }
            }
        });

        return frequency_map;
    };

    private static final Function<Object, Object> sortTheMap = (obj) -> {

        HashMap<String, Integer> word_map = (HashMap<String, Integer>) obj;

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
    };

    private static final Function<Object, Object> toStringBuilder = (obj) -> {
        HashMap<String,Integer> sorted_map = (HashMap<String, Integer>) obj;
        StringBuilder sb = new StringBuilder("---------- Word counts (top 25) -----------\n");
        sorted_map.entrySet().stream()
                .forEach(entry -> sb.append(entry.getKey() + " - " + entry.getValue() + "\n"));
        return sb;
    };

    public static void main(String[] args) {
        new theOne(args)
                .bind(getFilePath)
                .bind(getLines)
                .bind(toFreqMap)
                .bind(sortTheMap)
                .bind(toStringBuilder)
                .print();
    }
}
