
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

//  Style 16: Bulletin-board

public class Sixteen {

    public static class EventManager {

        // list of event subscriptions
        HashMap<String,ArrayList<Consumer<String[]>>> subscriptions = new HashMap<>();

        public EventManager() {
            subscriptions = new HashMap<String, ArrayList<Consumer<String[]>>>();
        }

        // subscribing a fxn
        public void subscribe(String even_type, Consumer<String[]> handler){
            subscriptions.computeIfAbsent(even_type, k -> new ArrayList<Consumer<String[]>>()).add(handler);
        }

        //
        public void publish(String[] event) throws IOException {
            String event_type = event[0];
            if(subscriptions.containsKey(event_type)){
                for(Consumer<String[]> c : subscriptions.get(event_type))
                    c.accept(event);
            }
        }
    }

    // Application entities
    public static class DataStorage {

        private EventManager event_manager;
        private List<String> words_list = new ArrayList<String>();

        public DataStorage(){
            event_manager = null;
        }

        // Data Storage constructure __init__
        public DataStorage(EventManager em) {

            this.event_manager = em;
            em.subscribe("load",(String[] event)->load(event));
            em.subscribe("start",(String [] event)-> {
                try {
                    produce_words(event);
                } catch (IOException e) {}

            });
        }

        // load files and
        public void load(String[] event){
            String file = event[1];
            try{
                String word_lines = new String(Files.readAllBytes(Paths.get(file)));
                words_list = Arrays.asList(word_lines.toLowerCase().split("[^a-zA-Z0-9]+"));

            }catch (Exception e){
                System.out.println("Problem with loading the file. Make sure the file is in the root directory");
            }
        }

        // store the words into the EventManager
        public void produce_words(String[] event) throws IOException {
            for(String word : words_list) {
                this.event_manager.publish(new String[]{"word", word});
            }
            this.event_manager.publish(new String[] {"eof"});
        }
    }

    private static class StopWordFilter {

         private static EventManager event_manager;
         public static List<String> stop_words;

        // __init__
        StopWordFilter(EventManager em) {

            this.event_manager = em;
            this.stop_words = new ArrayList<String>();
            em.subscribe("load",(String[] event)-> {
                load(event);
            });
            em.subscribe("word",(String [] event)-> {
                try {
                    is_stop_word(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }


        private static void load(String[] event) {
            String lines = null;
            try {
                lines = new String(Files.readAllBytes(Paths.get("stop_words.txt")));
            } catch (IOException e) {
                System.out.println("Problem with loading stop_words");
            }
            String[] tmp = lines.toLowerCase().split(",");
            stop_words = Arrays.asList(tmp);


        }

        public static void is_stop_word(String[] event) throws IOException {

            String word = event[1];
            if ( !stop_words.contains(word) && word.length() >= 2) {
                event_manager.publish(new String[] {"valid_word", word});
            }
        }
    }

    public static class WordFrequencyCounter {
        EventManager event_manager;
        Map<String, Integer> word_freq_map;

        public WordFrequencyCounter(EventManager em) {
            this.event_manager = em;
            word_freq_map = new HashMap<String, Integer>();
            em.subscribe("valid_word",(String[] event)->increment_count(event));
            em.subscribe("print",(String[] event) -> print_freqs(event));
        }


        public void increment_count(String[] event){

            String word = event[1];
//            int count = word_freq_map.containsKey(word) ? word_freq_map.get(word) : 0;

            if(word_freq_map.containsKey(word)){
                word_freq_map.put(word, word_freq_map.get(word) + 1);
            } else {
                word_freq_map.put(word, 1);
            }
        }

        public void print_freqs(String[] event){
            System.out.print("---------- Word counts (top 25) -----------\n");
            word_freq_map.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(25)
                    .forEach( entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
        }
    }


    public static class WordFrequencyApplication  {
        EventManager event_manager;

        public WordFrequencyApplication(EventManager em) {
            this.event_manager = em;

            em.subscribe("run",(String[] event)-> {
                try {
                    run(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            em.subscribe("eof",(String[] event)-> {
                try {
                    stop(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        
        public void run(String[] event) throws IOException {
            String file = event[1];
            event_manager.publish(new String[]{"load",file});
            event_manager.publish(new String[]{"start"});

        }

        public void stop(String[] event) throws IOException {
            event_manager.publish(new String[]{"print"});
        }
    }


    public static void main(String[] args) throws IOException {

        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }
        EventManager em = new EventManager();
        DataStorage dataStorage = new DataStorage(em);
        StopWordFilter stopWordFilter = new StopWordFilter(em);
        WordFrequencyCounter wordFrenquencyCounter = new WordFrequencyCounter(em);
        WordFrequencyApplication wordFrenquencyApplication = new WordFrequencyApplication(em);
        em.publish(new String[] {"run",args[0]});
    }
}
