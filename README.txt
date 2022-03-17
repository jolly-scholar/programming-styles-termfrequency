# Term Frequency Java
In this project, Java 11 programming language was used in order to count word frequencies from a large TXT file. The pride-and-prejudice TXT is used as a sample, and stop_words TXT includes the stop words which won't get counted. Multiple programming styles were used to tackle this same problem. This project demonstrates various programming styles that are commonly used and also highlights some of the unique features in the Java programming language.

Classes have been already compiled into the root directory
To recompile, simply run the console to call the Main.
While at the root directory, input the following commands:
____
## Exercise 1
#### Free Style: Just an example of a term frequency counter; without a specific style.
* java TermFrequency pride-and-prejudice.txt


## Exercise 2
#### Style 4: Monolithic approach, no uses of libraries nor any abstractions.
* java Four pride-and-prejudice.txt

#### Style 5: Problem solved by a sequence of functions representing each procedure.
* java Five pride-and-prejudice.txt

#### Style 6: Pipelining down the data by relaying the inputs and outputs onto each procedure.
* java Six pride-and-prejudice.txt


## Exercise 3
#### Style 7: Shortest as possible
* java Seven pride-and-prejudice.txt

#### Style 8: Recursive parsing
* java Eight pride-and-prejudice.txt


## Exercise 4
#### Style 9: Variation of factory style, using functional programming.
* java Nine pride-and-prejudice.txt
#### Style 10: The One class. Abstraction used to wrap around the values.
* java Ten pride-and-prejudice.txt
#### Style 16: Bulletin board. Sharing an infrastructure, abstractions do not communicate directly.
* java Sixteen pride-and-prejudice.txt

## Exercise 5
#### Reading Contents in JAR.
* java JarClasses json-java.jar

## Excercise 6
### Style 26: SQL Database

To run style 26, we will first need to compile:
```console
cd Exercise6/twentysix
javac *.java
```

Then, when running the program using the java command, make sure to copy the JDBC jar driver into the command as well

```console
java -cp .:./sqlite-jdbc-3.36.0.3.jar TwentySix ../../pride-and-prejudice.txt 
```


### Style 20: Using plugins

Instructions on running
Please follow the below steps for compilation and running the sample.
All the plugins JAR files have been compiled and placed in the _deploy folder.
You should be able to simply input the following and run the program.
```console
cd Exercise6/twenty/_deploy
java -jar framework.jar
```

In case a new compilation is necessary, you can follow the below steps.

#### Fresh compilation (if needed)
Create *.class files for the framework
```console
cd Exercise6/twenty/framework
javac *.java
```

Create a framework JAR file
```console
jar cfm framework.jar manifest.mf *.class
```

Create a jar: extractWords
```console
cd ../extractWords
javac -cp ../framework/framework.jar *.java
jar cf extractWords.jar *.class
```


Create a jar: extractWordsWithZ
```console
cd ../extractWordsWithZ
javac -cp ../framework/framework.jar *.java
jar cf extractWordsWithZ.jar *.class
```

Create a jar: countWords
```console
cd ../countWords
javac -cp ../framework/framework.jar *.java
jar cf countWords.jar *.class
```

Create a jar: countWordsFirstLetter
```console
cd ../countWordsFirstLetter
javac -cp ../framework/framework.jar *.java
jar cf countWordsFirstLetter.jar *.class
```

Copy all JAR files into _deploy folder.
All JAR files should get copied into the _deploy folder. Note that there is a period at the end of each command, for coping into the _deploy folder.

```console
cd ../_deploy
cp ../framework/*.jar ../extractWords/*.jar ../countWords/*.jar .
cp ../extractWordsWithZ/*.jar ../countWordsFirstLetter/*.jar .
```

Run the plugin packages starting from the framework
```console
java -jar framework.jar
```
When launching the program, it will ask you to input which configuration file to load. 
There are total four configuration files that loads different combination of plugins:

* 1) config.properties: extractWords / countWords
* 2) config2.properties: extractWords / countWordsFirstLetter
* 3) config3.properties: extractWordsWithZ / countWords
* 4) config4.properties: extractWordsWithZ / countWordsFirstLetter

## Exercise 7
#### Style: Iterators
* java Iterators pride-and-prejudice.txt
#### Style: Java Streams
* java Streams pride-and-prejudice.txt

## Exercise 8
Classes have been already compiled into the root directory
To recompile, simply run the console to call the Main.
While at the root directory, input the following commands:
#### Style 29: Actors
* java TwentyNine pride-and-prejudice.txt
#### Style 30: Database
* java Thirty pride-and-prejudice.txt
#### Style 32: Map reduce (double)
* java ThirtyTwo pride-and-prejudice.txt

## Exercise 9
#### Style3 : Arrays
Python file is in the Exercise9 folder.
Run the following commands to move to Exercise9 and run:
* cd Exercise9
* python Three.py ../pride-and-prejudice.txt