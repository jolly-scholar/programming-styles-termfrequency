import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

/**
 * Style 20  - plugins
 *
 * Provide 2 plugins for extracting words: one should implement the "normal" extraction we have been using so far;
 * the second one should extract only non-stop words with z.
 *
 * Provide 2 plugins for counting words: one should implement the "normal" counting we have been using so far;
 * the second one should count words based on their first letters, so words starting with 'a', words starting with 'b', etc.
 *
 * Constraints:
 *
 *  The problem is decomposed using some form of abstraction (procedures, functions, objects, etc.)
 *
 *  All or some of those abstractions are physically encapsulated into their own, usually pre-compiled, packages.
 *  Main program and each of the packages are compiled independently. These packages are loaded dynamically by the main program, usually in the beginning (but not necessarily).
 *
 *  Main program uses functions/objects from the dynamically-loaded packages, without knowing which exact implementations will be used. New implementations can be used without having to adapt or recompile the main program.
 *
 *  External specification of which packages to load. This can be done by a configuration file, path conventions, user input or other mechanisms for external specification of code to be linked at run time.
 *
 */
 
class Twenty {

    // instantiating properties class for the config file.
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        // Load class configuration files
        Properties prop = getProperties();

        String[] word = prop.getProperty("word").split(",");
        String[] freq = prop.getProperty("freq").split(",");
        String path = prop.getProperty("jarPath");

        IExtractWords wordExtract;
        ICountTermFrequency wordFreq;

        for(int i = 0 ; i < word.length ; i++) {

            // Loading IExtractWords instance
            System.out.println("Loading:\t" + path + "/" + word[i] + ".jar");
            URL classUrl = new File(path + "/" + word[i] + ".jar").toURI().toURL();
            Class cls = (new URLClassLoader(new URL[]{classUrl})).loadClass(word[i]);
            wordExtract = (IExtractWords) cls.newInstance();

            for(int j = 0; j < freq.length ; j++){
              System.out.println("Loading:\t" + path + "/" + freq[i] + ".jar");
                URL classUrl2 = new File(path + "/" + freq[i] + ".jar").toURI().toURL();
                Class cls2 = (new URLClassLoader(new URL[]{classUrl2})).loadClass(freq[i]);
                wordFreq = (ICountTermFrequency) cls2.newInstance();

                HashMap<String, Integer> wordmap =  wordFreq.countWords(wordExtract.extractWords("../pride-and-prejudice.txt"));

                // Sort and print just the top 25 entries from the frequency map
                System.out.print("---------- Word counts (top 25) -----------\n");
                wordmap.entrySet().stream()
                        .sorted(((o1, o2) -> o2.getValue().compareTo(o1.getValue())))
                        .limit(25)
                        .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
            }

        }
    }

        private static Properties getProperties() throws IOException {
        // Load class configuration files
        Properties prop = new Properties();

        System.out.println("There are different combination of plugins that can be used, each combination is stored in a config file under _deploy folder");
        System.out.println("Select the configuration to use, input 1 or 2 or 3 or 4:");
        System.out.println("\t1) config.properties:\textractWords / countWords");
        System.out.println("\t2) config2.properties:\textractWords / countWordsFirstLetter");
        System.out.println("\t3) config3.properties:\textractWordsWithZ / countWords");
        System.out.println("\t4) config4.properties:\textractWordsWithZ / countWordsFirstLetter\n");
        Scanner in = new Scanner(System.in);
        int selection = Integer.valueOf(in.nextLine());
        in.close();

        switch(selection){
            case 1:
                prop.load(new FileInputStream("config.properties"));
                break;

            case 2:
                prop.load(new FileInputStream("config2.properties"));
                break;

            case 3:
                prop.load(new FileInputStream("config3.properties"));
                break;

            case 4:
                prop.load(new FileInputStream("config4.properties"));
                break;
        }
        return prop;
    }
}
