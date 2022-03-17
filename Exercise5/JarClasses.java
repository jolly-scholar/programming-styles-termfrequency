import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JarClasses {

    private static List<String> findAllClassesInJar(JarFile jar){
        Stream<JarEntry> stream = jar.stream();
        return stream
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> getFQN(entry.getName()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static void printAllMethodModifiers(Class c) {
        Method[] methods = c.getDeclaredMethods();

        int public_count = 0;
        int private_count = 0;
        int static_count = 0;
        int protected_count = 0;

        for (final Method method : methods) {
            // Get function modifiers
            int modifier = method.getModifiers();
            String str = Modifier.toString(modifier);

            // Count static methods first
            if (str.contains("static")) {
                static_count++;
            } 

            // Then determine the remaining modifiers
            if (str.contains("public")) {
                public_count++;
            } else if (str.contains("private")) {
                private_count++;
            } else if (str.contains("protected")) {
                protected_count++;
            }
        }
        System.out.println("Public methods: " + public_count);
        System.out.println("Private methods: " + private_count);
        System.out.println("Protected methods: " + protected_count);
        System.out.println("Static methods: " + static_count);

        return;
    }


    private static void printAllFields(Class c) {
        Field[] fields = c.getDeclaredFields();

        int field_count = 0;

        for (final Field field : fields){
            field_count++;
        }
        System.out.println("Fields: " + field_count);

        return;
    }

    private static List<String> findAllMethods(JarEntry entry){
        List<String> stream = new ArrayList<>();

      if(entry.getMethod() == JarEntry.STORED){
          System.out.println(entry.getMethod());
          System.out.println(entry.getMethod());
          stream.add(String.valueOf(entry.getMethod()));
      }
        return stream;
    }

    private static String getFQN(String resourceName) {
        return resourceName
                .replaceAll("/",".")
                .substring(0, resourceName.lastIndexOf("."));
    }


    public static void main(String[] args) throws ClassNotFoundException {

        if(args.length > 1){
            System.err.print("Invalid Input");
            return;
        }
        String jar_filename = args[0];

        JarFile jar = null;
        File file = null;
        ClassLoader cl = null;

        try{
            file = new File(jar_filename);
            jar = new JarFile(file);
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            cl = new URLClassLoader(urls);

            List<String> classNames = findAllClassesInJar(jar);

            for(String name: classNames) {
                System.out.println("------" + name + "------");
                Class c = cl.loadClass(name);
                printAllMethodModifiers(c);
                printAllFields(c);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
