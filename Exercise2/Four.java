/* 
SWE262P: Exercise 2 Monolith - Joseph Lee
TermFrequency - tokenizes words in the inputted txt file and counts the term frequencies of 25 most counted words.
Use the command line to run. (e.g. java TermFrequency.java pride-and-prejudice.txt)
*/

import java.io.*;
import java.util.*;

class Four {
    public static void main(String[] args) throws FileNotFoundException {

        // Check input
        if(args.length != 1){
            System.out.println("Invalid Input");
            return;
        }
        String filepath = args[0];

        // List for storing stop words, words in the input, and corresponding frequencies
        List<String> stop_words_list = new ArrayList<String>();
        List<String> words_list = new ArrayList<String>();
        List<Integer> frequencies_list = new ArrayList<Integer>();

        // Scanner instances for reading the character inputs
        Scanner input_reader = new Scanner( new FileReader(filepath));
        Scanner stop_reader = new Scanner(new FileReader("/home/runner/SWE262Java/stop_words.txt"));

        // Insert the words from stop_words.txt into the list array
        while(stop_reader.hasNextLine()){
            String line = stop_reader.nextLine();
            String[] words = line.split(",");
            stop_words_list.addAll(Arrays.asList(words));
        }

        // Variables for storing words in each line.
        List<String> line_words = new ArrayList<String>();
        String word_buffer = "";
        char c;

        // Read each line and each character for words
        while(input_reader.hasNextLine()){
            String line = input_reader.nextLine() + "\n";
            for(int i = 0; i < line.length(); i++){
                c = line.charAt(i);

                // If the ith char in the input stream is alphabetical or numerical (ASCII)
                if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') ){
                    // Add the character to the word buffer
                    word_buffer = word_buffer + c;
                }

                // If the ith char in the input stream is not an alphabet nor a number,
                else{
                    // Check the length to confirm word and see if its a stop word.
                    if(!stop_words_list.contains(word_buffer.toLowerCase()) && word_buffer.length() >= 2){
                        line_words.add(word_buffer.toLowerCase());
                    }
                    // Reset word buffer
                    word_buffer = "";
                }
            }


            // Loop through the line words and add them to the lists.
            for(int i = 0; i < line_words.size(); i++){
                // Status for whether if the word already exists.
                boolean exists = false;

                // If the words_list is empty, just add.
                if(words_list.size() == 0){
                    words_list.add(line_words.get(i));
                    frequencies_list.add(1);
                }

                else{

                    // Loop through words list to see if the target word exists.
                    for(int j = 0; j < words_list.size(); j++){
                        if(words_list.get(j).equals(line_words.get(i))){
                            exists = true;
                            // append the corresponding term frequency count
                            frequencies_list.set(j, frequencies_list.get(j)+1);
                            break;
                        }
                    }

                    // New word, add word to the list, count 1.
                    if(exists == false){
                        words_list.add(line_words.get(i));
                        frequencies_list.add(1);
                    }
                }
            }

            // Clear the line_words buffer
            line_words.clear();
        } // Repeat for next lines.

        // Sorting both the words and frequency list using selection sort.
        for(int i = 0; i < frequencies_list.size() - 1 ; i++){
            int minIndex = i;

            for (int j = i + 1; j < frequencies_list.size(); j++){
                if(frequencies_list.get(j) > frequencies_list.get(minIndex)){
                    minIndex = j;
                }
            }

            // Get the minIndex words and store in temp
            int temp_freq = frequencies_list.get(minIndex);
            String temp_word = words_list.get(minIndex);

            // Swap
            frequencies_list.set(minIndex,frequencies_list.get(i));
            words_list.set(minIndex,words_list.get(i));
            frequencies_list.set(i, temp_freq);
            words_list.set(i,temp_word);
        }

        // Print top 25 words.
        System.out.print("---------- Word counts (top 25) -----------\n");
        for (int i = 0; i < 25; ++i) {
            System.out.printf("%s  -  %d\n", words_list.get(i), frequencies_list.get(i));
        }
    }
}
