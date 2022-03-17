import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/***
 * Twenty Nine - Actors:
 *
 *  Similar to the letterbox style, but where the 'things' have independent threads of execution.
 *
 *  Constraints:
 *      The larger problem is decomposed into 'things' that make sense for the problem domain
 *      Each 'thing' has a queue meant for other \textit{things} to place messages in it
 *      Each 'thing' is a capsule of data that exposes only its ability to receive messages via the queue
 *      Each 'thing' has its own thread of execution independent of the others.
 *
 *  Possible names:
 *      Free agents
 *      Active letterbox
 *      Actors
 *
 * https://github.com/crista/exercises-in-programming-style/tree/master/29-actors
 */

public class TwentyNine {

    /**
     * ActiveWFObject is the main class that manages threads operation
     */
    private static class ActiveWFObject extends Thread{

        /**
         * Messages are delivered in forms of Object[] arrays that
         * contains command strings and instances of ActiveWFObjects
         */
        private BlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
        public boolean running;

        ActiveWFObject(){
            this.start();
            this.running = true ;
        }

        @Override
        public void run(){
            while(running){
                Object[] message = queue.poll();
                if (message != null) {
                    this.dispatch(message);
                    if (message[0].equals("exit")) {
                        running = false;
                    }
                }
            }
        }

        // dispatch method will be overrided by each class to act to the messages in the queue
        public void dispatch(Object[] message){};
        public void end() { this.running = false;   }
        public void addMessage(Object[] message){   queue.add(message); }
    }

    // Send method adds the Object[] message to the queues of the corresponding objects.
    private static void send(ActiveWFObject receiver, Object[] message) {
        receiver.addMessage(message);
    }

    /***
     * DataStorageManager - Models the contents of the TXT file into Stream
     */
    private static class DataStorageManager extends ActiveWFObject{
        private Stream<String> word_lines;
        private StopWordManager stop_words_manager;

        public void init(Object[] message) {
            try {
                stop_words_manager = (StopWordManager) message[1];
                word_lines = Files.lines(Path.of((String) message[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dispatch(Object[] message){
            switch((String) message[0]){
                case "init":
                    this.init(new Object[]{message[1], message[2]});
                    break;
                case "process":
                    this.process_words(new Object[]{message[1]});
                    break;
                case "exit":
                default:
                    send(stop_words_manager,message);
                    break;
            }
        }

        private void process_words(Object[] message) {

            // Process words
            WordFrequencyController controller = (WordFrequencyController) message[0];

            // start counting
            word_lines.forEach(line -> {
                String[] words = line.split("[^a-zA-Z]+");
                for (String word : words) {
                    String w = word.toLowerCase();
                    // forward stop_words_manager each word so that they get filtered
                    send(stop_words_manager, new Object[]{"filter", w});
                }
            });
            // send the message over to stop_words_manager
            // which will forward it to the word_freq_manager
            send(stop_words_manager, new Object[]{"top25", controller});
        }
    }

    /**
     * StopWordManager - Models the stop words filter
     */
    private static class StopWordManager extends ActiveWFObject{
        List<String> stop_words = new ArrayList<>();
        WordFrequencyManager word_freq_manager;

        public void init(Object[] message){
            word_freq_manager = (WordFrequencyManager) message[0];
            // load stop words
            try {
                final byte[] bytes = Files.readAllBytes(Path.of("stop_words.txt"));
                final String[] words = new String(bytes).split(",");
                stop_words.addAll(Arrays.asList((words)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dispatch(Object[] message){
            switch((String) message[0]){
                case "init":
                    this.init(new Object[]{message[1]});
                    break;
                case "filter":
                    this.filter(new Object[]{(message[1])});
                    break;
                case "exit":
                default:
                    // Forward all other messages to word_freq_manager
                    send(word_freq_manager, message);
            }
        }


        private void filter(Object[] message) {
            String word = (String) message[0];
            if(!stop_words.contains(word) && word.length() >= 2){
                // If the word passes, send it to word_freq_manager to map the values.
                send(word_freq_manager, new Object[]{"increment", word});
            }
        }
    }

    /**
     * WordFrequencyManager - Keeps the word frequency data
     */
    private static class WordFrequencyManager extends ActiveWFObject{
        private HashMap<String, Integer> freq_map = new HashMap<String, Integer>();
        WordFrequencyController word_freq_controller;

        @Override
        public void dispatch(Object[] message){
            switch ((String) message[0]) {
                case "increment":
                    this.increment_count(new Object[]{message[1]});
                    break;
                case "top25":
                    this.top25(new Object[]{message[1]});
                    break;
                case "exit":
                    break;
                default:
                    System.exit(1);
                    break;
            }
        }

        private void increment_count(Object[] message){
            String word = (String) message[0];

            if (freq_map.containsKey(word)) {
                freq_map.put(word, freq_map.get(word) + 1);
            } else {
                freq_map.put(word, 1);
            }
        }

        private void top25(Object[] message){
            word_freq_controller = (WordFrequencyController) message[0];

            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(freq_map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            // tell word_freq_controller
            send(word_freq_controller, new Object[]{"display", list.subList(0,25)});
        }
    }

    /**
     * WordFrequencyController -
     *  Main controller that controls the DataStorageManager to start/stop processing
     */
    private static class WordFrequencyController extends ActiveWFObject{

        DataStorageManager data_storage_manager;

        @Override
        public void dispatch(Object[] message){
            switch((String) message[0]) {
                case "run":
                    this.start(new Object[]{message[1]});
                    break;
                case "display":
                    this.display(new Object[]{message[1]});
                    break;
                default:
                    System.out.println("WordFrequencyController: Invalid message");
                    break;
            }
        }

        // Tell data_storage_manager to start processing
        public void start(Object[] message){
            data_storage_manager = (DataStorageManager) message[0];
            send(data_storage_manager, new Object[]{"process", this});
        }

        public void display(Object[] message){
            final List<Map.Entry<String, Integer>> words_list
                    = (List<Map.Entry<String, Integer>>) message[0];

            // print first 25 words
            final StringBuilder result = new StringBuilder();
            System.out.print("---------- Word counts (top 25) -----------\n");
            for (int i = 0; i < 25; ++i) {
                final Map.Entry<String, Integer> entry = words_list.get(i);
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }

            end();
            // When complete, command data_storage_manager to exit
            send(data_storage_manager, new Object[]{"exit"});
        }

    }

    public static void main(String args[]){

        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }
        String filename = args[0];

        // initialize word frequency manager
        WordFrequencyManager word_freq_manager  = new WordFrequencyManager();

        // initialize stop words manager
        StopWordManager stop_word_manager  = new StopWordManager();
        Object[] message = {"init", word_freq_manager};
        send(stop_word_manager, message);

        //initialize data storage manager
        DataStorageManager storage_manager = new DataStorageManager();
        message = new Object[]{"init", filename, stop_word_manager};
        send(storage_manager, message);

        //initialize the controller
        WordFrequencyController wfcontroller = new WordFrequencyController();
        message = new Object[]{"run", storage_manager};
        send(wfcontroller,message);

        try {
            wfcontroller.join();
            storage_manager.join();
            stop_word_manager.join();
            word_freq_manager.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
