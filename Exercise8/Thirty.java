import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/***
 * Constraints:
 * Existence of one or more units that execute concurrently
 * Existence of one or more data spaces where concurrent units store and retrieve data
 * No direct data exchanges between the concurrent units, other than via the data spaces
 *
 * Possible names:
 * Dataspaces
 * Linda
 *
 * https://github.com/crista/exercises-in-programming-style/tree/master/30-dataspaces
 */
public class Thirty {

    static BlockingQueue<String> word_space = new LinkedBlockingQueue<>();
    static BlockingQueue<HashMap> freq_space = new LinkedBlockingQueue<>();
    static List<String> stop_words = new ArrayList<>();
    static HashMap<String, Integer> word_freq_map = new HashMap<>();

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


    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }
        String filename = args[0];
        LoadStopwords();

        // this thread populates the word space (input producer)
        try {
            Files.lines(Path.of(args[0]))
                    .forEach(line -> Arrays.stream(line.split("[^a-zA-Z]+"))
                            .filter(word -> word.length() > 1)
                            .map(String::toLowerCase)
                            .forEach(word_space::add));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Create threads from process_words class
        int num_workers = 5;
        List<Thread> workers = new ArrayList<>();
        for(int i = 0 ; i < num_workers ; i ++){
            workers.add(new Thread(new Thirty().new process_words2()));
        }


        // Start the threads and join them
        for(Thread th : workers){
            th.start();
        }

        workers.forEach(worker -> {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Merge results
        while(!freq_space.isEmpty()){
            HashMap<String, Integer> map = freq_space.poll();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                if (word_freq_map.containsKey(entry.getKey())) {
                    word_freq_map.put(key, word_freq_map.get(entry.getKey()) + value);
                } else {
                    word_freq_map.put(key,value);
                }
            }
        }

        //Output
        System.out.print("---------- Word counts (top 25) -----------\n");
        word_freq_map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((a, b) -> b - a))
                .limit(25)
                .collect(Collectors.toList())
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));

    }

    public class process_words2 implements Runnable{
        final HashMap<String, Integer> partial_map = new HashMap<>();

        @Override
        public void run() {
            try {
                while(!word_space.isEmpty()){
                    String word = word_space.poll(1, TimeUnit.SECONDS);

                    if (!stop_words.contains(word)) {
                        if (partial_map.containsKey(word)) {
                            partial_map.put(word, partial_map.get(word) + 1);
                        } else {
                            partial_map.put(word, 1);
                        }
                    }
                }

                // put all the words into frequency space
                freq_space.put(partial_map);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
