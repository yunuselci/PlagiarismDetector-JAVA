package com.company;

import java.io.*;
import java.util.*;

public class Main {
    //static String databasePath = "C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\database";
    //static String textToCheckPath= "C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\control\\text1.txt";
    static ArrayList<String> databaseSentences = new ArrayList<>();
    static ArrayList<String> controlSentences = new ArrayList<>();
    static HashMap<Double, String> hash_map = new HashMap<>();
    final static File databaseFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\database");
    final static File controlFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\control");
    static ArrayList<String> controlFolderAbsolutePaths = new ArrayList<>();


    /*
    static void readFile() throws IOException {
        String target_dir = databasePath;
        File dir = new File(target_dir);
        File[] files = dir.listFiles();

        assert files != null;
        for (File f : files) {
            if (f.isFile()) {

                try (BufferedReader inputStream = new BufferedReader(
                        new FileReader(f))) {
                    String line;


                    while ((line = inputStream.readLine()) != null) {
                        databaseSentences.add(line);
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/

    static void readTheDatabaseDirectory(final File folder) throws FileNotFoundException {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheDatabaseDirectory(fileEntry);
            } else {
                Scanner sentence = new Scanner(new File(fileEntry.getAbsolutePath()));
                sentence.useDelimiter("(?<=[.!?])\\s*");
                while (sentence.hasNextLine())
                {
                    databaseSentences.add(sentence.next());
                }
                sentence.close();
            }
        }
    }

    static void readTheControlDirectory(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheControlDirectory(fileEntry);
            } else {
                /*
                try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\control"))) {
                    paths
                            .filter(Files::isRegularFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //System.out.println(fileEntry.getAbsolutePath());
                controlFolderAbsolutePaths.add(fileEntry.getAbsolutePath());
            }
        }
    }

/*
    static void readTheDatabase() throws FileNotFoundException {


        Scanner sentence = new Scanner(new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\database\\database.txt"));

        sentence.useDelimiter("(?<=[.!?])\\s*");
        while (sentence.hasNextLine())
        {
            databaseSentences.add(sentence.next());
        }
        sentence.close();
    }

*/
    static void readSentences1(final String directory) throws FileNotFoundException {
        Scanner sentence = new Scanner(new File(directory));
        sentence.useDelimiter("(?<=[.!?])\\s*");
        while (sentence.hasNextLine())
        {
            controlSentences.add(sentence.next());
        }
        sentence.close();
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you haves Apache Common Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }
    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static void putIntoHashMap(String sentence1, String sentence2) {
        System.out.printf("%.3f is the similarity between \"%sentence1\" and \"%sentence1\"%n", similarity(sentence1, sentence2), sentence1, sentence2);
        hash_map.put(similarity(sentence1,sentence2),sentence2);

    }

    public static void getMostFiveSentences(int counter){
        /*// Print keys and values
        double maxScore = 0;

        for (int i = 0; i < 5; i++) {
            for (Double j : hash_map.keySet()) {
                if(j>=maxScore){
                    maxScore = j;
                }
            }
            System.out.println("Most Similar Sentence " +(i+1)+  " in folder:" + counter+" "+ "= " + maxScore + " " + hash_map.get(maxScore));
            hash_map.remove(maxScore);
            maxScore=0;
        }*/
        
    }



    public static void main(String[] args) throws IOException {
        int counter = 0;
        readTheDatabaseDirectory(databaseFolder);
        readTheControlDirectory(controlFolder);/*
        for (String controlFolderAbsolutePath : controlFolderAbsolutePaths) {
            readSentences1(controlFolderAbsolutePath);
            for (int i = 0; i < controlSentences.size(); i++) {
                putIntoHashMap(databaseSentences.get(i), controlSentences.get(i));
            }

            System.out.println(" ");
            ++counter;
            getMostFiveSentences(counter);
            controlSentences.clear();
            hash_map.clear();
        }*/
        readSentences1(controlFolderAbsolutePaths.get(0));
        for (int j = 0; j < databaseSentences.size(); j++) {

            for (int i = 0; i < controlSentences.size(); i++) {
                putIntoHashMap(databaseSentences.get(j), controlSentences.get(i));

            }

        }


    }
}
